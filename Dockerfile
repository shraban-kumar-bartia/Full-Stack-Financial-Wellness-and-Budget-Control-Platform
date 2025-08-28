# -------- Stage 1: Build the app --------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (caching layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar (skip tests for deployment)
RUN mvn clean package -DskipTests

# -------- Stage 2: Run the app --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar without hardcoding version
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot’s default port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
