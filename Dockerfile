FROM openjdk:17-jdk-slim
ARG JAR_FILE=targetJar/*.jar
COPY ${JAR_FILE} app_asistio.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app_asistio.jar" ]