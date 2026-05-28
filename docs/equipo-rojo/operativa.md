# Factibilidad Operativa

## Viabilidad General

En cuanto a la factibilidad operativa, el sistema define una estructura de roles estricta que garantiza la integridad, trazabilidad y seguridad de la información en cada etapa del proceso investigativo.

---

## Usuarios del Sistema

Los usuarios finales del sistema son los miembros de los organismos de seguridad e investigación criminal que interactúan directamente con los módulos de la plataforma. Se identifican los siguientes perfiles de usuario:

### Guardia u Oficial de Campo
Accede únicamente al módulo de **Registro de Crímenes y Denuncias**. Puede visualizar el panel completo de expedientes activos, crear nuevos registros y consultar el estatus de cualquier caso. No puede editar expedientes ajenos una vez sellados.

### Investigador Asignado
Tiene acceso completo a los módulos de **Gestión de la Escena del Crimen** y **Análisis de Modus Operandi y Firmas**. Solo puede ver los expedientes que le fueron asignados formalmente. Puede agregar información (evidencias, notas) pero no puede eliminar registros ya guardados.

### Analista
Cuenta con permisos exclusivos de **lectura y consulta** sobre el módulo de Análisis de Modus Operandi y Firmas. No puede crear ni modificar registros.

### Gestor / Jefe de Investigación
Supervisa el estado general de los expedientes. Dispone de una consola de supervisión que le permite visualizar el estado del sellado de cada expediente, auditar los cambios realizados mediante registros de auditoría, y acceder a dashboards en tiempo real.

---

## Operadores del Sistema

Los operadores son los responsables de la administración técnica de la plataforma:

### Administrador del Sistema
Responsable de la gestión de cuentas de usuario y de la auditoría de registros (logs). Es el único perfil con acceso total a la configuración del sistema y sus parámetros de seguridad.

---

## Mercado Objetivo

El sistema presenta valor operativo real para las siguientes entidades:

- **Cuerpos policiales municipales o estatales** que aún operan con registros en papel o sistemas no integrados.
- **Empresas de seguridad privada de gran escala**, para la gestión y control de protocolos internos de investigación.
- **Fiscalías**, como herramienta de apoyo para el análisis criminal y la construcción de expedientes.

---

## Estrategias para la Adopción del Sistema

Para garantizar que los usuarios y operadores puedan operar el sistema adecuadamente, se proponen las siguientes estrategias:

### Capacitación por Roles
Se diseñarán sesiones de capacitación diferenciadas para cada perfil de usuario, enfocando los contenidos en las funcionalidades específicas que cada rol utilizará. Esto reduce la curva de aprendizaje y evita confusión operativa.

### Documentación de Usuario
Se entregará un manual de usuario claro y accesible para cada rol, con capturas de pantalla y flujos paso a paso de las operaciones más frecuentes.

### Soporte Técnico Post-Implantación
Durante el período inicial de operación, se asignará un porcentaje del equipo de desarrollo (equivalente al 10–15 % del costo mensual del equipo) para atender dudas, correcciones y ajustes derivados del uso real del sistema.

### Interfaz Guiada
El sistema incluirá elementos de orientación en pantalla (tooltips, validaciones en tiempo real, checklists secuenciales) para reducir el error humano durante el registro de información, especialmente en el módulo de Escena del Crimen.
