export type TwistDirection = 'Dextrógiro (Derecha)' | 'Levógiro (Izquierda)';

export type PercussionType = 'Fuego Central' | 'Fuego Anular';

export type EvidenceStatus = 'En Bóveda' | 'En Tribunal' | 'Destruida';

export interface BalisticMetadata {
  expediente: string;
  calibre: string;
  estrias: number;
  twist: TwistDirection;
  percusion: PercussionType;
  fabricante: string;
  imageHash?: string;
}

export interface CorrelationResult {
  evidencia: string;
  fecha: string;
  caso: string;
  match: string;
  estado: EvidenceStatus;
}

export type MatchConfidence = 'Alta' | 'Media' | 'Baja';
export type VerificationStatus = 'Pendiente' | 'Confirmada' | 'Descartada';

export interface MatchRankingResult extends CorrelationResult {
  id: string;
  score: number;
  confidence: MatchConfidence;
  recurrenceCount: number;
  weaponType: string;
  location: string;
  verificationStatus: VerificationStatus;
}

export interface AuditLog {
  timestamp: string;
  usuario: string;
  accion: string;
  evidencia?: string;
  parametros?: string;
  ip: string;
}
