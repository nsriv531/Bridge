package com.example.snowbridge.model;

import java.util.List;

public class WorkloadResponse {
    private String jobName;
    private String recommendedStrategy;
    private String riskLevel;
    private long estimatedRuntimeMs;
    private double aggregateScore;
    private List<PartitionMetricResponse> partitions;
    private String engineVersion;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRecommendedStrategy() {
        return recommendedStrategy;
    }

    public void setRecommendedStrategy(String recommendedStrategy) {
        this.recommendedStrategy = recommendedStrategy;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public long getEstimatedRuntimeMs() {
        return estimatedRuntimeMs;
    }

    public void setEstimatedRuntimeMs(long estimatedRuntimeMs) {
        this.estimatedRuntimeMs = estimatedRuntimeMs;
    }

    public double getAggregateScore() {
        return aggregateScore;
    }

    public void setAggregateScore(double aggregateScore) {
        this.aggregateScore = aggregateScore;
    }

    public List<PartitionMetricResponse> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<PartitionMetricResponse> partitions) {
        this.partitions = partitions;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }
}
