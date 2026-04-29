# Producto Mínimo Viable (MVP)

## Descripción general del MVP

El Producto Mínimo Viable (MVP) del sistema de gestión de incidentes y patrullaje policial corresponde a una versión funcional inicial del sistema, orientada a validar el flujo principal de operación entre el centro de mando, la gestión de patrullas y el registro de incidentes con geolocalización.

Este MVP implementa únicamente las funcionalidades esenciales necesarias para simular el proceso operativo completo, sin incluir módulos avanzados o características complementarias.

## Alcance del MVP

El sistema en su versión MVP incluirá las siguientes funcionalidades:

### Registro de incidentes
- Registro de incidentes con información básica.  
- Captura de ubicación geográfica mediante integración con Google Maps.  
- Almacenamiento de coordenadas del evento.  
- Visualización del incidente en un mapa.  

### Gestión de patrullas
- Registro de unidades patrulleras.  
- Actualización del estado de cada patrulla (disponible / no disponible).  
- Consulta de patrullas activas en el sistema.  

### Asignación operativa
- Asignación de patrullas a incidentes según disponibilidad.  
- Relación entre incidente y unidad asignada.  

### Actualización de estados
- Cambio de estado del incidente (activo / en proceso / cerrado).  
- Registro del cierre del incidente.  
- Conservación del historial básico del incidente.  

## Funcionalidades excluidas del MVP

Para mantener el enfoque en el producto mínimo viable, se excluyen las siguientes funcionalidades:

- Autenticación avanzada de usuarios.  
- Análisis estadístico de incidentes.  
- Reportes avanzados o dashboards analíticos.  
- Predicción de incidentes o uso de inteligencia artificial.  
- Optimización automática de rutas.  

## Criterios de validación del MVP

El sistema será considerado funcional en su versión MVP cuando:

- Permita registrar incidentes con ubicación geográfica.  
- Permita visualizar incidentes en un mapa.  
- Permita registrar y gestionar patrullas.  
- Permita asignar patrullas a incidentes.  
- Permita actualizar y cerrar incidentes dentro del flujo operativo.  

## Flujo principal del MVP

El flujo operativo validado será:

**Registro de incidente → Visualización en mapa → Asignación de patrulla → Atención → Cierre del incidente**

Este flujo representa el núcleo funcional del sistema de gestión de incidentes y patrullaje policial.