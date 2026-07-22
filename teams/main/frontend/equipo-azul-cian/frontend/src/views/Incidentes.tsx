import React, { useState, useEffect } from 'react';
import IncidentsModal from '../components/incident/IncidentModal';
import { fetchWithRetry } from '../utils/fetchWithRetry';
import { API_BASE_URL } from '../config';

type IncidentStatus = 'ACTIVE' | 'IN_PROGRESS' | 'CLOSED';
type IncidentFilterStatus = 'ALL' | 'ACTIVE' | 'IN_PROGRESS' | 'CLOSED';

interface Incident {
  id: number;
  type: string;
  description: string;
  latitude: number;
  longitude: number;
  status: IncidentStatus;
  priority: string;
  createdAt: string;
  updatedAt?: string;
  closedAt?: string;
  patrolCode?: string;
  patrolOfficerName?: string;
}

const Incidentes: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  
  // Estado para los mensajes de notificación
  const [message, setMessage] = useState<{
    type: 'success' | 'error';
    text: string;
  } | null>(null);

  const [selectedIncident, setSelectedIncident] =
    useState<Incident | null>(null);

  const [isModalOpen, setIsModalOpen] =
    useState<boolean>(false);

  const [filter, setFilter] = useState<IncidentFilterStatus>('ALL');

  const fetchIncidents = async (): Promise<void> => {
    try {
      setLoading(true);

      const res = await fetchWithRetry(
        `${API_BASE_URL}/incidents`
      );

      const data: Incident[] = await res.json();

      setIncidents(data);
      setError(null);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error desconocido');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchIncidents();
  }, []);

  const handleCreate = (newIncident: Incident): void => {
    setIncidents((prev) => [
      newIncident,
      ...prev
    ]);

    setIsModalOpen(false);
  };

  const handleStatusUpdate = async (
    id: number,
    status: IncidentStatus
  ): Promise<void> => {
    try {
      let res: Response;

      if (status === 'CLOSED') {
        res = await fetch(
          `${API_BASE_URL}/incidents/${id}/close`,
          { method: 'PATCH' }
        );
      } else {
        res = await fetch(
          `${API_BASE_URL}/incidents/${id}/status`,
          {
            method: 'PATCH',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status })
          }
        );
      }

      // Validación robusta de la respuesta del backend
      if (!res.ok) {
        let errorMessage = 'No se puede cerrar un incidente mientras la patrulla sigue en ruta.';

        try {
          const errorData = await res.json();
          errorMessage = errorData.message || errorMessage;
        } catch {
          // Si no es un JSON válido, conserva la cadena por defecto
        }

        throw new Error(errorMessage);
      }

      try {
        await res.json();
      } catch {
        // Ignorar si el cuerpo exitoso viene vacío
      }

      // Sincronizar tabla
      await fetchIncidents();

      // Notificación exitosa
      setMessage({
        type: 'success',
        text: 'Estado del incidente actualizado correctamente.'
      });
      setTimeout(() => {
        setMessage(null);
      }, 3000);

      setIncidents((currentIncidents) => {
        const updated = currentIncidents.find((incident) => incident.id === id);
        if (updated) {
          setSelectedIncident(updated);
        }
        return currentIncidents;
      });
      
    } catch (err) {
      // Captura el mensaje lanzado (ej: "No se puede cerrar un incidente mientras la patrulla sigue en ruta")
      if (err instanceof Error) {
        setMessage({
          type: 'error',
          text: err.message
        });
      } else {
        setMessage({
          type: 'error',
          text: 'No se pudo actualizar el estado del incidente.'
        });
      }

      console.error('Error al actualizar el estado:', err);

      setTimeout(() => {
        setMessage(null);
      }, 4000);
    }
  };

  const filteredIncidents =
    filter === 'ALL'
      ? incidents
      : incidents.filter((i) => i.status === filter);

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: 20
      }}
    >
      {/* HEADER */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}
      >
        <div>
          <h1
            style={{
              margin: 0,
              color: '#fff',
              fontSize: '2rem'
            }}
          >
            Gestión de Incidentes
          </h1>

          <p
            style={{
              color: '#94a3b8',
              marginTop: 6
            }}
          >
            Registro y seguimiento operativo
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          style={{
            background: '#dc2626',
            color: '#fff',
            border: 'none',
            padding: '12px 18px',
            borderRadius: 8,
            cursor: 'pointer',
            fontWeight: 600
          }}
        >
          + Registrar Incidente
        </button>
      </div>

      {/* CONTROLES DE FILTRADO */}
      <div style={{ display: 'flex', gap: 10 }}>
        {([ 'ALL', 'ACTIVE', 'IN_PROGRESS', 'CLOSED' ] as IncidentFilterStatus[]).map((status) => (
          <button
            key={status}
            onClick={() => setFilter(status)}
            style={{
              padding: '8px 12px',
              borderRadius: 8,
              border: '1px solid #1f2937',
              cursor: 'pointer',
              background: filter === status ? '#dc2626' : '#111827',
              color: '#fff',
              fontWeight: filter === status ? 600 : 400
            }}
          >
            {status === 'ALL'
              ? 'Todos'
              : status === 'ACTIVE'
              ? 'Activos'
              : status === 'IN_PROGRESS'
              ? 'En Proceso'
              : 'Cerrados'}
          </button>
        ))}
      </div>

      {/* Alerta integrada con prefijo de emoji dinámico */}
      {message && (
        <div
          style={{
            background:
              message.type === 'success'
                ? 'rgba(16,185,129,.15)'
                : 'rgba(239,68,68,.15)',
            color:
              message.type === 'success'
                ? '#10b981'
                : '#ef4444',
            border: `1px solid ${
              message.type === 'success'
                ? '#10b981'
                : '#ef4444'
            }`,
            padding: '12px 16px',
            borderRadius: 8,
            fontWeight: 600
          }}
        >
          {message.type === 'success' ? '🟢 ' : '🔴 '}
          {message.text}
        </div>
      )}

      <div
        style={{
          display: 'flex',
          gap: 20
        }}
      >
        {/* TABLA */}
        <div
          style={{
            flex: selectedIncident ? 2 : 3,
            background: '#111827',
            borderRadius: 12,
            border: '1px solid #1f2937',
            overflow: 'hidden'
          }}
        >
          {loading && (
            <div style={{ padding: 30 }}>
              Cargando incidentes...
            </div>
          )}

          {error && (
            <div
              style={{
                padding: 30,
                color: '#ef4444'
              }}
            >
              {error}
            </div>
          )}

          {!loading && !error && (
            <table
              style={{
                width: '100%',
                borderCollapse: 'collapse'
              }}
            >
              <thead>
                <tr
                  style={{
                    background: '#0f172a'
                  }}
                >
                  <th style={thStyle}>ID</th>
                  <th style={thStyle}>Tipo</th>
                  <th style={thStyle}>Ubicación</th>
                  <th style={thStyle}>Fecha</th>
                  <th style={thStyle}>Estado</th>
                </tr>
              </thead>

              <tbody>
                {filteredIncidents.map((i) => (
                  <tr
                    key={i.id}
                    onClick={() => {
                      setSelectedIncident(i);
                    }}
                    style={{
                      cursor: 'pointer',
                      borderBottom:
                        '1px solid #1f2937',
                      background:
                        selectedIncident?.id === i.id
                          ? '#1e293b'
                          : 'transparent'
                    }}
                  >
                    <td style={tdStyle}>
                      #{i.id}
                    </td>

                    <td style={tdStyle}>
                      {i.type}
                    </td>

                    <td style={tdStyle}>
                      {i.latitude.toFixed(4)}, {i.longitude.toFixed(4)}
                    </td>

                    <td style={tdStyle}>
                      {new Date(i.createdAt || '').toLocaleString()}
                    </td>

                    <td style={tdStyle}>
                      <StatusBadge
                        status={i.status}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* PANEL DETALLE */}
        {selectedIncident && (
          <div
            style={{
              flex: 1,
              background: '#111827',
              borderRadius: 12,
              border: '1px solid #1f2937',
              padding: 20,
              minWidth: 320
            }}
          >
            <div
              style={{
                display: 'flex',
                justifyContent:
                  'space-between',
                alignItems: 'center'
              }}
            >
              <h2
                style={{
                  margin: 0,
                  color: '#fff'
                }}
              >
                Incidente #
                {selectedIncident.id}
              </h2>

              <button
                onClick={() =>
                  setSelectedIncident(null)
                }
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: '#94a3b8',
                  fontSize: 24,
                  cursor: 'pointer'
                }}
              >
                ×
              </button>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                ESTADO
              </span>

              <div style={{ marginTop: 8 }}>
                <StatusBadge
                  status={
                    selectedIncident.status
                  }
                />
              </div>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                TIPO
              </span>

              <p style={textStyle}>
                {selectedIncident.type}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                PRIORIDAD
              </span>

              <p style={textStyle}>
                {translatePriority(selectedIncident.priority)}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                PATRULLA ASIGNADA
              </span>

              <p style={textStyle}>
                {selectedIncident.patrolCode
                  ? `${selectedIncident.patrolCode} - ${selectedIncident.patrolOfficerName}`
                  : "Sin patrulla asignada"}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                DESCRIPCIÓN
              </span>

              <p style={textStyle}>
                {selectedIncident.description}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                FECHA DE CREACIÓN
              </span>

              <p style={textStyle}>
                {selectedIncident.createdAt
                  ? new Date(selectedIncident.createdAt).toLocaleString()
                  : 'Sin fecha de creación'}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                ÚLTIMA ACTUALIZACIÓN
              </span>

              <p style={textStyle}>
                {selectedIncident.updatedAt
                  ? new Date(selectedIncident.updatedAt).toLocaleString()
                  : 'Sin actualizaciones'}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                FECHA DE CIERRE
              </span>

              <p style={textStyle}>
                {selectedIncident.closedAt
                  ? new Date(selectedIncident.closedAt).toLocaleString()
                  : 'Sin fecha de cierre'}
              </p>
            </div>

            <div style={{ marginTop: 20 }}>
              <span style={labelStyle}>
                COORDENADAS
              </span>

              <div
                style={{
                  display: 'flex',
                  gap: 10,
                  marginTop: 10
                }}
              >
                <div style={coordBox}>
                  Lat:{' '}
                  {selectedIncident.latitude.toFixed(
                    4
                  )}
                </div>

                <div style={coordBox}>
                  Lng:{' '}
                  {selectedIncident.longitude.toFixed(
                    4
                  )}
                </div>
              </div>
            </div>

            <div
              style={{
                display: 'flex',
                gap: 10,
                marginTop: 24
              }}
            >
              {selectedIncident.status === 'IN_PROGRESS' && (
                <button
                  style={closeBtn}
                  onClick={() =>
                    void handleStatusUpdate(
                      selectedIncident.id,
                      'CLOSED'
                    )
                  }
                >
                  Cerrar
                </button>
              )}
            </div>
          </div>
        )}
      </div>

      {isModalOpen && (
        <IncidentsModal
          onClose={() =>
            setIsModalOpen(false)
          }
          onCreated={handleCreate}
        />
      )}
    </div>
  );
};

const thStyle: React.CSSProperties = {
  padding: 14,
  textAlign: 'left',
  color: '#94a3b8'
};

const tdStyle: React.CSSProperties = {
  padding: 14,
  color: '#e2e8f0'
};

const labelStyle: React.CSSProperties = {
  color: '#94a3b8',
  fontSize: 12,
  fontWeight: 600
};

const textStyle: React.CSSProperties = {
  color: '#fff'
};

const coordBox: React.CSSProperties = {
  flex: 1,
  background: '#1e293b',
  padding: 12,
  borderRadius: 8,
  color: '#38bdf8'
};

const closeBtn: React.CSSProperties = {
  flex: 1,
  padding: 10,
  borderRadius: 8,
  border: '1px solid #10b981',
  background: 'transparent',
  color: '#10b981',
  cursor: 'pointer'
};

interface StatusBadgeProps {
  status: IncidentStatus;
}

const StatusBadge: React.FC<
  StatusBadgeProps
> = ({ status }) => {
  const config = {
    ACTIVE: {
      text: 'Activo',
      color: '#ef4444',
      bg: 'rgba(239,68,68,.15)'
    },
    IN_PROGRESS: {
      text: 'En Proceso',
      color: '#f59e0b',
      bg: 'rgba(245,158,11,.15)'
    },
    CLOSED: {
      text: 'Cerrado',
      color: '#10b981',
      bg: 'rgba(16,185,129,.15)'
    }
  } as const;

  const item = config[status];

  return (
    <span
      style={{
        background: item.bg,
        color: item.color,
        padding: '4px 10px',
        borderRadius: 6,
        fontSize: 12,
        fontWeight: 600
      }}
    >
      {item.text}
    </span>
  );
};

const translatePriority = (priority: string): string => {
  const priorities: Record<string, string> = {
    LOW: 'Baja',
    MEDIUM: 'Media',
    HIGH: 'Alta'
  };

  return priorities[priority] || priority;
};

export default Incidentes;