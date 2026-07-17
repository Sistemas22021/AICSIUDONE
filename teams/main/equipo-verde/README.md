# Sistema Balístico - Equipo Verde 🔫🔍

Bienvenido al repositorio del **Módulo de Análisis Forense Holístico (Equipo Verde)**. Este sistema es responsable de la ingesta, estandarización, almacenamiento, y correlación de evidencia balística (balas y casquillos) utilizando algoritmos avanzados para la comparación de imágenes y la generación de hashes (SHA-256) para garantizar la trazabilidad y unicidad de la evidencia.

## 🚀 Tecnologías Utilizadas

### Backend
- **Java 21** con **Spring Boot 3**
- **Arquitectura:** Arquitectura Limpia (Hexagonal), aplicando principios SOLID.
- **Base de Datos:** PostgreSQL
- **Almacenamiento de Archivos:** Amazon S3 (emulado localmente a través de LocalStack)
- **Documentación API:** Swagger (OpenAPI)

### Frontend
- **React 18** con **TypeScript**
- **Build Tool:** Vite
- **Estilos:** Tailwind CSS / Material UI

## 📋 Requisitos Previos
- Docker y Docker Desktop
- Java 21 y Maven
- Node.js y pnpm (`npm install -g pnpm`)

## 🛠️ Cómo Levantar el Proyecto Localmente

1. **Levantar Infraestructura (Base de Datos y S3)**
   Ubicado en `backend/balistic-services`:
   ```bash
   docker-compose up -d
   ```

2. **Ejecutar el Backend**
   Ubicado en `backend/balistic-services`:
   ```bash
   mvn spring-boot:run
   ```
   *La documentación interactiva de Swagger UI estará disponible en:* `http://localhost:8080/swagger-ui.html`

3. **Ejecutar el Frontend**
   Ubicado en `frontend/equipo-verde-web`:
   ```bash
   pnpm install
   pnpm run dev
   ```
   *La interfaz web estará disponible en:* `http://localhost:3002/`

---

## 📊 Arquitectura y Diagramas del Sistema

A continuación se presentan los diagramas de modelado que describen el diseño arquitectónico y el flujo de la lógica de negocio.

### Caso de Uso General
![Caso de Uso General](docs/caso%20de%20uso%20general.jpeg)

### Diagrama de Clases
![Diagrama de Clases](docs/diagrama%20de%20clases%20S2.jpeg)

### Diagrama de Secuencia
![Diagrama de Secuencia](docs/diagrama%20de%20secuencia%20.jpeg)

### Diagrama de Estado
![Diagrama de Estado](docs/diagrama%20de%20estado.jpeg)

### Casos de Uso por Módulo

#### Módulo de Ingesta y Estandarización
![Ingesta y Estandarización](docs/caso%20de%20uso%20mod%20ingesta%20y%20estanr.jpg)

#### Módulo de Correlación y Custodia
![Correlación y Cadena de Custodia](docs/caso%20de%20uso%20correlacion%20y%20custodia%20balis.jpg)

#### Módulo del Motor de Correlación
![Motor de Correlación](docs/caso%20de%20uso%20mod%20motor%20correlacion.jpg)

#### Módulo de Trazabilidad y Auditoría
![Trazabilidad y Auditoría](docs/casos%20de%20uso%20mod%20trazab%20y%20auditoria.jpg)
