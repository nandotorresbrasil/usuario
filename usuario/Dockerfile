FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Substituido para build o target para gradle
COPY build/libs/usuario-0.0.1-SNAPSHOT.jar /app/usuario.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/usuario.jar"]