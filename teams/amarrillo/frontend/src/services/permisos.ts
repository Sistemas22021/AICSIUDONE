// frontend/src/services/permisos.ts
// Helper central de permisos por rol.
// IMPORTANTE: esto es solo experiencia de usuario (ocultar botones).
// La seguridad real se aplica en el backend (ManejadorAutorizacion).

export type Rol = 'ADMIN' | 'ANALISTA' | 'SUPERVISOR' | 'AUDITOR';

const norm = (rol?: string | null): string => (rol || '').toUpperCase();

/** Crear o editar entidades (personas, vehiculos, sucesos, etc.) */
export const puedeEditar = (rol?: string | null): boolean =>
  ['ADMIN', 'ANALISTA'].includes(norm(rol));

/** Eliminar registros: exclusivo del administrador */
export const puedeEliminar = (rol?: string | null): boolean =>
  norm(rol) === 'ADMIN';

/** Validar alertas (cambiar su estado) */
export const puedeValidarAlertas = (rol?: string | null): boolean =>
  ['ADMIN', 'ANALISTA', 'SUPERVISOR'].includes(norm(rol));

/** Gestionar usuarios del sistema */
export const puedeGestionarUsuarios = (rol?: string | null): boolean =>
  norm(rol) === 'ADMIN';

/** El auditor solo observa */
export const esSoloLectura = (rol?: string | null): boolean =>
  norm(rol) === 'AUDITOR';
