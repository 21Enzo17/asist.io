# Etapa de construcción
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el pom.xml y los archivos de código
COPY pom.xml .
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Etapa final (solo para ejecutar el JAR)
FROM eclipse-temurin:17-jdk-jammy AS runtime
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Comando para ejecutar el JAR
CMD ["java", "-jar", "app.jar"]