import { apiClient } from './api'
import type { FirmaConductual, FirmaConductualPayload } from '../types/api.types'

function basePath(expedienteId: string): string {
    return `/expedientes/${expedienteId}/firma-conductual`
}

export async function fetchFirmaVigente(expedienteId: string): Promise<FirmaConductual | null> {
    try {
        const res = await apiClient.get<{ data: FirmaConductual }>(basePath(expedienteId))
        return res.data
    } catch {
        return null // 404 = todavía no se ha registrado firma conductual para este expediente
    }
}

export async function fetchHistorialFirma(expedienteId: string): Promise<FirmaConductual[]> {
    const res = await apiClient.get<{ data: FirmaConductual[] }>(`${basePath(expedienteId)}/historial`)
    return res.data
}

export async function registrarFirmaConductual(
    expedienteId: string,
    payload: FirmaConductualPayload,
): Promise<FirmaConductual> {
    const res = await apiClient.post<{ data: FirmaConductual }>(basePath(expedienteId), payload)
    return res.data
}
