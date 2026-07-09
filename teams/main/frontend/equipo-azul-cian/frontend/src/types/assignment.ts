import type { Incident } from './incident';
import type { Patrol } from './patrol';

export interface Assignment {
  id: number;
  incident: Incident;
  patrol: Patrol;
}