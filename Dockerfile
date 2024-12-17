# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven-built jar file into the container at /app
COPY target/passwordrecovery-service-0.0.1-SNAPSHOT.jar /app/passwordrecovery-service.jar

# Expose the port that the application will run on
EXPOSE 8091

# Run the Spring Boot application
CMD ["java", "-jar", "passwordrecovery-service.jar"]
