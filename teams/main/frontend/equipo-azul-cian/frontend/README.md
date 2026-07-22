# 🚔 Frontend - Sistema de Gestión de Incidentes en Tiempo Real y Despacho de Patrullas

Proyecto desarrollado para la asignatura **Sistemas de Información II**.

Interfaz web interactiva para la visualización georreferenciada de incidentes en mapa, administración de patrullas operativas, asignación en tiempo real y seguimiento del ciclo de vida de los casos.

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

- [Descripción del Proyecto](#-frontend---sistema-de-gestión-de-incidentes-en-tiempo-real-y-despacho-de-patrullas)
- [Arquitectura General](#arquitectura-general)
- [Características del Frontend](#características-del-frontend)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura de Software](#arquitectura-de-software)
- [Patrones de Diseño Implementados](#patrones-de-diseño-implementados)
- [Estructura del Frontend](#estructura-del-frontend)
- [Requisitos Previos](#requisitos-previos)
- [Instalación del Frontend](#instalación-del-frontend)
- [Variables de Entorno](#variables-de-entorno)
- [Guía de Uso](#guía-de-uso)
- [Escenario de Uso](#escenario-de-uso)
- [Reglas de Negocio](#reglas-de-negocio)
- [Roles del Sistema](#roles-del-sistema)
- [Flujo Principal del Sistema](#flujo-principal-del-sistema)
- [Despliegue del Frontend](#despliegue-del-frontend)
- [Flujo de Despliegue](#flujo-de-despliegue)
- [Integración Continua (CI)](#integración-continua-ci)
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

El frontend consume la API REST desarrollada en Spring Boot para presentar la información operativa en un mapa interactivo y vistas tabulares.

---

# Características del Frontend

* Interfaz con mapas georreferenciados para ubicar incidentes y patrullas.
* Formulario modal para registro de nuevos incidentes.
* Listados y filtros de patrullas según su estado.
* Pantalla de gestión de asignaciones de patrullas.
* Dashboard intuitivo para visualización de estadísticas.
* Manejo automatizado de reintentos en peticiones HTTP.

---

# Tecnologías Utilizadas

* **Librería Core:** React
* **Lenguaje:** TypeScript
* **Herramienta de Construcción:** Vite
* **Estilos:** TailwindCSS
* **Mapeo:** React Leaflet / OpenStreetMap / Mapbox
* **Cliente HTTP:** Axios
* **Testing:** Vitest

---

# Arquitectura de Software

El frontend sigue una arquitectura modular en componentes estructurada de la siguiente manera:

* **Adapters:** Abstraen los proveedores de mapas.
* **Components:** UI reutilizable (modales, visores de mapa).
* **Views:** Pantallas principales asociadas a rutas de la aplicación.
* **Utils:** Funciones auxiliares para filtros y manejo de red.
* **Types:** Definiciones de interfaces de TypeScript para el dominio.

---

# Patrones de Diseño Implementados

| Patrón | Uso |
| --- | --- |
| **Factory Method** | Selección del adaptador de mapas (`MapAdapterFactory`). |
| **Adapter** | Integración con proveedores de mapas (`MapAdapter`, `OpenStreetMapAdapter`, `MapboxAdapter`). |

---

# Estructura del Frontend

```text
frontend
│
├── public
│
├── src
│   │
│   ├── adapters
│   │   ├── MapAdapter.ts
│   │   ├── MapAdapterFactory.ts
│   │   ├── MapboxAdapter.tsx
│   │   └── OpenStreetMapAdapter.tsx
│   │
│   ├── assets
│   │   ├── hero.png
│   │   ├── react.svg
│   │   └── vite.svg
│   │
│   ├── components
│   │   ├── incident
│   │   │   └── IncidentModal.tsx
│   │   └── map
│   │       └── MapView.tsx
│   │
│   ├── types
│   │   ├── assignment.ts
│   │   ├── incident.ts
│   │   └── patrol.ts
│   │
│   ├── utils
│   │   ├── fetchWithRetry.ts
│   │   ├── fetchWithRetry.test.ts
│   │   ├── patrolFilters.ts
│   │   └── patrolFilters.test.ts
│   │
│   ├── views
│   │   ├── Inicio.tsx
│   │   ├── Incidentes.tsx
│   │   ├── Patrullas.tsx
│   │   ├── Asignaciones.tsx
│   │   ├── Asignaciones.css
│   │   └── Mapa.tsx
│   │
│   ├── App.tsx
│   ├── Layout.tsx
│   ├── main.tsx
│   ├── App.css
│   └── index.css
│
├── .env
├── .env.example
├── .gitignore
├── eslint.config.js
├── index.html
├── package.json
├── package-lock.json
├── README.md
├── tsconfig.json
└── vite.config.js

```

---

# Requisitos Previos

Antes de ejecutar el frontend es necesario contar con:

* **Git**
* **Node.js** (v18.x o superior)
* **npm** (v9.x o superior)

---

# Instalación del Frontend

1. **Navegar al directorio del frontend e instalar dependencias:**
```bash
cd frontend
npm install

```


2. **Iniciar el servidor de desarrollo:**
```bash
npm run dev

```



---

# Variables de Entorno

Crear un archivo `.env` en la raíz de `frontend` utilizando como referencia `.env.example`:

```env
# Para desarrollo local tradicional con backend local:
# VITE_API_BASE_URL=http://localhost:8080/api

# Para conectar la app desplegada en Vercel con el backend local mediante localtunnel (subdominio reservado):
VITE_API_BASE_URL=https://equipo-cian-backend.loca.lt/api
```

> 💡 **Conexión entre Vercel (HTTPS) y Backend Local (Localtunnel):**  
> Debido a las restricciones de seguridad de contenido mixto (*Mixed Content*), el frontend desplegado en Vercel no puede enviar peticiones a `http://localhost:8080`. Se utiliza **localtunnel** reservando un subdominio fijo:  
> ```cmd
> npx localtunnel --port 8080 --subdomain equipo-cian-backend
> ```  
> Esto genera la URL tunelada segura `https://equipo-cian-backend.loca.lt`, permitiendo que la app de Vercel consuma la API sin necesidad de re-desplegar o actualizar variables a cada momento.

---

# Guía de Uso

El sistema interactivo está diseñado para el Operador del Centro de Despacho:

1. Registrar una patrulla desde la sección de patrullas.
2. Registrar un incidente abriendo el modal correspondiente.
3. Seleccionar las coordenadas exactas directamente en el mapa.
4. Asignar una patrulla disponible a un incidente activo.
5. Supervisar el cambio de estado en el panel de mapa y listas.
6. Realizar el cierre del incidente y consultar el resumen en el dashboard.

---

# Escenario de Uso

```text
Operador ingresa a la aplicación web
            │
            ▼
Navega a la vista de Mapa o Incidentes
            │
            ▼
Selecciona la ubicación en el mapa interactivo
            │
            ▼
Asigna una patrulla disponible de la lista
            │
            ▼
Supervisa el estado actualizado en pantalla
            │
            ▼
Cierra el incidente al finalizar la atención

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

1. Seleccionar la vista de registro.
2. Marcar la ubicación sobre el mapa.
3. Gestionar patrullas registradas.
4. Ejecutar la asignación mediante la interfaz.
5. Observar actualización del mapa en tiempo real.
6. Cerrar el incidente desde la interfaz del despacho.

---

# Despliegue del Frontend

El frontend se despliega utilizando **Vercel**.

Para conectarlo con un backend ejecutándose localmente durante presentaciones o pruebas, se utiliza **localtunnel** (`npx localtunnel --port 8080 --subdomain equipo-cian-backend`), permitiendo que la aplicación en producción consuma la API en tiempo real de forma segura.

---

# Flujo de Despliegue

```text
Desarrollador ──> Push ──> GitHub ──> GitHub Actions ──> Compilación ──> Deploy automático ──> Vercel ──> Usuario

```

Cada push a la rama correspondiente desencadena de forma automatizada el pipeline de integración continua y el despliegue del frontend.

---

# Integración Continua (CI)

El repositorio incorpora un pipeline de GitHub Actions para el frontend que ejecuta automáticamente el proceso de instalación de dependencias y compilación del proyecto en cada actualización enviada al repositorio, verificando que la aplicación pueda construirse correctamente antes de su despliegue.

---

# Testing

El frontend incorpora pruebas unitarias desarrolladas con **Vitest** para funciones de utilidad e integración de componentes (`patrolFilters.test.ts`, `fetchWithRetry.test.ts`).

Para ejecutar los tests del frontend:

```bash
npm run test

```

---

# Licencia

Proyecto académico desarrollado para la asignatura **Sistemas de Información II** - Universidad de Oriente.
