// services/ssoAuth.ts
// Logica del SSO del profesor (solo se usa cuando VITE_AUTH_MODE === 'sso').
// Flujo (segun ADR-004 del boilerplate): URL routing con ?token=<jwt>.
//
//  1. App sin token -> redirige a Login MFE con ?redirect=<url-de-nexo>
//  2. Profesor autentica -> redirige de vuelta con ?token=<jwt>
//  3. Nexo extrae el token, limpia la URL, lo guarda EN MEMORIA
//  4. Cada request manda Authorization: Bearer <token>
//  5. Al expirar (15 min), se intenta /auth/refresh (HttpOnly cookie)

import type { UserSession } from '../types';
import { SSO_CONFIG } from './authMode';

// El access token vive SOLO en memoria (no en localStorage), segun ADR-002.
let accessTokenEnMemoria: string | null = null;

export const getAccessToken = () => accessTokenEnMemoria;
export const setAccessToken = (t: string | null) => { accessTokenEnMemoria = t; };

// Decodifica el payload de un JWT (sin verificar firma; solo para leer datos del usuario).
function decodificarJwt(token: string): any | null {
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

// Construye un UserSession a partir del JWT del profesor.
// NOTA: los nombres de los claims dependen de como el profesor arme el token.
// Se intentan varios nombres comunes y se cae a valores por defecto.
export function sesionDesdeToken(token: string): UserSession {
  const p = decodificarJwt(token) || {};
  return {
    token,
    username: p.sub || p.username || p.user || 'usuario',
    nombreCompleto: p.nombreCompleto || p.name || p.sub || 'Usuario SSO',
    rol: p.rol || p.role || (Array.isArray(p.roles) ? p.roles[0] : '') || 'INVESTIGADOR',
  };
}

// Redirige al login del profesor, pidiendo volver a esta misma URL.
export function redirigirALogin() {
  const redirect = encodeURIComponent(window.location.origin + window.location.pathname);
  window.location.href = `${SSO_CONFIG.loginUrl}?redirect=${redirect}`;
}

// Si la URL trae ?token=..., lo extrae, limpia la URL y devuelve el token.
export function capturarTokenDeUrl(): string | null {
  const params = new URLSearchParams(window.location.search);
  const token = params.get('token');
  if (!token) return null;
  // Limpiar la URL para que el token no quede en el historial
  params.delete('token');
  const limpia = window.location.pathname + (params.toString() ? `?${params}` : '');
  window.history.replaceState({}, document.title, limpia);
  return token;
}

// Silent refresh: pide un nuevo accessToken usando el HttpOnly cookie.
// Devuelve el nuevo token o null si no se pudo.
export async function intentarRefresh(): Promise<string | null> {
  try {
    const res = await fetch(`${SSO_CONFIG.authApiUrl}/api/v1/auth/refresh`, {
      method: 'POST',
      credentials: 'include', // imprescindible: envia el HttpOnly cookie
    });
    if (!res.ok) return null;
    const data = await res.json();
    if (data?.accessToken) {
      setAccessToken(data.accessToken);
      return data.accessToken;
    }
    return null;
  } catch {
    return null;
  }
}

// Resuelve la sesion al arrancar la app en modo SSO:
//  - primero mira si volvimos del login con ?token=
//  - si no, intenta un refresh silencioso (por si hay cookie valida)
//  - si nada funciona, devuelve null (hay que redirigir al login)
export async function resolverSesionSSO(): Promise<UserSession | null> {
  const tokenUrl = capturarTokenDeUrl();
  if (tokenUrl) {
    setAccessToken(tokenUrl);
    return sesionDesdeToken(tokenUrl);
  }
  const refrescado = await intentarRefresh();
  if (refrescado) {
    return sesionDesdeToken(refrescado);
  }
  return null;
}

// Logout en modo SSO: limpia memoria. (Opcional: redirigir al logout del profesor.)
export function logoutSSO() {
  setAccessToken(null);
}
