# ADR-005: Base de Datos — Fase 1 Neon/Supabase, Fase 2 RDS

## Status: Aceptado | Date: 2026-04

## Contexto

El proyecto necesita una base de datos PostgreSQL desde el inicio del desarrollo.
Hay tres opciones principales:

| Opción | Costo | Latencia | Administración |
|---|---|---|---|
| **Neon** (elegida Fase 1) | Gratis (Free Tier) | ~30ms | Gestionada |
| **Supabase** (alternativa Fase 1) | Gratis (Free Tier) | ~30ms | Gestionada |
| **RDS PostgreSQL** (Fase 2) | ~$15/mes (db.t3.micro) | <5ms | Gestionada |
| PostgreSQL local (Docker) | Gratis | <1ms | Manual |

## Decisión

### Fase 1 (Ahora): Neon o Supabase
- **Por qué**: Son completamente gratuitos, no requieren tarjeta de crédito para empezar,
  y el Auth Service puede conectarse con una simple URL JDBC.
- **Tradeoff aceptado**: Latencia de ~30ms desde ECS (BD en cloud externo).
  Para el volumen de una evaluación universitaria, es imperceptible.

### Fase 2 (Actividad futura): RDS PostgreSQL
- **Por qué**: Coubicado con ECS en la misma VPC → latencia <5ms.
- **Cómo migrar**: Cambiar únicamente `DATASOURCE_URL` en los secrets de GitHub Actions.
  El código Java/JPA no cambia nada (mismo PostgreSQL).
- **Costo**: ~$15/mes con db.t3.micro bajo demanda. Free Tier solo cubre 750h de db.t2.micro
  en el primer año.

## Configuración de la conexión

Para Neon:
```
DATASOURCE_URL=jdbc:postgresql://ep-xxx.us-east-2.aws.neon.tech/sso_db?sslmode=require
```

Para Supabase:
```
DATASOURCE_URL=jdbc:postgresql://db.xxx.supabase.co:5432/postgres?sslmode=require
```

El pool de conexiones Hikari está configurado conservadoramente (`maximum-pool-size=5`)
porque Neon/Supabase limitan conexiones simultáneas en sus planes gratuitos.
