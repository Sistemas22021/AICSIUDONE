FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Copia cualquier .jar que genere Maven en la carpeta target
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Control de memoria para evitar OOM en los 512 MB de Render
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]