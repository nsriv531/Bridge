# SnowBridge Platform

SnowBridge Platform is a hybrid demo project that mirrors the split you would see in a modern data platform company.

- **Java + Spring Boot** powers the control plane and API layer.
- **C++** powers a fast compute engine that performs workload scoring on partitioned data.

The Java service accepts a workload request, launches the C++ engine, parses its JSON output, and returns a structured response. This gives you a repo that looks more like a platform or query-service demo than a standard full stack CRUD project.

## Why this is a strong portfolio piece

This project demonstrates:
- enterprise-style Java service design
- cross-language integration between Java and C++
- systems thinking around control plane vs compute plane
- clean API modeling and orchestration
- reproducible local setup for GitHub

## Architecture

```text
Client
  |
  v
Spring Boot API (Java)
  |
  | launches workload_engine executable
  v
C++ Workload Engine
  |
  v
JSON result with partition-level metrics + recommended strategy
```

## Features

- `POST /api/v1/workloads/analyze`
- validates and models incoming requests
- Java service launches the C++ engine with request parameters
- C++ engine simulates partition scans, skew, cache pressure, and latency risk
- engine returns a JSON result with:
  - per-partition metrics
  - aggregate score
  - recommended execution strategy
  - risk level

## Tech Stack

### Java service
- Java 17
- Spring Boot 3
- Maven
- Jackson

### C++ engine
- C++17
- CMake

## Project Structure

```text
snowbridge-platform/
├── java-service/
├── cpp-engine/
└── docs/
```

## Running locally

### 1. Build the C++ engine

```bash
cd cpp-engine
cmake -S . -B build
cmake --build build
```

This produces the executable used by the Java service.

### 2. Run the Java service

```bash
cd ../java-service
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd`.

The API starts on `http://localhost:8080`.

## Example request

```bash
curl -X POST http://localhost:8080/api/v1/workloads/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "retail_site_forecast",
    "partitions": 6,
    "rowsPerPartition": 150000,
    "skewFactor": 1.4,
    "cachePressure": 0.35,
    "concurrency": 4
  }'
```

## Example response

```json
{
  "jobName": "retail_site_forecast",
  "recommendedStrategy": "REBALANCE_AND_SCALE_OUT",
  "riskLevel": "MEDIUM",
  "estimatedRuntimeMs": 1770,
  "aggregateScore": 72.31,
  "partitions": [
    {
      "partitionId": 1,
      "effectiveRows": 160500,
      "partitionCost": 10.87,
      "latencyRisk": 0.34
    }
  ],
  "engineVersion": "1.0.0"
}
```

## Portfolio framing

You can describe this as:

> Built a hybrid data-platform prototype using Spring Boot and C++ that separates control-plane orchestration from compute-plane execution, simulating how large-scale analytics platforms coordinate performance-sensitive workloads.

## Good GitHub next steps

- Add unit tests for the Java service
- Dockerize both services
- Move from process execution to gRPC
- Add a job queue and async polling endpoint
- Add observability with metrics and tracing

