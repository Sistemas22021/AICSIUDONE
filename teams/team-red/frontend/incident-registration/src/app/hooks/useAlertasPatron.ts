import { useState, useEffect, useCallback, useRef } from 'react'
import {
    fetchAlertas,
    fetchAlertasPorInvestigador,
    marcarAlertaRevisada,
    marcarAlertaDescartada,
} from '../services/alertaPatronService'
import type { AlertaPatron, RevisarAlertaPayload, DescartarAlertaPayload } from '../types/api.types'

// Las alertas se generan de forma asíncrona en el backend: se
// refresca en segundo plano para reflejar alertas nuevas sin recargar la página.
const POLL_INTERVAL_MS = 20000

interface UseAlertasPatronResult {
    /** Panel del Guardia: todas las alertas del sistema. */
    todas: AlertaPatron[]
    /** Bandeja de notificaciones del Investigador (vacía si no se pasó investigadorId). */
    misAlertas: AlertaPatron[]
    loading: boolean
    error: string | null
    refetch: () => void
    revisar: (alertaId: number, payload: RevisarAlertaPayload) => Promise<void>
    descartar: (alertaId: number, payload: DescartarAlertaPayload) => Promise<void>
}

export function useAlertasPatron(investigadorId: string | null): UseAlertasPatronResult {
    const [todas, setTodas] = useState<AlertaPatron[]>([])
    const [misAlertas, setMisAlertas] = useState<AlertaPatron[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const primeraCargaRef = useRef(true)

    const cargar = useCallback(async () => {
        try {
            if (primeraCargaRef.current) setLoading(true)
            const [listaTodas, listaPersonal] = await Promise.all([
                fetchAlertas(),
                investigadorId ? fetchAlertasPorInvestigador(investigadorId) : Promise.resolve<AlertaPatron[]>([]),
            ])
            setTodas(listaTodas)
            setMisAlertas(listaPersonal)
            setError(null)
        } catch (err) {
            console.warn('[useAlertasPatron] No se pudieron cargar las alertas de patrón.', err)
            setError('No se pudieron cargar las alertas de patrón.')
        } finally {
            primeraCargaRef.current = false
            setLoading(false)
        }
    }, [investigadorId])

    useEffect(() => { cargar() }, [cargar])

    useEffect(() => {
        const interval = setInterval(cargar, POLL_INTERVAL_MS)
        return () => clearInterval(interval)
    }, [cargar])

    const reemplazarEnListas = useCallback((actualizada: AlertaPatron) => {
        setTodas(prev => prev.map(a => (a.id === actualizada.id ? actualizada : a)))
        setMisAlertas(prev => prev.map(a => (a.id === actualizada.id ? actualizada : a)))
    }, [])

    const revisar = useCallback(async (alertaId: number, payload: RevisarAlertaPayload) => {
        const actualizada = await marcarAlertaRevisada(alertaId, payload)
        reemplazarEnListas(actualizada)
    }, [reemplazarEnListas])

    const descartar = useCallback(async (alertaId: number, payload: DescartarAlertaPayload) => {
        const actualizada = await marcarAlertaDescartada(alertaId, payload)
        reemplazarEnListas(actualizada)
    }, [reemplazarEnListas])

    return { todas, misAlertas, loading, error, refetch: cargar, revisar, descartar }
}