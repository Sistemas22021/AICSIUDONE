import MapView from '../components/map/MapView';
import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';
import type { Assignment } from '../types/assignment';
import type { MapAdapter } from './MapAdapter';

export class OpenStreetMapAdapter implements MapAdapter {
  render(props: {
    incidents: Incident[];
    patrols: Patrol[];
    assignments: Assignment[];
    height?: string;
  }) {
    return (
      <MapView
        incidents={props.incidents}
        patrols={props.patrols}
        assignments={props.assignments}
        {...(props.height
          ? { height: props.height }
          : {})}
      />
    );
  }
}