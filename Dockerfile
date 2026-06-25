FROM eclipse-temurin:21-jre
ARG JAR_FILE=app.jar
WORKDIR /app
COPY jars/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
