package com.example.snowbridge.model;

public class PartitionMetricResponse {
    private int partitionId;
    private long effectiveRows;
    private double partitionCost;
    private double latencyRisk;
    private double memoryPressure;
    private double hotspotRatio;

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public long getEffectiveRows() {
        return effectiveRows;
    }

    public void setEffectiveRows(long effectiveRows) {
        this.effectiveRows = effectiveRows;
    }

    public double getPartitionCost() {
        return partitionCost;
    }

    public void setPartitionCost(double partitionCost) {
        this.partitionCost = partitionCost;
    }

    public double getLatencyRisk() {
        return latencyRisk;
    }

    public void setLatencyRisk(double latencyRisk) {
        this.latencyRisk = latencyRisk;
    }

    public double getMemoryPressure() {
        return memoryPressure;
    }

    public void setMemoryPressure(double memoryPressure) {
        this.memoryPressure = memoryPressure;
    }

    public double getHotspotRatio() {
        return hotspotRatio;
    }

    public void setHotspotRatio(double hotspotRatio) {
        this.hotspotRatio = hotspotRatio;
    }
}
