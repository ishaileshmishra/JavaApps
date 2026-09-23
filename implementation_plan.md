# Project Evolution: Employee Lifecycle & IT Provisioning Orchestration

The goal is to evolve the current "Employee Management" learning exercise into a **production-grade, event-driven orchestration system** that solves a genuine corporate problem: **IT Onboarding and Equipment Provisioning**.

In the real world, when a new employee is hired, multiple departments (IT, HR, Security) must act. This project will demonstrate how to use **Apache Kafka** to orchestrate these actions asynchronously, decoupling the core HR system from IT ticketing and access management.

## 1. The Real-World Problem & Value Proposition

**The Problem**: Employee onboarding is often a manual, fragmented process. HR adds an employee to a database, then manually emails IT to configure a laptop, and Security to create a badge. This leads to delays, missed steps, and a poor day-one experience.
**The Solution**: An event-driven Employee Lifecycle system. When HR creates an employee via the REST API, the system publishes a lifecycle event. Independent microservice components (simulated via Kafka consumers in this monolith, easily split later) react to these events to automatically provision resources.

## 2. Architecture & Data Flow

We will build upon the existing Spring Boot + MongoDB + Kafka stack, shifting the architecture to an **Event-Driven Architecture (EDA)**.

```mermaid
flowchart TD
    HR[HR Admin (cURL/UI)] -->|POST /employees| API(Employee API)
    API --> DB[(MongoDB: employeedb)]
    API -->|EmployeeCreatedEvent| KAFKA{{Apache Kafka}}
    
    KAFKA -->|Consume| IT[IT Provisioning Consumer]
    KAFKA -->|Consume| SEC[Security Access Consumer]
    KAFKA -->|Consume| NOTIFY[Notification Consumer]
    
    IT --> ITDB[(MongoDB: assets)]
    SEC --> SECDB[(MongoDB: access_logs)]
    NOTIFY --> MOCK[Mock Email Service]
```

## 3. Implementation Steps (The Complete Lifecycle)

### Phase 1: Core Domain Expansion & Validation
- **Refine Employee Model**: Add real-world fields (e.g., `department`, `employmentType`, `startDate`, `managerId`).
- **Domain-Driven Design (DDD)**: Restructure the app into clear bounded contexts (HR, IT Assets, Notifications).
- **Advanced Validation**: Implement custom annotations to validate complex rules (e.g., `startDate` cannot be in the past).

### Phase 2: Event-Driven Orchestration (Kafka)
- **Robust Event Schemas**: Define versioned event schemas (e.g., `EmployeeOnboarded_v1`) to handle schema evolution.
- **Idempotent Consumers**: Ensure Kafka consumers can safely handle duplicate messages (at-least-once delivery semantics).
- **Dead Letter Queues (DLQ)**: Configure Kafka DLQs to catch and inspect failed event processing (e.g., a hardware provisioning failure).

### Phase 3: Observability & Resilience
- **Distributed Tracing**: Integrate Micrometer Tracing (with Zipkin or Jaeger) so a single request ID flows from the REST API through Kafka to the consumers.
- **Advanced Actuator**: Create custom health checks for downstream simulated services and track custom metrics (e.g., `employees.onboarded.total`).
- **Resilience4j**: Add Circuit Breakers and Retries for any simulated external API calls (like sending an email).

### Phase 4: Production-Grade Testing
- **Testcontainers**: Replace embedded testing with Docker-based **Testcontainers** for real MongoDB and Kafka instances during integration tests.
- **Contract Testing**: Use Spring Cloud Contract to ensure the Kafka producer and consumers agree on the event schema.

### Phase 5: CI/CD & Deployment Strategy
- **Dockerization**: Refine the existing `Dockerfile` with multi-stage builds and non-root users for security.
- **GitHub Actions Pipeline**: Create a `.github/workflows/ci.yml` for automated testing, linting, and Docker image publishing on every PR.
- **Deployment Manifests**: Provide a complete `docker-compose.prod.yml` or Kubernetes Helm chart for a realistic deployment topology.

> [!NOTE]
> This plan transforms the app from a simple CRUD API into a demonstration of distributed system patterns (Event Sourcing, Saga/Choreography, Idempotency, Tracing).

## User Review Required

> [!IMPORTANT]
> **Domain Focus**: Does "Employee Lifecycle & IT Provisioning" sound like the right real-world pivot for your goals, or would you prefer a different domain (e.g., a scalable E-commerce Order Management system, which also fits this tech stack perfectly)?

> [!TIP]
> **Monolith vs. Microservices**: I recommend keeping this as a "Modular Monolith" first. The bounded contexts (HR, IT, Notifications) will be separate packages, communicating *only* via Kafka. This makes it easy to split into true microservices later without the immediate operational overhead. 

## Open Questions

1. Do you want to proceed with this **IT Onboarding** domain, or do you have another specific domain in mind?
2. Are there any specific Spring Boot ecosystem tools you want to prioritize learning in this project (e.g., Spring Batch, Spring Cloud Gateway, Redis caching)?
3. Once approved, I will create the GitHub Actions CI/CD pipeline and configure Testcontainers as our first technical step towards production-readiness. Shall we proceed?
