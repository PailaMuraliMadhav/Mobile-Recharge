FROM eclipse-temurin:21-jdk
WORKDIR /app

# Use a wildcard (*) so it finds the Eureka JAR regardless of the exact version name
COPY target/*.jar app.jar

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]