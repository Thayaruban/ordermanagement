# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar file from the target folder to the container
COPY target/ordermanagement-0.0.1-SNAPSHOT.jar app.jar


# Expose the port your application will run on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
