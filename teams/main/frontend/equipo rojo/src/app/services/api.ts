/**
 * Cliente base para comunicación con el backend (Spring Boot).
 *
 * En DESARROLLO: VITE_API_URL debe estar vacío en .env
 *   → las peticiones van a /api/v1/... y el proxy de Vite las redirige a
 *     http://localhost:8080/api/v1/... sin problemas de CORS.
 *
 * En PRODUCCIÓN: asignar VITE_API_URL con la URL real del servidor.
 *   → las peticiones van directamente a https://mi-backend.com/api/v1/...
 *
 * Endpoints del backend:
 *   GET  /api/v1/delitos/categorias     → DelitoTipo[]  (DelitoCategoriasController)
 *   POST /api/v1/incidentes             → ExpedienteResponse  (IncidenteController)
 */

// Si VITE_API_URL está vacío, las URLs quedan relativas (/api/v1/...)
// y el proxy de Vite las reenvía a localhost:8080 sin CORS.
const BASE_URL  = import.meta.env.VITE_API_URL ?? ''
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
  return response.json() as Promise<T>
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

  async patch<T>(path: string, body?: unknown): Promise<T> {
    const response = await fetch(`${BASE_URL}${API_PREFIX}${path}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
    return handleResponse<T>(response)
  },

  async delete<T>(path: string): Promise<T> {
    const response = await fetch(`${BASE_URL}${API_PREFIX}${path}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
    })
    return handleResponse<T>(response)
  },
}
