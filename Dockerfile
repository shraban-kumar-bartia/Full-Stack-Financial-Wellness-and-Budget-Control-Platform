# Stage 1: Build the application JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR from the build stage
COPY --from=build /app/target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar

# Expose Spring Boot’s default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
