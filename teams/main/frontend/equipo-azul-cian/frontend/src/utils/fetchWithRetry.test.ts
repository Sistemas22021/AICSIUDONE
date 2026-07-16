import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fetchWithRetry } from './fetchWithRetry';

// ─── Setup ────────────────────────────────────────────────────────────────────
// vi.useFakeTimers() hace que setTimeout() sea instantáneo en los tests,
// sin esto cada reintento esperaría 1500ms reales y los tests serían muy lentos.
beforeEach(() => {
  vi.useFakeTimers();
  vi.restoreAllMocks();
});

// Helper: construye un mock de Response con el status indicado
function mockResponse(status: number, ok: boolean): Response {
  return { status, ok } as Response;
}

// ─── Caso exitoso ─────────────────────────────────────────────────────────────
describe('fetchWithRetry — respuesta exitosa', () => {
  it('devuelve la respuesta inmediatamente si el primer intento es exitoso', async () => {
    // Mockeamos fetch global para que devuelva HTTP 200 OK
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(200, true)));

    const response = await fetchWithRetry('https://api.example.com/data');

    expect(response.ok).toBe(true);
    expect(response.status).toBe(200);
    // Si el primer intento fue exitoso, fetch solo debería llamarse una vez
    expect(fetch).toHaveBeenCalledTimes(1);
  });
});

// ─── Reintentos por error de servidor ─────────────────────────────────────────
describe('fetchWithRetry — reintentos por error de servidor (5xx)', () => {
  it('reintenta si el servidor devuelve un error (ej: 500) y eventualmente tiene éxito', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        // Primer intento: falla con 500
        .mockResolvedValueOnce(mockResponse(500, false))
        // Segundo intento: éxito con 200
        .mockResolvedValueOnce(mockResponse(200, true))
    );

    const promise = fetchWithRetry('https://api.example.com/data', undefined, 3, 100);
    // Avanzamos los timers para que el sleep() entre reintentos no bloquee
    await vi.runAllTimersAsync();
    const response = await promise;

    expect(response.ok).toBe(true);
    expect(fetch).toHaveBeenCalledTimes(2);
  });

  it('devuelve la última respuesta de error si todos los intentos fallan con 500', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(500, false)));

    const promise = fetchWithRetry('https://api.example.com/data', undefined, 3, 100);
    await vi.runAllTimersAsync();
    const response = await promise;

    // Después de 3 intentos fallidos, devuelve la última respuesta (no lanza)
    expect(response.ok).toBe(false);
    expect(response.status).toBe(500);
    expect(fetch).toHaveBeenCalledTimes(3);
  });
});

// ─── Reintentos por error de red ──────────────────────────────────────────────
describe('fetchWithRetry — reintentos por error de red', () => {
  it('reintenta si fetch lanza un error de red y eventualmente tiene éxito', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        // Primer intento: error de red (sin conexión)
        .mockRejectedValueOnce(new Error('Network Error'))
        // Segundo intento: éxito
        .mockResolvedValueOnce(mockResponse(200, true))
    );

    const promise = fetchWithRetry('https://api.example.com/data', undefined, 3, 100);
    await vi.runAllTimersAsync();
    const response = await promise;

    expect(response.ok).toBe(true);
    expect(fetch).toHaveBeenCalledTimes(2);
  });

  it('lanza un error si todos los intentos de red fallan', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('Network Error')));

    const promise = fetchWithRetry('https://api.example.com/data', undefined, 3, 100);

    // Importante: adjuntamos el handler de rechazo ANTES de avanzar los timers.
    // Si corriéramos runAllTimersAsync() primero, la promesa podría rechazarse
    // antes de que Vitest registre que el rechazo será manejado, lo que genera
    // un "UnhandledRejection" falso positivo.
    const assertion = expect(promise).rejects.toThrow(
      'Error al obtener datos del servidor. Verifica que el backend esté activo.'
    );

    await vi.runAllTimersAsync();
    await assertion;

    expect(fetch).toHaveBeenCalledTimes(3);
  });
});

// ─── Configuración de reintentos ──────────────────────────────────────────────
describe('fetchWithRetry — configuración de reintentos', () => {
  it('respeta el número de reintentos configurado (retries=1 → solo 1 intento)', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(mockResponse(500, false)));

    const promise = fetchWithRetry('https://api.example.com/data', undefined, 1, 100);
    await vi.runAllTimersAsync();
    await promise;

    // Con retries=1, solo debe intentar una vez
    expect(fetch).toHaveBeenCalledTimes(1);
  });
});
