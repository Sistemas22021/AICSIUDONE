import { apiClient } from './api'
import type {
    AlertaPatron,
    RevisarAlertaPayload,
    DescartarAlertaPayload,
} from '../types/api.types'

/** Panel del Guardia: todas las alertas del sistema (HU6, CA3). */
export async function fetchAlertas(): Promise<AlertaPatron[]> {
    const res = await apiClient.get<{ data: AlertaPatron[] }>('/alertas')
    return res.data
}

/** Bandeja de notificaciones del Investigador asignado */
export async function fetchAlertasPorInvestigador(investigadorId: string): Promise<AlertaPatron[]> {
    const res = await apiClient.get<{ data: AlertaPatron[] }>(`/alertas/investigador/${investigadorId}`)
    return res.data
}

export async function marcarAlertaRevisada(
    alertaId: number,
    payload: RevisarAlertaPayload,
): Promise<AlertaPatron> {
    const res = await apiClient.patch<{ data: AlertaPatron }>(`/alertas/${alertaId}/revisar`, payload)
    return res.data
}

export async function marcarAlertaDescartada(
    alertaId: number,
    payload: DescartarAlertaPayload,
): Promise<AlertaPatron> {
    const res = await apiClient.patch<{ data: AlertaPatron }>(`/alertas/${alertaId}/descartar`, payload)
    return res.data
}