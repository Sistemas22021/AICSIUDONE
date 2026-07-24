import { useState, useEffect, useCallback, useRef } from 'react'
import { fetchPropuestaVigente, solicitarAnalisisMO   } from '../services/modusOperandiService'
import type { PropuestaModusOperandi } from '../types/api.types'

const POLL_INTERVAL_MS = 2000
const POLL_TIMEOUT_MS = 20000

export type EstadoCargaMO = 'analizando' | 'listo' | 'sin_analisis' | 'error'

interface UsePropuestaModusOperandiResult {
    propuesta: PropuestaModusOperandi | null
    estadoCarga: EstadoCargaMO
    refetch: () => void
    reanalizar: () => Promise<void>
}

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

    const reanalizar = useCallback(async () => {
        if (!expedienteId) return
        try {
            await solicitarAnalisisMO(expedienteId)
        } finally {
            startedAtRef.current = Date.now()   // reinicia la ventana de POLL_TIMEOUT_MS
            setPropuesta(null)
            setEstadoCarga('analizando')        // re-arma el useEffect de polling (líneas 59-63)
        }
    }, [expedienteId])

    useEffect(() => {
        if (estadoCarga !== 'analizando') return
        const interval = setInterval(cargar, POLL_INTERVAL_MS)
        return () => clearInterval(interval)
    }, [estadoCarga, cargar])

    return { propuesta, estadoCarga, refetch: cargar, reanalizar  }
}