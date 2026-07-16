# Planteamiento del Problema

## Sistema Integral de Gestión Penitenciaria

---

## 1. Descripción General del Área

El sistema penitenciario venezolano constituye un área crítica de la administración pública que abarca la custodia, registro y seguimiento de personas privadas de libertad. Actualmente, los establecimientos penitenciarios y los cuerpos policiales que gestionan el seguimiento post-liberación operan con procesos manuales, documentación física dispersa y sin estandarización tecnológica. El Observatorio Venezolano de Prisiones (OVP) reporta que los centros penitenciarios funcionan al 200% o más de su capacidad instalada, lo que agrava la complejidad de la gestión y hace urgente la digitalización de sus procesos administrativos.

---

## 2. Detalle del Problema

La ausencia de un sistema centralizado de gestión penitenciaria genera dos problemas críticos y medibles:

**Problema 1 — Gestión penitenciaria manual y desorganizada.**
La información de un recluso (expediente judicial, características físicas, pertenencias, ubicación dentro del penal) está dispersa entre documentos físicos, distintas dependencias institucionales y sin ningún mecanismo de trazabilidad. Los errores en la asignación de celdas, el control de capacidad o la pérdida de pertenencias pueden derivar en conflictos de convivencia, sobrepoblación de espacios o responsabilidad patrimonial para el Estado.

**Problema 2 — Inexistencia de seguimiento post-penitenciario estructurado.**
Cuando una persona es liberada, el Estado prácticamente pierde contacto con ella salvo que reincida. No existe en la mayoría de los cuerpos policiales un mecanismo formal, digitalizado y operativo que gestione las presentaciones periódicas obligatorias, detecte incumplimientos en tiempo real o coordine la participación del egresado en programas de reinserción social. Este vacío institucional convierte la reincidencia en la norma: según el Banco Interamericano de Desarrollo (BID), entre el 40% y el 75% de las personas liberadas en América Latina vuelven a delinquir en los primeros tres años de libertad.

---

## 3. Pronóstico

Si la situación se mantiene sin intervención tecnológica, los efectos esperados son los siguientes:

- **Aumento en la carga administrativa** de los cuerpos policiales y penitenciarios, derivado de la gestión manual de expedientes que requiere duplicación de esfuerzo humano y genera inconsistencias de datos.
- **Incremento de incidentes operativos** relacionados con errores en el control de reclusos: asignaciones incorrectas de celdas, pérdida de pertenencias y ausencia de trazabilidad en traslados o egresos.
- **Tasa de reincidencia sin control ni medición**: al no existir datos estructurados sobre el seguimiento post-penitenciario, las instituciones no pueden medir el impacto de sus acciones ni implementar correcciones basadas en evidencia. La reincidencia continuará deteriorando la seguridad pública sin posibilidad de intervención preventiva.

La digitalización de estos procesos no solo resuelve un problema operativo: establece la infraestructura de datos necesaria para que, por primera vez, las instituciones puedan medir, analizar y mejorar sus procesos de manera sostenida.

---

## 4. Propuesta

Se propone el desarrollo de un **Sistema Integral de Gestión Penitenciaria**, un sistema de información web que digitalice el ciclo completo de vida de un recluso: desde su ingreso al establecimiento penitenciario, pasando por la asignación de celda y el control interno, hasta el seguimiento post-liberación y la detección automatizada de incumplimientos en el período de libertad condicional.

El sistema contempla cinco módulos funcionales como Producto Mínimo Viable (MVP):

1. **Módulo de Registro de Internos**: captura estructurada del expediente digital completo.
2. **Módulo de Mapa de Celdas**: visualización y gestión interactiva de la asignación de reclusos.
3. **Módulo de Dashboard**: indicadores operativos en tiempo real para supervisores y oficiales.
4. **Módulo Post-Penitenciario**: gestión del egreso y activación del seguimiento de liberados.
5. **Módulo de Control y Disciplina**: registro de presentaciones, detección de incumplimientos y sistema de alertas escalonadas.

El stack tecnológico seleccionado (React + Spring Boot + PostgreSQL + Python + DigitalOcean) garantiza que el sistema sea técnicamente alcanzable por el equipo y operativamente sostenible dentro de los costos definidos en el estudio de factibilidad.
