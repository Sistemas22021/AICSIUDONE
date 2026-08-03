El **Producto Mínimo Viable (MVP)** del proyecto se centra en consolidar el núcleo de inmutabilidad y el flujo legal básico para garantizar que un testimonio no pueda ser alterado una vez registrado. El equipo se compromete a desarrollar las siguientes funcionalidades:

#### 1. Módulo de Registro y Procesamiento (CU-01)
* **Captura de Datos Básicos:** Formulario para el registro de la identidad del detenido, motivo y circunstancias de la detención.
* **Confirmación de Registro:** Implementación de un botón de "Cierre de Registro" que, al ser accionado, bloquea todos los campos para evitar ediciones posteriores.

#### 2. Protocolo de Protección Jurídica (CU-02)
* **Checklist de Derechos:** Interfaz obligatoria donde el Oficial Encargado debe marcar la lectura de los derechos constitucionales.
* **Sello de Tiempo (Timestamp):** Generación automática de una marca de tiempo inalterable que certifique el momento exacto en que se realizó la notificación de derechos.

#### 3. Captura de Testimonio Inmutable (CU-03)
* **Editor de Declaración:** Espacio para la transcripción o carga de la versión del detenido o testigo.
* **Generador de Hash de Integridad:** Al finalizar la toma del testimonio, el sistema generará un código **SHA-256** único basado en el contenido del texto. Cualquier intento de modificar el archivo en la base de datos invalidará este código, evidenciando la ruptura de la inmutabilidad.

#### 4. Panel de Auditoría y Consulta
* **Visualizador de Expedientes:** Interfaz para que el Asesor Legal y el Supervisor puedan consultar testimonios previos en modo "Solo Lectura".
* **Registro de Trazabilidad (Logs):** Un historial básico que muestre qué usuario creó el registro y en qué fecha/hora se cerró el expediente.

#### 5. Persistencia y Seguridad Técnica
* **Base de Datos Relacional:** Configuración de tablas en PostgreSQL con restricciones de seguridad que impidan el borrado físico de registros (Soft Delete únicamente para administradores de sistema).
* **Acceso por Roles:** Implementación de tres niveles de acceso (Oficial, Asesor y Supervisor) con credenciales únicas.
