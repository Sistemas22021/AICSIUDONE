import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import {
    resolveToken,
    setAccessToken,
    clearAccessToken,
    redirectToLogin,
    getAuthenticatedUsername,
} from '../services/tokenService'

type AuthState = 'verificando' | 'autenticado' | 'no-autenticado'

interface AuthContextType {
    username: string
    isAuthenticated: boolean
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [authState, setAuthState] = useState<AuthState>('verificando')
    const [username, setUsername] = useState<string>('')

    useEffect(() => {
        let cancelado = false

        async function verificarSesion() {
            const token = await resolveToken()

            if (cancelado) return

            if (token) {
                setAccessToken(token)
                const nombreUsuario = getAuthenticatedUsername() ?? 'Usuario'
                setUsername(nombreUsuario)
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
        <AuthContext.Provider value={{ username, isAuthenticated: true, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
    return ctx
}