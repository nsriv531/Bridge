package com.example.snowbridge.controller;

import com.example.snowbridge.model.WorkloadJobResponse;
import com.example.snowbridge.model.WorkloadRequest;
import com.example.snowbridge.model.WorkloadResponse;
import com.example.snowbridge.model.WorkloadSummaryResponse;
import com.example.snowbridge.service.WorkloadAnalysisService;
import com.example.snowbridge.service.WorkloadOrchestrationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workloads")
public class WorkloadController {
    private final WorkloadAnalysisService workloadAnalysisService;
    private final WorkloadOrchestrationService workloadOrchestrationService;

    public WorkloadController(WorkloadAnalysisService workloadAnalysisService,
                              WorkloadOrchestrationService workloadOrchestrationService) {
        this.workloadAnalysisService = workloadAnalysisService;
        this.workloadOrchestrationService = workloadOrchestrationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<WorkloadResponse> analyze(@Valid @RequestBody WorkloadRequest request) {
        return ResponseEntity.ok(workloadAnalysisService.analyze(request));
    }

    @PostMapping("/submit")
    public ResponseEntity<WorkloadJobResponse> submit(@Valid @RequestBody WorkloadRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(workloadOrchestrationService.submit(request));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<WorkloadJobResponse> getJob(@PathVariable String jobId) {
        return ResponseEntity.ok(workloadOrchestrationService.getJob(jobId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WorkloadJobResponse>> history() {
        return ResponseEntity.ok(workloadOrchestrationService.getHistory());
    }

    @GetMapping("/summary")
    public ResponseEntity<WorkloadSummaryResponse> summary() {
        return ResponseEntity.ok(workloadOrchestrationService.getSummary());
    }
}
