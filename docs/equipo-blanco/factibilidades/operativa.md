# Factibilidad Operativa

## Sistema Integral de Gestión Penitenciaria

---

## Evaluación General

El sistema está diseñado con una curva de aprendizaje baja. Las acciones del día a día se realizan a través de formularios guiados con validaciones automáticas. No se requiere conocimiento técnico para operar el sistema en los roles operativos de campo.

**Conclusión: FACTIBLE.**

---

## Usuarios del Sistema

Los usuarios del sistema son el personal policial y penitenciario que opera directamente los módulos en sus funciones diarias. Cada módulo tiene un área de acción diferenciada y perfiles de acceso restringidos según el rol institucional.

| Módulo | Rol principal | Acciones permitidas |
|--------|--------------|---------------------|
| Registro de Internos | Oficial Penitenciario | Registrar el ingreso de nuevos reclusos con datos personales, judiciales, físicos, biométricos y de pertenencias |
| Mapa de Celdas | Oficial Penitenciario / Administrador del Sistema | Visualizar ocupación, crear/editar/eliminar celdas (Administrador), asignar reclusos a celdas disponibles, mover reclusos entre celdas (Supervisor) |
| Dashboard | Oficial Penitenciario / Supervisor Penitenciario | Consultar métricas clave del establecimiento en tiempo real al inicio de cada turno |
| Post-Penitenciario | Oficial de Seguimiento / Supervisor Policial | Registrar egresos, activar el seguimiento de liberados, asignar oficiales de seguimiento, programar calendarios de presentaciones |
| Control y Disciplina | Oficial de Seguimiento / Supervisor Policial | Registrar asistencias a presentaciones, consultar historial de cumplimiento, gestionar incumplimientos y alertas |

---

## Operadores del Sistema

Los **operadores** son los perfiles con privilegios de administración técnica y configuración del sistema. Se distinguen de los usuarios operativos en que tienen acceso a funciones de configuración estructural:

| Rol | Responsabilidades de operación |
|----|-------------------------------|
| Administrador del Sistema | Crear, editar y eliminar la estructura de celdas del establecimiento. Gestionar cuentas de usuario y asignación de roles. Supervisar el estado general del sistema. |
| Supervisor Penitenciario | Mover reclusos entre celdas, supervisar el estado de ocupación global, acceder al expediente completo de cualquier interno desde el mapa. |
| Supervisor Policial | Visualizar todos los expedientes post-penitenciarios de la sección, asignar y reasignar oficiales de seguimiento, gestionar alertas de nivel 2 y nivel 3. |

---

## Estrategias de Adopción y Capacitación

Para garantizar que los usuarios y operadores puedan operar el sistema adecuadamente desde el primer día, se proponen las siguientes estrategias:

### 1. Diseño orientado a la usabilidad operativa
El sistema implementa formularios guiados con campos claramente etiquetados, validaciones automáticas en tiempo real (por ejemplo, validación de cédula duplicada) y mensajes de error descriptivos. Las acciones críticas (registrar egreso, confirmar incumplimiento, emitir alerta) requieren confirmación explícita para prevenir errores accidentales.

### 2. Capacitación por roles
Se realizará una sesión de capacitación diferenciada por perfil de usuario antes del despliegue en producción:
- **Oficiales Penitenciarios**: enfocada en el flujo de registro de internos y uso del mapa de celdas.
- **Oficiales de Seguimiento**: enfocada en el módulo post-penitenciario y el registro de presentaciones.
- **Supervisores**: enfocada en el dashboard, la asignación de recursos y la gestión de alertas.
- **Administradores del Sistema**: capacitación completa incluyendo configuración de celdas, gestión de usuarios y respaldo de datos.

### 3. Manual de usuario por módulo
Se entregará documentación operativa simplificada (guía rápida de referencia) para cada módulo, diseñada para usuarios sin formación técnica. Incluirá capturas de pantalla de los flujos más frecuentes.

### 4. Sistema de alertas como mecanismo de soporte
El módulo de alertas escalonadas actúa como red de seguridad operativa: si un oficial de seguimiento no registra una presentación, el sistema la detecta automáticamente a las 23:59 y genera el incumplimiento, reduciendo el impacto del error humano sobre la integridad de los datos.

### 5. Soporte post-despliegue
El equipo de desarrollo estará disponible durante el primer mes de operación en producción para atender dudas funcionales, corregir errores detectados en campo y ajustar configuraciones según el feedback de los usuarios reales.
