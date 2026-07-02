# Backend — Sistema Integral de Gestión Penitenciaria (SIGP)

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.2.4 |
| Base de datos | PostgreSQL (Supabase) |
| ORM | Spring Data JPA (Hibernate) |
| Seguridad | Spring Security + Custom Filter (JwtAuthFilter) |
| Service Discovery | Eureka Client (Spring Cloud 2023.0.1) |
| Build | Maven (Wrapper) |
| Gestión de dependencias | Lombok, spring-dotenv |

---

## CI/CD — Pipeline

### ¿Qué es CI/CD?

**CI (Continuous Integration)**: cada vez que subes código a GitHub, el servidor de GitHub Actions descarga tu repo, compila, ejecuta tests y verifica que todo funciona. Si algo falla, te avisa.

**CD (Continuous Deployment)**: después de pasar los tests, el código se despliega automáticamente a un entorno de producción.

En este proyecto:
- **Backend**: solo tiene CI (test + build sin deploy)
- **Frontend**: tiene CI/CD completo (test + build + deploy a Vercel)

### ¿Cómo funciona?

#### 1. Los archivos del pipeline

Los pipelines son archivos YAML dentro de `.github/workflows/`:

| Pipeline | Archivo | Servicio |
|---|---|---|
| Backend | `.github/workflows/equipoblanco-prision-service.yml` | `prision-service` |
| Frontend | `.github/workflows/equipoblanco-prison-web.yml` | `prison-web` |

Cada archivo define:
- **Cuándo** se activa (eventos + ramas + rutas)
- **Qué** hacer (jobs con pasos)
- **Dónde** ejecutarse (ubuntu-latest)

#### 2. ¿Cuándo se activa?

| Evento | Ramas | ¿Cuándo? |
|---|---|---|
| `push` (subir código) | `main`, `develop` | Cambios en la carpeta del servicio |
| `pull_request` | `main`, `develop` | Cambios en la carpeta del servicio |
| `workflow_dispatch` | Cualquiera | Manual desde GitHub.com |

> **Importante**: si haces push a `main` pero cambias archivos de `main/`, NO se activa el pipeline de EquipoBlanco. Solo reacciona a cambios en `teams/EquipoBlanco/...`.

#### 3. ¿Qué hace exactamente?

**Backend** (solo CI, sin deploy):

```
Push → GitHub Actions →
  1. Test: mvn test -B (Java 21, Maven, caché)
  2. Build: mvn package -DskipTests -B → genera JAR → sube como artifact
  [Se detiene aquí — NO hay deploy]
```

**Frontend** (CI/CD completo con deploy):

```
Push → GitHub Actions →
  1. Test: npm ci → npm test (Node 20, Vitest)
  2. Build & Deploy:
     a. npm ci → npm run build
     b. Vercel deploy:
        - Si es develop → deploy preview
        - Si es main → deploy producción
```

### Cómo hacer funcionar los pipelines (paso a paso)

#### Requisitos iniciales

1. El repositorio debe estar en GitHub
2. Los archivos `.yml` deben estar en `.github/workflows/` (ya están creados)
3. Configurar los Secrets en GitHub

#### Configurar Secrets del Frontend (para deploy a Vercel)

El pipeline del frontend necesita autenticarse en Vercel. Ve a:

```
GitHub → Settings → Secrets and variables → Actions → New repository secret
```

Agrega estos 4 secrets:

| Secret | Valor | Dónde obtenerlo |
|---|---|---|
| `VERCEL_TOKEN` | `vcp_...` | https://vercel.com/account/tokens → Create token |
| `VERCEL_ORG_ID` | ID del team | https://vercel.com → Dashboard → Settings → General → Team ID |
| `VERCEL_PROJECT_ID_PRISON_WEB` | ID del proyecto | https://vercel.com → proyecto → Settings → General → Project ID |
| `VITE_API_URL` | URL del backend | Ej: `https://e31a2aa6b01992.lhr.life` (túnel) o URL de producción |

#### Probar los pipelines

**Opción A: Automático (al hacer push)**

```bash
# Desde tu terminal local:
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE

# Hacer cambios en backend
# (ej: modificar un archivo Java)
git add .
git commit -m "fix: corregir validación"
git push origin develop

# Automáticamente GitHub Actions ejecuta el pipeline del backend
```

**Opción B: Manual (desde GitHub)**

```
1. Ir a https://github.com/TU_USER/AICSIUDONE/actions
2. Seleccionar el workflow (ej: "[Equipo Blanco][Backend] Prision Service")
3. Click "Run workflow"
4. Seleccionar rama (develop o main)
5. Click "Run workflow"
```

#### Ver resultados

1. Ir a GitHub → Actions
2. Ver el workflow en ejecución
3. Click en el workflow → ver logs de cada job

- ✅ Verde = pasó
- ❌ Rojo = falló (click para ver el error)

### Códigos completos para terminal

#### Para activar el pipeline (subir cambios a GitHub)

```bash
# 1. Ir al repo
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE

# 2. Ver qué cambió
git status

# 3. Agregar cambios
git add .

# 4. Commit
git commit -m "descripción del cambio"

# 5. Subir a develop (activa el pipeline)
git push origin develop

# 6. Cuando todo esté probado, subir a main (activa pipeline + deploy)
git push origin main
```

#### Para deploy manual del frontend a Vercel (sin pipeline)

```bash
# 1. Ir al frontend
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\frontend\prison-web

# 2. Configurar variable de entorno en Vercel (si cambió la URL del túnel)
vercel env rm VITE_API_GATEWAY_URL production
vercel env add VITE_API_GATEWAY_URL production
# Pegar la URL (ej: https://XXXX.lhr.life)

# 3. Construir y desplegar
vercel build --prod && vercel --prod --prebuilt --yes
```

#### Para exponeer backend local con túnel (cuando trabajas con Vercel)

```bash
# Terminal 1 - Iniciar backend
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\backend\prision-service
.\mvnw.cmd spring-boot:run

# Terminal 2 - Iniciar túnel (después de que el backend esté arriba)
ssh -R 80:localhost:8081 nokey@localhost.run
# Te da una URL: https://XXXX.lhr.life

# Terminal 3 (o la misma) - Actualizar Vercel con la nueva URL
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\frontend\prison-web
vercel env rm VITE_API_GATEWAY_URL production
vercel env add VITE_API_GATEWAY_URL production
vercel build --prod && vercel --prod --prebuilt --yes
```

> **Nota**: Cada vez que reinicias el túnel, la URL cambia. Debes actualizar la variable en Vercel y redeployear.

### Resumen del flujo completo

```
Desarrollo local:
  [Backend] .\mvnw.cmd spring-boot:run  →  http://localhost:8081
  [Frontend] npm run dev                →  http://localhost:5173

Cuando subes cambios a GitHub:
  git push origin develop
    → GitHub Actions ejecuta:
       Backend:  Test → Build (JAR)
       Frontend: Test → Build → Deploy a Vercel (preview)

Cuando haces merge a main:
  git push origin main
    → GitHub Actions ejecuta:
       Backend:  Test → Build (JAR)
       Frontend: Test → Build → Deploy a Vercel (producción)

Para que Vercel hable con tu backend local:
  Terminal 1: .\mvnw.cmd spring-boot:run
  Terminal 2: ssh -R 80:localhost:8081 nokey@localhost.run
  Terminal 3: vercel build --prod && vercel --prod --prebuilt --yes
```

---

## Estructura de Carpetas

```
prision-service/
├── src/main/java/equipoBlanco/com/prison_service/
│   ├── PrisonServiceApplication.java                  # Entry point
│   ├── common/
│   │   ├── GlobalExceptionHandler.java                 # Mapeo de excepciones a HTTP status
│   │   └── security/
│   │       ├── SecurityConfig.java                     # Configuración de endpoints por rol
│   │       └── JwtAuthFilter.java                      # Filtro que lee headers de autenticación
│   └── modules/
│       ├── inmates/                                    # Módulo de reclusos
│       ├── cells/                                      # Módulo de celdas y mapa
│       ├── postpenal/                                  # Módulo post-penitenciario
│       └── control/                                    # Módulo de control y alertas
├── src/main/resources/
│   ├── application.yml                                 # Configuración general (puerto 8081, BD, JPA, multipart)
│   └── application-local.yml                           # Override local (credenciales BD hardcodeadas)
└── src/test/java/equipoBlanco/com/prison_service/
    └── PrisonServiceApplicationTests.java
```

### Reglas de organización de módulos

1. Cada **módulo funcional** vive en `modules/<nombre>/`
2. Un módulo **SIEMPRE** tiene: `controller/`, `dto/`, `model/`, `repository/`, `service/`
3. Solo los módulos completos (con CRUD) tienen todas las capas; módulos incipientes pueden tener solo `model/` y `repository/` (ej: `control/`)
4. El paquete `common/` contiene código compartido (excepciones, seguridad, configuración)
5. Todo endpoint debe pertenecer a un módulo — no hay controllers sueltos fuera de `modules/`

### Cómo agregar un endpoint nuevo

1. Ubicar el módulo correspondiente o crear uno nuevo siguiendo la estructura `modules/<nombre>/`
2. Crear el endpoint en el controller existente o crear un nuevo `@RestController` con `@RequestMapping("/api/v1/...")`
3. Registrar el permiso en `SecurityConfig.java` con `requestMatchers(HttpMethod.X, "/api/v1/...").hasRole("ROL")`
4. Si el módulo es nuevo, definir:
   - `model/Entidad.java` — clase con `@Entity`
   - `dto/EntidadDto.java` — clase con `@Getter @Setter @Builder`
   - `repository/EntidadRepository.java` — interfaz que extiende `JpaRepository`
   - `service/EntidadService.java` — clase con `@Service`
   - `controller/EntidadController.java` — clase con `@RestController`

---

## Control de Acceso por Roles

### Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| `Administrador del Sistema` | Configura infraestructura (celdas, planos) |
| `Oficial Penitenciario` | Gestión diaria del penal (ingresos, egresos, mapa) |
| `Supervisor Penitenciario` | Supervisión del penal (aprobación de traslados) |
| `Oficial de Seguimiento` | Seguimiento post-penitenciario (presentaciones, calendario) |
| `Supervisor Policial` | Supervisión post-penitenciaria (asignación de oficiales, alertas) |

### Cómo funciona

El frontend envía dos headers HTTP en cada petición:

```
X-User-Name: "Carlos Méndez"
X-User-Role: "Oficial Penitenciario"
```

**Flujo de autenticación/autorización:**

1. `JwtAuthFilter` intercepta cada request
2. Lee los headers `X-User-Name` y `X-User-Role`
3. Convierte el rol al formato Spring Security (`ROLE_OFICIAL_PENITENCIARIO` — espacios reemplazados por guiones bajos)
4. Crea un `UsernamePasswordAuthenticationToken` y lo inyecta en `SecurityContextHolder`
5. `SecurityConfig` verifica que el rol tenga permiso para el endpoint solicitado
6. Si no tiene permiso → `403 Forbidden`
7. Si tiene permiso → el request pasa al controller normalmente

### Mapa de permisos (endpoints vs roles)

| Endpoint | Método | Roles Permitidos |
|----------|--------|------------------|
| `/api/v1/cells` | POST | Administrador del Sistema |
| `/api/v1/cells/**` | PUT | Administrador del Sistema |
| `/api/v1/cells/**` | DELETE | Administrador del Sistema |
| `/api/v1/cells/*/position` | DELETE | Administrador del Sistema |
| `/api/v1/cells/*/assign/**` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/cells/**` | GET | Oficial Penitenciario, Administrador del Sistema, Supervisor Penitenciario |
| `/api/v1/maps/**` | POST | Administrador del Sistema |
| `/api/v1/maps/**` | PUT | Administrador del Sistema |
| `/api/v1/maps/**` | DELETE | Administrador del Sistema |
| `/api/v1/maps/**` | GET | Oficial Penitenciario, Administrador del Sistema, Supervisor Penitenciario |
| `/api/v1/inmates` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/inmates/*/discharge` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/inmates/**` | GET | Oficial Penitenciario, Administrador del Sistema, Supervisor Penitenciario |
| `/api/v1/transfers` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/transfers/*/resolve` | PUT | Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/transfers/**` | GET | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/post-penal/expedientes/*/assign` | POST | Supervisor Policial, Administrador del Sistema |
| `/api/v1/post-penal/**` | GET | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/api/v1/post-penal/**` | POST | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/api/v1/post-penal/**` | PUT | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/actuator/**` | GET | Permitido a todos (health, info) |

Los permisos se definen en `common/security/SecurityConfig.java`.

### Cómo agregar un rol nuevo

1. Agregar el string del rol en `SecurityConfig.java` en los `hasRole(...)` correspondientes
2. Agregar el rol a la lista `MOCK_USERS` en el frontend (`authContext.tsx` y `AuthGuard.tsx`)
3. Sincronizar el rol en `ProtectedRoute` y en los filtros de `SidebarLayout`

---

## Desarrollo local

```bash
# Requisitos: Java 21, Maven

# Compilar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Ejecutar servidor local (puerto 8081 por defecto)
./mvnw spring-boot:run
```

El archivo `.env` en la raíz contiene las credenciales de base de datos para desarrollo local (no se sube al repo).

---

## Buenas Prácticas

- **No exponer IDs secuenciales**: usar `UUID` como PK (ya configurado vía `GenerationType.UUID`)
- **Auditoría**: todo endpoint que modifique datos debe recibir `X-User-Name` y registrarlo
- **Manejo de errores**: lanzar `RuntimeException` con mensajes descriptivos → `GlobalExceptionHandler` mapea por contenido del mensaje:
  - Contiene "Ya existe" → `409 CONFLICT`
  - Contiene "no encontrado" / "no encontrada" → `404 NOT FOUND`
  - Contiene "llena" → `400 BAD_REQUEST`
  - Contiene "ya tiene celda" → `409 CONFLICT`
  - Otros → `500 INTERNAL_SERVER_ERROR`
- **DTOs**: nunca exponer entidades JPA directamente en los endpoints, siempre mapear a DTOs
- **Validación**: usar anotaciones `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Min`, etc.) en los DTOs
- **Seguridad**: no hardcodear contraseñas — usar variables de entorno (`.env`) o `application-local.yml` en `.gitignore`
- **CORS**: configuración permisiva (orígenes `*`, métodos estándar, headers expuestos `X-User-Name` y `X-User-Role`)
- **Multipart**: soporte para subida de archivos de hasta 50MB (imágenes de reclusos, huellas)

---

## Patrones de diseño implementados

### 1. **DTO (Data Transfer Object)**
Cada capa de presentación se comunica con el servicio mediante objetos DTO (`InmateDto`, `CellDto`, `ExpedienteDto`, etc.) para evitar exponer las entidades JPA directamente.

### 2. **Builder Pattern**
Gracias a Lombok `@Builder`, las entidades y DTOs se construyen con el patrón Builder (ej: `Inmate.builder().firstName(...).build()`).

### 3. **Repository Pattern**
Spring Data JPA abstrae el acceso a datos mediante interfaces que extienden `JpaRepository<T, UUID>`, encapsulando la lógica de persistencia.

### 4. **Service Layer**
Cada módulo tiene una capa de servicio (`InmateService`, `CellService`, `PostPenalService`) que contiene la lógica de negocio, separada de los controllers.

### 5. **Dependency Injection (DI)**
Inyección de dependencias por constructor usando `@RequiredArgsConstructor` de Lombok en todos los servicios y controllers.

### 6. **Filter Chain (Chain of Responsibility)**
`JwtAuthFilter` extiende `OncePerRequestFilter` y se inserta en la cadena de filtros de Spring Security mediante `addFilterBefore(...)`, interceptando cada request para autenticar.

### 7. **Strategy Pattern (Roles)**
Spring Security `authorizeHttpRequests` define permisos por endpoint usando `hasRole`/`hasAnyRole`, aplicando una estrategia de autorización según el rol autenticado.

### 8. **Template Method (JPA)**
`JpaRepository` proporciona métodos CRUD predefinidos (`findAll`, `findById`, `save`, `delete`) que actúan como plantilla, y los repositorios agregan consultas personalizadas.

### 9. **Global Exception Handler (Controller Advice)**
`GlobalExceptionHandler` con `@ControllerAdvice` centraliza el manejo de excepciones y mapea mensajes de error a códigos HTTP específicos.

### 10. **Modular Architecture**
Separación del código en módulos funcionales (`inmates`, `cells`, `postpenal`, `control`) con responsabilidades bien delimitadas, facilitando el mantenimiento y escalabilidad.

### 11. **Embedded Value (JPA Embeddables)**
Uso de relaciones `@OneToMany` y `@ManyToOne` entre entidades para modelar agregaciones (ej: `Inmate` → `InmatePhoto`, `Inmate` → `Belonging`).

### 12. **Enumeration Pattern**
 Estados finitos modelados como enums de Java:
 - `Inmate.InmateStatus`: `ACTIVO_SIN_CELDA`, `ACTIVO_CON_CELDA`, `EGRESADO`
 - `TransferRequest.TransferStatus`: `PENDIENTE`, `APROBADO`, `RECHAZADO`
