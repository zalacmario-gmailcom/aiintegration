# Use an official Gradle image with Java 21 to build the project
FROM gradle:8.7-jdk21 AS builder

# Set the working directory inside the build container
WORKDIR /workspace

# Copy the entire project into the container
COPY . /workspace

# Build the project using Gradle
# This command builds the JAR file and runs tests
RUN gradle clean build -x test

# Use a lightweight JRE image for running the application
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the runtime container
WORKDIR /app

# Copy the built JAR file from the builder stage
# Spring Boot Gradle builds output to: build/libs/<app-name>-<version>.jar
COPY --from=builder /workspace/build/libs/aiintegration-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the Spring Boot application listens on
# (Default Spring Boot port is 8080)
EXPOSE 8080

# Health check to verify the container is running correctly
HEALTHCHECK --interval=30s --timeout=10s --start-period=20s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the Spring Boot application
# Environment variables like OPENAI_API_KEY should be passed when running the container
# Example: docker run -e OPENAI_API_KEY=sk-... my-spring-app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
