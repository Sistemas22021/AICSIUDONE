# ─────────────────────────────────────────────────────────────────────────────
# ADR-001: Arquitectura Hexagonal en el Auth Service
#
# Status: Aceptado
# Date: 2026-04
# Autores: Equipo main
# ─────────────────────────────────────────────────────────────────────────────

# ADR-001: Arquitectura Hexagonal (Ports & Adapters) en el Auth Service

## Contexto

El Auth Service es el microservicio más crítico del ecosistema SSO. Gestiona credenciales
de usuario, genera tokens JWT y mantiene sesiones activas. Dado su rol central, cualquier
cambio en la tecnología subyacente (base de datos, protocolo de comunicación, formato de token)
debe poder realizarse sin afectar la lógica de negocio.

Al mismo tiempo, este proyecto es educativo: los estudiantes deben poder seguir el código
intuitivamente y entender la separación de responsabilidades.

## Decisión

Implementar el Auth Service con **Arquitectura Hexagonal** (también llamada Ports & Adapters
o Clean Architecture), organizando el código en tres capas explícitas:

```
auth-service/
└── src/main/java/com/sso/auth/
    ├── domain/           ← NÚCLEO: sin dependencias de frameworks
    │   ├── model/        ← Entidades y Value Objects puros (Java records)
    │   └── port/
    │       ├── in/       ← Interfaces de casos de uso (lo que el dominio ofrece)
    │       └── out/      ← Interfaces de repositorios (lo que el dominio necesita)
    ├── application/      ← ORQUESTACIÓN: implementa los casos de uso
    │   └── service/      ← Depende solo del dominio. Testeable con Mockito puro.
    └── infrastructure/   ← DETALLES: Spring, JPA, JWT, REST
        └── adapter/
            ├── in/rest/  ← Controllers (HTTP → Dominio)
            └── out/persistence/ ← Repositorios JPA (Dominio → BD)
```

## Consecuencias

**Positivas:**
- Los casos de uso (`application/`) son testeables sin Spring ni base de datos
- Se puede cambiar la BD (PostgreSQL → MongoDB) sin tocar el dominio
- Cada capa tiene una responsabilidad clarísima → facilita revisión pedagógica
- El dominio en `record` de Java 17 es inmutable y auto-documentado

**Negativas:**
- Más archivos que un enfoque tradicional de MVC (Controller → Service → Repository)
- Los estudiantes deben entender el flujo de adaptadores antes de modificar código

## Alternativas Descartadas

| Alternativa | Razón del descarte |
|---|---|
| MVC tradicional (Controller → Service → Repository) | Simple pero mezcla lógica de negocio con infraestructura |
| CQRS completo | Sobreingeniería para el alcance del boilerplate |
| Spring Data directamente en el servicio | Acopla el dominio a JPA |

## Referencias

- "Hexagonal Architecture" — Alistair Cockburn (2005)
- "Clean Architecture" — Robert C. Martin (2017)
