---
name: gradle-workflow
description: >-
  Use this skill when compiling, building, testing, or running Gradle tasks for this Java 17 Spring Boot application.
---

# Gradle Workflow Skill

This skill provides instructions and automation scripts for managing the Gradle build lifecycle, running unit and slice tests, generating test reports, and starting the Spring Boot application.

## Prerequisites

- JDK 17 installed and configured (`java -version`).
- Gradle Wrapper (`./gradlew`) located in the project root.

---

## Key Workflows

### 1. Build & Compile
To perform a clean build and compile all source and test classes:
```bash
./gradlew clean build
```

To skip running tests during build:
```bash
./gradlew build -x test
```

### 2. Running Tests
To run all unit and integration tests:
```bash
./gradlew test
```

To run a specific test class:
```bash
./gradlew test --tests "com.shaileshmishra.app.employee.service.EmployeeServiceTest"
```

To run a specific test method:
```bash
./gradlew test --tests "com.shaileshmishra.app.employee.service.EmployeeServiceTest.createEmployee_shouldSaveAndPublishEvent"
```

To force re-running tests without Gradle cache:
```bash
./gradlew test --rerun
```

To run helper script for test execution:
```bash
./.agents/skills/gradle-workflow/scripts/run_tests.sh
```

### 3. Inspecting Test Results
Test reports are compiled in HTML format:
- Path: `build/reports/tests/test/index.html`

### 4. Running the Spring Boot Application
To start the application locally:
```bash
./gradlew bootRun
```
The application will listen at `http://localhost:8080/api/v1`.
