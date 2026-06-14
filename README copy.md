# 🧵 Nexo Criminal — The Red Thread

### Sistema de Inteligencia de Vínculos para Análisis Criminal

> *"Ningún crimen ocurre en el vacío. Detrás de cada delito hay un hilo que lo conecta con otros. Nexo Criminal encuentra ese hilo."*

---

## 📌 Descripción General

**Nexo Criminal** es una plataforma de inteligencia criminal que aplica el concepto de **The Red Thread (El Hilo Rojo)** para descubrir vínculos no evidentes entre personas, vehículos, lugares y sucesos delictivos. El sistema está diseñado como herramienta táctica para analistas de inteligencia, investigadores de desapariciones y operadores de monitoreo urbano.

A diferencia de los sistemas tradicionales de registro policial —que almacenan hechos de forma aislada— Nexo Criminal **enhebra** los datos: cruza coordenadas, tiempos, relaciones sociales, patrones de movimiento y modus operandi para revelar la estructura oculta del crimen organizado.

### Ejes del sistema

1. **Robo de vehículos** — detección de nodos logísticos (deshuesaderos), vehículos de apoyo (escoltas/mulas) y patrones de modus operandi compartidos entre bandas.
2. **Personas desaparecidas** — reconstrucción del círculo de confianza de la víctima, identificación de intermediarios críticos y detección de puntos de contacto geográficos comunes.

---

## 🎯 Objetivos

### Objetivo general

Construir una plataforma de análisis de vínculos criminales que permita a los cuerpos de investigación descubrir relaciones ocultas entre entidades (personas, vehículos, ubicaciones y sucesos) mediante el cruce automático de datos y visualización en grafos.

### Objetivos específicos

- Registrar y normalizar información sobre vehículos robados, personas desaparecidas, sospechosos y sucesos criminales.
- Detectar automáticamente vínculos comunes entre entidades mediante reglas de negocio (proximidad temporal, proximidad geográfica, relaciones sociales, modus operandi).
- Visualizar los vínculos descubiertos en forma de grafo de nodos y aristas (el "hilo rojo").
- Generar alertas proactivas cuando se detectan patrones sospechosos (nodos logísticos, vehículos de escolta, puntos de contacto).
- Ofrecer un MVP funcional en 4 semanas que sirva como base escalable para futuras expansiones.

---

## 🧩 Módulos del Sistema

| Módulo | Responsabilidad |
|---|---|
| **Gestión de Entidades** | CRUD de Personas, Vehículos, Ubicaciones, Sucesos. |
| **Motor de Vínculos (Red Thread Engine)** | Algoritmos que descubren relaciones entre entidades. |
| **Alertas Inteligentes** | Notificaciones automáticas cuando se detecta un patrón sospechoso. |
| **Visualización de Grafos** | Interfaz frontend que muestra los vínculos como red interactiva. |
| **Similitud de Modus Operandi** | Agrupa casos por método delictivo compartido. |
| **Reportes y Exportación** | Generación de informes para la fiscalía o unidades tácticas. |

---

## 🛠️ Stack Tecnológico

### Backend
- **Java 17+** — lenguaje principal del backend.
- **Spring Boot 3.x** — framework de aplicación.
- **Spring Data JPA** — persistencia.
- **Spring Security** — autenticación y autorización.
- **IntelliJ IDEA Community Edition** — IDE de desarrollo.

### Frontend
- **TypeScript** — tipado estricto.
- **React 18+** — framework de UI.
- **Vite** — bundler rápido.
- **Cytoscape.js** — visualización de grafos.
- **NestJS** — BFF (Backend for Frontend) opcional.

### Scripts y Procesamiento
- **Python 3.11+** — scripts de ETL, carga masiva, simulación de datasets.

### Base de Datos
- **PostgreSQL 15+** — base de datos relacional principal.
- **Extensión PostGIS** — para consultas geoespaciales.

### Herramientas de Gestión
- **GitHub** — control de versiones, ramas, pull requests.
- **Scrum** — metodología ágil (sprints de 1 semana).
- **Markdown (.md)** — toda la documentación del proyecto.

---

## 📂 Estructura del Proyecto

```
nexo-criminal/
├── README.md                      # Este archivo
├── docs/                          # Documentación completa
│   ├── 01-MVP.md
│   ├── 02-casos-de-uso.md
│   ├── 03-historias-de-usuario.md
│   ├── 04-modelo-de-datos.md
│   ├── 05-arquitectura.md
│   ├── 06-scrum-plan.md
│   ├── 07-git-workflow.md
│   ├── 08-glosario.md
│   └── 09-manifiesto-agil.md
├── diagramas/                     # Diagramas en Mermaid
│   ├── arquitectura.md
│   ├── modelo-er.md
│   └── flujo-red-thread.md
├── database/                      # Scripts SQL
│   ├── 01-schema.sql
│   └── 02-datos-ejemplo.sql
├── backend/                       # Spring Boot (Java)
│   ├── pom.xml
│   └── src/main/...
├── frontend/                      # React + TypeScript
│   ├── package.json
│   └── src/...
└── scripts/                       # Python ETL / simulador
    ├── generar_datos.py
    └── requirements.txt
```

---

## 🚀 Entregable (Factibilidad)

Según lo acordado en la clase del miércoles 22/04, el entregable de factibilidad incluye:

1. Este documento principal (`README.md`).
2. Documentación detallada en `docs/`.
3. Producto Mínimo Viable (MVP) funcional por al menos **4 semanas**.
4. Repositorio GitHub con estrategia de ramas definida.
5. Primeras historias de usuario listas para el sprint planning del viernes.

---

## ⚡ Quickstart

### 1. Base de datos
```bash
psql -U postgres -f database/01-schema.sql
psql -U postgres -d nexo_criminal -f database/02-datos-ejemplo.sql
```

### 2. Backend
```bash
cd backend
./mvnw spring-boot:run
# API en http://localhost:8080
```

### 3. Scripts Python (generar datos simulados)
```bash
cd scripts
pip install -r requirements.txt
python generar_datos.py
```

### 4. Frontend
```bash
cd frontend
npm install
npm run dev
# UI en http://localhost:5173
```

---

## 👥 Equipo

| Rol | Responsable |
|---|---|
| Scrum Master / Líder Sprint 1 | *Por definir el viernes* |
| Product Owner | *Por definir* |
| Backend (Java/Spring) | *Por asignar* |
| Frontend (React) | *Por asignar* |
| Scripts/DB (Python/PostgreSQL) | *Por asignar* |

Correo grupal del equipo: *pendiente de creación*.

---

## 📅 Cronograma

| Semana | Hito |
|---|---|
| Semana 1 | Factibilidad, MVP definido, primer sprint planning. |
| Semana 2 | Sprint 1 — CRUD de entidades y base de datos. |
| Semana 3 | Sprint 2 — Motor de vínculos (Casos A y C). |
| Semana 4 | Sprint 3 — Visualización de grafo y alertas. Demo final. |

---

## 📖 Lecturas recomendadas

- Manifiesto Ágil y sus 12 principios.
- Literatura sobre Scrum.
- Software altamente escalable (patrones de microservicios, caching, colas).

---

## 📜 Licencia

Proyecto académico. Uso restringido a fines educativos.
