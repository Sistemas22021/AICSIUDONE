FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src/ src/
RUN mvn clean package -DskipTests

# Etapa final: usar una imagen JRE más ligera
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el jar generado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
