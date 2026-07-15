import { useState, useEffect, useCallback } from 'react'
import { fetchExpedientesActivos, fetchTodosLosExpedientes  } from '../services/expedientesService'
import { MOCK_EXPEDIENTES }        from '../data/mockExpedientes'
import type { ExpedienteActivo }   from '../types/api.types'

const POLL_INTERVAL_MS = 30_000

interface UseExpedientesActivosOptions {
  filtro?: 'ACTIVO' | 'TODOS'
}

interface UseExpedientesActivosResult {
  expedientes:         ExpedienteActivo[]
  loading:             boolean
  usingMock:           boolean
  ultimaActualizacion: Date | null
  refetch:             () => void
}

export function useExpedientesActivos({ filtro = 'ACTIVO' }: UseExpedientesActivosOptions = {}): UseExpedientesActivosResult {
  const [expedientes, setExpedientes] = useState<ExpedienteActivo[]>([])
  const [loading, setLoading] = useState(true)
  const [usingMock, setUsingMock] = useState(false)
  const [ultimaActualizacion, setUltimaActualizacion] = useState<Date | null>(null)

  const fetch = useCallback(async () => {
    try {
      const data = filtro === 'ACTIVO'
          ? await fetchExpedientesActivos()
          : await fetchTodosLosExpedientes()

      setExpedientes(data)
      setUsingMock(false)
    } catch {
      console.warn('[useExpedientesActivos] Backend no disponible, usando datos mock.')
      const sorted = [...MOCK_EXPEDIENTES].sort(
          (a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime(),
      )
      setExpedientes(sorted)
      setUsingMock(true)
    } finally {
      setLoading(false)
      setUltimaActualizacion(new Date())
    }
  }, [filtro])
  useEffect(() => { fetch() }, [fetch])

  useEffect(() => {
    const interval = setInterval(fetch, POLL_INTERVAL_MS)
    return () => clearInterval(interval)
  }, [fetch])

  return { expedientes, loading, usingMock, ultimaActualizacion, refetch: fetch }
}
