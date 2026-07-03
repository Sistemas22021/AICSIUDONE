import { apiClient } from './api'

/**
 * Servicio de comunicación HTTP para firmas digitales.
 * firmar pasos y consultar historial.
 */

export interface FirmaChecklistResponseDTO {
    id: number
    pasoChecklistId: number
    paso: string
    investigadorId: number
    investigadorNombre: string
    timestampFirma: string
    exitoso: boolean
    motivoFallo: string | null
}

export interface FirmarPasoRequestDTO {
    investigadorId: number
    pin: string
}

/** Firma digitalmente el paso actual y lo avanza en el backend */
export async function firmarYAvanzarPaso(
    escenaId: number,
    dto: FirmarPasoRequestDTO
): Promise<void> {
    await apiClient.patch(`/escenas/${escenaId}/firmar-y-avanzar`, dto)
}

/** Obtiene el historial de firmas de todos los pasos de una escena */
export async function obtenerHistorialFirmas(
    escenaId: number
): Promise<FirmaChecklistResponseDTO[]> {
    const res = await apiClient.get<{ data: FirmaChecklistResponseDTO[] }>(
        `/escenas/${escenaId}/firmas`
    )
    return res.data
}