FROM eclipse-temurin:21-jre-alpine

VOLUME /tmp
COPY target/tao-digital-interview-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]