# API Endpoints & DTO Contracts Reference

## Base Path
`http://localhost:8080/api/v1`

---

## 1. Employee Endpoints (`/employees`)

| Method   | Endpoint             | Description                | Success Status | Error Codes |
| :------- | :------------------- | :------------------------- | :------------- | :---------- |
| `GET`    | `/employees`         | Fetch active employees     | `200 OK`       | `500` |
| `GET`    | `/employees/{empId}` | Fetch employee by ID       | `200 OK`       | `404` |
| `POST`   | `/employees`         | Create employee            | `201 Created`  | `400`, `409` |
| `PUT`    | `/employees/{empId}` | Update employee            | `200 OK`       | `400`, `404` |
| `DELETE` | `/employees/{empId}` | Soft delete employee       | `200 OK`       | `404` |

### Schemas

#### EmployeeRequestDTO
```json
{
  "name": "Jane Doe",         // Required, non-blank
  "designation": "Developer", // Required, non-blank
  "salary": 95000.00          // Required, positive
}
```

#### EmployeeResponseDTO
```json
{
  "empId": "sh1a2b3c4d5e6f78",
  "name": "Jane Doe",
  "designation": "Developer",
  "salary": 95000.00
}
```

---

## 2. Authentication Endpoints (`/auth`)

| Method | Endpoint         | Description           | Success Status | Error Codes |
| :----- | :--------------- | :-------------------- | :------------- | :---------- |
| `POST` | `/auth/register` | Register new user     | `200 OK`       | `400`, `409` |
| `POST` | `/auth/login`    | Login & receive JWT   | `200 OK`       | `400`, `401` |

---

## 3. Global Exception Response Schema (`ErrorResponse`)

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
