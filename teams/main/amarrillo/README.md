# Nexo Criminal — The Red Thread

### Sistema de Inteligencia de Vínculos para Análisis Criminal

> *"Ningún crimen ocurre en el vacío. Detrás de cada delito hay un hilo que lo conecta con otros. Nexo Criminal encuentra ese hilo."*

Proyecto académico desarrollado para la asignatura **Sistemas de Información II** de la **Licenciatura en Informática**, Universidad de Oriente (UDO), Núcleo Nueva Esparta. Desarrollado por el **Equipo Amarillo**.

---

## Descripción general

**Nexo Criminal** es una plataforma de inteligencia criminal que aplica el concepto de *The Red Thread* (El Hilo Rojo) para descubrir vínculos no evidentes entre personas, vehículos, ubicaciones y sucesos delictivos. A diferencia de un registro policial tradicional —que almacena hechos de forma aislada— el sistema cruza coordenadas, tiempos, relaciones sociales y modus operandi para revelar la estructura oculta detrás del crimen.

El sistema se organiza en torno a dos ejes de investigación:

1. **Robo de vehículos** — detección de nodos logísticos, vehículos de apoyo (escoltas) y patrones de modus operandi compartidos entre bandas.
2. **Personas desaparecidas** — reconstrucción del círculo de confianza de la víctima, identificación de intermediarios y detección de puntos de contacto geográficos comunes.

El proyecto se encuentra **desplegado y funcionando en producción**.

---

## Enlaces del sistema en producción

| Recurso | URL |
|---|---|
| Aplicación web (frontend) | https://nexo-criminal-web-final.onrender.com |
| API REST (backend) | https://nexo-criminal-api-final.onrender.com |
| Documentación de la API (Swagger) | https://nexo-criminal-api-final.onrender.com/swagger-ui.html |

Credenciales de demostración: usuario `admin`, contraseña `admin123`.

> Nota: el backend está desplegado en el plan gratuito de Render, por lo que tras un período de inactividad puede tardar unos segundos en responder la primera petición (arranque en frío).

---

## Stack tecnológico

### Backend
- **Java 17** — lenguaje principal.
- **Spring Boot 3.2.5** — framework de aplicación.
- **Spring Data JPA / Hibernate** — persistencia.
- **Spring Security + JWT** — autenticación por token.
- **springdoc-openapi (Swagger UI)** — documentación interactiva de la API.
- **Cloudinary** — almacenamiento persistente de imágenes.
- **Maven** — gestión de dependencias y construcción.

### Frontend
- **TypeScript** — tipado estricto.
- **React 18** — biblioteca de interfaz de usuario.
- **Vite** — bundler y servidor de desarrollo.
- **React Router** — enrutamiento de la SPA.
- **Leaflet / React-Leaflet** — mapas tácticos con coordenadas reales.
- **Cytoscape.js** — visualización del grafo de vínculos.

### Base de datos
- **PostgreSQL** — base de datos relacional.

### Inteligencia artificial
- **Claude (Anthropic)** — análisis de zonas de búsqueda, generación de reportes, clasificación de modus operandi y extracción de datos de testimonios.

### Infraestructura y despliegue
- **Render** — hosting de la base de datos, el backend (servicio Docker) y el frontend (sitio estático).
- **Docker** — contenedor del backend.
- **GitHub / GitHub Actions** — control de versiones e integración continua (pipelines de build, lint, test y despliegue).

---

## Arquitectura

El backend sigue los principios de **Arquitectura Limpia (Clean Architecture)** con un enfoque **hexagonal (puertos y adaptadores)**, organizado por dominios de negocio. Cada dominio separa claramente sus capas:

- **`domain/model`** — modelos de dominio (POJOs puros, sin dependencias de frameworks). Contienen la lógica de negocio.
- **`domain/port`** — puertos: interfaces que definen las operaciones que el dominio necesita (abstracciones).
- **`application`** — casos de uso: una clase por operación (Create, Read, Update, Delete y operaciones específicas).
- **`infrastructure/persistence`** — adaptadores de persistencia: implementan los puertos usando JPA, más los *mappers* entre dominio y entidad.
- **`infrastructure/web`** — controladores REST y DTOs de entrada/salida.

Esta separación garantiza que la lógica de negocio **no dependa de detalles de implementación**: los casos de uso dependen de puertos (interfaces), y son los adaptadores concretos los que dependen del dominio, nunca al revés. Cambiar la base de datos o el framework web no obliga a tocar la lógica de negocio.

### Los sucesos como núcleo del sistema

Un principio de diseño central: **todo hecho delictivo se registra como un Suceso**, no de forma aislada en cada entidad. El módulo `suceso` es el punto único donde se crean los robos, avistamientos, transacciones y desapariciones, vinculando en cada caso las entidades involucradas (vehículo, víctima, ubicación, testigos). Las entidades individuales (como Vehículo) no crean hechos por su cuenta: solo exponen consultas sobre su propio estado —por ejemplo, la lista de vehículos marcados como robados—. Este enfoque respeta el propósito del sistema (es una plataforma de denuncias y reportes, no un catálogo genérico) y mantiene la coherencia de los datos, ya que un vehículo o una persona cobran sentido operativo únicamente cuando están asociados a un suceso.

### Principios y patrones aplicados

- **SOLID** — SRP (servicios orquestadores con una responsabilidad), OCP (fuentes de datos extensibles), LSP (adaptadores sustituibles), ISP (puertos de lectura pequeños y específicos), DIP (dependencia de abstracciones vía puertos).
- **Patrones de diseño** — Adapter (adaptadores de repositorio), Strategy (estrategias de fuentes de datos), Factory (creación de estrategias), Chain of Responsibility (middleware de autenticación y autorización).

### Motor Red Thread

El corazón analítico del sistema. Aplica reglas heurísticas para detectar vínculos ocultos entre entidades:

- **Nodo logístico** — concentración sospechosa de vehículos en una zona y ventana temporal.
- **Escolta vehicular** — vehículos que se mueven en coordinación.
- **Círculo de confianza** — caminos de relación social entre víctima y sospechoso.
- **Modus operandi** — similitud de método entre distintos sucesos.
- **Clúster de desapariciones** — agrupamiento geográfico de casos.

Los vínculos detectados se visualizan como un grafo interactivo (el "hilo rojo") y generan alertas proactivas clasificadas por nivel de riesgo.

---

## Integraciones entre sistemas

Como parte del ecosistema de la cátedra, Nexo Criminal se integra con los sistemas de otros equipos:

- **Equipo Cian (patrullaje)** — al enviar un suceso o desaparición a patrullas, el sistema calcula la prioridad con IA y lo remite como incidente georreferenciado al sistema de despacho.
- **Equipo Blanco (registro)** — consumen la API de personas filtrando por rol (`GET /api/v1/personas?rol=SOSPECHOSO`) para obtener los datos de los sospechosos.
- **Equipo Naranja (testimonios)** — envían la transcripción de un testimonio y el sistema, con IA, extrae los datos estructurados del hecho para su revisión.

---

## Calidad y pruebas

El proyecto incluye **31 tests unitarios** (JUnit 5 + Mockito) sobre los casos de uso de los dominios de persona, vehículo, suceso, desaparecida y alerta. Los tests aprovechan la arquitectura hexagonal: al depender de puertos (interfaces), sustituyen la base de datos por mocks, verificando la lógica de negocio de forma aislada (incluyendo reglas como la unicidad de documento y placa). Se ejecutan con `mvn test`.

---

## Estructura del proyecto

```
nexo-criminal/
├── README.md
├── backend/                         # Spring Boot (Java 17)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/nexocriminal/
│       ├── persona/                 # Dominio Persona (hexagonal)
│       │   ├── application/         #   casos de uso
│       │   ├── domain/model|port/   #   modelo y puertos
│       │   └── infrastructure/      #   web + persistencia
│       ├── vehiculo/                # Dominio Vehículo
│       ├── suceso/                  # Dominio Suceso (núcleo: crea todos los hechos)
│       ├── desaparecida/            # Dominio Desaparecida
│       ├── ubicacion/               # Dominio Ubicación
│       ├── alerta/                  # Dominio Alerta
│       ├── vinculo/                 # Dominio Vínculo
│       ├── engine/                  # Motor Red Thread
│       ├── robo/                    # Registro transaccional de robos
│       ├── ia/                      # Integración con IA (Claude)
│       ├── grafo/                   # Generación del grafo
│       ├── fuentes/                 # Fuentes de datos externas
│       ├── integracion/             # Integraciones con otros equipos (Cian, Naranja)
│       ├── modus/                   # Catálogo de modus operandi
│       ├── middleware/              # Cadena de responsabilidad (auth)
│       ├── config/                  # Configuración (OpenAPI, motor)
│       ├── files/                   # Almacenamiento de archivos (Cloudinary)
│       └── security/                # JWT, autenticación, filtros
│
└── frontend/                        # React + TypeScript + Vite
    ├── package.json
    └── src/
        ├── pages/                   # Vistas (Dashboard, Personas, etc.)
        ├── components/              # Componentes reutilizables
        ├── services/                # Cliente API, utilidades
        └── types/                   # Tipos TypeScript
```

---

## Módulos y funcionalidades

| Módulo | Descripción |
|---|---|
| **Dashboard** | Panel de operaciones con estadísticas y acceso al motor. |
| **Personas** | Gestión de personas, roles, relaciones sociales y búsqueda de intermediarios. |
| **Vehículos** | Registro de vehículos y consulta de su estado (por ejemplo, listado de robados). |
| **Sucesos** | Punto único de registro de robos, avistamientos, transacciones y desapariciones, con edición y mapa. |
| **Desaparecidas** | Casos de desaparición con galería de fotos, prioridad y estado. |
| **Ubicaciones** | Gestión de coordenadas geográficas. |
| **Alertas** | Alertas generadas por el motor Red Thread, con niveles de riesgo. |
| **Asistente IA** | Chat y análisis asistido con IA. |
| **Testimonios** | Procesamiento de transcripciones con IA (integración Naranja). |
| **Grafo** | Visualización interactiva de la red de vínculos. |

---

## Ejecución local

### Requisitos previos
- Java 17 o superior
- Maven
- Node.js 18 o superior
- PostgreSQL

### 1. Base de datos
Crear una base de datos PostgreSQL local llamada `nexo_criminal`. El esquema se genera automáticamente al iniciar el backend (Hibernate `ddl-auto=update`).

### 2. Backend
```bash
cd backend
mvn spring-boot:run
# API disponible en http://localhost:8080
# Swagger en http://localhost:8080/swagger-ui.html
```

Variables de entorno relevantes (con valores por defecto para desarrollo local en `application.properties`):

| Variable | Descripción |
|---|---|
| `DATABASE_URL` | URL JDBC de PostgreSQL |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | Credenciales de la base |
| `JWT_SECRET` | Clave para firmar los tokens JWT |
| `ANTHROPIC_API_KEY` | Clave de la API de IA (opcional) |
| `CLOUDINARY_CLOUD_NAME` / `CLOUDINARY_API_KEY` / `CLOUDINARY_API_SECRET` | Credenciales de Cloudinary para las fotos |
| `CORS_ORIGINS` | Orígenes permitidos para CORS |

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
# UI disponible en http://localhost:5173
```

Variable de entorno del frontend:

| Variable | Descripción |
|---|---|
| `VITE_API_URL` | URL base del backend (vacía en local para usar el proxy de Vite) |

---

## Documentación de la API

La API está documentada con **Swagger / OpenAPI**. Con el backend en ejecución, la interfaz interactiva está disponible en `/swagger-ui.html`. Cada controlador está descrito con `@Tag` y los endpoints principales con `@Operation`. Para probar los endpoints protegidos:

1. Ejecutar `POST /api/v1/auth/login` con las credenciales.
2. Copiar el `token` de la respuesta.
3. Pulsar **Authorize** (candado) e ingresar el token.
4. A partir de ahí, todas las peticiones incluyen el token JWT.

---

## Equipo Amarillo

Asignatura: Sistemas de Información II — Licenciatura en Informática.
Universidad de Oriente (UDO), Núcleo Nueva Esparta.

| Integrante | Rol |
|---|---|
| John Salazar | Líder general / Scrum Master |
| Valeria García | Scrum Master |
| Isaac Carreño | Scrum Master |
| Santiago Ramírez | Scrum Master |
| Manuel Rodríguez | Tech Lead / Scrum Master |

---

## Licencia

Proyecto académico. Uso restringido a fines educativos.
