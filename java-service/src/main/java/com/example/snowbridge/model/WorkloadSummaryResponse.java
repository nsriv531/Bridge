package com.example.snowbridge.model;

public class WorkloadSummaryResponse {
    private long totalJobs;
    private long queuedJobs;
    private long runningJobs;
    private long completedJobs;
    private long failedJobs;
    private Double averageAggregateScore;
    private Double averageEstimatedRuntimeMs;

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public long getQueuedJobs() {
        return queuedJobs;
    }

    public void setQueuedJobs(long queuedJobs) {
        this.queuedJobs = queuedJobs;
    }

    public long getRunningJobs() {
        return runningJobs;
    }

    public void setRunningJobs(long runningJobs) {
        this.runningJobs = runningJobs;
    }

    public long getCompletedJobs() {
        return completedJobs;
    }

    public void setCompletedJobs(long completedJobs) {
        this.completedJobs = completedJobs;
    }

    public long getFailedJobs() {
        return failedJobs;
    }

    public void setFailedJobs(long failedJobs) {
        this.failedJobs = failedJobs;
    }

    public Double getAverageAggregateScore() {
        return averageAggregateScore;
    }

    public void setAverageAggregateScore(Double averageAggregateScore) {
        this.averageAggregateScore = averageAggregateScore;
    }

    public Double getAverageEstimatedRuntimeMs() {
        return averageEstimatedRuntimeMs;
    }

    public void setAverageEstimatedRuntimeMs(Double averageEstimatedRuntimeMs) {
        this.averageEstimatedRuntimeMs = averageEstimatedRuntimeMs;
    }
}
