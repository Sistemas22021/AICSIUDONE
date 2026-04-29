# Producto Mínimo Viable (MVP)

## Alcance del MVP

El MVP contempla **9 casos de uso**, seleccionados porque cubren el flujo de extremo a extremo que conecta ambos módulos y generan valor inmediato.

## Casos de Uso del MVP

| Caso de uso                              | Valor que genera |
| ---------------------------------------- | ---------------- |
| Registrar ingreso del recluso            | Elimina el registro manual, crea el expediente digital |
| Asignar celda                            | Control real de capacidad y ubicación |
| Consultar expediente del recluso         | Acceso inmediato a información sin buscar documentos físicos |
| Registrar egreso del recluso             | Dispara automáticamente el seguimiento post-penitenciario |
| Registrar perfil de egreso               | Crea el expediente de seguimiento desde el momento del egreso |
| Asignar oficial de seguimiento           | Garantiza que cada egresado tenga un responsable identificado |
| Programar presentaciones                 | Establece el calendario obligatorio de comparecencias |
| Registrar presentación                   | Registra el cumplimiento con trazabilidad completa |
| Registrar incumplimiento y emitir alerta | Detecta y notifica el riesgo de reincidencia en tiempo real |

## ¿Por qué este MVP es funcional?

### Mejora la productividad
Elimina el registro manual de expedientes, reduce el tiempo de búsqueda de información de minutos a segundos y automatiza las alertas que hoy se gestionan por llamada telefónica o radio.

### Genera valor institucional medible
Por primera vez la institución puede conocer:
- Cuántos reclusos egresaron en un período
- Cuántos egresados incumplieron presentaciones
- Qué oficial tiene más casos activos

### Es considerado funcional
Cubre el ciclo completo: **ingreso → reclusión → egreso → seguimiento → alerta**. Ningún paso crítico del proceso queda sin soporte del sistema.

## Módulos del MVP

### Módulo 1: Seguimiento Post-Penitenciario
- Registrar perfil de egreso
- Asignar oficial de seguimiento
- Programar presentaciones
- Registrar presentación
- Registrar incumplimiento y emitir alerta

### Módulo 3: Gestión Penitenciaria
- Registrar ingreso del recluso
- Asignar celda
- Consultar expediente del recluso
- Registrar egreso del recluso

## Flujo de Integración

El MVP está diseñado para que ambos módulos funcionen de manera integrada:

1. **Ingreso**: Se registra el recluso en el sistema penitenciario
2. **Reclusión**: Se gestiona su expediente durante la condena
3. **Egreso**: Al registrar la salida, se dispara automáticamente el módulo de seguimiento
4. **Seguimiento**: Se asigna un oficial y se programan las presentaciones obligatorias
5. **Alertas**: El sistema detecta incumplimientos y notifica en tiempo real

## Lo que queda fuera del MVP

Las siguientes funcionalidades se desarrollarán en la fase siguiente, una vez que el flujo principal esté estable y validado con usuarios reales:

- Validación biométrica
- Geolocalización con restricciones de zona
- Gestión de visitas
- Traslados entre establecimientos
- Sanciones disciplinarias
- Programas de rehabilitación
- Reportes judiciales formales

## Criterios de Éxito del MVP

El MVP se considerará exitoso si:

1. **Elimina el registro manual**: 100% de los ingresos y egresos se registran digitalmente
2. **Reduce tiempo de búsqueda**: El tiempo para consultar un expediente se reduce de minutos a segundos
3. **Automatiza alertas**: Las alertas por incumplimiento se emiten automáticamente sin intervención manual
4. **Genera métricas**: La institución puede producir reportes básicos de actividad mensual

## Tiempo Estimado de Desarrollo

**Duración total**: 5 meses

**Distribución por fase**:
- Levantamiento y análisis: 3 semanas
- Diseño (modelo de datos + arquitectura): 2 semanas
- Desarrollo Módulo 3 (Gestión Penitenciaria): 6 semanas
- Desarrollo Módulo 1 (Seguimiento Post-Penitenciario): 6 semanas
- Integración y pruebas: 3 semanas
- Despliegue y capacitación: 2 semanas

## Entregables del MVP

1. **Aplicación web funcional** con los 9 casos de uso implementados
2. **Base de datos** configurada y con datos de prueba
3. **Manuales de usuario** por rol
4. **Documentación técnica** para administradores
5. **Scripts de despliegue** y configuración
6. **Plan de capacitación** para usuarios finales
