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

# Expose the default Spring Boot port
EXPOSE 8080

# Provide a default location for Oracle Wallets mounted as volumes
ENV TNS_ADMIN=/oracle/wallet

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
