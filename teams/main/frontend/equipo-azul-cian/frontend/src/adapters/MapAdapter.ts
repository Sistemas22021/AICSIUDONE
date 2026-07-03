import type { ReactNode } from 'react';
import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';

export interface MapAdapterProps {
  incidents: Incident[];
  patrols: Patrol[];
  height?: string;
}

export interface MapAdapter {
  render(props: MapAdapterProps): ReactNode;
}