#### 1. Tecnologías a Usar
Para garantizar la inmutabilidad y la seguridad jurídica solicitada por el proyecto, se ha seleccionado un stack tecnológico robusto:

* **Backend:** **Java con Springboot**. Se eligió este framework por su arquitectura modular y el uso de **Java**, lo que permite un manejo de datos menos propenso a errores en el procesamiento de testimonios.
* **Base de Datos:** **PostgreSQL**. Se utilizarán *Triggers* y funciones en *PL/pgSQL* para crear tablas de auditoría (logs) que registren cualquier intento de acceso, asegurando que los registros confirmados sean técnicamente inalterables.
* **Criptografía:** Uso de una librería de criptografia para implementar **Hashing SHA-256**. Cada testimonio generado se sellará con un código único que se rompe si el texto es modificado.
* **Frontend:** **React y Next.js**, para una interfaz de usuario dinámica que permita al Oficial registrar datos de forma fluida y en tiempo real.

#### 2. Software y Hardware de Desarrollo
Recursos necesarios para la fase de construcción del sistema por parte del equipo de programación:

* **Software de Desarrollo:**
    * **IDE:** Visual Studio Code con extensiones para TypeScript y SQL.
    * **Control de Versiones:** Git (GitHub) para la gestión del código fuente.
    * **Pruebas de API:** Postman o Insomnia para validar los endpoints de registro y consulta.
* **Hardware de Desarrollo:**
    * Estaciones de trabajo con procesadores de 4 núcleos (mínimo).
    * Memoria RAM de 16GB (recomendado para el entorno de desarrollo simultáneamente).
    * Almacenamiento SSD para agilizar los tiempos de compilación.

---
