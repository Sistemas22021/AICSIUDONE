// Maneja la autenticación y las llamadas al backend para la gestión de incidentes

import { IncidentPayload } from "@/types/incident";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8081";

// Variable en memoria (Seguridad: no persistir en localStorage para evitar XSS)
let accessToken: string | null = null;

// --- GESTIÓN DE AUTENTICACIÓN ---

/**
 * Retorna el token actual almacenado en memoria
 */
export const getAccessToken = () => accessToken;

/**
 * Guarda el token en memoria
 */
export const setAccessToken = (token: string) => {
  accessToken = token;
};

/**
 * Intenta resolver el token desde memoria o desde la URL (callback del SSO)
 */
export const resolveToken = async (): Promise<string | null> => {
  if (accessToken) return accessToken;

  // Si no hay en memoria, buscamos en los parámetros de la URL
  const urlParams = new URLSearchParams(window.location.search);
  const tokenFromUrl = urlParams.get('token');

  if (tokenFromUrl) {
    // Limpiamos la URL para que no se vea el token por seguridad
    window.history.replaceState({}, document.title, window.location.pathname);
    return tokenFromUrl;
  }

  return null;
};

/**
 * Redirige al Portal SSO Centralizado si no hay sesión
 */
export const redirectToLogin = () => {
  // Ajusta esta URL a la de tu MFE de Login/SSO
  const SSO_URL = "http://localhost:5173/login";
  const callbackUrl = window.location.href;
  window.location.href = `${SSO_URL}?callback=${encodeURIComponent(callbackUrl)}`;
};


// --- LLAMADAS AL BACKEND (API) ---

/**
 * Crea un nuevo incidente enviando el token en el Header Authorization
 */
export async function createIncident(payload: IncidentPayload): Promise<void> {
  const token = getAccessToken();

  const response = await fetch(`${API_URL}/api/v1/incidents`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      // Inyectamos el token JWT para que el backend nos autorice
      "Authorization": token ? `Bearer ${token}` : ""
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    if (response.status === 401) {
      // Si el token expiró, forzamos login
      redirectToLogin();
    }
    throw new Error(`Error al guardar el expediente. Código: ${response.status}`);
  }
}