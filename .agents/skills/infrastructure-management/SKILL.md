---
name: infrastructure-management
description: >-
  Use this skill when starting, stopping, verifying, or troubleshooting MongoDB, Apache Kafka, and Docker containers for this project.
---

# Infrastructure Management Skill

This skill provides operational workflows to manage local service dependencies (MongoDB, Kafka, Redis) using Docker Compose or Homebrew services.

## Service Ports

| Service       | Default Port | Connection String / URL |
| :------------ | :----------- | :---------------------- |
| **MongoDB**   | `27017`      | `mongodb://localhost:27017/employeedb` |
| **Kafka**     | `9092`       | `localhost:9092` |
| **Redis**     | `6379`       | `localhost:6379` |
| **Spring App**| `8080`       | `http://localhost:8080/api/v1` |

---

## Workflows

### Option A: Docker Compose (Recommended)

Start all services in detached mode:
```bash
docker-compose up -d
```

Check status of containerized services:
```bash
docker-compose ps
```

View live logs for Kafka and MongoDB:
```bash
docker-compose logs -f kafka mongodb
```

Stop and remove containerized services:
```bash
docker-compose down
```

### Option B: macOS Homebrew Services

Start MongoDB and Kafka natively:
```bash
brew services start mongodb-community
brew services start kafka
```

Stop native services:
```bash
brew services stop mongodb-community
brew services stop kafka
```

---

## Verification & Health Check

Execute the service health check helper script:
```bash
./.agents/skills/infrastructure-management/scripts/check_services.sh
```

Or manually probe ports:
```bash
# Check MongoDB (port 27017)
nc -zv localhost 27017

# Check Kafka (port 9092)
nc -zv localhost 9092
```
