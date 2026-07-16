import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import { divIcon, LatLngBounds } from 'leaflet';
import { useEffect } from 'react';
import 'leaflet/dist/leaflet.css';

export interface PuntoMapa {
  id: string | number;
  lat: number;
  lng: number;
  tipo: string;
  titulo: string;
  subtitulo?: string;
  sospechoso?: boolean;
  campos?: { etiqueta: string; valor: string }[];
}

interface Props {
  puntos: PuntoMapa[];
  altura?: number | string;
  centroDefault?: [number, number];
  zoomDefault?: number;
  hudLabel?: string;
  hudValor?: string;
  emptyMessage?: string;
}

const iconoMaterial: Record<string, string> = {
  TALLER: 'build',
  GALPON: 'warehouse',
  TERRENO_BALDIO: 'landscape',
  DOMICILIO: 'home',
  CAJERO: 'local_atm',
  TRANSPORTE_PUBLICO: 'directions_bus',
  COMERCIO: 'storefront',
  OTRO: 'place',
  VEHICULO: 'directions_car',
  VEHICULO_ROBADO: 'directions_car',
  SUCESO: 'warning',
  AVISTAMIENTO: 'visibility',
  DESAPARECIDA: 'person_search',
};

function crearIcono(tipo: string, sospechoso?: boolean) {
  const claseTipo = tipo.toLowerCase();
  const clase = sospechoso
    ? `marker-pin sospechoso`
    : tipo === 'VEHICULO_ROBADO'
    ? `marker-pin vehiculo-robado`
    : tipo === 'SUCESO'
    ? `marker-pin suceso`
    : tipo === 'DESAPARECIDA'
    ? `marker-pin desaparecida`
    : `marker-pin ${claseTipo}`;

  const icono = iconoMaterial[tipo] || iconoMaterial[tipo.replace('_ROBADO', '')] || 'place';

  return divIcon({
    className: 'custom-marker',
    iconSize: [32, 42],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32],
    html: `<div class="${clase}">
      <span class="material-symbols-outlined">${icono}</span>
    </div>`,
  });
}

function AjustarVista({ puntos }: { puntos: PuntoMapa[] }) {
  const map = useMap();
  useEffect(() => {
    if (puntos.length === 0) return;
    if (puntos.length === 1) {
      map.setView([puntos[0].lat, puntos[0].lng], 14);
      return;
    }
    const bounds = new LatLngBounds(puntos.map((p) => [p.lat, p.lng]));
    map.fitBounds(bounds, { padding: [40, 40], maxZoom: 14 });
  }, [puntos, map]);
  return null;
}

export default function MapaTactical({
  puntos,
  altura = 380,
  centroDefault = [25.7617, -80.1918],
  zoomDefault = 12,
  hudLabel = 'Vista actual',
  hudValor = 'Sector metropolitano',
  emptyMessage = 'Sin datos georreferenciados',
}: Props) {
  const validos = puntos.filter(
    (p) => Number.isFinite(p.lat) && Number.isFinite(p.lng) && (p.lat !== 0 || p.lng !== 0)
  );

  return (
    <div className="map-card" style={{ height: altura, position: 'relative' }}>
      <div className="map-hud">
        <div className="map-hud-label">{hudLabel}</div>
        <div className="map-hud-value">{hudValor}</div>
      </div>
      <div className="map-stats">
        <span>PUNTOS: {validos.length}</span>
        <span>
          MODO: <span style={{ color: 'var(--red-500)' }}>SATELITAL</span>
        </span>
      </div>
      <MapContainer
        center={centroDefault}
        zoom={zoomDefault}
        style={{ height: '100%', width: '100%' }}
        scrollWheelZoom
      >
        <TileLayer
          attribution='&copy; OpenStreetMap'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <AjustarVista puntos={validos} />
        {validos.map((p) => (
          <Marker
            key={`${p.tipo}-${p.id}`}
            position={[p.lat, p.lng]}
            icon={crearIcono(p.tipo, p.sospechoso)}
          >
            <Popup>
              <div className="popup-title">{p.titulo}</div>
              {p.subtitulo && (
                <div style={{ fontSize: 10, color: 'var(--slate-400)', marginBottom: 6 }}>
                  {p.subtitulo}
                </div>
              )}
              {p.campos?.map((c, i) => (
                <div className="popup-row" key={i}>
                  <span>{c.etiqueta}</span>
                  <span>{c.valor}</span>
                </div>
              ))}
              <div className="popup-row" style={{ marginTop: 6, paddingTop: 6, borderTop: '1px solid var(--slate-800)' }}>
                <span>Coords</span>
                <span>{p.lat.toFixed(4)}, {p.lng.toFixed(4)}</span>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
      {validos.length === 0 && (
        <div className="map-empty">
          <span className="material-symbols-outlined">map</span>
          <div style={{ color: 'white', fontWeight: 700, fontSize: 14 }}>{emptyMessage}</div>
          <div style={{ color: 'var(--slate-500)', fontSize: 11, marginTop: 4 }}>
            Cargá ubicaciones con coordenadas para visualizarlas en el mapa
          </div>
        </div>
      )}
    </div>
  );
}