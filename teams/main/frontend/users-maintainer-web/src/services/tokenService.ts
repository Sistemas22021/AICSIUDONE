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
  }

  return null;
}

export function redirectToLogin(): void {
  const currentUrl = window.location.href;
  window.location.href = `${LOGIN_MFE_URL}?redirect=${encodeURIComponent(currentUrl)}`;
}
