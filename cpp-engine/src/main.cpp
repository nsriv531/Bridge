#include <algorithm>
#include <cmath>
#include <cctype>
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
    double memoryPressure;
    double hotspotRatio;
};

struct RequestInput {
    std::string jobName;
    int partitions;
    long long rowsPerPartition;
    double skewFactor;
    double cachePressure;
    int concurrency;
    std::string workloadType;
    std::string priority;
    long long targetSlaMs;
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

static std::string normalizeEnum(std::string value) {
    std::transform(value.begin(), value.end(), value.begin(), [](unsigned char c) {
        return static_cast<char>(std::toupper(c));
    });
    return value;
}

static double workloadMultiplier(const std::string& workloadType) {
    if (workloadType == "STREAMING") {
        return 0.92;
    }
    if (workloadType == "INTERACTIVE") {
        return 1.18;
    }
    return 1.0;
}

static double priorityMultiplier(const std::string& priority) {
    if (priority == "LOW") {
        return 1.08;
    }
    if (priority == "HIGH") {
        return 0.95;
    }
    if (priority == "CRITICAL") {
        return 0.88;
    }
    return 1.0;
}

static RequestInput parseArgs(int argc, char* argv[]) {
    if (argc != 10) {
        throw std::runtime_error(
            "Expected 9 arguments: <jobName> <partitions> <rowsPerPartition> <skewFactor> <cachePressure> <concurrency> <workloadType> <priority> <targetSlaMs>");
    }

    RequestInput input{};
    input.jobName = argv[1];
    input.partitions = std::stoi(argv[2]);
    input.rowsPerPartition = std::stoll(argv[3]);
    input.skewFactor = std::stod(argv[4]);
    input.cachePressure = std::stod(argv[5]);
    input.concurrency = std::stoi(argv[6]);
    input.workloadType = normalizeEnum(argv[7]);
    input.priority = normalizeEnum(argv[8]);
    input.targetSlaMs = std::stoll(argv[9]);

    if (input.partitions <= 0 || input.rowsPerPartition <= 0 || input.concurrency <= 0) {
        throw std::runtime_error("Partitions, rowsPerPartition, and concurrency must be positive.");
    }
    if (input.skewFactor < 1.0) {
        throw std::runtime_error("skewFactor must be at least 1.0.");
    }
    if (input.cachePressure < 0.0 || input.cachePressure > 1.0) {
        throw std::runtime_error("cachePressure must be between 0.0 and 1.0.");
    }
    if (input.targetSlaMs < 0) {
        throw std::runtime_error("targetSlaMs cannot be negative.");
    }

    const std::vector<std::string> validWorkloadTypes{"BATCH", "STREAMING", "INTERACTIVE"};
    const std::vector<std::string> validPriorities{"LOW", "NORMAL", "HIGH", "CRITICAL"};

    if (std::find(validWorkloadTypes.begin(), validWorkloadTypes.end(), input.workloadType) == validWorkloadTypes.end()) {
        throw std::runtime_error("workloadType must be one of: BATCH, STREAMING, INTERACTIVE.");
    }
    if (std::find(validPriorities.begin(), validPriorities.end(), input.priority) == validPriorities.end()) {
        throw std::runtime_error("priority must be one of: LOW, NORMAL, HIGH, CRITICAL.");
    }

    return input;
}

static std::vector<PartitionMetric> computeMetrics(const RequestInput& input) {
    std::vector<PartitionMetric> metrics;
    metrics.reserve(input.partitions);

    const double typeFactor = workloadMultiplier(input.workloadType);
    const double priorityFactor = priorityMultiplier(input.priority);

    for (int i = 0; i < input.partitions; ++i) {
        const double partitionSpread = static_cast<double>(i) / std::max(1, input.partitions - 1);
        const double skewMultiplier = 1.0 + (partitionSpread * (input.skewFactor - 1.0));
        const long long effectiveRows = static_cast<long long>(std::llround(input.rowsPerPartition * skewMultiplier));
        const double computeCost = (effectiveRows / 10000.0) * (1.0 + input.cachePressure * 1.8) * typeFactor * priorityFactor;
        const double concurrencyPenalty = 1.0 + std::max(0, input.concurrency - 2) * 0.12;
        const double partitionCost = computeCost * concurrencyPenalty;
        const double latencyRisk = std::min(1.0, (partitionCost / 40.0) + input.cachePressure * 0.3 + (partitionSpread * 0.08));
        const double memoryPressure = std::min(1.0, 0.2 + (effectiveRows / 750000.0) + input.cachePressure * 0.4);
        const double hotspotRatio = std::min(1.0, std::max(0.0, ((skewMultiplier - 1.0) / std::max(0.01, input.skewFactor - 1.0))));

        metrics.push_back(PartitionMetric{
            i + 1,
            effectiveRows,
            std::round(partitionCost * 100.0) / 100.0,
            std::round(latencyRisk * 100.0) / 100.0,
            std::round(memoryPressure * 100.0) / 100.0,
            std::round(hotspotRatio * 100.0) / 100.0,
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
        double maxMemoryPressure = 0.0;
        double maxHotspotRatio = 0.0;

        for (const auto& metric : metrics) {
            totalCost += metric.partitionCost;
            maxRisk = std::max(maxRisk, metric.latencyRisk);
            maxMemoryPressure = std::max(maxMemoryPressure, metric.memoryPressure);
            maxHotspotRatio = std::max(maxHotspotRatio, metric.hotspotRatio);
        }

        const long long estimatedRuntimeMs = static_cast<long long>(std::llround((totalCost * 65.0) / std::max(1, input.concurrency)));
        const bool slaBreached = input.targetSlaMs > 0 && estimatedRuntimeMs > input.targetSlaMs;
        const double scorePenalty = std::min(92.0, (totalCost / input.partitions) + maxMemoryPressure * 20.0 + (slaBreached ? 12.0 : 0.0));
        const double aggregateScore = std::round((100.0 - scorePenalty) * 100.0) / 100.0;

        std::string riskLevel = "LOW";
        std::string strategy = "KEEP_CURRENT_PLAN";
        std::string bottleneck = "BALANCED";

        if (maxHotspotRatio >= 0.8 || input.skewFactor >= 1.8) {
            bottleneck = "DATA_SKEW";
        } else if (maxMemoryPressure >= 0.75 || input.cachePressure >= 0.55) {
            bottleneck = "MEMORY_PRESSURE";
        } else if (slaBreached || input.workloadType == "INTERACTIVE") {
            bottleneck = "LATENCY";
        }

        if (slaBreached || maxRisk >= 0.78 || input.priority == "CRITICAL") {
            riskLevel = "HIGH";
            strategy = "REPARTITION_SCALE_AND_PRIORITIZE";
        } else if (maxRisk >= 0.45 || input.cachePressure >= 0.35 || input.skewFactor >= 1.5) {
            riskLevel = "MEDIUM";
            strategy = "REBALANCE_AND_SCALE_OUT";
        }

        int recommendedWorkers = std::max(input.concurrency,
            static_cast<int>(std::ceil((input.partitions * (1.0 + input.cachePressure + (slaBreached ? 0.5 : 0.0))) / 2.0)));
        if (input.priority == "CRITICAL") {
            recommendedWorkers = std::max(recommendedWorkers, input.concurrency + 2);
        }

        std::ostringstream json;
        json << std::fixed << std::setprecision(2);
        json << "{";
        json << "\"jobName\":\"" << escapeJson(input.jobName) << "\",";
        json << "\"workloadType\":\"" << escapeJson(input.workloadType) << "\",";
        json << "\"priority\":\"" << escapeJson(input.priority) << "\",";
        json << "\"recommendedStrategy\":\"" << strategy << "\",";
        json << "\"riskLevel\":\"" << riskLevel << "\",";
        json << "\"estimatedRuntimeMs\":" << estimatedRuntimeMs << ",";
        json << "\"targetSlaMs\":" << input.targetSlaMs << ",";
        json << "\"slaBreached\":" << (slaBreached ? "true" : "false") << ",";
        json << "\"recommendedWorkers\":" << recommendedWorkers << ",";
        json << "\"bottleneck\":\"" << bottleneck << "\",";
        json << "\"aggregateScore\":" << aggregateScore << ",";
        json << "\"partitions\":[";

        for (std::size_t i = 0; i < metrics.size(); ++i) {
            const auto& metric = metrics[i];
            json << "{";
            json << "\"partitionId\":" << metric.partitionId << ",";
            json << "\"effectiveRows\":" << metric.effectiveRows << ",";
            json << "\"partitionCost\":" << metric.partitionCost << ",";
            json << "\"latencyRisk\":" << metric.latencyRisk << ",";
            json << "\"memoryPressure\":" << metric.memoryPressure << ",";
            json << "\"hotspotRatio\":" << metric.hotspotRatio;
            json << "}";
            if (i + 1 < metrics.size()) {
                json << ",";
            }
        }

        json << "],";
        json << "\"engineVersion\":\"1.2.0\"";
        json << "}";

        std::cout << json.str() << std::endl;
        return 0;
    } catch (const std::exception& ex) {
        std::cerr << ex.what() << std::endl;
        return 1;
    }
}
