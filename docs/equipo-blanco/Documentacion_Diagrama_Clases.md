# Documentación del Diagrama de Clases
## Sistema Integral de Gestión Penitenciaria

**Universidad de Oriente · Núcleo de Nueva Esparta**
**Sistemas de Información II — Sección 0520 · Semestre I-2026**

| Integrante | C.I. | Rol en el proyecto |
|---|---|---|
| Br. Jeisi Rosales | 29.668.571 | Frontend + Coordinadora |
| Br. Orlando Zabala | 31.256.875 | Backend (Java/Spring Boot) |
| Br. Alexander Ramos | 31.062.067 | Base de datos + Backend |
| Br. Samuel Salazar | 31.775.609 | Frontend + UI/UX |
| Br. Santiago Velázquez | 31.671.291 | Servicio de alertas (Python) + Backend |

---

## 1. Visión General

El diagrama de clases modela la estructura estática del Sistema Integral de Gestión Penitenciaria, cubriendo el ciclo de vida completo de una persona privada de libertad: desde su ingreso al establecimiento, su gestión interna, hasta el seguimiento post-penitenciario y el control de cumplimiento. El modelo se organiza en cuatro módulos funcionales claramente delimitados, más un conjunto de componentes transversales de alertas.

---

## 2. Estructura por Módulos

### 2.1 Módulo de Registro

Este módulo concentra las clases responsables de representar el expediente digital de un recluso y toda su información asociada.

#### Clase `recluso`

Entidad central del sistema. Representa a cada persona privada de libertad e integra en un solo objeto todos los datos que conforman su expediente digital.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único del registro |
| `cedula` | String (unique) | Número de cédula de identidad; garantiza unicidad del expediente |
| `primerNombre` | String | Primer nombre del recluso |
| `segundoNombre` | String | Segundo nombre del recluso |
| `primerApellido` | String | Primer apellido del recluso |
| `segundoApellido` | String | Segundo apellido del recluso |
| `fechaNacimiento` | Date | Fecha de nacimiento |
| `/edad` | int | Atributo derivado; calculado a partir de `fechaNacimiento` |
| `delitoImputado` | String | Descripción del delito que motiva la privación de libertad |
| `numeroExpediente` | String | Número del expediente judicial |
| `tribunalSentencia` | String | Tribunal que emitió la sentencia |
| `fechaIngreso` | Date | Fecha de ingreso al establecimiento |
| `duracionPenaAnios` | int | Duración de la pena en años |
| `duracionPenaMeses` | int | Duración de la pena en meses (complementaria a años) |
| `fechaLiberacionEstimada` | Date | Atributo derivado; calculado automáticamente a partir de `fechaIngreso` y la duración de la pena |
| `colorOjos` | String | Color de ojos (características físicas) |
| `colorCabello` | String | Color de cabello |
| `complexion` | String | Complexión física |
| `estatura` | float | Estatura en metros |
| `peso` | float | Peso en kilogramos |
| `senasParticulares` | String | Señas particulares o marcas identificatorias |
| `estado` | String | Estado actual del expediente (ej. "Activo — Sin celda asignada", "Egresado") |
| `nivelConducta` | String | Nivel de conducta clasificado como bajo, medio o alto; determina compatibilidad con celdas |

**Métodos:**

| Método | Descripción |
|---|---|
| `registrarIngreso()` | Registra el ingreso del recluso y genera el expediente con estado inicial |
| `calcularEdad()` | Calcula la edad actual a partir de `fechaNacimiento` |
| `calcularFechaLiberacion()` | Calcula la fecha estimada de liberación según `fechaIngreso` y duración de la pena |
| `actualizarEstado()` | Actualiza el estado del expediente ante cambios operativos (asignación de celda, egreso, etc.) |

**Relaciones:**
- Un `recluso` tiene **una o ninguna** `celda` asignada (1 a N: una celda puede contener múltiples reclusos).
- Un `recluso` puede tener **uno o más** registros de `huellaDactilar`.
- Un `recluso` puede tener **una o más** `fotos`.
- Un `recluso` puede tener **una o más** `pertenencias` registradas.
- Un `recluso` puede tener **uno** `expedienteSeguimiento` asociado al egresar.

---

#### Clase `huellaDactilar`

Almacena los datos biométricos de huella dactilar del recluso.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único |
| `url` | String | URL de almacenamiento de la imagen de la huella |
| `fechaRegistro` | DateTime | Fecha y hora en que fue registrada la huella |

**Métodos:**

| Método | Descripción |
|---|---|
| `registrarHuella()` | Asocia un nuevo registro biométrico al expediente del recluso |
| `eliminarHuella()` | Elimina el registro de huella del sistema |

**Cardinalidad:** `recluso` (1) → `huellaDactilar` (N)

---

#### Clase `fotos`

Gestiona las fotografías del recluso que forman parte del expediente visual.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único |
| `url` | String | URL de almacenamiento de la fotografía |
| `descripcion` | String | Descripción o etiqueta de la foto (frontal, lateral, etc.) |
| `fechaCarga` | DateTime | Fecha y hora de carga al sistema |

**Métodos:**

| Método | Descripción |
|---|---|
| `cargarFoto()` | Sube y registra una nueva fotografía vinculada al recluso |
| `eliminarFoto()` | Elimina una fotografía del expediente |

**Cardinalidad:** `recluso` (1) → `fotos` (N)

---

#### Clase `Pertenencia`

Registra los objetos personales que el recluso entrega al ingresar al establecimiento.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único |
| `descripcion` | String | Descripción del objeto entregado |
| `cantidad` | int | Cantidad de unidades del objeto |
| `observaciones` | String | Notas adicionales sobre el estado o características del objeto |

**Métodos:**

| Método | Descripción |
|---|---|
| `agregarPertenencia()` | Agrega un nuevo ítem a la lista de pertenencias del recluso |
| `eliminarPertenencia()` | Elimina un ítem del listado de pertenencias |

**Cardinalidad:** `recluso` (1) → `Pertenencia` (N)

---

### 2.2 Módulo de Mapa de Celdas

Agrupa las clases que modelan la infraestructura física del establecimiento y la trazabilidad de asignaciones.

#### Clase `celda`

Representa cada unidad de alojamiento del establecimiento penitenciario.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único interno |
| `identificador` | String (unique) | Código visual único de la celda (ej. "C-01") |
| `capacidadMaxima` | int | Número máximo de reclusos que puede alojar |
| `ocupacionActual` | int | Número de reclusos actualmente asignados |
| `nivelConducta` | String | Nivel de conducta requerido para ingresar a esta celda (bajo / medio / alto) |
| `dimensionLargo` | float | Dimensión largo de la celda en metros |
| `dimensionAncho` | float | Dimensión ancho de la celda en metros |
| `estado` | String | Estado operativo de la celda (disponible, ocupada, llena, etc.) |

**Métodos:**

| Método | Descripción |
|---|---|
| `actualizarOcupacion()` | Recalcula `ocupacionActual` tras una asignación o traslado |
| `verificarDisponibilidad()` | Retorna si la celda puede recibir nuevos reclusos según su capacidad |
| `validarConducta()` | Verifica si el nivel de conducta del recluso es compatible con el de la celda |

**Relaciones:**
- Una `celda` puede contener **cero o varios** `recluso` (relación N a 1 desde el recluso).
- Una `celda` puede tener **cero o varios** registros en `historialMovimiento`.

---

#### Clase `historialMovimiento`

Registra cada traslado de un recluso entre celdas, garantizando trazabilidad completa.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único del movimiento |
| `fecha` | Date | Fecha en que ocurrió el traslado |
| `hora` | Time | Hora exacta del traslado |
| `celdaOrigen` | String | Identificador de la celda de origen |
| `celdaDestino` | String | Identificador de la celda de destino |

**Métodos:**

| Método | Descripción |
|---|---|
| `registrarMovimiento()` | Crea un nuevo registro de traslado en el historial |

**Cardinalidad:** `celda` (1) → `historialMovimiento` (N)

---

### 2.3 Módulo Post-Penitenciario

Modela el proceso posterior al egreso del recluso: la creación del perfil de seguimiento, la asignación de responsables y la programación de presentaciones periódicas.

#### Clase `expedienteSeguimiento`

Representa el perfil de reinserción social del egresado y centraliza toda la información del seguimiento post-penitenciario.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único del expediente de seguimiento |
| `idRecluso` | int (FK) | Referencia al expediente penitenciario del recluso egresado |
| `fechaEgreso` | Date | Fecha en que el recluso fue liberado |
| `domicilio` | String | Dirección de residencia del egresado |
| `municipio` | String | Municipio de residencia |
| `contactoEmergenciaNombre` | String | Nombre del contacto de emergencia |
| `contactoEmergenciaTelefono` | String | Teléfono del contacto de emergencia |
| `nivelRiesgo` | String | Nivel de riesgo asignado (bajo / medio / alto); determina la frecuencia de presentaciones |
| `estado` | String | Estado del expediente (ej. "Activo — Sin oficial asignado", "Activo — Oficial asignado", "Alerta Crítica Activa") |

**Métodos:**

| Método | Descripción |
|---|---|
| `completarPerfil()` | Completa los datos de domicilio, contacto y nivel de riesgo del egresado |
| `actualizarEstado()` | Actualiza el estado del expediente según eventos de asignación, incumplimiento o alertas |

**Relaciones:**
- Un `expedienteSeguimiento` está asociado a **un** `recluso`.
- Un `expedienteSeguimiento` genera **uno o varios** registros de `presentacion`.
- Un `expedienteSeguimiento` puede generar **cero o varios** registros de `incumplimiento`.
- Un `expedienteSeguimiento` es **supervisado por** un `supervisorPolicial`.
- Un `expedienteSeguimiento` **es asignado a** un `oficialPolicial`.

---

#### Clase `supervisorPolicial`

Representa al supervisor encargado de gestionar los expedientes y asignar oficiales de seguimiento.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único |
| `nombre` | String | Nombre completo del supervisor |
| `cedula` | String (unique) | Cédula de identidad |
| `email` | String | Correo electrónico institucional |
| `telefono` | String | Número de contacto |
| `seccion` | String | Sección o área de responsabilidad |

**Métodos:**

| Método | Descripción |
|---|---|
| `asignarOficial()` | Asigna un oficial de seguimiento a un expediente pendiente |
| `revisarAlertas()` | Consulta las alertas activas de los expedientes bajo su supervisión |

**Cardinalidad:** `supervisorPolicial` (1) → `expedienteSeguimiento` (N), en relación de supervisión.

---

#### Clase `oficialPolicial`

Representa al oficial de seguimiento directamente responsable del monitoreo de un conjunto de egresados.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único |
| `nombre` | String | Nombre completo del oficial |
| `cedula` | String (unique) | Cédula de identidad |
| `email` | String | Correo electrónico institucional |
| `telefono` | String | Número de contacto |
| `casosActivos` | int | Contador de expedientes activos actualmente asignados |

**Métodos:**

| Método | Descripción |
|---|---|
| `asignarCaso()` | Recibe la asignación de un nuevo expediente de seguimiento |
| `reasignarCaso()` | Transfiere un expediente a otro oficial, con registro obligatorio de motivo |

**Cardinalidad:** `oficialPolicial` (1) → `expedienteSeguimiento` (N), en relación de asignación.

---

### 2.4 Módulo de Control y Disciplina

Agrupa las clases que gestionan el cumplimiento de las presentaciones periódicas y el sistema de alertas escalonadas.

#### Clase `presentacion`

Representa cada cita de comparecencia programada para un egresado en seguimiento.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único de la presentación |
| `fechaProgramada` | Date | Fecha en que está programada la presentación |
| `fechaRealizada` | Date | Fecha real en que el egresado se presentó (nulo si no se presentó) |
| `hora` | Time | Hora de la presentación |
| `estado` | String | Estado actual: pendiente, cumplida o incumplida |
| `observaciones` | String | Notas adicionales del oficial al registrar la presentación |

**Métodos:**

| Método | Descripción |
|---|---|
| `registrarCumplimiento()` | Marca la presentación como cumplida y registra hora real y observaciones |
| `registrarIncumplimiento()` | Marca la presentación como incumplida y activa el proceso de alerta |

**Cardinalidad:** `expedienteSeguimiento` (1) → `presentacion` (N).

---

#### Clase `incumplimiento`

Registra formalmente cada incumplimiento detectado, ya sea por reporte manual del oficial o por detección automática del sistema nocturno.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único del incumplimiento |
| `nivel` | int (1/2/3) | Nivel del incumplimiento según contador acumulado del egresado |
| `fechaEmision` | DateTime | Fecha y hora en que fue generado el incumplimiento |
| `destinatario` | String | Rol o usuario al que se notifica según el nivel |
| `estado` | String | Estado de atención (activa / atendida) |
| `accionRequerida` | String | Descripción de la acción recomendada según el nivel de alerta |

**Métodos:**

| Método | Descripción |
|---|---|
| `generarIncumplimiento()` | Crea el registro formal de incumplimiento e incrementa el contador del egresado |

**Cardinalidad:** `expedienteSeguimiento` (0..N) → `incumplimiento`, `presentacion` (0..1) → `incumplimiento`.

---

#### Clase `alerta`

Modela cada notificación emitida por el sistema de alertas escalonadas.

**Atributos:**

| Atributo | Tipo | Descripción |
|---|---|---|
| `id` | int (PK) | Identificador único de la alerta |
| `nivel` | int (1/2/3) | Nivel de urgencia de la alerta |
| `fechaEmision` | DateTime | Fecha y hora de emisión |
| `destinatario` | String | Destinatario de la notificación (oficial, supervisor o ambos) |
| `estado` | String | Estado de la alerta: activa o atendida |
| `accionRequerida` | String | Mensaje o acción esperada ante la alerta |

**Métodos:**

| Método | Descripción |
|---|---|
| `emitirAlerta()` | Emite la notificación al destinatario correspondiente según el nivel |
| `marcarAtendida()` | Cambia el estado de la alerta a "atendida" una vez gestionada |

**Cardinalidad:** `incumplimiento` (N) → `alerta` (N).

---

## 3. Relaciones del Diagrama — Resumen General

| Clase origen | Cardinalidad | Clase destino | Tipo de relación |
|---|---|---|---|
| `recluso` | 1 — N | `huellaDactilar` | Composición |
| `recluso` | 1 — N | `fotos` | Composición |
| `recluso` | 1 — N | `Pertenencia` | Composición |
| `recluso` | N — 1 | `celda` | Asociación (asignación) |
| `celda` | 1 — N | `historialMovimiento` | Asociación con trazabilidad |
| `recluso` | 1 — 1 | `expedienteSeguimiento` | Asociación (derivada del egreso) |
| `expedienteSeguimiento` | 1 — N | `presentacion` | Composición |
| `expedienteSeguimiento` | 0 — N | `incumplimiento` | Asociación |
| `presentacion` | 0 — 1 | `incumplimiento` | Asociación |
| `incumplimiento` | N — N | `alerta` | Asociación (disparo de alerta) |
| `supervisorPolicial` | 1 — N | `expedienteSeguimiento` | Asociación (supervisión) |
| `oficialPolicial` | 1 — N | `expedienteSeguimiento` | Asociación (asignación) |

---

## 4. Atributos Derivados

El diagrama emplea la notación estándar UML de barra inclinada `/` para señalar los atributos cuyo valor no es ingresado directamente sino calculado a partir de otros.

| Clase | Atributo derivado | Cálculo |
|---|---|---|
| `recluso` | `/edad` | Calculado con `calcularEdad()` a partir de `fechaNacimiento` y la fecha actual |
| `recluso` | `fechaLiberacionEstimada` | Calculado con `calcularFechaLiberacion()` sumando `duracionPenaAnios` y `duracionPenaMeses` a `fechaIngreso` |

---

## 5. Correspondencia con Casos de Uso

| Caso de Uso | Clases involucradas |
|---|---|
| CU-01 — Registrar ingreso de un recluso | `recluso`, `huellaDactilar`, `fotos`, `Pertenencia` |
| CU-02 — Configurar estructura de celdas | `celda` |
| CU-03 — Visualizar mapa de celdas | `celda`, `recluso` |
| CU-04 — Mover recluso entre celdas | `celda`, `recluso`, `historialMovimiento` |
| CU-05 — Ver dashboard penitenciario | `recluso`, `celda` |
| CU-06 — Registrar egreso y activar seguimiento | `recluso`, `celda`, `expedienteSeguimiento` |
| CU-07 — Gestionar perfil y asignación de oficial | `expedienteSeguimiento`, `supervisorPolicial`, `oficialPolicial` |
| CU-08 — Programar calendario de presentaciones | `expedienteSeguimiento`, `presentacion` |
| CU-09 — Gestionar presentaciones del día | `presentacion`, `oficialPolicial` |
| CU-10 — Registrar incumplimiento y consultar historial | `presentacion`, `incumplimiento`, `expedienteSeguimiento` |
| CU-11 — Ver dashboard de control y disciplina | `expedienteSeguimiento`, `presentacion`, `incumplimiento`, `alerta` |
| CU-12 — Detectar presentaciones vencidas (proceso nocturno) | `presentacion`, `incumplimiento` |
| CU-13 — Emitir alerta escalonada | `incumplimiento`, `alerta`, `expedienteSeguimiento` |

---

## 6. Flujo de Estados Modelado por las Clases

### Estado del expediente del recluso (`recluso.estado`)

```
Activo — Sin celda asignada
        │
        ▼ (CU-01 / CU-03)
Activo — Celda asignada
        │
        ▼ (CU-06)
     Egresado
```

### Estado del expediente de seguimiento (`expedienteSeguimiento.estado`)

```
Activo — Sin oficial asignado
        │
        ▼ (CU-07)
Activo — Oficial asignado
        │
        ▼ (3 o más incumplimientos — CU-13)
  Alerta Crítica Activa
```

### Estado de la presentación (`presentacion.estado`)

```
  Pendiente
    │   │
    │   ▼ (CU-09)
    │ Cumplida
    │
    ▼ (CU-10 / CU-12)
 Incumplida ──► genera Incumplimiento ──► activa Alerta
```

---

## 7. Restricciones de Integridad Representadas en el Modelo

Las siguientes restricciones de negocio están implícitas en el modelo de clases y deben ser validadas en la capa de lógica de aplicación:

1. **Unicidad de expediente activo:** El atributo `cedula` en `recluso` es `unique`, impidiendo el registro de un mismo individuo con dos expedientes activos simultáneos.
2. **Restricción de eliminación de celda:** Una `celda` no puede eliminarse si `ocupacionActual > 0`.
3. **Compatibilidad de conducta:** El método `validarConducta()` en `celda` garantiza que `recluso.nivelConducta` sea compatible con `celda.nivelConducta` antes de cualquier asignación o traslado.
4. **Generación única de incumplimiento automático:** El proceso nocturno (CU-12) verifica que no exista ya un `incumplimiento` asociado a la misma `presentacion` antes de crear uno nuevo.
5. **Inmutabilidad del registro de cumplimiento:** Una vez registrado el estado "Cumplida" en una `presentacion`, no puede modificarse sin autorización explícita (restricción funcional documentada en CU-09).
6. **Frecuencia de presentaciones por nivel de riesgo:** La generación de objetos `presentacion` en `programarCalendario()` respeta la frecuencia definida por `expedienteSeguimiento.nivelRiesgo`: semanal (alto), quincenal (medio), mensual (bajo).
