FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/medicalimageanalyser-version-1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]