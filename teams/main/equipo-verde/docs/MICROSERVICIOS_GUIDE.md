# Guía de Estándares e Integración de Microservicios en el Ecosistema SSO

Esta guía define las reglas y lineamientos arquitectónicos oficiales del proyecto para conectar nuevos módulos con el sistema centralizado de identidad (Login MFE, API Gateway y Eureka).

---

## Cómo Agregar un Nuevo Micro-frontend

> **Ejemplo:** Equipo Alpha quiere agregar un dashboard de productos (`products-dashboard`).

### 1. Crear la carpeta del equipo

```bash
mkdir -p teams/team-alpha/frontend/products-dashboard/src
```

### 2. Copiar el Dockerfile del consumer-app como base

```bash
cp teams/main/frontend/consumer-app/Dockerfile teams/team-alpha/frontend/products-dashboard/
```

### 3. Usar el AuthGuard

En tu archivo `App.tsx`, envuelve el enrutador o rutas principales con el componente `AuthGuard`:

```tsx
import { AuthGuard } from './guards/AuthGuard';

// AuthGuard hace automáticamente:
// 1. Resuelve el token (memoria → URL param → silent refresh)
// 2. Si no hay sesión → redirige al Login MFE centralizado
export default function App() {
  return (
    <AuthGuard>
      <MisRutas />
    </AuthGuard>
  );
}
```

### 4. Agregar un Docker Compose para tu equipo

Crear un archivo específico de tu equipo que extienda la infraestructura común:

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

### 5. Agregar su pipeline

En la carpeta `.github/workflows/team-alpha-products-dashboard.yml`:
Usar `main-login-mfe.yml` como plantilla, cambiando exclusivamente el filtro de `paths:`.

---

## Cómo Agregar un Nuevo Microservicio

> **Ejemplo:** Equipo Alpha quiere agregar un `Products Service`.

### 1. Registrarse en Eureka

Agregar la dependencia de descubrimiento al archivo `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Y en tu `application.properties`:
```properties
spring.application.name=products-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### 2. Registrar la ruta en el API Gateway

En `GatewayConfig.java` (dentro del microservicio `api-gateway` en `teams/main/backend/api-gateway`), agregar tu bloque de enrutamiento y filtro JWT:

```java
// En GatewayConfig.java del api-gateway, agregar:
.route("team-alpha-products", r -> r
        .path("/api/v1/products/**")
        .filters(f -> f.filter(jwtAuthFilter)) // ← El JWT ya viene validado
        .uri("lb://PRODUCTS-SERVICE"))          // ← Nombre en Eureka
```

### 3. Leer el usuario desde el header

El API Gateway valida el token JWT e inyecta automáticamente el encabezado HTTP `X-User-Name` en cada petición autenticada que se encamina hacia los microservicios:

```java
@GetMapping("/my-products")
public List<Product> getMyProducts(
        @RequestHeader("X-User-Name") String username) {
    return productService.getByUser(username);
}
```

> **NOTA SOBRE ROLES LOCALES:**
> Si tu microservicio necesita validación de roles específicos (por ejemplo, en Equipo Verde `PERITO_BALISTICO`), implementa una **Cadena de Responsabilidad (Chain of Responsibility)** mediante interceptores que validen la existencia del usuario (`X-User-Name` o JWT) como primer eslabón, y luego consulten una tabla local en la base de datos de tu servicio para verificar si el usuario tiene el rol y permisos para el endpoint invocado como segundo eslabón.
