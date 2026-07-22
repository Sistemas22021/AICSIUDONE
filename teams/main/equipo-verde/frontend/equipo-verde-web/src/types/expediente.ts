export interface ExpedienteDTO {
  idExpedient?: number;
  caseNumber: string;
  description: string;
  status: string;
  createdAt?: string;
  isDelete?: boolean;
}

export interface ExpedientePageResponse {
  content: ExpedienteDTO[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
