<div align="center">

# 🔴 Nexo Criminal

### Sistema de Inteligencia de Precisión

Plataforma web de inteligencia criminal que descubre vínculos no evidentes entre personas, vehículos, ubicaciones y sucesos mediante un motor de reglas heurísticas y asistencia de IA generativa.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-61DAFB?style=flat-square&logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6?style=flat-square&logo=typescript)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Claude](https://img.shields.io/badge/Claude-Sonnet_4.5-8B5CF6?style=flat-square)](https://www.anthropic.com/)
[![License](https://img.shields.io/badge/License-Academic-blue?style=flat-square)](#)

**Universidad de Oriente — Núcleo Nueva Esparta**
Materia: Sistemas de Información II — Prof. Alejandro Marcano

[Características](#-características-principales) ·
[Demo](#-escenario-de-demo) ·
[Arquitectura](#-arquitectura) ·
[Instalación](#-instalación-y-configuración) ·
[Documentación](#-documentación) ·
[Equipo](#-equipo-de-desarrollo)

</div>

---

## 📖 Sobre el proyecto

**Nexo Criminal** es un sistema diseñado para apoyar a las unidades policiales de Latinoamérica en la correlación automatizada de información operativa. El sistema permite registrar entidades del dominio criminal (personas, vehículos, ubicaciones, sucesos, casos de desaparición) y descubre vínculos no evidentes entre ellas mediante un motor de reglas heurísticas denominado **Red Thread Engine**.

El proyecto se complementa con un asistente conversacional basado en **Anthropic Claude** que permite consultas en lenguaje natural sobre el estado del sistema, generación de reportes ejecutivos, predicción de zonas de búsqueda para personas desaparecidas y detección de patrones entre casos.

> ⚠️ **Aviso académico**: este es un proyecto educativo desarrollado para la materia *Sistemas de Información II*. No es un producto comercial ni está conectado a sistemas reales de inteligencia policial.

---

## ✨ Características principales

### 🎯 Motor Red Thread (5 reglas heurísticas)

| Regla | Detecta |
|---|---|
| **Nodo Logístico** | Talleres o galpones donde convergen vehículos robados |
| **Vehículo Escolta** | Vehículos que acompañan repetidamente a otros robados |
| **Círculo de Confianza** | Intermediarios entre víctimas y sospechosos |
| **Modus Operandi** | Sucesos con métodos delictivos similares |
| **Cluster de Desapariciones** | Múltiples desapariciones en zonas geográficas cercanas |

### 🤖 Asistente IA (Claude Sonnet 4.5)

- 💬 **Chat conversacional** con contexto del sistema
- 📍 **Predicción de zonas de búsqueda** para personas desaparecidas
- 🔍 **Análisis de similitud** entre casos
- 📝 **Generación de reportes ejecutivos** automáticos

### 🔐 Otros módulos

- 🔐 Autenticación JWT con roles (Administrador, Analista)
- 👥 Gestión completa de Personas, Vehículos, Ubicaciones, Sucesos
- 🔎 Personas Desaparecidas con dossier fotográfico
- 🌐 Visualización interactiva de grafo de vínculos (Cytoscape.js)
- 🗺️ Mapa táctico georreferenciado (Leaflet + OpenStreetMap)
- 🚨 Sistema de alertas con niveles de riesgo
- 📊 Dashboard con métricas en tiempo real
- 🔍 Búsqueda global multi-entidad
- 🌗 Tema oscuro / claro configurable
- 📥 Exportación de datos a CSV

---

## 🎬 Escenario de demo

> *"En una ciudad hipotética, tres camionetas son reportadas como robadas en las últimas 24 horas. El sistema descubre que las tres tuvieron su última ubicación conocida cerca del mismo taller. Dispara una alerta de **Nodo Logístico**. Al abrir el grafo, el analista ve un taller rodeado de tres vehículos enhebrados en rojo. Luego carga una desaparición y el sistema revela que la víctima comparte un contacto en común con un sospechoso previamente registrado."*

⏱️ Demo completa: **5 minutos**.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│              Frontend (React 18)            │
│   TypeScript · Vite · Leaflet · Cytoscape   │
└──────────────────┬──────────────────────────┘
                   │ HTTP/REST + JWT
┌──────────────────▼──────────────────────────┐
│           Backend (Spring Boot 3.2)         │
│   Java 17 · Hibernate · Spring Security     │
│   ┌──────────────────────────────────────┐  │
│   │  Red Thread Engine (5 reglas)        │  │
│   │  Cliente Claude IA (HTTP nativo)     │  │
│   └──────────────────────────────────────┘  │
└──────┬─────────────────┬────────────────────┘
       │ JDBC            │ HTTPS
┌──────▼──────┐   ┌──────▼─────────────────┐
│ PostgreSQL  │   │ api.anthropic.com      │
│  + PostGIS  │   │ (Claude Sonnet 4.5)    │
└─────────────┘   └────────────────────────┘
```

📐 **Diagramas completos**: ver [DIAGRAMAS.md](./DIAGRAMAS.md)

---

## 🛠️ Stack tecnológico

### Backend
- **Java 17** (LTS)
- **Spring Boot 3.2.5** + Spring Security + Spring Data JPA
- **Hibernate 6.4** + PostgreSQL Driver
- **JWT** (jjwt 0.12)
- **Lombok** + Maven 3.9

### Frontend
- **React 18.2** + **TypeScript 5**
- **Vite 5** + React Router 6
- **Axios** (cliente HTTP)
- **Leaflet 1.9** + react-leaflet 4.2 (mapas)
- **Cytoscape.js 3.x** (grafos)
- **Material Symbols** (iconografía)

### Base de datos
- **PostgreSQL 14+** con extensión **PostGIS 3.x**

### IA
- **Anthropic Claude Sonnet 4.5** vía API REST

---

## 🚀 Instalación y configuración

### Requisitos previos

- **JDK 17+** ([Eclipse Temurin recomendado](https://adoptium.net/))
- **Maven 3.9+** (o usar el wrapper `mvnw` incluido)
- **Node.js 18+** y **npm 9+**
- **PostgreSQL 14+** con extensión **PostGIS**
- **Git 2.40+**
- (Opcional) API Key de Anthropic para activar la IA — [obtener aquí](https://console.anthropic.com/)

### 1. Clonar el repositorio

```bash
git clone https://github.com/Sistemas22021/AICSIUDONE.git
cd AICSIUDONE
git checkout amarillo
```

### 2. Configurar la base de datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE nexo_criminal;
\c nexo_criminal
CREATE EXTENSION IF NOT EXISTS postgis;
```

### 3. Configurar el backend

Editar `backend/src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/nexo_criminal
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT
nexo.jwt.secret=cambia-este-secret-por-uno-largo-y-aleatorio
nexo.jwt.expiration-ms=86400000

# IA (opcional)
nexo.ia.api-key=${ANTHROPIC_API_KEY:}
nexo.ia.modelo=claude-sonnet-4-5-20250929
nexo.ia.max-tokens=2048
```

### 4. Iniciar el backend

```bash
cd backend

# Si usás IA, exportar la API key primero:
# Linux / macOS:
export ANTHROPIC_API_KEY="sk-ant-..."

# Windows PowerShell:
# $env:ANTHROPIC_API_KEY="sk-ant-..."

# Levantar el servidor
./mvnw spring-boot:run
```

El backend queda disponible en **http://localhost:8080**.

### 5. Iniciar el frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend queda disponible en **http://localhost:5173**.

### 6. Acceso inicial

| Campo | Valor |
|---|---|
| URL | http://localhost:5173 |
| Usuario | `admin2` |
| Contraseña | `admin123` |

> 💡 Cambiá la contraseña del admin después del primer login.

### 7. Cargar datos de prueba (opcional)

```bash
cd scripts
pip install -r requirements.txt
python generar_datos.py
```

Esto carga 51 personas, 33 vehículos, 15 ubicaciones, 20 sucesos y dispara las 5 reglas del motor.

---

## 📁 Estructura del proyecto

```
AICSIUDONE/
├── backend/                    # Spring Boot (Java 17)
│   ├── src/main/java/com/nexocriminal/
│   │   ├── domain/             # Entidades, repositorios, servicios
│   │   │   ├── persona/
│   │   │   ├── vehiculo/
│   │   │   ├── ubicacion/
│   │   │   ├── suceso/
│   │   │   ├── desaparecida/
│   │   │   ├── alerta/
│   │   │   └── vinculo/
│   │   ├── engine/             # Motor Red Thread (5 reglas)
│   │   ├── ia/                 # Integración Claude
│   │   ├── files/              # Almacenamiento de fotos
│   │   └── security/           # JWT + filtros
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/                   # React + TypeScript
│   ├── src/
│   │   ├── pages/              # Páginas (Dashboard, Personas, etc.)
│   │   ├── components/         # Componentes reutilizables
│   │   ├── services/           # API client + contextos
│   │   ├── types/              # Definiciones TS
│   │   └── styles.css
│   ├── package.json
│   └── vite.config.ts
│
├── scripts/                    # Scripts auxiliares Python
│   └── generar_datos.py        # Genera datos de prueba
│
├── docs/                       # Documentación adicional
│   ├── INFORME_FACTIBILIDAD.md
│   ├── INFORME_TECNICO.md
│   ├── DIAGRAMAS.md
│   └── PLAN_PRUEBAS.md
│
├── .gitignore
├── CONTRIBUTING.md
└── README.md
```

---

## 📚 Documentación

| Documento | Descripción |
|---|---|
| [INFORME_FACTIBILIDAD.md](./docs/INFORME_FACTIBILIDAD.md) | Análisis de factibilidad económica, técnica y operativa |
| [INFORME_TECNICO.md](./docs/INFORME_TECNICO.md) | Arquitectura, módulos, decisiones técnicas |
| [DIAGRAMAS.md](./docs/DIAGRAMAS.md) | Diagramas del sistema en Mermaid |
| [PLAN_PRUEBAS.md](./docs/PLAN_PRUEBAS.md) | Plan de testing y casos de prueba |
| [CONTRIBUTING.md](./CONTRIBUTING.md) | Guía para contribuir al proyecto |

---

## 🔌 API REST

Todos los endpoints están bajo `/api/v1/`. Requieren header `Authorization: Bearer <jwt>` excepto los de autenticación.

### Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/auth/login` | Iniciar sesión, devuelve JWT |
| `POST` | `/auth/registrar` | Registrar nuevo usuario |
| `POST` | `/auth/cambiar-password` | Cambiar contraseña |
| `GET` | `/personas` | Listar personas |
| `POST` | `/personas` | Crear persona |
| `PUT` | `/personas/{id}` | Actualizar persona |
| `DELETE` | `/personas/{id}` | Eliminar persona |
| `GET` | `/vehiculos` | Listar vehículos |
| `POST` | `/vehiculos` | Registrar vehículo |
| `PATCH` | `/vehiculos/{id}/estado` | Cambiar estado de vehículo |
| `GET` | `/ubicaciones` | Listar ubicaciones |
| `POST` | `/ubicaciones` | Registrar ubicación |
| `GET` | `/sucesos` | Listar sucesos |
| `POST` | `/sucesos` | Registrar suceso |
| `GET` | `/desaparecidas` | Listar personas desaparecidas |
| `POST` | `/desaparecidas` | Registrar caso de desaparición |
| `POST` | `/desaparecidas/{id}/foto` | Subir foto (multipart) |
| `POST` | `/engine/ejecutar-todo` | Ejecutar las 5 reglas del motor |
| `POST` | `/engine/nodo-logistico` | Ejecutar regla específica |
| `GET` | `/grafo/completo` | Grafo Cytoscape completo |
| `GET` | `/alertas` | Listar alertas |
| `PATCH` | `/alertas/{id}/estado` | Cambiar estado de alerta |
| `GET` | `/ia/estado` | Estado de configuración de IA |
| `POST` | `/ia/chat` | Chat con asistente IA |
| `POST` | `/ia/zonas-busqueda/{id}` | Predicción de zonas |
| `POST` | `/ia/reporte/{tipo}/{id}` | Generar reporte ejecutivo |

📋 **Documentación completa de API**: ver módulo `Documentación API` dentro del sistema (menú Ayuda).

---

## 🧪 Datos de prueba

El sistema viene con un script Python que genera datos realistas para probar todos los módulos:

| Entidad | Cantidad |
|---|---|
| Personas | 51 |
| Vehículos | 33 (8 reportados como robados) |
| Ubicaciones | 15 (algunas marcadas como sospechosas) |
| Sucesos | 20 |
| Personas Desaparecidas | Variable según prueba |

Estos datos están preparados para **disparar las 5 reglas del motor** al ejecutarlo.

---

## 🔒 Seguridad

- 🔐 Contraseñas hasheadas con **BCrypt** (factor 10)
- 🎟️ Autenticación stateless con **JWT** (HS256)
- 🛡️ Filtro de seguridad sobre todos los endpoints `/api/v1/**`
- 🔑 API Keys gestionadas vía **variables de entorno** (nunca commiteadas)
- 🚫 CORS configurable según entorno
- ⚠️ Para producción se requiere: HTTPS, rate limiting, logs de auditoría

---

## 🎓 Equipo de desarrollo

Proyecto desarrollado por el **Equipo Amarillo** de la materia Sistemas de Información II:

| Integrante | C.I. | Rol | GitHub |
|---|---|---|---|
| Manuel Rodríguez | 30.911.587 | Full Stack Junior | [@manuel-rodriguez](#) |
| Santiago Ramírez | 31.455.345 | Full Stack Junior | [@santiago-ramirez](#) |
| Valeria García | 31.649.272 | Full Stack Junior | [@valeria-garcia](#) |
| Isaac Carreño | 31.841.776 | Full Stack Junior | [@isaac-carreño](#) |
| John Salazar | 31.648.942 | Full Stack Junior | [@john-salazar](#) |

**Universidad**: Universidad de Oriente — Núcleo Nueva Esparta
**Materia**: Sistemas de Información II
**Profesor**: Alejandro Marcano
**Período**: 2026

---

## 📈 Estado del proyecto

| Módulo | Estado |
|---|---|
| Autenticación JWT | ✅ Completo |
| Gestión de personas | ✅ Completo |
| Gestión de vehículos | ✅ Completo |
| Gestión de ubicaciones | ✅ Completo |
| Gestión de sucesos | ✅ Completo |
| Personas desaparecidas | ✅ Completo |
| Motor Red Thread (5 reglas) | ✅ Completo |
| Sistema de alertas | ✅ Completo |
| Visualización de grafo | ✅ Completo |
| Mapa táctico | ✅ Completo |
| Asistente IA | ✅ Completo |
| Dashboard | ✅ Completo |
| Búsqueda global | ✅ Completo |
| Tests automatizados | 🔜 Trabajo futuro |
| Dockerización | 🔜 Trabajo futuro |
| Despliegue cloud | 🔜 Trabajo futuro |

---

## 🛣️ Roadmap

### v0.2 (próximo)
- [ ] Tests unitarios JUnit 5 (cobertura ≥ 70%)
- [ ] Migración a Flyway para gestión de schema
- [ ] Dockerización completa
- [ ] CI/CD con GitHub Actions
- [ ] Rate limiting

### v0.3
- [ ] Aplicación móvil React Native
- [ ] WebSockets para alertas en tiempo real
- [ ] Generación de reportes en PDF
- [ ] Importación masiva de datos (CSV/Excel)

### v1.0
- [ ] Multi-tenancy (varias unidades policiales)
- [ ] Logs de auditoría persistentes
- [ ] Capa de Machine Learning para anomalías
- [ ] OCR de placas vehiculares (CCTV)

---

## 🤝 Contribuir

¿Querés contribuir al proyecto? Lee primero [CONTRIBUTING.md](./CONTRIBUTING.md).

```bash
# 1. Fork del repositorio
# 2. Crear branch desde 'amarillo'
git checkout amarillo
git checkout -b feature/mi-feature

# 3. Hacer cambios y commit
git commit -m "feat: descripción del cambio"

# 4. Push y crear Pull Request
git push origin feature/mi-feature
```

---

## 📜 Licencia

Este proyecto se desarrolla con fines **académicos** dentro del marco de la materia Sistemas de Información II de la Universidad de Oriente, Núcleo Nueva Esparta.

No tiene fines comerciales y no está autorizado para uso operativo en fuerzas policiales reales sin las debidas adaptaciones, auditorías de seguridad y aprobaciones legales.

---

## 🙏 Agradecimientos

- **Prof. Alejandro Marcano** por la guía durante el desarrollo del proyecto.
- **Anthropic** por el acceso a la API de Claude.
- Comunidad **open-source** por las librerías y herramientas utilizadas.

---

<div align="center">

### 🔴 Nexo Criminal

*"Cada hilo cuenta una historia."*

Hecho con 💻 por el **Equipo Amarillo**
Universidad de Oriente — Núcleo Nueva Esparta — 2026

</div>