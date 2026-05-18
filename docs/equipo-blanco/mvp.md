# Producto Mínimo Viable (MVP)

## Sistema Integral de Gestión Penitenciaria

---

## Alcance del MVP

El equipo desarrollará un sistema web funcional que digitalice el ciclo de vida completo de un recluso: desde su ingreso al establecimiento penitenciario hasta la detección automatizada de incumplimientos en su período de libertad condicional. El MVP está compuesto por **cinco módulos funcionales** y un conjunto de **componentes transversales**.

---

## Módulo 1 — Registro de Internos

**Objetivo:** Ingresar al sistema a nuevas personas privadas de libertad, generando un expediente digital único.

**Funcionalidades incluidas en el MVP:**
- Captura estructurada de seis categorías de información:
  - **Datos de identidad:** cédula, nombres, apellidos, fecha de nacimiento.
  - **Información judicial:** delito, número de expediente, tribunal.
  - **Datos de condena:** fecha de ingreso, duración de la pena (cálculo automático de fecha estimada de liberación).
  - **Características físicas:** edad, ojos, cabello, complexión, estatura, peso, señas particulares.
  - **Datos biométricos:** fotografías del recluso y foto de huellas dactilares.
  - **Registro de pertenencias:** lista dinámica de objetos entregados (cantidad, observaciones, confirmación firmada).
- Validación de cédula única (previene expedientes duplicados activos).
- Estado inicial del expediente: *"Activo — Sin celda asignada"*.

---

## Módulo 2 — Mapa de Celdas

**Objetivo:** Visualizar y gestionar la asignación de internos a celdas mediante una interfaz interactiva en 2D.

**Funcionalidades incluidas en el MVP:**

| Rol | Funciones disponibles |
|----|-----------------------|
| Administrador del Sistema | Crear, editar y eliminar celdas (con restricción: no eliminar si tiene reclusos activos). Datos configurables por celda: identificador, capacidad máxima, nivel de conducta requerido, dimensiones físicas. |
| Oficial Penitenciario | Asignar reclusos sin celda a celdas disponibles desde el mapa interactivo. |
| Supervisor Penitenciario | Mover reclusos entre celdas (arrastrar/soltar o menú contextual) con validación de capacidad y compatibilidad de nivel de conducta. |

**Elementos visuales del mapa:**
- Código de colores por celda: verde (disponible), amarillo (≥80% de ocupación), rojo (lleno).
- Tooltip con información resumida: capacidad total, reclusos actuales, nivel de conducta requerido.
- Panel de resumen global: total de celdas, reclusos activos, capacidad disponible y porcentaje de ocupación.
- Acceso directo al expediente del interno desde su celda asignada.

---

## Módulo 3 — Dashboard (Pantalla de Inicio)

**Objetivo:** Proveer una visión inmediata del estado operativo del establecimiento al inicio de cada turno.

**Perfiles destinatarios:** Oficial Penitenciario y Supervisor Penitenciario.

**Indicadores incluidos en el MVP:**
- Total de reclusos activos.
- Porcentaje de ocupación general del establecimiento.
- Ingresos del día actual.
- Egresos del día actual.
- Número de celdas disponibles.

**Elementos de acción rápida:**
- Botones de acceso directo: registrar ingreso, consultar expediente, acceder al mapa de celdas.
- Resaltado visual de alertas operativas (por ejemplo: ocupación superior al 90%, celdas en límite de capacidad).

---

## Módulo 4 — Post-Penitenciario

**Objetivo:** Gestionar el egreso de internos y activar el seguimiento de los liberados.

**Subprocesos incluidos en el MVP:**

**4.1 Registro de egreso**
- Motivos contemplados: cumplimiento de condena, libertad condicional, traslado, fallecimiento.
- Acciones automáticas al registrar el egreso: liberación de la celda asignada, actualización del mapa de celdas, cambio de estado del expediente a *"Egresado"*.

**4.2 Activación del seguimiento** *(solo para cumplimiento de condena o libertad condicional)*
- El Oficial de Seguimiento completa el perfil de egreso con datos complementarios: domicilio, municipio, contacto de emergencia y nivel de riesgo asignado.
- Estado inicial post-egreso: *"Activo — Sin oficial asignado"*.
- Notificación automática al Supervisor Policial.

**4.3 Asignación de oficial de seguimiento**
- El Supervisor visualiza los expedientes pendientes de asignación, revisa la carga de trabajo de los oficiales disponibles y realiza la asignación de forma equitativa.
- La reasignación es posible con registro obligatorio del motivo.
- Estado final: *"Activo — Oficial asignado"*.

**4.4 Programación de presentaciones**
- Frecuencia automática según nivel de riesgo: semanal (alto), quincenal (medio), mensual (bajo).
- Generación automática del calendario de presentaciones con posibilidad de ajuste manual de fechas.
- Notificación interna al oficial asignado 24 horas antes de cada presentación programada.

---

## Módulo 5 — Control y Disciplina

**Objetivo:** Monitorear el cumplimiento del plan de presentaciones periódicas y gestionar los incumplimientos detectados.

**Funcionalidades incluidas en el MVP:**
- Lista diaria de presentaciones por oficial, con estado (pendiente / cumplida / incumplida) y resaltado visual en rojo si la hora de la presentación pasó sin ser registrada.
- Registro de asistencia:
  - **Cumplida:** se registra la hora real de presentación y observaciones. El registro no puede modificarse sin autorización.
  - **Incumplida:** se confirma la inasistencia, se muestra el contador de incumplimientos acumulados y el nivel de alerta a emitir.
- Historial completo del egresado en vista cronológica, con resumen de presentaciones cumplidas, incumplidas y porcentaje de cumplimiento.
- Dashboard específico por perfil:
  - **Oficial de Seguimiento:** solo sus expedientes asignados.
  - **Supervisor Policial:** todos los expedientes de la sección, con indicadores agregados.
- Indicadores del dashboard de control: egresados activos, presentaciones pendientes del día, incumplimientos sin resolver y alertas activas.

---

## Componente Transversal — Sistema de Alertas Automáticas

**Mecanismo de detección:**
- Proceso nocturno ejecutado a las 23:59: detecta presentaciones vencidas sin registro y genera automáticamente el incumplimiento con la etiqueta *"Detectado por sistema"*.

**Alertas escalonadas por nivel de incumplimiento:**

| Nivel | Condición | Destinatario | Acción |
|-------|-----------|-------------|--------|
| Nivel 1 | 1er incumplimiento | Oficial de Seguimiento | Notificación interna. Puede marcarse como atendida. |
| Nivel 2 | 2do incumplimiento | Oficial de Seguimiento + Supervisor Policial | Notificación con diferenciación visual de urgencia. |
| Nivel 3 | 3 o más incumplimientos | Supervisor Policial | Notificación con mensaje *"Solicitud de medida urgente ante tribunal"*. El expediente pasa al estado *"Alerta Crítica Activa"* y aparece de forma prioritaria en listas y búsquedas. |

---

## Resumen de Estados y Flujos Clave

| Módulo | Estado inicial | Estados intermedios | Estado final / Evento relevante |
|--------|---------------|--------------------|---------------------------------|
| Registro de Internos | Activo — Sin celda asignada | — | Asignación de celda mediante Módulo 2 |
| Post-Penitenciario | Activo — Sin oficial asignado | Activo — Oficial asignado | Alerta Crítica Activa (si aplica) |
| Control y Disciplina | Pendiente (presentación programada) | Cumplida / Incumplida | Incumplimiento → activa alerta escalonada |

---

## Límites del MVP

El MVP **no incluye**:
- Integración con sistemas externos (tribunales, otros cuerpos de seguridad).
- Aplicación móvil nativa.
- Módulo de reportes estadísticos avanzados o exportación de datos masivos.
- Gestión de múltiples establecimientos penitenciarios desde una sola instancia (arquitectura multi-tenancy).

Estas funcionalidades quedan documentadas como alcance futuro post-MVP.
