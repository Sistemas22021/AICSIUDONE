import axios from 'axios';

let accessTokenMemory: string | null = null;

export const setAccessToken = (token: string | null) => {
  accessTokenMemory = token;
};

export const getAccessToken = () => accessTokenMemory;

// Helper para redirigir al Login MFE pasando la URL actual como parámetro "redirect"
export const redirectToLogin = () => {
  if (typeof window !== 'undefined') {
    const loginMfeUrl = process.env.NEXT_PUBLIC_LOGIN_MFE_URL || 'http://localhost:3000';
    const currentAppUrl = window.location.origin + window.location.pathname;
    window.location.href = `${loginMfeUrl}?redirect=${encodeURIComponent(currentAppUrl)}`;
  }
};

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_GATEWAY_URL || 'http://localhost:8080',
  withCredentials: true, // Importante para enviar cookies HttpOnly (refresh token)
});

// 1. Interceptor de Petición: adjunta el token JWT
api.interceptors.request.use((config) => {
  if (accessTokenMemory) {
    config.headers.Authorization = `Bearer ${accessTokenMemory}`;
  }
  return config;
});

// 2. Interceptor de Respuesta: detecta 401 y redirige al SSO
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      setAccessToken(null);
      redirectToLogin(); // Si el Gateway devuelve 401 (token expirado/inválido), redirige
    }
    return Promise.reject(error);
  }
);