import { useState } from 'react';
import { login, redirectWithToken } from '../services/authService';

/**
 * useAuth — Hook de autenticación para el Login MFE.
 *
 * Encapsula el estado del formulario, el manejo de errores y el
 * flujo de redirección post-login. El componente LoginForm solo
 * necesita renderizar la UI — toda la lógica vive aquí.
 *
 * Principio: Separación entre lógica (hook) y presentación (componente).
 */
export function useAuth() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Ejecuta el login y redirige si es exitoso.
   * Actualiza el estado de error si falla.
   */
  async function handleLogin(username: string, password: string) {
    setIsLoading(true);
    setError(null);

    try {
      const result = await login({ username, password });
      redirectWithToken(result.accessToken);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido');
    } finally {
      setIsLoading(false);
    }
  }

  return { handleLogin, isLoading, error };
}
