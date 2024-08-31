# Dockerfile-postgres
FROM amazoncorretto:18.0.2
WORKDIR /app
ARG JAR_FILE=*.jar
COPY pepegaVpnManager-1.0-SNAPSHOT.jar /app/pepegaVpnManager.jar
EXPOSE 8800
ENTRYPOINT ["java", "-jar", "pepegaVpnManager.jar"]