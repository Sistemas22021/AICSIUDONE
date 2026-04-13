import { ReactNode, useEffect, useState } from 'react';
import { resolveToken, redirectToLogin, setAccessToken } from '../services/tokenService';

interface AuthGuardProps {
  children: ReactNode;
}

type AuthState = 'loading' | 'authenticated' | 'unauthenticated';

/**
 * AuthGuard — Higher-Order Component de autenticación.
 *
 * Protege las rutas de la Consumer App. Si el usuario no tiene
 * una sesión válida, redirige al Login MFE.
 *
 * Flujo:
 *  1. Intenta resolver el token (memoria → URL → refresh cookie)
 *  2. Si tiene token: renderiza los children (ruta protegida)
 *  3. Si no tiene token: redirige al Login MFE
 *
 * Mientras resuelve el token, muestra un estado de carga.
 */
export function AuthGuard({ children }: AuthGuardProps) {
  const [authState, setAuthState] = useState<AuthState>('loading');

  useEffect(() => {
    async function checkAuth() {
      const token = await resolveToken();

      if (token) {
        setAccessToken(token);
        setAuthState('authenticated');
      } else {
        setAuthState('unauthenticated');
        redirectToLogin();
      }
    }

    checkAuth();
  }, []);

  if (authState === 'loading') {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin w-10 h-10 border-4 border-indigo-500 border-t-transparent
                          rounded-full mx-auto mb-4" />
          <p className="text-slate-400 text-sm">Verificando sesión...</p>
        </div>
      </div>
    );
  }

  if (authState === 'unauthenticated') {
    // El redirect ya fue disparado, mostrar mensaje temporal
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center">
        <p className="text-slate-400 text-sm">Redirigiendo al login...</p>
      </div>
    );
  }

  return <>{children}</>;
}
