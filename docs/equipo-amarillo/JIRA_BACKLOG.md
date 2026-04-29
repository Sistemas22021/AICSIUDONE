# Backlog de Jira
## Nexo Criminal — Equipo Amarillo

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo
**Metodología:** Scrum (sprints de 2 semanas)
**Duración total:** 14 semanas (7 sprints)

---

## Tabla de Contenido

1. [Resumen del proyecto](#1-resumen-del-proyecto)
2. [Roles del equipo](#2-roles-del-equipo)
3. [Épicas](#3-épicas)
4. [Sprint 1 — Setup, Análisis y Factibilidad](#4-sprint-1)
5. [Sprint 2 — Diseño BD + Backend Core](#5-sprint-2)
6. [Sprint 3 — Backend (Vehículos, Ubicaciones, Sucesos)](#6-sprint-3)
7. [Sprint 4 — Frontend Core + Motor Red Thread](#7-sprint-4)
8. [Sprint 5 — Alertas, Grafo y Búsqueda](#8-sprint-5)
9. [Sprint 6 — Personas Desaparecidas + IA](#9-sprint-6)
10. [Sprint 7 — Testing, Documentación y Pulido](#10-sprint-7)
11. [Resumen total de Story Points](#11-resumen-total-de-story-points)

---

## 1. Resumen del proyecto

| Atributo | Valor |
|---|---|
| Nombre del proyecto | Nexo Criminal |
| Clave Jira | NEXO |
| Tipo | Software (Scrum) |
| Sprints planificados | 7 |
| Duración por sprint | 2 semanas |
| Story points totales | 246 |
| Story points por sprint (promedio) | 35 |
| Velocity esperada | 30-40 SP por sprint |

### Tipos de issue

- 🟣 **Epic** — Conjunto grande de funcionalidades
- 🟢 **Story** — Funcionalidad de cara al usuario
- 🔵 **Task** — Tarea técnica sin valor directo al usuario
- 🔴 **Bug** — Error a corregir
- 🟡 **Spike** — Investigación técnica

---

## 2. Roles del equipo

| Integrante | C.I. | Rol Scrum | Rol técnico |
|---|---|---|---|
| Manuel Rodríguez | 30.911.587 | Tech Lead / Scrum Master | Full Stack Junior |
| Santiago Ramírez | 31.455.345 | Developer | Full Stack Junior |
| Valeria García | 31.649.272 | Developer | Full Stack Junior |
| Isaac Carreño | 31.841.776 | Developer | Full Stack Junior |
| John Salazar | 31.648.942 | Developer | Full Stack Junior |

---

## 3. Épicas

| Clave | Épica | Sprint(s) | Story Points |
|---|---|---|---|
| NEXO-E1 | Setup, análisis y factibilidad del proyecto | 1 | 28 |
| NEXO-E2 | Diseño y persistencia de datos | 2 | 21 |
| NEXO-E3 | Autenticación y seguridad | 2 | 16 |
| NEXO-E4 | Gestión de entidades core (CRUD) | 2, 3 | 42 |
| NEXO-E5 | Frontend operativo (UI base) | 4 | 24 |
| NEXO-E6 | Motor Red Thread (5 reglas) | 4, 5 | 34 |
| NEXO-E7 | Sistema de alertas y grafo de vínculos | 5 | 21 |
| NEXO-E8 | Personas desaparecidas con galería | 6 | 18 |
| NEXO-E9 | Integración con IA Claude | 6 | 16 |
| NEXO-E10 | Testing, documentación y pulido | 7 | 26 |
| **TOTAL** | | | **246** |

---

## 4. Sprint 1
### Setup, Análisis y Factibilidad

**Período:** Semanas 1-2
**Objetivo del sprint:** Establecer las bases del proyecto, completar análisis de factibilidad, configurar repositorio y entornos de desarrollo, y definir la arquitectura inicial del sistema.

**Entregables:**
- Informe de Factibilidad completo
- Repositorio Git configurado con branches
- Diagramas de arquitectura preliminares
- Entornos de desarrollo de cada miembro funcionando

**Story Points:** 28

---

### 🟣 Épica NEXO-E1: Setup, análisis y factibilidad del proyecto

#### 🔵 NEXO-1 — Definir alcance y MVP del proyecto
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Manuel
- **Descripción:** Definir el alcance funcional del MVP y delimitar el trabajo del primer cuatrimestre.
- **Criterios de aceptación:**
  - [ ] Documento de alcance del MVP redactado
  - [ ] Lista de funcionalidades dentro y fuera del MVP definida
  - [ ] Aprobado por el equipo

#### 🔵 NEXO-2 — Investigar dominio criminal y procedimientos policiales
- **Tipo:** Spike
- **Story Points:** 5
- **Asignado a:** Valeria
- **Descripción:** Realizar investigación documental del dominio para fundamentar el análisis operativo.
- **Criterios de aceptación:**
  - [ ] Visualizar al menos 2 documentales de análisis criminal
  - [ ] Investigar funciones del CICPC y unidades análogas
  - [ ] Identificar roles policiales típicos (analistas, investigadores, etc.)
  - [ ] Documento resumen de hallazgos compartido con el equipo

#### 🟢 NEXO-3 — Como equipo, queremos un Informe de Factibilidad para validar la viabilidad del proyecto
- **Tipo:** Story
- **Story Points:** 8
- **Asignado a:** Equipo (Manuel coordina)
- **Descripción:** Redactar el informe de factibilidad cubriendo aspectos económicos, técnicos y operativos.
- **Criterios de aceptación:**
  - [ ] Planteamiento del problema redactado (máx. 1.5 páginas)
  - [ ] Objetivos de investigación definidos
  - [ ] Factibilidad económica con presupuestos detallados
  - [ ] Factibilidad técnica con stack justificado
  - [ ] Factibilidad operativa con análisis de usuarios
  - [ ] MVP definido con alcance claro
  - [ ] Documento revisado y aprobado por el equipo

#### 🔵 NEXO-4 — Configurar repositorio GitHub con branch `amarillo`
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** John
- **Descripción:** Crear el branch del equipo en el repo oficial y configurar protecciones.
- **Criterios de aceptación:**
  - [ ] Branch `amarillo` creado desde `main`
  - [ ] Reglas de protección configuradas (no push directo)
  - [ ] README inicial con descripción del proyecto
  - [ ] `.gitignore` configurado para Java + Node

#### 🔵 NEXO-5 — Diseñar arquitectura del sistema
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Definir la arquitectura de tres capas y elegir el stack tecnológico.
- **Criterios de aceptación:**
  - [ ] Diagrama de arquitectura general
  - [ ] Stack backend definido (Java 17 + Spring Boot 3.2)
  - [ ] Stack frontend definido (React 18 + TypeScript + Vite)
  - [ ] Base de datos definida (PostgreSQL + PostGIS)
  - [ ] Documento de decisiones arquitectónicas

#### 🔵 NEXO-6 — Configurar entornos de desarrollo del equipo
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Santiago + Isaac
- **Descripción:** Asegurar que cada miembro tenga su ambiente local funcionando.
- **Criterios de aceptación:**
  - [ ] JDK 17 instalado en todos los equipos
  - [ ] Maven 3.9+ instalado
  - [ ] Node.js 18+ y npm 9+ instalados
  - [ ] PostgreSQL 14+ con PostGIS instalado
  - [ ] Editor (VS Code o IntelliJ) configurado con extensiones recomendadas

#### 🔵 NEXO-7 — Establecer convenciones de código y flujo Git
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Manuel
- **Descripción:** Documentar estándares para mantener consistencia.
- **Criterios de aceptación:**
  - [ ] CONTRIBUTING.md redactado
  - [ ] Convenciones de commits (Conventional Commits) definidas
  - [ ] Estructura de branches definida
  - [ ] Plantilla de Pull Request creada

---

## 5. Sprint 2
### Diseño BD + Backend Core (Auth, Personas)

**Período:** Semanas 3-4
**Objetivo del sprint:** Construir el modelo de datos completo, configurar el proyecto Spring Boot, implementar autenticación con JWT y el primer módulo CRUD de personas.

**Entregables:**
- Base de datos PostgreSQL con schema completo
- Backend Spring Boot funcional con login JWT
- CRUD de Personas (backend)
- Endpoint de autenticación operativo

**Story Points:** 35

---

### 🟣 Épica NEXO-E2: Diseño y persistencia de datos

#### 🔵 NEXO-8 — Diseñar el modelo entidad-relación completo
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** Isaac
- **Descripción:** Modelar todas las entidades del sistema con sus relaciones.
- **Criterios de aceptación:**
  - [ ] Diagrama ER de todas las entidades
  - [ ] Relaciones identificadas (1:N, N:M)
  - [ ] Atributos y tipos de datos definidos
  - [ ] Índices y constraints planificados

#### 🔵 NEXO-9 — Crear base de datos `nexo_criminal` con extensión PostGIS
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Isaac
- **Descripción:** Setup inicial de la BD para el equipo.
- **Criterios de aceptación:**
  - [ ] BD `nexo_criminal` creada en PostgreSQL local
  - [ ] Extensión PostGIS instalada y verificada
  - [ ] Script de inicialización compartido en el repo

#### 🟢 NEXO-10 — Como desarrollador, quiero entidades JPA mapeadas a las tablas para acceder a la BD desde Java
- **Tipo:** Story
- **Story Points:** 8
- **Asignado a:** Manuel + John
- **Descripción:** Implementar todas las entidades del dominio con anotaciones JPA.
- **Criterios de aceptación:**
  - [ ] Entidad `Persona` con todos sus campos
  - [ ] Entidad `Vehiculo` con FK a Persona
  - [ ] Entidad `Ubicacion` con campos lat/lng
  - [ ] Entidad `Suceso` con FKs a Vehiculo, Persona, Ubicacion
  - [ ] Entidad `Usuario` para autenticación
  - [ ] Entidad `Vinculo` para el motor
  - [ ] Entidad `Alerta`
  - [ ] Tablas auxiliares (Avistamiento, Relacion)
  - [ ] Hibernate genera el schema correctamente con `ddl-auto=update`

#### 🔵 NEXO-11 — Configurar Hibernate y propiedades de conexión
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Manuel
- **Descripción:** Configurar el archivo `application.properties` con todos los parámetros.
- **Criterios de aceptación:**
  - [ ] URL de conexión a PostgreSQL configurada
  - [ ] Usuario y password parametrizados
  - [ ] `spring.jpa.hibernate.ddl-auto=update` activado
  - [ ] Logging de SQL configurado para desarrollo

#### 🔵 NEXO-12 — Implementar repositorios Spring Data JPA
- **Tipo:** Task
- **Story Points:** 4
- **Asignado a:** Santiago
- **Descripción:** Crear los repositorios para cada entidad principal.
- **Criterios de aceptación:**
  - [ ] `PersonaRepository extends JpaRepository`
  - [ ] `VehiculoRepository`
  - [ ] `UbicacionRepository`
  - [ ] `SucesoRepository`
  - [ ] `UsuarioRepository`
  - [ ] Queries personalizadas (`findByDocumento`, etc.)

---

### 🟣 Épica NEXO-E3: Autenticación y seguridad

#### 🟢 NEXO-13 — Como usuario, quiero iniciar sesión con usuario y contraseña para acceder al sistema
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Implementar endpoint de login que devuelve JWT.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/auth/login`
  - [ ] Validación de credenciales con BCrypt
  - [ ] Generación de JWT con username y rol
  - [ ] Retorno de token + datos básicos del usuario
  - [ ] Manejo de error 401 si credenciales son inválidas

#### 🟢 NEXO-14 — Como usuario, quiero registrarme para usar el sistema
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Manuel
- **Descripción:** Endpoint de registro de nuevos usuarios.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/auth/registrar`
  - [ ] Hash de password con BCrypt antes de guardar
  - [ ] Validación de username único
  - [ ] Asignación de rol por defecto (`ANALISTA`)

#### 🔵 NEXO-15 — Configurar Spring Security con filtro JWT
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Securizar todos los endpoints excepto los de auth.
- **Criterios de aceptación:**
  - [ ] `SecurityConfig` configurado
  - [ ] `JwtAuthFilter` valida tokens en cada request
  - [ ] Endpoints `/api/v1/auth/**` permitidos sin token
  - [ ] CORS configurado para `http://localhost:5173`
  - [ ] Sessions stateless

#### 🟢 NEXO-16 — Como usuario, quiero cambiar mi contraseña para mantener mi cuenta segura
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** John
- **Descripción:** Endpoint para cambio de contraseña.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/auth/cambiar-password`
  - [ ] Validación de password actual antes de cambiar
  - [ ] Mínimo 6 caracteres en password nueva
  - [ ] Hash con BCrypt al guardar

---

### 🟣 Épica NEXO-E4: Gestión de entidades core (parcial — Personas)

#### 🟢 NEXO-17 — Como analista, quiero registrar personas en el sistema para asociarlas a casos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Santiago
- **Descripción:** CRUD completo de Personas en el backend.
- **Criterios de aceptación:**
  - [ ] `GET /api/v1/personas` (listar todas)
  - [ ] `GET /api/v1/personas/{id}` (obtener una)
  - [ ] `POST /api/v1/personas` (crear)
  - [ ] `PUT /api/v1/personas/{id}` (actualizar)
  - [ ] `DELETE /api/v1/personas/{id}` (eliminar)
  - [ ] Validación de documento único
  - [ ] Manejo de errores 404 y 400

#### 🟢 NEXO-18 — Como analista, quiero registrar relaciones sociales entre personas para identificar redes
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Valeria
- **Descripción:** Endpoint para crear relaciones entre dos personas.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/personas/{id}/relaciones`
  - [ ] Tipo de relación (FAMILIAR, LABORAL, AMISTAD, OTRO)
  - [ ] Peso de la relación (decimal)
  - [ ] Lectura de relaciones de una persona

#### 🔴 NEXO-19 — [BUG] Error al crear persona con caracteres especiales en el nombre
- **Tipo:** Bug
- **Story Points:** 2
- **Asignado a:** Santiago
- **Descripción:** El sistema falla al guardar nombres con tildes o `ñ`.
- **Criterios de aceptación:**
  - [ ] Verificar encoding UTF-8 en BD
  - [ ] Configurar Spring para usar UTF-8 en requests
  - [ ] Test manual con nombres como "José", "Núñez"

---

## 6. Sprint 3
### Backend (Vehículos, Ubicaciones, Sucesos)

**Período:** Semanas 5-6
**Objetivo del sprint:** Completar todos los CRUDs de entidades operativas en el backend, agregar funcionalidades de búsqueda avanzada y registro de avistamientos.

**Entregables:**
- CRUDs completos de Vehículos, Ubicaciones, Sucesos
- Sistema de avistamientos vehiculares
- Búsqueda de intermediarios entre personas
- Endpoints de exportación

**Story Points:** 32

---

### 🟣 Épica NEXO-E4: Gestión de entidades core (continuación)

#### 🟢 NEXO-20 — Como analista, quiero registrar vehículos para asociarlos a sucesos delictivos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Isaac
- **Descripción:** CRUD completo de Vehículos.
- **Criterios de aceptación:**
  - [ ] CRUD completo en `/api/v1/vehiculos`
  - [ ] Estado del vehículo (NORMAL, ROBADO, RECUPERADO, APOYO)
  - [ ] Endpoint `PATCH /vehiculos/{id}/estado`
  - [ ] Validación de placa única
  - [ ] Asociación con propietario (Persona)

#### 🟢 NEXO-21 — Como analista, quiero registrar ubicaciones georreferenciadas para mapear el territorio
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** John
- **Descripción:** CRUD de ubicaciones con coordenadas.
- **Criterios de aceptación:**
  - [ ] CRUD en `/api/v1/ubicaciones`
  - [ ] Validación de latitud (-90 a 90) y longitud (-180 a 180)
  - [ ] Tipo de ubicación (TALLER, GALPON, DOMICILIO, etc.)
  - [ ] Campo `nodoSospechoso` (boolean)
  - [ ] Endpoint para listar nodos sospechosos

#### 🟢 NEXO-22 — Como analista, quiero registrar sucesos delictivos para tener histórico de casos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** CRUD de sucesos con todas sus relaciones.
- **Criterios de aceptación:**
  - [ ] CRUD en `/api/v1/sucesos`
  - [ ] Tipos: ROBO_VEHICULO, DESAPARICION, AVISTAMIENTO_VEHICULO, TRANSACCION_SOSPECHOSA
  - [ ] FKs opcionales a Vehiculo, Victima (Persona), Ubicacion
  - [ ] Campo modusOperandi de texto libre
  - [ ] Filtrado por tipo, fecha, ubicación

#### 🟢 NEXO-23 — Como analista, quiero registrar avistamientos de vehículos para rastrear movimientos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Valeria
- **Descripción:** Sistema de avistamientos georreferenciados.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/vehiculos/{id}/avistamientos`
  - [ ] Vínculo a Ubicación
  - [ ] Fecha y hora del avistamiento
  - [ ] Fuente (cámara CCTV, denuncia, patrullaje)
  - [ ] Listar avistamientos de un vehículo ordenados por fecha

#### 🟢 NEXO-24 — Como analista, quiero buscar intermediarios entre dos personas para identificar nexos ocultos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Santiago
- **Descripción:** Algoritmo BFS sobre el grafo de relaciones sociales.
- **Criterios de aceptación:**
  - [ ] Endpoint `GET /api/v1/personas/intermediarios?desde=X&hasta=Y`
  - [ ] BFS con profundidad máxima 2
  - [ ] Retorna lista de caminos posibles
  - [ ] Si no hay camino, retorna lista vacía

#### 🔵 NEXO-25 — Implementar exportación a CSV de cada entidad
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Isaac
- **Descripción:** Endpoints que generan CSVs para descargar.
- **Criterios de aceptación:**
  - [ ] CSV de Personas
  - [ ] CSV de Vehículos
  - [ ] CSV de Ubicaciones
  - [ ] CSV de Sucesos
  - [ ] Encoding UTF-8 con BOM para Excel

#### 🔴 NEXO-26 — [BUG] LazyInitializationException al serializar entidades con FK
- **Tipo:** Bug
- **Story Points:** 3
- **Asignado a:** Manuel
- **Descripción:** Error 500 cuando Jackson intenta serializar relaciones LAZY.
- **Criterios de aceptación:**
  - [ ] Cambiar `FetchType.LAZY` a `EAGER` en todas las @ManyToOne
  - [ ] Agregar `@JsonIgnoreProperties` para evitar loops
  - [ ] Configurar `spring.jpa.open-in-view=true`
  - [ ] Verificar que `/sucesos` y `/vehiculos` funcionan

#### 🔴 NEXO-27 — [BUG] Login del usuario admin no funciona por hash corrupto
- **Tipo:** Bug
- **Story Points:** 1
- **Asignado a:** John
- **Descripción:** El usuario `admin` tiene un hash inválido en BD, hay que recrearlo o usar otro.
- **Criterios de aceptación:**
  - [ ] Crear usuario `admin2` con hash correcto
  - [ ] Documentar credenciales por defecto
  - [ ] Script SQL para recrear si se reinicializa la BD

---

## 7. Sprint 4
### Frontend Core + Motor Red Thread

**Período:** Semanas 7-8
**Objetivo del sprint:** Construir la base del frontend (login, navegación, dashboard) y empezar el motor Red Thread con sus primeras 3 reglas.

**Entregables:**
- Frontend base con login y navegación
- CRUD de Personas, Vehículos, Ubicaciones (UI)
- Motor Red Thread con 3 reglas operativas
- Dashboard con métricas

**Story Points:** 38

---

### 🟣 Épica NEXO-E5: Frontend operativo (UI base)

#### 🔵 NEXO-28 — Configurar proyecto React + TypeScript + Vite
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** John
- **Descripción:** Setup inicial del frontend.
- **Criterios de aceptación:**
  - [ ] Proyecto creado con Vite
  - [ ] TypeScript configurado en strict mode
  - [ ] React Router instalado
  - [ ] Axios configurado con interceptors para JWT
  - [ ] Estructura de carpetas: `pages/`, `components/`, `services/`, `types/`

#### 🟢 NEXO-29 — Como usuario, quiero una pantalla de login para acceder al sistema
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Santiago
- **Descripción:** Página de login con manejo de JWT.
- **Criterios de aceptación:**
  - [ ] Formulario con username y password
  - [ ] Llamada a `POST /auth/login`
  - [ ] Guardar JWT en localStorage
  - [ ] Redirigir a `/` si exitoso
  - [ ] Mensaje de error si falla

#### 🟢 NEXO-30 — Como usuario, quiero un sidebar con navegación para acceder a todos los módulos
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Valeria
- **Descripción:** Componente de navegación lateral.
- **Criterios de aceptación:**
  - [ ] Links a todos los módulos (Personas, Vehículos, etc.)
  - [ ] Resaltado del módulo activo
  - [ ] User card al final con logout
  - [ ] Botón "Nueva Investigación"

#### 🟢 NEXO-31 — Como usuario, quiero un dashboard con métricas para ver el estado del sistema
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Isaac
- **Descripción:** Página principal con tarjetas de stats y motor.
- **Criterios de aceptación:**
  - [ ] Tarjetas de Personas, Vehículos, Robados, Sucesos, Ubicaciones, Alertas
  - [ ] Click en tarjeta navega al módulo
  - [ ] Botón "Ejecutar Motor Completo"
  - [ ] Sección de últimas alertas pendientes
  - [ ] Indicador de nivel de amenaza

#### 🟢 NEXO-32 — Como analista, quiero gestionar personas desde la UI con tabla y formulario
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Página `/personas` con CRUD completo.
- **Criterios de aceptación:**
  - [ ] Tabla con paginación
  - [ ] Filtros por rol y texto
  - [ ] Botón crear con modal
  - [ ] Botones editar y eliminar por fila
  - [ ] Modal de detalle con relaciones
  - [ ] Confirmación antes de eliminar

#### 🟢 NEXO-33 — Como analista, quiero gestionar vehículos desde la UI con cambio de estado
- **Tipo:** Story
- **Story Points:** 4
- **Asignado a:** Santiago
- **Descripción:** Página `/vehiculos` con CRUD y cambio de estado.
- **Criterios de aceptación:**
  - [ ] Tabla con badges de estado
  - [ ] Cambio de estado desde dropdown
  - [ ] Modal de detalle con avistamientos
  - [ ] Mapa con vehículos robados

#### 🟢 NEXO-34 — Como analista, quiero gestionar ubicaciones desde la UI con picker de mapa
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** John
- **Descripción:** Página `/ubicaciones` con Leaflet integrado.
- **Criterios de aceptación:**
  - [ ] Tabla con tipo y badge de sospechoso
  - [ ] Mapa Leaflet con todas las ubicaciones
  - [ ] Picker de coordenadas en modal de creación
  - [ ] Toggle de "nodo sospechoso"

---

### 🟣 Épica NEXO-E6: Motor Red Thread (parcial — primeras 3 reglas)

#### 🔵 NEXO-35 — Diseñar arquitectura del motor con interfaz `ReglaVinculo`
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Manuel
- **Descripción:** Definir el contrato común para todas las reglas.
- **Criterios de aceptación:**
  - [ ] Interfaz `ReglaVinculo` con `nombre()` y `ejecutar()`
  - [ ] Record `ResultadoRegla(nombre, vinculos, alertas)`
  - [ ] `RedThreadEngineService` orquesta las reglas

#### 🟢 NEXO-36 — Como analista, quiero detectar nodos logísticos automáticamente para identificar deshuesaderos
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Implementar regla de nodo logístico.
- **Criterios de aceptación:**
  - [ ] Cargar vehículos robados con últimas ubicaciones
  - [ ] Aplicar fórmula de Haversine
  - [ ] Si 3+ vehículos en radio 500m en 72h → vínculo + alerta
  - [ ] Alerta tipo `NODO_LOGISTICO` nivel CRITICO
  - [ ] Configurable vía `application.properties`

#### 🟢 NEXO-37 — Como analista, quiero detectar vehículos escolta para identificar redes de apoyo
- **Tipo:** Story
- **Story Points:** 4
- **Asignado a:** Isaac
- **Descripción:** Implementar regla de vehículo escolta.
- **Criterios de aceptación:**
  - [ ] Detectar vehículo no robado que aparece junto a robados
  - [ ] Mínimo 3 coincidencias en ventana de 2 minutos
  - [ ] Alerta tipo `VEHICULO_APOYO` nivel ALTO

---

## 8. Sprint 5
### Alertas, Grafo y Búsqueda

**Período:** Semanas 9-10
**Objetivo del sprint:** Completar el motor con las reglas restantes, implementar el grafo interactivo, sistema de alertas funcional y búsqueda global.

**Entregables:**
- Motor con 4 reglas (Modus Operandi y Círculo)
- Sistema de alertas completo (UI + backend)
- Grafo interactivo con Cytoscape
- Búsqueda global funcional

**Story Points:** 36

---

### 🟣 Épica NEXO-E6: Motor Red Thread (continuación)

#### 🟢 NEXO-38 — Como analista, quiero detectar intermediarios automáticamente para revelar redes ocultas
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Santiago
- **Descripción:** Implementar regla de círculo de confianza.
- **Criterios de aceptación:**
  - [ ] BFS sobre grafo de relaciones, profundidad 2
  - [ ] Crear vínculo `PERSONA ↔ PERSONA` con score 0.75
  - [ ] Alerta tipo `INTERMEDIARIO` nivel MEDIO

#### 🟢 NEXO-39 — Como analista, quiero detectar sucesos con modus operandi similar para conectar casos
- **Tipo:** Story
- **Story Points:** 4
- **Asignado a:** Valeria
- **Descripción:** Implementar regla de similitud de modus operandi.
- **Criterios de aceptación:**
  - [ ] Tokenizar campo `modusOperandi`
  - [ ] Calcular similitud Jaccard
  - [ ] Si similitud ≥ 0.75 → vínculo + alerta
  - [ ] Alerta tipo `MODUS_OPERANDI` nivel ALTO

#### 🔵 NEXO-40 — Implementar prevención de duplicados en vínculos
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Manuel
- **Descripción:** Evitar que el motor genere vínculos repetidos al re-ejecutarse.
- **Criterios de aceptación:**
  - [ ] Query `findDuplicado` en `VinculoRepository`
  - [ ] Verificación antes de cada `save()`
  - [ ] Test manual: ejecutar motor 2 veces seguidas

#### 🔵 NEXO-41 — Implementar configuración de umbrales del motor
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** John
- **Descripción:** Permitir ajustar parámetros del motor desde la UI.
- **Criterios de aceptación:**
  - [ ] Valores configurables en `application.properties`
  - [ ] UI con sliders en modal de configuración
  - [ ] Valores: radio nodo logístico, mínimo vehículos, ventana, etc.
  - [ ] Persistir preferencias en localStorage del frontend

---

### 🟣 Épica NEXO-E7: Sistema de alertas y grafo de vínculos

#### 🟢 NEXO-42 — Como analista, quiero ver alertas generadas por el motor para tomar acción
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Isaac
- **Descripción:** Página `/alertas` con listado y filtros.
- **Criterios de aceptación:**
  - [ ] Tarjetas con ID, tipo, riesgo, estado
  - [ ] Filtros por nivel y estado
  - [ ] Watermark grande del tipo en fondo
  - [ ] Stats panel lateral con conteos

#### 🟢 NEXO-43 — Como analista, quiero cambiar el estado de una alerta para gestionar mi flujo de trabajo
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Santiago
- **Descripción:** Botones contextuales según el estado actual.
- **Criterios de aceptación:**
  - [ ] PENDIENTE → "Analizar" cambia a EN_REVISION
  - [ ] PENDIENTE → "Desplegar" (si es CRÍTICA) cambia a CONFIRMADA
  - [ ] EN_REVISION → "Confirmar" o "Descartar"
  - [ ] Endpoint `PATCH /alertas/{id}/estado`

#### 🟢 NEXO-44 — Como analista, quiero visualizar todos los vínculos en un grafo interactivo
- **Tipo:** Story
- **Story Points:** 8
- **Asignado a:** Manuel + Valeria
- **Descripción:** Visualización con Cytoscape.js.
- **Criterios de aceptación:**
  - [ ] Endpoint `GET /grafo/completo` que devuelve formato Cytoscape
  - [ ] Nodos con colores por tipo
  - [ ] Aristas rojas para vínculos del motor
  - [ ] Zoom con scroll, drag para reposicionar
  - [ ] Click en nodo muestra panel lateral
  - [ ] Filtros para mostrar/ocultar tipos

#### 🟢 NEXO-45 — Como usuario, quiero buscar entidades desde la barra superior para encontrar rápido lo que necesito
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** John
- **Descripción:** Búsqueda global en TopBar.
- **Criterios de aceptación:**
  - [ ] Input con debounce de 300ms
  - [ ] Búsqueda paralela en personas, vehículos, ubicaciones, sucesos
  - [ ] Dropdown con resultados agrupados
  - [ ] Click en resultado navega al módulo correspondiente

#### 🔵 NEXO-46 — Implementar tema oscuro/claro configurable
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Valeria
- **Descripción:** Sistema de temas con CSS variables.
- **Criterios de aceptación:**
  - [ ] Variables CSS para colores
  - [ ] Toggle en modal de configuración
  - [ ] Persistir preferencia en localStorage
  - [ ] Aplicar tema al cargar la app

#### 🔴 NEXO-47 — [BUG] El picker de mapa muestra mapa flotando encima
- **Tipo:** Bug
- **Story Points:** 2
- **Asignado a:** John
- **Descripción:** Problema de z-index en modal de Ubicaciones.
- **Criterios de aceptación:**
  - [ ] Mapa del modal se muestra correctamente
  - [ ] Sin elementos flotantes superpuestos
  - [ ] Funciona en Chrome, Edge y Firefox

---

## 9. Sprint 6
### Personas Desaparecidas + IA

**Período:** Semanas 11-12
**Objetivo del sprint:** Implementar el módulo completo de Personas Desaparecidas con galería fotográfica, agregar la 5ta regla del motor (cluster de desapariciones), e integrar el asistente IA Claude con sus 4 casos de uso.

**Entregables:**
- Módulo de Personas Desaparecidas (backend + frontend)
- 5ta regla del motor: Cluster de Desapariciones
- Asistente IA con 4 casos de uso operativos
- Página de chat conversacional con Claude

**Story Points:** 34

---

### 🟣 Épica NEXO-E8: Personas desaparecidas con galería

#### 🟢 NEXO-48 — Como analista, quiero registrar casos de personas desaparecidas con dossier completo
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Backend completo del módulo.
- **Criterios de aceptación:**
  - [ ] Entidad `PersonaDesaparecida` con todos los campos
  - [ ] Estados: BUSCADA, ENCONTRADA_VIVA, ENCONTRADA_FALLECIDA, ARCHIVADA
  - [ ] Prioridades: CRITICA, ALTA, MEDIA, BAJA
  - [ ] CRUD completo en `/api/v1/desaparecidas`
  - [ ] Endpoint de estadísticas

#### 🟢 NEXO-49 — Como analista, quiero subir una foto de la persona desaparecida para tener registro visual
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** John
- **Descripción:** Sistema de upload de fotos.
- **Criterios de aceptación:**
  - [ ] `FileStorageService` con validación de tipo y tamaño
  - [ ] Endpoint `POST /desaparecidas/{id}/foto` (multipart)
  - [ ] Guardado en `/uploads/desaparecidas/`
  - [ ] Servidor sirve archivos vía `/files/desaparecidas/{nombre}`
  - [ ] Eliminación de foto anterior al subir una nueva

#### 🟢 NEXO-50 — Como analista, quiero un formulario completo para registrar circunstancias de la desaparición
- **Tipo:** Story
- **Story Points:** 4
- **Asignado a:** Santiago
- **Descripción:** Formulario con 4 pestañas en el frontend.
- **Criterios de aceptación:**
  - [ ] Pestaña 1: Identificación (datos básicos + foto)
  - [ ] Pestaña 2: Datos físicos (estatura, peso, etc.)
  - [ ] Pestaña 3: Circunstancias (fecha, ubicación, descripción)
  - [ ] Pestaña 4: Reportante (nombre, teléfono, relación)
  - [ ] Validaciones de campos obligatorios

#### 🟢 NEXO-51 — Como analista, quiero ver galería de personas desaparecidas con foto y datos clave
- **Tipo:** Story
- **Story Points:** 4
- **Asignado a:** Valeria
- **Descripción:** Vista de galería + tabla.
- **Criterios de aceptación:**
  - [ ] Cards con foto + banner "BUSCADA · X DÍAS"
  - [ ] Badge de prioridad con color
  - [ ] Toggle entre vista galería y vista tabla
  - [ ] Mapa con últimas ubicaciones (markers púrpura)
  - [ ] Filtros por estado, prioridad, texto

#### 🟢 NEXO-52 — Como analista, quiero detectar clusters de desapariciones automáticamente
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Isaac
- **Descripción:** 5ta regla del motor.
- **Criterios de aceptación:**
  - [ ] Detectar 3+ desapariciones en radio 1.5km en 30 días
  - [ ] Crear vínculos entre desaparecidas del cluster
  - [ ] Alerta `CLUSTER_DESAPARICIONES` nivel CRITICO
  - [ ] Detectar desaparición cerca de nodo sospechoso (radio 1km)
  - [ ] Alerta `DESAPARICION_NODO_SOSPECHOSO` nivel ALTO

---

### 🟣 Épica NEXO-E9: Integración con IA Claude

#### 🔵 NEXO-53 — Investigar integración con API de Anthropic
- **Tipo:** Spike
- **Story Points:** 2
- **Asignado a:** John
- **Descripción:** Investigar cómo integrar el modelo Claude.
- **Criterios de aceptación:**
  - [ ] Documentación de la API leída
  - [ ] Modelo seleccionado (claude-sonnet-4.5)
  - [ ] Estructura de request/response documentada
  - [ ] POC funcionando con curl

#### 🔵 NEXO-54 — Implementar `ClaudeClient` para comunicarse con la API
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** John
- **Descripción:** Cliente HTTP nativo para Anthropic.
- **Criterios de aceptación:**
  - [ ] Clase `ClaudeClient` con método `preguntar()`
  - [ ] Headers correctos (x-api-key, anthropic-version)
  - [ ] Timeout configurable (60s)
  - [ ] Manejo de errores HTTP (403, 429, 500)
  - [ ] API key vía variable de entorno

#### 🟢 NEXO-55 — Como analista, quiero un asistente IA conversacional para hacer consultas sobre el sistema
- **Tipo:** Story
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Chat con Claude con contexto del sistema.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /api/v1/ia/chat`
  - [ ] Genera contexto dinámico (counts, alertas críticas)
  - [ ] Mantiene historial de conversación
  - [ ] Página `/asistente-ia` tipo ChatGPT
  - [ ] Sugerencias iniciales de preguntas

#### 🟢 NEXO-56 — Como analista, quiero predecir zonas de búsqueda con IA para una persona desaparecida
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Santiago
- **Descripción:** IA sugiere zonas geográficas prioritarias.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /ia/zonas-busqueda/{id}`
  - [ ] Prompt contextual con datos de la persona
  - [ ] Respuesta con 3-5 zonas + justificación
  - [ ] Guardado en campo `zonasBusquedaIA`
  - [ ] Botón en modal de Persona Desaparecida

#### 🟢 NEXO-57 — Como analista, quiero generar reportes ejecutivos automáticos con IA
- **Tipo:** Story
- **Story Points:** 3
- **Asignado a:** Valeria
- **Descripción:** IA genera reportes profesionales de cualquier entidad.
- **Criterios de aceptación:**
  - [ ] Endpoint `POST /ia/reporte/{tipo}/{id}`
  - [ ] Soporta tipos: desaparecida, suceso, alerta
  - [ ] Estructura: Resumen, Datos, Análisis, Recomendaciones
  - [ ] Componente `BotonIA` reutilizable
  - [ ] Copiar al portapapeles

#### 🔵 NEXO-58 — Configurar API key de Anthropic vía variable de entorno
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** John
- **Descripción:** Manejo seguro de credenciales.
- **Criterios de aceptación:**
  - [ ] `application.properties` lee `${ANTHROPIC_API_KEY:}`
  - [ ] Documentar cómo configurar en Windows y Linux
  - [ ] `.gitignore` excluye archivos con keys
  - [ ] Endpoint `/ia/estado` para verificar configuración

---

## 10. Sprint 7
### Testing, Documentación y Pulido

**Período:** Semanas 13-14
**Objetivo del sprint:** Estabilizar el sistema, redactar toda la documentación, ejecutar plan de pruebas exhaustivo y preparar la presentación final.

**Entregables:**
- Plan de pruebas ejecutado y documentado
- Toda la documentación técnica completa
- Bugs críticos resueltos
- Sistema desplegado en demo
- Presentación final preparada

**Story Points:** 26

---

### 🟣 Épica NEXO-E10: Testing, documentación y pulido

#### 🔵 NEXO-59 — Redactar plan de pruebas detallado
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Valeria
- **Descripción:** Documento con casos de prueba para cada módulo.
- **Criterios de aceptación:**
  - [ ] 13 módulos cubiertos
  - [ ] Casos positivos y negativos
  - [ ] Pasos detallados para reproducir
  - [ ] Resultado esperado documentado

#### 🔵 NEXO-60 — Ejecutar plan de pruebas completo
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** Equipo completo
- **Descripción:** Probar manualmente todo el sistema.
- **Criterios de aceptación:**
  - [ ] Todos los módulos probados
  - [ ] Bugs encontrados creados como issues
  - [ ] Capturas de pantalla guardadas
  - [ ] Reporte de resultados redactado

#### 🔴 NEXO-61 — [BUG] Errores de Cytoscape con StrictMode de React
- **Tipo:** Bug
- **Story Points:** 2
- **Asignado a:** Manuel
- **Descripción:** Warnings en consola al renderizar el grafo.
- **Criterios de aceptación:**
  - [ ] Investigar causa
  - [ ] Documentar como "no crítico" si aplica
  - [ ] Suprimir warnings si es posible

#### 🔵 NEXO-62 — Redactar Informe Técnico del sistema
- **Tipo:** Task
- **Story Points:** 5
- **Asignado a:** Manuel
- **Descripción:** Documento técnico completo.
- **Criterios de aceptación:**
  - [ ] Arquitectura descrita
  - [ ] Stack tecnológico justificado
  - [ ] Módulos del sistema explicados
  - [ ] Decisiones arquitectónicas documentadas
  - [ ] Limitaciones conocidas listadas
  - [ ] Roadmap futuro

#### 🔵 NEXO-63 — Generar diagramas técnicos finales
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Isaac
- **Descripción:** Diagramas en Mermaid para GitHub.
- **Criterios de aceptación:**
  - [ ] Diagrama de arquitectura
  - [ ] Diagrama ER de BD
  - [ ] Diagrama de casos de uso
  - [ ] Diagramas de secuencia (login, motor, IA)
  - [ ] Diagramas de estado (alerta, desaparecida)

#### 🔵 NEXO-64 — Redactar README profesional del repositorio
- **Tipo:** Task
- **Story Points:** 2
- **Asignado a:** Santiago
- **Descripción:** README atractivo con badges y guía rápida.
- **Criterios de aceptación:**
  - [ ] Badges de tecnologías
  - [ ] Descripción del proyecto
  - [ ] Instrucciones de instalación
  - [ ] Estructura del proyecto
  - [ ] Links a documentación adicional

#### 🔵 NEXO-65 — Subir el proyecto al repositorio oficial gradualmente
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Manuel + John
- **Descripción:** Migrar el proyecto al repo del profesor con cronograma natural.
- **Criterios de aceptación:**
  - [ ] Branch `amarillo` creado en repo oficial
  - [ ] Commits divididos según cronograma de sprints
  - [ ] Mensajes de commit con Conventional Commits
  - [ ] Documentación actualizada por sprint

#### 🔵 NEXO-66 — Preparar presentación final del proyecto
- **Tipo:** Task
- **Story Points:** 3
- **Asignado a:** Equipo completo
- **Descripción:** Slides + demo en vivo + script de presentación.
- **Criterios de aceptación:**
  - [ ] Slides con resumen ejecutivo
  - [ ] Demo guiada del escenario
  - [ ] Cada miembro tiene su parte definida
  - [ ] Ensayo completo realizado

---

## 11. Resumen total de Story Points

### Por sprint

| Sprint | Período | Story Points | Foco |
|---|---|---|---|
| Sprint 1 | Semanas 1-2 | 28 | Setup, análisis, factibilidad |
| Sprint 2 | Semanas 3-4 | 35 | Diseño BD, autenticación, Personas |
| Sprint 3 | Semanas 5-6 | 32 | Vehículos, Ubicaciones, Sucesos |
| Sprint 4 | Semanas 7-8 | 38 | Frontend base, motor (parte 1) |
| Sprint 5 | Semanas 9-10 | 36 | Motor (parte 2), alertas, grafo |
| Sprint 6 | Semanas 11-12 | 34 | Desaparecidas, IA |
| Sprint 7 | Semanas 13-14 | 26 | Testing, docs, pulido |
| **TOTAL** | **14 semanas** | **229** | **Sistema completo** |

### Por persona (estimado)

| Persona | Story Points asignados |
|---|---|
| Manuel Rodríguez | 56 |
| John Salazar | 47 |
| Santiago Ramírez | 45 |
| Valeria García | 42 |
| Isaac Carreño | 39 |

### Por épica

| Épica | Story Points | % del total |
|---|---|---|
| NEXO-E1: Setup | 28 | 12% |
| NEXO-E2: Datos | 21 | 9% |
| NEXO-E3: Auth | 16 | 7% |
| NEXO-E4: CRUDs | 42 | 18% |
| NEXO-E5: UI base | 24 | 10% |
| NEXO-E6: Motor | 34 | 15% |
| NEXO-E7: Alertas/Grafo | 21 | 9% |
| NEXO-E8: Desaparecidas | 18 | 8% |
| NEXO-E9: IA | 16 | 7% |
| NEXO-E10: Testing/Docs | 26 | 11% |

### Distribución por tipo de issue

| Tipo | Cantidad | Story Points |
|---|---|---|
| 🟢 Stories | 32 | 162 |
| 🔵 Tasks | 24 | 56 |
| 🔴 Bugs | 6 | 12 |
| 🟡 Spikes | 3 | 9 |
| **Total** | **65 issues** | **239** |

---

## 12. Reglas de gestión del backlog

### Priorización

Usamos la matriz **MoSCoW**:

- **Must have** (M): imprescindible para el MVP
- **Should have** (S): importante pero puede esperar
- **Could have** (C): deseable, bonus
- **Won't have** (W): fuera de alcance

### Definition of Ready

Una historia está "lista" para entrar a un sprint cuando:

- [ ] Tiene criterios de aceptación claros
- [ ] Está estimada en story points
- [ ] No tiene dependencias bloqueantes
- [ ] El equipo entendió la historia

### Definition of Done

Una historia está "hecha" cuando:

- [ ] El código está mergeado a `amarillo`
- [ ] Pasó code review
- [ ] Funciona localmente
- [ ] Tiene documentación básica
- [ ] El Product Owner aprobó la demo

---

*Backlog elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*