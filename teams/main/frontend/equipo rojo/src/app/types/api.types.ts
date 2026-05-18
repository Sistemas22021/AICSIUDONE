/**
 * Tipos compartidos para la capa de API.
 * Reflejan la estructura de respuesta del backend (incident-service).
 */

export interface DelitoSubtipo {
  value: string
  label: string
}

export interface DelitoTipo {
  value: string
  label: string
  subtipos: DelitoSubtipo[]
}

export interface ApiError {
  status: number
  message: string
  timestamp?: string
}
