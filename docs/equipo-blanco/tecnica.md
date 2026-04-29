# Factibilidad Técnica

## Stack Tecnológico

El sistema se desarrollará con tecnologías estándar, abiertas y ampliamente documentadas:

| Componente            | Tecnología                                  | Justificación |
| --------------------- | ------------------------------------------- | ------------- |
| Backend               | Java 17 + Spring Boot                       | Ecosistema maduro, documentación extensa, estándar en sistemas institucionales en Latinoamérica |
| Base de datos         | PostgreSQL 15                               | Base de datos relacional gratuita, robusta y con soporte de integridad referencial nativa. Adecuada para el volumen de datos del MVP |
| Frontend              | React 18                                    | Librería líder para interfaces interactivas, amplia comunidad de soporte en español |
| ORM                   | Hibernate / Spring Data JPA                 | ORM nativo del ecosistema Spring Boot, reduce errores en consultas y mapea las entidades del sistema de forma tipada |
| Servidor web          | Nginx                                       | Sirve el frontend React como archivos estáticos y actúa como proxy inverso hacia Spring Boot. Gratuito e incluido en la configuración del Droplet |
| Autenticación         | Spring Security + JWT                       | Estándar en aplicaciones Spring Boot, control de acceso por roles sin dependencias externas de pago |
| Infraestructura       | DigitalOcean — Droplet único                | Un solo servidor VPS aloja backend, frontend, base de datos y servicio Python. Arquitectura apropiada para el volumen de usuarios del MVP con costo controlado |
| Control de versiones  | Git + GitHub                                | Estándar de la industria, gratuito |
| Alertas               | Python + Resend API                         | El servicio Python evalúa condiciones diarias y envía correos. Resend es gratuito hasta 3,000 emails/mes |

## Arquitectura del Sistema

Durante la fase de desarrollo cada integrante trabaja en su entorno local. Se provisiona un Droplet compartido en DigitalOcean que funciona como servidor de integración continua y pruebas del equipo, donde se despliegan los avances para validación conjunta.

### Propuesta de infraestructura (DigitalOcean)

Un único Droplet aloja los tres componentes del sistema:
- Backend en Java (Spring Boot)
- Servicio de alertas en Python
- Frontend en React (servido como archivos estáticos mediante Nginx)
- Base de datos PostgreSQL

Esta arquitectura de servidor único es adecuada para la fase MVP al reducir costos y simplificar la administración.

## Verificación de Capacidad Técnica

### ¿Tiene el equipo la capacidad técnica?

**Sí.** El perfil de Licenciatura en Informática cubre los fundamentos necesarios para trabajar con el stack propuesto. Spring Boot, React y Python son parte del currículo estándar de Informática y cuentan con extensa documentación oficial y comunidades activas en español.

### ¿Alguien más ya lo ha hecho antes?

**Sí.** Existen referencias directas:
- **Mark43**: Sistema RMS para policías en EE.UU.
- **Tyler Technologies Jail Management**: Gestión penitenciaria en producción
- **SIGES**: Sistema de gestión en Latinoamérica
- **COMPAS**: Seguimiento post-penitenciario en EE.UU.

Estos sistemas en producción validan que este tipo de sistema es técnicamente alcanzable.

## Requisitos de Hardware para Desarrollo

Cada desarrollador necesita un equipo con las siguientes especificaciones mínimas:

| Componente          | Mínimo requerido                                        |
| ------------------- | ------------------------------------------------------- |
| Procesador          | Intel Core i5 / AMD Ryzen 5 (8ª generación o superior) |
| Memoria RAM         | 8 GB (recomendado 16 GB)                                |
| Almacenamiento      | 256 GB SSD                                              |
| Sistema operativo   | Windows 10/11, macOS o cualquier distribución Linux     |
| Conexión a internet | Mínimo 10 Mbps estables                                 |
| Navegador moderno   | Chrome o Firefox actualizado                            |

## Software Necesario para Despliegue

El sistema requiere el siguiente software en el servidor de producción:

- **Sistema operativo**: Ubuntu Server 22.04 LTS o superior
- **Java Runtime Environment (JRE)**: OpenJDK 17
- **Python**: Python 3.10 o superior
- **Servidor web**: Nginx (última versión estable)
- **Base de datos**: PostgreSQL 15
- **Gestor de procesos**: systemd (incluido en Ubuntu)
- **Certificado SSL**: Let's Encrypt (gratuito)

## Conclusión

El proyecto es **TÉCNICAMENTE FACTIBLE**. El stack tecnológico es estándar, el equipo tiene la base para desarrollarlo y existen referencias reales del sistema en producción en otros países.
