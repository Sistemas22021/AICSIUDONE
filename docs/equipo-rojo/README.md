# Equipo Rojo — Definición Inicial del Sistema

## Descripción General

El Equipo Rojo tiene como objetivo el desarrollo de un sistema de gestión criminal que permita a investigadores y funcionarios documentar crímenes, escenas del crimen y patrones delictivos de manera estructurada y trazable.

---

## Casos de Uso Principales

### CU-A: Registro de Crímenes y Denuncias

- Un funcionario o investigador puede registrar crímenes y denuncias formales en el sistema.
- Cada registro queda asociado a un número de caso único.
- Se almacena el tipo de delito, fecha y hora del hecho, lugar y personas involucradas.- 
- El expediente queda sellado con el nombre, rol e ID del agente que lo creó y la marca de tiempo exacta. Ningún campo puede modificarse una vez sellado.

---

### CU-B: Gestión de Escena del Crimen

El sistema guía al investigador a través de un checklist secuencial de 4 pasos que simula el protocolo real de manejo de escena. Los pasos deben completarse en orden y cada uno queda registrado con su timestamp individual:

1. **Aseguramiento del perímetro**: Confirmación de que la escena está sellada, número de agentes presentes y hora de cierre.
2. **Documentación de evidencia**: Levantamiento ítem por ítem. Cada evidencia genera su propio subregistro vinculado al folio del expediente, con tipo, descripción, número de ítem (EV-001, EV-002…) y firma digital del investigador mediante PIN personal.
3. **Recolección y embalaje**: Registro de quién tomó cada evidencia, tipo de embalaje y etiqueta.
4. **Liberación de la escena**: Firma final del investigador responsable y hora de cierre de protocolo.

El módulo incluye además el registro de escena negativa, que documenta lo que se buscó y no se encontró (ej. *"Se buscó arma de fuego en el área perimetral, no localizada"*). Este registro protege al investigador de acusaciones de negligencia y alimenta el análisis de modus operandi del Componente C.

---

### CU-C: Registro y Análisis de Firma Conductual (Modus Operandi)

El sistema permite identificar y registrar patrones de comportamiento delictivo mediante inteligencia artificial integrada, implementada en dos capas:

1. **Embeddings**: Al registrar un delito, el sistema convierte su descripción en una representación numérica que captura el significado del texto. Esto permite reconocer patrones similares aunque estén redactados de forma diferente (ej. *"robo de madrugada por ventana trasera"* y *"asalto nocturno entrando por la parte trasera"* son identificados como el mismo patrón). Los vectores se almacenan y comparan contra todos los expedientes anteriores.

2. **Análisis con LLM (GPT-4)**: Con los expedientes similares ya identificados, el modelo analiza los textos en conjunto y devuelve una explicación en lenguaje natural: características compartidas, firma reconocible del perpetrador, consistencia de horario y zona, y probabilidad de autoría común. Esta explicación queda adjunta al expediente y es visible para el investigador asignado.

Cuando el sistema detecta un patrón con suficiente nivel de confianza, genera automáticamente una **alerta interna** con los folios relacionados y un resumen en lenguaje claro, permitiendo al investigador agrupar esos delitos en un caso desde el mismo panel. La integración con los modelos de IA se realiza a través de **Spring AI**, que actúa como puente nativo entre el backend Java y los modelos externos.

Adicionalmente, el módulo incluye un **motor de búsqueda** con filtros combinables por tipo de delito, zona geográfica (municipio, colonia o radio aproximado) y período (rango de fechas del hecho).

---

## Actores del Sistema

| Actor | Rol |
|---|---|
| **Guardia / Oficial de turno** | Registra nuevos expedientes, consulta el panel completo de expedientes activos y su estatus. No puede editar expedientes ajenos una vez sellados. |
| **Investigador asignado** | Accede únicamente a los expedientes que le fueron asignados formalmente. Puede agregar información (evidencia, notas), pero no puede eliminar registros existentes. |
| **Analista Criminal** | Consulta firmas conductuales, patrones detectados por IA y alertas de modus operandi. |
| **Supervisor** | Valida y aprueba registros sensibles. |

---

## Notas Técnicas

- Sistema basado en nodos*para vincular delitos, escenas del crimen, evidencias.
- **Trazabilidad completa** de cada registro: historial de modificaciones, timestamps por acción y firma digital del agente responsable en cada paso.
- **Folio único e inmutable** como identificador central de cada expediente, referenciado transversalmente en todos los componentes.
- Integración de IA mediante **Spring AI** (embeddings + GPT-4) para detección automática de patrones de modus operandi.
- La **escena negativa** (ausencia documentada de evidencia) se procesa como dato analítico en el motor de detección de patrones.

---

*Definición inicial capturada el 17/04/2026 — Actualizada con base en MVP del 01/05/2026 — Sujeta a revisión en informe de factibilidad.*