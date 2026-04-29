# Planteamiento del Problema
## Proyecto: Nexo Criminal

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo

---

## 1. Descripción general del área

El área de inteligencia criminal abarca el conjunto de actividades, procesos y herramientas mediante los cuales las fuerzas del orden recopilan, analizan y correlacionan información operativa para prevenir y resolver delitos. En el contexto policial, esta área incluye unidades especializadas como brigadas de investigaciones, divisiones de inteligencia táctica, departamentos de análisis criminal y secciones dedicadas a casos específicos como personas desaparecidas o robo de vehículos.

Las fuerzas policiales trabajan con grandes volúmenes de información heterogénea: denuncias ciudadanas, reportes de avistamientos, declaraciones de testigos, registros vehiculares, ubicaciones georreferenciadas de hechos delictivos y datos de personas (víctimas, sospechosos, testigos). Tradicionalmente, esta información se gestiona mediante hojas de cálculo, archivos físicos, pizarras operativas y comunicación verbal entre unidades, lo cual genera una operación fragmentada y dependiente de la memoria individual del investigador.

En Latinoamérica, el área enfrenta un escenario particularmente desafiante por la sofisticación creciente de las redes delictivas, que operan transversalmente a través de múltiples tipos de delitos (robos vehiculares conectados con redes de desguace, secuestros vinculados con redes de trata, etc.) y por la limitación tecnológica de los cuerpos policiales en la región.

## 2. Detalle del problema

El problema central es la **incapacidad operativa de las unidades policiales para correlacionar automáticamente información dispersa y descubrir vínculos no evidentes entre entidades del dominio criminal** (personas, vehículos, ubicaciones, sucesos).

Concretamente, el problema se manifiesta en cinco síntomas observables:

**Primero**, la información está fragmentada. Una denuncia de robo vehicular se registra en un sistema, el avistamiento posterior del vehículo en otro, la denuncia de la persona dueña en un tercero, y nadie correlaciona los tres registros. Cada brigada trabaja con su propia información sin un repositorio común.

**Segundo**, los procesos de análisis son manuales y lineales. Un analista experimentado puede tardar entre 40 y 80 horas en correlacionar manualmente 50 entidades para detectar patrones, cuando un sistema automatizado podría hacerlo en segundos. La memoria del investigador se convierte en cuello de botella.

**Tercero**, los patrones criminales no evidentes pasan desapercibidos. Por ejemplo, un taller mecánico que recibe sistemáticamente vehículos robados puede operar durante meses sin ser detectado porque ningún oficial conecta los puntos individuales en el mapa.

**Cuarto**, no existe una visualización unificada del panorama operativo. El analista no puede ver, en una sola pantalla, todas las relaciones entre las entidades de un caso, lo que dificulta el análisis estratégico.

**Quinto**, en casos de alta urgencia como personas desaparecidas, la velocidad de correlación es crítica para la vida de la víctima. Cada hora perdida en análisis manual reduce las probabilidades de un desenlace favorable.

Según datos del Observatorio Venezolano de Violencia (OVV) del año 2025, en Venezuela se registraron más de 28.000 casos de robo de vehículos y aproximadamente 4.500 denuncias de personas desaparecidas, de las cuales un porcentaje significativo permanece sin esclarecer durante el primer año.

## 3. Pronóstico

De no implementarse soluciones tecnológicas adecuadas, el pronóstico para el área de inteligencia criminal es desfavorable en al menos cuatro dimensiones:

**Dimensión cuantitativa**: el volumen de datos crecerá exponencialmente en los próximos años por la digitalización de denuncias, la proliferación de cámaras de seguridad y la expansión de los registros vehiculares. Los métodos manuales actuales ya están en su límite operativo y se saturarán completamente.

**Dimensión cualitativa**: las redes delictivas se sofistican más rápido que las herramientas para combatirlas. Sin sistemas que correlacionen automáticamente, la brecha entre la complejidad del delito y la capacidad de respuesta institucional se ampliará, con consecuencias directas sobre la efectividad policial y la percepción ciudadana de seguridad.

**Dimensión humana**: en casos críticos como secuestros y desapariciones, la incapacidad de detectar patrones rápidamente puede comprometer vidas. Cada año, casos que podrían haberse resuelto con una correlación temprana terminan archivados.

**Dimensión institucional**: las unidades policiales que no modernicen sus herramientas perderán competitividad operativa frente a unidades que sí lo hagan, generando desequilibrios entre regiones y dependencias.

Si la situación actual se prolonga cinco años más sin intervención tecnológica, se estima un incremento sostenido de casos sin resolver, una pérdida acumulada de horas-analista equivalente a millones de dólares en productividad perdida, y una erosión de la confianza ciudadana en la capacidad investigativa de las fuerzas del orden.

## 4. Propuesta

Se propone el desarrollo de **Nexo Criminal**, un sistema web de inteligencia criminal diseñado específicamente para apoyar a las unidades policiales en la correlación automatizada de entidades operativas y en el descubrimiento de patrones delictivos no evidentes.

La propuesta consiste en una plataforma de uso interno policial que integra cuatro componentes principales:

**Un módulo de gestión de entidades** que centraliza el registro de personas, vehículos, ubicaciones georreferenciadas, sucesos delictivos y casos de personas desaparecidas, eliminando la fragmentación de la información.

**Un motor de reglas heurísticas denominado Red Thread Engine** que aplica cinco reglas sobre los datos para descubrir vínculos automáticamente: detección de nodos logísticos delictivos, identificación de vehículos escolta, búsqueda de intermediarios entre personas, agrupación de sucesos por modus operandi similar y detección de clusters de desapariciones.

**Una capa de visualización avanzada** que incluye un grafo interactivo de vínculos descubiertos, mapas tácticos georreferenciados y dashboards con métricas operativas, permitiendo al analista comprender de un vistazo el panorama completo.

**Un asistente conversacional basado en inteligencia artificial generativa** (Anthropic Claude) que apoya al analista mediante consultas en lenguaje natural, predicción de zonas de búsqueda para personas desaparecidas, análisis de similitud entre casos y generación automática de reportes ejecutivos.

La propuesta se construye con tecnologías open-source de bajo costo (Java + Spring Boot, React + TypeScript, PostgreSQL con PostGIS), lo cual la hace viable económicamente para instituciones públicas latinoamericanas. El sistema es de uso interno y no estará abierto al público.

Nexo Criminal no pretende reemplazar al analista humano, sino potenciarlo: el sistema sugiere vínculos y alertas, pero el analista mantiene la autoridad sobre las decisiones operativas. Esto garantiza explicabilidad legal (cada vínculo descubierto se justifica con una regla concreta) y respeta la cadena de custodia de la inteligencia criminal.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*