---
name: api-testing
description: >-
  Use this skill when testing, executing cURL commands, or validating HTTP request/response payloads for employee and auth REST API endpoints.
---

# API Testing Skill

This skill provides step-by-step procedures for testing the REST endpoints exposed by the application, including payload validations, status code checks, and authentication workflows.

## Base Configuration

- Base URL: `http://localhost:8080/api/v1`
- Content-Type: `application/json`

---

## Detailed Endpoint Reference

Consult the endpoint reference document for full request/response DTO contracts:
[API Endpoints Reference](./references/api_endpoints.md)

---

## Standard Workflows

### 1. Execute API Smoke Tests
Run the provided automated cURL test script against a running server (`http://localhost:8080/api/v1`):
```bash
./.agents/skills/api-testing/scripts/test_endpoints.sh
```

### 2. Manual cURL Testing

#### Create Employee (`POST /employees`)
```bash
curl -i -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Backend Engineer",
    "salary": 95000.00
  }'
```

#### List Employees (`GET /employees`)
```bash
curl -i http://localhost:8080/api/v1/employees
```

#### Get Employee by ID (`GET /employees/{empId}`)
```bash
curl -i http://localhost:8080/api/v1/employees/<empId>
```

#### Update Employee (`PUT /employees/{empId}`)
```bash
curl -i -X PUT http://localhost:8080/api/v1/employees/<empId> \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "designation": "Senior Engineer",
    "salary": 120000.00
  }'
```

#### Delete Employee (`DELETE /employees/{empId}`)
```bash
curl -i -X DELETE http://localhost:8080/api/v1/employees/<empId>
```

#### Register User (`POST /auth/register`)
```bash
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "role": "ADMIN"
  }'
```

#### Login User (`POST /auth/login`)
```bash
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```
