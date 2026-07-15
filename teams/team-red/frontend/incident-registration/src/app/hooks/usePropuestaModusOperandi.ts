import { useState, useEffect, useCallback, useRef } from 'react'
import { fetchPropuestaVigente } from '../services/modusOperandiService'
import type { PropuestaModusOperandi } from '../types/api.types'

const POLL_INTERVAL_MS = 4000
const POLL_TIMEOUT_MS = 60000 // deja de reintentar tras 1 minuto sin resultado

export type EstadoCargaMO = 'analizando' | 'listo' | 'sin_analisis' | 'error'

interface UsePropuestaModusOperandiResult {
    propuesta: PropuestaModusOperandi | null
    estadoCarga: EstadoCargaMO
    refetch: () => void
}

/**
 * Consulta la propuesta de MO vigente de un expediente. Como HU2 la genera de
 * forma ASÍNCRONA (evento + @Async en el backend), el resultado puede no
 * existir todavía justo después de registrar el expediente: este hook hace
 * polling corto mientras el backend responde 404 ("aún no hay propuesta"),
 * y se detiene apenas la encuentra o al superar POLL_TIMEOUT_MS.
 *
 * Nota: fetchPropuestaVigente vive en modusOperandiService.ts, el mismo
 * archivo de servicio que usa HU3 para sus acciones — el backend expone la
 * lectura y la validación desde el mismo controlador.
 */
export function usePropuestaModusOperandi(expedienteId: string | null): UsePropuestaModusOperandiResult {
    const [propuesta, setPropuesta] = useState<PropuestaModusOperandi | null>(null)
    const [estadoCarga, setEstadoCarga] = useState<EstadoCargaMO>('analizando')
    const startedAtRef = useRef<number>(Date.now())

    const cargar = useCallback(async () => {
        if (!expedienteId) return
        try {
            const data = await fetchPropuestaVigente(expedienteId)
            setPropuesta(data)
            setEstadoCarga('listo')
        } catch (err) {
            const esNoEncontrado = err instanceof Error && err.message.includes('HTTP 404')
            if (esNoEncontrado) {
                const transcurrido = Date.now() - startedAtRef.current
                setEstadoCarga(transcurrido > POLL_TIMEOUT_MS ? 'sin_analisis' : 'analizando')
            } else {
                console.error('[usePropuestaModusOperandi] Error consultando propuesta MO', err)
                setEstadoCarga('error')
            }
        }
    }, [expedienteId])

    // Reinicia el estado cada vez que cambia el expediente observado
    useEffect(() => {
        startedAtRef.current = Date.now()
        setPropuesta(null)
        setEstadoCarga('analizando')
    }, [expedienteId])

    useEffect(() => { cargar() }, [cargar])

    useEffect(() => {
        if (estadoCarga !== 'analizando') return
        const interval = setInterval(cargar, POLL_INTERVAL_MS)
        return () => clearInterval(interval)
    }, [estadoCarga, cargar])

    return { propuesta, estadoCarga, refetch: cargar }
}