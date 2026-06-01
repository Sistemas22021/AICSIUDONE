# 🟢 Equipo Verde — Definición Inicial del Sistema

## Descripción General

El equipo Verde tiene como objetivo el desarrollo de un módulo de **análisis forense holístico**, enfocado en la gestión y comparación de evidencias físicas recolectadas en escenas del crimen, con especial énfasis en análisis balístico y evidencia dubitativa.

## Casos de Uso Principales

### CU-01: Módulo de Análisis Holístico
- Visualización gráfica integrada de evidencias y casos activos.
- Dashboard con estado de evidencias pendientes de análisis.
- Vista consolidada de hallazgos por caso.

### CU-02: Registro de Evidencia
El sistema registra los datos de origen de cada evidencia, incluyendo:

| Campo | Descripción |
|-------|-------------|
| Número de Caso | Identificador único del caso asociado |
| Fecha de Recolección | Fecha y hora en que se recolectó la evidencia |
| Tipo de Munición | Clasificación del proyectil o evidencia balística |
| Deformación | Grado y tipo de deformación detectada |
| Peso (gramos) | Peso registrado de la evidencia |

### CU-03: Comparación Balística
- Carga de imágenes microscópicas para análisis comparativo.
- Herramienta para confrontar proyectiles de prueba con evidencias de escena.
- Soporte para evidencia dubitativa recolectada en el lugar del hecho.

### CU-04: Evidencia Dubitativa
- Registro de evidencias cuya procedencia o autenticidad está en duda.
- Seguimiento del estado de análisis (pendiente, en proceso, confirmada, descartada).

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Perito Forense | Registra y analiza evidencias |
| Investigador | Consulta comparaciones y reportes balísticos |
| Supervisor | Valida hallazgos y cierra análisis |

## Notas Técnicas

- Soporte para carga de imágenes de alta resolución (microscópicas).
- Módulo de comparación balística con plantillas estandarizadas.
- Trazabilidad cadena de custodia por evidencia.

---
*Definición inicial capturada el 17/04/2026 — Sujeta a revisión en informe de factibilidad.*
