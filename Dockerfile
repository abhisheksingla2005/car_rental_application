# ============================================
# Multi-stage Dockerfile for Spring Boot App
# ============================================

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom files first (cache dependencies layer)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY booking-service/pom.xml booking-service/pom.xml
COPY car-service/pom.xml car-service/pom.xml

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src
COPY booking-service/src booking-service/src
COPY car-service/src car-service/src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy entrypoint script
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# Expose the port (Render will set PORT env var)
EXPOSE 8081

# Run with the entrypoint script
ENTRYPOINT ["./entrypoint.sh"]
