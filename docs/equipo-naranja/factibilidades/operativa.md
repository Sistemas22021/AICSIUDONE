#### 1. Identificación de Usuarios y Operadores
El sistema clasifica a sus interactores en tres niveles de acceso, asegurando que cada uno cumpla una función específica dentro del ciclo de vida del testimonio:

* **Oficial Encargado (Operador de Campo):**
    * **Función:** Es el usuario de primera línea. Su responsabilidad es capturar los datos del detenido, documentar las circunstancias del hecho y registrar la declaración.
    * **Acciones:** Registro de datos, lectura de derechos y carga del testimonio.

* **Asesor Legal (Validador Jurídico):**
    * **Función:** Actúa como garante del debido proceso. Su rol es verificar que la protección jurídica y la notificación de derechos se hayan ejecutado conforme a la ley.
    * **Acciones:** Validación de cumplimiento de derechos y consulta de actas para defensa o acusación.

* **Supervisor (Auditor de Sistema):**
    * **Función:** Responsable de velar por la transparencia. No interviene en la carga de datos, pero supervisa que no existan irregularidades en los registros.
    * **Acciones:** Revisión de logs de trazabilidad (quién, qué y cuándo) y auditoría de integridad de los expedientes.

---

#### 2. Estrategias de Operación Adecuada
Para garantizar que los usuarios operen el sistema de manera eficiente y sin errores que comprometan la validez legal, se implementarán las siguientes estrategias:

* **Interfaz de Flujo Secuencial Obligatorio:**
    El sistema utilizará un diseño de "paso a paso" donde el **Oficial Encargado** no podrá acceder al módulo de *Toma de Testimonio* (CU-03) si no ha completado y verificado primero el módulo de *Protección Jurídica y Lectura de Derechos* (CU-02). Esto elimina el error humano por omisión de protocolos legales.

* **Programa de Alfabetización Digital y Legal:**
    Se establecerán jornadas de capacitación técnica dirigidas al personal operativo, enfocándose en el concepto de **inmutabilidad digital**. El objetivo es que el operador comprenda que una vez confirmada la información, el sistema bloqueará cualquier edición, fomentando la precisión en la carga inicial de datos.

* **Protocolo de Contingencia (Modo Offline):**
    Dado que la toma de testimonios no puede detenerse por fallas de conectividad, se operará bajo una estrategia de "registro local con sincronización posterior". Los operadores dispondrán de una versión simplificada que guarda los datos localmente con una marca de tiempo inalterable, los cuales se sincronizan automáticamente al restaurar la conexión, garantizando la continuidad operativa.

* **Sistema de Alertas y Notificaciones de Integridad:**
    El **Supervisor** recibirá alertas automáticas si se detecta un intento fallido de acceso o una inconsistencia en los sellos de integridad de los registros, permitiendo una reacción inmediata ante posibles intentos de vulneración del sistema.

---
