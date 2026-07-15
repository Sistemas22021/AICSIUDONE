export enum TwistDirection {
  DEXTRORSUM = 'DEXTRORSUM',
  SINISTRORSUM = 'SINISTRORSUM',
  NONE = 'NONE'
}

export enum PercussionType {
  CENTRAL = 'CENTRAL',
  ANULAR = 'ANULAR',
  ELECTRICA = 'ELECTRICA',
  LATERAL = 'LATERAL'
}

export enum FormStatus {
  IDLE = 'idle',
  SAVING = 'saving',
  SUCCESS = 'success',
  ERROR = 'error',
}

export interface EvidenceFormState {
  expediente: string;
  calibre: string;
  estrias: string; // Keep as string for text input, validate as number later
  twist: TwistDirection | '';
  percussion: PercussionType | '';
  marca: string;
}

export interface EvidenceFormErrors {
  expediente?: string;
  calibre?: string;
  estrias?: string;
  twist?: string;
  percussion?: string;
  marca?: string;
  general?: string;
}

export interface EvidenceRecord extends EvidenceFormState {
  id: string;
  createdAt: string;
  previewUrl: string | null;
}
