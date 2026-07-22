import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const savedUser = localStorage.getItem('sigp_equipo_azul_cian_user');
  if (savedUser) {
    try {
      const user = JSON.parse(savedUser);
      if (user.username && user.role) {
        config.headers['X-User-Name'] = user.username;
        config.headers['X-User-Role'] = user.role;
      }
    } catch {
      // Ignorar error de deserealización
    }
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;
