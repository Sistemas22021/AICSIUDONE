# Informe Técnico
## Nexo Criminal — Sistema de Inteligencia de Precisión

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo
**Versión:** 1.0
**Fecha:** Agosto 2026

---

## Tabla de Contenido

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Stack Tecnológico](#3-stack-tecnológico)
4. [Módulos del Sistema](#4-módulos-del-sistema)
5. [Motor Red Thread](#5-motor-red-thread)
6. [Integración con Inteligencia Artificial](#6-integración-con-inteligencia-artificial)
7. [Modelo de Datos](#7-modelo-de-datos)
8. [Seguridad](#8-seguridad)
9. [Decisiones Arquitectónicas](#9-decisiones-arquitectónicas)
10. [Limitaciones Conocidas](#10-limitaciones-conocidas)
11. [Roadmap Futuro](#11-roadmap-futuro)

---

## 1. Resumen Ejecutivo

**Nexo Criminal** es un sistema web de inteligencia criminal diseñado para apoyar a las unidades policiales en la correlación automatizada de entidades operativas (personas, vehículos, ubicaciones y sucesos). El sistema identifica patrones criminales no evidentes mediante un motor de reglas heurísticas denominado **Red Thread Engine** y se complementa con un asistente conversacional basado en inteligencia artificial generativa (Anthropic Claude).

El sistema se desarrolló con arquitectura de tres capas (cliente-servidor-base de datos), backend en Java + Spring Boot, frontend en React + TypeScript y persistencia en PostgreSQL con extensión PostGIS. Se utilizó autenticación stateless mediante JWT, comunicación HTTP/REST entre capas y visualización avanzada de grafos con Cytoscape.js.

El sistema entrega los siguientes módulos funcionales:

| Módulo | Estado |
|---|---|
| Autenticación y gestión de sesiones | ✅ Implementado |
| Gestión de personas (CRUD + relaciones sociales) | ✅ Implementado |
| Gestión de vehículos (CRUD + avistamientos) | ✅ Implementado |
| Gestión de ubicaciones (CRUD + georreferenciación) | ✅ Implementado |
| Gestión de sucesos | ✅ Implementado |
| Personas desaparecidas (CRUD + galería fotográfica) | ✅ Implementado |
| Motor Red Thread (5 reglas heurísticas) | ✅ Implementado |
| Sistema de alertas | ✅ Implementado |
| Visualización de grafo interactivo | ✅ Implementado |
| Asistente IA Claude | ✅ Implementado |
| Dashboard analítico | ✅ Implementado |
| Búsqueda global | ✅ Implementado |

---

## 2. Arquitectura del Sistema

### Vista general (alto nivel)

El sistema sigue una arquitectura cliente-servidor de tres capas:

```
┌─────────────────────────────────────────────┐
│              CAPA DE PRESENTACIÓN           │
│   React 18 + TypeScript + Vite              │
│   (SPA — Single Page Application)           │
└──────────────────┬──────────────────────────┘
                   │ HTTP/REST + JWT
                   │
┌──────────────────▼──────────────────────────┐
│              CAPA DE APLICACIÓN             │
│   Spring Boot 3.2.5 + Spring Security       │
│   ┌───────────────────────────────────┐     │
│   │ Controllers (REST endpoints)      │     │
│   ├───────────────────────────────────┤     │
│   │ Services (lógica de negocio)      │     │
│   ├───────────────────────────────────┤     │
│   │ Engine (motor de reglas)          │     │
│   ├───────────────────────────────────┤     │
│   │ Repositories (Spring Data JPA)    │     │
│   └───────────────────────────────────┘     │
│                                             │
│   ┌───────────────────────────────────┐     │
│   │ Cliente Anthropic (HTTP nativo)   │     │
│   └───────────────────┬───────────────┘     │
└───────────────────────┼─────────────────────┘
            │ JDBC      │ HTTPS
            │           │
┌───────────▼───┐  ┌────▼──────────────────────┐
│  PostgreSQL   │  │  api.anthropic.com        │
│  + PostGIS    │  │  (Servicio externo IA)    │
└───────────────┘  └───────────────────────────┘
```

### Capa de Presentación (Frontend)

Aplicación de página única (SPA) construida con React 18 y TypeScript. Se comunica con el backend mediante HTTP usando Axios. Mantiene el estado de sesión en `localStorage` (JWT) y el estado de UI en React Context.

**Características principales:**
- Routing declarativo con React Router 6.
- Sistema de temas (oscuro/claro) persistente.
- Componentes reutilizables (Modal, Tabla, Paginación, Filtros).
- Integración con Leaflet para mapas y Cytoscape para grafos.
- Material Symbols para iconografía consistente.

### Capa de Aplicación (Backend)

Servicio REST construido con Spring Boot 3.2.5. Sigue el patrón **Controller → Service → Repository**. Expone endpoints bajo el prefijo `/api/v1/`.

**Estructura de paquetes:**

```
com.nexocriminal/
├── NexoCriminalApplication.java
├── config/
├── domain/
│   ├── alerta/
│   ├── desaparecida/
│   ├── persona/
│   ├── relacion/
│   ├── suceso/
│   ├── ubicacion/
│   ├── usuario/
│   ├── vehiculo/
│   └── vinculo/
├── engine/
│   ├── RedThreadEngineService.java
│   ├── ReglaNodoLogistico.java
│   ├── ReglaEscoltaVehicular.java
│   ├── ReglaCirculoConfianza.java
│   ├── ReglaSimilitudModusOperandi.java
│   └── ReglaClusterDesapariciones.java
├── files/
│   ├── FileStorageService.java
│   └── FileStorageConfig.java
├── ia/
│   ├── ClaudeClient.java
│   ├── IAService.java
│   ├── IAController.java
│   └── dto/
└── security/
    ├── SecurityConfig.java
    ├── JwtAuthFilter.java
    └── JwtService.java
```

### Capa de Datos

PostgreSQL 14+ con extensión **PostGIS** para soportar consultas geoespaciales. La gestión del esquema se realiza automáticamente mediante Hibernate (`ddl-auto=update`) en desarrollo. Para producción se recomienda migrar a **Flyway** o **Liquibase** y usar `validate`.

### Servicio externo

El sistema se integra con **api.anthropic.com** (modelo Claude Sonnet 4.5) para los casos de uso de inteligencia artificial. La comunicación se realiza vía HTTPS usando el cliente HTTP nativo de Java 17.

---

## 3. Stack Tecnológico

### Backend

| Tecnología | Versión | Función |
|---|---|---|
| Java | 17 (LTS) | Lenguaje base |
| Spring Boot | 3.2.5 | Framework de aplicación |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Acceso a datos |
| Hibernate | 6.4 | ORM |
| PostgreSQL Driver | 42.x | Conexión a PostgreSQL |
| jjwt | 0.12.x | Generación y validación de JWT |
| Lombok | 1.18 | Reducción de boilerplate |
| Maven | 3.9 | Gestor de dependencias |

### Frontend

| Tecnología | Versión | Función |
|---|---|---|
| React | 18.2 | Librería UI |
| TypeScript | 5.x | Tipado estático |
| Vite | 5.x | Build tool |
| React Router | 6.x | Routing |
| Axios | 1.x | Cliente HTTP |
| Leaflet | 1.9 | Mapas interactivos |
| react-leaflet | 4.2 | Bindings React para Leaflet |
| Cytoscape.js | 3.x | Visualización de grafos |
| Material Symbols | latest | Iconografía |

### Base de datos

| Tecnología | Versión | Función |
|---|---|---|
| PostgreSQL | 14+ | Motor de base de datos relacional |
| PostGIS | 3.x | Extensión geoespacial |

### Servicios externos

| Servicio | Modelo / Versión | Función |
|---|---|---|
| Anthropic API | claude-sonnet-4.5 | Asistente IA |

---

## 4. Módulos del Sistema

### 4.1 Autenticación y Sesiones

- Registro de usuarios con BCrypt para hash de contraseñas (factor de costo 10).
- Login que devuelve un **JSON Web Token (JWT)** con expiración configurable.
- Filtro `JwtAuthFilter` que intercepta cada request y valida el token.
- Soporte para cambio de contraseña.
- Roles soportados: `ADMIN`, `ANALISTA`.

**Endpoints clave:**
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/registrar`
- `POST /api/v1/auth/cambiar-password`

### 4.2 Personas

CRUD completo de personas con los siguientes atributos: documento (único), nombre, apellido, alias, fecha de nacimiento, género, rol (víctima, sospechoso, testigo, propietario, intermediario), teléfono, dirección, observaciones.

**Funcionalidades adicionales:**
- Creación de relaciones sociales entre personas (con tipo y peso).
- Búsqueda de intermediarios entre dos personas mediante algoritmo BFS sobre el grafo de relaciones.
- Exportación a CSV.

### 4.3 Vehículos

CRUD de vehículos con: placa (única), marca, modelo, año, color, propietario, estado (`NORMAL`, `ROBADO`, `RECUPERADO`, `APOYO`).

**Funcionalidades adicionales:**
- Registro de avistamientos georreferenciados (vínculo a Ubicación con fecha/hora y fuente).
- Cambio rápido de estado desde la tabla.
- Mapa con todos los vehículos robados.

### 4.4 Ubicaciones

CRUD de ubicaciones con georreferenciación. Tipos soportados: `TALLER`, `GALPON`, `TERRENO_BALDIO`, `DOMICILIO`, `CAJERO`, `TRANSPORTE_PUBLICO`, `COMERCIO`, `OTRO`.

**Funcionalidades destacadas:**
- Picker de coordenadas en mapa interactivo.
- Marcado de ubicaciones como **nodo sospechoso** (input para el motor).
- Visualización de todas las ubicaciones en mapa táctico.

### 4.5 Sucesos

CRUD de sucesos con tipos: `ROBO_VEHICULO`, `DESAPARICION`, `AVISTAMIENTO_VEHICULO`, `TRANSACCION_SOSPECHOSA`, `OTRO`.

Cada suceso vincula opcionalmente: vehículo, víctima, ubicación, fecha y hora, modus operandi (texto libre), descripción.

**Filtros avanzados:**
- Por tipo de suceso.
- Por vehículo.
- Por víctima.
- Por ubicación.
- Por rango de fechas.

### 4.6 Personas Desaparecidas

Módulo dedicado con dossier completo de cada caso:

- **Datos de identificación**: documento, nombre, apellido, alias, género.
- **Descripción física**: estatura, peso, contextura, color de cabello, color de ojos, señas particulares.
- **Circunstancias**: fecha y hora de desaparición, última ubicación conocida, ropa que vestía, descripción del contexto.
- **Datos del reportante**: nombre, teléfono, relación con la persona.
- **Foto**: subida vía multipart, guardada en `/uploads/desaparecidas/`, servida vía endpoint `/files/desaparecidas/{nombre}`.
- **Estado del caso**: `BUSCADA`, `ENCONTRADA_VIVA`, `ENCONTRADA_FALLECIDA`, `ARCHIVADA`.
- **Prioridad**: `CRITICA`, `ALTA`, `MEDIA`, `BAJA`.

**Vistas:**
- Galería con cards (foto + datos clave).
- Tabla detallada.
- Mapa con últimas ubicaciones.

### 4.7 Sistema de Alertas

Las alertas se generan automáticamente al ejecutar el motor Red Thread.

**Atributos:**
- ID con prefijo `NX-` (ej: NX-00001).
- Tipo de alerta (uno por cada regla del motor).
- Nivel de riesgo: `CRITICO`, `ALTO`, `MEDIO`, `BAJO`.
- Estado: `PENDIENTE`, `EN_REVISION`, `CONFIRMADA`, `DESCARTADA`.
- Título y descripción detallada.
- Vínculo a la entidad relacionada (cuando aplica).

### 4.8 Grafo Red Thread

Visualización interactiva basada en **Cytoscape.js**.

- Nodos con colores por tipo (persona, vehículo, ubicación, suceso, desaparecida).
- Aristas que representan vínculos descubiertos.
- Filtros laterales: mostrar/ocultar tipos de entidad, mostrar relaciones directas vs vínculos del motor.
- Click en nodo: muestra panel lateral con detalles.
- Zoom con scroll, drag para reposicionar, layout con `cose` (force-directed).

### 4.9 Dashboard

Panel principal con métricas en tiempo real:

- Conteo de personas, vehículos, vehículos robados, sucesos, ubicaciones, alertas pendientes.
- Botón de ejecución completa del motor.
- Lista de últimas alertas pendientes.
- Estado del motor (en reposo / en ejecución).
- Nivel de amenaza global (calculado en función de alertas críticas/altas activas).

### 4.10 Búsqueda Global

Barra de búsqueda en TopBar que consulta paralelamente personas, vehículos, ubicaciones y sucesos. Devuelve resultados agrupados con navegación directa al detalle.

---

## 5. Motor Red Thread

El motor Red Thread es el componente diferencial del sistema. Su función es **descubrir vínculos no evidentes** entre entidades aplicando reglas heurísticas configurables.

### Arquitectura del motor

```
RedThreadEngineService
    │
    ├── ReglaNodoLogistico
    ├── ReglaEscoltaVehicular
    ├── ReglaCirculoConfianza
    ├── ReglaSimilitudModusOperandi
    └── ReglaClusterDesapariciones
```

Cada regla implementa la interfaz `ReglaVinculo`:

```java
public interface ReglaVinculo {
    String nombre();
    ResultadoRegla ejecutar();

    record ResultadoRegla(
        String nombre,
        List<Vinculo> vinculos,
        List<Alerta> alertas
    ) {}
}
```

El servicio `RedThreadEngineService` orquesta la ejecución secuencial de todas las reglas y consolida los resultados.

### Regla 1 — Nodo Logístico

**Hipótesis**: si varios vehículos robados convergen físicamente cerca de una misma ubicación (taller, galpón) en una ventana temporal corta, esa ubicación probablemente es un **deshuesadero o punto logístico delictivo**.

**Parámetros configurables:**
- Radio de búsqueda (default: 500 m)
- Mínimo de vehículos cercanos (default: 3)
- Ventana temporal (default: 72 horas)

**Salida:**
- Vínculos `VEHICULO ↔ UBICACION` con score 0.90.
- Alerta `NODO_LOGISTICO` con nivel **CRITICO**.

### Regla 2 — Vehículo Escolta

**Hipótesis**: si un vehículo NO robado aparece sistemáticamente en avistamientos junto a vehículos robados, es probable que actúe como **escolta o vehículo de apoyo**.

**Parámetros:**
- Mínimo de coincidencias (default: 3)
- Ventana de tiempo entre avistamientos (default: 2 minutos)

**Salida:**
- Vínculos `VEHICULO ↔ VEHICULO` con score variable.
- Alerta `VEHICULO_APOYO` con nivel **ALTO**.

### Regla 3 — Círculo de Confianza

**Hipótesis**: si dos personas (víctima y sospechoso) tienen un contacto en común a través del grafo de relaciones sociales, ese contacto es un **intermediario potencial**.

**Algoritmo:** búsqueda en anchura (BFS) sobre el grafo de relaciones, profundidad máxima 2.

**Salida:**
- Vínculos `PERSONA ↔ PERSONA` con score 0.75.
- Alerta `INTERMEDIARIO` con nivel **MEDIO**.

### Regla 4 — Similitud de Modus Operandi

**Hipótesis**: sucesos con descripciones de modus operandi similares pueden estar relacionados aunque ocurran en zonas geográficas distintas.

**Algoritmo:** cálculo de similitud Jaccard sobre tokens del campo `modusOperandi`.

**Parámetros:**
- Umbral de similitud (default: 0.75)

**Salida:**
- Vínculos `SUCESO ↔ SUCESO` con score igual a la similitud calculada.
- Alerta `MODUS_OPERANDI` con nivel **ALTO**.

### Regla 5 — Cluster de Desapariciones

**Hipótesis**: múltiples desapariciones en una zona geográfica restringida dentro de una ventana temporal corta pueden indicar **actividad delictiva organizada** (trata, secuestros, redes).

**Parámetros:**
- Radio del cluster (default: 1500 m)
- Mínimo de desapariciones para considerar cluster (default: 3)
- Ventana de tiempo (default: 30 días)
- Radio de proximidad a nodo sospechoso (default: 1000 m)

**Salida:**
- Vínculos `DESAPARECIDA ↔ DESAPARECIDA` (cluster).
- Vínculos `DESAPARECIDA ↔ UBICACION` (cercanía a nodo sospechoso).
- Alertas `CLUSTER_DESAPARICIONES` (CRITICO) y `DESAPARICION_NODO_SOSPECHOSO` (ALTO).

### Cálculo geoespacial

Todas las reglas que evalúan distancias usan la **fórmula de Haversine** para calcular la distancia en metros entre dos puntos geográficos:

```java
static double distanciaMetros(double lat1, double lon1,
                               double lat2, double lon2) {
    final double R = 6371000.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1)) *
               Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon/2) * Math.sin(dLon/2);
    return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}
```

### Prevención de duplicados

Cada vez que una regla genera un vínculo, se consulta el repositorio para verificar que no exista previamente la misma combinación `(tipoOrigen, idOrigen, tipoDestino, idDestino, regla)`. Esto evita acumulación de duplicados al re-ejecutar el motor.

---

## 6. Integración con Inteligencia Artificial

### Modelo utilizado

**Anthropic Claude Sonnet 4.5** — modelo de lenguaje generativo accesible vía API REST.

### Casos de uso implementados

#### 6.1 Chat conversacional contextual

El asistente IA recibe el historial de la conversación, una pregunta nueva y un **contexto del sistema** generado dinámicamente que incluye conteos de entidades, alertas críticas activas y desapariciones de alta prioridad. Esto permite responder preguntas como:

- *"¿Cuáles son las alertas críticas activas?"*
- *"¿Qué patrones detectaste en las desapariciones recientes?"*
- *"Dame un resumen del estado del sistema."*

#### 6.2 Predicción de zonas de búsqueda

Para una persona desaparecida específica, el sistema arma un prompt detallado con:
- Datos de la persona.
- Última ubicación conocida.
- Otras desapariciones cercanas (radio de 5 km).
- Ubicaciones sospechosas registradas.
- Circunstancias y vestimenta.

Y solicita a Claude **3 a 5 zonas geográficas prioritarias** con justificación, prioridad y acciones recomendadas.

El resultado se almacena en el campo `zonasBusquedaIA` de la entidad para consulta posterior.

#### 6.3 Análisis de similitud entre desapariciones

Recibe todos los casos activos y solicita a la IA un análisis comparativo que detecte:
- Patrones demográficos.
- Similitudes en circunstancias.
- Patrones geográficos o temporales.
- Indicios de modus operandi recurrente.

#### 6.4 Generación de reportes ejecutivos

Para una entidad específica (desaparecida, suceso o alerta), genera un reporte profesional con secciones:
- Resumen ejecutivo
- Datos del caso
- Análisis
- Recomendaciones

### Arquitectura de la integración

```
Frontend ──HTTP──> IAController ──> IAService
                                       │
                                       ├──> ClaudeClient ──HTTPS──> api.anthropic.com
                                       │
                                       └──> Servicios de dominio
                                            (PersonaService, etc.)
```

El componente `ClaudeClient` encapsula:
- Construcción del payload JSON.
- Headers de autenticación (`x-api-key`, `anthropic-version`).
- Manejo de errores HTTP (403, 429, 500).
- Métricas de uso (tokens de entrada/salida, duración).

### Configuración

```properties
nexo.ia.api-key=${ANTHROPIC_API_KEY:}
nexo.ia.modelo=claude-sonnet-4-5-20250929
nexo.ia.max-tokens=2048
nexo.ia.url=https://api.anthropic.com/v1/messages
```

La API key se inyecta como variable de entorno por seguridad. **No se almacena en el código fuente ni en el repositorio.**

### Estado actual de la integración

La integración está **técnicamente completa y verificada**. El endpoint `/api/v1/ia/estado` confirma la configuración correcta. La activación del servicio externo de Anthropic (billing operativo) es una dependencia que se gestiona fuera del alcance técnico del proyecto.

---

## 7. Modelo de Datos

### Entidades principales

```
Usuario
├── id, username (unique), passwordHash, nombreCompleto
├── rol (ADMIN | ANALISTA)
└── activo, creadoEn

Persona
├── id, documento (unique), nombre, apellido, alias
├── fechaNacimiento, genero, telefono, direccion
├── rol (VICTIMA | SOSPECHOSO | TESTIGO | PROPIETARIO | INTERMEDIARIO)
└── observaciones, creadoEn

Vehiculo
├── id, placa (unique), marca, modelo, año, color
├── propietario_id (FK Persona)
├── estado (NORMAL | ROBADO | RECUPERADO | APOYO)
└── observaciones, creadoEn

Ubicacion
├── id, direccion, latitud, longitud
├── tipo (TALLER | GALPON | DOMICILIO | ...)
├── nodoSospechoso (boolean)
└── observaciones, creadoEn

Suceso
├── id, tipo (ROBO_VEHICULO | DESAPARICION | ...)
├── fechaHora, descripcion, modusOperandi
├── vehiculo_id (FK), victima_id (FK Persona), ubicacion_id (FK)
└── creadoEn

PersonaDesaparecida
├── id, documento (unique), nombre, apellido, alias
├── fechaNacimiento, genero
├── estaturaCm, pesoKg, contextura, colorCabello, colorOjos
├── señasParticulares, ropaAlDesaparecer
├── fotoUrl
├── fechaDesaparicion, ultimaUbicacion_id (FK)
├── circunstancias
├── reportanteNombre, reportanteTelefono, reportanteRelacion
├── estado (BUSCADA | ENCONTRADA_VIVA | ENCONTRADA_FALLECIDA | ARCHIVADA)
├── prioridad (CRITICA | ALTA | MEDIA | BAJA)
├── analisisIA, zonasBusquedaIA
└── fechaResolucion, creadoEn, actualizadoEn

Alerta
├── id, tipo, titulo, descripcion
├── nivelRiesgo (CRITICO | ALTO | MEDIO | BAJO)
├── estado (PENDIENTE | EN_REVISION | CONFIRMADA | DESCARTADA)
└── creadoEn

Vinculo
├── id, tipoOrigen, idOrigen
├── tipoDestino, idDestino
├── reglaDetectada, score (decimal 0-1)
└── creadoEn

Relacion (relaciones sociales entre personas)
├── id, persona1_id (FK), persona2_id (FK)
├── tipo (FAMILIAR | LABORAL | AMISTAD | OTRO)
├── peso (decimal)
└── creadoEn

Avistamiento (relación N:M entre Vehiculo y Ubicacion)
├── id, vehiculo_id (FK), ubicacion_id (FK)
├── fechaHora, fuente, descripcion
└── creadoEn
```

### Estrategia de fetch

Para evitar errores `LazyInitializationException` durante la serialización JSON, **todas las relaciones `@ManyToOne` están configuradas como `EAGER`** y usan `@JsonIgnoreProperties` para evitar ciclos infinitos en la serialización.

Adicionalmente, `spring.jpa.open-in-view=true` está activado para garantizar que las sesiones de Hibernate permanezcan abiertas durante la serialización.

---

## 8. Seguridad

### Autenticación

- **JWT (JSON Web Token)** firmado con HS256 y secret configurable vía property.
- Expiración configurable (default: 24 horas).
- Header esperado: `Authorization: Bearer <token>`.

### Autorización

- Roles definidos en el token (`ADMIN`, `ANALISTA`).
- En el MVP actual, todos los endpoints bajo `/api/v1/**` están permitidos para usuarios autenticados.
- En futuras versiones se restringirán endpoints sensibles solo a administradores (eliminación de registros, gestión de usuarios).

### Hashing de contraseñas

- **BCrypt** con factor de costo 10 (recomendado por OWASP).
- Salt automático por contraseña.

### CORS

Configurado en `SecurityConfig`. Permite todos los orígenes (`*`) en desarrollo. En producción se debe restringir al dominio del frontend.

### Recursos servidos públicamente

- `/files/**` (fotos de personas desaparecidas) — accesible sin autenticación. En producción se debe agregar firma temporal o token de acceso.

### Riesgos conocidos y mitigaciones planeadas

| Riesgo | Estado | Mitigación |
|---|---|---|
| API Key expuesta | ✅ Mitigado | Variable de entorno, no commiteada al repo |
| SQL Injection | ✅ Mitigado | JPA usa prepared statements automáticamente |
| XSS en campos de texto | ⚠️ Parcial | React escapa por defecto, pero falta sanitización en backend |
| CSRF | ⚠️ Bajo riesgo | Stateless con JWT, no usa cookies de sesión |
| Rate limiting | ❌ No implementado | Pendiente para producción |
| HTTPS | ❌ Solo HTTP local | Producción requiere SSL (Let's Encrypt) |

---

## 9. Decisiones Arquitectónicas

### 9.1 ¿Por qué Spring Boot y no otro framework?

**Decisión**: Spring Boot 3.2 con Java 17.

**Alternativas consideradas:**
- Node.js + Express → descartado por menor robustez en aplicaciones empresariales.
- .NET Core → descartado por menor familiaridad del equipo.
- Django (Python) → descartado por menor performance comparado con JVM.

**Justificación**: Spring Boot ofrece autoconfiguración, ecosistema maduro, soporte de larga duración, integración nativa con seguridad (JWT, BCrypt), y excelente soporte para PostgreSQL.

### 9.2 ¿Por qué Java 17 y no Java 21?

**Decisión**: Java 17 (LTS).

**Justificación**: Java 17 es la versión LTS recomendada por la cátedra. Aunque Java 21 ofrece características nuevas (virtual threads, pattern matching mejorado), no son necesarias para este proyecto y mantener compatibilidad con la consigna del profesor es prioritario.

### 9.3 ¿Por qué React + TypeScript?

**Decisión**: React 18 con TypeScript.

**Alternativas consideradas:**
- Angular → descartado por curva de aprendizaje más pronunciada.
- Vue → descartado por menor demanda laboral.
- Svelte → descartado por ecosistema menor.

**Justificación**: React es la librería UI más demandada en la industria, TypeScript reduce errores en runtime y facilita el mantenimiento. Vite ofrece tiempos de build dramáticamente más rápidos que Webpack.

### 9.4 ¿Por qué PostgreSQL y no MySQL/MongoDB?

**Decisión**: PostgreSQL 14 con extensión PostGIS.

**Justificación:**
- Soporte de tipos complejos (JSON, arrays, geoespaciales).
- **PostGIS** habilita consultas geoespaciales nativas, fundamentales para el motor Red Thread.
- Mayor cumplimiento del estándar SQL.
- Excelente performance en consultas complejas.

MongoDB se descartó porque el dominio criminal es altamente relacional (personas relacionadas con vehículos, vehículos con ubicaciones, etc.) y NoSQL agregaría complejidad innecesaria.

### 9.5 ¿Por qué un motor de reglas y no Machine Learning?

**Decisión**: Reglas heurísticas determinísticas.

**Justificación:**
- **Explicabilidad**: cada vínculo descubierto puede ser justificado por una regla concreta. Esto es crítico en contextos legales/policiales donde un analista debe poder defender ante un fiscal por qué dos entidades están vinculadas.
- **Datos limitados**: ML requiere datasets etiquetados grandes que no están disponibles en este contexto.
- **Costo computacional**: las reglas son rápidas, ML requiere infraestructura adicional.
- **Aprobación legal**: las decisiones automatizadas con ML enfrentan más resistencia regulatoria.

ML se considera para iteraciones futuras como capa complementaria.

### 9.6 ¿Por qué Cytoscape.js para grafos?

**Decisión**: Cytoscape.js 3.x.

**Alternativas consideradas:**
- D3.js → descartado por curva de aprendizaje pronunciada.
- Vis.js → descartado por mantenimiento irregular.
- Sigma.js → descartado por API menos rica.

**Justificación**: Cytoscape es el estándar de facto en visualización de grafos para web, con excelente performance hasta miles de nodos, layouts integrados (cose, breadthfirst, circle, etc.) y API estable.

### 9.7 ¿Por qué subida de fotos en disco local y no S3?

**Decisión**: Almacenamiento local en `/uploads/desaparecidas/`.

**Justificación**: Para el MVP, el almacenamiento local es suficiente y elimina dependencia de servicios externos. En producción se recomienda migrar a un object storage (AWS S3, MinIO) para escalabilidad y respaldos.

---

## 10. Limitaciones Conocidas

### Limitaciones técnicas actuales

1. **No hay rate limiting**: el sistema no limita requests por usuario. Un cliente malicioso podría saturar el motor.
2. **Sin paginación en algunos endpoints**: `GET /personas` retorna todos los registros. En datasets grandes esto podría degradar performance.
3. **Logs no centralizados**: los logs se imprimen en consola. No hay sistema de logging persistente (ELK, Loki, etc.).
4. **Sin tests automatizados**: la cobertura de tests unitarios es 0%. Se priorizó funcionalidad por la naturaleza académica del MVP.
5. **Migrations manuales**: Hibernate `ddl-auto=update` es práctico en desarrollo pero peligroso en producción. Se requiere migración a Flyway/Liquibase.
6. **Frontend monolítico**: todos los componentes están en un solo bundle. Para producción se recomienda code splitting por ruta.
7. **Almacenamiento de fotos local**: no soporta múltiples instancias del backend (cada una tendría sus propios archivos).

### Limitaciones funcionales

1. **No hay multi-tenancy**: una sola unidad policial por instancia.
2. **Sin notificaciones en tiempo real**: las alertas se ven al refrescar el dashboard. WebSockets pendientes.
3. **Sin export a PDF**: solo CSV. PDFs son requerimiento futuro.
4. **Sin app móvil**: solo web responsive. App nativa pendiente.
5. **Búsqueda básica**: no hay full-text search ni filtros avanzados en la búsqueda global.

---

## 11. Roadmap Futuro

### Corto plazo (siguientes 3 meses)

- ✅ Tests unitarios con JUnit 5 y cobertura ≥ 70%.
- ✅ Tests E2E con Playwright o Cypress.
- ✅ Migración a Flyway para gestión de schema.
- ✅ Rate limiting con bucket4j.
- ✅ Dockerización completa (backend + frontend + BD).
- ✅ Pipeline CI/CD con GitHub Actions.
- ✅ Logs centralizados con Loki + Grafana.

### Mediano plazo (3-6 meses)

- ✅ App móvil React Native para oficiales en campo.
- ✅ WebSockets para notificaciones en tiempo real.
- ✅ Generación de reportes en PDF.
- ✅ Importación masiva de datos desde CSV/Excel.
- ✅ Multi-tenancy por organización.
- ✅ Logs de auditoría persistentes.

### Largo plazo (6-12 meses)

- ✅ Capa de Machine Learning para detección de anomalías.
- ✅ Integración con OCR para placas vehiculares (cámaras CCTV).
- ✅ Integración con sistemas externos (RNI, INTERPOL, otras unidades policiales).
- ✅ Dashboard predictivo con mapas de calor de incidencia delictiva.
- ✅ Aplicación de análisis de redes sociales (NetworkX, GraphFrames).

---

*Documento técnico elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*