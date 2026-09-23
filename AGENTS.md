# JavaApps Workspace Guidelines

## Response & Verification Standards
1. **Never Assume or Guess**: Never provide generic or theoretical answers. Always inspect the relevant codebase files (e.g., Dockerfile, build.gradle, properties) and check active system states before answering.
2. **Validate Before Responding**: If providing commands, verify that all prerequisites in the repository are satisfied and that the command works end-to-end.
3. **Be Concrete and Precise**: Ground every explanation in the exact code, file paths, and configurations present in this workspace.

## Architecture & Progressive Disclosure
This project employs Antigravity Skills for modular context loading to minimize token consumption.

- **Root Architecture Guide**: Activate the [`project-overview`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/project-overview/SKILL.md) skill for high-level system topology, data flow, ports, and package routing.
- **Package-Specific Skills**: Always activate the corresponding package skill before modifying domain code:
  - `com.shaileshmishra.app.employee` ──► [`employee-package`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/employee-package/SKILL.md)
  - `com.shaileshmishra.app.auth / security / user` ──► [`auth-security`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/auth-security/SKILL.md)
  - `com.shaileshmishra.app.comsumer / employee.event` ──► [`kafka-messaging`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/kafka-messaging/SKILL.md)
  - `com.shaileshmishra.app.exception` ──► [`exception-handling`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/exception-handling/SKILL.md)
  - `com.shaileshmishra.app.practice` ──► [`practice-dsa`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/.agents/skills/practice-dsa/SKILL.md)

## Core Invariants
1. **Employee Deletions**: Must be soft-deletes via `deletedAt = UtcTimestamp.now()`.
2. **Security**: Stateless JWT; public endpoints are `/auth/**`, `/error`, `/actuator/**`, and `/health`.
3. **Events**: Employee creations and deletions must publish an `EmployeeEvent` to Kafka `employee-events`.
4. **Build & Test**: Run `./gradlew test` to verify changes before concluding tasks.
