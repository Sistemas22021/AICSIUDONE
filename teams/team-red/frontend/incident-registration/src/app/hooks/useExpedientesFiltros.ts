import { useState } from 'react'
import type { ExpedienteActivo, EstatusExpediente } from '../types/api.types'

export type SortCol =
    | 'folioCOPP'
    | 'tipoDelito'
    | 'fechaHecho'
    | 'estatus'

export interface UseExpedientesFiltrosResult {
  filtrados:         ExpedienteActivo[]
  filtroEstatus:     EstatusExpediente | ''
  setFiltroEstatus:  (v: EstatusExpediente | '') => void
  soloAlertas:       boolean
  setSoloAlertas:    (v: boolean | ((prev: boolean) => boolean)) => void
}

/**
 * A partir de la Tarea 1, la búsqueda libre y el ordenamiento se resuelven en el
 * backend (ver useExpedientesActivos). Este hook solo aplica, sobre la página ya
 * recibida, el filtro de estatus fino (cuando corresponde mostrarlo en cliente)
 * y "Solo alertas" — este último seguirá siendo client-side hasta la Tarea 4,
 * que es quien puede proveer tieneAlertaPatron con datos reales desde el backend.
 */
export function useExpedientesFiltros(
    expedientes: ExpedienteActivo[],
): UseExpedientesFiltrosResult {
  const [filtroEstatus, setFiltroEstatus] = useState<EstatusExpediente | ''>('')
  const [soloAlertas,   setSoloAlertas]   = useState(false)

  const filtrados = expedientes.filter(e => {
    if (filtroEstatus && e.estatus !== filtroEstatus) return false
    return !(soloAlertas && !e.tieneAlertaPatron)
  })

  return { filtrados, filtroEstatus, setFiltroEstatus, soloAlertas, setSoloAlertas }
}