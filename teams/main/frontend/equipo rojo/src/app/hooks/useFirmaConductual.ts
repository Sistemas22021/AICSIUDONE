import { useState, useEffect, useCallback } from 'react'
import {
    obtenerFirmaConductualActual,
    obtenerHistorialFirmaConductual,
    type FirmaConductualResponseDTO,
} from '../services/firmaConductualService'

interface UseFirmaConductualResult {
    /** Versión vigente de la firma conductual del expediente, o null si aún no existe */
    firmaActual: FirmaConductualResponseDTO | null
    /** Historial completo de versiones (vigentes + históricas), más reciente primero */
    historial: FirmaConductualResponseDTO[]
    loadingActual: boolean
    loadingHistorial: boolean
    /** Error real (red/servidor) al consultar la firma vigente — null si simplemente no existe aún */
    error: string | null
    recargarActual: () => Promise<void>
    cargarHistorial: () => Promise<void>
}

/**
 * Consulta la firma conductual vigente y el historial de versiones de un
 * expediente, contra FirmaConductualController (GET /expediente/{id} y
 * GET /expediente/{id}/historial).
 *
 * Un 404 en la consulta de la firma vigente es un estado normal (todavía no
 * se ha registrado ninguna firma para ese expediente) y no se trata como error.
 */
export function useFirmaConductual(expedienteId: number | null): UseFirmaConductualResult {
    const [firmaActual, setFirmaActual]           = useState<FirmaConductualResponseDTO | null>(null)
    const [historial, setHistorial]               = useState<FirmaConductualResponseDTO[]>([])
    const [loadingActual, setLoadingActual]       = useState(false)
    const [loadingHistorial, setLoadingHistorial] = useState(false)
    const [error, setError]                       = useState<string | null>(null)

    const recargarActual = useCallback(async () => {
        if (!expedienteId) {
            setFirmaActual(null)
            setError(null)
            return
        }
        setLoadingActual(true)
        setError(null)
        try {
            const data = await obtenerFirmaConductualActual(expedienteId)
            setFirmaActual(data)
        } catch (err) {
            setFirmaActual(null)
            const mensaje = err instanceof Error ? err.message : ''
            // "No existe una firma conductual…" (404) es un estado esperado, no un error de sistema.
            if (mensaje && !/no existe una firma conductual/i.test(mensaje)) {
                setError(mensaje)
                console.warn('[useFirmaConductual] Error al consultar la firma vigente:', err)
            }
        } finally {
            setLoadingActual(false)
        }
    }, [expedienteId])

    const cargarHistorial = useCallback(async () => {
        if (!expedienteId) {
            setHistorial([])
            return
        }
        setLoadingHistorial(true)
        try {
            const data = await obtenerHistorialFirmaConductual(expedienteId)
            setHistorial(data)
        } catch (err) {
            console.warn('[useFirmaConductual] No se pudo obtener el historial:', err)
            setHistorial([])
        } finally {
            setLoadingHistorial(false)
        }
    }, [expedienteId])

    useEffect(() => {
        recargarActual()
        setHistorial([])
    }, [recargarActual])

    return {
        firmaActual,
        historial,
        loadingActual,
        loadingHistorial,
        error,
        recargarActual,
        cargarHistorial,
    }
}