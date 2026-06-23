/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useMemo, type ReactNode } from 'react'

export type UserRole =
  | 'Oficial Penitenciario'
  | 'Administrador del Sistema'
  | 'Oficial de Seguimiento'
  | 'Supervisor'

export interface AuthState {
  username: string
  role: UserRole
  hasRole: (...roles: UserRole[]) => boolean
}

const AuthContext = createContext<AuthState | null>(null)

const MOCK_USERS: { username: string; role: UserRole }[] = [
  { username: 'Carlos Méndez',   role: 'Oficial Penitenciario' },
  { username: 'Ana Rodríguez',   role: 'Administrador del Sistema' },
  { username: 'María González',  role: 'Oficial de Seguimiento' },
  { username: 'Pedro Castillo',  role: 'Supervisor' },
]

const VALID_ROLES: UserRole[] = ['Oficial Penitenciario', 'Administrador del Sistema', 'Oficial de Seguimiento', 'Supervisor']

function getInitialAuth(): AuthState {
  let raw = localStorage.getItem('mock_user')
  if (!raw) {
    const defaultUser = MOCK_USERS[0]
    localStorage.setItem('mock_user', JSON.stringify(defaultUser))
    raw = JSON.stringify(defaultUser)
  }
  const parsed = JSON.parse(raw!)
  
  if (!VALID_ROLES.includes(parsed.role)) {
    const defaultUser = MOCK_USERS[0]
    localStorage.setItem('mock_user', JSON.stringify(defaultUser))
    parsed.username = defaultUser.username
    parsed.role = defaultUser.role
  }
  
  sessionStorage.setItem('token', 'mock.jwt.dev.' + btoa(parsed.username))
  sessionStorage.setItem('username', parsed.username)

  const hasRole = (...roles: UserRole[]) => roles.includes(parsed.role as UserRole)

  return { username: parsed.username, role: parsed.role, hasRole }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const auth = useMemo(() => getInitialAuth(), [])

  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
