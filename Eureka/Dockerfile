FROM openjdk:11-jre-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} eureka.jar

EXPOSE 8761

## RUN addgroup -S app && adduser -S app -G app
## USER app

ENTRYPOINT ["java","-jar","/eureka.jar"]