package com.example.snowbridge.controller;

import com.example.snowbridge.model.WorkloadRequest;
import com.example.snowbridge.model.WorkloadResponse;
import com.example.snowbridge.service.WorkloadAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workloads")
public class WorkloadController {
    private final WorkloadAnalysisService workloadAnalysisService;

    public WorkloadController(WorkloadAnalysisService workloadAnalysisService) {
        this.workloadAnalysisService = workloadAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<WorkloadResponse> analyze(@Valid @RequestBody WorkloadRequest request) {
        return ResponseEntity.ok(workloadAnalysisService.analyze(request));
    }
}
