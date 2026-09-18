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
}