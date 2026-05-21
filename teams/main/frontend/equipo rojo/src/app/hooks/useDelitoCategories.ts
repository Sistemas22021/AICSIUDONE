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
 *   { "value": "delitos_graves", "label": "DELITOS GRAVES", "subtipos": [...] },
 *   ...
 * ]
 */
const MOCK_DELITO_TYPES: DelitoTipo[] = [
  // ── DELITOS GRAVES ─────────────────────────────────────────
  {
    value: 'graves_contra_nacion',
    label: 'DELITOS GRAVES — CONTRA LA NACIÓN',
    subtipos: [
      { value: 'espionaje',                      label: 'Espionaje' },
      { value: 'conspiracion_potencias',         label: 'Conspiración con Potencias Extranjeras' },
    ],
  },
  {
    value: 'graves_contra_personas',
    label: 'DELITOS GRAVES — CONTRA LAS PERSONAS',
    subtipos: [
      { value: 'homicidio_calificado_agravado',  label: 'Homicidio Calificado / Agravado' },
      { value: 'violacion',                      label: 'Violación' },
      { value: 'secuestro',                      label: 'Secuestro' },
    ],
  },
  {
    value: 'graves_contra_propiedad',
    label: 'DELITOS GRAVES — CONTRA LA PROPIEDAD',
    subtipos: [
      { value: 'robo_mano_armada_agravado',      label: 'Robo a Mano Armada Agravado' },
      { value: 'extorsion',                      label: 'Extorsión' },
    ],
  },
  {
    value: 'graves_fe_publica',
    label: 'DELITOS GRAVES — CONTRA LA FE PÚBLICA',
    subtipos: [
      { value: 'falsificacion_moneda',           label: 'Falsificación de Moneda Nacional' },
    ],
  },
  {
    value: 'graves_leyes_especiales',
    label: 'DELITOS GRAVES — LEYES ESPECIALES',
    subtipos: [
      { value: 'narcotrafico',                   label: 'Narcotráfico (Ley Orgánica de Drogas)' },
      { value: 'terrorismo_financiamiento',      label: 'Terrorismo y Financiamiento del Terrorismo' },
      { value: 'asociacion_delinquir',           label: 'Asociación para Delinquir' },
      { value: 'femicidio',                      label: 'Femicidio' },
      { value: 'corrupcion_grave',               label: 'Corrupción Grave (Peculado, Malversación)' },
    ],
  },

  // ── DELITOS MENOS GRAVES (pena ≤ 8 años) ───────────────────────────────────
  {
    value: 'menos_graves',
    label: 'DELITOS MENOS GRAVES',
    subtipos: [
      { value: 'hurto_simple',                   label: 'Hurto Simple' },
      { value: 'hurto_calificado',               label: 'Hurto Calificado' },
      { value: 'robo_simple',                    label: 'Robo Simple' },
      { value: 'estafa',                         label: 'Estafa' },
      { value: 'lesiones_personales',            label: 'Lesiones Personales' },
      { value: 'usurpacion',                     label: 'Usurpación' },
      { value: 'falsedad_documentos_privados',   label: 'Falsedad de Documentos Privados' },
    ],
  },

  // ── FALTAS (Libro III CP — multa o arresto breve) ──────────────────────────
  {
    value: 'faltas',
    label: 'FALTAS (LIBRO III CP)',
    subtipos: [
      { value: 'falta_orden_publico',            label: 'Contra el Orden Público' },
      { value: 'falta_seguridad_publica',        label: 'Contra la Seguridad Pública' },
      { value: 'falta_moralidad_publica',        label: 'Contra la Moralidad Pública' },
      { value: 'falta_proteccion_propiedad',     label: 'Contra la Protección de la Propiedad' },
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
