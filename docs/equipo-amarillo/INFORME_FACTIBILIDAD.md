# Informe de Factibilidad
## Proyecto: Nexo Criminal — Sistema de Inteligencia de Precisión

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo
**Fecha de entrega:** Agosto 2026

### Integrantes del equipo

| Nombre | Cédula de Identidad | Rol |
|---|---|---|
| Manuel Rodríguez | 30.911.587 | Full Stack Junior |
| Santiago Ramírez | 31.455.345 | Full Stack Junior |
| Valeria García | 31.649.272 | Full Stack Junior |
| Isaac Carreño | 31.841.776 | Full Stack Junior |
| John Salazar | 31.648.942 | Full Stack Junior |

---

## Tabla de Contenido

1. [Planteamiento del Problema](#1-planteamiento-del-problema)
2. [Objetivos de Investigación](#2-objetivos-de-investigación)
3. [Informe de Factibilidad](#3-informe-de-factibilidad)
   - 3.1 [Factibilidad Económica](#31-factibilidad-económica)
   - 3.2 [Factibilidad Técnica](#32-factibilidad-técnica)
   - 3.3 [Factibilidad Operativa](#33-factibilidad-operativa)
4. [Producto Mínimo Viable (MVP)](#4-producto-mínimo-viable-mvp)
5. [Conclusión](#5-conclusión)

---

## 1. Planteamiento del Problema

Las fuerzas policiales de Latinoamérica enfrentan una problemática crítica al investigar delitos de alto impacto como robos de vehículos, desapariciones forzadas y redes delictivas organizadas. La información relevante está fragmentada en múltiples fuentes (denuncias, reportes vehiculares, testimonios, ubicaciones geográficas, registros de personas), lo que dificulta detectar patrones criminales y vínculos no evidentes entre casos aparentemente desconectados.

Según datos del Observatorio Venezolano de Violencia (OVV) del año 2025, en Venezuela se registraron más de 28.000 casos de robo de vehículos y aproximadamente 4.500 denuncias de personas desaparecidas, de las cuales un porcentaje significativo permanece sin esclarecer durante el primer año. La carencia de herramientas tecnológicas que permitan correlacionar automáticamente personas, vehículos, ubicaciones y sucesos genera una brecha operativa que retrasa la resolución de casos y, en escenarios críticos como secuestros o trata, puede comprometer vidas.

El pronóstico, en ausencia de soluciones tecnológicas adecuadas, es que esta brecha continúe ampliándose: el volumen de datos crece exponencialmente, mientras que los métodos de análisis tradicionales (pizarras físicas, hojas de cálculo, intercambio manual de informes entre unidades) son lineales y dependientes de la memoria del investigador. Un analista experimentado puede tardar semanas en descubrir un vínculo que un sistema automatizado podría revelar en segundos.

Adicionalmente, la cooperación interna entre unidades policiales se ve limitada por la ausencia de un repositorio centralizado de inteligencia. Cada brigada trabaja con su propia información, perdiendo oportunidades de detectar redes que operan transversalmente entre diferentes tipos de delitos.

La problemática se sintetiza en tres ejes: **(1)** ausencia de herramientas que correlacionen entidades automáticamente, **(2)** dependencia de procesos manuales y memoria humana para el descubrimiento de patrones, y **(3)** falta de un sistema unificado que centralice la inteligencia operativa de las unidades policiales.

---

## 2. Objetivos de Investigación

### Objetivo General

Diseñar, desarrollar e implementar un sistema de inteligencia criminal denominado **Nexo Criminal**, que permita a las unidades policiales registrar, correlacionar y visualizar entidades operativas (personas, vehículos, ubicaciones y sucesos) mediante reglas heurísticas automatizadas y asistencia con inteligencia artificial generativa, para acelerar la detección de patrones delictivos no evidentes.

### Objetivos Específicos

#### 2.1 Diseño de Base de Datos

- Modelar el dominio criminal identificando las entidades principales (Persona, Vehículo, Ubicación, Suceso, Alerta, Vínculo, PersonaDesaparecida) y sus relaciones.
- Diseñar un esquema relacional normalizado en PostgreSQL con extensión PostGIS para soportar consultas geoespaciales.
- Definir índices, restricciones de integridad y políticas de eliminación que mantengan la consistencia operativa.
- Implementar un mecanismo de detección de duplicados en vínculos para evitar redundancia en el grafo de inteligencia.

#### 2.2 Desarrollo del Sistema

- Construir una API REST con Java 17 y Spring Boot 3.2 que exponga los recursos del dominio bajo el patrón controller-service-repository.
- Implementar un motor de reglas heurísticas (Red Thread Engine) capaz de descubrir vínculos automáticamente entre entidades, con cinco reglas diferenciadas: nodo logístico, vehículo escolta, círculo de confianza, modus operandi y cluster de desapariciones.
- Desarrollar una interfaz web con React 18 y TypeScript que permita la operación del sistema a usuarios no técnicos.
- Integrar visualización de grafos interactiva mediante Cytoscape.js y mapas operativos con Leaflet.
- Incorporar un asistente de inteligencia artificial basado en Claude (Anthropic) para análisis predictivo, generación de reportes ejecutivos y predicción de zonas de búsqueda.

#### 2.3 Testing

- Validar el correcto funcionamiento de cada módulo mediante pruebas manuales documentadas en un plan de pruebas.
- Realizar pruebas de integración entre el motor de reglas, la base de datos y el frontend.
- Verificar la generación correcta de alertas ante escenarios de prueba diseñados específicamente para disparar cada regla.
- Probar el flujo completo de subida de fotografías y persistencia en disco local.

#### 2.4 Implementación

- Documentar el procedimiento de instalación local en sistemas operativos Windows y Linux.
- Generar un manual de usuario y un manual de despliegue.
- Definir los requisitos mínimos de hardware tanto para el ambiente de desarrollo como para el ambiente de producción inicial.
- Establecer un plan de migración a entornos productivos cuando la institución policial lo requiera.

---

## 3. Informe de Factibilidad

### 3.1 Factibilidad Económica

#### Presupuesto de Desarrollo

El proyecto se desarrolla con un equipo de cinco desarrolladores Full Stack Junior. Los salarios se calculan tomando como referencia el rango medio para perfiles junior fullstack en Latinoamérica durante el año 2026, considerando contratación bajo modalidad freelance o jornada parcial dado el carácter académico del proyecto.

**Salario base mensual por desarrollador junior fullstack en Latinoamérica:**

| País de referencia | Rango mensual (USD) | Promedio considerado |
|---|---|---|
| Venezuela | 350 – 700 | 500 |
| Colombia | 800 – 1.400 | 1.100 |
| Argentina | 700 – 1.300 | 1.000 |
| México | 900 – 1.600 | 1.250 |

**Para efectos de este proyecto** se utiliza un salario promedio de **800 USD mensuales** por desarrollador junior fullstack, modalidad full-time, considerando un equipo distribuido en Latinoamérica.

#### Cálculo de costo del equipo

| Concepto | Cantidad | Costo unitario (USD) | Costo total (USD) |
|---|---|---|---|
| Desarrolladores Full Stack Junior | 5 | 800 / mes | 4.000 / mes |
| Duración estimada del proyecto | 4 meses | — | 16.000 |
| **Total mano de obra** | | | **16.000 USD** |

#### Costos de Infraestructura

##### Costos de arranque (one-time)

| Concepto | Costo (USD) |
|---|---|
| Dominio (.com.ve o .gob.ve) anual | 30 |
| Certificado SSL Let's Encrypt | 0 (gratuito) |
| Configuración inicial servidor | 100 |
| Setup de PostgreSQL gestionado | 0 (incluido en hosting) |
| **Total arranque** | **130 USD** |

##### Costos recurrentes mensuales (operación)

| Concepto | Costo mensual (USD) |
|---|---|
| Hosting backend (servidor 2 vCPU, 4GB RAM) | 25 |
| Hosting base de datos PostgreSQL gestionada | 15 |
| Almacenamiento de fotos (10GB iniciales) | 5 |
| Hosting frontend (CDN, ej. Cloudflare Pages) | 0 |
| Backup automatizado | 5 |
| Anthropic Claude API (1.000 consultas/mes) | 15 |
| **Total mensual** | **65 USD** |

##### Costos de cargo único (hardware del equipo)

Se asume que cada desarrollador cuenta con su propia computadora con capacidad mínima para desarrollo. En caso de necesidad de adquirir equipos:

| Concepto | Costo unitario (USD) | Cantidad | Total (USD) |
|---|---|---|---|
| Laptop dev (16GB RAM, SSD 512GB) | 700 | 5 | 3.500 |
| Monitor adicional (opcional) | 150 | 5 | 750 |
| **Total hardware (escenario completo)** | | | **4.250 USD** |

> Para este proyecto se asume que el equipo ya cuenta con su hardware, por lo cual este costo no se incluye en el presupuesto final.

#### Resumen económico

| Categoría | Monto (USD) |
|---|---|
| Mano de obra (4 meses, 5 personas) | 16.000 |
| Costos de arranque | 130 |
| Costos recurrentes (4 meses) | 260 |
| **Total inversión inicial del proyecto** | **16.390 USD** |
| **Costo mensual de operación post-lanzamiento** | **65 USD/mes** |

#### Análisis de costo-beneficio

Aunque el sistema **no genera ingresos directos** (es de uso interno policial), su valor se mide en términos de eficiencia operativa:

- Un analista experimentado puede tardar entre **40 y 80 horas** en correlacionar manualmente 50 entidades. Con Nexo Criminal, esa tarea se ejecuta en **menos de 5 segundos**.
- La detección temprana de un nodo logístico en una red de robo de vehículos puede prevenir entre **5 y 15 robos adicionales** mensuales en una zona afectada.
- El costo mensual de operación (65 USD) es inferior al costo de una hora de trabajo de un analista experimentado en la región.

**Conclusión económica**: el proyecto es **económicamente factible** dado que su inversión total es modesta para una institución pública y su mantenimiento mensual es de bajo impacto presupuestario.

---

### 3.2 Factibilidad Técnica

#### Tecnologías evaluadas y seleccionadas

##### Backend

| Tecnología | Versión | Justificación |
|---|---|---|
| Java | 17 (LTS) | Lenguaje robusto, soporte empresarial a largo plazo, comunidad amplia. |
| Spring Boot | 3.2.5 | Framework consolidado para APIs REST, autoconfiguración, ecosistema maduro. |
| Spring Security + JWT | 6.x | Autenticación stateless, estándar de la industria. |
| Hibernate / JPA | 6.4 | ORM líder, abstracción del acceso a BD, soporte nativo en Spring. |
| PostgreSQL | 14+ | Base de datos relacional open-source, soporte de tipos complejos. |
| PostGIS | 3.x | Extensión geoespacial necesaria para consultas por proximidad. |
| Maven | 3.9 | Gestor de dependencias estándar en proyectos Java. |
| Lombok | 1.18 | Reduce código repetitivo (getters, setters, constructores). |

##### Frontend

| Tecnología | Versión | Justificación |
|---|---|---|
| React | 18.2 | Librería UI más utilizada, ecosistema extenso. |
| TypeScript | 5.x | Tipado estático, menos errores en runtime. |
| Vite | 5.x | Build tool ultra rápido, mejor que Webpack para desarrollo. |
| React Router | 6.x | Routing declarativo estándar. |
| Axios | 1.x | Cliente HTTP con interceptors, mejor manejo de errores. |
| Leaflet + react-leaflet | 1.9 / 4.2 | Mapas open-source, sin costos de licencia (a diferencia de Google Maps). |
| Cytoscape.js | 3.x | Líder en visualización de grafos para web. |
| Material Symbols | — | Iconografía consistente y gratuita. |

##### Inteligencia Artificial

| Tecnología | Justificación |
|---|---|
| Anthropic Claude API (claude-sonnet-4.5) | Modelo de lenguaje de última generación, excelente para análisis de texto en español, costos competitivos. |
| HTTP nativo Java (HttpClient) | No agrega dependencias adicionales, control total sobre la integración. |

##### Otros

| Herramienta | Uso |
|---|---|
| Git + GitHub | Control de versiones y trabajo colaborativo. |
| Postman / curl | Testing manual de endpoints. |
| pgAdmin / DBeaver | Administración de base de datos. |
| Visual Studio Code / IntelliJ IDEA | IDEs de desarrollo. |
| Python 3.10+ | Scripts auxiliares para carga de datos de prueba. |

#### Verificación de capacidad técnica

##### ¿Existe la posibilidad técnica de hacerlo?

Sí. Sistemas similares ya han sido desarrollados en el ámbito internacional:

- **Palantir Gotham** — plataforma de análisis criminal usada por agencias como FBI y CIA. Es comercial y costosa (>1M USD anual).
- **i2 Analyst's Notebook (IBM)** — herramienta clásica de análisis de vínculos, ampliamente usada en policía europea.
- **MEMEX (DARPA)** — proyecto de motor de búsqueda especializado en redes criminales.

Estos sistemas validan que la propuesta es técnicamente viable. Nexo Criminal se diferencia por ser **open-source, low-cost y adaptable a realidades latinoamericanas**.

##### ¿El equipo tiene la capacidad técnica?

El equipo cuenta con conocimientos sólidos en:
- Programación orientada a objetos (Java).
- Desarrollo web moderno (React, TypeScript).
- Bases de datos relacionales (SQL, modelado).
- Consumo de APIs REST.
- Control de versiones con Git.

Las áreas que requirieron investigación durante el proyecto fueron:
- Visualización de grafos con Cytoscape.
- Cálculos geoespaciales (Haversine, PostGIS).
- Integración con APIs de modelos de lenguaje (Anthropic Claude).
- Diseño de motor de reglas heurísticas.

Estas brechas se cubrieron mediante autoaprendizaje y consulta de documentación oficial, demostrando capacidad de aprendizaje autónomo.

#### Requisitos mínimos de hardware

##### Ambiente de desarrollo (por desarrollador)

| Componente | Mínimo | Recomendado |
|---|---|---|
| Procesador | Intel Core i5 8va gen / AMD Ryzen 5 | Intel Core i7 / AMD Ryzen 7 |
| RAM | 8 GB | 16 GB |
| Almacenamiento | 256 GB SSD | 512 GB NVMe |
| Sistema operativo | Windows 10 / Ubuntu 20.04 / macOS 12 | Windows 11 / Ubuntu 22.04 / macOS 14 |
| Conexión a Internet | 10 Mbps | 50 Mbps |

##### Ambiente de producción (servidor)

| Componente | Mínimo | Recomendado |
|---|---|---|
| Procesador | 2 vCPU | 4 vCPU |
| RAM | 4 GB | 8 GB |
| Almacenamiento | 40 GB SSD | 100 GB SSD |
| Sistema operativo | Ubuntu Server 22.04 LTS | Ubuntu Server 24.04 LTS |
| Conexión | 100 Mbps simétricos | 1 Gbps |

#### Software requerido (instalación)

**En el servidor:**
- Java Runtime Environment 17+
- PostgreSQL 14+ con extensión PostGIS
- Nginx (proxy reverso)
- Certbot (SSL gratuito)

**En las máquinas de desarrollo:**
- JDK 17 (Eclipse Temurin recomendado)
- Maven 3.9+
- Node.js 18+ y npm 9+
- PostgreSQL 14+ (o conexión a base remota)
- Git 2.40+

**Conclusión técnica**: el proyecto es **técnicamente factible**. Las tecnologías seleccionadas son maduras, gratuitas y existen casos previos exitosos. El equipo posee las capacidades necesarias y los requisitos de hardware son alcanzables.

---

### 3.3 Factibilidad Operativa

#### Contexto operativo del cliente

El sistema está diseñado para **uso interno de fuerzas policiales**. No será expuesto al público general. Los usuarios finales son funcionarios de cuerpos de inteligencia, divisiones de investigaciones criminales y unidades de análisis táctico.

Para diseñar el sistema con perspectiva operativa real, el equipo realizó investigación documental y audiovisual:

- Análisis de procedimientos publicados por el CICPC (Cuerpo de Investigaciones Científicas, Penales y Criminalísticas — Venezuela).
- Documentales sobre análisis criminal: *"Mindhunter"* (Netflix), *"The First 48"* (A&E), *"Confronting a Serial Killer"* (Starz).
- Lectura de manuales públicos del FBI sobre análisis de inteligencia.
- Revisión del modelo SARA (Scanning, Analysis, Response, Assessment) usado en policía comunitaria.

#### Definición de áreas policiales y división del trabajo

Las fuerzas policiales no son monolíticas. Cada unidad tiene funciones diferenciadas que el sistema debe respetar:

| Área policial | Función principal | Uso del sistema |
|---|---|---|
| Brigada de Investigaciones | Investiga delitos ya cometidos | Carga sucesos, registra evidencia, consulta vínculos. |
| Inteligencia Criminal | Genera análisis predictivo y patrones | Ejecuta el motor Red Thread, interpreta alertas. |
| Patrullaje y Receptivo | Atiende denuncias en primera instancia | Solo carga datos básicos de personas y vehículos. |
| División de Personas Desaparecidas | Casos específicos de desapariciones | Módulo dedicado, gestión de fotos y circunstancias. |
| Coordinación / Supervisión | Toma decisiones operativas | Consulta dashboard, lee reportes ejecutivos generados por IA. |

#### Roles del sistema (actores)

El sistema implementa autenticación con JWT y maneja los siguientes roles:

| Rol | Capacidades |
|---|---|
| **Administrador** | Acceso completo, gestión de usuarios, configuración del motor, eliminación de registros. |
| **Analista** | Carga, edición y consulta de entidades. Ejecuta el motor. Genera reportes con IA. No puede eliminar registros. |
| **Supervisor** (futuro) | Aprobación de alertas, validación de vínculos, lectura del histórico. |
| **Auditor** (futuro) | Solo lectura. Trazabilidad de operaciones. |

Para el MVP actual se implementaron los roles **Administrador** y **Analista**.

#### ¿El gestor del sistema cuenta con las capacidades para operarlo?

Sí. El sistema fue diseñado bajo principios de UX claros:

- **Curva de aprendizaje baja**: la interfaz utiliza patrones reconocibles (tablas, formularios, mapas, dashboards) que cualquier usuario con experiencia ofimática básica puede operar.
- **No requiere conocimientos técnicos**: ningún funcionario necesita saber SQL, Python o programación para usar el sistema.
- **Documentación incluida**: el sistema dispone de un manual de usuario integrado (accesible desde el menú de ayuda).
- **Tutoriales rápidos**: se incluye un tutorial paso a paso para nuevos usuarios.

Se estima que un funcionario policial con manejo básico de computadora puede ser productivo en el sistema en menos de **2 horas de capacitación**.

#### Plan de capacitación

| Etapa | Contenido | Duración |
|---|---|---|
| Inducción | Presentación del sistema, login, navegación general | 30 min |
| Carga de datos | CRUD de personas, vehículos, ubicaciones | 45 min |
| Sucesos y alertas | Registro de sucesos, interpretación de alertas | 30 min |
| Motor Red Thread | Cuándo ejecutarlo, cómo leer el grafo | 30 min |
| Asistente IA | Cómo usar la IA para reportes y predicciones | 15 min |
| **Total** | | **~2.5 horas** |

#### Componente administrativo del sistema

El sistema **sí cuenta con un componente administrativo interno** que se compone de:

- Panel de configuración accesible solo para administradores donde se ajustan los umbrales del motor (radios de búsqueda, ventanas temporales, mínimos de coincidencias).
- Panel de gestión de usuarios.
- Sistema de logs (futura implementación) para trazabilidad de acciones.
- Backups automáticos de base de datos (configurables a nivel de servidor).

Sin este componente administrativo, el sistema sería inflexible. Su existencia garantiza adaptabilidad a diferentes contextos operativos.

#### Aceptación esperada

Se prevé alta aceptación por parte de los usuarios objetivo dado que:

- El sistema **resuelve un problema real**: la dificultad de correlacionar información dispersa.
- **No reemplaza el trabajo humano** sino que lo potencia (el analista sigue tomando las decisiones).
- **Reduce horas de trabajo manual** notablemente.
- **Genera valor visible** desde la primera ejecución del motor.

**Conclusión operativa**: el proyecto es **operativamente factible**. Existen los usuarios objetivo, sus capacidades son compatibles con el sistema, y la propuesta de valor es clara y medible.

---

## 4. Producto Mínimo Viable (MVP)

### Filosofía del MVP

El MVP de Nexo Criminal demuestra el valor central del sistema: **descubrir un vínculo oculto entre entidades de forma automática**. Si el MVP no puede enhebrar al menos dos entidades por un hilo común, no se considera un MVP válido.

Siguiendo las indicaciones de la cátedra:

- El MVP debe seguir funcionando durante al menos **4 semanas** después de su lanzamiento.
- Debe estar **abierto a cambios** (metodología ágil).
- Debe enfocarse en el **mayor valor posible**, no en perfección técnica.
- El testing exhaustivo no genera valor tangible a corto plazo, por lo cual se prioriza funcionalidad sobre cobertura de pruebas.

### Alcance del MVP (lo que SÍ incluye)

#### 1. Gestión básica de entidades (CRUD)
- Registrar **Personas** (víctimas, sospechosos, testigos, propietarios).
- Registrar **Vehículos** (placa, marca, modelo, año, color, estado).
- Registrar **Ubicaciones** (dirección, latitud/longitud, tipo).
- Registrar **Sucesos** (robo de vehículo, desaparición, avistamiento, transacción sospechosa).

#### 2. Motor de vínculos — reglas mínimas

- **Regla temporal-geográfica (nodo logístico)**: detecta si dos o más sucesos ocurrieron en un radio configurable y dentro de una ventana temporal determinada.
- **Regla de círculo social**: detecta si dos personas tienen un contacto común (intermediario).
- **Regla de modus operandi**: agrupa sucesos que comparten el mismo método delictivo.

#### 3. Visualización básica del grafo
- Pantalla que muestra los nodos y sus vínculos de primer grado.
- Nodos diferenciados por color según tipo (persona, vehículo, ubicación, suceso).
- Aristas rojas (el "hilo rojo") entre nodos vinculados.

#### 4. Alertas simples
- Notificación en el dashboard cuando se detecta un nodo logístico o cualquier vínculo de alto riesgo.

#### 5. Autenticación básica
- Login con usuario y contraseña.
- Roles iniciales: **Administrador** y **Analista**.

### Fuera del alcance del MVP

Estos puntos quedan como trabajo futuro para iteraciones posteriores:

- Roles avanzados (supervisor, auditor con permisos granulares).
- Importación masiva desde CSV/Excel desde la interfaz de usuario.
- Integración real con cámaras CCTV, sistemas GPS o registros telefónicos.
- Generación automática de reportes en PDF.
- Aplicación móvil para oficiales en campo.
- Machine Learning predictivo (los vínculos se descubren con reglas determinísticas).
- Notificaciones por correo electrónico o SMS.
- Multi-tenancy (múltiples unidades policiales en una misma instancia).

### Criterios de éxito del MVP

El MVP se considera exitoso si un analista puede:

1. Cargar **10 vehículos**, **5 personas** y **3 ubicaciones** en menos de 15 minutos.
2. Ejecutar el motor de vínculos y obtener al menos un "hilo rojo" descubierto automáticamente.
3. Visualizar el grafo resultante en pantalla.
4. Recibir una alerta cuando se cumple la regla del nodo logístico.

### Funcionalidades adicionales implementadas (más allá del MVP base)

Durante el desarrollo, el equipo logró superar el alcance del MVP base e incorporar:

- **Módulo de Personas Desaparecidas** completo, con dossier fotográfico.
- **5ta regla del motor** (cluster de desapariciones).
- **Asistente IA con Claude** para chat conversacional, predicción de zonas de búsqueda y generación de reportes ejecutivos.
- **Tema claro y oscuro** configurable.
- **Búsqueda global** en todas las entidades.
- **Exportación de datos** a CSV en cada módulo.

### Plan de entregas del MVP (4 semanas iniciales)

| Semana | Entregable |
|---|---|
| 1 | Repositorio listo, base de datos creada, CRUD de Personas y Vehículos funcional. |
| 2 | CRUD de Ubicaciones y Sucesos. Script Python para cargar datos de prueba. |
| 3 | Motor de vínculos (reglas 1 y 3). Primera pantalla de grafo. |
| 4 | Regla del círculo social. Sistema de alertas. Demo integrada. |

### Definition of Done (DoD)

Una historia de usuario se considera "hecha" cuando:

- El código está integrado a la rama `develop`.
- La funcionalidad fue demostrada en ambiente local.
- Existe documentación básica en el README del módulo correspondiente.
- El Product Owner aprobó la demo.

### Escenario de demo para presentación

> *"En una ciudad hipotética, tres camionetas fueron reportadas como robadas en las últimas 24 horas. El sistema descubre que las tres tuvieron su última ubicación conocida cerca del mismo taller. Dispara una alerta de Nodo Logístico. Al abrir el grafo, el analista ve un taller rodeado de tres vehículos enhebrados en rojo. Luego carga una desaparición y el sistema revela que la víctima comparte un contacto común con un sospechoso previamente registrado."*

Esta narrativa cubre los cuatro casos de uso principales más el "plot twist" del módulo de desapariciones, y se completa en cinco minutos de demo.

---

## 5. Conclusión

El proyecto **Nexo Criminal** es **factible en sus tres dimensiones evaluadas**:

| Dimensión | Resultado | Justificación |
|---|---|---|
| **Económica** | ✅ Factible | Inversión inicial moderada (~16.400 USD), operación mensual baja (65 USD/mes). |
| **Técnica** | ✅ Factible | Tecnologías maduras y gratuitas, equipo capacitado, casos de éxito previos demuestran viabilidad. |
| **Operativa** | ✅ Factible | Usuarios objetivo claramente identificados, propuesta de valor evidente, capacitación corta requerida. |

El sistema responde a una problemática real del contexto latinoamericano en materia de inteligencia criminal, ofrece valor inmediato a sus usuarios potenciales y se construye con un stack tecnológico sostenible y de bajo costo de mantenimiento.

Se recomienda **proceder con el desarrollo del proyecto** en los términos planteados, con seguimiento ágil mediante metodología Scrum, sprints de dos semanas y entregas incrementales validadas con el Product Owner.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*