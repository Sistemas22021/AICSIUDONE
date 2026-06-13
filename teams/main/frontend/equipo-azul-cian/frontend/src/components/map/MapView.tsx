import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents
} from 'react-leaflet';
import L from 'leaflet';
import type { Incident } from '../../types/incident';

const incidentIcon = new L.Icon({
  iconUrl:
    'https://maps.google.com/mapfiles/ms/icons/red-dot.png',
  iconSize: [32, 32]
});

type OnSelectPosition = (lat: number, lng: number) => void;

interface Props {
  incidents?: Incident[];
  onSelectPosition?: OnSelectPosition;
  selectedPosition?: [number, number] | null;
  height?: string;
}

/**
 * Captura clicks del mapa y devuelve coordenadas al padre
 */
const MapClickHandler = ({
  onSelectPosition
}: {
  onSelectPosition?: Props['onSelectPosition'];
}) => {
  useMapEvents({
    click(e) {
      const { lat, lng } = e.latlng;
      onSelectPosition?.(lat, lng);
    }
  });

  return null;
};

const MapView: React.FC<Props> = ({
  incidents = [],
  onSelectPosition,
  selectedPosition,
  height = '600px'
}) => {
  return (
    <div
      style={{
        height,
        width: '100%',
        borderRadius: '12px',
        overflow: 'hidden'
      }}
    >
      <MapContainer
        center={[10.4806, -66.9036]}
        zoom={13}
        style={{
          height: '100%',
          width: '100%'
        }}
      >
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

        {/* CLICK HANDLER */}
        <MapClickHandler onSelectPosition={onSelectPosition} />

        {/* INCIDENTES (FIX CRÍTICO AQUÍ) */}
        {incidents.map((incident) => {
          const lat = Number(incident.latitude);
          const lng = Number(incident.longitude);

          const isValid =
            Number.isFinite(lat) &&
            Number.isFinite(lng);

          if (!isValid) {
            console.warn(
              'Coordenadas inválidas en incidente:',
              incident
            );
            return null;
          }

          return (
            <Marker
              key={incident.id}
              position={[lat, lng]}
              icon={incidentIcon}
            >
              <Popup>
                <strong>{incident.type}</strong>
                <br />
                {incident.description}
                <br />
                Estado: {incident.status}
              </Popup>
            </Marker>
          );
        })}

        {/* MARCADOR SELECCIONADO */}
        {selectedPosition && (
          <Marker
            position={selectedPosition}
            icon={incidentIcon}
          />
        )}
      </MapContainer>
    </div>
  );
};

export default MapView;