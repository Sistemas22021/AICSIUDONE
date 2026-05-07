# MVP — Equipo Verde

## Objetivo del MVP
Entregar una primera version funcional centrada en el Motor de Cotejo Masivo, como nucleo inteligente del sistema, para comparar automaticamente una evidencia recien ingresada contra el historico almacenado y devolver coincidencias tecnicas con porcentaje de confianza, utilizando OpenCV.

## Alcance funcional del MVP
1. Registro de evidencia forense con campos esenciales:
   - numero de caso,
   - fecha de recoleccion,
   - tipo de municion,
   - deformacion,
   - peso en gramos.
2. Carga de imagenes balisticas y normalizacion minima para su procesamiento.
3. Motor de Cotejo Masivo (OpenCV):
   - seleccion de una evidencia recien ingresada;
   - comparacion automatica contra todo el historico almacenado;
   - ejecucion de algoritmo de busqueda de similitudes tecnicas;
   - devolucion de lista de posibles coincidencias con porcentaje de confianza (ejemplo: 92%).
4. Visualizacion de resultados de cotejo:
   - top de coincidencias ordenadas por confianza;
   - referencia al caso/evidencia historica relacionada.
5. Gestion de estado de evidencia dubitativa:
   - pendiente,
   - en proceso,
   - confirmada,
   - descartada.
6. Registro de trazabilidad minima por accion (usuario, fecha, tipo de evento y ejecucion de cotejo).
7. Acceso por roles operativos principales (perito, investigador, supervisor) con permisos basicos.

## Fuera de alcance del MVP
- Entrenamiento de modelos de IA avanzados mas alla del procesamiento clasico con OpenCV.
- Integraciones externas complejas con laboratorios de terceros.
- Analitica predictiva de alto volumen y reporteria avanzada.
- Orquestacion multiinstitucional.

## Criterios de aceptacion del MVP
- Los usuarios pueden registrar y consultar evidencias sin perdida de datos.
- Al ejecutar el Motor de Cotejo Masivo, el sistema compara la evidencia seleccionada contra el historico completo disponible.
- El sistema devuelve una lista de coincidencias con porcentaje de confianza y permite identificar claramente el caso historico asociado.
- Cada evidencia presenta historial de cambios y estados visible para auditoria, incluyendo eventos de cotejo.
- El supervisor puede revisar resultados y validar el cierre del analisis dentro del flujo definido.
- El sistema se despliega en ambiente de prueba con estabilidad funcional basica.

## Entregables del MVP
- API funcional para registro de evidencias, consulta historica y ejecucion de cotejo masivo.
- Componente de procesamiento de imagen con OpenCV para comparacion tecnica.
- Interfaz web operativa para seleccionar evidencia y visualizar ranking de coincidencias.
- Esquema de base de datos inicial.
- Documentacion de uso del motor de cotejo y pruebas funcionales basicas.
