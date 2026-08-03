const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8090';
const LOGIN_MFE_URL = import.meta.env.VITE_LOGIN_MFE_URL || 'http://localhost:3000';

let _accessToken: string | null = null;

export function setAccessToken(token: string): void {
  _accessToken = token;
}

export function getAccessToken(): string | null {
  return _accessToken;
}

export function hasValidToken(): boolean {
  return _accessToken !== null;
}

export function clearToken(): void {
  _accessToken = null;
}

export async function resolveToken(): Promise<string | null> {
  if (_accessToken) return _accessToken;

  const urlParams = new URLSearchParams(window.location.search);
  const tokenFromUrl = urlParams.get('token');
  if (tokenFromUrl) {
    _accessToken = tokenFromUrl;
    const cleanUrl = window.location.pathname;
    window.history.replaceState({}, document.title, cleanUrl);
    return _accessToken;
  }

  try {
    const response = await fetch(`${API_GATEWAY_URL}/api/v1/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
    });

    if (response.ok) {
      const data = await response.json();
      _accessToken = data.accessToken;
      return _accessToken;
    }
  } catch {
    // No hay Cookie o el refresh falló
  }

  return null;
}

export function redirectToLogin(): void {
  const currentUrl = window.location.href;
  window.location.href = `${LOGIN_MFE_URL}?redirect=${encodeURIComponent(currentUrl)}`;
}

export async function logout(): Promise<void> {
  clearToken();
  try {
    await fetch(`${API_GATEWAY_URL}/api/v1/auth/logout`, {
      method: 'POST',
      credentials: 'include',
    });
  } catch {
    // Ignorar posible error de red
  }
  redirectToLogin();
}

export function getUserInfo(): { username: string; role: string; initials: string } {
  const token = getAccessToken();
  if (!token) return { username: 'USUARIO', role: 'Investigador / Detective', initials: 'US' };
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const username: string = (payload.sub || payload.username || 'Usuario').toString();
    let role = 'Investigador / Detective';
    const unLower = username.toLowerCase();
    if (unLower.includes('perito')) role = 'Perito Balístico (Gestor)';
    else if (unLower.includes('admin')) role = 'Admin del Sistema (TI)';
    else if (unLower.includes('fiscal')) role = 'Fiscalía / M. Público';

    const initials = username.substring(0, 2).toUpperCase();
    return { username: username.toUpperCase(), role, initials };
  } catch {
    return { username: 'USUARIO', role: 'División Forense', initials: 'UF' };
  }
}
