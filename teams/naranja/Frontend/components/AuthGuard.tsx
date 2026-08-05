'use client';

import { useEffect, useState, Suspense } from 'react';
import { usePathname, useSearchParams } from 'next/navigation';
import { getAccessToken, setAccessToken, redirectToLogin } from '@/lib/api';

function AuthGuardLogic({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      const tokenFromUrl = searchParams.get('token');

      if (tokenFromUrl) {
        // 1. Guardar el token en almacenamiento persistente (localStorage / cookie)
        setAccessToken(tokenFromUrl);

        // 2. Limpiar el 'token' de la URL de forma silenciosa sin disparar re-renders de Next.js
        const newParams = new URLSearchParams(searchParams.toString());
        newParams.delete('token');
        const queryString = newParams.toString();
        const cleanUrl = pathname + (queryString ? `?${queryString}` : '');

        // Reemplaza la URL en la barra de direcciones de forma síncrona
        window.history.replaceState(null, '', cleanUrl);

        setIsAuthorized(true);
        return;
      }

      // 3. Si no venía en la URL, verificar el token persistido
      const currentToken = getAccessToken();
      if (!currentToken) {
        redirectToLogin();
        return;
      }

      setIsAuthorized(true);
    };

    checkAuth();
  }, [pathname, searchParams]);

  if (!isAuthorized) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <p className="text-sm text-gray-500">Verificando sesión en Custodia 360...</p>
      </div>
    );
  }

  return <>{children}</>;
}

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  return (
    <Suspense
      fallback={
        <div className="flex h-screen w-full items-center justify-center">
          <p className="text-sm text-gray-500">Cargando...</p>
        </div>
      }
    >
      <AuthGuardLogic>{children}</AuthGuardLogic>
    </Suspense>
  );
}