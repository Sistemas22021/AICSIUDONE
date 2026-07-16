/**
 * fetchWithRetry
 * 
 * Wrapper de fetch que reintenta automáticamente si la petición falla.
 * Diseñado para manejar el "cold start" de Supabase, que puede causar
 * errores transitorios en las primeras conexiones.
 *
 * @param url - La URL a la que hacer la petición
 * @param options - Opciones de fetch (method, headers, body, etc.)
 * @param retries - Número máximo de intentos (default: 3)
 * @param delayMs - Milisegundos a esperar entre intentos (default: 1500)
 */
export async function fetchWithRetry(
  url: string,
  options?: RequestInit,
  retries: number = 3,
  delayMs: number = 1500
): Promise<Response> {
  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      const res = await fetch(url, options);

      // Si la respuesta es OK, la devolvemos directamente
      if (res.ok) {
        return res;
      }

      // Si el servidor respondió con error (ej. 500) y tenemos más intentos, reintentamos
      if (attempt < retries) {
        await sleep(delayMs);
        continue;
      }

      // Último intento fallido: devolvemos la respuesta de error
      return res;

    } catch {
      // Error de red (sin conexión, timeout, etc.)
      if (attempt < retries) {
        await sleep(delayMs);
      } else {
        // Último intento fallido: lanzamos el error
        throw new Error('Error al obtener datos del servidor. Verifica que el backend esté activo.');
      }
    }
  }

  // TypeScript requiere un retorno aquí aunque el bucle siempre retorna
  throw new Error('Error inesperado en fetchWithRetry');
}

function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
