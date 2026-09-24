# JavaApps — Employee Management Microservice

A **Spring Boot** microservice for managing employee records with stateless JWT security, MongoDB persistence, Apache Kafka event streaming, and Docker support.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| Language | Java 17 |
| Framework | Spring Boot 4.1.1 |
| Security | Spring Security 6 + JJWT 0.12.6 |
| Database | MongoDB (`employeedb`) |
| Messaging | Apache Kafka |
| Cache | Redis |
| Build | Gradle (via `./gradlew`) |
| Containerization | Docker + Docker Compose |

---

## Running Locally

### Option 1 — Docker (Recommended)

Starts the app, MongoDB, Redis, and Kafka together.

```bash
# Set required secret
export APP_JWT_SECRET=<your-256-bit-hex-secret>

docker compose up --build -d
```

App is available at `http://localhost:8080/api/v1`.

### Option 2 — Local (Gradle)

Ensure MongoDB (`27017`), Kafka (`9092`), and Redis (`6379`) are running, then:

```bash
export APP_JWT_SECRET=<your-256-bit-hex-secret>
./gradlew bootRun
```

---

## Authentication

All endpoints except `/auth/**`, `/health`, and `/actuator/**` require a JWT Bearer token.

### Register
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "email": "john@example.com", "password": "secret123"}'
```

### Login
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "secret123"}' \
  | sed -E 's/.*"token":"([^"]+)".*/\1/')
```

> **Note:** Employee write operations (`POST`, `PUT`, `PATCH`, `DELETE`) require the `ADMIN` role.

---

## API Reference

Base URL: `http://localhost:8080/api/v1`

### Auth — Public

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login and obtain JWT token |

### Employees — JWT Required (`ADMIN` for writes)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/employees` | List active employees (`?skip=0&limit=20`) |
| `GET` | `/employees/{empId}` | Get employee by ID |
| `POST` | `/employees` | Create employee |
| `PUT` | `/employees/{empId}` | Full update |
| `PATCH` | `/employees/{empId}` | Partial update |
| `DELETE` | `/employees/{empId}` | Soft delete |

#### Create Employee Request Body
```json
{
  "name": "Jane Doe",
  "designation": "Staff Engineer",
  "salary": 140000.00
}
```

### Health — Public

| Endpoint | Description |
| :--- | :--- |
| `GET /health` | Lightweight ping |
| `GET /actuator/health` | Full component health (MongoDB, Redis, Kafka) |

---

## Error Response Format

All errors follow a consistent JSON envelope:

```json
{
  "error_message": "You do not have permission to access this resource.",
  "error_code": 403,
  "errors": {
    "role": ["Access denied"]
  }
}
```

| Code | Scenario |
| :--- | :--- |
| `400` | Validation failure or invalid path |
| `401` | Missing or expired JWT |
| `403` | Insufficient role (`ADMIN` required) |
| `404` | Employee not found |
| `409` | Employee or username already exists |

---

## Key Design Decisions

- **Soft deletes**: `DELETE` sets `deletedAt` timestamp; records are never physically removed.
- **Custom `empId`**: Business-facing ID (e.g., `sh1a2b3c4d5e6f78`) separate from MongoDB's internal `_id`.
- **Kafka events**: `CREATED` and `DELETED` events are published asynchronously to `employee-events` topic.
- **Stateless security**: JWT roles are re-validated from the database on every request (no session state).

---

## Running Tests

```bash
./gradlew test
# Report: build/reports/tests/test/index.html
```

---

## Project Structure

```
src/main/java/com/shaileshmishra/app/
├── auth/           # Registration, login, JWT issuance
├── employee/       # CRUD, soft-delete, Kafka event publishing
├── exception/      # GlobalExceptionHandler + custom exceptions
├── health/         # /health endpoint + Actuator indicator
├── security/       # JWT filter chain, SecurityConfig, AccessDeniedHandler
├── user/           # User model + repository
├── comsumer/       # Kafka event consumer
└── practice/       # DSA sandbox (LRU Cache, Stack, Queue)
```
