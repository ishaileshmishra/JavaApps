# Employee Management REST API & Event-Driven Microservice

A Spring Boot REST API for managing employee records with **MongoDB** as the persistence store and **Apache Kafka** for event-driven asynchronous messaging.

When an employee is created or deleted, an event is published to Kafka, allowing consumer services (such as notification, audit, or search indexing services) to process changes in real time.

---

## Tech Stack

| Layer                | Technology                                |
|----------------------|-------------------------------------------|
| Language             | Java 17                                   |
| Framework            | Spring Boot 4.1.1                         |
| Web                  | Spring WebMVC                             |
| Database             | MongoDB                                   |
| Event Streaming      | Apache Kafka                              |
| Serialization        | Jackson (`jackson-databind`)              |
| Validation           | Jakarta Bean Validation                   |
| Build Tool           | Gradle (Groovy DSL)                       |
| Testing              | JUnit 5 / Mockito / Spring Boot Test      |

---

## Architecture & Event Flow

```text
HTTP Request (cURL/Postman)
       │
       ▼
EmployeeController  (/api/v1/employees)
       │
       ▼
EmployeeService ──────────────► Save / Soft-Delete in MongoDB (employeedb)
       │
       ▼
EmployeeEventProducer  ────────► Publish JSON event to Kafka Topic ("employee-events")
                                      │
                                      ▼
                             Apache Kafka Broker (localhost:9092)
                                      │
                                      ▼
EmployeeEventConsumer  ◄──────── Subscribe via @KafkaListener ("employee-service-group")
 (Asynchronous Processor)
```

---

## Prerequisites

- **Java 17** or later — [Download](https://adoptium.net/)
- **MongoDB** running on `localhost:27017`
- **Apache Kafka** running on `localhost:9092`
- **Gradle** (Gradle Wrapper `./gradlew` is included)

---

## Getting Started

### 1. Start Infrastructure Services (MongoDB & Kafka)

Using Homebrew on macOS:

```bash
# Start MongoDB
brew services start mongodb-community

# Start Kafka (KRaft mode)
brew services start kafka
```

To verify services are listening:
```bash
# Check Kafka (port 9092)
nc -zv localhost 9092

# Check MongoDB (port 27017)
nc -zv localhost 27017
```

### 2. Build the Application

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The server starts at **`http://localhost:8080/api/v1`**.

---

## API Endpoints

Base URL: `http://localhost:8080/api/v1/employees`

| Method   | Endpoint             | Description              | Status Code |
|----------|----------------------|--------------------------|-------------|
| `GET`    | `/employees`         | List all active employees| `200 OK`    |
| `GET`    | `/employees/{empId}` | Get employee by ID       | `200 OK`    |
| `POST`   | `/employees`         | Create a new employee    | `201 Created` |
| `PUT`    | `/employees/{empId}` | Update an employee       | `200 OK`    |
| `DELETE` | `/employees/{empId}` | Soft-delete an employee  | `200 OK`    |

---

## Request & Response Payloads

### Request Body (`POST` & `PUT`)

```json
{
  "name": "Jane Doe",
  "designation": "Software Engineer",
  "salary": 95000.00
}
```

**Validation Rules:**
- `name`: Required, non-blank
- `designation`: Required, non-blank
- `salary`: Required, positive number

### Response Body (`POST`, `PUT`, `GET`)

```json
{
  "name": "Jane Doe",
  "designation": "Software Engineer",
  "empId": "sh1a2b3c4d5e6f78",
  "salary": 95000.00
}
```

---

## Usage Examples (cURL)

### Create an Employee
```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Backend Engineer",
    "salary": 95000.00
  }'
```

### List All Employees
```bash
curl http://localhost:8080/api/v1/employees
```

### Get Employee by ID
```bash
curl http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78
```

### Update an Employee
```bash
curl -X PUT http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Senior Engineer",
    "salary": 120000.00
  }'
```

### Delete an Employee (Soft Delete)
```bash
curl -X DELETE http://localhost:8080/api/v1/employees/sh1a2b3c4d5e6f78
```

---

## Error Handling

The application uses a `@RestControllerAdvice` to format error responses into a consistent JSON structure.

### 1. Duplicate Employee Name (`409 Conflict`)
Triggered when attempting to create an active employee with a name that already exists:
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

### 2. Employee Not Found (`404 Not Found`)
```json
{
  "error_message": "Employee With UID Not Found.",
  "error_code": 404,
  "errors": {
    "path": [
      "Employee not found with empId: invalid-id"
    ]
  }
}
```

### 3. Invalid Route / Path (`400 Bad Request`)
Triggered when accessing an undefined endpoint path:
```json
{
  "error_message": "The requested url path is not valid.",
  "error_code": 400,
  "errors": {
    "path": [
      "is not valid."
    ]
  }
}
```

---

## Running Unit & Integration Tests

The project includes unit and slice tests for services, controllers, generators, and utilities using **JUnit 5** and **Mockito**.

### Run All Tests via CLI
```bash
./gradlew test
```

### Useful CLI Test Flags
```bash
# Run tests with verbose output
./gradlew test --info

# Run a specific test class
./gradlew test --tests "com.shaileshmishra.app.employee.service.EmployeeServiceTest"

# Run a specific test method
./gradlew test --tests "com.shaileshmishra.app.employee.service.EmployeeServiceTest.ping_shouldReturnAllOkay"

# Force re-run skipping task cache
./gradlew test --rerun
```

Test reports are generated at:
`build/reports/tests/test/index.html`

---

## Configuration

Application settings are defined in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.application.name=app
spring.mongodb.uri=mongodb://localhost:27017/employeedb
spring.data.mongodb.auto-index-creation=true

# Global API Version Prefix
server.servlet.context-path=/api/v1

# Kafka Broker & Producer/Consumer Config
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

spring.kafka.consumer.group-id=employee-service-group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/shaileshmishra/app/
│   │   ├── AppApplication.java                     # Application Entry Point
│   │   ├── comsumer/
│   │   │   └── EmployeeEventConsumer.java          # Kafka Listener Consumer
│   │   ├── common/util/
│   │   │   └── UtcTimestamp.java                    # ISO-8601 UTC Timestamp Util
│   │   ├── employee/
│   │   │   ├── controller/
│   │   │   │   └── EmployeeController.java          # REST Controller (@RequestMapping /employees)
│   │   │   ├── dto/
│   │   │   │   ├── EmployeeRequestDTO.java          # Request Body DTO (Validation)
│   │   │   │   └── EmployeeResponseDTO.java         # Response DTO
│   │   │   ├── event/
│   │   │   │   ├── EmployeeEvent.java               # Kafka Event Payload Model
│   │   │   │   └── EmployeeEventProducer.java       # Kafka Producer Component
│   │   │   ├── models/
│   │   │   │   └── Employee.java                    # MongoDB Document Entity
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java          # MongoRepository Interface
│   │   │   ├── service/
│   │   │   │   └── EmployeeService.java             # Business Logic & Event Dispatch
│   │   │   └── util/
│   │   │       └── EmployeeIdGenerator.java         # Custom ID Generator
│   │   └── exception/
│   │       ├── EmployeeAlreadyExistsException.java # Custom 409 Exception
│   │       ├── EmployeeNotFoundException.java      # Custom 404 Exception
│   │       └── GlobalExceptionHandler.java          # @RestControllerAdvice Error Handler
│   └── resources/
│       └── application.properties                   # App Configuration
└── test/
    └── java/com/shaileshmishra/app/
        ├── AppApplicationTests.java                 # Context Load Test
        ├── common/util/
        │   └── UtcTimestampTest.java                # Timestamp Unit Tests
        └── employee/
            ├── controller/
            │   └── EmployeeControllerTest.java      # Controller WebMvc Tests
            ├── service/
            │   └── EmployeeServiceTest.java         # Service Layer Unit Tests
            └── util/
                └── EmployeeIdGeneratorTest.java     # Generator Unit Tests
```

---

## Key Design Decisions

- **Global Path Versioning** — `server.servlet.context-path=/api/v1` prefixes all endpoints globally without cluttering individual controller code.
- **Event-Driven Architecture** — Uses Kafka to publish `CREATED` and `DELETED` employee events asynchronously, decoupling core business logic from downstream listeners.
- **Soft Delete** — Records set `deletedAt` timestamps rather than being deleted physically from MongoDB. Soft-deleted records are filtered out automatically.
- **Duplicate Prevention** — Validates that an active employee with the same name doesn't exist before saving (`existsByNameAndDeletedAtIsNull`).
- **Constructor Injection** — All Spring components use explicit constructor injection for immutability and testability.
