import React from 'react';
import { useAuth, UserRole } from './authContext';

interface ProtectedRouteProps {
  allowedRoles: UserRole[];
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  allowedRoles,
  children,
  fallback = (
    <div style={{ padding: '2rem', textAlign: 'center', color: '#e53e3e' }}>
      <h2>Acceso Denegado</h2>
      <p>No tienes los permisos necesarios para acceder a esta sección.</p>
    </div>
  ),
}) => {
  const { hasRole } = useAuth();

  if (!hasRole(allowedRoles)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
