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
