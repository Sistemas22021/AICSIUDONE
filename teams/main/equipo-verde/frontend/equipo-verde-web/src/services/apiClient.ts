import { getAccessToken, redirectToLogin } from './tokenService';

/**
 * Cliente HTTP personalizado que envuelve fetch para inyectar automáticamente
 * el token de autenticación (JWT) de tokenService en la cabecera Authorization: Bearer <token>.
 * Adicionalmente, si recibe un status 401 Unauthorized desde el API Gateway o Microservicio,
 * redirige automáticamente al usuario de vuelta al Login MFE.
 */
export async function fetchWithAuth(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
  const headers = new Headers(init?.headers || {});
  const token = getAccessToken();
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(input, {
    ...init,
    headers,
  });

  if (response.status === 401 && import.meta.env.VITE_BYPASS_AUTH !== 'true') {
    redirectToLogin();
  }

  return response;
}
