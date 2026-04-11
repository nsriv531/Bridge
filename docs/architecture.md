# Architecture Notes

## Goal

This project is intentionally designed to resemble the split used in data infrastructure companies.

- The **Java layer** behaves like a control plane service.
- The **C++ layer** behaves like a compute engine.

## Why this matters

Typical enterprise apps often stop at REST + database.
This project shows a stronger systems angle:

- API receives workload metadata
- orchestration layer validates and coordinates execution
- compute engine focuses on performance-sensitive scoring logic
- results are returned in a platform-friendly JSON format

## Suggested talking points in interviews

1. Why did you separate Java and C++?
   - Java is productive for API and service orchestration.
   - C++ is strong for performance-sensitive compute logic.

2. Why not keep it all in Java?
   - Separation better mirrors real platform architecture and lets the compute layer evolve independently.

3. What would you improve in production?
   - gRPC instead of shell execution
   - async jobs and durable queues
   - workload history persistence
   - containerized deployment
   - observability and tracing
