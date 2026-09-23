# Stage 1: Build the application
FROM gradle:8.7-jdk17 AS builder
WORKDIR /workspace/app

# Copy gradle wrapper and configuration files
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./

# Copy source code
COPY src src

# Make gradlew executable and build the jar (skip tests for faster image builds)
RUN chmod +x gradlew && ./gradlew build -x test

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create a non-root user and group for security
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

# Copy the built jar from the builder stage
COPY --from=builder /workspace/app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]