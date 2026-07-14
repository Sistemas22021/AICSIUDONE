// types/audit interface alignments with Backend and Frontend

export interface AuditLogBackendEntry {
  id: string;
  rev: number;
  revTimestamp: number;
  revType: number; // 0 = ADD, 1 = MOD, 2 = DEL
  entityType: string; // "BULLET", "IMAGES"
  entityId: string;
  operator: string;
}

export interface AuditLogBackendPageResponse {
  content: AuditLogBackendEntry[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

class AuditService {
  /**
   * Obtiene los logs de auditoría reales del backend
   */
  public async getAuditLogs(page = 0, size = 10): Promise<AuditLogBackendPageResponse> {
    const res = await fetch(`/api/v1/audit-log?page=${page}&size=${size}`);
    if (!res.ok) throw new Error(`Error al obtener logs de auditoría: ${res.status}`);
    return res.json();
  }

  /**
   * Acción legacy/simulación. Dado que Hibernate Envers registra de manera
   * automática en el backend las acciones sobre el modelo de datos,
   * este método actúa como no-op para evitar errores de compilación.
   */
  public logAction(entry: any): any {
    console.log("Acción registrada localmente (Envers lo registrará en BD):", entry);
    return entry;
  }
}

export const auditService = new AuditService();
