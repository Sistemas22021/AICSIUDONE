/**
 * tokenService.ts — Gestión del Access Token en la Consumer App
 *
 * El accessToken se guarda en una variable de módulo (memoria del navegador).
 * Ventajas:
 *   ✅ Invisible para JavaScript malicioso (protección XSS)
 *   ✅ Se limpia automáticamente al cerrar la pestaña
 *   ✅ No persiste en disco
 *
 * Desventaja:
 *   ⚠️ Se pierde al recargar la página → se llama a /refresh automáticamente
 */

const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8090';
const LOGIN_MFE_URL = import.meta.env.VITE_LOGIN_MFE_URL || 'http://localhost:3000';

// ─── Store en memoria ─────────────────────────────────────────────────────────

let _accessToken: string | null = null;

// ─── Funciones públicas ───────────────────────────────────────────────────────

/** Guarda el token en memoria */
export function setAccessToken(token: string): void {
  _accessToken = token;
}

/** Obtiene el token actual */
export function getAccessToken(): string | null {
  return _accessToken;
}

/** Verifica si hay un token activo en memoria */
export function hasValidToken(): boolean {
  return _accessToken !== null;
}

/** Limpia el token (logout local) */
export function clearToken(): void {
  _accessToken = null;
}

/**
 * Intenta obtener el token desde diferentes fuentes:
 * 1. Memoria (caso normal, misma sesión)
 * 2. Query param ?token=<jwt> (redirección desde login-mfe)
 * 3. Cookie de refresco → llamar a /refresh (recarga de página)
 *
 * Retorna el token si se pudo obtener, null si hay que hacer login.
 */
export async function resolveToken(): Promise<string | null> {
  // 1. Ya tenemos token en memoria
  if (_accessToken) return _accessToken;

  // 2. Viene de una redirección del Login MFE (?token=...)
  const urlParams = new URLSearchParams(window.location.search);
  const tokenFromUrl = urlParams.get('token');
  if (tokenFromUrl) {
    _accessToken = tokenFromUrl;
    // Limpiar el token de la URL por seguridad (reemplazar sin recargar)
    const cleanUrl = window.location.pathname;
    window.history.replaceState({}, document.title, cleanUrl);
    return _accessToken;
  }

  // 3. Intentar refrescar con el HttpOnly Cookie (si existe)
  try {
    const response = await fetch(`${API_GATEWAY_URL}/api/v1/auth/refresh`, {
      method: 'POST',
      credentials: 'include', // Envía el Cookie automáticamente
    });

    if (response.ok) {
      const data = await response.json();
      _accessToken = data.accessToken;
      return _accessToken;
    }
  } catch {
    // No hay Cookie o el refresh falló → necesita login
  }

  return null;
}

/**
 * Redirige al Login MFE con la URL actual como parámetro de retorno.
 * El Login MFE autenticará y redirigirá de vuelta aquí con el token.
 */
export function redirectToLogin(): void {
  const currentUrl = window.location.href;
  window.location.href = `${LOGIN_MFE_URL}?redirect=${encodeURIComponent(currentUrl)}`;
}
