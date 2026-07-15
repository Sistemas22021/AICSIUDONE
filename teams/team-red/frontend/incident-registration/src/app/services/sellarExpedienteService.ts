import { apiClient } from './api'
import type { ExpedienteDetalleResponse } from '../types/api.types'

export async function sellarExpediente(
    expedienteId: number,
    agenteSelladorId: number,
): Promise<ExpedienteDetalleResponse> {
    const res = await apiClient.patch<{ data: ExpedienteDetalleResponse }>(
        `/expedientes/${expedienteId}/sellar?agenteSelladorId=${agenteSelladorId}`,
    )
    return res.data
}