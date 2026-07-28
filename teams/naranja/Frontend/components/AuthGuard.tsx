'use client';

import { useEffect, useState, Suspense } from 'react';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { getAccessToken, setAccessToken, redirectToLogin } from '@/lib/api';

// 1. Componente interno que maneja la lógica con useSearchParams
function AuthGuardLogic({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const [isAuthorized, setIsAuthorized] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      // 1. Verificar si viene regresando del Login MFE con un token en la URL
      const tokenFromUrl = searchParams.get('token');

      if (tokenFromUrl) {
        setAccessToken(tokenFromUrl);
        // Limpiar la URL para no dejar expuesto el token
        const newParams = new URLSearchParams(searchParams.toString());
        newParams.delete('token');
        const cleanUrl = pathname + (newParams.toString() ? `?${newParams.toString()}` : '');
        router.replace(cleanUrl);
        setIsAuthorized(true);
        return;
      }

      // 2. Si no hay token en la URL ni en memoria, redirigir al Login MFE
      const currentToken = getAccessToken();
      if (!currentToken) {
        redirectToLogin();
        return;
      }

      setIsAuthorized(true);
    };

    checkAuth();
  }, [pathname, searchParams, router]);

  if (!isAuthorized) {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <p className="text-sm text-gray-500">Verificando sesión en Custodia 360...</p>
      </div>
    );
  }

  return <>{children}</>;
}

// 2. Export principal envuelto en Suspense
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