/**
 * authService.ts — Servicio de autenticación del Login MFE
 *
 * Responsabilidades:
 * 1. Llamar al API Gateway para autenticar al usuario
 * 2. Guardar el accessToken en memoria (variable de módulo)
 * 3. Redirigir a la app solicitante con el token
 *
 * NOTA IMPORTANTE sobre seguridad:
 * - accessToken: variable en memoria → invisible para XSS
 * - refreshToken: HttpOnly Cookie → el servidor lo gestiona automáticamente
 * - NUNCA usar localStorage o sessionStorage para tokens de seguridad
 */

const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8090';

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface AuthResult {
  accessToken: string;
  username: string;
}

// ─── Estado en memoria ────────────────────────────────────────────────────────
// El accessToken vive aquí durante la sesión del navegador.
// Si el usuario cierra la pestaña, se pierde (eso es intencional).
// El refreshToken (Cookie) lo recupera automáticamente.

let _accessToken: string | null = null;

// ─── Funciones públicas ───────────────────────────────────────────────────────

/**
 * Autentica al usuario contra el Auth Service.
 * Si es exitoso, guarda el accessToken en memoria.
 */
export async function login(credentials: LoginCredentials): Promise<AuthResult> {
  const response = await fetch(`${API_GATEWAY_URL}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // Necesario para enviar/recibir Cookies
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || 'Error de autenticación');
  }

  const data: AuthResult = await response.json();
  _accessToken = data.accessToken;
  return data;
}

/**
 * Extrae el redirect URL del query param y redirige con el accessToken.
 *
 * Flujo SSO:
 *  1. Consumer App detecta que no hay token → redirige a login-mfe?redirect=<url>
 *  2. Usuario se autentica aquí
 *  3. login-mfe redirige a <url>?token=<accessToken>
 *  4. Consumer App guarda el token en memoria y consume APIs
 *
 * NOTA DE SEGURIDAD: El token en query param es de corta vida (15 min).
 * La Consumer App debe extraerlo y limpiarlo de la URL inmediatamente.
 */
export function redirectWithToken(accessToken: string): void {
  const params = new URLSearchParams(window.location.search);
  const redirectUrl = params.get('redirect');

  if (redirectUrl) {
    // Validar que la URL de redirección es de confianza (whitelist básica)
    const url = new URL(redirectUrl);
    const isTrusted = url.hostname === 'localhost' || url.hostname.endsWith('.vercel.app');

    if (isTrusted) {
      const targetUrl = new URL(redirectUrl);
      targetUrl.searchParams.set('token', accessToken);
      window.location.href = targetUrl.toString();
      return;
    }
  }

  // Si no hay redirect o no es confiable, ir a una página por defecto
  window.location.href = '/';
}

/** Obtiene el accessToken actual de la memoria */
export function getAccessToken(): string | null {
  return _accessToken;
}

/** Limpia el accessToken de la memoria (logout local) */
export function clearAccessToken(): void {
  _accessToken = null;
}
