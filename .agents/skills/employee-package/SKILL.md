---
name: employee-package
description: >-
  Use this skill when working on, modifying, or debugging the Employee domain, including Employee entity, repository queries, service layer, REST endpoints, and soft-delete lifecycle.
---

# Employee Package Skill

This skill provides domain-specific instructions, architecture, and patterns for `com.shaileshmishra.app.employee`.

---

## Package Structure

- **Controller**: [`EmployeeController`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/controller/EmployeeController.java) (`/employees`)
- **Service**: [`EmployeeService`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/service/EmployeeService.java)
- **Repository**: [`EmployeeRepository`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/repository/EmployeeRepository.java)
- **Model**: [`Employee`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/models/Employee.java) (`@Document(collection = "employees")`)
- **DTOs**: [`EmployeeRequestDTO`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/dto/EmployeeRequestDTO.java), [`EmployeeResponseDTO`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/dto/EmployeeResponseDTO.java)
- **Utilities**: [`EmployeeIdGenerator`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/util/EmployeeIdGenerator.java)
- **Events**: [`EmployeeEventProducer`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/event/EmployeeEventProducer.java)

---

## Key Domain Patterns & Invariants

### 1. Soft-Delete Lifecycle
- **Never physically remove documents** from MongoDB on `DELETE /employees/{empId}`.
- Setting `deletedAt = UtcTimestamp.now()` flags an employee as inactive.
- All active queries **must filter out soft-deleted records**:
  - `findByDeletedAtIsNull()`
  - `findByEmpIdAndDeletedAtIsNull(empId)`
  - `existsByNameAndDeletedAtIsNull(name)`

### 2. Custom Employee ID (`empId`)
- Mongo internal `_id` is an ObjectId, but external API contracts use business `empId`.
- Generated via `EmployeeIdGenerator.generate()` on creation.
- Route parameters bind to `{empId}` (not `_id`).

### 3. Asynchronous Event Notification
- On successful creation (`createEmployee`), `eventProducer.publishCreateEvent(...)` is dispatched.
- On successful deletion (`deleteEmployee`), `eventProducer.publishDeleteEvent(...)` is dispatched.
- Refer to the [`kafka-messaging`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/kafka-messaging/SKILL.md) skill for event schema and consumer details.

### 4. Duplicate Guard
- Unique active employee names are enforced via:
  ```java
  if (employeeRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
      throw new EmployeeAlreadyExistsException("Employee with name '" + request.getName() + "' already exists.");
  }
  ```

---

## Unit Testing Guidelines

- Mock both `EmployeeRepository` and `EmployeeEventProducer` in `EmployeeServiceTest`.
- Validate that `publishCreateEvent` or `publishDeleteEvent` is called exactly once when service methods succeed.
- Test soft-deleted queries return empty results or throw `EmployeeNotFoundException`.
