import { useState, useEffect } from 'react'
import { getAccessToken, setUserRole } from '../services/tokenService'
import { apiClient } from '../services/api'
import type { Usuario } from '../types/api.types'

export function useUserRole() {
    const [rol, setRol] = useState<'OFICIAL' | 'ANALISTA' | null>(null)
    const [loading, setLoading] = useState(true)
    const [username, setUsername] = useState<string | null>(null)

    useEffect(() => {
        const fetchUserRole = async () => {
            try {
                const token = getAccessToken()
                if (!token) {
                    setLoading(false)
                    return
                }

                // Obtener información del usuario
                const response = await apiClient.get<{ data: Usuario }>('/usuarios/username/me')
                // O usar el username del token para buscar

                if (response.data) {
                    setRol(response.data.rol)
                    setUsername(response.data.username)
                    setUserRole(response.data.rol)
                }
            } catch (error) {
                console.error('Error obteniendo rol del usuario:', error)
            } finally {
                setLoading(false)
            }
        }

        fetchUserRole()
    }, [])

    const isOficial = rol === 'OFICIAL'
    const isAnalista = rol === 'ANALISTA'

    return { rol, username, loading, isOficial, isAnalista }
}