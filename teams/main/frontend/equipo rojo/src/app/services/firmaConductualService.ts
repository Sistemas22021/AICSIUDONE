/**
 * Responsable de la comunicación HTTP con /api/firma-conductual
 * (FirmaConductualController).
 *
 * NOTA: al igual que /api/expedientes (ver RegistroDelHecho/index.tsx),
 * este controlador vive FUERA del prefijo /api/v1 que usa `apiClient`
 * (services/api.ts), así que este servicio arma sus propias URLs con
 * `fetch` en lugar de usar `apiClient`.
 */

const BASE_URL  = import.meta.env.VITE_API_URL ?? ''
const RESOURCE  = '/api/firma-conductual'

// ─── Tipos espejo de los DTOs del backend ────────────────────────────────────

export interface FirmaConductualResponseDTO {
    id: number
    expedienteId: number
    analistaId: number
    version: number
    vigente: boolean
    fechaRegistro: string
    comportamientoPreDelictivo: string | null
    metodoAproximacion: string | null
    metodoAtaque: string | null
    comportamientoPostDelictivo: string | null
    elementosDistintivos: string | null
}

export interface RegistrarFirmaConductualRequestDTO {
    expedienteId: number
    analistaId: number
    comportamientoPreDelictivo?: string
    metodoAproximacion?: string
    metodoAtaque?: string
    comportamientoPostDelictivo?: string
    elementosDistintivos?: string
}

export interface ActualizarFirmaConductualRequestDTO {
    comportamientoPreDelictivo?: string
    metodoAproximacion?: string
    metodoAtaque?: string
    comportamientoPostDelictivo?: string
    elementosDistintivos?: string
}

interface ApiResponseDTO<T> {
    success: boolean
    message: string | null
    data: T
    errors?: unknown
}

// ─── Helpers internos ─────────────────────────────────────────────────────────

function getAuthHeader(): Record<string, string> {
    // El token lo guarda el login-mfe en localStorage bajo la clave 'auth_token'
    const token = localStorage.getItem('auth_token')
    return token ? { Authorization: `Bearer ${token}` } : {}
}

async function handleResponse<T>(response: Response): Promise<T> {
    const raw = await response.text().catch(() => '')

    let body: ApiResponseDTO<T> | null = null
    try {
        body = raw ? (JSON.parse(raw) as ApiResponseDTO<T>) : null
    } catch {
        body = null
    }

    if (!response.ok || !body || body.success === false) {
        const mensaje = body?.message || `HTTP ${response.status}: ${response.statusText}`
        throw new Error(mensaje)
    }

    return body.data
}

// ─── Métodos ──────────────────────────────────────────────────────────────────

/** Registra una nueva firma conductual para un expediente (o una nueva versión, si ya existía una vigente) */
export async function registrarFirmaConductual(
    dto: RegistrarFirmaConductualRequestDTO,
): Promise<FirmaConductualResponseDTO> {
    const response = await fetch(`${BASE_URL}${RESOURCE}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
        body: JSON.stringify(dto),
    })
    return handleResponse<FirmaConductualResponseDTO>(response)
}

/** Edita la firma conductual vigente de un expediente, creando una nueva versión */
export async function editarFirmaConductual(
    firmaId: number,
    dto: ActualizarFirmaConductualRequestDTO,
): Promise<FirmaConductualResponseDTO> {
    const response = await fetch(`${BASE_URL}${RESOURCE}/${firmaId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
        body: JSON.stringify(dto),
    })
    return handleResponse<FirmaConductualResponseDTO>(response)
}

/** Obtiene la versión vigente de la firma conductual de un expediente */
export async function obtenerFirmaConductualActual(expedienteId: number): Promise<FirmaConductualResponseDTO> {
    const response = await fetch(`${BASE_URL}${RESOURCE}/expediente/${expedienteId}`, {
        headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
    })
    return handleResponse<FirmaConductualResponseDTO>(response)
}

/** Obtiene el historial completo de versiones de la firma conductual de un expediente */
export async function obtenerHistorialFirmaConductual(expedienteId: number): Promise<FirmaConductualResponseDTO[]> {
    const response = await fetch(`${BASE_URL}${RESOURCE}/expediente/${expedienteId}/historial`, {
        headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
    })
    return handleResponse<FirmaConductualResponseDTO[]>(response)
}