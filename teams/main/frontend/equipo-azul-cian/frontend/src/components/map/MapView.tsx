import React from 'react';
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents
} from 'react-leaflet';
import L from 'leaflet';
import type { Incident } from '../../types/incident';
import type { Patrol } from '../../types/patrol';
import type { Assignment } from '../../types/assignment';

const incidentIcon = new L.Icon({
  iconUrl: 'https://maps.google.com/mapfiles/ms/icons/red-dot.png',
  iconSize: [32, 32]
});

const patrolIcon = new L.Icon({
  iconUrl: 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png',
  iconSize: [32, 32]
});

// Icono un poco más grande para simular el movimiento óptico de la patrulla en ruta
const movingPatrolIcon = new L.Icon({
  iconUrl: 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png',
  iconSize: [38, 38]
});

type OnSelectPosition = (lat: number, lng: number) => void;

interface Props {
  incidents?: Incident[];
  patrols?: Patrol[];
  assignments?: Assignment[];
  onSelectPosition?: OnSelectPosition;
  selectedPosition?: [number, number] | null;
  selectionType?: 'incident' | 'patrol';
  height?: string;
  showCounters?: boolean;
}

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
  patrols = [],
  assignments = [],
  onSelectPosition,
  selectedPosition,
  selectionType = 'incident',
  height = '600px',
  showCounters = true
}) => {
  // Estado local para almacenar las posiciones animadas/simuladas de las patrullas
  const [animatedPatrols, setAnimatedPatrols] = React.useState<Patrol[]>(patrols);

  // ✅ Problema 1: Sincronizar sin reiniciar la posición simulada
  React.useEffect(() => {
    setAnimatedPatrols(prev => {
      if (prev.length === 0) {
        return patrols;
      }
      return prev.map(oldPatrol => {
        const updated = patrols.find(p => p.id === oldPatrol.id);
        return updated ?? oldPatrol;
      });
    });

    const interval = setInterval(() => {
      setAnimatedPatrols(prev =>
        prev.map(patrol => {
          const status = patrol.status
            ? String(patrol.status).toLowerCase().trim()
            : '';

          if (
            status !== 'en_route' &&
            status !== 'en ruta' &&
            status !== 'en_ruta'
          ) {
            return patrol;
          }

          const assignment = assignments.find(
            a => a.patrol.id === patrol.id
          );

          if (!assignment) return patrol;

          const targetLat = Number(assignment.incident.latitude);
          const targetLng = Number(assignment.incident.longitude);

          // Importante usar la posición ya animada en vez de reiniciar desde las props base
          let lat = Number(patrol.latitude);
          let lng = Number(patrol.longitude);

          const step = 0.0003;

          if (Math.abs(targetLat - lat) > step) {
            lat += targetLat > lat ? step : -step;
          }

          if (Math.abs(targetLng - lng) > step) {
            lng += targetLng > lng ? step : -step;
          }

          // ✅ Problema 2: Detectar llegada exacta para detenerse
          const arrived =
            Math.abs(targetLat - lat) < step &&
            Math.abs(targetLng - lng) < step;

          if (arrived) {
            lat = targetLat;
            lng = targetLng;
          }

          return {
            ...patrol,
            latitude: lat,
            longitude: lng
          };
        })
      );
    }, 1000);

    return () => clearInterval(interval);
  }, [patrols, assignments]);

  // Filtros adaptados a los estados del Backend usando variables limpias
  const activosCount = incidents.filter(i => {
    const status = i.status ? String(i.status).toLowerCase().trim() : '';
    return status === 'active' || status === 'activo' || status === 'activa';
  }).length;

  const enProcesoCount = incidents.filter(i => {
    const status = i.status ? String(i.status).toLowerCase().trim() : '';
    return status === 'in_progress' || status === 'en proceso' || status === 'en progreso' || status === 'progreso';
  }).length;

  // Filtrar las patrullas reales que vienen por props
  const patrullasActivas = patrols.filter(p => {
    const status = p.status ? String(p.status).toLowerCase().trim() : '';
    return status !== 'out_of_service' 
      && status !== 'fuera de servicio' 
      && status !== 'fuera' 
      && status !== 'inactiva';
  });

  const patrullasCount = patrullasActivas.length;

  return (
    <div style={{ width: '100%', fontFamily: 'sans-serif' }}>
      
      {/* INYECCIÓN DE ANIMACIONES GLOBALES */}
      <style>{`
        @keyframes dot-pulse-animation {
          0% { opacity: 1; transform: scale(1); }
          50% { opacity: 0.3; transform: scale(0.85); }
          100% { opacity: 1; transform: scale(1); }
        }
        .pulsing-dot {
          animation: dot-pulse-animation 1.8s infinite ease-in-out;
          display: inline-block;
        }
        .custom-popup .leaflet-popup-content-wrapper {
          background: #111827 !important;
          color: #f3f4f6 !important;
          border-radius: 12px !important;
          padding: 4px;
          box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
        }
        .custom-popup .leaflet-popup-tip {
          background: #111827 !important;
        }
        .custom-popup .leaflet-popup-close-button {
          color: #9ca3af !important;
          padding: 8px !important;
        }
      `}</style>

      {/* MODIFICACIÓN 3: envolver únicamente la leyenda con showCounters */}
      {showCounters && (
        <>
          {/* BARRA SUPERIOR DE CONTADORES */}
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: '24px',
              alignItems: 'center',
              justifyContent: 'flex-start',
              fontSize: '0.95rem',
              fontWeight: 600,
              marginTop: '8px',
              marginBottom: '20px',
              paddingLeft: '4px'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#9ca3af' }}>
              <span className="pulsing-dot" style={{ width: '12px', height: '12px', borderRadius: '50%', background: '#ef4444', boxShadow: '0 0 6px #ef4444' }} />
              Incidentes Activos ({activosCount})
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#9ca3af' }}>
              <span className="pulsing-dot" style={{ width: '12px', height: '12px', borderRadius: '50%', background: '#f59e0b', boxShadow: '0 0 6px #f59e0b' }} />
              Incidentes en Proceso ({enProcesoCount})
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#9ca3af' }}>
              <span className="pulsing-dot" style={{ width: '12px', height: '12px', borderRadius: '50%', background: '#3b82f6', boxShadow: '0 0 6px #3b82f6' }} />
              Patrullas ({patrullasCount})
            </div>
          </div>
        </>
      )}

      {/* CONTENEDOR DEL MAPA */}
      <div
        style={{
          height,
          width: '100%',
          borderRadius: '12px',
          overflow: 'hidden',
          position: 'relative'
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

          <MapClickHandler onSelectPosition={onSelectPosition} />

          {/* MARCADORES DE INCIDENTES */}
          {incidents.map((incident) => {
            const lat = Number(incident.latitude);
            const lng = Number(incident.longitude);

            if (!Number.isFinite(lat) || !Number.isFinite(lng)) return null;

            const currentStatus = incident.status ? String(incident.status).toLowerCase().trim() : '';
            
            const isActive = currentStatus === 'active' || currentStatus === 'activo' || currentStatus === 'activa';
            const isInProgress = currentStatus === 'in_progress' || currentStatus === 'en proceso' || currentStatus === 'en progreso' || currentStatus === 'progreso';
            const isClosed = currentStatus === 'closed' || currentStatus === 'cerrado' || currentStatus === 'cerrada';

            if (isClosed) return null;

            let statusColor = '#ef4444'; 
            let statusLabel = 'Activo';

            if (isInProgress) {
              statusColor = '#f59e0b'; 
              statusLabel = 'En Proceso';
            } else if (!isActive && incident.status) {
              statusLabel = String(incident.status);
            }

            return (
              <Marker
                key={`incident-${incident.id}`}
                position={[lat, lng]}
                icon={incidentIcon}
              >
                <Popup className="custom-popup">
                  <div style={{ minWidth: '220px', color: '#f3f4f6' }}>
                    
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '10px' }}>
                      <div style={{
                        background: statusColor,
                        borderRadius: '50%',
                        width: '28px',
                        height: '28px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#fff',
                        fontWeight: 'bold',
                        fontSize: '14px'
                      }}>
                        !
                      </div>
                      <div>
                        <div style={{ fontWeight: 700, fontSize: '1rem', color: '#ffffff' }}>
                          {incident.id ? `INC-${String(incident.id).padStart(3, '0')}` : 'INC-001'}
                        </div>
                        <div style={{ fontSize: '0.8rem', color: '#9ca3af' }}>{incident.type || 'Incidente'}</div>
                      </div>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', fontSize: '0.85rem' }}>
                      <div>
                        <span style={{ color: '#9ca3af' }}>Descripción: </span>
                        <span style={{ fontWeight: 600 }}>{incident.description || 'Coordenadas en Mapa'}</span>
                      </div>
                      <div>
                        <span style={{ color: '#9ca3af' }}>Estado: </span>
                        <span style={{ fontWeight: 600, color: statusColor }}>
                          {statusLabel}
                        </span>
                      </div>
                    </div>

                  </div>
                </Popup>
              </Marker>
            );
          })}

          {/* MARCADORES DE PATRULLAS ANIMADAS */}
          {animatedPatrols.map((patrol) => {
            const patrolStatus = patrol.status ? String(patrol.status).toLowerCase().trim() : '';
            
            const isMoving =
              patrolStatus === 'en_route' ||
              patrolStatus === 'en ruta' ||
              patrolStatus === 'en_ruta';

            // ✅ Problema 3: Desplazamiento visual estratégico al llegar / estar ocupada
            const patrolPosition: [number, number] = [
              Number(patrol.latitude),
              Number(patrol.longitude)
            ];

            const isBusy =
              patrolStatus === 'busy' ||
              patrolStatus === 'ocupada' ||
              patrolStatus === 'en_proceso' || 
              patrolStatus === 'in_progress';

            const visualPosition: [number, number] = isBusy
              ? [
                  patrolPosition[0] + 0.00003,
                  patrolPosition[1] + 0.00003
                ]
              : patrolPosition;

            return (
              <Marker
                key={`patrol-${patrol.id}`}
                position={visualPosition}
                icon={isMoving ? movingPatrolIcon : patrolIcon}
              >
                <Popup className="custom-popup">
                  <div style={{ minWidth: '200px', color: '#f3f4f6' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
                      <div style={{ background: '#3b82f6', borderRadius: '50%', width: '24px', height: '24px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '12px', lineHeight: '1' }}>🛡️</div>
                      <strong style={{ fontSize: '0.95rem' }}>Patrulla {patrol.code}</strong>
                    </div>
                    <div style={{ fontSize: '0.85rem', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <div><span style={{ color: '#9ca3af' }}>Oficial:</span> {patrol.officerName}</div>
                      <div><span style={{ color: '#9ca3af' }}>Estado:</span> <span style={{ color: '#3b82f6', fontWeight: 600 }}>{patrol.status || 'Activa'}</span></div>
                    </div>
                  </div>
                </Popup>
              </Marker>
            );
          })}

          {/* MARCADOR SELECCIONADO INTERCAMBIABLE (ROJO O AZUL) */}
          {selectedPosition && (
            <Marker
              position={selectedPosition}
              icon={selectionType === 'patrol' ? patrolIcon : incidentIcon}
            />
          )}
        </MapContainer>
      </div>
    </div>
  );
};

export default MapView;