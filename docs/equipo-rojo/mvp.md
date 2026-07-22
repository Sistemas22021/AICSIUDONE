# MVP — Producto Mínimo Viable

## Descripción General

El MVP del Módulo de Gestión Criminal comprende el diseño e implementación de tres componentes funcionales que cubren las etapas esenciales del proceso investigativo: el registro del hecho delictivo, el procesamiento de la escena del crimen y el análisis de modus operandi. Estos tres componentes operan de forma integrada bajo una única plataforma, compartiendo un folio único de expediente como referencia cruzada.

---

## Componente A — Registro de Delitos

### Formulario de Captura de Incidente
El sistema contará con un formulario estructurado que garantiza que ningún expediente quede incompleto. Los campos obligatorios son:

- **Tipo de delito**: Selector categorizado basado en el catálogo del código penal aplicable, con tipo y subtipo. No se puede avanzar sin seleccionarlo.
- **Fecha y hora del hecho**: Se distingue entre la hora del hecho (cuándo ocurrió) y la hora del reporte (cuándo se levantó el acta), ambas registradas con timestamp del sistema.
- **Geolocalización básica**: Captura de coordenadas GPS desde dispositivo móvil, o entrada manual con municipio, colonia y referencia. El mapa interactivo queda para versiones posteriores.

### Sellado del Expediente con Número Único
Al guardar el formulario, el sistema genera automáticamente un folio único de expediente. Este folio es inmutable y sirve como referencia cruzada en todos los componentes posteriores. El expediente queda sellado con el nombre, rol e ID del agente que lo creó y la marca de tiempo exacta.

### Control de Acceso por Rol
El MVP define dos roles base:
- **Guardia / Oficial de turno**: Puede crear expedientes y consultar el estatus de todos los casos activos. No puede editar expedientes ajenos sellados.
- **Investigador asignado**: Solo accede a los expedientes asignados formalmente. Puede agregar información, pero no puede eliminar lo ya registrado.

---

## Componente B — Escena del Crimen

### Checklist Secuencial de 4 Pasos
El sistema obliga a completar los pasos en orden, simulando el protocolo real de manejo de escena:

1. **Aseguramiento del perímetro**: Confirmación de sellado, número de agentes presentes y hora de cierre.
2. **Documentación de evidencia**: Levantamiento ítem por ítem.
3. **Recolección y embalaje**: Registro de quién tomó cada evidencia, tipo de embalaje y etiqueta.
4. **Liberación de la escena**: Firma final del investigador responsable y hora de cierre de protocolo.

Cada paso queda registrado con su timestamp individual para fines de auditoría.

### Registro de Evidencia con Firma del Investigador
Cada evidencia levantada genera su propio subregistro vinculado al folio del expediente, incluyendo: tipo de evidencia, descripción, número de ítem (EV-001, EV-002…) y firma digital del investigador (implementada como confirmación con PIN personal en el MVP).

### Registro de Escena Negativa
El sistema documenta lo que se buscó y no se encontró. Este dato tiene valor analítico para el Componente C, ya que la ausencia de ciertos elementos también constituye un patrón de modus operandi, además de proteger al investigador ante posibles acusaciones de negligencia.

---

## Componente C — Análisis de Modus Operandi

### Detección de Patrones Delictivos
El sistema cruza variables entre expedientes usando dos capas de inteligencia artificial:

- **Embeddings**: Cada registro de delito se convierte en una representación numérica que captura su significado semántico. El sistema compara estos vectores contra todos los expedientes anteriores para identificar casos con patrones similares, independientemente de la redacción exacta.
- **Análisis con LLM (GPT-4)**: Los expedientes similares identificados se envían al modelo de lenguaje, que analiza en conjunto qué características comparten, si existe una firma reconocible del perpetrador y si el horario o zona son consistentes. La explicación queda adjunta al expediente y es visible para el investigador.

Cuando el sistema detecta un patrón con suficiente nivel de confianza, genera automáticamente una alerta interna con los folios relacionados y un resumen del patrón en lenguaje claro. El investigador puede revisar la alerta y, si lo considera pertinente, agrupar los delitos en un caso desde el mismo panel.

### Motor de Búsqueda con Filtros Combinables
El buscador del MVP incluye los siguientes filtros:
- Tipo de delito (una o varias categorías del catálogo)
- Zona geográfica (municipio, colonia o radio aproximado)
- Período (rango de fechas del hecho)

Los resultados muestran folio, estatus, investigador asignado y fecha, con acceso directo al expediente completo.
