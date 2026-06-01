import axios from 'axios';
import toast from 'react-hot-toast';
import { getAccessToken, clearToken, redirectToLogin } from './tokenService';

export const api = axios.create({
  baseURL: 'http://localhost:8085/api',
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      clearToken();
      redirectToLogin();
      return Promise.reject(error);
    }
    const errorMsg = error.response?.data?.message || 'Ocurrió un error inesperado';
    toast.error(errorMsg);
    return Promise.reject(error);
  }
);

// Mocks of JWT tokens if needed...
