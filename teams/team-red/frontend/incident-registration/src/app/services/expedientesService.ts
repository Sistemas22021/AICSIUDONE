import { apiClient } from './api'
import type { ExpedienteActivo, PageResponse, FiltrosBusquedaExpediente } from '../types/api.types'


export interface ExpedientesQuery {
  estatus?:   'ACTIVO' | 'TODOS' | string
  sort?:      string
  busqueda?:  string
  page?:      number
  size?:      number
}

function buildQueryString(query: ExpedientesQuery): string {
  const params = new URLSearchParams()
  if (query.estatus && query.estatus !== 'TODOS') params.set('estatus', query.estatus)
  params.set('sort', query.sort ?? 'fechaCreacion,desc')
  if (query.busqueda) params.set('busqueda', query.busqueda)
  params.set('page', String(query.page ?? 0))
  params.set('size', String(query.size ?? 10))
  return params.toString()
}

export async function fetchExpedientesPaginado(
    query: ExpedientesQuery = {},
): Promise<PageResponse<ExpedienteActivo>> {
  const res = await apiClient.get<{ data: PageResponse<ExpedienteActivo> }>(
      `/expedientes?${buildQueryString(query)}`,
  )
  return res.data
}

/** @deprecated usar fetchExpedientesPaginado — se mantiene por compatibilidad con AnalisisMOTab/CasosPanel. */
export async function fetchExpedientesActivos(): Promise<ExpedienteActivo[]> {
  const pagina = await fetchExpedientesPaginado({ estatus: 'ACTIVO', size: 200 })
  return pagina.content
}

/** @deprecated usar fetchExpedientesPaginado — se mantiene por compatibilidad. */
export async function fetchTodosLosExpedientes(): Promise<ExpedienteActivo[]> {
  const pagina = await fetchExpedientesPaginado({ estatus: 'TODOS', size: 200 })
  return pagina.content
}

export async function buscarExpedientesConFiltros(
    filtros: FiltrosBusquedaExpediente,
): Promise<ExpedienteActivo[]> {
  const params = new URLSearchParams()
  filtros.tiposDelito.forEach(t => params.append('tiposDelito', t))
  if (filtros.municipio.trim())  params.set('municipio', filtros.municipio.trim())
  if (filtros.colonia.trim())    params.set('colonia', filtros.colonia.trim())
  if (filtros.latitud != null)   params.set('latitud', String(filtros.latitud))
  if (filtros.longitud != null)  params.set('longitud', String(filtros.longitud))
  if (filtros.radioKm != null)   params.set('radioKm', String(filtros.radioKm))
  if (filtros.fechaDesde)        params.set('fechaDesde', filtros.fechaDesde)
  if (filtros.fechaHasta)        params.set('fechaHasta', filtros.fechaHasta)

  const res = await apiClient.get<{ data: ExpedienteActivo[] }>(`/expedientes/buscar?${params.toString()}`)
  return Array.isArray(res.data) ? res.data : []
}