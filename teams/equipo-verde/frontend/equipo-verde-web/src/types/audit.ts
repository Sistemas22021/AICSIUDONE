export enum AuditActionType {
  IMAGE_UPLOAD = 'Carga de imagen',
  STATE_CHANGE = 'Cambio de estado',
  RECORD_CREATED = 'Registro creado',
  RECORD_UPDATED = 'Registro modificado',
  RECORD_DELETED = 'Registro eliminado',
  COMPARISON_RUN = 'Cotejo realizado',
  LOGIN = 'Inicio de sesión',
  EXPORT = 'Exportación de datos',
}

export interface AuditFieldChange {
  field: string;
  oldValue: string;
  newValue: string;
}

export interface AuditEntry {
  id: string;
  timestamp: string;
  userId: string;
  userName: string;
  userRole: string;
  actionType: AuditActionType;
  targetEntity: string;    // Ej: "EXP-2026-089"
  description: string;
  ipAddress: string;
  changes: AuditFieldChange[];  // Vacío si no es modificación
}
