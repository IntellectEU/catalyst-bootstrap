FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./catalyst-generator/target/catalyst-generator*SNAPSHOT.jar /opt/catalyst-generator.jar
COPY ./catalyst-web/target/catalyst-web*SNAPSHOT.jar /opt/catalyst-web.jar
COPY ./catalyst-service/target/catalyst-service.jar /opt/catalyst-service.jar
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/catalyst-service.jar"]
