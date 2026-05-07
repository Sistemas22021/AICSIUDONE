# Factibilidad Tecnica — Equipo Verde

## 1. Alcance tecnico evaluado
Se evalua la viabilidad tecnica para construir un modulo de analisis forense holistico con funciones de registro de evidencia, comparacion balistica y control de evidencia dubitativa dentro del ecosistema del proyecto.

## 2. Tecnologias propuestas
- Backend: Java 17 con Spring Boot 3 para exponer API REST y reglas de negocio.
- Frontend: React 18 para interfaces de registro, consulta y visualizacion.
- Base de datos: PostgreSQL para persistencia de evidencias, casos y trazabilidad.
- Motor de cotejo de imagenes: OpenCV para extraccion de caracteristicas y calculo de similitud tecnica entre evidencias.
- Seguridad e integracion: JWT y consumo a traves de API Gateway del proyecto.
- Infraestructura local: Docker Compose para orquestacion de servicios.
- Infraestructura de despliegue: AWS (EC2/S3) y aprovisionamiento con Terraform/Terragrunt segun lineamientos del repositorio.

## 3. Software de desarrollo requerido
- Sistema operativo de desarrollo (Windows, Linux o macOS).
- IDE: IntelliJ IDEA o VS Code.
- JDK 17, Maven 3.9+, Node.js 20 LTS, Git 2.40+.
- Docker Desktop 24+ para ejecucion local de contenedores.
- Librerias OpenCV (segun stack del modulo) y dependencias para procesamiento de imagenes.
- Herramientas de prueba: frameworks de testing backend/frontend definidos por el proyecto.

## 4. Hardware de desarrollo recomendado
- CPU: 4 nucleos (8 hilos) o superior.
- RAM: 16 GB recomendados (8 GB minimo).
- Almacenamiento: 20 GB libres para dependencias, imagenes Docker y artefactos.
- Conexion a internet estable para dependencias, repositorio y servicios externos.

## 5. Software y componentes para lanzamiento/despliegue
- Contenedores Docker de los servicios del modulo.
- Configuracion centralizada en `config-repo` para entornos.
- Pipeline CI/CD en GitHub Actions para build, pruebas y despliegue.
- Servicios cloud del proyecto (segun ambiente): computo, red, almacenamiento de artefactos y monitoreo basico.

## 6. Riesgos tecnicos y mitigaciones
- Riesgo: alta carga de imagenes microscopicas.  
  Mitigacion: compresion, limites de tamano y almacenamiento optimizado por ambiente.
- Riesgo: variacion de calidad en imagenes que afecte la precision del cotejo OpenCV.  
  Mitigacion: preprocesamiento estandarizado (escala de grises, normalizacion, reduccion de ruido) y umbrales de confianza calibrados.
- Riesgo: inconsistencias en datos de evidencia.  
  Mitigacion: validaciones de esquema, catalogos controlados y reglas de integridad.
- Riesgo: complejidad de trazabilidad forense.  
  Mitigacion: bitacora de auditoria por evento y pruebas de integridad en cada flujo.

## 7. Conclusion tecnica
La construccion del modulo es tecnicamente factible con la base tecnologica existente del repositorio y las capacidades de infraestructura ya definidas, siempre que se implemente una estrategia disciplinada de validacion de datos, manejo de imagenes y auditoria operativa.
