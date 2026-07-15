import { useState, useEffect, useCallback } from 'react'
import { apiClient } from '../services/api'

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface UsuarioOption {
    id: number
    nombre: string
    identificacion: string
    correo: string
}

interface UseUsuariosResult {
    usuarios: UsuarioOption[]
    loading: boolean
    /** null si no hubo error, string con mensaje si no se pudo cargar la lista */
    warning: string | null
    refetch: () => void
}

/**
 * Obtiene la lista de usuarios registrados (investigadores/analistas) desde
 * GET /api/v1/usuarios (UsuarioController), para poblar selects como el de
 * "Analista responsable" en el formulario de Firma Conductual.
 */
export function useUsuarios(): UseUsuariosResult {
    const [usuarios, setUsuarios] = useState<UsuarioOption[]>([])
    const [loading, setLoading]   = useState(true)
    const [warning, setWarning]   = useState<string | null>(null)

    const fetchUsuarios = useCallback(async () => {
        try {
            setLoading(true)
            const res = await apiClient.get<{ data: UsuarioOption[] }>('/usuarios')
            setUsuarios(res.data ?? [])
            setWarning(null)
        } catch (err) {
            console.warn('[useUsuarios] Backend no disponible, no se pudo cargar la lista de analistas.', err)
            setUsuarios([])
            setWarning('No se pudo cargar la lista de analistas — verifica la conexión con el backend')
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { fetchUsuarios() }, [fetchUsuarios])

    return { usuarios, loading, warning, refetch: fetchUsuarios }
}