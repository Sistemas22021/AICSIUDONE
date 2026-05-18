# Factibilidad Técnica

## Sistema Integral de Gestión Penitenciaria

---

## Evaluación General

El sistema no requiere tecnologías experimentales ni de nicho. Todos los componentes propuestos cuentan con amplia documentación, comunidades activas y antecedentes en producción en sistemas de naturaleza similar. El equipo de desarrollo posee la formación técnica de base para trabajar con el stack seleccionado, y existen referencias directas de sistemas de gestión penitenciaria en producción que validan que este tipo de solución es técnicamente alcanzable.

**Conclusión: FACTIBLE.**

---

## Stack Tecnológico de Desarrollo

| Capa | Tecnología | Justificación |
|------|-----------|---------------|
| Frontend | React + Vite | Componentes reutilizables; ideal para construir los 5 módulos del MVP de forma independiente y con actualizaciones reactivas de la UI |
| Backend principal | Java 17 + Spring Boot (Community) | Framework empresarial de código abierto, robusto para la gestión de expedientes y reglas de negocio complejas |
| Servicio de alertas | Python 3 + scripts programados (cron) | Procesamiento de fechas, evaluación de incumplimientos y envío de notificaciones de forma automatizada |
| Base de datos | PostgreSQL | Motor relacional estable y gratuito; soporta correctamente las relaciones entre reclusos, celdas, presentaciones y alertas |
| ORM | Hibernate / Spring Data JPA | Reduce errores en consultas SQL y mapea las entidades del sistema a clases Java |
| Servidor web | Nginx | Sirve el frontend React como archivos estáticos y actúa como proxy inverso hacia el backend Spring Boot |
| Autenticación | Spring Security + JWT | Control de acceso por roles sin dependencias externas de pago |
| Control de versiones | Git + GitHub | Estándar de la industria; permite trabajo colaborativo entre los 5 integrantes del equipo |

---

## Software y Hardware de Desarrollo

### Hardware (entorno local por integrante)
Cada integrante del equipo trabaja en su máquina personal. Los requerimientos mínimos para ejecutar el stack de desarrollo localmente son:

- Procesador: 2 núcleos o más
- RAM: 8 GB (mínimo recomendado para correr el backend Java + instancia local de PostgreSQL)
- Almacenamiento: 20 GB disponibles para dependencias, builds e imágenes de base de datos

### Software de desarrollo (sin costo)
- Java 17 JDK (OpenJDK)
- Node.js + npm (para el frontend React + Vite)
- Python 3 (para el servicio de alertas)
- PostgreSQL (instancia local para desarrollo)
- IntelliJ IDEA Community Edition o VS Code
- Postman o Bruno (pruebas de API)
- Figma (plan gratuito, para diseño de interfaces)
- Git + GitHub (repositorio compartido del equipo)

---

## Infraestructura de Despliegue (Producción)

Se adopta una arquitectura de servidor único (monolito desplegado), adecuada para el volumen de usuarios del MVP y que minimiza los costos operativos.

### Componentes alojados en un único Droplet (DigitalOcean)

| Componente | Descripción |
|-----------|-------------|
| Backend Java (Spring Boot) | API REST del sistema; gestiona toda la lógica de negocio |
| Servicio de alertas (Python + cron) | Proceso nocturno que evalúa incumplimientos y emite alertas escalonadas |
| Frontend React (Nginx) | Archivos estáticos servidos por Nginx |
| Base de datos PostgreSQL | Instancia única en el mismo servidor; respaldada mediante snapshots mensuales |

### Especificaciones del servidor de producción

| Recurso | Especificación |
|---------|---------------|
| Plan | DigitalOcean Droplet — Basic |
| vCPUs | 2 |
| RAM | 4 GB |
| Almacenamiento | 80 GB SSD |
| Ancho de banda incluido | 4 TB/mes |
| Costo mensual | $24 USD |

### Software de infraestructura (sin costo adicional)

- **Nginx**: proxy inverso y servidor de archivos estáticos
- **Certbot + Let's Encrypt**: certificado SSL/TLS gratuito
- **Snapshots de DigitalOcean**: respaldo mensual del estado del Droplet ($1.60/mes)
- **Resend** (free tier): correo transaccional para el envío de alertas (hasta 3,000 emails/mes)
- **GitHub Actions** (opcional): integración continua para despliegues automatizados

---

## Verificación de Capacidad Técnica

| Criterio | Resultado |
|---------|-----------|
| ¿El equipo tiene la capacidad técnica para desarrollar el sistema? | **Sí.** El perfil de Licenciatura en Informática cubre los fundamentos necesarios para trabajar con el stack propuesto (Java, Python, React, PostgreSQL). |
| ¿El stack tecnológico ha sido utilizado en sistemas equivalentes? | **Sí.** Existen sistemas de gestión penitenciaria en producción que utilizan arquitecturas similares, lo que valida la viabilidad técnica del enfoque. |
| ¿Las herramientas seleccionadas tienen soporte activo y documentación disponible? | **Sí.** Todas las tecnologías del stack cuentan con comunidades activas, documentación oficial actualizada y amplia disponibilidad de recursos de aprendizaje. |
