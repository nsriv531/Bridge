package com.example.snowbridge.controller;

import com.example.snowbridge.repository.WorkloadJobRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final WorkloadJobRepository workloadJobRepository;
    private final String enginePath;

    public HealthController(WorkloadJobRepository workloadJobRepository,
                            @Value("${app.engine.path}") String enginePath) {
        this.workloadJobRepository = workloadJobRepository;
        this.enginePath = enginePath;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", "glacier-control-plane");
        response.put("enginePath", enginePath);
        response.put("trackedJobs", workloadJobRepository.count());
        return response;
    }
}
