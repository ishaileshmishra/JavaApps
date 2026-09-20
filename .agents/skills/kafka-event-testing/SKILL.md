---
name: kafka-event-testing
description: >-
  Use this skill when producing, consuming, inspecting, or debugging Kafka events published on the employee-events topic.
---

# Kafka Event Testing Skill

This skill provides procedures for inspecting and testing asynchronous Kafka event publishing and consumption within the microservice.

## Event Architecture Overview

- **Kafka Topic**: `employee-events`
- **Consumer Group**: `employee-service-group`
- **Producer Component**: `EmployeeEventProducer`
- **Consumer Component**: `EmployeeEventConsumer` (@KafkaListener)

---

## Event Schema Reference

Consult the event schema reference for payload fields:
[Kafka Event Schema Reference](./references/event_schema.md)

---

## Operational Workflows

### 1. Verifying Event Publication
When an employee is created via `POST /employees` or deleted via `DELETE /employees/{empId}`, `EmployeeService` invokes `EmployeeEventProducer.sendEmployeeEvent(eventType, employee)`.

Check application logs to verify event publication:
```bash
./gradlew bootRun | grep "Kafka"
```

Look for log entries such as:
- `Sent employee event: EmployeeEvent(...)`

### 2. Verifying Event Consumption
`EmployeeEventConsumer` listens to `employee-events` with consumer group `employee-service-group`.

Look for log entries indicating receipt:
- `Received Employee Event: EmployeeEvent(...)`

### 3. Kafka Console Consumer CLI
To monitor messages on the topic directly using Kafka CLI tools:
```bash
# If using Kafka installed via Homebrew or Docker:
kafka-console-consumer --bootstrap-server localhost:9092 --topic employee-events --from-beginning
```
