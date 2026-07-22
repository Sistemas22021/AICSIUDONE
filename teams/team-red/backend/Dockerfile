FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY guardia-core/pom.xml .
RUN mvn dependency:go-offline -B

COPY guardia-core/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /app/target/guardia-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]