package com.example.snowbridge.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

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
}
