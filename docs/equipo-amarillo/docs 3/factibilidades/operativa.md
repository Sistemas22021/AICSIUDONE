# Factibilidad Operativa
## Nexo Criminal

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Equipo:** Amarillo

---

## 1. Introducción

El presente documento determina la factibilidad operativa del proyecto Nexo Criminal, identificando a los usuarios y operadores del sistema, evaluando si poseen las capacidades necesarias para utilizarlo y definiendo las estrategias requeridas para asegurar una adopción exitosa.

La conclusión adelantada es que el sistema es **operativamente factible**, dado que existen los usuarios objetivo, sus capacidades son compatibles con el sistema y la propuesta de valor es clara y medible.

---

## 2. Contexto operativo del cliente

El sistema está diseñado para **uso interno de fuerzas policiales**. No se expone al público general. Los usuarios finales son funcionarios de cuerpos de inteligencia, divisiones de investigaciones criminales y unidades de análisis táctico.

Para diseñar el sistema con perspectiva operativa real, el equipo realizó investigación documental sobre procedimientos publicados por el Cuerpo de Investigaciones Científicas, Penales y Criminalísticas (CICPC) de Venezuela, manuales públicos del FBI sobre análisis de inteligencia, y revisó el modelo SARA (Scanning, Analysis, Response, Assessment) usado en policía comunitaria.

---

## 3. Áreas policiales y su uso del sistema

Las fuerzas policiales no son monolíticas. Cada unidad tiene funciones diferenciadas que el sistema debe respetar y potenciar.

| Área policial | Función principal | Uso del sistema |
|---|---|---|
| Brigada de Investigaciones | Investiga delitos cometidos | Carga sucesos, registra evidencia, consulta vínculos descubiertos |
| Inteligencia Criminal | Genera análisis predictivo y detecta patrones | Ejecuta el motor Red Thread, interpreta alertas, usa la IA para reportes |
| Patrullaje y Receptivo | Atiende denuncias en primera instancia | Carga datos básicos de personas y vehículos reportados |
| División de Personas Desaparecidas | Casos específicos de desapariciones | Usa el módulo dedicado, gestiona fotos y circunstancias |
| Coordinación / Supervisión | Toma decisiones operativas | Consulta el dashboard, lee reportes ejecutivos generados por IA |

---

## 4. Identificación de usuarios y operadores

### 4.1 Usuarios del sistema (operadores directos)

#### Analista Criminal

- **Perfil**: funcionario con formación técnica en análisis criminal, conocimiento de procedimientos investigativos y manejo intermedio de computadoras.
- **Frecuencia de uso**: diaria, varias horas al día.
- **Operaciones principales**: registro de entidades, ejecución del motor de reglas, interpretación del grafo de vínculos, gestión de alertas, consultas al asistente IA.

#### Funcionario de Patrullaje

- **Perfil**: oficial de calle con manejo básico de computadoras, generalmente sin formación técnica avanzada.
- **Frecuencia de uso**: ocasional, principalmente para registrar denuncias rápidamente.
- **Operaciones principales**: alta de personas, alta de vehículos, registro de avistamientos.

#### Investigador

- **Perfil**: funcionario con experiencia en investigación de campo, conocimiento procedimental.
- **Frecuencia de uso**: diaria, focalizada en sus casos asignados.
- **Operaciones principales**: registro de sucesos, consulta de detalles de entidades, búsqueda de intermediarios entre personas, generación de reportes ejecutivos.

#### Coordinador / Supervisor

- **Perfil**: oficial con responsabilidad de mando, toma de decisiones operativas.
- **Frecuencia de uso**: diaria, breve (consultas rápidas al dashboard).
- **Operaciones principales**: revisión de métricas globales, lectura de alertas críticas, supervisión del trabajo del equipo.

### 4.2 Operadores del sistema (administración técnica)

#### Administrador del sistema

- **Perfil**: funcionario con formación en informática o profesional externo contratado por la institución.
- **Frecuencia de uso**: ocasional.
- **Operaciones principales**: gestión de usuarios y roles, configuración de umbrales del motor, supervisión de logs, gestión de respaldos.

#### Soporte técnico

- **Perfil**: profesional externo o de área de sistemas de la institución.
- **Frecuencia de uso**: bajo demanda.
- **Operaciones principales**: resolución de incidencias, mantenimiento del servidor, actualizaciones del sistema.

---

## 5. Roles definidos en el sistema

El sistema implementa autenticación con JWT y maneja los siguientes roles internos:

| Rol | Capacidades |
|---|---|
| **Administrador** | Acceso completo. Gestión de usuarios. Configuración de umbrales del motor. Eliminación de registros. Acceso a logs. |
| **Analista** | Carga, edición y consulta de entidades. Ejecución del motor. Generación de reportes con IA. No puede eliminar registros ni gestionar usuarios. |
| **Supervisor** (futuro) | Aprobación de alertas. Validación de vínculos descubiertos. Lectura del histórico operacional. |
| **Auditor** (futuro) | Solo lectura. Trazabilidad completa de operaciones. |

Para el alcance del MVP actual se implementan los roles **Administrador** y **Analista**.

---

## 6. Evaluación de capacidades de los operadores

### 6.1 ¿Los gestores del sistema cuentan con las capacidades para operarlo?

**Sí.** El sistema fue diseñado bajo principios de UX claros que minimizan la curva de aprendizaje:

- **Interfaz intuitiva**: utiliza patrones reconocibles (tablas, formularios, mapas, dashboards) que cualquier usuario con experiencia ofimática básica puede operar.
- **No requiere conocimientos técnicos**: ningún funcionario necesita saber SQL, programación o términos técnicos avanzados para usar el sistema.
- **Documentación incluida**: se entrega un manual de usuario con capturas de pantalla e instrucciones paso a paso.
- **Mensajes claros**: el sistema utiliza mensajes en español natural, sin jerga técnica visible al usuario final.
- **Validación inmediata**: los formularios validan datos en tiempo real, evitando errores frustrantes.

### 6.2 Tiempo estimado para productividad

Se estima que un funcionario policial con manejo básico de computadora puede ser productivo en el sistema en **menos de 2 horas de capacitación**.

---

## 7. Estrategias para la operación adecuada del sistema

### 7.1 Plan de capacitación inicial

| Etapa | Contenido | Duración |
|---|---|---|
| Inducción | Presentación del sistema, login, navegación general | 30 minutos |
| Carga de datos | CRUD de personas, vehículos, ubicaciones | 45 minutos |
| Sucesos y alertas | Registro de sucesos, interpretación de alertas | 30 minutos |
| Motor Red Thread | Cuándo ejecutarlo, cómo leer el grafo de vínculos | 30 minutos |
| Asistente IA | Uso del chat conversacional y reportes ejecutivos | 15 minutos |
| **Total capacitación inicial** | | **2.5 horas** |

### 7.2 Materiales de soporte entregados

- **Manual de usuario** en formato PDF con capturas de pantalla.
- **Videos cortos** explicando los flujos principales (login, carga de entidades, ejecución del motor).
- **Documento de preguntas frecuentes** (FAQ) para autoservicio.
- **Tutorial integrado en el sistema** para nuevos usuarios.

### 7.3 Estrategias de adopción

- **Implementación gradual**: comenzar con un grupo piloto reducido (1-2 analistas) antes de extender el sistema a toda la unidad.
- **Acompañamiento del equipo de desarrollo** durante las primeras dos semanas de uso real.
- **Canal de comunicación directo** con el equipo de soporte para reportar problemas o sugerencias.
- **Reuniones quincenales** durante el primer mes para recoger feedback y ajustar el sistema.

### 7.4 Estrategias de mantenimiento operativo

- **Backups automáticos diarios** de la base de datos.
- **Monitoreo del servidor** para detectar caídas o degradación de performance.
- **Actualizaciones periódicas** con mejoras y correcciones de bugs.
- **Auditoría trimestral** del uso del sistema para identificar oportunidades de mejora.

---

## 8. Componente administrativo interno del sistema

El sistema cuenta con un **componente administrativo interno** que garantiza su adaptabilidad operativa:

- **Panel de configuración** accesible solo para administradores, donde se ajustan los umbrales del motor (radios de búsqueda, ventanas temporales, mínimos de coincidencias).
- **Panel de gestión de usuarios** para alta, baja y modificación de cuentas.
- **Sistema de logs** (futura implementación) para trazabilidad de acciones.
- **Backups automáticos** configurables a nivel de servidor.

Sin este componente administrativo, el sistema sería rígido. Su existencia garantiza adaptabilidad a diferentes contextos operativos y unidades policiales.

---

## 9. Aceptación esperada por parte de los usuarios

Se prevé alta aceptación por parte de los usuarios objetivo dado que:

- El sistema **resuelve un problema real**: la dificultad de correlacionar información dispersa.
- **No reemplaza el trabajo humano** sino que lo potencia. El analista mantiene la autoridad sobre las decisiones operativas.
- **Reduce horas de trabajo manual** notablemente, liberando tiempo para análisis estratégico.
- **Genera valor visible** desde la primera ejecución del motor (los analistas ven inmediatamente vínculos que antes pasaban desapercibidos).
- La interfaz es **moderna y agradable**, lo cual contribuye a la satisfacción del usuario.

---

## 10. Conclusión

El proyecto Nexo Criminal es **operativamente factible**. Los usuarios y operadores del sistema están claramente identificados y poseen las capacidades necesarias para utilizarlo tras una capacitación corta de 2.5 horas. Las estrategias de capacitación, materiales de soporte, implementación gradual y mantenimiento operativo aseguran una adopción exitosa. El componente administrativo interno garantiza la adaptabilidad del sistema a diferentes contextos operativos. La aceptación esperada es alta dado que el sistema resuelve un problema real y no reemplaza al usuario humano sino que lo potencia.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*