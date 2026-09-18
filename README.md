# Employee Management REST API

A Spring Boot REST API for managing employee records with MongoDB as the data store. The application provides full CRUD operations with request validation, soft-delete support, and global exception handling.

---

## Tech Stack

| Layer          | Technology                        |
|----------------|-----------------------------------|
| Language       | Java 17                           |
| Framework      | Spring Boot 4.1.1                 |
| Web            | Spring WebMVC                     |
| Database       | MongoDB                           |
| Validation     | Jakarta Bean Validation           |
| Build Tool     | Gradle (Groovy DSL)               |
| Testing        | JUnit 5 / Spring Boot Test        |

---

## Prerequisites

- **Java 17** or later — [Download](https://adoptium.net/)
- **MongoDB** running on `localhost:27017` — [Install Guide](https://www.mongodb.com/docs/manual/installation/)
- **Gradle** (the project includes the Gradle Wrapper, so a local install is optional)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ishaileshmishra/JavaApps.git
cd JavaApps
```

### 2. Start MongoDB

Make sure MongoDB is running locally on the default port (`27017`). The application will automatically create a database named `employeedb`.

```bash
# macOS (Homebrew)
brew services start mongodb-community

# Docker (alternative)
docker run -d -p 27017:27017 --name mongo mongo:latest
```

### 3. Build the Application

```bash
./gradlew clean build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

The server starts at **http://localhost:8080**.

---

## API Endpoints

Base URL: `http://localhost:8080/employees`

| Method   | Endpoint             | Description              | Status Code |
|----------|----------------------|--------------------------|-------------|
| `GET`    | `/employees`         | List all employees       | `200 OK`    |
| `GET`    | `/employees/{empId}` | Get employee by ID       | `200 OK`    |
| `POST`   | `/employees`         | Create a new employee    | `201 Created` |
| `PUT`    | `/employees/{empId}` | Update an employee       | `200 OK`    |
| `DELETE` | `/employees/{empId}` | Soft-delete an employee  | `200 OK`    |

### Request Body (POST & PUT)

```json
{
  "name": "John Doe",
  "designation": "Software Engineer",
  "salary": 85000.00
}
```

**Validation Rules:**

| Field         | Rule                         |
|---------------|------------------------------|
| `name`        | Required, must not be blank  |
| `designation` | Required, must not be blank  |
| `salary`      | Must be a positive number    |

### Response Body

```json
{
  "name": "John Doe",
  "designation": "Software Engineer",
  "empId": "sh1a2b3c4d5e6f7890ab",
  "salary": 85000.00
}
```

---

## Usage Examples (cURL)

### Create an Employee

```bash
curl -X POST http://localhost:8080/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "designation": "Backend Developer",
    "salary": 95000.00
  }'
```

### List All Employees

```bash
curl http://localhost:8080/employees
```

### Get Employee by ID

```bash
curl http://localhost:8080/employees/{empId}
```

### Update an Employee

```bash
curl -X PUT http://localhost:8080/employees/{empId} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "designation": "Senior Developer",
    "salary": 120000.00
  }'
```

### Delete an Employee

```bash
curl -X DELETE http://localhost:8080/employees/{empId}
```

> **Note:** Delete is a **soft delete** — the record's `deletedAt` timestamp is set rather than removing it from the database. Soft-deleted employees are excluded from all GET queries.

---

## Error Handling

The API uses a global exception handler that returns structured error responses.

### Employee Not Found (`404`)

```json
{
  "errorMessage": "EMPLOYEE_NOT_FOUND",
  "errorCode": 404,
  "message": "Employee not found with empId: sh1a2b3c4d5e6f"
}
```

### Validation Error (`400`)

Returned when request body fields fail Jakarta validation constraints (e.g., blank name, non-positive salary).

---

## Running Tests

```bash
./gradlew test
```

Test reports are generated at `build/reports/tests/test/index.html`.

---

## Configuration

Application properties are in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.application.name=app
spring.mongodb.uri=mongodb://localhost:27017/employeedb
spring.data.mongodb.auto-index-creation=true
```

To override the MongoDB connection string (e.g., for a remote database), set the environment variable:

```bash
SPRING_MONGODB_URI=mongodb://user:pass@remote-host:27017/employeedb ./gradlew bootRun
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/shaileshmishra/app/
│   │   ├── AppApplication.java                  # Spring Boot entry point
│   │   ├── common/util/
│   │   │   └── UtcTimestamp.java                 # UTC timestamp utility
│   │   ├── employee/
│   │   │   ├── controller/
│   │   │   │   └── EmployeeController.java       # REST controller
│   │   │   ├── dto/
│   │   │   │   ├── EmployeeRequestDTO.java       # Request payload with validation
│   │   │   │   └── EmployeeResponseDTO.java      # Response payload
│   │   │   ├── models/
│   │   │   │   └── Employee.java                 # MongoDB document entity
│   │   │   ├── repository/
│   │   │   │   └── EmployeeRepository.java       # MongoDB repository
│   │   │   ├── service/
│   │   │   │   └── EmployeeService.java          # Business logic layer
│   │   │   └── util/
│   │   │       └── EmployeeIdGenerator.java      # Custom employee ID generator
│   │   └── exception/
│   │       ├── EmployeeNotFoundException.java    # Custom 404 exception
│   │       ├── ErrorResponse.java                # Error response DTO
│   │       └── GlobalExceptionHandler.java       # @RestControllerAdvice handler
│   └── resources/
│       └── application.properties                # App configuration
└── test/
    └── java/com/shaileshmishra/app/
        └── AppApplicationTests.java              # Spring context load test
```

---

## Key Design Decisions

- **Soft Deletes** — Employees are never physically removed. The `deletedAt` field is set with a UTC timestamp, and all queries filter with `deletedAtIsNull`.
- **Custom Employee IDs** — Each employee receives a unique ID (prefixed with `sh`) generated from a UUID, rather than relying on MongoDB's `ObjectId`.
- **DTO Pattern** — Request and response payloads are separated from the persistence model to keep the API contract independent of the database schema.
- **Constructor Injection** — All dependencies are injected via constructors (no `@Autowired` on fields), following Spring best practices.

---

## License

This project is for educational and demonstration purposes.

