# 🔵 Equipo Azul — Definición Inicial del Sistema

## Descripción General

El equipo Azul tiene como objetivo el desarrollo de un sistema de gestión de incidentes y patrullaje policial, integrando geolocalización en tiempo real para la coordinación efectiva de recursos en campo.

## Casos de Uso Principales

### CU-01: Registrar Incidente
- El sistema permite registrar un incidente georreferenciado mediante integración con servicios de mapas.
- Se capturan las coordenadas exactas del lugar del evento.
- La información llega al centro de mando para su procesamiento.

### CU-02: Gestionar Patrullas
- El sistema recibe información de disponibilidad de cada unidad patrullera.
- Se registra y actualiza el estado de cada patrulla (disponible / en ruta / ocupada / fuera de servicio).
- Mantiene información actualizada para apoyar las decisiones de asignación operativa.

### CU-03: Asignación Operativa
- Se selecciona el incidente a atender.
- Se selecciona la patrulla disponible más adecuada.
- Se genera la asignación correspondiente.
- Se actualizan los estados relacionados con la operación.

### CU-04: Cierre del Incidente
- Se actualiza el estado final del incidente.
- Se libera la patrulla asignada.
- Se registra la finalización de la atención.
- Se conserva el historial del incidente para futuras consultas.

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Oficial de Patrulla | Reporta disponibilidad y atiende incidentes |
| Centro de Mando | Coordina y asigna recursos |
| Supervisor | Monitorea estado de incidentes activos |

## Notas Técnicas

- Integración con servicios de mapas para geolocalización y visualización cartográfica.
- Sistema de estados en tiempo real para patrullas e incidentes.

