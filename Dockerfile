FROM openjdk:17-jdk-slim

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests
FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libxrender1 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app

COPY --from=0 /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]