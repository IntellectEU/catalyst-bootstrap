FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/*.jar /opt/app.jar
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app.jar"]
