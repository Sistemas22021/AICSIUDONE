import React, { createContext, useContext, useState, useEffect } from 'react';

export type UserRole = 'SUPERVISOR' | 'CENTRO_MANDO' | 'OFICIAL_PATRULLA';

export interface User {
  id: string;
  username: string;
  name: string;
  role: UserRole;
}

export const MOCK_USERS: User[] = [
  { id: '1', username: 'sup_maria', name: 'María Rodríguez', role: 'SUPERVISOR' },
  { id: '2', username: 'mando_juan', name: 'Juan Pérez', role: 'CENTRO_MANDO' },
  { id: '3', username: 'patrulla_carlos', name: 'Carlos Gómez', role: 'OFICIAL_PATRULLA' },
];

interface AuthContextType {
  currentUser: User;
  setRoleUser: (username: string) => void;
  hasRole: (allowedRoles: UserRole[]) => boolean;
}

const STORAGE_KEY = 'sigp_equipo_azul_cian_user';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<User>(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        // Fallback
      }
    }
    return MOCK_USERS[0]; // Default: SUPERVISOR
  });

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(currentUser));
  }, [currentUser]);

  const setRoleUser = (username: string) => {
    const user = MOCK_USERS.find(u => u.username === username);
    if (user) {
      setCurrentUser(user);
    }
  };

  const hasRole = (allowedRoles: UserRole[]): boolean => {
    return allowedRoles.includes(currentUser.role);
  };

  return (
    <AuthContext.Provider value={{ currentUser, setRoleUser, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe utilizarse dentro de un AuthProvider');
  }
  return context;
};
