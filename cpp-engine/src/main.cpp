#include <cmath>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

struct PartitionMetric {
    int partitionId;
    long long effectiveRows;
    double partitionCost;
    double latencyRisk;
};

struct RequestInput {
    std::string jobName;
    int partitions;
    long long rowsPerPartition;
    double skewFactor;
    double cachePressure;
    int concurrency;
};

static std::string escapeJson(const std::string& input) {
    std::ostringstream escaped;
    for (char c : input) {
        switch (c) {
            case '"': escaped << "\\\""; break;
            case '\\': escaped << "\\\\"; break;
            case '\n': escaped << "\\n"; break;
            case '\r': escaped << "\\r"; break;
            case '\t': escaped << "\\t"; break;
            default: escaped << c;
        }
    }
    return escaped.str();
}

static RequestInput parseArgs(int argc, char* argv[]) {
    if (argc != 7) {
        throw std::runtime_error(
            "Expected 6 arguments: <jobName> <partitions> <rowsPerPartition> <skewFactor> <cachePressure> <concurrency>");
    }

    RequestInput input{};
    input.jobName = argv[1];
    input.partitions = std::stoi(argv[2]);
    input.rowsPerPartition = std::stoll(argv[3]);
    input.skewFactor = std::stod(argv[4]);
    input.cachePressure = std::stod(argv[5]);
    input.concurrency = std::stoi(argv[6]);

    if (input.partitions <= 0 || input.rowsPerPartition <= 0 || input.concurrency <= 0) {
        throw std::runtime_error("Partitions, rowsPerPartition, and concurrency must be positive.");
    }
    if (input.skewFactor < 1.0) {
        throw std::runtime_error("skewFactor must be at least 1.0.");
    }
    if (input.cachePressure < 0.0 || input.cachePressure > 1.0) {
        throw std::runtime_error("cachePressure must be between 0.0 and 1.0.");
    }

    return input;
}

static std::vector<PartitionMetric> computeMetrics(const RequestInput& input) {
    std::vector<PartitionMetric> metrics;
    metrics.reserve(input.partitions);

    for (int i = 0; i < input.partitions; ++i) {
        const double skewMultiplier = 1.0 + ((static_cast<double>(i) / std::max(1, input.partitions - 1)) * (input.skewFactor - 1.0));
        const long long effectiveRows = static_cast<long long>(std::llround(input.rowsPerPartition * skewMultiplier));
        const double computeCost = (effectiveRows / 10000.0) * (1.0 + input.cachePressure * 1.8);
        const double concurrencyPenalty = 1.0 + std::max(0, input.concurrency - 2) * 0.12;
        const double partitionCost = computeCost * concurrencyPenalty;
        const double latencyRisk = std::min(1.0, (partitionCost / 40.0) + input.cachePressure * 0.3);

        metrics.push_back(PartitionMetric{
            i + 1,
            effectiveRows,
            std::round(partitionCost * 100.0) / 100.0,
            std::round(latencyRisk * 100.0) / 100.0,
        });
    }

    return metrics;
}

int main(int argc, char* argv[]) {
    try {
        const RequestInput input = parseArgs(argc, argv);
        const std::vector<PartitionMetric> metrics = computeMetrics(input);

        double totalCost = 0.0;
        double maxRisk = 0.0;
        for (const auto& metric : metrics) {
            totalCost += metric.partitionCost;
            maxRisk = std::max(maxRisk, metric.latencyRisk);
        }

        const double aggregateScore = std::round((100.0 - std::min(95.0, totalCost / input.partitions)) * 100.0) / 100.0;
        const long long estimatedRuntimeMs = static_cast<long long>(std::llround((totalCost * 65.0) / std::max(1, input.concurrency)));

        std::string riskLevel = "LOW";
        std::string strategy = "KEEP_CURRENT_PLAN";

        if (maxRisk >= 0.75 || input.skewFactor >= 1.8) {
            riskLevel = "HIGH";
            strategy = "REPARTITION_AND_THROTTLE";
        } else if (maxRisk >= 0.4 || input.cachePressure >= 0.35) {
            riskLevel = "MEDIUM";
            strategy = "REBALANCE_AND_SCALE_OUT";
        }

        std::ostringstream json;
        json << std::fixed << std::setprecision(2);
        json << "{";
        json << "\"jobName\":\"" << escapeJson(input.jobName) << "\",";
        json << "\"recommendedStrategy\":\"" << strategy << "\",";
        json << "\"riskLevel\":\"" << riskLevel << "\",";
        json << "\"estimatedRuntimeMs\":" << estimatedRuntimeMs << ",";
        json << "\"aggregateScore\":" << aggregateScore << ",";
        json << "\"partitions\":[";

        for (std::size_t i = 0; i < metrics.size(); ++i) {
            const auto& metric = metrics[i];
            json << "{";
            json << "\"partitionId\":" << metric.partitionId << ",";
            json << "\"effectiveRows\":" << metric.effectiveRows << ",";
            json << "\"partitionCost\":" << metric.partitionCost << ",";
            json << "\"latencyRisk\":" << metric.latencyRisk;
            json << "}";
            if (i + 1 < metrics.size()) {
                json << ",";
            }
        }

        json << "],";
        json << "\"engineVersion\":\"1.0.0\"";
        json << "}";

        std::cout << json.str() << std::endl;
        return 0;
    } catch (const std::exception& ex) {
        std::cerr << ex.what() << std::endl;
        return 1;
    }
}
