---
name: kafka-messaging
description: >-
  Use this skill when developing, updating, or debugging Kafka producers, consumers, event schemas, serializers, or event topic configurations.
---

# Kafka Messaging Skill

This skill documents the event-driven architecture, producer/consumer implementations, serialization protocols, and topic configurations in `com.shaileshmishra.app.employee.event` and `com.shaileshmishra.app.comsumer`.

---

## Architecture & Configuration

- **Topic**: `employee-events`
- **Consumer Group**: `employee-service-group`
- **Broker**: `localhost:9092` (configured in `application.properties`)
- **Fault-tolerance**: `spring.kafka.listener.missing-topics-fatal=false` (prevents app crash if broker starts slowly)

---

## Event Schema (`EmployeeEvent`)

Reference: [`EmployeeEvent.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/event/EmployeeEvent.java)

| Field | Type | Description |
| :--- | :--- | :--- |
| `eventType` | `String` | Event action indicator: `"CREATED"` or `"DELETED"` |
| `empId` | `String` | Custom unique employee ID (also used as Kafka message key) |
| `name` | `String` | Employee name |
| `designation` | `String` | Job title |
| `salary` | `Double` | Employee salary |
| `timestamp` | `String` | ISO-8601 UTC timestamp string (from `UtcTimestamp.now()`) |

---

## Producer & Consumer Implementation

### Producer: [`EmployeeEventProducer`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/employee/event/EmployeeEventProducer.java)
- Injects `KafkaTemplate<String, Object>`.
- Partitions by `empId` as the record key to guarantee in-order delivery per employee.
- Methods:
  - `publishCreateEvent(EmployeeEvent event)`
  - `publishDeleteEvent(EmployeeEvent event)`

### Consumer: [`EmployeeEventConsumer`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/comsumer/EmployeeEventConsumer.java)
- Listens via `@KafkaListener(topics = "employee-events", groupId = "employee-service-group")`.
- Deserializes JSON payloads into `EmployeeEvent` automatically (`spring.json.trusted.packages=*`).
- Handles events via modern switch expression:
  ```java
  switch (event.getEventType()) {
      case "CREATED" -> log.info("📥 Consumer New employee created: {} ({})", event.getName(), event.getEmpId());
      case "DELETED" -> log.info("🗑️ Consumer Employee deleted: {} ({})", event.getName(), event.getEmpId());
      default -> log.warn("Unknown event type: {}", event.getEventType());
  }
  ```

---

## Operational Verification

To inspect live topic data or verify message receipt:
- Review logs: `grep -i "consumer" / path/to/app.log`
- CLI console consumer:
  ```bash
  kafka-console-consumer --bootstrap-server localhost:9092 --topic employee-events --from-beginning
  ```
- Related testing skill: [kafka-event-testing](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/kafka-event-testing/SKILL.md).
