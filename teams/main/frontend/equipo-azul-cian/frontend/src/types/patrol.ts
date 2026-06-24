export type PatrolStatus = 'AVAILABLE' | 'EN_ROUTE' | 'BUSY' | 'OUT_OF_SERVICE';

export interface Patrol {
  id: number;
  code: string;
  officerName: string;
  latitude: number;
  longitude: number;
  status: PatrolStatus;
  lastUpdated?: string;
}
