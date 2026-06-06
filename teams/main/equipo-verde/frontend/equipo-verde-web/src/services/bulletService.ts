/**
 * bulletService.ts
 * Servicio para comunicarse con el backend balistic-services
 * a través del proxy configurado en vite.config.ts
 * Base URL del API: /api/v1/bullet  →  http://localhost:8080/api/v1/bullet
 */

const BASE_URL = '/api/v1/bullet';

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

// ─── Funciones del servicio ──────────────────────────────────────────────────

/**
 * Obtener todas las evidencias (paginado)
 */
export async function getBullets(page = 0, size = 20): Promise<PageResponse<BulletDTO>> {
  const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
  if (!res.ok) throw new Error(`Error al obtener evidencias: ${res.status}`);
  return res.json();
}

/**
 * Obtener una evidencia por ID
 */
export async function getBulletById(id: number): Promise<BulletDTO> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) throw new Error(`Error al obtener evidencia ${id}: ${res.status}`);
  return res.json();
}

/**
 * Crear una nueva evidencia con su imagen
 * El backend espera multipart/form-data con los campos del BulletDTO + file
 */
export async function createBullet(bulletData: Omit<BulletDTO, 'idBullet'>, imageFile: File): Promise<BulletDTO> {
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
  if (!res.ok) throw new Error(`Error al crear evidencia: ${res.status}`);
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
  if (!res.ok) throw new Error(`Error al actualizar evidencia ${id}: ${res.status}`);
  return res.json();
}

/**
 * Eliminar una evidencia (borrado lógico)
 */
export async function deleteBullet(id: number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error(`Error al eliminar evidencia ${id}: ${res.status}`);
}

/**
 * Obtener la URL pública de una imagen de evidencia
 */
export function getBulletImageUrl(filePath: string): string {
  return `${BASE_URL}/images/${filePath}`;
}
