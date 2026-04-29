# Factibilidad Económica

## A) Presupuesto de Desarrollo — Equipo Humano

El equipo de desarrollo está conformado por 5 estudiantes de Licenciatura en Informática con perfil fullstack. Para efectos del cálculo se toma como referencia el rango salarial de desarrolladores junior fullstack en Venezuela y Latinoamérica.

**Duración estimada del MVP**: 5 meses

| Concepto                          | Cant. | Salario/mes | Meses | Total        |
| --------------------------------- | ----- | ----------- | ----- | ------------ |
| Desarrolladores fullstack junior  | 5     | $450        | 5     | $11,250      |
| **Total costo de desarrollo**     |       |             |       | **$11,250 USD** |

## B) Presupuesto de Infraestructura — Fase de Desarrollo

Durante la fase de desarrollo cada integrante trabaja en su entorno local. Se provisiona un Droplet compartido en DigitalOcean que funciona como servidor de integración continua y pruebas del equipo, donde se despliegan los avances para validación conjunta.

### Costos de Arranque (únicos, se pagan una sola vez)

| Concepto                                                                 | Costo estimado (USD)     |
| ------------------------------------------------------------------------ | ------------------------ |
| Registro de dominio (.com, 1 año)                                        | $15                      |
| Configuración inicial del Droplet (Nginx, Java, Python, PostgreSQL)      | $0 (trabajo del equipo)  |
| Licencias de herramientas de diseño (Figma free)                         | $0                       |
| **Subtotal arranque**                                                    | **$15**                  |

### Droplet de Desarrollo/Integración — DigitalOcean

**Especificaciones**:
- 2 vCPUs
- 4 GB RAM
- 80 GB SSD
- 4 TB transferencia

**Costo**: $24/mes durante 5 meses = **$120 USD**

### Costo Total de Desarrollo (Primera Inversión)

| Concepto                     | Costo        |
| ---------------------------- | ------------ |
| Equipo humano (5 meses)      | $11,250      |
| Infraestructura arranque     | $15          |
| Droplet desarrollo (5 meses) | $120         |
| **TOTAL DESARROLLO**         | **$11,385 USD** |

## C) Presupuesto Operativo Mensual (Post-Lanzamiento)

Una vez finalizado el MVP, el sistema entra en operación con los siguientes costos recurrentes:

| Concepto                              | Costo mensual (USD) |
| ------------------------------------- | ------------------- |
| Droplet producción (mismo spec)       | $24.00              |
| Dominio (prorrateado)                 | $1.25               |
| Alertas por email (Resend free tier)  | $0.00               |
| Backups automáticos (DigitalOcean)    | $1.60               |
| **TOTAL MENSUAL**                     | **$26.85 USD**      |

### Proyección Anual de Operación

| Concepto                     | Costo anual (USD) |
| ---------------------------- | ----------------- |
| Costo mensual × 12           | $322.20           |
| Renovación dominio (año 2)   | $15.00            |
| **TOTAL AÑO 1 OPERACIÓN**    | **$337.20 USD**   |

## Resumen de Inversión

| Fase                         | Monto (USD)   | Observaciones |
| ---------------------------- | ------------- | ------------- |
| Inversión inicial (desarrollo) | $11,385     | Una sola vez  |
| Costo operativo mensual      | $26.85        | Recurrente    |
| Costo operativo anual        | $337.20       | Año 1         |

## Conclusión

La inversión total de **$11,385 USD** es razonable para el alcance del MVP. El costo operativo mensual de **$26.85** sobre un único Droplet de DigitalOcean es sostenible para una institución pública.

El proyecto es **ECONÓMICAMENTE FACTIBLE**.
