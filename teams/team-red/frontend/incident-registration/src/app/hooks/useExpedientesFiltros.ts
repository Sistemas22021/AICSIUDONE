import { useState } from 'react'
import type { ExpedienteActivo, EstatusExpediente } from '../types/api.types'

export type SortCol =
    | 'folioCOPP'
    | 'tipoDelito'
    | 'fechaHecho'
    | 'investigadorAsignado'
    | 'estatus'

export interface UseExpedientesFiltrosResult {
  filtrados:         ExpedienteActivo[]
  busqueda:          string
  setBusqueda:       (v: string) => void
  filtroEstatus:     EstatusExpediente | ''
  setFiltroEstatus:  (v: EstatusExpediente | '') => void
  soloAlertas:       boolean
  setSoloAlertas:    (v: boolean | ((prev: boolean) => boolean)) => void
  sortCol:           SortCol
  sortAsc:           boolean
  toggleSort:        (col: SortCol) => void
}

export function useExpedientesFiltros(
    expedientes: ExpedienteActivo[],
): UseExpedientesFiltrosResult {
  const [busqueda,      setBusqueda]      = useState('')
  const [filtroEstatus, setFiltroEstatus] = useState<EstatusExpediente | ''>('')
  const [soloAlertas,   setSoloAlertas]   = useState(false)
  const [sortCol,       setSortCol]       = useState<SortCol>('folioCOPP')
  const [sortAsc,       setSortAsc]       = useState(false)   // desc por defecto

  const toggleSort = (col: SortCol) => {
    if (sortCol === col) setSortAsc(v => !v)
    else { setSortCol(col); setSortAsc(true) }
  }

  const filtrados = expedientes
      .filter(e => {
        const q = busqueda.toLowerCase()
        if (q && ![e.folioCOPP, e.tipoDelito, e.subtipoDelito, e.investigadorAsignado, e.municipio]
            .some(s => (s ?? '').toLowerCase().includes(q))) return false
        if (filtroEstatus && e.estatus !== filtroEstatus) return false
        return !(soloAlertas && !e.tieneAlertaPatron);

      })
      .sort((a, b) => {
        const mul = sortAsc ? 1 : -1
        const valA = a[sortCol] ?? ''
        const valB = b[sortCol] ?? ''
        return valA.localeCompare(valB) * mul
      })

  return {
    filtrados,
    busqueda,      setBusqueda,
    filtroEstatus, setFiltroEstatus,
    soloAlertas,   setSoloAlertas,
    sortCol,       sortAsc, toggleSort,
  }
}