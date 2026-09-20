# Kafka Employee Event Schema Reference

## Topic Details
- **Topic Name**: `employee-events`
- **Serialization**: JSON (`JsonSerializer` / `JsonDeserializer`)

---

## Event Types

1. `CREATED` — Fired when a new employee record is successfully created in MongoDB.
2. `DELETED` — Fired when an employee record is soft-deleted (`deletedAt` updated).

---

## Event Payload Structure (`EmployeeEvent`)

```json
{
  "eventType": "CREATED",
  "empId": "sh1a2b3c4d5e6f78",
  "name": "Jane Doe",
  "designation": "Software Engineer",
  "salary": 95000.00,
  "timestamp": "2026-09-20T17:52:00Z"
}
```

### Fields Description

| Field | Type | Description |
| :--- | :--- | :--- |
| `eventType` | `String` | Event action type: `"CREATED"` or `"DELETED"` |
| `empId` | `String` | Unique employee identifier |
| `name` | `String` | Employee full name |
| `designation` | `String` | Employee job title |
| `salary` | `Double` | Employee salary |
| `timestamp` | `String` | ISO-8601 UTC timestamp of event creation |
