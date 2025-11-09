# syntax=docker/dockerfile:1

# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Cache dependencies by copying only the project descriptor first
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# script de arranque
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# Expose the default Spring Boot port
EXPOSE 8080

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

ENTRYPOINT ["/app/start.sh"]
