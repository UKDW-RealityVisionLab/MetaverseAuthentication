# Use OpenJDK as base image
#build the apps
FROM maven:3.8.5-openjdk-17-slim AS builder
COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package -DskipTests

# Command to run the application
#FROM openjdk:17-jdk-slim
FROM openjdk:17-jdk-alpine
COPY --from=builder /app/target/*.jar /app/msg-server.jar
# Expose the port used by the application
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/msg-server.jar"]