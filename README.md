# Glacier

Glacier is a hybrid data platform prototype that demonstrates how modern systems separate control plane orchestration from compute plane execution. The project uses a Java service built with Spring Boot to handle API requests, manage workloads, and coordinate execution, while a high performance C++ engine processes the actual computational tasks. When a request is submitted, the Java layer delegates the job to the C++ engine, collects the results, and returns structured insights to the user. This design mirrors real world data infrastructure patterns where scalable services manage logic and communication while optimized systems handle intensive processing, making it a strong demonstration of distributed system design and cross language integration.

