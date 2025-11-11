# syntax=docker/dockerfile:1

# Build stage: compile the Spring Boot application with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the project files and download dependencies
COPY pom.xml .
COPY src ./src
COPY oracle ./oracle

RUN mvn -B -DskipTests clean package

# Runtime stage: lightweight JRE image to run the packaged application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the generated jar and required runtime assets
COPY --from=builder /app/target/*.jar /app/app.jar
COPY --from=builder /app/oracle /oracle

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
