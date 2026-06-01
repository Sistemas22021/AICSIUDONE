# 🟡 Equipo Amarillo — Definición Inicial del Sistema

## Descripción General

El equipo Amarillo tiene como objetivo el desarrollo de un sistema de **análisis de personas y vehículos desaparecidos**, orientado a vincular evidencias, detectar redes criminales mediante análisis de círculo de confianza y determinar puntos de desconexión geográfica.

## Casos de Uso Principales

### CU-01: Registro de Personas y Vehículos Desaparecidos
- Registro de casos activos de personas y vehículos desaparecidos.
- Captura de datos personales, descripción física, última ubicación conocida y fecha de desaparición.
- Vinculación automática con expedientes criminales relacionados.

### CU-02: Vinculación de Delitos y Criminales
- El sistema identifica y vincula delitos relacionados entre sí.
- Permite cruzar información de casos para detectar patrones comunes.
- Facilita la identificación de redes delictivas operando en conjunto.

### CU-03: Análisis de Círculo de Confianza
- Mapeo de relaciones sociales entre individuos involucrados en los casos.
- Identificación del principio "amigo de un amigo" para detectar cómplices encubiertos.
- Análisis de terceros utilizados como intermediarios para ocultar actividad criminal.

### CU-04: Determinación de Punto de Desconexión
- Identificación geográfica del último punto conocido de contacto o avistamiento.
- Mapa de calor y lógica geoespacial para ubicar zonas de alto riesgo.
- Detección de patrones de movimiento vinculados a condiciones indígnas o situaciones de riesgo.

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Investigador | Registra casos y analiza vínculos criminales |
| Analista de Inteligencia | Ejecuta análisis de redes y círculos de confianza |
| Oficial de Campo | Provee información de ubicación y avistamientos |
| Supervisor | Valida hallazgos y coordina acciones de búsqueda |

## Notas Técnicas

- Integración con herramientas de mapas para análisis geoespacial (lógica geográfica).
- Motor de grafos para representar y analizar redes de confianza.
- Algoritmos de detección de patrones aplicados a registros de movimiento.

---
*Definición inicial capturada el 17/04/2026 — Sujeta a revisión en informe de factibilidad.*
