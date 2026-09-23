package com.shaileshmishra.app.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmployeeNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(
                        EmployeeNotFoundException ex) {

                Map<String, Object> errorBody = Map.of(
                                "error_message", "Employee With UID Not Found.",
                                "error_code", 404,
                                "errors", Map.of("path", List.of(ex.getMessage())));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(errorBody);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<Map<String, Object>> handleNoResourceFound(
                        NoResourceFoundException ex) {

                Map<String, Object> errorBody = Map.of(
                                "error_message", "The requested url path is not valid.",
                                "error_code", 400,
                                "errors", Map.of("path", List.of("is not valid.")));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errorBody);
        }

        @ExceptionHandler(EmployeeAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleEmployeeAlreadyExists(
                        EmployeeAlreadyExistsException ex) {

                Map<String, Object> errorBody = Map.of(
                                "error_message", "Employee already exists.",
                                "error_code", 409,
                                "errors", Map.of("name", List.of(ex.getMessage())));

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(errorBody);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(
                        UserAlreadyExistsException ex) {

                Map<String, Object> errorBody = Map.of(
                                "error_message", ex.getMessage(),
                                "error_code", 409,
                                "errors", Map.of("user", List.of(ex.getMessage())));

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(errorBody);
        }

        @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
        public ResponseEntity<Map<String, Object>> handleBadCredentials(
                        org.springframework.security.authentication.BadCredentialsException ex) {

                Map<String, Object> errorBody = Map.of(
                                "error_message", "Invalid username or password.",
                                "error_code", 401);

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(errorBody);
        }

        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
                Map<String, List<String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(java.util.stream.Collectors.groupingBy(
                                                error -> error.getField() != null ? error.getField() : "unknown",
                                                java.util.stream.Collectors.mapping(
                                                                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                                                                java.util.stream.Collectors.toList())));
                
                return ResponseEntity.badRequest().body(Map.of(
                                "error_code", 400,
                                "errors", fieldErrors));
        }
}