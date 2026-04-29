# Factibilidad Operativa

## Usuarios y Operadores del Sistema

El sistema será operado por personal policial y penitenciario. Cada rol tiene un área de acción diferenciada — no es lo mismo registrar el ingreso de un recluso que aprobar un traslado o generar un reporte para un tribunal.

### Roles del Sistema

| Rol                        | Perfil                                        | Módulo principal | Acciones permitidas |
| -------------------------- | --------------------------------------------- | ---------------- | ------------------- |
| Oficial Penitenciario      | Guardia o funcionario de control diario        | Módulo 3         | Registrar ingresos, egresos, asignar celdas, registrar visitas, sanciones y participación en programas |
| Supervisor Penitenciario   | Jefe de guardia o coordinador de pabellón      | Módulo 3         | Aprobar traslados, gestionar alertas, generar reportes, gestionar capacidad de pabellones |
| Oficial de Seguimiento     | Policía asignado al acompañamiento post-penitenciario | Módulo 1  | Registrar presentaciones, incumplimientos, cambios de domicilio y participación en programas |
| Supervisor Policial        | Comandante o jefe de sección                  | Módulo 1         | Asignar oficiales, cerrar casos, aprobar reportes judiciales y gestionar alertas escalonadas |
| Administrador del Sistema  | Personal técnico institucional                | Ambos módulos    | Gestión de usuarios, roles, configuración y auditoría de accesos |

## Curva de Aprendizaje

El sistema está diseñado con una **curva de aprendizaje baja** para el personal policial. Las acciones del día a día son:

- Registrar un ingreso
- Marcar una presentación
- Registrar una visita

Estas acciones se realizan mediante formularios guiados con validaciones automáticas. **No se requiere conocimiento técnico** para operar el sistema en los roles operativos.

### Requisitos Técnicos por Rol

**Roles Operativos** (Oficial Penitenciario, Oficial de Seguimiento, Supervisores):
- Alfabetización digital básica
- Capacidad de navegar formularios web
- No requieren conocimiento técnico

**Administrador del Sistema**:
- Conocimiento técnico básico
- Gestión de usuarios y accesos
- Configuración del sistema
- Este rol puede cubrirse con personal del área de informática de la institución o con uno de los desarrolladores del equipo durante una fase inicial de transición

## Estrategias de Capacitación

### 1. Capacitación Inicial (Previo al Lanzamiento)

| Grupo objetivo            | Duración | Contenido principal |
| ------------------------- | -------- | ------------------- |
| Administradores           | 8 horas  | Gestión de usuarios, configuración, auditoría, resolución de problemas |
| Supervisores              | 4 horas  | Flujos de aprobación, generación de reportes, gestión de alertas |
| Oficiales operativos      | 2 horas  | Registro diario, formularios básicos, navegación del sistema |

### 2. Material de Soporte

- **Manual de usuario** por rol (PDF descargable)
- **Videos tutoriales cortos** (3-5 minutos) para cada acción principal
- **Guía de referencia rápida** (infografía de 1 página) pegada en las estaciones de trabajo

### 3. Soporte Post-Lanzamiento

- **Mesa de ayuda interna**: Un miembro del equipo de desarrollo disponible durante el primer mes
- **Canal de consultas**: Grupo de Telegram/WhatsApp para resolución rápida de dudas
- **Sesiones de retroalimentación**: Reuniones semanales durante el primer mes para identificar problemas de usabilidad

## Componente de Administración del Sistema

El sistema necesita un componente de administración interna para mantenerse operativo a largo plazo. Este componente incluye:

### Panel de Administración de Usuarios
- Alta, baja y modificación de cuentas de policías y supervisores
- Gestión de roles y permisos

### Gestión de Pabellones y Celdas
- Configuración inicial del establecimiento penitenciario
- Número de pabellones
- Capacidad por celda

### Bitácora de Auditoría
- Registro de quién accedió a qué expediente y cuándo
- Requisito de seguridad en sistemas institucionales

### Configuración de Alertas
- Umbrales para las alertas escalonadas
- Días de anticipación al vencimiento de condena
- Frecuencia de presentaciones según nivel de riesgo

**Sin este componente el sistema no puede sostenerse operativamente**, ya que el personal rota y la institución necesita gestionar usuarios sin depender del equipo de desarrollo.

## Factores de Éxito Operativo

Para que el sistema sea operativamente viable, se requiere:

1. **Compromiso institucional**: Designación formal de un administrador del sistema
2. **Infraestructura mínima**: Computadoras con navegador web actualizado en cada estación de trabajo
3. **Conectividad estable**: Conexión a internet confiable en los puntos de operación
4. **Respaldo de datos**: Política de backups automáticos configurada desde el inicio

## Conclusión

El sistema puede operarse **sin conocimiento técnico en los roles de campo**. Se requiere designar un **administrador del sistema dentro de la institución** para garantizar la continuidad operativa.

El proyecto es **OPERATIVAMENTE FACTIBLE CON CONDICIÓN**: la institución debe comprometerse a asignar un responsable técnico interno para la administración del sistema.
