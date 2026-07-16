/**
 * Convierte una URL relativa de archivo (ej: /files/desaparecidas/foo.jpg)
 * a una URL absoluta apuntando al backend.
 *
 * En desarrollo local: usa el proxy de Vite (queda relativa).
 * En produccion: usa VITE_API_URL para apuntar al backend de Render.
 */
export function fileUrl(path?: string | null): string | undefined {
  if (!path) return undefined;

  // Si ya es una URL absoluta, devolverla tal cual
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }

  // Si es base64 (preview en formularios), devolverla tal cual
  if (path.startsWith('data:')) {
    return path;
  }

  // En desarrollo local, VITE_API_URL es vacio y el proxy de Vite redirige
  // En produccion, VITE_API_URL apunta al backend
  const baseUrl = import.meta.env.VITE_API_URL || '';

  // Si el path no empieza con /, agregarle /
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;

  return baseUrl + normalizedPath;
}