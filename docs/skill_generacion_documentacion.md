# Skill: Generador de Documentación por Equipo

Este archivo define las instrucciones (skill) para generar la documentación requerida en cada una de las carpetas de los equipos del proyecto en este repositorio. Puede ser utilizado como prompt o contexto base para cualquier IA o Agente que asista en la documentación del proyecto.

## 📌 Instrucciones Principales
Cuando se entregue la información base del proyecto para un equipo específico, se deberá generar la siguiente estructura de archivos y carpetas dentro del directorio correspondiente al equipo (ej. `docs/equipo-azul/`).

### 1. `planteamiento_problema.md`
Debe contener el planteamiento del problema dividido **estrictamente en el siguiente orden**:
1. Descripción general del área
2. Detalle del problema
3. Pronóstico
4. Propuesta

**⚠️ Verificación de Orden:** Se debe verificar que este orden se cumpla correctamente. Si al generar el documento falta la información de alguno de estos puntos, se debe dejar una marca visible entre corchetes comentando el faltante. Por ejemplo: `[FALTO PRONÓSTICO]`.

### 2. `objetivos.md`
Debe contener los objetivos del proyecto adhiriéndose a las **normas APA**.
- Debe incluir un **Objetivo General** y varios **Objetivos Específicos**.
- **Reglas para Objetivos Específicos:**
  - Deben tratar exclusivamente sobre las fases del ciclo de vida y desarrollo del proyecto (ej. Levantamiento del proyecto, recolección de datos, análisis, diseño, implementación e implantación).
  - **PROHIBIDO:** No se pueden incluir objetivos que no sean de desarrollo. Tampoco se pueden incluir los objetivos/beneficios del software a crear (es decir, qué va a lograr el software una vez hecho). Solo objetivos del *proceso de creación* del software.

### 3. Carpeta `factibilidades/`
Se debe crear esta subcarpeta y dentro de ella generar los siguientes tres (3) archivos:
- **`tecnica.md`**: Debe poseer todo lo necesario para entender si es factible a nivel técnico hacer el sistema. Debe incluir las tecnologías a usar, el software y hardware de desarrollo, y el software necesario para el lanzamiento/despliegue.
- **`economica.md`**: Debe poseer los datos necesarios para establecer el presupuesto durante el desarrollo, el costo de inicialización, y el costo mensual operativo por tener activo el sistema.
- **`operativa.md`**: Debe determinar quiénes son los usuarios y los operadores del sistema, así como las estrategias necesarias para que estos puedan operar el sistema adecuadamente.

### 4. `mvp.md`
- Este archivo debe contener el planteamiento de qué es exactamente lo que alcanzará a desarrollar el equipo como Producto Mínimo Viable (MVP).

---
*Uso del Skill: El usuario proporciona la información de entrada (contexto) y la IA procede a crear los archivos uno a uno en la carpeta respectiva del equipo cumpliendo con estas restricciones.*
