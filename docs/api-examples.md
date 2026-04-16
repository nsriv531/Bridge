# API Examples

## Analyze synchronously

```bash
curl -X POST http://localhost:8080/api/v1/workloads/analyze \
  -H 'Content-Type: application/json' \
  -d '{
    "jobName": "checkout-events",
    "partitions": 8,
    "rowsPerPartition": 120000,
    "skewFactor": 1.4,
    "cachePressure": 0.30,
    "concurrency": 4,
    "workloadType": "STREAMING",
    "priority": "HIGH",
    "targetSlaMs": 3500
  }'
```

## Submit asynchronously

```bash
curl -X POST http://localhost:8080/api/v1/workloads/submit \
  -H 'Content-Type: application/json' \
  -d '{
    "jobName": "customer-360-refresh",
    "partitions": 10,
    "rowsPerPartition": 200000,
    "skewFactor": 1.7,
    "cachePressure": 0.48,
    "concurrency": 5,
    "workloadType": "BATCH",
    "priority": "NORMAL",
    "targetSlaMs": 7000
  }'
```

## Fetch job status

```bash
curl http://localhost:8080/api/v1/workloads/<jobId>
```

## Fetch workload summary

```bash
curl http://localhost:8080/api/v1/workloads/summary
```
