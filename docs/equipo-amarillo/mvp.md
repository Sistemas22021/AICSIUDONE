# Producto Mínimo Viable (MVP)
## Nexo Criminal

---

**Universidad de Oriente — Núcleo Nueva Esparta**
**Materia:** Sistemas de Información II
**Profesor:** Alejandro Marcano
**Equipo:** Amarillo

---

## 1. Filosofía del MVP

El MVP de Nexo Criminal demuestra el **valor central del sistema**: descubrir un vínculo oculto entre entidades de forma automática. Si el MVP no puede enhebrar al menos dos entidades por un hilo común, no se considera un MVP válido.

Siguiendo las indicaciones de la cátedra del 22 de abril:

- El MVP debe seguir funcionando durante al menos **4 semanas** después de su lanzamiento.
- Debe estar **abierto a cambios** (metodología ágil).
- Debe enfocarse en el **mayor valor posible**, no en perfección técnica.
- El testing exhaustivo no genera valor tangible a corto plazo, por lo que se prioriza funcionalidad sobre cobertura de pruebas.

El MVP debe representar **el esfuerzo mínimo para obtener el mayor valor del sistema**, considerando que el proyecto se desarrollará hasta donde sea posible dentro del cronograma académico de siete sprints.

---

## 2. Definición del MVP del proyecto

El **Producto Mínimo Viable** que el equipo amarillo se compromete a desarrollar consiste en un sistema web operativo que cumpla los siguientes criterios:

> *"Un analista debe poder cargar entidades operativas, ejecutar el motor de reglas heurísticas y obtener al menos un hilo rojo descubierto automáticamente, visualizándolo en un grafo y recibiendo una alerta cuando se cumpla la condición de un nodo logístico."*

Esta definición es deliberadamente concreta y verificable: si al final del proyecto el sistema **no cumple estos cuatro pasos en menos de 15 minutos** con datos cargados desde cero, el MVP no se considera entregado.

---

## 3. Alcance del MVP — Lo que SÍ incluye

### 3.1 Gestión básica de entidades (CRUD)

- Registrar **Personas** (víctimas, sospechosos, testigos, propietarios).
- Registrar **Vehículos** (con placa, marca, modelo, año, color, estado).
- Registrar **Ubicaciones** (dirección, latitud, longitud, tipo).
- Registrar **Sucesos** (robo de vehículo, desaparición, avistamiento, transacción sospechosa).

### 3.2 Motor de vínculos — Reglas heurísticas mínimas

- **Regla temporal-geográfica (Nodo Logístico)**: detecta si dos o más sucesos ocurren en un radio configurable y dentro de una ventana temporal determinada.
- **Regla de Círculo Social**: detecta si dos personas tienen un contacto común que puede actuar como intermediario.
- **Regla de Modus Operandi**: agrupa sucesos que comparten el mismo método delictivo.

### 3.3 Visualización básica del grafo

- Pantalla que muestra un nodo seleccionado y sus vínculos de primer grado.
- Nodos diferenciados por color según tipo de entidad.
- Aristas rojas representando los "hilos rojos" descubiertos por el motor.

### 3.4 Alertas simples

- Notificación visible en el dashboard cuando se detecta un nodo logístico u otro patrón de alto riesgo.

### 3.5 Autenticación básica

- Login con usuario y contraseña.
- Roles iniciales: **Administrador** y **Analista**.

---

## 4. Fuera del alcance del MVP

Estos puntos quedan documentados como **trabajo futuro** para iteraciones posteriores:

- Roles avanzados (supervisor, auditor con permisos granulares).
- Importación masiva desde CSV o Excel desde la interfaz de usuario.
- Integración real con cámaras CCTV, sistemas GPS o registros telefónicos.
- Generación automática de reportes en formato PDF.
- Aplicación móvil para oficiales en campo.
- Machine Learning predictivo (los vínculos se descubren con reglas determinísticas).
- Notificaciones por correo electrónico o SMS.
- Multi-tenancy (múltiples unidades policiales en una misma instancia).

---

## 5. Funcionalidades adicionales logradas (más allá del MVP base)

Durante el desarrollo, el equipo logró superar el alcance del MVP base e incorporar las siguientes funcionalidades adicionales:

- **Módulo completo de Personas Desaparecidas** con dossier fotográfico.
- **Quinta regla del motor**: detección de clusters de desapariciones.
- **Asistente conversacional con IA Claude** (Anthropic) con cuatro casos de uso: chat, predicción de zonas de búsqueda, análisis de similitud y reportes ejecutivos.
- **Sistema de tema** claro y oscuro configurable.
- **Búsqueda global** multi-entidad desde la barra superior.
- **Exportación de datos a CSV** en cada módulo.
- **Mapa táctico** con visualización geoespacial de entidades.

---

## 6. Criterios de éxito del MVP

El MVP se considera **exitoso** si un analista puede cumplir los siguientes pasos sin asistencia técnica:

1. Cargar **10 vehículos**, **5 personas** y **3 ubicaciones** en menos de 15 minutos.
2. Ejecutar el motor de vínculos y obtener **al menos un hilo rojo descubierto automáticamente**.
3. Visualizar el grafo resultante en pantalla.
4. Recibir **una alerta** cuando se cumple la regla del nodo logístico.

Si los cuatro pasos se cumplen, el sistema demuestra su valor central y el MVP se considera entregado.

---

## 7. Plan de entregas del MVP base (4 semanas iniciales)

| Semana | Entregable |
|---|---|
| 1 | Repositorio listo, base de datos creada, CRUD de Personas y Vehículos funcional |
| 2 | CRUD de Ubicaciones y Sucesos. Script Python para cargar datos de prueba |
| 3 | Motor de vínculos (reglas 1 y 3). Primera pantalla del grafo |
| 4 | Regla del círculo social. Sistema de alertas. Demo integrada |

Posteriormente, durante los sprints 5, 6 y 7 se incorporaron las funcionalidades adicionales mencionadas en la sección 5.

---

## 8. Definition of Done (DoD)

Una historia de usuario se considera **"hecha"** cuando cumple las siguientes condiciones:

- El código está integrado a la rama del equipo (`amarillo`).
- La funcionalidad fue demostrada en ambiente local.
- Existe documentación básica en el README del módulo correspondiente.
- El Product Owner aprobó la demo.

---

## 9. Escenario de demo para presentación

> *"En una ciudad hipotética, tres camionetas fueron reportadas como robadas en las últimas 24 horas. El sistema descubre que las tres tuvieron su última ubicación conocida cerca del mismo taller. Dispara una alerta de Nodo Logístico. Al abrir el grafo, el analista ve un taller rodeado de tres vehículos enhebrados en rojo. Luego carga una desaparición y el sistema revela que la víctima comparte un contacto común con un sospechoso previamente registrado."*

Esta narrativa cubre los cuatro casos de uso principales más el "plot twist" del módulo de desapariciones, y se completa en aproximadamente cinco minutos de demo.

---

## 10. Justificación del MVP

El MVP propuesto se justifica en los siguientes términos:

- **Genera valor visible** desde la primera ejecución del motor.
- **Demuestra el diferencial técnico** del sistema (motor de reglas heurísticas).
- **Es defendible académicamente** frente al profesor.
- **Es escalable**: cada funcionalidad fuera del MVP es una extensión natural, no un rediseño.
- **Cumple las indicaciones de la cátedra**: enfocado en valor, abierto a cambios, funcional durante al menos 4 semanas.

---

*Documento elaborado por el equipo amarillo — Sistemas de Información II — Universidad de Oriente, Núcleo Nueva Esparta.*