package com.example.snowbridge.service;

import com.example.snowbridge.model.WorkloadRequest;
import com.example.snowbridge.model.WorkloadResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WorkloadAnalysisService {
    private final ObjectMapper objectMapper;
    private final String enginePath;
    private final Duration timeout;

    public WorkloadAnalysisService(ObjectMapper objectMapper,
                                   @Value("${app.engine.path}") String enginePath,
                                   @Value("${app.engine.timeout-seconds:5}") long timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.enginePath = enginePath;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    public WorkloadResponse analyze(WorkloadRequest request) {
        List<String> command = new ArrayList<>();
        command.add(enginePath);
        command.add(request.getJobName());
        command.add(String.valueOf(request.getPartitions()));
        command.add(String.valueOf(request.getRowsPerPartition()));
        command.add(String.valueOf(request.getSkewFactor()));
        command.add(String.valueOf(request.getCachePressure()));
        command.add(String.valueOf(request.getConcurrency()));
        command.add(normalizeOrDefault(request.getWorkloadType(), "BATCH"));
        command.add(normalizeOrDefault(request.getPriority(), "NORMAL"));
        command.add(String.valueOf(request.getTargetSlaMs() == null ? 0 : request.getTargetSlaMs()));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new IllegalStateException("C++ engine timed out.");
            }

            String output = readOutput(process);

            if (process.exitValue() != 0) {
                throw new IllegalStateException("C++ engine failed: " + output);
            }

            return objectMapper.readValue(output, WorkloadResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to launch C++ engine at path: " + enginePath, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Engine execution was interrupted.", e);
        }
    }

    private String normalizeOrDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String readOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }
}
