# Multi-stage build: Maven build stage + JRE runtime stage
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory for Maven build
WORKDIR /app

# Copy Maven wrapper and pom.xml for dependency resolution
COPY mvnw* ./
COPY .mvn .mvn
COPY pom.xml ./

# Download dependencies (cached if pom.xml unchanged)
RUN ./mvnw dependency:resolve

# Copy source code and build application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Runtime stage - lightweight JRE image
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy the Spring Boot JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
