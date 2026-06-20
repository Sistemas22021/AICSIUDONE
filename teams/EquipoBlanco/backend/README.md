# Backend — Sistema Integral de Gestión Penitenciaria (SIGP)

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.2.4 |
| Base de datos | PostgreSQL (Supabase) |
| ORM | Spring Data JPA (Hibernate) |
| Seguridad | Spring Security + Custom Filter |
| Service Discovery | Eureka Client (Spring Cloud 2023.0.1) |
| Build | Maven (Wrapper) |

---

## Estructura de Carpetas

```
prision-service/
├── src/main/java/equipoBlanco/com/prison_service/
│   ├── PrisonServiceApplication.java          # Entry point
│   ├── common/
│   │   ├── GlobalExceptionHandler.java        # Mapeo de excepciones a HTTP status
│   │   └── security/
│   │       ├── SecurityConfig.java            # Configuración de endpoints por rol
│   │       └── JwtAuthFilter.java             # Filtro que lee headers de autenticación
│   └── modules/
│       ├── cells/                             # Módulo de celdas y mapa
│       │   ├── controller/                    # REST controllers
│       │   ├── dto/                           # Data Transfer Objects
│       │   ├── model/                         # Entidades JPA
│       │   ├── repository/                    # Repositorios Spring Data
│       │   └── service/                       # Lógica de negocio
│       ├── inmates/                           # Módulo de reclusos
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       ├── postpenal/                         # Módulo post-penitenciario
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       └── control/                           # Módulo de control y alertas
│           ├── model/
│           └── repository/
├── src/main/resources/
│   ├── application.yml                        # Configuración general
│   └── application-local.yml                  # Override local (credenciales BD)
├── pom.xml
└── .env                                       # Variables de entorno (no se sube a git)
```

### Reglas de organización de módulos

1. Cada **módulo funcional** vive en `modules/<nombre>/`
2. Un módulo **SIEMPRE** tiene: `controller/`, `dto/`, `model/`, `repository/`, `service/`
3. Solo los módulos completos (con CRUD) tienen todas las capas; módulos incipientes pueden tener solo `model/` y `repository/`
4. El paquete `common/` contiene código compartido (excepciones, seguridad, configuración)
5. Todo endpoint debe pertenecer a un módulo — no hay controllers sueltos fuera de `modules/`

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
3. Convierte el rol al formato Spring Security (`ROLE_OFICIAL_PENITENCIARIO`)
4. Crea un `UsernamePasswordAuthenticationToken` y lo inyecta en `SecurityContextHolder`
5. `SecurityConfig` verifica que el rol tenga permiso para el endpoint solicitado
6. Si no tiene permiso → `403 Forbidden`
7. Si tiene permiso → el request pasa al controller normalmente

### Mapa de permisos (endpoints vs roles)

| Endpoint | Método | Roles Permitidos |
|----------|--------|------------------|
| `/api/v1/cells` | POST | Administrador del Sistema |
| `/api/v1/cells` | PUT | Administrador del Sistema |
| `/api/v1/cells/**` | DELETE | Administrador del Sistema |
| `/api/v1/cells/*/assign/**` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/cells/**` | GET | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/maps/**` | PUT/DELETE | Administrador del Sistema |
| `/api/v1/maps/**` | GET | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/inmates` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/inmates/*/discharge` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/inmates/**` | GET | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/transfers` | POST | Oficial Penitenciario, Administrador del Sistema |
| `/api/v1/transfers/*/resolve` | PUT | Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/transfers/**` | GET | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/api/v1/post-penal/expedientes/*/assign` | POST | Supervisor Policial, Administrador del Sistema |
| `/api/v1/post-penal/**` | GET | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/api/v1/post-penal/**` | POST/PUT | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |

Los permisos se definen en `common/security/SecurityConfig.java`.

---

## Cómo agregar un endpoint nuevo

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

## Cómo agregar un rol nuevo

1. Agregar el string del rol en `SecurityConfig.java` en los `hasRole(...)` correspondientes
2. Agregar el rol a la lista `MOCK_USERS` en el frontend (`authContext.tsx` y `AuthGuard.tsx`)
3. Sincronizar el rol en `ProtectedRoute` y en los filtros de `SidebarLayout`

---

## Buenas Prácticas

- **No exponer IDs secuenciales**: usar `UUID` como PK (ya configurado vía `GenerationType.UUID`)
- **Auditoría**: todo endpoint que modifique datos debe recibir `X-User-Name` y registrarlo
- **Manejo de errores**: lanzar `RuntimeException` con mensajes descriptivos → `GlobalExceptionHandler` mapea automáticamente al HTTP status correcto
- **DTOs**: nunca exponer entidades JPA directamente en los endpoints, siempre mapear a DTOs
- **Validación**: usar anotaciones `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Min`, etc.) en los DTOs
- **Seguridad**: no hardcodear contraseñas — usar variables de entorno (`.env`) o `application-local.yml` en `.gitignore`
