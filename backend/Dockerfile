# STAGE 1: BUILD
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*
COPY pom.xml ./
COPY src ./src
COPY mvnw ./
COPY .mvn ./.mvn
RUN chmod +x mvnw
RUN mvn clean package -DskipTests

# STAGE 2: RUNTIME
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]