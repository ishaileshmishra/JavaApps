# JavaApps: Employee Management & Event-Driven Microservice

A production-ready **Spring Boot** microservice for managing employee records, featuring **Stateless JWT Security**, **MongoDB** persistence with soft-deletes, **Apache Kafka** event streaming, and **Spring Boot Actuator** observability.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture & Data Flow](#architecture--data-flow)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Authentication Workflow](#authentication-workflow)
- [API Reference](#api-reference)
- [cURL Usage Examples](#curl-usage-examples)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Configuration](#configuration)
- [Project Structure](#project-structure)

---

## Overview

When employees are created or deleted, an asynchronous domain event (`EmployeeEvent`) is dispatched to Kafka. Downstream consumers (such as audit logs, notifications, or cache invalidators) can consume these events in real time without blocking core business transactions.

All endpoints (except public authentication and health routes) are secured using **Stateless JWT Authentication**.

---

## Key Features

- **Stateless JWT Security**: Role-based access control, BCrypt password hashing, and token validation.
- **RESTful CRUD with Soft Delete**: Employee records maintain auditability via timestamped soft-deletion (`deletedAt`).
- **Event-Driven Messaging**: Asynchronous event publishing (`CREATED`, `DELETED`) to Apache Kafka using JSON serialization.
- **Observability & Probes**: Spring Boot Actuator health checks and custom application health indicators (`/actuator/health` and `/health`).
- **Standardized Error Envelope**: Centralized exception handling with structured JSON error responses.

---

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language & Runtime** | Java 17 |
| **Framework** | Spring Boot 4.1.1 |
| **Security** | Spring Security 6+ & JJWT (`0.12.6`) |
| **Database** | MongoDB (`employeedb`) via Spring Data MongoDB |
| **Messaging** | Apache Kafka (Producer & Consumer) |
| **Observability** | Spring Boot Actuator & Micrometer |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Gradle (Groovy DSL) with Gradle Wrapper (`./gradlew`) |
| **Testing** | JUnit 5, Mockito, Spring Security Test, Spring Boot Test |

---

## Architecture & Data Flow

```text
                                 HTTP Client (cURL / Postman / Frontend)
                                                    │
                                                    ▼
                                      [JwtAuthenticationFilter]
                                        │ (Validates Bearer JWT)
                ┌───────────────────────┴────────────────────────┬───────────────────────┐
                ▼                                                ▼                       ▼
      /api/v1/auth/** (Public)                        /api/v1/health (Public)   /api/v1/employees/** (Protected)
      AuthController                                  HealthController & Actuator      EmployeeController
             │                                                   │                               │
             ▼                                                   ▼                               ▼
      AuthService                                    ApplicationHealthIndicator           EmployeeService
             │                                                                                   │
             ▼                                                                    ┌──────────────┴──────────────┐
       UserRepository                                                             ▼                             ▼
       (MongoDB: users)                                                  EmployeeRepository           EmployeeEventProducer
                                                                        (MongoDB: employeedb)                   │
                                                                                                                ▼
                                                                                                        Kafka Topic:
                                                                                                      employee-events
                                                                                                                │
                                                                                                                ▼
                                                                                                      EmployeeEventConsumer
                                                                                                      (@KafkaListener)
```

---

## Prerequisites

- **Java 17+** ([Adoptium Temurin](https://adoptium.net/))
- **MongoDB** running on `localhost:27017`
- **Apache Kafka** running on `localhost:9092`
- **Gradle** (included via `./gradlew`)

---

## Getting Started

### 1. Start Infrastructure Services

#### Using Homebrew (macOS):
```bash
# Start MongoDB
brew services start mongodb-community

# Start Apache Kafka
brew services start kafka
```

#### Verify Services are Listening:
```bash
# Check MongoDB (port 27017)
nc -zv localhost 27017

# Check Kafka (port 9092)
nc -zv localhost 9092
```

### 2. Build the Application
```bash
./gradlew clean build
```

### 3. Run the Application
```bash
./gradlew bootRun
```

The application runs at **`http://localhost:8080/api/v1`**.

---

## Authentication Workflow

The application uses **stateless JWT Bearer authentication**. Follow this two-step process:

1. **Register or Login** to receive a JWT access token.
2. Pass the token in the `Authorization` header for protected endpoints:
   ```text
   Authorization: Bearer <your-jwt-token>
   ```

---

## API Reference

Base URL: `http://localhost:8080/api/v1`

### 1. Authentication (`/auth`) — Public

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new user | `200 OK`, `400 Bad Request`, `409 Conflict` |
| `POST` | `/auth/login` | Authenticate and obtain JWT token | `200 OK`, `400 Bad Request`, `401 Unauthorized` |

### 2. Health & Monitoring — Public

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/health` | Lightweight service health ping | `200 OK` |
| `GET` | `/actuator/health` | Comprehensive Actuator health & component status | `200 OK`, `503 Service Unavailable` |
| `GET` | `/actuator/info` | Application info | `200 OK` |
| `GET` | `/actuator/metrics` | Micrometer metrics summary | `200 OK` |

### 3. Employees (`/employees`) — Protected (JWT Required)

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/employees` | List all active (non-deleted) employees | `200 OK`, `401 Unauthorized` |
| `GET` | `/employees/{empId}` | Fetch employee by unique ID | `200 OK`, `404 Not Found` |
| `POST` | `/employees` | Create a new employee & publish Kafka event | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `PUT` | `/employees/{empId}` | Update employee details | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/employees/{empId}` | Soft delete employee & publish Kafka event | `200 OK`, `404 Not Found` |

---

## cURL Usage Examples

### 1. Check Health
```bash
# Lightweight ping
curl -i http://localhost:8080/api/v1/health

# Full Actuator health report
curl -i http://localhost:8080/api/v1/actuator/health
```

### 2. Register & Login
```bash
# Register a new user
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123",
    "role": "ADMIN"
  }'

# Login and extract token into variable
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }' | sed -E 's/.*"token":"([^"]+)".*/\1/')

echo "JWT Token: $TOKEN"
```

### 3. Manage Employees

#### Create an Employee (`POST /employees`)
```bash
curl -i -X POST http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Staff Software Engineer",
    "salary": 140000.00
  }'
```

#### List All Employees (`GET /employees`)
```bash
curl -i -X GET http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer $TOKEN"
```

#### Get Employee by ID (`GET /employees/{empId}`)
```bash
curl -i -X GET http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78 \
  -H "Authorization: Bearer $TOKEN"
```

#### Update an Employee (`PUT /employees/{empId}`)
```bash
curl -i -X PUT http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Principal Engineer",
    "salary": 160000.00
  }'
```

#### Delete an Employee (`DELETE /employees/{empId}`)
```bash
curl -i -X DELETE http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Error Handling

All controller errors are caught by [`GlobalExceptionHandler`](src/main/java/com/shaileshmishra/app/exception/GlobalExceptionHandler.java) and returned in a consistent JSON format:

```json
{
  "error_message": "Employee already exists.",
  "error_code": 409,
  "errors": {
    "name": [
      "Employee with name 'Jane Doe' already exists."
    ]
  }
}
```

### Common Error Codes

| Status Code | Scenario | Sample Error Message |
| :--- | :--- | :--- |
| `400 Bad Request` | Missing/invalid fields or invalid URL path | `"Validation failed"` or `"The requested url path is not valid."` |
| `401 Unauthorized` | Missing or expired JWT token | `"Unauthorized access. Valid JWT token is required."` |
| `404 Not Found` | Employee ID not found or already deleted | `"Employee With UID Not Found."` |
| `409 Conflict` | Employee name or username already exists | `"Employee already exists."` or `"Username already exists"` |

---

## Testing

The project includes unit, controller (`@WebMvcTest`), and full integration tests (`@SpringBootTest`).

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Classes
```bash
# Employee service tests
./gradlew test --tests "com.shaileshmishra.app.employee.service.EmployeeServiceTest"

# Security integration tests
./gradlew test --tests "com.shaileshmishra.app.security.SecurityIntegrationTest"

# Health endpoints integration tests
./gradlew test --tests "com.shaileshmishra.app.health.HealthEndpointIntegrationTest"
```

### Test Reports
HTML reports are generated at:
```text
build/reports/tests/test/index.html
```

---

## Configuration

Settings are configured in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.application.name=app

# Global context path
server.servlet.context-path=/api/v1

# Actuator & Health
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always
management.endpoint.health.probes.enabled=true

# MongoDB
spring.mongodb.uri=mongodb://localhost:27017/employeedb
spring.data.mongodb.auto-index-creation=true

# Apache Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.group-id=employee-service-group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.listener.missing-topics-fatal=false

# JWT Security
app.jwt.secret=9a2f8c3e4b1a6d8e7f0c9b8a7d6e5f4c3b2a109876543210fedcba9876543210
app.jwt.expiration-ms=86400000
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/shaileshmishra/app/
│   │   ├── AppApplication.java                     # Application bootstrap
│   │   ├── auth/                                   # Authentication & User Registration
│   │   │   ├── controller/AuthController.java      # /auth/register, /auth/login
│   │   │   ├── dto/                                # Register, Login, AuthResponse DTOs
│   │   │   └── service/AuthService.java            # Authentication logic
│   │   ├── common/util/
│   │   │   └── UtcTimestamp.java                   # ISO UTC Timestamp utility
│   │   ├── comsumer/
│   │   │   └── EmployeeEventConsumer.java          # Kafka event consumer listener
│   │   ├── employee/                               # Core Employee Domain
│   │   │   ├── controller/EmployeeController.java  # /employees REST controller
│   │   │   ├── dto/                                # EmployeeRequestDTO, EmployeeResponseDTO
│   │   │   ├── event/                              # EmployeeEvent model & Producer
│   │   │   ├── models/Employee.java                # MongoDB document model
│   │   │   ├── repository/EmployeeRepository.java  # MongoRepository query methods
│   │   │   ├── service/EmployeeService.java        # Business logic & event emission
│   │   │   └── util/EmployeeIdGenerator.java       # Custom 16-character ID generator
│   │   ├── exception/                              # Centralized error handling
│   │   │   ├── GlobalExceptionHandler.java         # @RestControllerAdvice
│   │   │   └── *.java                              # Custom domain exceptions
│   │   ├── health/                                 # Health checks & Actuator indicators
│   │   │   ├── ApplicationHealthIndicator.java     # Custom HealthIndicator
│   │   │   ├── HealthController.java               # GET /health endpoint
│   │   │   └── dto/HealthResponseDTO.java          # Health response model
│   │   ├── practice/                               # DSA Sandbox (LRU Cache, Stack, Queue)
│   │   ├── security/                               # Spring Security & JWT Filter Chain
│   │   │   ├── SecurityConfig.java                 # SecurityFilterChain & public routes
│   │   │   ├── JwtAuthenticationFilter.java        # Bearer token extractor
│   │   │   ├── JwtTokenProvider.java               # HMAC-SHA256 signer/parser
│   │   │   └── CustomUserDetailsService.java       # UserDetails adapter
│   │   └── user/                                   # User Persistence
│   │       ├── model/User.java                     # MongoDB user entity
│   │       └── repository/UserRepository.java      # User query repository
│   └── resources/
│       └── application.properties                  # Spring Boot configuration
└── test/
    └── java/com/shaileshmishra/app/                # Comprehensive unit & integration tests
```
