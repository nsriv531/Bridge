package com.example.snowbridge.model;

import java.util.List;

public class WorkloadResponse {
    private String jobName;
    private String workloadType;
    private String priority;
    private String recommendedStrategy;
    private String riskLevel;
    private long estimatedRuntimeMs;
    private long targetSlaMs;
    private boolean slaBreached;
    private int recommendedWorkers;
    private String bottleneck;
    private double aggregateScore;
    private List<PartitionMetricResponse> partitions;
    private String engineVersion;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getWorkloadType() {
        return workloadType;
    }

    public void setWorkloadType(String workloadType) {
        this.workloadType = workloadType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public long getTargetSlaMs() {
        return targetSlaMs;
    }

    public void setTargetSlaMs(long targetSlaMs) {
        this.targetSlaMs = targetSlaMs;
    }

    public boolean isSlaBreached() {
        return slaBreached;
    }

    public void setSlaBreached(boolean slaBreached) {
        this.slaBreached = slaBreached;
    }

    public int getRecommendedWorkers() {
        return recommendedWorkers;
    }

    public void setRecommendedWorkers(int recommendedWorkers) {
        this.recommendedWorkers = recommendedWorkers;
    }

    public String getBottleneck() {
        return bottleneck;
    }

    public void setBottleneck(String bottleneck) {
        this.bottleneck = bottleneck;
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
