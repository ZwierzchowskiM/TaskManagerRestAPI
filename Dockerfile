FROM openjdk:17-alpine
COPY target/taskmanager-0.0.1-SNAPSHOT.jar taskmanager.jar
ENTRYPOINT ["java", "-jar", "taskmanager.jar"]

