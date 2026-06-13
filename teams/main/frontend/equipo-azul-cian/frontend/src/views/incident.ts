export type IncidentStatus =
  | 'ACTIVE'
  | 'IN_PROGRESS'
  | 'CLOSED';

/**
 * Modelo sincronizado con backend Spring Boot
 */
export interface Incident {
  id: number;
  type: string;
  description: string;

  latitude: number;
  longitude: number;

  status: IncidentStatus;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';

  createdAt?: string;
  updatedAt?: string;
  closedAt?: string;
}

/**
 * DTO para creación desde UI
 */
export type CreateIncident = {
  type: string;
  description: string;

  latitude: number;
  longitude: number;

  priority: 'LOW' | 'MEDIUM' | 'HIGH';
};