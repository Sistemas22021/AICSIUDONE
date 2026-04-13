# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile — Auth Service (y aplicable a Eureka, Config y Gateway)
#
# Multi-stage build:
#   - Stage 1 (builder): Compila con Maven. Imagen: eclipse-temurin:17
#   - Stage 2 (runtime): Solo el JAR. Imagen: eclipse-temurin:17-jre-alpine (más pequeña)
#
# Resultado: imagen ~180MB vs ~600MB sin multi-stage
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copiar solo el POM primero para aprovechar el cache de capas de Docker.
# Si solo cambia el código (no las dependencias), Maven no re-descarga nada.
COPY pom.xml .
RUN apk add --no-cache maven && mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde el stage de build
COPY --from=builder /build/target/*.jar app.jar

# Puerto por defecto — sobreescribir con variable de entorno si es necesario
EXPOSE 8080

# JVM flags optimizados para contenedores con poca memoria (t2.micro: 1GB)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=70.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
