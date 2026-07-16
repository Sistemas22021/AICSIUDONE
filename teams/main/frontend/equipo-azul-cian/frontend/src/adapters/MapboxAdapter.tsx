import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';
import type { Assignment } from '../types/assignment';
import type { MapAdapter } from './MapAdapter';

export class MapboxAdapter implements MapAdapter {
  render(props: {
    incidents: Incident[];
    patrols: Patrol[];
    assignments: Assignment[];
    height?: string;
  }) {
    return (
      <div
        style={{
          height: props.height ?? '600px',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          background: '#111827',
          border: '2px dashed #3b82f6',
          borderRadius: '12px',
          color: 'white',
          fontSize: '22px',
          fontWeight: 'bold',
          textAlign: 'center',
          flexDirection: 'column'
        }}
      >
        🗺️ MAPBOX ADAPTER ACTIVO
        <br />
        (Proveedor alternativo seleccionado)
      </div>
    );
  }
}