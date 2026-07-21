# Prison Service (Backend - Equipo Blanco)

Este es el microservicio principal encargado de la gestión interna penitenciaria, desarrollado con Spring Boot.

---

## Instalación Desde Cero

Si ya tienes instalado alguna herramienta, omite el paso.

### 1. Instalar Java 21

**Eclipse Temurin:**
```bash
# Descargar desde: https://adoptium.net/es/temurin/releases/?version=21
# Ejecutar el .msi y seguir los pasos

# Verificar instalación:
java -version
# Debería mostrar: openjdk version "21.0.x"
```

### 2. Instalar Git

```bash
# Descargar desde: https://git-scm.com/download/win
# Ejecutar el .exe y seguir los pasos (opciones por defecto)

# Verificar:
git --version
```

### 3. Configurar Variable de Entorno JAVA_HOME

```bash
# Windows (PowerShell como Administrador):
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Amazon Corretto\jdk21.0.x_xx', 'Machine')
[System.Environment]::SetEnvironmentVariable('PATH', "$env:JAVA_HOME\bin;$env:PATH", 'Machine')

# Cerrar y reabrir terminal. Verificar:
echo $env:JAVA_HOME
java -version
```

### 4. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/AICSIUDONE.git
cd AICSIUDONE/teams/EquipoBlanco/backend/prision-service
```

### 5. Configurar Variables de Entorno

El archivo `.env` ya está incluido en el proyecto con las credenciales de Supabase. Si necesitas crear el tuyo:

```bash
# Copiar el archivo de ejemplo y renombrarlo:
cp .env.example .env
# Editar .env con tus credenciales de Supabase
```

**Nota:** La base de datos PostgreSQL ya está configurada en Supabase (nube), no requiere instalación local.

### 6. Ejecutar el Backend

Maven Wrapper descargará automáticamente Maven y las dependencias:

```bash
# Windows:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```

La aplicación se levantará en `http://localhost:8081`.

### 7. Verificar que Funciona

- Health check: `http://localhost:8081/actuator/health`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Framework | Spring Boot 3.2.4 |
| Lenguaje | Java 21 |
| Build Tool | Maven (Wrapper) |
| Base de Datos | PostgreSQL (Supabase) |
| ORM | Spring Data JPA / Hibernate |
| Seguridad | Spring Security + JWT (cabeceras X-User-Name, X-User-Role) |
| Documentación API | SpringDoc OpenAPI (Swagger UI) |
| Validación | Spring Boot Validation |
| Service Discovery | Spring Cloud Netflix Eureka |
| Monitoreo | Spring Boot Actuator |
| Utilidades | Lombok, Spring Dotenv |
| Tests | JUnit + Mockito |

---

## Estructura del Proyecto

```
prision-service/
├── pom.xml                          # Configuración Maven (Spring Boot 3.2.4, Java 21)
├── .env                             # Variables de entorno (credenciales BD PostgreSQL)
├── .env.example                     # Ejemplo de variables de entorno
├── mvnw / mvnw.cmd                  # Maven Wrapper (Linux / Windows)
│
└── src/
    ├── main/
    │   ├── java/equipoBlanco/com/prison_service/
    │   │   ├── PrisonServiceApplication.java    # Punto de entrada de la aplicación
    │   │   │
    │   │   ├── common/                          # Componentes transversales compartidos
    │   │   │   ├── config/
    │   │   │   │   └── OpenApiConfig.java       # Configuración de Swagger/OpenAPI
    │   │   │   ├── security/
    │   │   │   │   ├── JwtAuthFilter.java       # Filtro JWT (cabeceras X-User-Name, X-User-Role)
    │   │   │   │   └── SecurityConfig.java      # Configuración de Spring Security
    │   │   │   ├── GlobalExceptionHandler.java  # Manejador global de excepciones (@ControllerAdvice)
    │   │   │   └── DatabaseConstraintUpdater.java # Utilidad para actualizar constraints en BD
    │   │   │
    │   │   └── modules/
    │   │       ├── cells/                       # Gestión de celdas y mapa penitenciario
    │   │       │   ├── controller/              # Endpoints REST (CellController, PrisonMapController)
    │   │       │   ├── dto/                     # DTOs: CellDto, CellPositionDto, PrisonMapDto, MapWithPositionsDto
    │   │       │   ├── model/                   # Entidades: Cell, CellAssignment, CellPosition, PrisonMap
    │   │       │   ├── repository/              # Acceso a datos (JpaRepository)
    │   │       │   └── service/                 # Lógica de negocio (CellService, CellPositionService, PrisonMapService)
    │   │       │
    │   │       ├── control/                     # Monitoreo y sistema de alertas de seguridad
    │   │       │   ├── model/                   # Entidad: Alerta
    │   │       │   └── repository/              # Acceso a datos (AlertaRepository)
    │   │       │
    │   │       ├── inmates/                     # Registro de reclusos, traslados, incidentes y salidas
    │   │       │   ├── controller/              # Endpoints: InmateController, DeathReportController, TransferRequestController
    │   │       │   ├── dto/                     # DTOs: InmateDto, TransferRequestDto, DeathReportDto, TemporaryEgressDto, etc.
    │   │       │   ├── model/                   # Entidades: Inmate, Belonging, DeathReport, TransferRequest, InternalIncident, etc.
    │   │       │   ├── repository/              # Acceso a datos (7 repositorios)
    │   │       │   └── service/                 # Lógica de negocio (InmateService, DeathReportService, TransferRequestService, BelongingService)
    │   │       │
    │   │       └── postpenal/                   # Seguimiento post-penal, oficiales de carga y calendarios
    │   │           ├── controller/              # Endpoints: PostPenalController, CalendarioController
    │   │           ├── dto/                     # DTOs: ExpedienteDto, OficialCargaDto, CalendarioDto, CumplimientoDto, etc.
    │   │           ├── model/                   # Entidades: ExpedienteSeguimiento, CalendarioPresentacion
    │   │           ├── repository/              # Acceso a datos (2 repositorios)
    │   │           └── service/                 # Lógica de negocio (PostPenalService, CalendarioService)
    │   │
    │   └── resources/
    │       ├── application.yml                  # Configuración principal de Spring Boot
    │       └── application-local.yml            # Configuración para entorno local
    │
    └── test/
        └── java/equipoBlanco/com/prison_service/
            └── modules/
                ├── cells/service/CellServiceTest.java       # Tests de CellService
                ├── control/model/AlertaModelTest.java       # Tests del modelo Alerta
                ├── inmates/service/InmateServiceTest.java   # Tests de InmateService
                └── postpenal/service/PostPenalServiceTest.java # Tests de PostPenalService
```

---

## Pipelines Implementados (CI/CD)

El proyecto cuenta con integración continua configurada a través de GitHub Actions.

### Ejecución automática
Se ejecuta automáticamente ante cada evento `push` o `pull_request` hacia las ramas `main` y `develop` (siempre que existan modificaciones dentro de `teams/EquipoBlanco/backend/prision-service/**`).

### Ejecución manual desde GitHub UI
Ir al repositorio en GitHub → **Actions** → **"Equipo Blanco - Prision Service CI"** → **Run workflow** → seleccionar rama → **Run workflow**.

### Ejecución manual desde terminal (CLI)
Requiere [GitHub CLI](https://cli.github.com/) instalada y autenticada (`gh auth login`):
```bash
gh workflow run equipoblanco-prision-service.yml --ref main
```

### ¿Qué hace el pipeline?
1. Prepara un entorno Linux con Java 21
2. Ejecuta las pruebas unitarias con Maven (`.\mvnw.cmd test`)
3. Compila y empaqueta el código (`.\mvnw.cmd package`)
4. Sube el artefacto `.jar` generado

---

## Tests Implementados

Se han desarrollado pruebas unitarias garantizando un **aislamiento total (100%)** utilizando la librería Mockito, evitando que interactúen con la base de datos real y siguiendo rigurosamente el patrón **AAA (Arrange, Act, Assert)**.

### Módulos probados
- `cells` (`CellServiceTest`)
- `inmates` (`InmateServiceTest`)
- `postpenal` (`PostPenalServiceTest`)
- `control` (`AlertaModelTest`)

### Ejecutar todos los tests
```bash
# Desde la carpeta prision-service:
.\mvnw.cmd test
```

### Ejecutar un test específico
```bash
.\mvnw.cmd test -Dtest=CellServiceTest
```

## Regla de Organización de Módulos y Cómo Agregar un Nuevo Endpoint

El proyecto separa estrictamente la lógica de negocio por dominios. Para agregar un nuevo endpoint de manera limpia, sigue estas reglas:
1. **Identifica el Módulo:** Ubícate en el paquete correcto (ej. `inmates/controller`).
2. **Crea/Modifica el Controlador:** Añade tu clase o método y anótalo correctamente (`@RestController` y `@RequestMapping`, o `@GetMapping`, etc.).
3. **Inyecta el Servicio:** Usa la anotación `@RequiredArgsConstructor` (Lombok) para inyectar dependencias del servicio. Ningún controlador debe tocar un repositorio.
4. **Utiliza DTOs:** *Regla de oro:* Nunca expongas ni recibas entidades JPA (`@Entity`) directamente. Realiza mapeos utilizando objetos DTO creados en la carpeta `dto`.
5. **Documenta:** Incorpora las anotaciones de Swagger (ej. `@Operation(summary = "...", description = "...")`) en el nuevo método.

## Swagger y Documentación de la API

La API cuenta con documentación interactiva y estandarizada gracias a **springdoc-openapi**.
- **Implementación en el código:** Toda la configuración general se encuentra centralizada en la clase `OpenApiConfig.java` (paquete `common/config`). Adicionalmente, nuestro filtro de seguridad (`JwtAuthFilter.java`) y el `SecurityConfig.java` incluyen reglas de exclusión explícitas que permiten el tráfico libre (sin token) a los endpoints de Swagger.
- **Cómo ver la documentación:** Levanta tu servidor Spring Boot localmente y abre tu navegador web en la siguiente dirección: 
  `http://localhost:8081/swagger-ui.html`

## Roles del Sistema y Flujo de Autenticación

El microservicio está diseñado para trabajar bajo una arquitectura basada en API Gateway que resuelve el JWT externamente.
- **Flujo:** La autenticación y asimilación de roles se realiza a través de la clase `JwtAuthFilter.java` (un `OncePerRequestFilter`).
- **Funcionamiento:** En cada petición (exceptuando Swagger), el filtro intercepta la llamada y verifica que existan las cabeceras HTTP `X-User-Name` y `X-User-Role`.
- Si existen, el filtro formatea el rol anteponiendo la palabra clave `ROLE_` y deposita un token de autenticación configurado (`UsernamePasswordAuthenticationToken`) dentro del **SecurityContextHolder**.
- Gracias a este flujo, los endpoints pueden restringirse nativamente en Spring con anotaciones sencillas como `@PreAuthorize("hasRole('ROLE_DIRECTOR')")`.

## Implementaciones que siguen el Patrón SOLID

1. **Single Responsibility Principle (SRP - Responsabilidad Única):** Está presente en toda la arquitectura dividida. Los *Controllers* únicamente gestionan entradas y salidas HTTP, los *Services* concentran exclusivamente las lógicas y reglas de negocio complejas, y los *Repositories* solo proveen acceso a los datos de la base de datos.
2. **Open/Closed Principle (OCP - Abierto/Cerrado):** Al usar interfaces que extienden de `JpaRepository`, permitimos que Spring nos provea la implementación y somos capaces de agregar nuevos métodos de búsqueda customizados sin alterar ni sobrescribir el código nativo subyacente.
3. **Dependency Inversion Principle (DIP - Inversión de Dependencia):** Nuestros servicios dependen de abstracciones (interfaces Repository) inyectadas por el contenedor de Inversión de Control de Spring (mediante inyección de dependencias en constructores), y no de clases concretas instanciadas con `new`.

## Patrones de Diseño Implementados en Nuestro Código

En nuestro backend hemos implementado:

### Patrones Creacionales
- **Builder (Constructor):** Utilizado de forma intensiva a través de todo el proyecto mediante la etiqueta `@Builder` de Lombok, tanto en Entidades JPA como en los DTOs. Esto nos ha permitido flexibilizar la creación de objetos evitando constructores gigantes y anidados (Ej: `CellDto.builder().identifier("A1").build()`).
- **Singleton (Instancia única):** Implementado transparente y nativamente por Spring Boot; cada componente anotado con `@Service`, `@RestController` y `@Repository` existe en memoria como una única instancia para toda la aplicación.

### Patrones Estructurales
- **Facade (Fachada):** Este patrón es visible en las clases de la capa Service (como `PostPenalService.java` o `CellService.java`). Esta capa actúa como una fachada que envuelve interacciones sumamente complejas que involucran llamadas a múltiples repositorios (`InmateRepository`, `AlertaRepository`, etc.), dándole al controlador métodos limpios y fáciles de consumir sin que deba conocer dicha complejidad.

### Patrones de Comportamiento
- **Strategy (Estrategia):** A nivel interno, el ecosistema de Spring Security implementa profundamente el patrón estrategia utilizando interfaces (como para la resolución de roles y `SecurityContext`). De igual forma, las distintas anotaciones y clases de configuración proveen a Spring estrategias dinámicas sobre cómo ejecutar o manejar las políticas de seguridad en cada petición filtrada.
