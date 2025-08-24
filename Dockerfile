# Stage 1: build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven configuration and source
COPY pom.xml .
COPY src ./src

# Package the JAR (skip tests for speed)
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
