# Factibilidad Economica — Equipo Verde

## 1. Objetivo del analisis economico
Estimar los costos del desarrollo e implantacion inicial del modulo forense del Equipo Verde, asi como los costos operativos mensuales de mantener el sistema activo.

## 2. Supuestos base
- Duracion estimada del desarrollo inicial: 4 meses.
- Equipo de trabajo de referencia: 4 integrantes (perfil formativo/universitario con apoyo docente).
- Uso preferente de herramientas open source y capas gratuitas cuando sea posible.
- Despliegue inicial en infraestructura de bajo costo (free tier o instancia minima).

## 3. Presupuesto durante el desarrollo
### 3.1 Costos de recurso humano (referenciales)
- Coordinacion y analisis: USD 300/mes
- Desarrollo backend/frontend: USD 900/mes
- Pruebas y documentacion: USD 300/mes
- Total mensual estimado desarrollo: USD 1,500
- Total fase desarrollo (4 meses): USD 6,000

### 3.2 Herramientas y servicios de apoyo durante desarrollo
- Repositorio y CI/CD academico: USD 0 a USD 20/mes (segun limites de uso)
- Ambientes de prueba y almacenamiento: USD 20/mes
- Integracion y pruebas del motor de cotejo con OpenCV: USD 20/mes (computo/pruebas de imagen)
- Total mensual herramientas: USD 20 a USD 40
- Total fase desarrollo (4 meses): USD 80 a USD 160

## 4. Costo de inicializacion (one-time)
- Configuracion de entorno y despliegue inicial: USD 120
- Ajustes de seguridad basicos y monitoreo inicial: USD 80
- Capacitacion operativa inicial: USD 100
- Total costo de inicializacion: USD 300

## 5. Costo mensual operativo del sistema activo
- Infraestructura cloud minima (computo, red, almacenamiento): USD 35/mes
- Servicios de respaldo y monitoreo basico: USD 15/mes
- Costo incremental por ejecucion de cotejo masivo (OpenCV y procesamiento de imagen): USD 20/mes
- Mantenimiento correctivo menor y soporte: USD 100/mes
- Total mensual operativo estimado: USD 170/mes

## 6. Evaluacion de viabilidad economica
Con un costo de arranque moderado y una operacion mensual controlada, el modulo resulta economicamente factible para un contexto academico o institucional de baja a media escala. La mayor sensibilidad del presupuesto esta en horas de desarrollo, por lo que conviene priorizar alcance MVP y reutilizacion de componentes existentes del repositorio.
