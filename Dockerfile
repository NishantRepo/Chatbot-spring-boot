# Use OpenJDK image
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run jar
ENTRYPOINT ["java","-jar","app.jar"]



## java-app/Dockerfile
## Multi-stage Dockerfile for the Spring Boot application.
## This results in a small, production-ready image.
#
## Stage 1: Build the application
#FROM eclipse-temurin:17-jdk-jammy as builder
#
#WORKDIR /app
#
## Copy the Maven project files.
#COPY pom.xml .
#COPY src ./src
#
## Build the JAR file.
#RUN apt-get update && apt-get install -y maven
#RUN mvn clean package -DskipTests
#
## Stage 2: Create the final, lightweight image
#FROM eclipse-temurin:17-jre-jammy
#
## Set the working directory.
#WORKDIR /app
#
## Copy the built JAR from the builder stage.
#COPY --from=builder /app/target/*.jar app.jar
#
## Expose the port defined in application.properties.
#EXPOSE 9091
#
## Set the command to run the application.
#ENTRYPOINT ["java", "-jar", "app.jar"]
