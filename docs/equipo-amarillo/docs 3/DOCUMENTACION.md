# Documentación del Proyecto — Nexo Criminal

**Universidad de Oriente — Núcleo Nueva Esparta**
**Escuela de Ingeniería y Ciencias Aplicadas · Departamento de Licenciatura en Informática**
**Asignatura:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo

---

## Índice

1. [Introducción general](#1-introducción-general)
2. [Objetivos](#2-objetivos)
3. [Planteamiento del problema](#3-planteamiento-del-problema)
4. [Producto Mínimo Viable (MVP)](#4-producto-mínimo-viable-mvp)
5. [Estudio de factibilidad](#5-estudio-de-factibilidad)
6. [Arquitectura del sistema](#6-arquitectura-del-sistema)
7. [Aplicación de los principios SOLID](#7-aplicación-de-los-principios-solid)
8. [Integraciones entre sistemas](#8-integraciones-entre-sistemas)
9. [Calidad y pruebas](#9-calidad-y-pruebas)
10. [Control de versiones y despliegue](#10-control-de-versiones-y-despliegue)
11. [Equipo](#11-equipo)

---

## 1. Introducción general

**Nexo Criminal** es una plataforma de inteligencia criminal que aplica el concepto de *The Red Thread* (El Hilo Rojo) para descubrir vínculos no evidentes entre personas, vehículos, ubicaciones y sucesos delictivos. A diferencia de un registro policial tradicional —que almacena hechos de forma aislada— el sistema cruza coordenadas, tiempos, relaciones sociales y modus operandi para revelar la estructura oculta detrás del crimen.

El sistema se organiza en torno a dos ejes de investigación: el **robo de vehículos** (detección de nodos logísticos, vehículos escolta y modus operandi compartidos) y las **personas desaparecidas** (reconstrucción del círculo de confianza, identificación de intermediarios y puntos de contacto geográficos). El proyecto se encuentra desplegado y funcionando en producción.

### Enlaces en producción

| Recurso | URL |
|---|---|
| Aplicación web (frontend) | https://nexo-criminal-web-final.onrender.com |
| API REST (backend) | https://nexo-criminal-api-final.onrender.com |
| Documentación de la API (Swagger) | https://nexo-criminal-api-final.onrender.com/swagger-ui.html |

Credenciales de demostración: usuario `admin`, contraseña `admin123`.

---

## 2. Objetivos

> **Nota importante sobre la distinción:** los *objetivos del proyecto* describen lo que el equipo se propone lograr como trabajo de ingeniería de software (levantar, analizar, diseñar, implementar, verificar y documentar el sistema). No deben confundirse con las *funcionalidades del sistema* (lo que la aplicación hace una vez construida), que se describen en las secciones de MVP y arquitectura.

### Objetivo general del proyecto

Desarrollar el sistema de inteligencia criminal Nexo Criminal mediante la aplicación de metodologías ágiles de ingeniería de software, ejecutando de forma completa el ciclo de vida del desarrollo —desde el levantamiento y análisis hasta la implementación, verificación y documentación— con el fin de obtener un producto funcional, desplegado y fundamentado en principios sólidos de diseño.

### Objetivos específicos del proyecto

Los objetivos específicos se formulan como etapas del **proceso de desarrollo**, no como funciones del sistema:

- **Levantamiento del proyecto:** analizar el dominio criminal, investigar procedimientos policiales, identificar los actores involucrados y formular el planteamiento del problema, los objetivos y el alcance del producto mínimo viable.
- **Recolección de datos:** revisar bibliografía de manuales policiales públicos, estudiar casos internacionales similares (Palantir Gotham, IBM i2 Analyst's Notebook) y consolidar los hallazgos como insumo para las fases posteriores.
- **Análisis de factibilidad:** elaborar el estudio de factibilidad en sus tres dimensiones (técnica, económica y operativa).
- **Diseño del sistema:** definir el esquema relacional, modelar las entidades del dominio y elaborar los diagramas técnicos (arquitectura, entidad-relación, casos de uso, secuencia y estados).
- **Implementación:** desarrollar de forma iterativa en sprints el backend (Java 17 + Spring Boot), el frontend (React + TypeScript), la integración con PostgreSQL, el motor de reglas heurísticas y la integración con IA.
- **Implantación:** documentar el procedimiento de instalación, elaborar el manual de usuario y definir los requisitos de hardware y software.
- **Verificación y validación:** elaborar y ejecutar un plan de pruebas, documentar casos positivos y negativos, registrar y resolver bugs, y verificar el cumplimiento de los criterios de aceptación. Esta etapa incluye la construcción de una batería de pruebas unitarias automatizadas.
- **Documentación técnica:** redactar los informes de factibilidad y técnico, documentar la arquitectura, generar diagramas y consolidar el backlog.

### Objetivo general del sistema

A modo de referencia, el sistema (el producto construido) tiene como fin permitir a las unidades policiales registrar, vincular y analizar casos de personas y vehículos desaparecidos, apoyando la labor investigativa mediante la detección automatizada de patrones y relaciones. Las capacidades concretas del sistema se detallan en las secciones 4 y 6.

---

## 3. Planteamiento del problema

### Descripción general del área

El área de inteligencia criminal abarca las actividades mediante las cuales las fuerzas del orden recopilan, analizan y correlacionan información operativa para prevenir y resolver delitos. Las fuerzas policiales trabajan con grandes volúmenes de información heterogénea —denuncias, avistamientos, declaraciones, registros vehiculares, ubicaciones georreferenciadas y datos de personas— que tradicionalmente se gestiona mediante hojas de cálculo, archivos físicos y comunicación verbal, generando una operación fragmentada y dependiente de la memoria del investigador.

### El problema central

El problema central es la **incapacidad operativa de las unidades policiales para correlacionar automáticamente información dispersa y descubrir vínculos no evidentes entre entidades del dominio criminal** (personas, vehículos, ubicaciones, sucesos). Se manifiesta en cinco síntomas observables:

1. **Información fragmentada:** una denuncia de robo, el avistamiento posterior y la denuncia de la víctima se registran por separado, y nadie correlaciona los registros.
2. **Análisis manual y lento:** un analista puede tardar entre 40 y 80 horas en correlacionar 50 entidades, cuando un sistema automatizado lo haría en segundos.
3. **Patrones no evidentes que pasan desapercibidos:** por ejemplo, un taller que recibe sistemáticamente vehículos robados puede operar meses sin ser detectado.
4. **Ausencia de visualización unificada:** el analista no puede ver en una sola pantalla todas las relaciones de un caso.
5. **Criticidad temporal:** en casos como desapariciones, cada hora perdida en análisis manual reduce las probabilidades de un desenlace favorable.

Según el Observatorio Venezolano de Violencia (2025), en Venezuela se registraron más de 28.000 casos de robo de vehículos y aproximadamente 4.500 denuncias de personas desaparecidas, muchas sin esclarecer durante el primer año.

### Propuesta de solución

Se propone **Nexo Criminal**, un sistema web de uso interno policial que integra cuatro componentes: un **módulo de gestión de entidades** que centraliza el registro; un **motor de reglas heurísticas (Red Thread Engine)** que descubre vínculos automáticamente; una **capa de visualización** con grafo interactivo y mapas tácticos; y un **asistente basado en IA generativa** (Anthropic Claude). El sistema no reemplaza al analista humano, sino que lo potencia: sugiere vínculos y alertas, pero el analista mantiene la autoridad sobre las decisiones, garantizando explicabilidad (cada vínculo se justifica con una regla concreta).

---

## 4. Producto Mínimo Viable (MVP)

### Filosofía del MVP

El MVP demuestra el valor central del sistema: descubrir un vínculo oculto entre entidades de forma automática. Si no puede enhebrar al menos dos entidades por un hilo común, no es un MVP válido. Siguiendo las indicaciones de la cátedra: debe seguir funcionando al menos 4 semanas, estar abierto a cambios (ágil) y enfocarse en el mayor valor posible.

### Definición del MVP

> *"Un analista debe poder cargar entidades operativas, ejecutar el motor de reglas heurísticas y obtener al menos un hilo rojo descubierto automáticamente, visualizándolo en un grafo y recibiendo una alerta cuando se cumpla la condición de un nodo logístico."*

### Alcance — lo que SÍ incluye

- **Gestión de entidades:** registrar personas, vehículos, ubicaciones y sucesos.
- **Motor de vínculos:** regla temporal-geográfica (nodo logístico), regla de círculo social y regla de modus operandi.
- **Visualización del grafo:** nodo seleccionado y sus vínculos, nodos por color, aristas rojas para los hilos descubiertos.
- **Alertas simples:** notificación en el dashboard al detectar un patrón de alto riesgo.
- **Autenticación:** login con usuario y contraseña; roles Administrador y Analista.

### El registro de hechos: los sucesos como núcleo

Un principio de diseño central del sistema: **todo hecho delictivo se registra como un Suceso**, en un punto único (el módulo `suceso`), no de forma aislada en cada entidad. Cuando se registra un robo, un avistamiento, una transacción o una desaparición, el suceso vincula las entidades involucradas (vehículo, víctima, ubicación, testigos). Las entidades individuales no crean hechos por su cuenta: por ejemplo, el módulo de vehículos no registra un robo aislado, sino que expone consultas sobre el estado de los vehículos (como el listado de los marcados como robados). Este enfoque respeta el propósito del sistema —es una plataforma de denuncias y reportes, no un catálogo genérico de autos— y garantiza la coherencia: un vehículo o una persona cobran sentido operativo únicamente cuando se asocian a un suceso.

### Fuera del alcance del MVP

Roles avanzados granulares, importación masiva desde CSV/Excel por interfaz, integración en tiempo real con sistemas policiales externos (CICPC, SAIME, INTERPOL), generación de reportes PDF, aplicación móvil nativa, Machine Learning predictivo, notificaciones por correo/SMS, y multi-tenancy.

### Funcionalidades adicionales logradas (más allá del MVP base)

Durante el desarrollo, el equipo superó el alcance base e incorporó: el módulo completo de personas desaparecidas con dossier fotográfico, la quinta regla del motor (clusters de desapariciones), el asistente con IA Claude (chat, predicción de zonas, análisis de similitud y reportes), tema claro/oscuro, búsqueda global, exportación a CSV, mapa táctico, y las integraciones con los sistemas de otros equipos (Cian, Blanco, Naranja).

### Criterios de éxito

El MVP se considera exitoso si un analista puede: cargar 10 vehículos, 5 personas y 3 ubicaciones en menos de 15 minutos; ejecutar el motor y obtener al menos un hilo rojo; visualizar el grafo; y recibir una alerta al cumplirse la regla del nodo logístico.

---

## 5. Estudio de factibilidad

### 5.1 Factibilidad técnica

El proyecto es técnicamente factible: se construye íntegramente con tecnologías open-source y maduras (Java 17, Spring Boot, React, PostgreSQL), sin dependencias propietarias costosas. El equipo cuenta con las competencias necesarias, y la arquitectura hexagonal adoptada facilita el mantenimiento y la extensibilidad.

### 5.2 Factibilidad económica

Todos los montos en USD, valores de mercado de Latinoamérica 2026.

**Presupuesto de desarrollo:** con un equipo de 5 desarrolladores full stack junior a un promedio de 800 USD/mes durante 4 meses, la mano de obra asciende a **16.000 USD**. El software de desarrollo es gratuito (JDK, VS Code, IntelliJ Community, Maven, Node, PostgreSQL, Git). Se asume hardware propio.

**Costo de inicialización (one-time):** dominio anual (30), configuración inicial del servidor (100), SSL gratuito (Let's Encrypt) → **130 USD**.

**Costo operativo mensual:** hosting backend (25), base de datos gestionada (15), almacenamiento de fotos (5), backup (5), API de IA (15), frontend en CDN gratuito → **65 USD/mes** (780 USD anuales).

**Resumen:** inversión total año 1 = **16.910 USD**; años siguientes = **780 USD anuales**.

**Análisis costo-beneficio:** aunque el sistema no genera ingresos directos (es de uso interno), su valor se cuantifica en eficiencia: una tarea de correlación que toma 40–80 horas manuales se ejecuta en segundos, y el costo mensual (65 USD) es inferior al de una hora de trabajo de un analista experimentado.

### 5.3 Factibilidad operativa

El sistema es operativamente factible: los usuarios objetivo (analistas, investigadores, funcionarios de patrullaje, supervisores) están identificados y poseen las capacidades necesarias. La interfaz sigue patrones reconocibles (tablas, formularios, mapas) que no requieren conocimientos técnicos avanzados. Se estima que un funcionario con manejo básico de computadora es productivo tras **2.5 horas de capacitación**. Se prevé alta aceptación porque el sistema resuelve un problema real y potencia —no reemplaza— el trabajo del analista.

---

## 6. Arquitectura del sistema

### Visión general

Nexo Criminal adopta **Arquitectura Limpia (Clean Architecture)** con enfoque **hexagonal (puertos y adaptadores)**, organizada por dominios de negocio con separación estricta entre las capas de presentación, aplicación, dominio e infraestructura.

Cada dominio separa sus capas de la siguiente forma:

- **`domain/model`** — modelos de dominio (POJOs puros, sin dependencias de frameworks).
- **`domain/port`** — puertos: interfaces que definen las operaciones que el dominio necesita.
- **`application`** — casos de uso: una clase por operación.
- **`infrastructure/persistence`** — adaptadores que implementan los puertos con JPA, más los mappers.
- **`infrastructure/web`** — controladores REST y DTOs.

### Respeto estricto de las capas

El sistema respeta la cadena **Controladores → Casos de uso/Servicios → Puertos → Adaptadores de persistencia → Base de datos**. Ningún servicio interactúa directamente con la base de datos saltándose la capa de persistencia: todo acceso a datos pasa por el puerto correspondiente, cuya implementación (el adaptador) es la única que conoce JPA. Esto garantiza que la lógica de negocio permanezca independiente de la tecnología de persistencia y que la estructura en capas definida no se rompa en ningún flujo.

### Los sucesos como punto único de registro de hechos

Como se detalla en la sección 4, todos los hechos delictivos se crean de forma centralizada en el módulo `suceso`, que orquesta la vinculación de las entidades participantes. Las entidades individuales exponen únicamente consultas sobre su propio estado. Este diseño evita la duplicación de lógica de registro y mantiene un único punto de verdad para los hechos.

### Motor Red Thread

El corazón analítico aplica cinco reglas heurísticas: nodo logístico, escolta vehicular, círculo de confianza, similitud de modus operandi y clúster de desapariciones. Los vínculos detectados se visualizan como grafo interactivo y generan alertas clasificadas por nivel de riesgo (Crítico, Alto, Medio, Bajo).

### Stack tecnológico

**Backend:** Java 17, Spring Boot 3.2.5, Spring Data JPA, Spring Security + JWT, springdoc-openapi (Swagger), Cloudinary, Maven, Lombok.
**Frontend:** React 18, TypeScript, Vite, React Router, Axios, React-Leaflet, Cytoscape.js.
**Base de datos:** PostgreSQL.
**IA:** Anthropic Claude.
**Infraestructura:** Render (despliegue), Docker, GitHub Actions (CI/CD).

### Actores del sistema

| Actor | Rol |
|---|---|
| Investigador | Registra casos y analiza vínculos criminales. |
| Analista de Inteligencia | Ejecuta análisis de redes y círculos de confianza. |
| Oficial de Campo | Provee información de ubicación y avistamientos. |
| Supervisor | Valida hallazgos y coordina acciones de búsqueda. |

### Estructura de carpetas del backend

Organizada por dominio (no por capa técnica global), aplicando separación hexagonal dentro de cada dominio. Módulos principales: `persona`, `vehiculo`, `suceso`, `desaparecida`, `ubicacion`, `alerta`, `vinculo` (dominios hexagonales); `engine` (motor Red Thread); `robo` (registro transaccional); `ia` (integración Claude); `grafo`; `fuentes` (datos externos); `integracion` (Cian, Naranja); `modus`; `middleware` (cadena de responsabilidad para auth); `config`; `files` (Cloudinary); `security` (JWT); y `excepciones` (manejador global de excepciones).

> **Nota sobre nomenclatura:** el paquete que agrupa el manejo global de excepciones se denomina `excepciones` (anteriormente `common`), de modo que el nombre refleje con precisión su responsabilidad: centralizar el tratamiento de errores de la aplicación.

---

## 7. Aplicación de los principios SOLID

El sistema demuestra los cinco principios SOLID sobre código real y en producción, principalmente sobre el registro transaccional de robos y el módulo de fuentes de datos externas.

### (S) Responsabilidad Única

`RoboCompletoService` tiene una sola responsabilidad: orquestar la transacción del robo. No conoce cómo se persiste una persona o un vehículo; delega en los servicios especializados. Cambia únicamente si cambia el flujo de orquestación, no si cambian las reglas internas de otras entidades.

### (O) Abierto/Cerrado

El módulo de fuentes de datos externas está abierto a extensión, cerrado a modificación. Agregar una fuente nueva no requiere tocar el código existente: basta crear una clase que implemente `FuenteDatosPersona` y registrarla en la fábrica.

### (L) Sustitución de Liskov

Cualquier implementación de `EstrategiaBusqueda` puede sustituir a otra sin alterar la corrección del programa. `BuscadorPersonasService` funciona idénticamente ya reciba `SoloCneStrategy`, `SoloApiStrategy` o `AmbasStrategy`, porque todas respetan el mismo contrato.

### (I) Segregación de Interfaces

Las interfaces son pequeñas y cohesivas. `FuenteDatosPersona` expone solo lo que un consumidor necesita (buscar por cédula y conocer el nombre de la fuente), sin obligar a implementar métodos no usados.

### (D) Inversión de Dependencias

Los módulos de alto nivel dependen de abstracciones. `BuscadorPersonasService` depende de la interfaz `EstrategiaBusqueda`, no de una estrategia concreta; la fábrica `FuentesDatosFactory` decide en tiempo de ejecución qué implementación inyectar según una variable de entorno.

Este mismo principio (DIP) es la base de toda la arquitectura hexagonal: cada caso de uso depende de un puerto (interfaz), y el adaptador concreto de persistencia implementa ese puerto. Por eso los casos de uso son testeables con mocks, sin base de datos real.

---

## 8. Integraciones entre sistemas

Como parte del ecosistema de la cátedra, Nexo Criminal se integra con los sistemas de otros equipos:

- **Equipo Cian (patrullaje):** al enviar un suceso o una desaparición a patrullas, el sistema calcula la prioridad con IA (LOW/MEDIUM/HIGH) y lo remite como incidente georreferenciado al endpoint de despacho de Cian.
- **Equipo Blanco (registro):** consumen la API de personas filtrando por rol (`GET /api/v1/personas?rol=SOSPECHOSO`) para obtener los datos completos de los sospechosos. Esta consulta de lectura es de acceso público; las operaciones de escritura permanecen protegidas.
- **Equipo Naranja (testimonios):** envían la transcripción en texto plano de un testimonio a un endpoint del sistema, y la IA extrae los datos estructurados del hecho (tipo, modus, ubicación, personas mencionadas) para que un analista los revise antes de guardarlos.

---

## 9. Calidad y pruebas

El proyecto incluye **31 tests unitarios** (JUnit 5 + Mockito) sobre los casos de uso de los dominios de persona, vehículo, suceso, desaparecida y alerta. Se ejecutan con `mvn test` y su salida (`Tests run: 31, Failures: 0`) es la evidencia de cumplimiento.

Los tests aprovechan la arquitectura hexagonal: como los casos de uso dependen de puertos (interfaces), se sustituye la base de datos por un mock del puerto, verificando la lógica de negocio de forma aislada. Se prueban tanto los flujos correctos como los casos de borde y las reglas de negocio (por ejemplo, que crear una persona con documento duplicado, o un vehículo con placa duplicada, lance excepción sin llegar a guardar; y que obtener una entidad inexistente lance la excepción correspondiente). Esta testabilidad es una consecuencia directa del cumplimiento del principio de Inversión de Dependencias.

---

## 10. Control de versiones y despliegue

### Estrategia de ramas

El equipo adopta un Git Flow simplificado con dos ramas principales protegidas (`main` para versiones estables entregables, y `dev` para integración continua) y ramas temporales por funcionalidad (`feat/`, `fix/`, `docs/`, `hotfix/`).

Un flujo de despliegue característico es el push a la rama del repositorio de producción:

```
git push deploy refactor-clean:main
```

que sube la rama de trabajo local como rama principal del repositorio de despliegue conectado a Render, separando el trabajo del despliegue.

### Integración continua (CI/CD)

El proyecto cuenta con dos pipelines independientes de GitHub Actions (backend y frontend), que se activan selectivamente según la carpeta modificada. Cada pipeline ejecuta build, lint y test; el del frontend, además, dispara el despliegue en Render mediante un Deploy Hook y ejecuta un **Approval Test** que verifica que el sitio en producción responde con un código de estado satisfactorio antes de dar la entrega por válida.

### Despliegue

El frontend se compila a estáticos y se publica como Static Site; el backend se empaqueta con Docker y corre como servicio web; la base de datos PostgreSQL se aprovisiona como servicio gestionado. Todo en la plataforma Render.

---

## 11. Equipo

**Equipo Amarillo** — Sistemas de Información II — UDO, Núcleo Nueva Esparta.

| Integrante | Cédula | Rol |
|---|---|---|
| John Salazar | 31.648.942 | Líder general / Scrum Master |
| Santiago Ramírez | 31.455.345 | Scrum Master |
| Manuel Rodríguez | 30.911.587 | Tech Lead / Scrum Master |
| Valeria García | 31.649.272 | Scrum Master |
| Isaac Carreño | 31.841.776 | Scrum Master |

---

*Documento elaborado por el Equipo Amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*
