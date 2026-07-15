import { useState, useEffect, useCallback } from 'react'
import { fetchUsuarios } from '../services/usuariosService'
import type { Usuario } from '../types/api.types'

interface UseUsuariosResult {
    usuarios: Usuario[]
    loading:  boolean
    error:    string | null
    refetch:  () => void
}

export function useUsuarios(): UseUsuariosResult {
    const [usuarios, setUsuarios] = useState<Usuario[]>([])
    const [loading, setLoading]   = useState(true)
    const [error, setError]       = useState<string | null>(null)

    const cargar = useCallback(async () => {
        setLoading(true)
        try {
            setUsuarios(await fetchUsuarios())
            setError(null)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'No se pudieron cargar los usuarios.')
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { cargar() }, [cargar])

    return { usuarios, loading, error, refetch: cargar }
}