FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/wordguess-game-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]