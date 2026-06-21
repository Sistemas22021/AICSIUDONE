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
  - **Datos de identidad:** cédula, nombres, apellidos, fecha de nacimiento y botón "Verificar estado" que consulta si la cédula tiene expediente previo.
  - **Información judicial:** delito, número de expediente, tribunal.
  - **Datos de condena:** fecha de ingreso, duración de la pena (años y meses con incrementadores/decrementadores) y cálculo automático de fecha estimada de liberación.
  - **Características físicas:** edad (calculada automáticamente, solo lectura), color de ojos, color de cabello, complexión, estatura, peso, señas particulares.
  - **Datos biométricos:** carga de fotografía frontal, perfil izquierdo, perfil derecho y registro de huellas dactilares (mano izquierda y derecha).
  - **Registro de pertenencias:** lista dinámica con descripción, cantidad y observaciones por fila, botón "+ Agregar Objeto" y checkbox de confirmación firmada.
- Validación de cédula única: no permite registrar si existe un expediente activo con la misma cédula.
- Estado inicial del expediente: *"Activo — Sin celda asignada"*.
- Después de guardar, el sistema pregunta "¿Desea asignar una celda a este recluso ahora?" con opciones "Sí" (abre el mapa para asignar) o "No" (el recluso queda pendiente de asignación).

---

## Módulo 2 — Mapa de Celdas

**Objetivo:** Visualizar y gestionar la asignación de internos a celdas mediante una interfaz interactiva en 2D.

**Funcionalidades incluidas en el MVP:**
- Administrador del Sistema: crear, editar y eliminar celdas (no eliminar si tiene reclusos activos). Datos configurables: identificador único, capacidad máxima, nivel de conducta requerido (bajo/medio/alto), dimensiones físicas (largo y ancho en metros). Los cambios se reflejan inmediatamente en el mapa 2D.
- Mapa 2D interactivo con código de colores: verde (<80% ocupación), amarillo (≥80% ocupación), rojo (lleno). Tooltip con capacidad total, reclusos actuales y nivel de conducta de la celda. Panel de resumen global: total de celdas, reclusos activos, capacidad disponible y porcentaje de ocupación.
- Oficial Penitenciario: asignar reclusos sin celda a celdas disponibles. Al hacer clic en una celda seleccionable, se abre un modal con la lista de reclusos en estado "Activo — Sin celda asignada". Validación de capacidad. El mapa actualiza el contador en tiempo real. Registro en el historial del expediente con usuario, fecha y hora.
- Supervisor Penitenciario: mover reclusos entre celdas mediante menú contextual (clic derecho sobre un recluso en el mapa) o botón "Mover" dentro del expediente. Validación de capacidad de la celda destino y compatibilidad de nivel de conducta. Registro en el historial con usuario, fecha, hora, celda origen y celda destino.
- Acceso directo al expediente del recluso desde el tooltip o popup de la celda en el mapa. Botón "Volver al mapa" dentro del expediente cuando se accede desde esa ruta.

---

## Módulo 3 — Dashboard (Pantalla de Inicio)

**Objetivo:** Proveer una visión inmediata del estado operativo del establecimiento al inicio de cada turno.

**Funcionalidades incluidas en el MVP:**
- Perfiles destinatarios: Oficial Penitenciario y Supervisor Penitenciario.
- Indicadores: total de reclusos activos, porcentaje de ocupación general, ingresos del día, egresos del día, celdas disponibles.
- Los datos se actualizan automáticamente mediante polling cada 5 segundos sin necesidad de recargar la página.
- Botones de acceso directo: registrar ingreso, consultar expediente por búsqueda, acceder al mapa de celdas.
- Los indicadores con alertas activas (ej. ocupación superior al 90%, celdas en límite de capacidad) se resaltan visualmente con color naranja o rojo y un icono de advertencia.

---

## Módulo 4 — Post-Penitenciario

**Objetivo:** Gestionar el egreso de internos, activar el seguimiento de los liberados y registrar las presentaciones periódicas.

**Funcionalidades incluidas en el MVP:**
- Registro de egreso: selección del recluso (buscador por nombre o cédula), motivo de egreso (cumplimiento de condena / libertad condicional / traslado / fallecimiento), fecha y hora (por defecto la actual), observaciones opcionales. Al confirmar: liberación de la celda, actualización del mapa en tiempo real, cambio de estado a "Egresado".
- Activación del seguimiento: si el motivo es cumplimiento de condena o libertad condicional, el sistema crea automáticamente un perfil base de seguimiento y notifica al Oficial de Seguimiento.
- Completar perfil del egresado: datos precargados desde el módulo penitenciario (solo lectura: nombre, cédula, delito, fecha de egreso) más domicilio, municipio, contacto de emergencia (nombre y teléfono) y nivel de riesgo (bajo/medio/alto). Campos obligatorios: domicilio, municipio y nivel de riesgo. Estado: "Activo — Sin oficial asignado". Notificación automática al Supervisor Policial.
- Asignación de oficial de seguimiento: el Supervisor Policial ve los expedientes en estado "Activo — Sin oficial asignado" ordenados por fecha de creación (más antiguos primero), revisa la carga de casos activos de cada oficial y asigna. El oficial seleccionado recibe notificación interna. El expediente cambia a "Activo — Oficial asignado". Reasignación posible con registro obligatorio del motivo.
- Programación de calendario de presentaciones: frecuencia semanal (riesgo alto), quincenal (riesgo medio) o mensual (riesgo bajo). Generación automática de la secuencia de fechas. Ajuste manual de fechas individuales con registro de responsable y motivo. Notificación interna al oficial 24 horas antes de cada presentación. No se puede programar si el expediente no tiene oficial asignado.
- Lista de presentaciones pendientes del día: filtrada por oficial autenticado, muestra nombre del egresado, hora programada y estado. Las presentaciones cuya hora pasó sin registro aparecen resaltadas en rojo como "vencidas". Acceso directo al formulario de registro desde la lista.
- Registro de presentación cumplida: confirmación de asistencia, hora real de presentación, observaciones opcionales. La presentación cambia a estado "Cumplida". Una vez registrada, no puede modificarse sin autorización del supervisor.
- Registro de incumplimiento: confirmación de inasistencia, observaciones. Antes de confirmar, el sistema muestra el contador acumulado de incumplimientos del egresado y el nivel de alerta que se emitirá. Al confirmar: incrementa el contador, la presentación cambia a "Incumplida" y se dispara la alerta escalonada correspondiente. El incumplimiento queda marcado como "Registrado por oficial".

---

## Módulo 5 — Control y Disciplina

**Objetivo:** Monitorear el cumplimiento del plan de presentaciones periódicas y gestionar los incumplimientos y alertas detectados.

**Funcionalidades incluidas en el MVP:**
- Historial de presentaciones del egresado: accesible desde el expediente, orden cronológico descendente, con fecha programada, estado (cumplida/incumplida/pendiente), fecha real, observaciones y nombre del oficial que registró. Resumen en la parte superior: total de presentaciones, cumplidas, incumplidas y porcentaje de cumplimiento. El supervisor ve cualquier egresado; el oficial solo sus asignados.
- Dashboard de control y disciplina: panel integrado dentro del módulo con indicadores de total de egresados activos, presentaciones pendientes del día, incumplimientos sin resolver en los últimos 30 días y alertas activas por nivel (1, 2, 3). El oficial ve solo sus expedientes; el supervisor ve toda la sección. Los datos se actualizan automáticamente cada 10 segundos. Navegación con clic en cualquier indicador a la lista filtrada correspondiente.
- Detección automática de presentaciones vencidas: proceso nocturno ejecutado a las 23:59 que evalúa todas las presentaciones con estado "pendiente" y fecha programada ≤ fecha actual. Para cada una, genera automáticamente un incumplimiento marcado como "Detectado por sistema", incrementa el contador y activa la alerta escalonada. No genera duplicados si ya existe registro manual.
- Sistema de alertas escalonadas:
  - Nivel 1 (1er incumplimiento): notificación exclusiva al Oficial de Seguimiento, con diferenciación visual amarilla. Puede marcarse como atendida con observación opcional.
  - Nivel 2 (2do incumplimiento): notificación al Oficial de Seguimiento y al Supervisor Policial, con diferenciación visual naranja.
  - Nivel 3 (3+ incumplimientos): notificación exclusiva al Supervisor Policial, color rojo, con acción requerida "Solicitud de medida urgente ante tribunal". El expediente cambia automáticamente a estado "Alerta Crítica Activa" y aparece prioritario en listas y búsquedas. El supervisor debe registrar una acción tomada antes de marcar como atendida.

---

## Componente Transversal — Sistema de Alertas Automáticas

**Mecanismo de detección (servicio implementado en Python con cron):**
- Proceso nocturno ejecutado a las 23:59: detecta presentaciones vencidas sin registro y genera automáticamente el incumplimiento con la etiqueta *"Detectado por sistema"*.

**Alertas escalonadas por nivel de incumplimiento:**

| Nivel | Condición | Destinatario | Acción |
|-------|-----------|-------------|--------|
| Nivel 1 | 1er incumplimiento | Oficial de Seguimiento | Notificación interna (color amarillo). Puede marcarse como atendida. |
| Nivel 2 | 2do incumplimiento | Oficial de Seguimiento + Supervisor Policial | Notificación con diferenciación visual naranja. |
| Nivel 3 | 3 o más incumplimientos | Supervisor Policial | Notificación roja con mensaje *"Solicitud de medida urgente ante tribunal"*. El expediente pasa al estado *"Alerta Crítica Activa"* y aparece de forma prioritaria en listas y búsquedas. |

Las notificaciones se muestran dentro del sistema (campana) y opcionalmente por correo electrónico (Resend free tier).

---

## Resumen de Estados y Flujos Clave

| Módulo | Estado inicial | Estados intermedios | Estado final / Evento relevante |
|--------|---------------|--------------------|---------------------------------|
| Registro de Internos | Activo — Sin celda asignada | Activo — Celda asignada | Egreso mediante Módulo 4 |
| Post-Penitenciario | Activo — Sin oficial asignado | Activo — Oficial asignado | Alerta Crítica Activa (3+ incumplimientos) |
| Control y Disciplina | Pendiente (presentación programada) | Cumplida / Incumplida | Incumplimiento → activa alerta escalonada |

---

## Límites del MVP

El MVP **no incluye**:
- Integración con sistemas externos (tribunales, otros cuerpos de seguridad).
- Aplicación móvil nativa.
- Módulo de reportes estadísticos avanzados o exportación de datos masivos.
- Gestión de múltiples establecimientos penitenciarios desde una sola instancia (arquitectura multi-tenancy).
- Registro de salida y retorno temporal de reclusos (traslados a tribunales, emergencias médicas).
- Gestión de fallecimientos con informe de deceso y bloqueo de celdas por investigación.
- Expediente de incidente o siniestro interno.
- Reubicación de emergencia de co-habitantes por siniestro.

Estas funcionalidades quedan documentadas como alcance futuro post-MVP.
