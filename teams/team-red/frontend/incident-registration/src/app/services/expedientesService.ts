// Comunicarse con el backend para obtener expedientes.

import { apiClient } from './api'
import type { ExpedienteActivo } from '../types/api.types'

export async function fetchExpedientesActivos(): Promise<ExpedienteActivo[]> {
  const res = await apiClient.get<{ data: ExpedienteActivo[] }>(
      '/expedientes?estatus=ACTIVO&sort=fechaCreacion,desc',
  )
  return res.data
}
