# syntax=docker/dockerfile:1
# escape=`


# Stage 1: Build
# 3.9 updated to 3.9.11 which caused build issues, now using 3.9.10
FROM maven:3.9.10 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests


# Stage 2: Production
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
