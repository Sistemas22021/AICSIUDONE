import { ReactNode, useEffect, useState } from 'react';
import { resolveToken, redirectToLogin, setAccessToken } from '../services/tokenService';

interface AuthGuardProps {
  children: ReactNode;
}

type AuthState = 'loading' | 'authenticated' | 'unauthenticated';

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
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin w-10 h-10 border-4 border-indigo-500 border-t-transparent rounded-full mx-auto mb-4" />
          <p className="text-slate-500 text-sm">Verificando sesión segura...</p>
        </div>
      </div>
    );
  }

  if (authState === 'unauthenticated') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <p className="text-slate-500 text-sm">Redirigiendo al Login MFE...</p>
      </div>
    );
  }

  return <>{children}</>;
}
