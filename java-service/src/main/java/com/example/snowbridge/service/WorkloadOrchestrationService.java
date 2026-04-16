package com.example.snowbridge.service;

import com.example.snowbridge.model.WorkloadJobResponse;
import com.example.snowbridge.model.WorkloadJobStatus;
import com.example.snowbridge.model.WorkloadRequest;
import com.example.snowbridge.model.WorkloadResponse;
import com.example.snowbridge.model.WorkloadSummaryResponse;
import com.example.snowbridge.repository.WorkloadJobRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.springframework.stereotype.Service;

@Service
public class WorkloadOrchestrationService {
    private final WorkloadAnalysisService workloadAnalysisService;
    private final WorkloadJobRepository workloadJobRepository;
    private final ExecutorService workloadExecutor;

    public WorkloadOrchestrationService(WorkloadAnalysisService workloadAnalysisService,
                                        WorkloadJobRepository workloadJobRepository,
                                        ExecutorService workloadExecutor) {
        this.workloadAnalysisService = workloadAnalysisService;
        this.workloadJobRepository = workloadJobRepository;
        this.workloadExecutor = workloadExecutor;
    }

    public WorkloadJobResponse submit(WorkloadRequest request) {
        WorkloadJobResponse job = new WorkloadJobResponse();
        job.setJobId(UUID.randomUUID().toString());
        job.setStatus(WorkloadJobStatus.QUEUED);
        job.setSubmittedAt(Instant.now());
        job.setRequest(request);
        workloadJobRepository.save(job);

        workloadExecutor.submit(() -> executeJob(job.getJobId(), request));
        return job;
    }

    public WorkloadJobResponse getJob(String jobId) {
        return workloadJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown jobId: " + jobId));
    }

    public List<WorkloadJobResponse> getHistory() {
        return workloadJobRepository.findAll();
    }

    public WorkloadSummaryResponse getSummary() {
        List<WorkloadJobResponse> jobs = workloadJobRepository.findAll();
        WorkloadSummaryResponse summary = new WorkloadSummaryResponse();
        summary.setTotalJobs(jobs.size());
        summary.setQueuedJobs(jobs.stream().filter(job -> job.getStatus() == WorkloadJobStatus.QUEUED).count());
        summary.setRunningJobs(jobs.stream().filter(job -> job.getStatus() == WorkloadJobStatus.RUNNING).count());
        summary.setCompletedJobs(jobs.stream().filter(job -> job.getStatus() == WorkloadJobStatus.COMPLETED).count());
        summary.setFailedJobs(jobs.stream().filter(job -> job.getStatus() == WorkloadJobStatus.FAILED).count());

        List<WorkloadResponse> successfulResults = jobs.stream()
            .map(WorkloadJobResponse::getResult)
            .filter(result -> result != null)
            .toList();

        if (!successfulResults.isEmpty()) {
            summary.setAverageAggregateScore(successfulResults.stream()
                .mapToDouble(WorkloadResponse::getAggregateScore)
                .average()
                .orElse(0.0));
            summary.setAverageEstimatedRuntimeMs(successfulResults.stream()
                .mapToDouble(WorkloadResponse::getEstimatedRuntimeMs)
                .average()
                .orElse(0.0));
        }

        return summary;
    }

    private void executeJob(String jobId, WorkloadRequest request) {
        WorkloadJobResponse job = getJob(jobId);
        job.setStatus(WorkloadJobStatus.RUNNING);
        job.setStartedAt(Instant.now());
        workloadJobRepository.save(job);

        try {
            WorkloadResponse result = workloadAnalysisService.analyze(request);
            job.setResult(result);
            job.setStatus(WorkloadJobStatus.COMPLETED);
        } catch (Exception ex) {
            job.setStatus(WorkloadJobStatus.FAILED);
            job.setErrorMessage(ex.getMessage());
        } finally {
            job.setCompletedAt(Instant.now());
            workloadJobRepository.save(job);
        }
    }
}
