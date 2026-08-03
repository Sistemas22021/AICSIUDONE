import { apiClient } from './api'
import type { PatronBusquedaResultado } from '../types/api.types'

export interface BuscarPatronesParams {
    textoMO?: string
    textoFirmaConductual?: string
    limite?: number
}

function construirQuery(params: BuscarPatronesParams): string {
    const query = new URLSearchParams()
    if (params.textoMO?.trim()) query.set('textoMO', params.textoMO.trim())
    if (params.textoFirmaConductual?.trim()) query.set('textoFirmaConductual', params.textoFirmaConductual.trim())
    if (params.limite) query.set('limite', String(params.limite))
    return query.toString()
}

/**
 * Busca expedientes con MO validado y/o firma conductual semánticamente
 * similares al texto indicado. Requiere al menos uno de los dos criterios;
 * el backend responde 422 si ambos vienen vacíos.
 */
export async function buscarPatrones(params: BuscarPatronesParams): Promise<PatronBusquedaResultado[]> {
    const query = construirQuery(params)
    const res = await apiClient.get<{ data: PatronBusquedaResultado[] }>(`/patrones/buscar?${query}`)
    return res.data
}