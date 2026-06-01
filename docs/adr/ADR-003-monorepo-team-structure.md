# ADR-003: Monorepo con separación por equipos

## Status: Aceptado | Date: 2026-04

## Contexto

El ecosistema SSO está diseñado para ser usado por múltiples equipos universitarios
en paralelo. Se necesitaba una estrategia de organización de código que:

1. Facilitara la evaluación al tener todo el código en un mismo repositorio
2. Mantuviera independencia entre equipos (un equipo no puede romper el código de otro)
3. Permitiera pipelines de CI/CD independientes por proyecto

## Decisión

Usar un **Monorepo** con estructura de carpetas por equipo:

```
teams/
├── main/          ← Equipo principal (este repo)
│   ├── backend/
│   └── frontend/
├── team-alpha/    ← Equipo Alpha (lo crea el equipo Alpha)
│   ├── backend/
│   └── frontend/
└── team-beta/     ← Equipo Beta
```

La independencia entre pipelines se logra con el filtro `paths:` en GitHub Actions:
```yaml
paths:
  - "teams/main/backend/auth-service/**"
```

Un push de `team-alpha` al monorepo NO activa los pipelines del equipo `main`.

## Consecuencias

**Positivas:**
- Un solo `git clone` para acceder a todo el ecosistema
- El profesor puede evaluar todos los proyectos desde un lugar
- Los pipelines están en el mismo repo (más fácil de auditar)

**Negativas:**
- Los repositorios de cada equipo crecen con el código de los otros
- Se necesita disciplina para no modificar carpetas ajenas
- Hace `git history` más ruidoso (mitigado con paths filter)

## Regla de equipo

Cada equipo **solo puede modificar archivos dentro de su carpeta** `teams/<nombre-equipo>/`.
Las carpetas compartidas (`docker/`, `config-repo/`, `infrastructure/`) se coordinan con el equipo main.
