# 🟠 Equipo Naranja — Definición Inicial del Sistema

## Descripción General

El equipo Naranja tiene como objetivo el desarrollo de un sistema de **procesamiento y toma de testimonios**, garantizando la protección jurídica de los involucrados y la inmutabilidad de los registros capturados durante el proceso.

## Casos de Uso Principales

### CU-01: Procesamiento del Detenido
- El oficial encargado registra los datos del imputado o persona detenida.
- Se documenta el motivo de la detención y las circunstancias del hecho.
- El sistema garantiza que los datos capturados sean inmutables una vez confirmados.

### CU-02: Protección Jurídica y Lectura de Derechos
- El sistema registra la notificación formal de los derechos del detenido.
- Incluye confirmación de que se leyeron y explicaron los derechos correspondientes.
- Genera un registro firmado/verificado de este proceso para soporte legal.

### CU-03: Toma de Testimonio
- Módulo para capturar la declaración o versión del detenido o testigo.
- El testimonio queda vinculado al expediente del caso de forma inmutable.
- Permite lectura, verificación y consulta posterior por personal autorizado.

## Actores del Sistema

| Actor | Rol |
|-------|-----|
| Oficial Encargado | Registra datos del detenido y toma el testimonio |
| Asesor Legal | Verifica protección jurídica y derechos garantizados |
| Supervisor | Audita la integridad y completitud del proceso |

## Garantías del Sistema

- **Inmutabilidad**: Los registros confirmados no pueden ser modificados.
- **Trazabilidad**: Cada acción queda registrada con usuario, fecha y hora.
- **Protección jurídica**: El proceso digital cumple con requisitos legales establecidos.

## Notas Técnicas

- Firma digital o verificación de integridad sobre los testimonios registrados.
- Control de acceso por rol para consulta y modificación de datos.
- Auditoría completa de accesos y operaciones sobre los expedientes.

---
*Definición inicial capturada el 17/04/2026 — Sujeta a revisión en informe de factibilidad.*
