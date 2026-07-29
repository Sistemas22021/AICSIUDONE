import { useState, useEffect, useCallback } from 'react'
import { fetchExpedientesPaginado } from '../services/expedientesService'
import { MOCK_EXPEDIENTES }        from '../data/mockExpedientes'
import type { ExpedienteActivo }   from '../types/api.types'
import type { SortCol } from './useExpedientesFiltros'

const POLL_INTERVAL_MS = 30_000
const PAGE_SIZE = 10

interface UseExpedientesActivosOptions {
  filtro?: 'ACTIVO' | 'TODOS'
}

interface UseExpedientesActivosResult {
  expedientes:         ExpedienteActivo[]
  loading:             boolean
  usingMock:           boolean
  ultimaActualizacion: Date | null
  refetch:             () => void
  // ── Paginación / orden server-side ──
  page:                number
  totalPages:          number
  totalElements:       number
  setPage:             (p: number) => void
  busqueda:            string
  setBusqueda:         (v: string) => void
  sortCol:             SortCol
  sortAsc:             boolean
  toggleSort:          (col: SortCol) => void
}

export function useExpedientesActivos({ filtro = 'ACTIVO' }: UseExpedientesActivosOptions = {}): UseExpedientesActivosResult {
  const [expedientes, setExpedientes] = useState<ExpedienteActivo[]>([])
  const [loading, setLoading] = useState(true)
  const [usingMock, setUsingMock] = useState(false)
  const [ultimaActualizacion, setUltimaActualizacion] = useState<Date | null>(null)

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [busqueda, setBusquedaState] = useState('')
  const [sortCol, setSortCol] = useState<SortCol>('folioCOPP')
  const [sortAsc, setSortAsc] = useState(false)

  // Cambiar búsqueda o filtro reinicia a la primera página.
  const setBusqueda = useCallback((v: string) => {
    setBusquedaState(v)
    setPage(0)
  }, [])

  const toggleSort = useCallback((col: SortCol) => {
    setPage(0)
    setSortCol(prevCol => {
      if (prevCol === col) {
        setSortAsc(prevAsc => !prevAsc)
        return prevCol
      }
      setSortAsc(true)
      return col
    })
  }, [])

  const fetch = useCallback(async () => {
    try {
      const sortParam = `${sortCol === 'folioCOPP' ? 'folio' : sortCol},${sortAsc ? 'asc' : 'desc'}`
      const data = await fetchExpedientesPaginado({
        estatus: filtro,
        sort: sortParam,
        busqueda: busqueda || undefined,
        page,
        size: PAGE_SIZE,
      })
      setExpedientes(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
      setUsingMock(false)
    } catch {
      console.warn('[useExpedientesActivos] Backend no disponible, usando datos mock.')
      const sorted = [...MOCK_EXPEDIENTES].sort(
          (a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime(),
      )
      setExpedientes(sorted)
      setTotalPages(1)
      setTotalElements(sorted.length)
      setUsingMock(true)
    } finally {
      setLoading(false)
      setUltimaActualizacion(new Date())
    }
  }, [filtro, page, busqueda, sortCol, sortAsc])

  useEffect(() => { fetch() }, [fetch])

  useEffect(() => {
    const interval = setInterval(fetch, POLL_INTERVAL_MS)
    return () => clearInterval(interval)
  }, [fetch])

  return {
    expedientes, loading, usingMock, ultimaActualizacion, refetch: fetch,
    page, totalPages, totalElements, setPage,
    busqueda, setBusqueda,
    sortCol, sortAsc, toggleSort,
  }
}