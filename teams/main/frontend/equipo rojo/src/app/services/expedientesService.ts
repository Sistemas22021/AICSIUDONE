// Comunicarse con el backend para obtener expedientes.

import { apiClient } from './api'
import type { ExpedienteActivo } from '../types/api.types'

export async function fetchExpedientesActivos(): Promise<ExpedienteActivo[]> {
  return apiClient.get<ExpedienteActivo[]>(
    '/expedientes?estatus=ACTIVO&sort=fechaCreacion,desc',
  )
}
