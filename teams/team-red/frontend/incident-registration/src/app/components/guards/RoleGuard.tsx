import { ReactNode } from 'react'
import { useAuth } from '../../context/AuthContext'

interface RoleGuardProps {
    children: ReactNode
    allowedRoles: ('OFICIAL' | 'ANALISTA')[]
    fallback?: ReactNode
}

export const RoleGuard = ({ children, allowedRoles, fallback }: RoleGuardProps) => {
    const { rol, isAuthenticated } = useAuth()

    if (!isAuthenticated) {
        return fallback || (
            <div className="flex items-center justify-center h-64 text-cyan-500">
                <p>No autenticado. Por favor, inicia sesión.</p>
            </div>
        )
    }

    if (!rol || !allowedRoles.includes(rol)) {
        return fallback || (
            <div className="flex flex-col items-center justify-center h-64 gap-4 text-center">
                <div className="text-4xl">🚫</div>
                <p className="text-cyan-500 text-sm uppercase tracking-wider">
                    No tienes permisos para acceder a esta sección.
                </p>
                <p className="text-cyan-700 text-xs">
                    Tu rol: {rol} | Roles permitidos: {allowedRoles.join(', ')}
                </p>
            </div>
        )
    }

    return <>{children}</>
}