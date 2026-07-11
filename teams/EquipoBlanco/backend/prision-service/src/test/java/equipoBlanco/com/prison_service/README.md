# Documentación de Pruebas Unitarias - Módulos (prison-service)

Este directorio contiene todas las pruebas unitarias organizadas por módulo. Cada prueba está desarrollada utilizando el patrón **AAA (Arrange, Act, Assert)** y simulando las interacciones con bases de datos u otros servicios externos utilizando **Mockito**.

## Estructura de Directorios

```text
C:\...\prison_service\modules
├── README.md
├── cells
│   └── service
│       └── CellServiceTest.java
├── control
│   └── model
│       └── AlertaModelTest.java
├── inmates
│   └── service
│       └── InmateServiceTest.java
└── postpenal
    └── service
        └── PostPenalServiceTest.java
```

A continuación, se detalla la función de cada test implementado en los diferentes módulos del sistema.

---

## Módulo: `cells`

### `CellServiceTest`
Esta clase de prueba se encarga de verificar el comportamiento y las reglas de negocio aisladas de los servicios asociados a las celdas, mockeando los repositorios `CellRepository`, `InmateRepository` y `CellAssignmentRepository`.

#### Tests Implementados:
1. **`createCell_ShouldCreateAndReturnCellDto_WhenIdentifierDoesNotExist`**: Verifica el camino feliz para la creación de una celda con un ID único.
2. **`createCell_ShouldThrowException_WhenIdentifierAlreadyExists`**: Comprueba la regla de negocio que prohíbe crear celdas con identificadores duplicados.
3. **`assignInmate_ShouldAssignInmate_WhenConditionsAreMet`**: Valida el proceso exitoso de asignación de un recluso a una celda disponible.
4. **`assignInmate_ShouldThrowException_WhenCellIsBlocked`**: Confirma la restricción de que una celda en investigación (bloqueada) no puede recibir reclusos.

---

## Módulo: `control`

### `AlertaModelTest`
Debido a que el módulo `control` por los momentos solo aloja la entidad `Alerta` y su repositorio (sin un servicio complejo), se optó por probar el modelo y su builder.

#### Tests Implementados:
1. **`testAlertaBuilder`**: Valida la correcta construcción del objeto `Alerta` a través del patrón Builder asegurando que todas las propiedades (nivel, fecha de emisión, destinatario, estado, acción) se asignen correctamente.

---

## Módulo: `inmates`

### `InmateServiceTest`
Esta clase de prueba se centra en la lógica de negocio y los flujos de estado del recluso, mockeando las interacciones de persistencia con `InmateRepository`.

#### Tests Implementados:
1. **`testRegisterTemporaryEgress_Success`**: Valida el registro exitoso de la salida temporal de un recluso.
2. **`testRegisterTemporaryEgress_Failure_AlreadyEgressed`**: Verifica la regla de negocio de prevención de doble salida, lanzando una excepción si ya está fuera.
3. **`testRegisterTemporaryReturn_Success`**: Comprueba el proceso de reingreso de un recluso y su cambio de estado.
4. **`testRegisterTemporaryReturn_Failure_NotOnTemporaryEgress`**: Confirma la regla que impide registrar un retorno temporal para un recluso que no ha salido.

---

## Módulo: `postpenal`

### `PostPenalServiceTest`
Clase encargada de probar la lógica del servicio post-penal para la creación, asignación y completación de perfiles (expedientes de seguimiento). Simula repositorios como `ExpedienteSeguimientoRepository`, `AlertaRepository` y `InmateRepository`.

#### Tests Implementados:
1. **`createBaseProfile_ShouldSaveExpedienteAndAlerta`**: Verifica que al crear un perfil base para un recluso que egresa, se guarde exitosamente su expediente inicial y se genere una `Alerta` para el sistema.
2. **`assignOfficer_ShouldAssignAndCreateAlerta`**: Comprueba que la asignación de un oficial a un expediente funcione, actualizando el estado del expediente a "asignado" y emitiendo una alerta dirigida al oficial correspondiente.
3. **`completeProfile_ShouldUpdateAndChangeState`**: Valida el completado del expediente con datos (ej. domicilio, contacto) confirmando que el expediente modifique su estado a "completado" adecuadamente.
