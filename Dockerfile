FROM openjdk:8-jre-slim

WORKDIR /usr/local/runtime
COPY target/springbootserver-0.0.1-SNAPSHOT.jar webapp.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "webapp.jar"]