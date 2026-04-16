# Glacier

Glacier is a hybrid data platform prototype that models how modern infrastructure separates a **control plane** from a **compute plane**.

- The **Java Spring Boot service** accepts workload requests, validates them, tracks jobs, and orchestrates execution.
- The **C++ engine** performs the computational analysis and returns structured JSON with risk, runtime, and scaling recommendations.

This upgraded version moves beyond a single demo endpoint and behaves more like a miniature workload orchestration platform.

## What was added

### Control plane upgrades

- synchronous analysis endpoint
- asynchronous job submission endpoint
- job status lookup
- workload history endpoint
- aggregate summary endpoint
- centralized API error handling
- configurable worker pool for background execution

### Compute engine upgrades

- workload type support: `BATCH`, `STREAMING`, `INTERACTIVE`
- priority support: `LOW`, `NORMAL`, `HIGH`, `CRITICAL`
- optional SLA target evaluation
- bottleneck classification
- recommended worker count
- per-partition memory pressure and hotspot metrics

## API overview

### Health

`GET /api/v1/health`

### Synchronous analysis

`POST /api/v1/workloads/analyze`

Example request:

```json
{
  "jobName": "daily-revenue-rollup",
  "partitions": 6,
  "rowsPerPartition": 180000,
  "skewFactor": 1.6,
  "cachePressure": 0.42,
  "concurrency": 4,
  "workloadType": "BATCH",
  "priority": "HIGH",
  "targetSlaMs": 4500
}
```

### Asynchronous orchestration

- `POST /api/v1/workloads/submit`
- `GET /api/v1/workloads/{jobId}`
- `GET /api/v1/workloads/history`
- `GET /api/v1/workloads/summary`

## Build notes

### C++ engine

```bash
cd cpp-engine
cmake -S . -B build
cmake --build build
```

### Java service

Use Maven in `java-service/` after building the engine and ensuring `app.engine.path` points to the compiled binary.

## Suggested next steps

- replace process execution with gRPC
- add persistent job storage
- add Docker / docker-compose
- add tracing and metrics
- add auth and multi-tenant workload isolation
