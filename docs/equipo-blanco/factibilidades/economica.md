# Factibilidad Económica

## Sistema Integral de Gestión Penitenciaria

---

## Evaluación General

La inversión total requerida para lanzar el MVP es de **$11,355 USD**, con un costo operativo mensual post-lanzamiento de **$26.85 USD/mes**. Ambas cifras son razonables dado el alcance del sistema y el tamaño del equipo de desarrollo.

**Conclusión: FACTIBLE.**

---

## A. Presupuesto de Desarrollo — Equipo Humano

El equipo de desarrollo está conformado por 5 estudiantes de Licenciatura en Informática con perfil fullstack. La referencia salarial utilizada corresponde al rango de desarrolladores junior fullstack en Venezuela y Latinoamérica.

- **Duración estimada del MVP:** 5 meses (5 sprints de 3 semanas cada uno = 15 semanas efectivas)

| Concepto | Cantidad | Salario/mes (USD) | Meses | Total (USD) |
|---------|---------|-------------------|-------|-------------|
| Desarrolladores fullstack junior | 5 | $450 | 5 | $11,250 |
| **Total costo de desarrollo** | | | | **$11,250** |

---

## B. Costos de Inicialización (Únicos — Se pagan una sola vez)

Estos costos se incurren una única vez antes o al momento del lanzamiento del sistema.

| Concepto | Costo (USD) |
|---------|-------------|
| Registro de dominio (.com, 1 año) | $15 |
| Configuración inicial del Droplet (Nginx, Java, Python, PostgreSQL) | $0 *(trabajo del equipo)* |
| Licencias de herramientas de diseño (Figma — plan gratuito) | $0 |
| **Subtotal de arranque** | **$15** |

---

## C. Presupuesto de Infraestructura — Fase de Desarrollo

Durante el desarrollo, el equipo trabaja en entornos locales y comparte un Droplet de integración continua en DigitalOcean donde se despliegan los avances para validación conjunta.

**Plan seleccionado:** Basic — 2 vCPUs / 2 GB RAM / 60 GB SSD → **$18/mes**

| Concepto | Costo mensual (USD) |
|---------|---------------------|
| Droplet DigitalOcean (Basic 2 vCPUs / 2 GB RAM / 60 GB SSD) | $18 |
| Ancho de banda incluido en el plan (1 TB/mes) | $0 |
| SSL/TLS — Let's Encrypt (Certbot) | $0 |
| Repositorio de código — GitHub | $0 |
| Correo transaccional para alertas — Resend (free tier) | $0 |
| **Subtotal mensual fase de desarrollo** | **$18** |
| **Total fase de desarrollo (× 5 meses)** | **$90** |

---

## D. Costo Mensual Operativo — Fase de Producción

Una vez lanzado el MVP, se mantiene la arquitectura de servidor único pero con un plan de mayor capacidad para soportar usuarios reales.

**Plan seleccionado:** Basic — 2 vCPUs / 4 GB RAM / 80 GB SSD → **$24/mes**

| Concepto | Costo mensual (USD) |
|---------|---------------------|
| Droplet DigitalOcean (Basic 2 vCPUs / 4 GB RAM / 80 GB SSD) | $24.00 |
| Ancho de banda incluido en el plan (4 TB/mes) | $0 |
| Snapshots del Droplet (respaldo mensual) | $1.60 |
| SSL/TLS — Let's Encrypt (Certbot) | $0 |
| Dominio (prorrateado mensualmente: $15 / 12) | $1.25 |
| Correo transaccional — Resend (free tier hasta 3,000 emails/mes) | $0 |
| **Total operación mensual (post-lanzamiento)** | **$26.85** |

---

## E. Resumen Económico Completo

| Concepto | Costo (USD) |
|---------|-------------|
| Costo de desarrollo — equipo humano (5 meses) | $11,250 |
| Costo de arranque único (registro de dominio) | $15 |
| Infraestructura de desarrollo en DigitalOcean (5 meses × $18) | $90 |
| **Inversión total para lanzar el MVP** | **$11,355** |
| **Costo mensual de operación (post-lanzamiento)** | **$26.85 / mes** |

---

## Notas

- Todos los costos de herramientas de desarrollo (IDEs, gestión de repositorio, certificados SSL, diseño de interfaces) son $0 por uso de planes gratuitos o herramientas de código abierto.
- El costo de desarrollo del equipo humano es referencial; en un contexto académico donde el equipo no percibe remuneración, la inversión real se reduce a los costos de infraestructura ($105 durante el desarrollo + $26.85/mes en producción).
- El costo operativo de $26.85/mes es sostenible para una institución pública o privada que adopte el sistema.

**Nota sobre Supabase:** El proyecto utiliza Supabase (plan gratuito) como hosting de la base de datos PostgreSQL en la nube, eliminando la necesidad de mantener una instancia de PostgreSQL dentro del Droplet de desarrollo.
