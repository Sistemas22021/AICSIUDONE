import { useState, useEffect } from 'react'
import { apiClient } from '../services/api'
import type { DelitoTipo } from '../types/api.types'

/**
 * Datos mock usados como fallback mientras el backend no está disponible.
 * Cuando el endpoint GET /api/v1/delitos/categorias esté listo,
 * estos datos se reemplazarán automáticamente por la respuesta real.
 *
 * Formato esperado del backend:
 * [
 *   { "value": "homicidio", "label": "HOMICIDIO", "subtipos": [...] },
 *   ...
 * ]
 */
const MOCK_DELITO_TYPES: DelitoTipo[] = [
  {
    value: 'homicidio',
    label: 'HOMICIDIO',
    subtipos: [
      { value: 'intencional', label: 'INTENCIONAL' },
      { value: 'calificado', label: 'CALIFICADO' },
      { value: 'culposo', label: 'CULPOSO' },
      { value: 'femicidio', label: 'FEMICIDIO' },
    ],
  },
  {
    value: 'robo',
    label: 'ROBO',
    subtipos: [
      { value: 'persona_a_persona', label: 'PERSONA A PERSONA' },
      { value: 'vivienda', label: 'VIVIENDA' },
      { value: 'comercio', label: 'COMERCIO' },
      { value: 'agravado', label: 'AGRAVADO' },
    ],
  },
  {
    value: 'hurto',
    label: 'HURTO',
    subtipos: [
      { value: 'simple', label: 'SIMPLE' },
      { value: 'agravado', label: 'AGRAVADO' },
      { value: 'vehiculo', label: 'VEHÍCULO' },
    ],
  },
  {
    value: 'danos',
    label: 'DAÑOS',
    subtipos: [
      { value: 'propiedad_publica', label: 'PROPIEDAD PÚBLICA' },
      { value: 'propiedad_privada', label: 'PROPIEDAD PRIVADA' },
    ],
  },
  {
    value: 'delitos_sexuales',
    label: 'DELITOS SEXUALES',
    subtipos: [
      { value: 'violacion', label: 'VIOLACIÓN' },
      { value: 'abuso_menor_13', label: 'ABUSO <13 AÑOS' },
      { value: 'abuso_13_16', label: 'ABUSO 13-16 AÑOS' },
      { value: 'actos_lascivos', label: 'ACTOS LASCIVOS' },
    ],
  },
  {
    value: 'extorsion',
    label: 'EXTORSIÓN',
    subtipos: [
      { value: 'simple', label: 'SIMPLE' },
      { value: 'agravada', label: 'AGRAVADA' },
    ],
  },
]

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
          setTipos(MOCK_DELITO_TYPES)
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
