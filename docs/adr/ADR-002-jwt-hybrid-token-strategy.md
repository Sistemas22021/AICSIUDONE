# ─────────────────────────────────────────────────────────────────────────────
# ADR-002: Estrategia JWT Híbrida
# Status: Aceptado | Date: 2026-04
# ─────────────────────────────────────────────────────────────────────────────

# ADR-002: Estrategia de Token JWT Híbrida (Memoria + HttpOnly Cookie)

## Contexto

La estrategia de almacenamiento del token JWT es una decisión de seguridad crítica.
Existen tres opciones principales con trade-offs distintos:

| Opción | Resistente a XSS | Resistente a CSRF | Persiste al recargar |
|---|---|---|---|
| `localStorage` | ❌ No | ✅ Sí | ✅ Sí |
| `HttpOnly Cookie` para todo | ✅ Sí | ❌ No (necesita CSRF token) | ✅ Sí |
| **Híbrido (elegido)** | ✅ Sí | ✅ Sí | Parcialmente |

## Decisión

Usar una estrategia **híbrida** con dos tipos de token:

### Access Token (JWT corto plazo)
- **Duración:** 15 minutos
- **Almacenamiento:** Variable JavaScript en memoria del navegador
- **Por qué:** Invisible para scripts maliciosos (XSS); desaparece al cerrar pestaña
- **Flujo:** Se envía como `Authorization: Bearer <token>` en cada request

### Refresh Token (opaco, largo plazo)
- **Duración:** 7 días
- **Almacenamiento:** `HttpOnly Cookie` (inaccessible desde JavaScript)
- **Por qué:** Persiste entre recargas de página; el servidor puede invalidarlo borrándolo de la BD
- **Flujo:** Se envía automáticamente al endpoint `/api/v1/auth/refresh`

## Flujo de renovación (Silent Refresh)

```
1. Usuario abre Consumer App (nueva pestaña/recarga)
2. No hay accessToken en memoria → resolveToken() intenta refresh
3. El navegador envía el HttpOnly Cookie automáticamente a /auth/refresh
4. El servidor valida el refreshToken en BD y emite nuevo accessToken
5. El accessToken se guarda en memoria y el usuario no nota nada
```

## Estrategia: Refresh Token Simple (no rotación)

El mismo Refresh Token es válido hasta su expiración (7 días).

**Extensión futura (Rotación):** Cada uso del refresh emite uno nuevo e invalida el anterior.
Esto detecta robo de tokens (si el original se usa después de ser reemplazado).
Se puede implementar en `RefreshTokenService` reemplazando `findByToken` + `save` en el mismo paso.

## Consecuencias

- El accessToken dura 15 min: si expira, la app llama silenciosamente a /refresh
- Si el refreshToken expira (7 días sin actividad): el usuario debe hacer login de nuevo
- El endpoint `/refresh` debe estar en path separado (para que el Cookie solo se envíe allí)
