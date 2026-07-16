import { apiClient } from './api'
import type {
    PropuestaModusOperandi,
    AprobarPropuestaMoPayload,
    CorregirPropuestaMoPayload,
    RechazarPropuestaMoPayload,
} from '../types/api.types'

function basePath(expedienteId: string): string {
    return `/expedientes/${expedienteId}/modus-operandi`
}

export async function fetchPropuestaVigente(expedienteId: string): Promise<PropuestaModusOperandi> {
    const res = await apiClient.get<{ data: PropuestaModusOperandi }>(basePath(expedienteId))
    return res.data
}

export async function fetchHistorialMO(expedienteId: string): Promise<PropuestaModusOperandi[]> {
    const res = await apiClient.get<{ data: PropuestaModusOperandi[] }>(`${basePath(expedienteId)}/historial`)
    return res.data
}

export async function aprobarPropuestaMO(
    expedienteId: string,
    propuestaId: number,
    payload: AprobarPropuestaMoPayload,
): Promise<PropuestaModusOperandi> {
    const res = await apiClient.post<{ data: PropuestaModusOperandi }>(
        `${basePath(expedienteId)}/${propuestaId}/aprobar`,
        payload,
    )
    return res.data
}

export async function corregirPropuestaMO(
    expedienteId: string,
    propuestaId: number,
    payload: CorregirPropuestaMoPayload,
): Promise<PropuestaModusOperandi> {
    const res = await apiClient.post<{ data: PropuestaModusOperandi }>(
        `${basePath(expedienteId)}/${propuestaId}/corregir`,
        payload,
    )
    return res.data
}

export async function rechazarPropuestaMO(
    expedienteId: string,
    propuestaId: number,
    payload: RechazarPropuestaMoPayload,
): Promise<PropuestaModusOperandi> {
    const res = await apiClient.post<{ data: PropuestaModusOperandi }>(
        `${basePath(expedienteId)}/${propuestaId}/rechazar`,
        payload,
    )
    return res.data
}