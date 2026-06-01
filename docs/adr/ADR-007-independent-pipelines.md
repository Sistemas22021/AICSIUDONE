# ADR-007: CI/CD — Pipeline independiente por proyecto (paths filter)

## Status: Aceptado | Date: 2026-04

## Contexto

El monorepo contiene 6 proyectos desplegables. Se requería que:
1. Un cambio en un proyecto NO active los deploys de los demás
2. Cada proyecto pueda desplegarse de forma autónoma
3. Los pipelines de front y back NO se crucen

## Decisión

Un archivo de workflow de GitHub Actions por proyecto, con filtro `paths:`:

| Workflow | Activa con cambios en |
|---|---|
| `main-eureka-server.yml` | `teams/main/backend/eureka-server/**` |
| `main-config-server.yml` | `teams/main/backend/config-server/**` + `config-repo/**` |
| `main-auth-service.yml` | `teams/main/backend/auth-service/**` |
| `main-api-gateway.yml` | `teams/main/backend/api-gateway/**` |
| `main-login-mfe.yml` | `teams/main/frontend/login-mfe/**` |
| `main-consumer-app.yml` | `teams/main/frontend/consumer-app/**` |

Cada workflow tiene 4 stages:
1. `test` → en todo PR y push
2. `build` → solo en push (genera imagen Docker y la sube a ECR)
3. `deploy-dev` → solo en `main` branch
4. `deploy-prod` → solo en tags `vX.X.X` (con aprobación manual en GitHub Environments)

## Convención de naming

Los equipos seguirán el patrón `{equipo}-{proyecto}.yml`:
- `team-alpha-products-service.yml`
- `team-alpha-products-frontend.yml`

Esto permite filtrar los workflows visualmente en la pestaña Actions del repo.

## Consecuencias

- **Positivo**: Un fallo en el test del Login MFE no bloquea el deploy del Auth Service
- **Positivo**: Los estudiantes pueden ver exactamente qué pipeline corresponde a qué código
- **Negativo**: 6+ archivos de workflow (aceptable, hay mucha repetición intencional para claridad pedagógica)
