/**
 * Cliente base para comunicación con el API Gateway (Spring Boot).
 * URL configurada via VITE_API_URL en .env
 *
 * Endpoints esperados del incident-service:
 *   GET  /api/v1/delitos/categorias     → DelitoTipo[]
 *   POST /api/v1/incidentes             → Incidente creado
 *
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
const API_PREFIX = '/api/v1'

function getAuthHeader(): Record<string, string> {
  // El token lo guarda el login-mfe en localStorage bajo la clave 'auth_token'
  const token = localStorage.getItem('auth_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorBody = await response.text().catch(() => '')
    throw new Error(`HTTP ${response.status}: ${errorBody || response.statusText}`)
  }
  return await response.json() as Promise<T>
}

export const apiClient = {
  async get<T>(path: string): Promise<T> {
    const response = await fetch(`${BASE_URL}${API_PREFIX}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
    })
    return handleResponse<T>(response)
  },

  async post<T>(path: string, body: unknown): Promise<T> {
    const response = await fetch(`${BASE_URL}${API_PREFIX}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: JSON.stringify(body),
    })
    return handleResponse<T>(response)
  },

  async put<T>(path: string, body: unknown): Promise<T> {
    const response = await fetch(`${BASE_URL}${API_PREFIX}${path}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: JSON.stringify(body),
    })
    return handleResponse<T>(response)
  },
}
