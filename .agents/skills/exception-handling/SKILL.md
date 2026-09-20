---
name: exception-handling
description: >-
  Use this skill when adding or handling exceptions, customizing error response JSON schemas, or modifying GlobalExceptionHandler.
---

# Exception Handling Skill

This skill documents error handling conventions, custom domain exceptions, and HTTP status code mappings in `com.shaileshmishra.app.exception`.

---

## Global Exception Handler

All unhandled domain and security exceptions are intercepted by [`GlobalExceptionHandler`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/exception/GlobalExceptionHandler.java) (`@RestControllerAdvice`).

### Standard Error Response Envelope

All API errors conform to the following JSON structure:
```json
{
  "error_message": "Human-readable description of error",
  "error_code": 404,
  "errors": {
    "fieldOrPath": ["Detailed error message"]
  }
}
```

---

## Exception Mappings Table

| Exception Class | HTTP Status | Response `error_code` | Default Message Pattern |
| :--- | :--- | :--- | :--- |
| [`EmployeeNotFoundException`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/exception/EmployeeNotFoundException.java) | `404 NOT FOUND` | `404` | `"Employee With UID Not Found."` |
| [`EmployeeAlreadyExistsException`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/exception/EmployeeAlreadyExistsException.java) | `409 CONFLICT` | `409` | `"Employee already exists."` |
| [`UserAlreadyExistsException`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/exception/UserAlreadyExistsException.java) | `409 CONFLICT` | `409` | Variable message from exception constructor |
| `BadCredentialsException` | `401 UNAUTHORIZED` | `401` | `"Invalid username or password."` |
| `NoResourceFoundException` | `400 BAD REQUEST` | `400` | `"The requested url path is not valid."` |

---

## Adding New Exceptions

When introducing a new business exception:
1. Extend `RuntimeException` in `com.shaileshmishra.app.exception`.
2. Add an `@ExceptionHandler(YourNewException.class)` method in `GlobalExceptionHandler`.
3. Return `ResponseEntity<Map<String, Object>>` matching the envelope format (`error_message`, `error_code`, `errors`).
4. Add unit test assertions checking both HTTP status code and response payload.
