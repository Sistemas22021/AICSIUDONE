import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import {
    resolveToken,
    setAccessToken,
    clearAccessToken,
    redirectToLogin,
    getUsername,
    setUserRole,
} from '../services/tokenService'
import type { Usuario } from '../types/api.types'

type AuthState = 'verificando' | 'autenticado' | 'no-autenticado'

interface AuthContextType {
    username: string
    rol: 'OFICIAL' | 'ANALISTA' | null
    isAuthenticated: boolean
    isOficial: boolean
    isAnalista: boolean
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [authState, setAuthState] = useState<AuthState>('verificando')
    const [username, setUsername] = useState<string>('')
    const [rol, setRol] = useState<'OFICIAL' | 'ANALISTA' | null>(null)

    useEffect(() => {
        let cancelado = false

        async function verificarSesion() {
            const token = await resolveToken()

            if (cancelado) return

            if (token) {
                setAccessToken(token)
                const nombreUsuario = getUsername() ?? 'Usuario'
                setUsername(nombreUsuario)
                try {
                    const { apiClient } = await import('../services/api')
                    // Intentar obtener el usuario actual
                    const response = await apiClient.get<{ data: Usuario }>('/usuarios/username/me')
                    if (response.data) {
                        setRol(response.data.rol)
                        setUserRole(response.data.rol)
                    }
                } catch (error) {
                    console.error('Error obteniendo rol:', error)
                    // Fallback: intentar obtener de la lista de usuarios
                    try {
                        const { apiClient } = await import('../services/api')
                        const usernameFromToken = getUsername()
                        if (usernameFromToken) {
                            const response = await apiClient.get<{ data: Usuario[] }>('/usuarios')
                            const user = response.data.find(u => u.username === usernameFromToken)
                            if (user) {
                                setRol(user.rol)
                                setUserRole(user.rol)
                            }
                        }
                    } catch (fallbackError) {
                        console.error('Error en fallback:', fallbackError)
                        // Por defecto asumir OFICIAL
                        setRol('OFICIAL')
                        setUserRole('OFICIAL')
                    }
                }

                setAuthState('autenticado')
            } else {
                setAuthState('no-autenticado')
                redirectToLogin()
            }
        }

        verificarSesion()
        return () => {
            cancelado = true
        }
    }, [])

    const logout = () => {
        clearAccessToken()
        redirectToLogin()
    }

    const isOficial = rol === 'OFICIAL'
    const isAnalista = rol === 'ANALISTA'

    if (authState === 'verificando') {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#020617] text-cyan-300">
                <div className="text-center">
                    <div className="animate-spin w-10 h-10 border-4 border-cyan-400 border-t-transparent rounded-full mx-auto mb-4" />
                    <p className="text-sm uppercase tracking-[0.15em] text-cyan-400/80">Verificando sesión...</p>
                </div>
            </div>
        )
    }

    if (authState === 'no-autenticado') {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#020617] text-cyan-300">
                <p className="text-sm uppercase tracking-[0.15em] text-cyan-400/80">Redirigiendo al inicio de sesión...</p>
            </div>
        )
    }

    return (
        <AuthContext.Provider
            value={{
                username,
                rol,
                isAuthenticated: true,
                isOficial,
                isAnalista,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
    return ctx
}