# Diagramas del Sistema
## Nexo Criminal — Sistema de Inteligencia de Precisión

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Equipo:** Amarillo

---

## Tabla de Contenido

1. [Diagrama de Arquitectura General](#1-diagrama-de-arquitectura-general)
2. [Diagrama de Componentes Backend](#2-diagrama-de-componentes-backend)
3. [Diagrama Entidad-Relación (Modelo de Datos)](#3-diagrama-entidad-relación-modelo-de-datos)
4. [Diagrama de Casos de Uso](#4-diagrama-de-casos-de-uso)
5. [Diagrama de Clases (Backend)](#5-diagrama-de-clases-backend)
6. [Diagrama de Secuencia: Login](#6-diagrama-de-secuencia-login)
7. [Diagrama de Secuencia: Ejecución del Motor Red Thread](#7-diagrama-de-secuencia-ejecución-del-motor-red-thread)
8. [Diagrama de Secuencia: Consulta a IA Claude](#8-diagrama-de-secuencia-consulta-a-ia-claude)
9. [Diagrama de Flujo: Motor Red Thread](#9-diagrama-de-flujo-motor-red-thread)
10. [Diagrama de Flujo: Subida de Foto](#10-diagrama-de-flujo-subida-de-foto)
11. [Diagrama de Estados: Alerta](#11-diagrama-de-estados-alerta)
12. [Diagrama de Estados: Persona Desaparecida](#12-diagrama-de-estados-persona-desaparecida)
13. [Diagrama de Despliegue](#13-diagrama-de-despliegue)

---

## 1. Diagrama de Arquitectura General

Vista de alto nivel de las tres capas del sistema y su comunicación con servicios externos.

```mermaid
graph TB
    subgraph "Cliente (Navegador)"
        UI[React 18 + TypeScript<br/>Single Page Application]
    end

    subgraph "Backend - Spring Boot"
        API[API REST<br/>Spring Boot 3.2]
        SEC[Spring Security<br/>JWT Filter]
        SVC[Servicios de Dominio]
        ENG[Motor Red Thread<br/>5 Reglas Heurísticas]
        IA[Cliente Claude IA]
    end

    subgraph "Persistencia"
        DB[(PostgreSQL 14<br/>+ PostGIS)]
        FS[Sistema de Archivos<br/>uploads/desaparecidas]
    end

    subgraph "Servicios Externos"
        ANTHROPIC[Anthropic API<br/>Claude Sonnet 4.5]
    end

    UI -->|HTTPS + JWT| SEC
    SEC --> API
    API --> SVC
    SVC --> ENG
    SVC --> IA
    SVC -->|JPA / Hibernate| DB
    SVC -->|MultipartFile| FS
    IA -->|HTTPS| ANTHROPIC
    UI -->|GET /files/**| FS

    style UI fill:#3B82F6,color:#fff
    style API fill:#10B981,color:#fff
    style ENG fill:#DC2626,color:#fff
    style IA fill:#8B5CF6,color:#fff
    style DB fill:#F59E0B,color:#fff
    style ANTHROPIC fill:#6D28D9,color:#fff
```

---

## 2. Diagrama de Componentes Backend

Detalle de los paquetes y sus responsabilidades dentro del backend.

```mermaid
graph LR
    subgraph "com.nexocriminal"
        APP[NexoCriminalApplication]

        subgraph "security"
            SEC_CFG[SecurityConfig]
            JWT_FLT[JwtAuthFilter]
            JWT_SVC[JwtService]
            AUTH_CTRL[AuthController]
        end

        subgraph "domain"
            PER[persona/]
            VEH[vehiculo/]
            UBI[ubicacion/]
            SUC[suceso/]
            DES[desaparecida/]
            ALE[alerta/]
            VIN[vinculo/]
            REL[relacion/]
            USU[usuario/]
        end

        subgraph "engine"
            ENG_SVC[RedThreadEngineService]
            R1[ReglaNodoLogistico]
            R2[ReglaEscoltaVehicular]
            R3[ReglaCirculoConfianza]
            R4[ReglaSimilitudModusOperandi]
            R5[ReglaClusterDesapariciones]
            ENG_CTRL[EngineController]
        end

        subgraph "ia"
            IA_CTRL[IAController]
            IA_SVC[IAService]
            CLAUDE[ClaudeClient]
        end

        subgraph "files"
            FS_SVC[FileStorageService]
            FS_CFG[FileStorageConfig]
        end
    end

    APP --> SEC_CFG
    SEC_CFG --> JWT_FLT
    JWT_FLT --> JWT_SVC
    AUTH_CTRL --> JWT_SVC

    ENG_SVC --> R1
    ENG_SVC --> R2
    ENG_SVC --> R3
    ENG_SVC --> R4
    ENG_SVC --> R5
    ENG_CTRL --> ENG_SVC

    IA_CTRL --> IA_SVC
    IA_SVC --> CLAUDE
    IA_SVC --> DES
    IA_SVC --> ALE

    R1 --> VEH
    R1 --> UBI
    R2 --> VEH
    R3 --> PER
    R3 --> REL
    R4 --> SUC
    R5 --> DES
    R5 --> UBI

    DES --> FS_SVC

    style APP fill:#0F172A,color:#fff
    style ENG_SVC fill:#DC2626,color:#fff
    style IA_SVC fill:#8B5CF6,color:#fff
```

---

## 3. Diagrama Entidad-Relación (Modelo de Datos)

Modelo relacional completo del sistema.

```mermaid
erDiagram
    USUARIO {
        bigint id PK
        varchar username UK
        varchar password_hash
        varchar nombre_completo
        varchar rol
        boolean activo
        timestamp creado_en
    }

    PERSONA {
        bigint id PK
        varchar documento UK
        varchar nombre
        varchar apellido
        varchar alias
        date fecha_nacimiento
        varchar genero
        varchar telefono
        text direccion
        varchar rol
        text observaciones
        timestamp creado_en
    }

    VEHICULO {
        bigint id PK
        varchar placa UK
        varchar marca
        varchar modelo
        int anio
        varchar color
        bigint propietario_id FK
        varchar estado
        text observaciones
        timestamp creado_en
    }

    UBICACION {
        bigint id PK
        text direccion
        decimal latitud
        decimal longitud
        varchar tipo
        boolean nodo_sospechoso
        text observaciones
        timestamp creado_en
    }

    SUCESO {
        bigint id PK
        varchar tipo
        timestamp fecha_hora
        text descripcion
        text modus_operandi
        bigint vehiculo_id FK
        bigint victima_id FK
        bigint ubicacion_id FK
        timestamp creado_en
    }

    PERSONA_DESAPARECIDA {
        bigint id PK
        varchar documento UK
        varchar nombre
        varchar apellido
        varchar alias
        date fecha_nacimiento
        varchar genero
        int estatura_cm
        int peso_kg
        varchar contextura
        varchar color_cabello
        varchar color_ojos
        text señas_particulares
        text ropa_al_desaparecer
        varchar foto_url
        timestamp fecha_desaparicion
        bigint ultima_ubicacion_id FK
        text circunstancias
        varchar reportante_nombre
        varchar reportante_telefono
        varchar reportante_relacion
        varchar estado
        varchar prioridad
        text analisis_ia
        text zonas_busqueda_ia
        timestamp fecha_resolucion
        timestamp creado_en
        timestamp actualizado_en
    }

    ALERTA {
        bigint id PK
        varchar tipo
        varchar titulo
        text descripcion
        varchar nivel_riesgo
        varchar estado
        timestamp creado_en
    }

    VINCULO {
        bigint id PK
        varchar tipo_origen
        bigint id_origen
        varchar tipo_destino
        bigint id_destino
        varchar regla_detectada
        decimal score
        timestamp creado_en
    }

    RELACION {
        bigint id PK
        bigint persona1_id FK
        bigint persona2_id FK
        varchar tipo
        decimal peso
        timestamp creado_en
    }

    AVISTAMIENTO {
        bigint id PK
        bigint vehiculo_id FK
        bigint ubicacion_id FK
        timestamp fecha_hora
        varchar fuente
        text descripcion
        timestamp creado_en
    }

    PERSONA ||--o{ VEHICULO : "es_propietario_de"
    PERSONA ||--o{ SUCESO : "es_victima_de"
    UBICACION ||--o{ SUCESO : "ocurre_en"
    VEHICULO ||--o{ SUCESO : "involucra"
    PERSONA ||--o{ RELACION : "se_relaciona"
    UBICACION ||--o{ PERSONA_DESAPARECIDA : "ultima_vista_en"
    VEHICULO ||--o{ AVISTAMIENTO : "es_avistado"
    UBICACION ||--o{ AVISTAMIENTO : "lugar_de"
```

---

## 4. Diagrama de Casos de Uso

Actores principales y operaciones disponibles en el sistema.

```mermaid
graph TB
    subgraph "Sistema Nexo Criminal"
        UC1[Iniciar sesión]
        UC2[Cambiar contraseña]
        UC3[Gestionar Personas]
        UC4[Gestionar Vehículos]
        UC5[Gestionar Ubicaciones]
        UC6[Gestionar Sucesos]
        UC7[Gestionar Personas Desaparecidas]
        UC8[Subir foto de desaparecida]
        UC9[Crear relaciones sociales]
        UC10[Buscar intermediarios]
        UC11[Registrar avistamiento de vehículo]
        UC12[Ejecutar Motor Red Thread]
        UC13[Configurar umbrales del motor]
        UC14[Ver alertas]
        UC15[Cambiar estado de alerta]
        UC16[Visualizar grafo de vínculos]
        UC17[Filtrar grafo por tipo]
        UC18[Consultar Asistente IA]
        UC19[Generar reporte ejecutivo IA]
        UC20[Predecir zonas de búsqueda IA]
        UC21[Exportar datos a CSV]
        UC22[Búsqueda global]
        UC23[Gestionar usuarios]
        UC24[Ver dashboard]
    end

    ANALISTA((👤 Analista))
    ADMIN((👤 Administrador))
    IA_EXT((🤖 Anthropic Claude))

    ANALISTA --> UC1
    ANALISTA --> UC2
    ANALISTA --> UC3
    ANALISTA --> UC4
    ANALISTA --> UC5
    ANALISTA --> UC6
    ANALISTA --> UC7
    ANALISTA --> UC8
    ANALISTA --> UC9
    ANALISTA --> UC10
    ANALISTA --> UC11
    ANALISTA --> UC12
    ANALISTA --> UC14
    ANALISTA --> UC15
    ANALISTA --> UC16
    ANALISTA --> UC17
    ANALISTA --> UC18
    ANALISTA --> UC19
    ANALISTA --> UC20
    ANALISTA --> UC21
    ANALISTA --> UC22
    ANALISTA --> UC24

    ADMIN --> UC1
    ADMIN --> UC2
    ADMIN --> UC13
    ADMIN --> UC23
    ADMIN --> UC24

    UC18 -.->|API REST| IA_EXT
    UC19 -.->|API REST| IA_EXT
    UC20 -.->|API REST| IA_EXT

    style ANALISTA fill:#3B82F6,color:#fff
    style ADMIN fill:#DC2626,color:#fff
    style IA_EXT fill:#8B5CF6,color:#fff
```

---

## 5. Diagrama de Clases (Backend)

Vista simplificada de las clases principales del backend.

```mermaid
classDiagram
    class Persona {
        +Long id
        +String documento
        +String nombre
        +String apellido
        +String alias
        +LocalDate fechaNacimiento
        +String genero
        +String telefono
        +String direccion
        +RolPersona rol
        +String observaciones
        +LocalDateTime creadoEn
    }

    class Vehiculo {
        +Long id
        +String placa
        +String marca
        +String modelo
        +Integer anio
        +String color
        +Persona propietario
        +EstadoVehiculo estado
        +String observaciones
        +LocalDateTime creadoEn
    }

    class Ubicacion {
        +Long id
        +String direccion
        +Double latitud
        +Double longitud
        +TipoUbicacion tipo
        +Boolean nodoSospechoso
        +String observaciones
        +LocalDateTime creadoEn
    }

    class Suceso {
        +Long id
        +TipoSuceso tipo
        +LocalDateTime fechaHora
        +String descripcion
        +String modusOperandi
        +Vehiculo vehiculo
        +Persona victima
        +Ubicacion ubicacion
        +LocalDateTime creadoEn
    }

    class PersonaDesaparecida {
        +Long id
        +String documento
        +String nombre
        +String apellido
        +Integer estaturaCm
        +String fotoUrl
        +LocalDateTime fechaDesaparicion
        +Ubicacion ultimaUbicacion
        +EstadoDesaparicion estado
        +PrioridadDesaparicion prioridad
        +String analisisIA
        +String zonasBusquedaIA
    }

    class Alerta {
        +Long id
        +TipoAlerta tipo
        +String titulo
        +String descripcion
        +NivelRiesgo nivelRiesgo
        +EstadoAlerta estado
        +LocalDateTime creadoEn
    }

    class Vinculo {
        +Long id
        +String tipoOrigen
        +Long idOrigen
        +String tipoDestino
        +Long idDestino
        +String reglaDetectada
        +BigDecimal score
    }

    class ReglaVinculo {
        <<interface>>
        +String nombre()
        +ResultadoRegla ejecutar()
    }

    class RedThreadEngineService {
        -ReglaNodoLogistico reglaNodo
        -ReglaEscoltaVehicular reglaEscolta
        -ReglaCirculoConfianza reglaCirculo
        -ReglaSimilitudModusOperandi reglaModus
        -ReglaClusterDesapariciones reglaCluster
        +List ejecutarTodas()
    }

    class ClaudeClient {
        -String apiKey
        -String modelo
        -HttpClient http
        +RespuestaIA preguntar(String, List)
        +boolean configurada()
    }

    class IAService {
        -ClaudeClient claudeClient
        +RespuestaIA chat(List, String, boolean)
        +RespuestaIA predecirZonasBusqueda(Long)
        +RespuestaIA generarReporte(String, Long)
    }

    Persona "1" --o "*" Vehiculo : propietario
    Persona "1" --o "*" Suceso : victima
    Vehiculo "0..1" --o "*" Suceso
    Ubicacion "0..1" --o "*" Suceso
    Ubicacion "0..1" --o "*" PersonaDesaparecida

    ReglaVinculo <|.. ReglaNodoLogistico
    ReglaVinculo <|.. ReglaEscoltaVehicular
    ReglaVinculo <|.. ReglaCirculoConfianza
    ReglaVinculo <|.. ReglaSimilitudModusOperandi
    ReglaVinculo <|.. ReglaClusterDesapariciones

    RedThreadEngineService --> ReglaVinculo
    IAService --> ClaudeClient
```

---

## 6. Diagrama de Secuencia: Login

Flujo de autenticación con JWT.

```mermaid
sequenceDiagram
    actor U as Usuario
    participant F as Frontend (React)
    participant API as AuthController
    participant SVC as UsuarioService
    participant DB as PostgreSQL
    participant JWT as JwtService

    U->>F: Ingresa credenciales
    F->>API: POST /api/v1/auth/login<br/>{username, password}
    API->>SVC: autenticar(username, password)
    SVC->>DB: findByUsername(username)
    DB-->>SVC: Usuario
    SVC->>SVC: BCrypt.matches(password, hash)

    alt Credenciales válidas
        SVC->>JWT: generarToken(username, rol)
        JWT-->>SVC: token
        SVC-->>API: AuthResponse(token, usuario)
        API-->>F: 200 OK + JWT
        F->>F: localStorage.setItem('token', jwt)
        F-->>U: Redirect /dashboard
    else Credenciales inválidas
        SVC-->>API: AuthException
        API-->>F: 401 Unauthorized
        F-->>U: Mostrar error
    end
```

---

## 7. Diagrama de Secuencia: Ejecución del Motor Red Thread

Flujo completo cuando el analista ejecuta el motor.

```mermaid
sequenceDiagram
    actor A as Analista
    participant F as Frontend
    participant CTRL as EngineController
    participant ENG as RedThreadEngineService
    participant R1 as ReglaNodoLogistico
    participant R2 as ReglaEscoltaVehicular
    participant R3 as ReglaCirculoConfianza
    participant R4 as ReglaModusOperandi
    participant R5 as ReglaClusterDesapariciones
    participant DB as PostgreSQL

    A->>F: Click "Ejecutar Motor"
    F->>CTRL: POST /api/v1/engine/ejecutar-todo
    CTRL->>ENG: ejecutarTodas()

    par Ejecución secuencial de reglas
        ENG->>R1: ejecutar()
        R1->>DB: SELECT vehiculos robados
        R1->>DB: SELECT ubicaciones cercanas
        R1->>R1: aplicar Haversine
        R1->>DB: INSERT vinculos + alertas
        R1-->>ENG: ResultadoRegla
    and
        ENG->>R2: ejecutar()
        R2->>DB: SELECT avistamientos
        R2->>R2: detectar coincidencias
        R2->>DB: INSERT vinculos + alertas
        R2-->>ENG: ResultadoRegla
    and
        ENG->>R3: ejecutar()
        R3->>DB: SELECT relaciones sociales
        R3->>R3: BFS sobre grafo
        R3->>DB: INSERT vinculos + alertas
        R3-->>ENG: ResultadoRegla
    and
        ENG->>R4: ejecutar()
        R4->>DB: SELECT sucesos
        R4->>R4: similitud Jaccard
        R4->>DB: INSERT vinculos + alertas
        R4-->>ENG: ResultadoRegla
    and
        ENG->>R5: ejecutar()
        R5->>DB: SELECT desaparecidas
        R5->>R5: detectar clusters
        R5->>DB: INSERT vinculos + alertas
        R5-->>ENG: ResultadoRegla
    end

    ENG-->>CTRL: List<ResultadoRegla>
    CTRL-->>F: 200 OK + resumen
    F-->>A: "Motor ejecutado: X vínculos, Y alertas"
```

---

## 8. Diagrama de Secuencia: Consulta a IA Claude

Flujo cuando el analista consulta el asistente IA.

```mermaid
sequenceDiagram
    actor A as Analista
    participant F as Frontend
    participant CTRL as IAController
    participant SVC as IAService
    participant CLAUDE as ClaudeClient
    participant ANT as Anthropic API
    participant DB as PostgreSQL

    A->>F: Escribe pregunta y envía
    F->>CTRL: POST /api/v1/ia/chat<br/>{historial, pregunta, contexto: true}
    CTRL->>SVC: chat(historial, pregunta, true)

    SVC->>DB: contar entidades del sistema
    DB-->>SVC: counts
    SVC->>DB: obtener alertas críticas
    DB-->>SVC: alertas
    SVC->>SVC: generar contexto del sistema

    SVC->>CLAUDE: preguntar(systemPrompt, mensajes)
    CLAUDE->>CLAUDE: build JSON payload
    CLAUDE->>ANT: POST /v1/messages<br/>headers: x-api-key
    ANT-->>CLAUDE: 200 OK + content
    CLAUDE->>CLAUDE: parse response
    CLAUDE-->>SVC: RespuestaIA(contenido, tokens, ms)

    SVC-->>CTRL: RespuestaIA
    CTRL-->>F: 200 OK + respuesta
    F-->>A: Mostrar mensaje en chat
```

---

## 9. Diagrama de Flujo: Motor Red Thread

Lógica detallada de la ejecución del motor.

```mermaid
flowchart TD
    START([Inicio: Click 'Ejecutar Motor']) --> INIT[Inicializar lista de resultados]
    INIT --> R1{Regla 1<br/>Nodo Logístico}

    R1 --> R1_LOAD[Cargar vehículos robados<br/>+ ubicaciones]
    R1_LOAD --> R1_LOOP[Para cada ubicación]
    R1_LOOP --> R1_CHECK{Hay 3+ vehículos<br/>en radio 500m<br/>en 72h?}
    R1_CHECK -->|Sí| R1_SAVE[Crear vínculos<br/>+ Alerta CRITICO]
    R1_CHECK -->|No| R1_NEXT[Siguiente ubicación]
    R1_SAVE --> R1_NEXT
    R1_NEXT --> R1_END{Más ubicaciones?}
    R1_END -->|Sí| R1_LOOP
    R1_END -->|No| R2

    R2{Regla 2<br/>Escolta Vehicular} --> R2_LOAD[Cargar avistamientos]
    R2_LOAD --> R2_CHECK{Vehículo no robado<br/>aparece 3+ veces<br/>cerca de robados?}
    R2_CHECK -->|Sí| R2_SAVE[Crear vínculos<br/>+ Alerta ALTO]
    R2_CHECK -->|No| R3
    R2_SAVE --> R3

    R3{Regla 3<br/>Círculo Confianza} --> R3_LOAD[Cargar relaciones]
    R3_LOAD --> R3_BFS[BFS víctima → sospechoso<br/>profundidad 2]
    R3_BFS --> R3_CHECK{Existe<br/>intermediario?}
    R3_CHECK -->|Sí| R3_SAVE[Crear vínculo<br/>+ Alerta MEDIO]
    R3_CHECK -->|No| R4
    R3_SAVE --> R4

    R4{Regla 4<br/>Modus Operandi} --> R4_LOAD[Cargar sucesos]
    R4_LOAD --> R4_JACCARD[Calcular similitud<br/>Jaccard sobre tokens]
    R4_JACCARD --> R4_CHECK{Similitud<br/>≥ 0.75?}
    R4_CHECK -->|Sí| R4_SAVE[Crear vínculo<br/>+ Alerta ALTO]
    R4_CHECK -->|No| R5
    R4_SAVE --> R5

    R5{Regla 5<br/>Cluster Desapariciones} --> R5_LOAD[Cargar desaparecidas<br/>activas]
    R5_LOAD --> R5_CHECK{3+ desapariciones<br/>en radio 1.5km<br/>en 30 días?}
    R5_CHECK -->|Sí| R5_SAVE[Crear vínculos<br/>+ Alerta CRITICO]
    R5_CHECK -->|No| R5_NODOS[Verificar cercanía<br/>a nodos sospechosos]
    R5_SAVE --> R5_NODOS
    R5_NODOS --> R5_NCHECK{Desaparición<br/>≤ 1km de<br/>nodo sospechoso?}
    R5_NCHECK -->|Sí| R5_NSAVE[Crear vínculo<br/>+ Alerta ALTO]
    R5_NCHECK -->|No| END
    R5_NSAVE --> END

    END([Fin: Retornar resumen<br/>X vínculos, Y alertas])

    style START fill:#10B981,color:#fff
    style END fill:#10B981,color:#fff
    style R1 fill:#DC2626,color:#fff
    style R2 fill:#DC2626,color:#fff
    style R3 fill:#DC2626,color:#fff
    style R4 fill:#DC2626,color:#fff
    style R5 fill:#DC2626,color:#fff
```

---

## 10. Diagrama de Flujo: Subida de Foto

Proceso de subida y persistencia de fotografía de persona desaparecida.

```mermaid
flowchart TD
    START([Usuario selecciona foto]) --> VALIDATE_FRONT{Tipo es imagen?}
    VALIDATE_FRONT -->|No| ERR1[Error: Solo imágenes]
    VALIDATE_FRONT -->|Sí| SIZE_FRONT{Tamaño < 5MB?}
    SIZE_FRONT -->|No| ERR2[Error: Máximo 5MB]
    SIZE_FRONT -->|Sí| PREVIEW[Mostrar preview con FileReader]

    PREVIEW --> SUBMIT[Click 'Guardar']
    SUBMIT --> CREATE{¿Persona<br/>existe?}
    CREATE -->|No| POST_PERSONA[POST /desaparecidas<br/>crear primero]
    CREATE -->|Sí| UPLOAD
    POST_PERSONA --> UPLOAD[POST /desaparecidas/id/foto<br/>multipart/form-data]

    UPLOAD --> BACKEND[FileStorageService]
    BACKEND --> VALIDATE_BACK{Validar<br/>en backend}
    VALIDATE_BACK -->|Falla| RET_ERR[400 Bad Request]
    VALIDATE_BACK -->|OK| OLD_CHECK{¿Foto<br/>anterior?}
    OLD_CHECK -->|Sí| DELETE_OLD[Eliminar archivo viejo]
    OLD_CHECK -->|No| GEN_NAME
    DELETE_OLD --> GEN_NAME[Generar UUID + extensión]

    GEN_NAME --> SAVE_DISK[Guardar en<br/>uploads/desaparecidas/]
    SAVE_DISK --> UPDATE_DB[Actualizar fotoUrl en BD]
    UPDATE_DB --> RET_OK[200 OK + URL relativa]

    RET_OK --> RELOAD[Frontend recarga datos]
    RELOAD --> SHOW[Mostrar foto en galería]
    SHOW --> END([Fin])

    ERR1 --> END
    ERR2 --> END
    RET_ERR --> END

    style START fill:#10B981,color:#fff
    style END fill:#10B981,color:#fff
    style ERR1 fill:#DC2626,color:#fff
    style ERR2 fill:#DC2626,color:#fff
    style RET_ERR fill:#DC2626,color:#fff
```

---

## 11. Diagrama de Estados: Alerta

Ciclo de vida de una alerta generada por el motor.

```mermaid
stateDiagram-v2
    [*] --> PENDIENTE: Motor crea alerta

    PENDIENTE --> EN_REVISION: Click 'Analizar'
    PENDIENTE --> CONFIRMADA: Click 'Desplegar'<br/>(si es CRÍTICA o ALTA)
    PENDIENTE --> DESCARTADA: Falso positivo evidente

    EN_REVISION --> CONFIRMADA: Click 'Confirmar'
    EN_REVISION --> DESCARTADA: Click 'Descartar'
    EN_REVISION --> PENDIENTE: Reabrir

    CONFIRMADA --> [*]: Caso cerrado
    DESCARTADA --> [*]: Archivada

    note right of PENDIENTE
        Estado inicial al crearse
        Visible en dashboard
    end note

    note right of CONFIRMADA
        Acción policial requerida
        Genera reporte ejecutivo
    end note

    note right of DESCARTADA
        Falso positivo
        No genera más alertas similares
    end note
```

---

## 12. Diagrama de Estados: Persona Desaparecida

Estados de un caso de desaparición.

```mermaid
stateDiagram-v2
    [*] --> BUSCADA: Reportar caso

    BUSCADA --> ENCONTRADA_VIVA: Localizada con vida
    BUSCADA --> ENCONTRADA_FALLECIDA: Localizada sin vida
    BUSCADA --> ARCHIVADA: Caso suspendido

    ENCONTRADA_VIVA --> [*]: Caso resuelto
    ENCONTRADA_FALLECIDA --> [*]: Caso cerrado
    ARCHIVADA --> BUSCADA: Reapertura del caso
    ARCHIVADA --> [*]: Cierre definitivo

    note right of BUSCADA
        Estado activo
        Aparece en mapa táctico
        Genera alertas si forma cluster
    end note

    note right of ENCONTRADA_VIVA
        Caso resuelto exitosamente
        Se mantiene histórico
    end note

    note left of ARCHIVADA
        Sin avances en 1+ años
        Puede reabrirse
    end note
```

---

## 13. Diagrama de Despliegue

Vista de cómo se despliega el sistema en un ambiente de producción típico.

```mermaid
graph TB
    subgraph "Cliente"
        BROWSER[Navegador Web<br/>Chrome / Edge / Firefox]
    end

    subgraph "Servidor de Aplicaciones (Ubuntu Server 22.04)"
        NGINX[Nginx<br/>Proxy reverso + SSL]
        FRONTEND_BUILD[Build estático React<br/>HTML / JS / CSS]
        BACKEND_JAR[backend.jar<br/>Spring Boot]
        UPLOADS[Volumen<br/>/uploads/desaparecidas]
    end

    subgraph "Servidor de Base de Datos"
        PG[PostgreSQL 14<br/>+ PostGIS]
        BACKUP[Backup automático<br/>diario]
    end

    subgraph "Servicios Externos"
        ANTHROPIC[Anthropic API<br/>api.anthropic.com]
        DNS[DNS público<br/>nexocriminal.com.ve]
    end

    subgraph "Cliente DevOps"
        GH[GitHub<br/>Repositorio + Actions]
    end

    BROWSER -->|HTTPS 443| DNS
    DNS --> NGINX
    NGINX -->|Static files| FRONTEND_BUILD
    NGINX -->|/api/**| BACKEND_JAR
    NGINX -->|/files/**| UPLOADS

    BACKEND_JAR -->|JDBC 5432| PG
    BACKEND_JAR -->|HTTPS| ANTHROPIC
    BACKEND_JAR --> UPLOADS

    PG --> BACKUP

    GH -.->|CI/CD Deploy| NGINX
    GH -.->|CI/CD Deploy| BACKEND_JAR

    style BROWSER fill:#3B82F6,color:#fff
    style NGINX fill:#10B981,color:#fff
    style BACKEND_JAR fill:#F59E0B,color:#fff
    style PG fill:#6366F1,color:#fff
    style ANTHROPIC fill:#8B5CF6,color:#fff
```

---

## Notas sobre los diagramas

- Todos los diagramas están escritos en sintaxis **Mermaid**, que GitHub renderiza nativamente al visualizar archivos `.md`.
- Para visualizar fuera de GitHub, podés usar **https://mermaid.live** y pegar cualquier bloque.
- Los diagramas son **vivos**: se actualizan automáticamente al modificar el código fuente del archivo.
- Para exportar a PNG/SVG, podés usar la extensión "Mermaid Markdown Syntax Highlighting" en VS Code.

---

*Diagramas elaborados por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*