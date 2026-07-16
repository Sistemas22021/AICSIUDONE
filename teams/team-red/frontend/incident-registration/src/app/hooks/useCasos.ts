import { useState, useEffect, useCallback } from 'react'
import { obtenerCasos } from '../services/casoService'
import type { CasoResponseDTO } from '../types/api.types'

export function useCasos() {
    const [casos, setCasos] = useState<CasoResponseDTO[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const fetch = useCallback(async () => {
        try {
            setLoading(true)
            const data = await obtenerCasos()
            setCasos(data)
            setError(null)
        } catch (err) {
            console.warn('[useCasos] No se pudo cargar la lista de casos.', err)
            setError('No se pudo cargar la lista de casos.')
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { fetch() }, [fetch])

    return { casos, loading, error, refetch: fetch }
}