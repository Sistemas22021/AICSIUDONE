import { AuditActionType, AuditEntry } from '../types/audit';

// Base de datos simulada para la demostración
let MOCK_AUDIT_LOGS: AuditEntry[] = [
  {
    id: 'a1',
    timestamp: '2026-05-21T08:30:00Z',
    userId: 'u101',
    userName: 'Perito A. Córdoba',
    userRole: 'Perito Balístico',
    actionType: AuditActionType.LOGIN,
    targetEntity: 'Sistema Central',
    description: 'Inicio de sesión exitoso mediante biometría',
    ipAddress: '192.168.1.45',
    changes: []
  },
  {
    id: 'a2',
    timestamp: '2026-05-21T09:15:22Z',
    userId: 'u101',
    userName: 'Perito A. Córdoba',
    userRole: 'Perito Balístico',
    actionType: AuditActionType.RECORD_CREATED,
    targetEntity: 'EXP-2026-089',
    description: 'Creación de nuevo expediente balístico',
    ipAddress: '192.168.1.45',
    changes: []
  },
  {
    id: 'a3',
    timestamp: '2026-05-21T09:16:05Z',
    userId: 'u101',
    userName: 'Perito A. Córdoba',
    userRole: 'Perito Balístico',
    actionType: AuditActionType.IMAGE_UPLOAD,
    targetEntity: 'EXP-2026-089',
    description: 'Carga de documentación fotográfica adjunta',
    ipAddress: '192.168.1.45',
    changes: []
  },
  {
    id: 'a4',
    timestamp: '2026-05-21T11:42:10Z',
    userId: 'u205',
    userName: 'Analista J. Pérez',
    userRole: 'Analista Forense',
    actionType: AuditActionType.COMPARISON_RUN,
    targetEntity: 'EXP-2026-089',
    description: 'Ejecución de algoritmo de cotejo contra base de datos nacional',
    ipAddress: '192.168.1.12',
    changes: []
  },
  {
    id: 'a5',
    timestamp: '2026-05-21T14:22:00Z',
    userId: 'u300',
    userName: 'Juez Tribunal 2',
    userRole: 'Magistrado',
    actionType: AuditActionType.STATE_CHANGE,
    targetEntity: 'EV-410',
    description: 'Cambio de estado de EN BÓVEDA a EN TRIBUNAL',
    ipAddress: '10.0.4.55',
    changes: []
  },
  {
    id: 'a6',
    timestamp: '2026-05-21T16:05:33Z',
    userId: 'u101',
    userName: 'Perito A. Córdoba',
    userRole: 'Perito Balístico',
    actionType: AuditActionType.RECORD_UPDATED,
    targetEntity: 'EXP-2026-089',
    description: 'Modificación de características técnicas',
    ipAddress: '192.168.1.45',
    changes: [
      { field: 'calibre', oldValue: '9mm', newValue: '9mm Parabellum' },
      { field: 'marca', oldValue: 'Desconocida', newValue: 'Glock' }
    ]
  },
  {
    id: 'a7',
    timestamp: '2026-05-21T17:30:45Z',
    userId: 'u404',
    userName: 'Admin Sistema',
    userRole: 'Administrador',
    actionType: AuditActionType.RECORD_DELETED,
    targetEntity: 'EXP-2024-001',
    description: 'Eliminación por orden judicial (Expurgo)',
    ipAddress: '192.168.0.10',
    changes: []
  }
];

// Ordenar por defecto desde el más reciente
MOCK_AUDIT_LOGS.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());

class AuditService {
  getEntries(): AuditEntry[] {
    return [...MOCK_AUDIT_LOGS];
  }

  logAction(entry: Omit<AuditEntry, 'id' | 'timestamp'>): AuditEntry {
    const newEntry: AuditEntry = {
      ...entry,
      id: Math.random().toString(36).substr(2, 9),
      timestamp: new Date().toISOString()
    };
    MOCK_AUDIT_LOGS = [newEntry, ...MOCK_AUDIT_LOGS];
    return newEntry;
  }
}

export const auditService = new AuditService();
