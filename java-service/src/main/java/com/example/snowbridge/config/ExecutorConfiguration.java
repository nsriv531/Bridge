package com.example.snowbridge.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfiguration {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService workloadExecutor(@Value("${app.orchestration.max-workers:4}") int maxWorkers) {
        return Executors.newFixedThreadPool(Math.max(1, maxWorkers));
    }
}
