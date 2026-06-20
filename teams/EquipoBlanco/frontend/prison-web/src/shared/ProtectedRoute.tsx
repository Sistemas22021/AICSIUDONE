import { type ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth, type UserRole } from './authContext'

interface Props {
  allowedRoles: UserRole[]
  children: ReactNode
  fallback?: string
}

export default function ProtectedRoute({ allowedRoles, children, fallback = '/dashboard' }: Props) {
  const auth = useAuth()

  if (!auth.username) {
    return <div className="p-4 text-center text-gray-500">Verificando sesión...</div>
  }

  if (!auth.hasRole(...allowedRoles)) {
    return <Navigate to={fallback} replace />
  }

  return children
}
