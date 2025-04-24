FROM maven:3.8.3-openjdk-17-slim AS build

  # Set the working directory in the container
WORKDIR /app

  # Copy the pom.xml and install dependencies (to make sure dependencies are cached)
COPY pom.xml .

  # Download dependencies (without building the full app)
RUN mvn dependency:go-offline

  # Copy the source code into the container
COPY src /app/src

  # Package the application (this will run `mvn clean install`)
RUN mvn clean package -DskipTests

  # Use an OpenJDK image to run the app
FROM openjdk:17-jdk-slim

  # Set the working directory in the container
WORKDIR /app

  # Copy the built JAR from the build stage
COPY --from=build /app/target/subscriptionservice-0.0.1-SNAPSHOT.jar app.jar

  # Expose the port your app runs on
EXPOSE 8080

  # Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

