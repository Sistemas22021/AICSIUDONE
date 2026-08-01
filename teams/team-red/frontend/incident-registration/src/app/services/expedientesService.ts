import { apiClient } from './api'
import type { ExpedienteActivo, FiltrosBusquedaExpediente } from '../types/api.types'


export interface ExpedienteEstadoDTO {
  id: number
  estadoExpediente: string
}

export async function obtenerExpedientePorId(id: number): Promise<ExpedienteEstadoDTO> {
  const res = await apiClient.get<{ data: ExpedienteEstadoDTO }>(`/expedientes/${id}`)
  return res.data
}

export async function fetchExpedientesActivos(): Promise<ExpedienteActivo[]> {
  const res = await apiClient.get<{ data: ExpedienteActivo[] }>(
      '/expedientes?estatus=ACTIVO&sort=fechaCreacion,desc',
  )
  return res.data
}

export async function fetchTodosLosExpedientes(): Promise<ExpedienteActivo[]> {
  const res = await apiClient.get<{ data: ExpedienteActivo[] }>(
      '/expedientes?sort=fechaCreacion,desc',
  )
  return res.data
}

function construirQueryDeFiltros(filtros: Partial<FiltrosBusquedaExpediente>): string {
  const params = new URLSearchParams()

  filtros.tiposDelito?.forEach(tipo => params.append('tiposDelito', tipo))
  if (filtros.municipio)        params.set('municipio', filtros.municipio)
  if (filtros.colonia)          params.set('colonia', filtros.colonia)
  if (filtros.latitud != null)  params.set('latitud', String(filtros.latitud))
  if (filtros.longitud != null) params.set('longitud', String(filtros.longitud))
  if (filtros.radioKm != null)  params.set('radioKm', String(filtros.radioKm))
  if (filtros.fechaDesde)       params.set('fechaDesde', filtros.fechaDesde)
  if (filtros.fechaHasta)       params.set('fechaHasta', filtros.fechaHasta)
  params.set('sort', 'fechaCreacion,desc')

  return params.toString()
}

export async function buscarExpedientesConFiltros(
    filtros: Partial<FiltrosBusquedaExpediente>,
): Promise<ExpedienteActivo[]> {
  const query = construirQueryDeFiltros(filtros)
  const res = await apiClient.get<{ data: ExpedienteActivo[] }>(`/expedientes/buscar?${query}`)
  return res.data
}