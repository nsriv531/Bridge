package com.example.snowbridge.model;

import java.time.Instant;

public class WorkloadJobResponse {
    private String jobId;
    private WorkloadJobStatus status;
    private Instant submittedAt;
    private Instant startedAt;
    private Instant completedAt;
    private WorkloadRequest request;
    private WorkloadResponse result;
    private String errorMessage;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public WorkloadJobStatus getStatus() {
        return status;
    }

    public void setStatus(WorkloadJobStatus status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public WorkloadRequest getRequest() {
        return request;
    }

    public void setRequest(WorkloadRequest request) {
        this.request = request;
    }

    public WorkloadResponse getResult() {
        return result;
    }

    public void setResult(WorkloadResponse result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
