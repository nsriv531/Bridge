package com.example.snowbridge.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class WorkloadRequest {
    @NotBlank
    private String jobName;

    @Min(1)
    private int partitions;

    @Min(1)
    private long rowsPerPartition;

    @DecimalMin("1.0")
    private double skewFactor;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double cachePressure;

    @Min(1)
    private int concurrency;

    @Pattern(regexp = "BATCH|STREAMING|INTERACTIVE", message = "workloadType must be BATCH, STREAMING, or INTERACTIVE")
    private String workloadType = "BATCH";

    @Pattern(regexp = "LOW|NORMAL|HIGH|CRITICAL", message = "priority must be LOW, NORMAL, HIGH, or CRITICAL")
    private String priority = "NORMAL";

    @Min(1)
    private Long targetSlaMs;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public long getRowsPerPartition() {
        return rowsPerPartition;
    }

    public void setRowsPerPartition(long rowsPerPartition) {
        this.rowsPerPartition = rowsPerPartition;
    }

    public double getSkewFactor() {
        return skewFactor;
    }

    public void setSkewFactor(double skewFactor) {
        this.skewFactor = skewFactor;
    }

    public double getCachePressure() {
        return cachePressure;
    }

    public void setCachePressure(double cachePressure) {
        this.cachePressure = cachePressure;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
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

    public Long getTargetSlaMs() {
        return targetSlaMs;
    }

    public void setTargetSlaMs(Long targetSlaMs) {
        this.targetSlaMs = targetSlaMs;
    }
}
