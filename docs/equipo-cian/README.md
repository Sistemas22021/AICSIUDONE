# 🔵 Equipo Azul — Definición Inicial del Sistema

## Descripción General

El equipo Azul tiene como objetivo el desarrollo de un sistema de gestión de incidentes y patrullaje policial, integrando geolocalización en tiempo real para la coordinación efectiva de recursos en campo.

## Casos de Uso Principales

### CU-01: Registrar Incidente
- El sistema permite registrar un incidente georreferenciado mediante integración con Google Maps.
- Se capturan las coordenadas exactas del lugar del evento.
- La información llega al centro de mando para su procesamiento.

### CU-02: Gestionar Patrullas
- El sistema recibe información de disponibilidad de cada unidad patrullera.
- Se registra y actualiza el estado de cada patrulla (disponible / no disponible).
- Permite asignar patrullas según proximidad y disponibilidad al incidente reportado.

### CU-03: Actualización de Estado y Cierre de Incidente
- El sistema permite actualizar el estado de un incidente en curso.
- Se registra el cierre del incidente una vez atendido.
- Se mantiene historial del ciclo de vida completo del incidente.

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Oficial de Patrulla | Reporta disponibilidad y atiende incidentes |
| Centro de Mando | Coordina y asigna recursos |
| Supervisor | Monitorea estado de incidentes activos |

## Notas Técnicas

- Integración con API de Google Maps para geolocalización.
- Radio de cobertura configurable por unidad.
- Sistema de estados en tiempo real para patrullas e incidentes.

---
*Definición inicial capturada el 17/04/2026 — Sujeta a revisión en informe de factibilidad.*
