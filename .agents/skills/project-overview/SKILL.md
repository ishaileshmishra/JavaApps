---
name: project-overview
description: >-
  Use this root skill for a concise architectural overview of the entire JavaApps microservice, cross-module data flow, port configurations, and index of package-specific skills.
---

# Project Overview (Root Skill)

A lightweight architectural map of the **JavaApps** Spring Boot microservice. Keep this loaded when seeking a high-level understanding of system interactions; use package skills for domain-specific implementation details.

---

## Tech Stack & Runtime

| Component | Specification |
| :--- | :--- |
| **Language & Runtime** | Java 17, Spring Boot 4.1.1 |
| **Web Layer** | Spring WebMVC (Servlet context: `/api/v1`) |
| **Security** | Spring Security 6+ (Stateless JWT, HMAC-SHA256) |
| **Persistence** | Spring Data MongoDB (`employeedb` database) |
| **Messaging** | Apache Kafka (Producer: JSON, Consumer: `@KafkaListener`) |
| **Build & Test** | Gradle Wrapper (`./gradlew`), JUnit 5, Mockito |

---

## System Flow & Architecture

```text
[Client]
   │
   ▼ HTTP (Bearer Token)
[JwtAuthenticationFilter] ──► Validates JWT via JwtTokenProvider
   │
   ├──► /api/v1/auth/**      ──► AuthController ──► AuthService ──► UserRepository (MongoDB)
   │                                                                 └─► Returns JWT Token
   └──► /api/v1/employees/** ──► EmployeeController ──► EmployeeService
                                                            │
                                                            ├─► EmployeeRepository (MongoDB: employeedb)
                                                            │     (Soft delete via deletedAt)
                                                            │
                                                            └─► EmployeeEventProducer
                                                                     │
                                                                     ▼ Kafka Topic: employee-events
                                                                [Kafka Broker]
                                                                     │
                                                                     ▼
                                                                EmployeeEventConsumer
                                                                (@KafkaListener, group: employee-service-group)
```

---

## Infrastructure & Service Endpoints

- **Spring App**: `http://localhost:8080/api/v1`
- **MongoDB**: `mongodb://localhost:27017/employeedb`
- **Kafka Broker**: `localhost:9092` (Topic: `employee-events`)

---

## Package Skills Directory (Progressive Disclosure)

Activate package-specific skills for granular implementation rules:

| Package / Domain | Skill Identifier | Scope & Key Components |
| :--- | :--- | :--- |
| `com.shaileshmishra.app.employee` | [`employee-package`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/employee-package/SKILL.md) | Employee entity, soft-delete queries, UUID ID generation, CRUD endpoints, event dispatch. |
| `com.shaileshmishra.app.auth`<br>`com.shaileshmishra.app.security`<br>`com.shaileshmishra.app.user` | [`auth-security`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/auth-security/SKILL.md) | JWT auth filter chain, token provider, BCrypt encoding, User entity & repo, registration/login. |
| `com.shaileshmishra.app.comsumer`<br>`com.shaileshmishra.app.employee.event` | [`kafka-messaging`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/kafka-messaging/SKILL.md) | Event schema (`EmployeeEvent`), JSON producer, asynchronous consumer with group management. |
| `com.shaileshmishra.app.exception` | [`exception-handling`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/exception-handling/SKILL.md) | `@RestControllerAdvice`, standard error envelope, domain exception mapping (400, 401, 404, 409). |
| `com.shaileshmishra.app.practice` | [`practice-dsa`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/practice-dsa/SKILL.md) | Standalone DSA implementations (LRU Cache, Stack, Queue, Binary Search). |

### Operational Workflow Skills
- [api-testing](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/api-testing/SKILL.md): cURL commands and endpoint testing.
- [gradle-workflow](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/gradle-workflow/SKILL.md): Compilation, testing, and bootRun.
- [infrastructure-management](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/infrastructure-management/SKILL.md): Docker Compose and local services.
- [kafka-event-testing](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/kafka-event-testing/SKILL.md): Topic inspection and event verification.
