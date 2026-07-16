import type { ReactNode } from 'react';
import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';
import type { Assignment } from '../types/assignment';

export interface MapAdapterProps {
  incidents: Incident[];
  patrols: Patrol[];
  assignments: Assignment[];
  height?: string;
}

export interface MapAdapter {
  render(props: MapAdapterProps): ReactNode;
}