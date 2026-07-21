# 🚔 Backend - Sistema de Gestión de Incidentes en Tiempo Real y Despacho de Patrullas

Proyecto desarrollado para la asignatura **Sistemas de Información II**.

El backend administra la lógica de negocio, la persistencia de datos en Supabase y provee la API REST para la gestión georreferenciada de incidentes, administración de patrullas y asignaciones en tiempo real.

---

**Universidad de Oriente**  
Núcleo Nueva Esparta  
**Período Académico:** I-2026  
**Equipo:** Azul Cian  

## Integrantes

| Integrante | Cédula |
|------------|---------|
| Br. Elia Alfonzo | 32.495.353 |
| Br. Samuel Gil | 31.257.297 |
| Br. Franceli Millán | 31.820.833 |
| Br. Víctor Quijada | 31.257.011 |
| Br. Samuel Rosales | 31.257.129 |

---

# Tabla de Contenidos

- [Descripción del Proyecto](#-backend---sistema-de-gestión-de-incidentes-en-tiempo-real-y-despacho-de-patrullas)
- [Arquitectura General](#arquitectura-general)
- [Características del Backend](#características-del-backend)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura de Software](#arquitectura-de-software)
- [Patrones de Diseño Implementados](#patrones-de-diseño-implementados)
- [Estructura del Backend](#estructura-del-backend)
- [Pruebas Unitarias del Backend](#pruebas-unitarias-del-backend)
- [Requisitos Previos](#requisitos-previos)
- [Instalación del Backend](#instalación-del-backend)
- [Variables de Entorno](#variables-de-entorno)
- [Guía de Uso](#guía-de-uso)
- [Escenario de Uso](#escenario-de-uso)
- [Reglas de Negocio](#reglas-de-negocio)
- [Roles del Sistema](#roles-del-sistema)
- [Flujo Principal del Sistema](#flujo-principal-del-sistema)
- [API REST](#api-rest)
- [Documentación de la API](#documentación-de-la-api)
- [Testing](#testing)
- [Licencia](#licencia)

---

# Arquitectura General

```text
Frontend (React + TypeScript + Vite)
            │
            │ REST API
            ▼
Backend (Spring Boot)
            │
            ▼
Supabase

```

El backend administra toda la lógica de negocio, reglas del dominio y el acceso a la base de datos PostgreSQL/Supabase.

---

# Características del Backend

* Persistencia de incidentes georreferenciados.
* Administración y actualización de estados operacionales de patrullas.
* Lógica de asignación de patrullas disponibles a incidentes activos.
* Cálculo de métricas y estadísticas para el dashboard.
* API REST documentada con Swagger/OpenAPI 3.0.
* Pruebas unitarias automatizadas para reglas de negocio.

---

# Tecnologías Utilizadas

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA)
* **Base de Datos:** PostgreSQL / Supabase
* **Gestor de Dependencias:** Maven
* **Documentación:** OpenAPI 3.0 / Swagger UI
* **Testing:** JUnit 5, Mockito

---

# Arquitectura de Software

El sistema sigue una arquitectura en capas (*Domain-Driven Design*) donde cada componente tiene una responsabilidad específica:

* **Controllers:** Exponen los endpoints de la API REST y reciben solicitudes HTTP.
* **Services:** Contienen la lógica de negocio y orquestan los flujos del sistema.
* **Repositories:** Administran la persistencia y consultas a la base de datos Supabase.
* **Models (Entidades):** Representan los objetos de dominio.
* **DTOs:** Encapsulan los datos transmitidos entre la API y el cliente.

---

# Patrones de Diseño Implementados

| Patrón | Uso |
| --- | --- |
| **Strategy** | Implementado en el módulo de asignaciones (`assignment`) para intercambiar dinámicamente las estrategias de selección y despacho de patrullas (`BasicAssignmentStrategy`). |

---

# Estructura del Backend

```text
com.azulcian.GestionIncidentesPatrullas
│
├── GestionIncidentesPatrullasApplication.java
│
├── config
│   └── SwaggerConfig.java
│
├── incident
│   ├── controller
│   │   └── IncidentController.java
│   ├── dto
│   │   ├── IncidentDetailDTO.java
│   │   └── IncidentSummaryDTO.java
│   ├── model
│   │   ├── Incident.java
│   │   └── IncidentStatus.java
│   ├── repository
│   │   └── IncidentRepository.java
│   └── service
│       └── IncidentService.java
│
├── patrol
│   ├── controller
│   │   └── PatrolController.java
│   ├── model
│   │   ├── Patrol.java
│   │   └── PatrolStatus.java
│   ├── repository
│   │   └── PatrolRepository.java
│   └── service
│       └── PatrolService.java
│
└── assignment
    ├── controller
    │   └── AssignmentController.java
    ├── dto
    │   └── AssignmentRequestDTO.java
    ├── model
    │   └── Assignment.java
    ├── repository
    │   └── AssignmentRepository.java
    ├── service
    │   └── AssignmentService.java
    └── strategy
        ├── AssignmentStrategy.java
        └── BasicAssignmentStrategy.java

```

---

# Pruebas Unitarias del Backend

```text
src
└── test
    └── java
        └── com.azulcian.GestionIncidentesPatrullas
            │
            ├── assignment
            │   ├── service
            │   │   └── AssignmentServiceTest.java
            │   └── strategy
            │       └── BasicAssignmentStrategyTest.java
            │
            ├── incident
            │   └── service
            │       └── IncidentServiceTest.java
            │
            └── patrol
                └── service
                    └── PatrolServiceTest.java

```

Las pruebas unitarias verifican las principales reglas de negocio del sistema utilizando mocks, garantizando la independencia respecto a la base de datos y otros componentes externos.

---

# Requisitos Previos

Antes de ejecutar el backend es necesario contar con:

* **Git**
* **JDK 21** (Java Development Kit)
* **Maven 3.9+** (o el wrapper `./mvnw` incluido)
* Instancia de **PostgreSQL** / **Supabase** activa

---

# Instalación del Backend

1. **Clonar el repositorio y acceder al directorio:**
```bash
git clone <URL_DEL_REPOSITORIO>
cd backend

```


2. **Configurar las variables de entorno o archivo de propiedades.**
3. **Compilar y ejecutar en desarrollo:**
* En Linux/macOS:
```bash
./mvnw spring-boot:run

```


* En Windows (CMD/PowerShell):
```cmd
.\mvnw.cmd spring-boot:run

```





---

# Variables de Entorno

Crear un archivo `.env` en la raíz del backend tomando como base `.env.example`:

```env
DB_URL=jdbc:postgresql://tu-instancia-supabase.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña_secreta

```

Enlazadas en `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

```

---

# Guía de Uso

El backend provee los servicios requeridos para el operador del despacho:

1. Registrar patrullas en la base de datos.
2. Recibir registros de incidentes georreferenciados.
3. Procesar las asignaciones evaluando patrullas disponibles.
4. Actualizar estados según el ciclo de vida del incidente.
5. Calcular totales para la vista del dashboard.

---

# Escenario de Uso

```text
Solicitud HTTP de registro de incidente (ACTIVE)
            │
            ▼
Persistencia en Supabase via Repository
            │
            ▼
Solicitud HTTP de asignación de patrulla
            │
            ▼
Ejecución de BasicAssignmentStrategy
            │
            ▼
Actualización: Incidente -> IN_PROGRESS / Patrulla -> BUSY
            │
            ▼
Cierre de incidente -> Patrulla pasa a AVAILABLE

```

---

# Reglas de Negocio

* Todo incidente inicia con estado `ACTIVE`.
* Solo las patrullas `AVAILABLE` pueden asignarse.
* Un incidente solo admite una asignación activa.
* Al asignar una patrulla el incidente cambia automáticamente a `IN_PROGRESS`.
* Al cerrar el incidente la patrulla vuelve automáticamente a `AVAILABLE`.

---

# Roles del Sistema

| Rol | Responsabilidades |
| --- | --- |
| **Centro de Mando** | Registra incidentes, administra patrullas, realiza asignaciones operativas, supervisa el estado de las operaciones y gestiona el cierre de los incidentes. |
| **Oficial de Patrulla** | Atiende los incidentes asignados, actualiza el estado operativo de la patrulla y reporta la finalización de la atención. |
| **Supervisor** | Monitorea los incidentes y patrullas en tiempo real, consulta el estado de las operaciones y supervisa la información mostrada en el mapa para apoyar la toma de decisiones. |

---

# Flujo Principal del Sistema

1. Recibir y almacenar el incidente registrado.
2. Guardar coordenadas geográficas del suceso.
3. Almacenar el registro de patrullas operativas.
4. Validar disponibilidad de patrulla al solicitar asignación.
5. Transicionar estados de entidad según el ciclo del despacho.
6. Liberar la patrulla en base de datos al concluir la atención.

---

# API REST

El backend expone una API REST documentada mediante Swagger.

Entre las principales operaciones se encuentran:

* Crear incidentes.
* Consultar incidentes.
* Obtener estadísticas.
* Registrar patrullas.
* Asignar patrullas.
* Actualizar estados.
* Cerrar incidentes.

---

# Documentación de la API

El backend expone una documentación interactiva mediante Swagger.

Una vez iniciado el backend puede accederse desde:

🔗 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Swagger permite:

* Consultar todos los endpoints.
* Visualizar los modelos utilizados.
* Probar las operaciones del sistema.
* Ver las respuestas esperadas de cada servicio.

---

# Testing

El backend incorpora pruebas unitarias desarrolladas con:

* **JUnit 5**
* **Mockito**

Para ejecutar la suite completa de pruebas:

```bash
./mvnw test

```

---

# Licencia

Proyecto académico desarrollado para la asignatura **Sistemas de Información II** - Universidad de Oriente.
