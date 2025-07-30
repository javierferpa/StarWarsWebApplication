# I’m using the lightweight Temurin JRE 17 image to keep the final layer small.
FROM eclipse-temurin:17-jre-alpine

# Everything in the jar sits under /app so it’s easy to locate during troubleshooting.
WORKDIR /app

# Copy the Spring Boot fat-jar produced by Maven.
COPY target/*.jar app.jar

# Expose the internal port; Docker-Compose maps it to the host automatically.
EXPOSE 8080

# Run the jar. A simple ENTRYPOINT keeps the container one-process-per-PID-1.
ENTRYPOINT ["java","-jar","/app/app.jar"]