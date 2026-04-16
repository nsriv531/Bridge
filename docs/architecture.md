# Architecture Notes

## Current platform shape

Glacier is now structured like a small control-plane / compute-plane platform rather than a single request-response demo.

- The **Java layer** acts as the control plane.
- The **C++ layer** acts as the compute engine.
- The control plane now supports both **synchronous analysis** and **asynchronous job orchestration**.
- Job execution history is tracked in-memory behind a repository abstraction so it can later be swapped for Redis, Postgres, or a queue-backed worker model.

## What the upgraded version now demonstrates

### Control plane responsibilities

- Request validation
- Job submission and tracking
- Async execution using a worker pool
- History and summary endpoints
- Process boundary integration with the native engine
- Error handling suitable for API consumers

### Compute plane responsibilities

- Partition-level workload simulation
- Runtime and risk estimation
- SLA-aware recommendations
- Bottleneck classification
- Scaling guidance through recommended worker counts

## New API capabilities

### Synchronous

- `POST /api/v1/workloads/analyze`

Returns the native engine result immediately.

### Asynchronous

- `POST /api/v1/workloads/submit`
- `GET /api/v1/workloads/{jobId}`
- `GET /api/v1/workloads/history`
- `GET /api/v1/workloads/summary`

These endpoints make the project feel much closer to a real orchestration service.

## Why this is stronger for interviews

This version tells a better distributed-systems story:

1. **Separation of concerns**
   - Java owns orchestration and service behavior.
   - C++ owns performance-sensitive scoring logic.

2. **Evolution path is obvious**
   - Replace shell execution with gRPC.
   - Replace in-memory job tracking with Redis/Postgres.
   - Replace local worker pool with queue-driven background workers.

3. **The workload model is richer**
   - Workload type awareness: `BATCH`, `STREAMING`, `INTERACTIVE`
   - Priority awareness: `LOW`, `NORMAL`, `HIGH`, `CRITICAL`
   - Optional SLA target evaluation
   - Partition memory pressure and hotspot metrics

## Good production next steps

- gRPC between Java and C++
- persistent job state
- durable queue for async execution
- metrics / tracing
- authentication and rate limiting
- Dockerized local environment
