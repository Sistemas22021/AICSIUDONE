import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { UserSession } from '../types';
import { esSSO } from './authMode';
import { resolverSesionSSO, setAccessToken, logoutSSO } from './ssoAuth';

interface AuthCtx {
  user: UserSession | null;
  login: (u: UserSession) => void;
  logout: () => void;
  cargando: boolean; // true mientras se resuelve la sesion (importante en SSO)
}

const AuthContext = createContext<AuthCtx | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<UserSession | null>(null);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    let activo = true;

    const arrancar = async () => {
      if (esSSO()) {
        // Modo SSO: intentar resolver la sesion desde la URL (?token=) o refresh
        const sesion = await resolverSesionSSO();
        if (activo) {
          setUser(sesion);
          setCargando(false);
        }
      } else {
        // Modo local: comportamiento de siempre (localStorage)
        const stored = localStorage.getItem('nexo_user');
        if (stored && activo) setUser(JSON.parse(stored));
        if (activo) setCargando(false);
      }
    };

    arrancar();
    return () => { activo = false; };
  }, []);

  const login = (u: UserSession) => {
    if (esSSO()) {
      // En SSO el token ya vive en memoria (lo guardo por las dudas)
      setAccessToken(u.token);
      setUser(u);
    } else {
      localStorage.setItem('nexo_token', u.token);
      localStorage.setItem('nexo_user', JSON.stringify(u));
      setUser(u);
    }
  };

  const logout = () => {
    if (esSSO()) {
      logoutSSO();
      setUser(null);
    } else {
      localStorage.removeItem('nexo_token');
      localStorage.removeItem('nexo_user');
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, cargando }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return ctx;
};
