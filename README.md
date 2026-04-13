# SSO Boilerplate Centralizado

> Ecosistema de microservicios y micro-frontends con Single Sign-On centralizado.
> Diseñado como base educativa para equipos universitarios.

[![Backend: Java 17](https://img.shields.io/badge/Backend-Java%2017-orange)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![Frontend: React 18](https://img.shields.io/badge/Frontend-React%2018-blue)](https://react.dev)
[![Infrastructure: Terragrunt](https://img.shields.io/badge/IaC-Terragrunt-purple)](https://terragrunt.gruntwork.io)
[![CI/CD: GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-black)](https://github.com/features/actions)

---

## 📋 Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Requisitos Previos](#requisitos-previos)
3. [Inicio Rápido (Local)](#inicio-rápido-local)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Flujo de Autenticación SSO](#flujo-de-autenticación-sso)
6. [Cómo Agregar un Nuevo Micro-frontend](#cómo-agregar-un-nuevo-micro-frontend)
7. [Cómo Agregar un Nuevo Microservicio](#cómo-agregar-un-nuevo-microservicio)
8. [Despliegue en AWS](#despliegue-en-aws)
9. [Variables de Entorno](#variables-de-entorno)
10. [Testing](#testing)
11. [Metodología: Kent Beck TDD](#metodología-kent-beck-tdd)

---

## Arquitectura General

```
                    ┌─────────────────────────────────────┐
                    │           CLIENTE / BROWSER          │
                    │                                      │
                    │  ┌──────────────┐  ┌─────────────┐  │
                    │  │  Login MFE   │  │ Consumer App│  │
                    │  │  :3000       │  │  :3001      │  │
                    │  └──────┬───────┘  └──────┬──────┘  │
                    └─────────┼─────────────────┼─────────┘
                              │                 │
                              ▼                 ▼
                    ┌─────────────────────────────────────┐
                    │          API Gateway  :8090          │
                    │     (Spring Cloud Gateway)           │
                    │     Valida JWT en cada request       │
                    └──────────────┬──────────────────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              ▼                    ▼                    ▼
  ┌───────────────────┐  ┌──────────────────┐  ┌──── ─────────────┐
  │   Auth Service    │  │   (futuro)       │  │   (futuro)       │
  │      :8080        │  │  Products Svc    │  │  Orders Svc      │
  │  Hexagonal Arch   │  │  Equipo Alpha    │  │  Equipo Beta     │
  └────────┬──────────┘  └──────────────────┘  └──────────────────┘
           │
           ▼
  ┌──────────────────┐     ┌──────────────────┐
  │   PostgreSQL     │     │  Eureka Server   │
  │ (Neon/Supabase)  │     │     :8761        │
  └──────────────────┘     └──────────────────┘
                                    ▲
                           ┌────────┴──────────┐
                           │   Config Server   │
                           │     :8888         │
                           │  (Lee config-repo)│
                           └───────────────────┘
```

---

## Requisitos Previos

| Herramienta | Versión Mínima | Instalación |
|---|---|---|
| Docker Desktop | 24.x | https://www.docker.com/get-started |
| Java JDK | 17 | https://adoptium.net |
| Maven | 3.9 | https://maven.apache.org |
| Node.js | 20 LTS | https://nodejs.org |
| Git | 2.40 | https://git-scm.com |

**Para desarrollo de infraestructura:**
| Herramienta | Versión |
|---|---|
| Terraform | 1.7.x |
| Terragrunt | 0.55.x |
| AWS CLI | 2.x |

---

## Inicio Rápido (Local)

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-org/sso-boilerplate.git
cd sso-boilerplate
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con tus credenciales de Neon/Supabase y JWT secret
```

**Variables mínimas requeridas en `.env`:**

```env
# Base de datos (Neon o Supabase)
DATASOURCE_URL=jdbc:postgresql://your-host/your-db?sslmode=require
DATASOURCE_USERNAME=your_user
DATASOURCE_PASSWORD=your_password

# JWT — usar un string de al menos 32 caracteres, aleatorio
JWT_SECRET=cambia-este-valor-por-algo-aleatorio-de-32-chars-minimo
```

> 💡 **Cómo obtener una BD gratis:**
> - [Neon](https://neon.tech): Crea un proyecto → copia la "Connection string" en `DATASOURCE_URL`
> - [Supabase](https://supabase.com): Settings → Database → Connection string (modo "JDBC")

### 3. Levantar la infraestructura común

```bash
# Construye y levanta: Eureka, Config Server, Auth Service, API Gateway
docker compose -f docker/docker-compose.common.yml up --build -d

# Ver logs de todos los servicios
docker compose -f docker/docker-compose.common.yml logs -f
```

### 4. Levantar el proyecto de prueba (Login MFE + Consumer App)

```bash
# En otra terminal: levanta todo (common + consumer)
docker compose \
  -f docker/docker-compose.common.yml \
  -f docker/docker-compose.consumer.yml \
  up --build
```

### 5. Acceder a los servicios

| Servicio | URL |
|---|---|
| **Login MFE** | http://localhost:3000 |
| **Consumer App** | http://localhost:3001 |
| **Swagger — Auth Service** | http://localhost:8080/swagger-ui.html |
| **Eureka Dashboard** | http://localhost:8761 |
| **API Gateway** | http://localhost:8090 |

### 6. Probar el flujo SSO

1. Abre http://localhost:3001 (Consumer App)
2. Como no hay sesión, te redirige a http://localhost:3000 (Login MFE)
3. Registra un usuario en Swagger primero: http://localhost:8080/swagger-ui.html
4. Inicia sesión en el Login MFE
5. Eres redirigido de vuelta a la Consumer App con la página de Bienvenida

---

## Estructura del Proyecto

```
sso-boilerplate/
├── .github/workflows/       ← 6 pipelines independientes (1 por proyecto)
├── teams/
│   └── main/                ← Equipo main (tu equipo)
│       ├── backend/
│       │   ├── auth-service/         ← Microservicio core (Hexagonal)
│       │   ├── api-gateway/          ← Punto de entrada único
│       │   ├── eureka-server/        ← Service Discovery
│       │   └── config-server/        ← Configuración centralizada
│       └── frontend/
│           ├── login-mfe/            ← Micro-frontend de login
│           └── consumer-app/         ← App de prueba
├── config-repo/             ← YAMLs de configuración (leídos por Config Server)
├── docker/                  ← Docker Compose por responsabilidad
├── infrastructure/
│   ├── terraform/modules/   ← Módulos Terraform reutilizables
│   └── terragrunt/          ← Configuración por ambiente (dev/prod)
└── docs/adr/                ← Decisiones de arquitectura documentadas
```

---

## Flujo de Autenticación SSO

```
Consumer App          Login MFE             Auth Service        API Gateway
     │                    │                      │                   │
     │ 1. Detecta sin token│                      │                   │
     │────────────────────>│                      │                   │
     │   ?redirect=<url>  │                      │                   │
     │                    │ 2. POST /auth/login   │                   │
     │                    │──────────────────────>│                   │
     │                    │ 3. {accessToken}      │                   │
     │                    │<──────────────────────│                   │
     │                    │   Set-Cookie: refresh_token (HttpOnly)    │
     │ 4. redirect?token=xxx                      │                   │
     │<───────────────────│                      │                   │
     │ 5. Guarda token en memoria                 │                   │
     │ 6. GET /api/protected    Authorization: Bearer <token>         │
     │─────────────────────────────────────────────────────────────>  │
     │                    │                      │  7. Valida JWT     │
     │                    │                      │<───────────────────│
     │                    │                      │  8. 200 OK + data  │
     │<──────────────────────────────────────────────────────────────│
```

---

## Cómo Agregar un Nuevo Micro-frontend

> **Ejemplo:** Equipo Alpha quiere agregar un dashboard de productos.

### 1. Crear la carpeta del equipo

```bash
mkdir -p teams/team-alpha/frontend/products-dashboard/src
```

### 2. Copiar el Dockerfile del consumer-app como base

```bash
cp teams/main/frontend/consumer-app/Dockerfile teams/team-alpha/frontend/products-dashboard/
```

### 3. Usar el AuthGuard

```tsx
// En tu App.tsx, envuelve tus rutas con AuthGuard
import { AuthGuard } from './guards/AuthGuard';

// AuthGuard hace automáticamente:
// 1. Resuelve el token (memoria → URL param → silent refresh)
// 2. Si no hay sesión → redirige al Login MFE centralizado
```

### 4. Agregar un Docker Compose para tu equipo

```yaml
# docker/docker-compose.team-alpha.yml
include:
  - docker-compose.common.yml  # Reutiliza toda la infraestructura común

services:
  products-dashboard:
    build: ../teams/team-alpha/frontend/products-dashboard
    ports:
      - "3002:80"
    environment:
      - VITE_API_GATEWAY_URL=http://localhost:8090
      - VITE_LOGIN_MFE_URL=http://localhost:3000
    networks:
      - sso-network
```

### 5. Agregar su pipeline (`.github/workflows/team-alpha-products-dashboard.yml`)

Usar `main-login-mfe.yml` como plantilla, cambiando el `paths:` filter.

---

## Cómo Agregar un Nuevo Microservicio

> **Ejemplo:** Equipo Alpha quiere agregar un Products Service.

### 1. Registrarse en Eureka

Agregar al `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 2. Registrar la ruta en el Gateway

```java
// En GatewayConfig.java del api-gateway, agregar:
.route("team-alpha-products", r -> r
        .path("/api/v1/products/**")
        .filters(f -> f.filter(jwtAuthFilter)) // ← El JWT ya viene validado
        .uri("lb://PRODUCTS-SERVICE"))          // ← Nombre en Eureka
```

### 3. Leer el usuario desde el header

El Gateway agrega `X-User-Name` en cada petición autenticada:
```java
@GetMapping("/my-products")
public List<Product> getMyProducts(
        @RequestHeader("X-User-Name") String username) {
    return productService.getByUser(username);
}
```

---

## Despliegue en AWS

### Prerequisitos AWS

1. Cuenta AWS con Free Tier activo
2. Crear manualmente el bucket S3 y tabla DynamoDB para el estado de Terraform:
   ```bash
   aws s3 mb s3://sso-terraform-state-dev
   aws dynamodb create-table \
     --table-name sso-terraform-locks-dev \
     --attribute-definitions AttributeName=LockID,AttributeType=S \
     --key-schema AttributeName=LockID,KeyType=HASH \
     --billing-mode PAY_PER_REQUEST
   ```

### Deploy infraestructura

```bash
cd infrastructure/terragrunt/dev

# Plan (muestra qué se va a crear)
terragrunt run-all plan

# Apply (crea los recursos)
terragrunt run-all apply
```

> ⚠️ **Costo**: El EC2 t2.micro solo consume horas cuando está corriendo.
> Para mantener el costo en $0, apagar la instancia cuando no se usa:
> ```bash
> aws ec2 stop-instances --instance-ids <instance-id>
> ```

### Secretos en GitHub

Configurar en `Settings → Secrets and variables → Actions`:

| Secret | Descripción |
|---|---|
| `AWS_ACCESS_KEY_ID` | IAM Access Key |
| `AWS_SECRET_ACCESS_KEY` | IAM Secret Key |
| `DATASOURCE_URL` | URL de Neon/Supabase |
| `DATASOURCE_USERNAME` | Usuario de la BD |
| `DATASOURCE_PASSWORD` | Contraseña de la BD |
| `JWT_SECRET` | Mínimo 32 caracteres aleatorios |
| `DEPLOY_TARGET` | `s3` o `vercel` |

---

## Variables de Entorno

Ver `.env.example` para la lista completa con descripciones.

---

## Testing

### Backend — Ejecutar todos los tests

```bash
cd teams/main/backend/auth-service
mvn test
```

### Backend — Por capa

```bash
# Solo tests unitarios (sin Spring, ultra rápidos)
mvn test -Dtest="RegisterUserServiceTest,LoginServiceTest"

# Solo tests de persistencia (@DataJpaTest con H2)
mvn test -Dtest="UserRepositoryAdapterTest"

# Solo tests de controllers (@WebMvcTest)
mvn test -Dtest="AuthControllerTest"
```

### Frontend — Tests

```bash
cd teams/main/frontend/login-mfe
npm test

cd teams/main/frontend/consumer-app
npm test
```

---

## Metodología: Kent Beck TDD

Este proyecto sigue el ciclo **Red → Green → Refactor**:

1. **RED** 🔴: Escribir un test que falle ANTES de implementar
2. **GREEN** 🟢: Implementar el mínimo código para pasar el test
3. **REFACTOR** 🔵: Mejorar la estructura sin romper tests

### Tidy First

Los commits están separados según el tipo de cambio:
- `[structural]` Renombrar, extraer método, mover código
- `[behavioral]` Agregar o modificar funcionalidad

**Ejemplo de flujo TDD en el Auth Service:**
```
1. Escribir RegisterUserServiceTest.shouldThrowExceptionWhenUsernameAlreadyExists() → FALLA
2. Implementar la verificación en RegisterUserService → PASA
3. Extraer la condición a un método `isUsernameTaken()` → REFACTOR (sin cambiar tests)
4. Commit: "[structural] Extraer isUsernameTaken en RegisterUserService"
5. Escribir el siguiente test...
```

---

## Documentación Adicional

- 📄 [ADR-001: Arquitectura Hexagonal](docs/adr/ADR-001-hexagonal-architecture.md)
- 📄 [ADR-002: Estrategia JWT Híbrida](docs/adr/ADR-002-jwt-hybrid-token-strategy.md)
- 📄 [Diagrama del Flujo SSO](docs/diagrams/auth-flow.md)

---

*SSO Boilerplate — Proyecto educativo de código abierto. MIT License.*
