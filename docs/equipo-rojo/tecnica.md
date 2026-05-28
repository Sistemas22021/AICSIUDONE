# Factibilidad Técnica

## Viabilidad General

Desde el punto de vista técnico, el sistema propuesto resulta plenamente factible. El stack tecnológico seleccionado —Java 21, Spring Boot y PostgreSQL— es altamente robusto y adecuado para sistemas críticos.

---

## Tecnologías a Utilizar

### Capacidad y Rendimiento

Spring Boot permite implementar una arquitectura basada en microservicios o en un modelo modular-monolítico, capaz de soportar alta concurrencia. Asimismo, Java 21 (LTS) introduce mejoras significativas en rendimiento y concurrencia mediante los denominados hilos virtuales (*virtual threads*).

### Seguridad

Resulta crítico implementar Spring Security con JWT (JSON Web Tokens) para el control de acceso basado en roles (RBAC), así como el cifrado de datos en reposo a nivel de PostgreSQL.

### Inteligencia Artificial

Spring AI actúa como el puente entre el backend Java y los modelos de inteligencia artificial externos, permitiendo que tanto la generación de embeddings como el análisis con GPT-4 se integren de forma nativa en la aplicación sin complejidad adicional.

---

## Software de Desarrollo

Las herramientas de trabajo contempladas —IDE, Git, Jira o Trello— ofrecen en su mayoría versiones gratuitas para equipos pequeños. Por ejemplo, IntelliJ Community es gratuito.

---

## Hardware de Desarrollo (Requisitos Mínimos por Desarrollador)

Para las tareas de desarrollo, se establecen los siguientes requerimientos mínimos:

- Procesador Intel Core i3 o i5 (de sexta a octava generación)
- 8 GB de memoria RAM
- Disco SSD de 250 GB

---

## Software e Infraestructura para el Despliegue (Producción)

### Opción Principal — AWS (Amazon Web Services)

Para el alojamiento del MVP, se recomienda el uso de servicios en la nube que permitan escalabilidad horizontal. En particular, se sugiere emplear AWS mediante:

- **Instancia EC2 t3.medium** para el backend. Costo aproximado: $30 USD/mes.
- **RDS db.t3.micro con PostgreSQL** como base de datos administrada. Costo aproximado: $15 USD/mes.
- **Amazon S3** para la gestión de evidencias o fotografías. Costo variable desde $0.023 USD/GB.

El costo total mensual en AWS para el MVP se estima entre $50 y $80 USD.

### Opción Alternativa — Plataformas PaaS

Como alternativa costo-eficiente, se consideran plataformas como Render o Railway, las cuales facilitan el despliegue de contenedores Docker (Spring Boot + PostgreSQL) sin requerir una gestión compleja de la infraestructura subyacente, con costos mensuales que parten desde los $20 USD (Railway) hasta $50 USD (Render) por entorno de desarrollo.

### Dominio y Certificado SSL

Costo anual aproximado: $20 USD.
