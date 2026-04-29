# Factibilidad Económica
## Nexo Criminal

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Equipo:** Amarillo

---

## 1. Introducción

El presente documento detalla los costos asociados al desarrollo, la inicialización y la operación mensual del sistema Nexo Criminal. Se determinan tres categorías de costos: presupuesto durante el desarrollo, costos de inicialización (one-time) y costos operativos mensuales.

Todos los montos se expresan en dólares estadounidenses (USD) y se calculan con valores de mercado de Latinoamérica para el año 2026.

---

## 2. Presupuesto durante el desarrollo

### 2.1 Salarios del equipo

El proyecto se desarrolla con un equipo de cinco desarrolladores Full Stack Junior. Los salarios se calculan tomando como referencia el rango medio para perfiles junior fullstack en Latinoamérica durante el año 2026.

#### Referencias de salarios mensuales por país (USD)

| País | Rango mensual | Promedio considerado |
|---|---|---|
| Venezuela | 350 – 700 | 500 |
| Colombia | 800 – 1.400 | 1.100 |
| Argentina | 700 – 1.300 | 1.000 |
| México | 900 – 1.600 | 1.250 |

Para efectos de este proyecto se utiliza un salario promedio de **800 USD mensuales** por desarrollador junior fullstack en modalidad full-time, considerando un equipo distribuido en Latinoamérica.

#### Cálculo de costo del equipo

| Concepto | Cantidad | Costo unitario (USD) | Subtotal |
|---|---|---|---|
| Desarrolladores Full Stack Junior | 5 | 800 / mes | 4.000 / mes |
| Duración estimada del proyecto | 4 meses | — | 16.000 |
| **Total mano de obra** | | | **16.000 USD** |

### 2.2 Forma de pago

Los desarrolladores son contratados bajo modalidad **freelance mensual**, con pagos al final de cada mes contra entrega de los avances del sprint correspondiente. Esta modalidad es compatible con el contexto académico del proyecto.

### 2.3 Software de desarrollo

Todo el software utilizado es **open-source y gratuito**:

| Herramienta | Costo |
|---|---|
| JDK 17 (Eclipse Temurin) | 0 |
| Visual Studio Code | 0 |
| IntelliJ IDEA Community | 0 |
| Maven | 0 |
| Node.js / npm | 0 |
| PostgreSQL + PostGIS | 0 |
| Git | 0 |
| GitHub (cuentas personales) | 0 |
| **Total software de desarrollo** | **0 USD** |

### 2.4 Hardware del equipo

Se asume que cada desarrollador cuenta con su propia computadora con los requisitos mínimos. Por lo tanto, **no se incluye costo de hardware** en este presupuesto.

En caso de necesidad de adquirir equipos adicionales, los costos referenciales serían:

| Concepto | Costo unitario (USD) | Cantidad | Total |
|---|---|---|---|
| Laptop de desarrollo (16 GB RAM, SSD 512 GB) | 700 | 5 | 3.500 |
| Monitor adicional (opcional) | 150 | 5 | 750 |
| **Total hardware (no incluido)** | | | **4.250 USD** |

### 2.5 Total del presupuesto de desarrollo

| Categoría | Monto (USD) |
|---|---|
| Mano de obra | 16.000 |
| Software de desarrollo | 0 |
| Hardware (asumido propio) | 0 |
| **Total presupuesto de desarrollo** | **16.000 USD** |

---

## 3. Costo de inicialización (one-time)

Estos son costos únicos asociados a la puesta en funcionamiento inicial del sistema.

| Concepto | Costo (USD) |
|---|---|
| Dominio anual (.com.ve o .gob.ve) | 30 |
| Certificado SSL (Let’s Encrypt) | 0 (gratuito) |
| Configuración inicial del servidor (mano de obra) | 100 |
| Setup de PostgreSQL gestionada | 0 (incluido en hosting) |
| Configuración de backups automatizados | 0 (script propio) |
| **Total inicialización** | **130 USD** |

---

## 4. Costo operativo mensual

Una vez el sistema está activo en producción, los costos recurrentes son:

| Concepto | Costo mensual (USD) |
|---|---|
| Hosting backend (servidor 2 vCPU, 4 GB RAM) | 25 |
| Hosting base de datos PostgreSQL gestionada | 15 |
| Almacenamiento de fotos (10 GB iniciales) | 5 |
| Hosting frontend (CDN, ej. Cloudflare Pages) | 0 |
| Backup automatizado | 5 |
| Anthropic Claude API (1.000 consultas/mes promedio) | 15 |
| **Total mensual operativo** | **65 USD** |

### 4.1 Proyección anual de costos operativos

| Período | Costo (USD) |
|---|---|
| Mensual | 65 |
| Trimestral | 195 |
| Semestral | 390 |
| **Anual** | **780** |

---

## 5. Resumen económico consolidado

| Categoría | Monto (USD) |
|---|---|
| Presupuesto de desarrollo (4 meses, 5 personas) | 16.000 |
| Costo de inicialización (one-time) | 130 |
| Costo operativo mensual | 65 |
| Costo operativo anual proyectado | 780 |
| **Inversión total año 1** | **16.910 USD** |
| **Inversión total año 2 en adelante** | **780 USD anual** |

---

## 6. Análisis costo-beneficio

Aunque el sistema **no genera ingresos directos** (es de uso interno policial), su valor se cuantifica en términos de eficiencia operativa:

- Un analista experimentado puede tardar entre **40 y 80 horas** en correlacionar manualmente 50 entidades. Con Nexo Criminal, esa tarea se ejecuta en **menos de 5 segundos**.
- La detección temprana de un nodo logístico en una red de robo de vehículos puede prevenir entre **5 y 15 robos adicionales** mensuales en una zona afectada.
- El costo mensual operativo (65 USD) es inferior al costo de **una hora de trabajo** de un analista experimentado en la región.

El retorno de inversión se manifiesta en horas-analista ahorradas, casos resueltos más rápidamente y delitos prevenidos por detección temprana de patrones.

---

## 7. Conclusión

El proyecto Nexo Criminal es **económicamente factible**. La inversión total durante el primer año (16.910 USD) es modesta para una institución pública. El costo operativo mensual (65 USD) es de bajo impacto presupuestario y permite mantener el sistema activo sin presión financiera. La relación costo-beneficio es ampliamente favorable cuando se considera el valor en horas-analista ahorradas y casos resueltos por correlación automática.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*