import { useState, useEffect } from 'react'
import { apiClient } from '../services/api'
import type { DelitoTipo } from '../types/api.types'

interface UseDelitoCategoriesResult {
  tipos: DelitoTipo[]
  loading: boolean
  /** null si no hubo error, string con mensaje si se está usando el mock */
  warning: string | null
}

export function useDelitoCategories(): UseDelitoCategoriesResult {
  const [tipos, setTipos] = useState<DelitoTipo[]>([])
  const [loading, setLoading] = useState(true)
  const [warning, setWarning] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    const fetchTipos = async () => {
      try {
        setLoading(true)
        const data = await apiClient.get<DelitoTipo[]>('/delitos/categorias')
        if (!cancelled) {
          setTipos(data)
          setWarning(null)
        }
      } catch (err) {
        if (!cancelled) {
          console.warn('[useDelitoCategories] Backend no disponible, usando datos locales.', err)
          setWarning('Catálogo local — conectar backend para datos actualizados')
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    fetchTipos()
    return () => { cancelled = true }
  }, [])

  return { tipos, loading, warning }
}
