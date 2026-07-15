/**
 * bulletService.ts
 * Servicio para comunicarse con el backend balistic-services
 * a través del proxy configurado en vite.config.ts
 * Base URL del API: /api/v1/bullet  →  http://localhost:8080/api/v1/bullet
 */

const BASE_URL = '/api/v1/bullet';
const CALIBER_URL = '/api/v1/caliber';

// ─── Tipos que coinciden con el BulletDTO del backend ───────────────────────

export interface BulletDTO {
  idBullet?: number;
  caseFile: string;        // Número de expediente (ej. EXP-2026-089)
  landsAndGrooves: number; // Número de estrías
  percussionType: string;  // CENTRAL | ANULAR | ELECTRICA | LATERAL
  twistDirection: string;  // DEXTRORSUM | SINISTRORSUM | NONE
  caliber: number;         // ID del calibre
  manufacturer: string;    // Marca del fabricante
  createdAt?: string;
  isDelete?: boolean;
  images?: string[];       // URLs de imágenes
}

export interface CaliberDTO {
  idCaliber: number;
  name: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/** Error tipado con el mensaje devuelto por el backend */
export class BackendApiError extends Error {
  status: number;
  backendMessage: string;

  constructor(status: number, backendMessage: string) {
    super(backendMessage);
    this.status = status;
    this.backendMessage = backendMessage;
    this.name = 'BackendApiError';
  }
}

/**
 * Lee la respuesta de error del backend (JSON con campo "message")
 * y lanza un BackendApiError con el status HTTP y el mensaje del servidor.
 */
async function throwBackendError(res: Response): Promise<never> {
  let backendMessage = `Error del servidor (${res.status})`;
  try {
    const body = await res.json();
    if (body?.message) backendMessage = body.message;
    else if (body?.error) backendMessage = body.error;
  } catch {
    // No pudo leer JSON — usar mensaje genérico
  }
  throw new BackendApiError(res.status, backendMessage);
}

// ─── Funciones del servicio ──────────────────────────────────────────────────

/**
 * Obtener todas las evidencias (paginado)
 */
export async function getBullets(page = 0, size = 20): Promise<PageResponse<BulletDTO>> {
  const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
  if (!res.ok) await throwBackendError(res);
  return res.json();
}

/**
 * Obtener una evidencia por ID
 */
export async function getBulletById(id: number): Promise<BulletDTO> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) await throwBackendError(res);
  return res.json();
}

/**
 * Buscar evidencias por expediente o fabricante (paginado)
 * Endpoint: GET /api/v1/bullet/search?query=...&page=...&size=...
 */
export async function searchBullets(
  query: string,
  page = 0,
  size = 20
): Promise<PageResponse<BulletDTO>> {
  const params = new URLSearchParams({ query, page: String(page), size: String(size) });
  const res = await fetch(`${BASE_URL}/search?${params.toString()}`);
  if (!res.ok) await throwBackendError(res);
  return res.json();
}

/**
 * Crear una nueva evidencia con su imagen.
 * El backend espera multipart/form-data con los campos del BulletDTO + file.
 * Lanza BackendApiError con mensajes específicos para:
 *   413 → archivo demasiado pesado
 *   415 → tipo no soportado
 *   409 → imagen ya existente (conflict)
 */
export async function createBullet(
  bulletData: Omit<BulletDTO, 'idBullet'>,
  imageFile: File
): Promise<BulletDTO> {
  const formData = new FormData();
  formData.append('caseFile', bulletData.caseFile);
  formData.append('landsAndGrooves', String(bulletData.landsAndGrooves));
  formData.append('percussionType', bulletData.percussionType);
  formData.append('twistDirection', bulletData.twistDirection);
  formData.append('caliber', String(bulletData.caliber));
  formData.append('manufacturer', bulletData.manufacturer);
  formData.append('file', imageFile);

  const res = await fetch(BASE_URL, {
    method: 'POST',
    body: formData,
  });

  if (!res.ok) await throwBackendError(res);
  return res.json();
}

/**
 * Actualizar una evidencia existente
 */
export async function updateBullet(id: number, bulletData: BulletDTO): Promise<BulletDTO> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(bulletData),
  });
  if (!res.ok) await throwBackendError(res);
  return res.json();
}

/**
 * Eliminar una evidencia (borrado lógico)
 */
export async function deleteBullet(id: number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
  if (!res.ok) await throwBackendError(res);
}

/**
 * Obtener la URL pública de una imagen de evidencia
 */
export function getBulletImageUrl(filePath: string): string {
  return `${BASE_URL}/images/${filePath}`;
}

/**
 * Buscar calibres por nombre (autocomplete)
 * Endpoint: GET /api/v1/caliber/search?query=...
 */
export async function searchCalibers(query: string): Promise<PageResponse<CaliberDTO>> {
  const params = new URLSearchParams({ query });
  const res = await fetch(`${CALIBER_URL}/search?${params.toString()}`);
  if (!res.ok) await throwBackendError(res);
  return res.json();
}
