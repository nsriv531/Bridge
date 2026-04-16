package com.example.snowbridge.repository;

import com.example.snowbridge.model.WorkloadJobResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class WorkloadJobRepository {
    private final Map<String, WorkloadJobResponse> jobs = new ConcurrentHashMap<>();

    public WorkloadJobResponse save(WorkloadJobResponse job) {
        jobs.put(job.getJobId(), job);
        return job;
    }

    public Optional<WorkloadJobResponse> findById(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public List<WorkloadJobResponse> findAll() {
        List<WorkloadJobResponse> result = new ArrayList<>(jobs.values());
        result.sort(Comparator.comparing(WorkloadJobResponse::getSubmittedAt).reversed());
        return result;
    }

    public long count() {
        return jobs.size();
    }
}
