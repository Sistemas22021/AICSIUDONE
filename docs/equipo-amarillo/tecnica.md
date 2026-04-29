# Factibilidad Técnica
## Nexo Criminal

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Equipo:** Amarillo

---

## 1. Introducción

El presente documento evalúa la factibilidad técnica del proyecto Nexo Criminal, determinando si las tecnologías seleccionadas, el equipo de desarrollo, el hardware disponible y el ecosistema de software necesario son adecuados para llevar a cabo la construcción del sistema en los términos planteados.

La conclusión adelantada es que el proyecto es **técnicamente factible**, sustentado en la madurez de las tecnologías elegidas, la existencia de casos de éxito previos y la capacidad técnica del equipo para abordar los desafíos identificados.

---

## 2. Tecnologías a utilizar

### 2.1 Backend

| Tecnología | Versión | Función |
|---|---|---|
| Java | 17 (LTS) | Lenguaje de programación principal |
| Spring Boot | 3.2.5 | Framework de aplicación |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Capa de acceso a datos |
| Hibernate | 6.4 | ORM (mapeo objeto-relacional) |
| jjwt | 0.12 | Generación y validación de tokens JWT |
| Lombok | 1.18 | Reducción de código repetitivo |
| Maven | 3.9 | Gestor de dependencias y build |

**Justificación:** Spring Boot es el framework empresarial más adoptado en el ecosistema Java, ofrece autoconfiguración, ecosistema maduro de librerías, soporte de larga duración y excelente integración con seguridad y persistencia.

### 2.2 Frontend

| Tecnología | Versión | Función |
|---|---|---|
| React | 18.2 | Librería UI |
| TypeScript | 5.x | Lenguaje con tipado estático |
| Vite | 5.x | Build tool de desarrollo |
| React Router | 6.x | Routing del cliente |
| Axios | 1.x | Cliente HTTP |
| Leaflet | 1.9 | Librería de mapas |
| react-leaflet | 4.2 | Bindings de Leaflet para React |
| Cytoscape.js | 3.x | Visualización de grafos |
| Material Symbols | latest | Iconografía |

**Justificación:** React es la librería UI más demandada en la industria. TypeScript reduce errores en runtime y mejora el mantenimiento. Vite ofrece tiempos de build dramáticamente más rápidos que Webpack.

### 2.3 Base de datos

| Tecnología | Versión | Función |
|---|---|---|
| PostgreSQL | 14+ | Motor de base de datos relacional |
| PostGIS | 3.x | Extensión geoespacial |

**Justificación:** PostgreSQL es la base de datos relacional open-source más potente disponible. La extensión PostGIS habilita consultas geoespaciales nativas, fundamentales para el motor Red Thread (cálculos de proximidad, búsqueda por radio).

### 2.4 Inteligencia Artificial

| Tecnología | Función |
|---|---|
| Anthropic Claude API (modelo claude-sonnet-4.5) | Asistente conversacional, predicción de zonas, generación de reportes |
| HTTP nativo Java (HttpClient) | Cliente para integración con la API |

**Justificación:** Claude Sonnet 4.5 ofrece excelente performance en idioma español, costos competitivos por token y políticas de privacidad adecuadas para contextos sensibles. La integración vía HTTP nativo evita dependencias adicionales.

### 2.5 Herramientas auxiliares

| Herramienta | Uso |
|---|---|
| Git + GitHub | Control de versiones |
| Postman / Insomnia | Pruebas manuales de endpoints |
| pgAdmin / DBeaver | Administración de base de datos |
| Visual Studio Code | IDE para frontend |
| IntelliJ IDEA Community | IDE para backend |
| Python 3.10+ | Scripts auxiliares para datos de prueba |

---

## 3. Software para desarrollo

### 3.1 Software requerido en máquinas de desarrollo

| Software | Versión mínima | Propósito |
|---|---|---|
| Java Development Kit (JDK) | 17 | Compilación y ejecución de Java |
| Maven | 3.9 | Build del backend |
| Node.js | 18 LTS | Build del frontend |
| npm | 9 | Gestor de paquetes Node |
| PostgreSQL | 14 | Base de datos local |
| PostGIS | 3.x | Extensión geoespacial |
| Git | 2.40 | Control de versiones |
| Visual Studio Code o IntelliJ IDEA | última estable | Editor / IDE |

### 3.2 Sistema operativo

El sistema es multiplataforma. El equipo desarrolla principalmente sobre:

- Windows 10 / 11
- Ubuntu 22.04 LTS
- macOS 12 o superior

---

## 4. Hardware para desarrollo

### 4.1 Requisitos mínimos por desarrollador

| Componente | Mínimo | Recomendado |
|---|---|---|
| Procesador | Intel Core i5 8va generación / AMD Ryzen 5 | Intel Core i7 / AMD Ryzen 7 |
| Memoria RAM | 8 GB | 16 GB |
| Almacenamiento | 256 GB SSD | 512 GB NVMe |
| Pantalla | 13 pulgadas, resolución 1366×768 | 15 pulgadas, 1920×1080 |
| Conexión a Internet | 10 Mbps | 50 Mbps simétricos |

Se asume que cada miembro del equipo posee un equipo personal que cumple los requisitos mínimos.

---

## 5. Software para lanzamiento y despliegue

### 5.1 Software requerido en el servidor de producción

| Software | Versión | Propósito |
|---|---|---|
| Sistema operativo | Ubuntu Server 22.04 LTS | OS del servidor |
| Java Runtime Environment (JRE) | 17 | Ejecución del JAR del backend |
| PostgreSQL | 14+ | Base de datos en producción |
| PostGIS | 3.x | Extensión geoespacial |
| Nginx | 1.24+ | Proxy reverso y servidor web |
| Certbot | última estable | Certificados SSL gratuitos (Let’s Encrypt) |
| Cron | nativo Linux | Tareas programadas (backups) |

### 5.2 Hardware mínimo para servidor de producción

| Componente | Mínimo | Recomendado |
|---|---|---|
| Procesador | 2 vCPU | 4 vCPU |
| Memoria RAM | 4 GB | 8 GB |
| Almacenamiento | 40 GB SSD | 100 GB SSD |
| Conexión | 100 Mbps | 1 Gbps |

### 5.3 Servicios externos requeridos

| Servicio | Propósito |
|---|---|
| api.anthropic.com | Endpoint de Claude (vía HTTPS) |
| Servidor SMTP (futuro) | Envío de notificaciones por correo |
| Object storage S3 (futuro) | Almacenamiento de fotos en la nube |

---

## 6. Verificación de capacidad técnica

### 6.1 Existencia de antecedentes

Sistemas con propósitos similares han sido desarrollados previamente, validando la viabilidad técnica de la propuesta:

- **Palantir Gotham**: plataforma de análisis criminal usada por agencias federales en Estados Unidos.
- **IBM i2 Analyst’s Notebook**: herramienta clásica de análisis de vínculos, usada por policías europeas.
- **MEMEX (DARPA)**: proyecto de motor de búsqueda especializado en redes criminales.

Estos sistemas validan que la construcción de un motor de correlación de entidades es técnicamente posible. Nexo Criminal se diferencia por su enfoque open-source, su bajo costo de operación y su adaptación al contexto latinoamericano.

### 6.2 Capacidad técnica del equipo

El equipo de desarrollo cuenta con conocimientos sólidos en:

- Programación orientada a objetos (Java, TypeScript).
- Desarrollo web moderno (React, HTML, CSS).
- Bases de datos relacionales (SQL, modelado).
- Consumo de APIs REST.
- Control de versiones con Git.
- Metodologías ágiles (Scrum).

Las áreas que requieren autoaprendizaje durante el proyecto son:

- Visualización avanzada de grafos con Cytoscape.js.
- Cálculos geoespaciales (fórmula de Haversine, PostGIS).
- Integración con APIs de modelos de lenguaje (Anthropic Claude).
- Diseño de motores de reglas heurísticas.

Estas brechas se cubren mediante consulta de documentación oficial y prototipos progresivos.

---

## 7. Conclusión

El proyecto Nexo Criminal es **técnicamente factible**. Las tecnologías seleccionadas son maduras, gratuitas, ampliamente documentadas y cuentan con comunidades activas de soporte. El hardware necesario es estándar y accesible. El software de despliegue es open-source y de bajo costo. El equipo posee las capacidades técnicas requeridas y las brechas identificadas son superables mediante autoaprendizaje en plazos razonables.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*