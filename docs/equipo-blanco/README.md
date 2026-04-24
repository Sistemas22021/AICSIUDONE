# ⚪ Equipo Blanco — Definición Inicial del Sistema

## Descripción General

El equipo Blanco tiene como objetivo el desarrollo de un sistema integral de **seguimiento, gestión penitenciaria y rehabilitación**, que permita controlar expedientes de condenados, gestionar su participación en programas de rehabilitación y administrar su vida dentro del sistema penitenciario.

## Casos de Uso Principales

### CU-01: Perfil del Oficial de Seguimiento
- Gestión del expediente completo de cada condenado asignado a un oficial.
- Registro de evaluaciones periódicas de comportamiento y cumplimiento.
- Documentación de incumplimientos, cambios de domicilio y resistencias al programa.

### CU-02: Programa de Rehabilitación
- Registro y consulta de programas de rehabilitación activos.
- Control de participación de cada recluso en los programas asignados.
- Generación de reportes de avance y cumplimiento por participante.

### CU-03: Reportes Judiciales y de Seguimiento
- Generación de informes judiciales requeridos por el proceso legal.
- Reportes de caso de seguimiento para autoridades competentes.
- Historial trazable de toda la vida judicial del condenado.

### CU-04: Gestión Penitenciaria y de Reclusos
- Registro de ingreso al sistema penitenciario con identidad verificada.
- Control y seguimiento de reclusos activos.
- Gestión de traslados entre centros penitenciarios.
- Registro de comportamiento, sanciones y participación en actividades.

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Oficial de Seguimiento | Gestiona expedientes y evalúa condenados |
| Administrador Penitenciario | Controla ingresos, traslados y comportamiento |
| Juez / Autoridad Judicial | Consulta reportes y toma decisiones legales |
| Facilitador de Programa | Registra participación en actividades de rehabilitación |

## Notas Técnicas

- Control de acceso granular por rol y nivel de autorización.
- Historial completo e inmutable de cada acción sobre los expedientes.
- Generación automatizada de reportes en formatos estándar para autoridades judiciales.

---
*Definición inicial capturada el 17/04/2026 — Sujeta a revisión en informe de factibilidad.*
