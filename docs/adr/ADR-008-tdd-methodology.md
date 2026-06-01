# ADR-008: TDD con metodología Kent Beck (Red-Green-Refactor + Tidy First)

## Status: Aceptado | Date: 2026-04

## Contexto

El proyecto es educativo. Además del código funcional, se quiere demostrar
buenas prácticas de desarrollo de software. Kent Beck's TDD y Tidy First son
metodologías probadas que mejoran la calidad del código sin sobreingeniería.

## Decisión

Aplicar el ciclo **Red → Green → Refactor** en toda la implementación:

```
🔴 RED:       Escribir un test que falle ANTES de implementar
🟢 GREEN:     Implementar el MÍNIMO código para pasar el test
🔵 REFACTOR:  Mejorar estructura SIN cambiar comportamiento
```

Y el principio **Tidy First**: separar los cambios en dos categorías de commits:

| Tipo | Descripción | Ejemplo |
|---|---|---|
| `[structural]` | Reorganización del código | Extraer método, renombrar |
| `[behavioral]` | Cambio de comportamiento | Agregar validación, nuevo endpoint |

## Estrategia de tests por capa

```
┌─────────────────────────────────────────────────┐
│  @WebMvcTest         → Controllers (HTTP layer)  │  Rápido, sin BD
│  @DataJpaTest        → Repositories (JPA layer)  │  Con H2 en memoria
│  Mockito puro        → Services (domain layer)   │  Ultrarápido
│  Testcontainers      → E2E (cuando sea necesario)│  Requiere Docker
└─────────────────────────────────────────────────┘
```

## Ejemplo de ciclo TDD en RegisterUserService

```java
// 1. 🔴 RED — Escribir el test ANTES de implementar
@Test
void shouldThrowExceptionWhenUsernameAlreadyExists() {
    when(userRepository.existsByUsername("existingUser")).thenReturn(true);
    assertThatThrownBy(() -> service.register(command))
        .isInstanceOf(UsernameAlreadyExistsException.class);
}

// 2. 🟢 GREEN — Implementar el mínimo para pasar
public User register(RegisterCommand command) {
    if (userRepository.existsByUsername(command.username())) {
        throw new UsernameAlreadyExistsException(command.username());
    }
    // ...
}

// 3. 🔵 REFACTOR — Mejorar sin romper tests
// Commit: [structural] Extraer validación de username a método privado
private void validateUsernameAvailability(String username) {
    if (userRepository.existsByUsername(username)) {
        throw new UsernameAlreadyExistsException(username);
    }
}
```

## Consecuencias

- Los tests sirven como documentación viva del comportamiento esperado
- Cada caso de uso tiene al menos 3 tests: happy path, error, y edge case
- El código refactorizado es más legible para los estudiantes que lo estudien
