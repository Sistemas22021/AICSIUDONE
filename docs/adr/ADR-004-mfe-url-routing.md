# ADR-004: MFE Integration via URL Routing (sin Module Federation)

## Status: Aceptado | Date: 2026-04

## Contexto

Existen varias estrategias para integrar Micro-Frontends (MFEs):

| Estrategia | Complejidad | Requisito |
|---|---|---|
| **URL Routing** (elegida) | Baja | Ninguno |
| iframes | Media | Política de CORS |
| Module Federation (Webpack) | Alta | Webpack 5 en todos |
| Single-SPA | Alta | Framework agnóstico |

El objetivo del proyecto es educativo: los estudiantes deben entender el concepto
de SSO y micro-frontends sin que la complejidad técnica de la integración oscurezca
el aprendizaje.

## Decisión

Usar **URL Routing simple**: cada MFE es una aplicación React independiente
que vive en una URL distinta. La comunicación entre apps se hace mediante:

1. **Redirección con query params** (`?redirect=<url>`, `?token=<jwt>`)
2. **HttpOnly Cookies** para el refresh token (automático por el browser)
3. **Variables en memoria** para el access token (dentro de cada app)

## Flujo de navegación

```
Consumer App (localhost:3001)
  → detecta sin token
  → redirige a: Login MFE (localhost:3000)?redirect=http://localhost:3001

Login MFE (localhost:3000)
  → usuario se autentica
  → redirige a: http://localhost:3001?token=<jwt>

Consumer App (localhost:3001)
  → extrae token del query param
  → limpia URL (window.history.replaceState)
  → guarda token en memoria
  → muestra contenido protegido
```

## Consecuencias

**Positivas:**
- Cualquier framework puede participar (React, Vue, Angular, Vanilla JS)
- Fácil de entender para estudiantes
- Sin dependencias entre proyectos (cada uno builda independiente)

**Negativas:**
- El token pasa por la URL (visible en historial del browser)
  → Mitigado: JWT de 15 min + limpieza inmediata de la URL
- No hay estado compartido en tiempo real entre MFEs
  → Aceptable para el caso de uso educativo

## Ruta de migración futura

Para producción real se puede migrar a Module Federation o Single-SPA
sin cambiar el flujo de autenticación (solo el mecanismo de composición de UI).
