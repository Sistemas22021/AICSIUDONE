import React, { useState, useEffect } from 'react';
import type { Patrol, PatrolStatus } from '../types/patrol';
import MapView from '../components/map/MapView';
import { fetchWithRetry } from '../utils/fetchWithRetry';

interface NewPatrol {
  code: string;
  officerName: string;
  latitude: string;
  longitude: string;
  status: PatrolStatus;
}

const Patrullas: React.FC = () => {
  // Estado para las patrullas
  const [patrols, setPatrols] = useState<Patrol[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Estado para filtros
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  // Estado para el modal de registro
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [newPatrol, setNewPatrol] = useState<NewPatrol>({
    code: '',
    officerName: '',
    latitude: '',
    longitude: '',
    status: 'AVAILABLE'
  });

  // Cargar patrullas desde el backend
  const fetchPatrols = async (): Promise<void> => {
    setLoading(true);
    try {
      const response = await fetchWithRetry('http://localhost:8080/api/patrols');
      if (!response.ok) {
        throw new Error('Error al obtener la lista de patrullas');
      }
      const data: Patrol[] = await response.json();
      setPatrols(data);
      setError(null);
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'No se pudo conectar con el servidor de patrullas.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchPatrols();
  }, []);

  // Actualizar el estado de una patrulla con tipado estricto
  const handleStatusChange = async (
    id: number,
    newStatus: PatrolStatus
  ): Promise<void> => {
    try {
      const response = await fetch(`http://localhost:8080/api/patrols/${id}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }),
      });

      if (!response.ok) {
        throw new Error('Error al actualizar el estado de la patrulla');
      }

      const updatedPatrol: Patrol = await response.json();
      setPatrols(prevPatrols =>
        prevPatrols.map(patrol => (patrol.id === id ? updatedPatrol : patrol))
      );
    } catch (err: any) {
      alert(`Error: ${err.message}`);
    }
  };

  // Decidir las acciones operativas según el estado de la patrulla
  const renderPatrolAction = (patrol: Patrol): React.ReactNode => {
    if (patrol.status === 'AVAILABLE') {
      return (
        <button
          className="btn btn-secondary"
          onClick={() =>
            void handleStatusChange(
              patrol.id,
              'OUT_OF_SERVICE'
            )
          }
        >
          Poner fuera de servicio
        </button>
      );
    }

    if (patrol.status === 'OUT_OF_SERVICE') {
      return (
        <button
          className="btn btn-primary"
          onClick={() =>
            void handleStatusChange(
              patrol.id,
              'AVAILABLE'
            )
          }
        >
          Activar patrulla
        </button>
      );
    }

    if (patrol.status === 'EN_ROUTE') {
      return (
        <span
          style={{
            color: '#60a5fa',
            fontSize: '0.85rem'
          }}
        >
          En camino al incidente
        </span>
      );
    }

    if (patrol.status === 'BUSY') {
      return (
        <span
          style={{
            color: '#f59e0b',
            fontSize: '0.85rem'
          }}
        >
          Atendiendo incidente
        </span>
      );
    }

    return null;
  };

  // Seleccionar posición en el mapa
  const handleMapSelect = (lat: number, lng: number): void => {
    setNewPatrol((prev) => ({
      ...prev,
      latitude: lat.toString(),
      longitude: lng.toString()
    }));
  };

  // Crear una nueva patrulla
  const handleCreatePatrol = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    if (!newPatrol.code || !newPatrol.officerName || !newPatrol.latitude || !newPatrol.longitude) {
      alert('Por favor complete todos los campos requeridos.');
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await fetch('http://localhost:8080/api/patrols', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          code: newPatrol.code,
          officerName: newPatrol.officerName,
          latitude: parseFloat(newPatrol.latitude),
          longitude: parseFloat(newPatrol.longitude),
          status: newPatrol.status
        }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || 'Error al crear la patrulla. Asegúrese de que el código sea único.');
      }

      const createdPatrol: Patrol = await response.json();
      setPatrols(prev => [createdPatrol, ...prev]);
      setIsModalOpen(false);
      setNewPatrol({
        code: '',
        officerName: '',
        latitude: '',
        longitude: '',
        status: 'AVAILABLE'
      });
    } catch (err: any) {
      alert(`Error al registrar patrulla: ${err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  // Calcular métricas
  const totalPatrols = patrols.length;
  const availableCount = patrols.filter(p => p.status === 'AVAILABLE').length;
  const enRouteCount = patrols.filter(p => p.status === 'EN_ROUTE').length;
  const busyCount = patrols.filter(p => p.status === 'BUSY').length;
  const outOfServiceCount = patrols.filter(p => p.status === 'OUT_OF_SERVICE').length;

  // Filtrar la lista
  const filteredPatrols = patrols.filter(p => {
    const matchesSearch =
      p.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.officerName.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesStatus = statusFilter === 'ALL' || p.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  // Traducción y badges de estado
  const getStatusBadge = (status: PatrolStatus): React.ReactNode => {
    switch (status) {
      case 'AVAILABLE':
        return <span className="badge badge-green">Disponible</span>;
      case 'EN_ROUTE':
        return <span className="badge badge-blue">En Ruta</span>;
      case 'BUSY':
        return <span className="badge badge-orange">Ocupada</span>;
      case 'OUT_OF_SERVICE':
        return <span className="badge badge-red">Fuera de Servicio</span>;
      default:
        return <span className="badge">{status}</span>;
    }
  };

  return (
    <div style={styles.container}>
      {/* Cabecera */}
      <div style={styles.headerRow}>
        <div>
          <h1 style={styles.title}>Gestión de Patrullas</h1>
          <p style={styles.subtitle}>Supervisión y control de disponibilidad de unidades policiales</p>
        </div>
        <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
          + Registrar Patrulla
        </button>
      </div>

      {/* Tarjetas de Métricas */}
      <div style={styles.metricsGrid}>
        <div style={styles.metricCard}>
          <div style={{ ...styles.iconBox, backgroundColor: 'rgba(255, 255, 255, 0.05)' }}>🚔</div>
          <div>
            <div style={styles.metricLabel}>Total Unidades</div>
            <div style={styles.metricValue}>{totalPatrols}</div>
          </div>
        </div>
        <div style={{ ...styles.metricCard, borderLeft: '4px solid var(--color-green)' }}>
          <div style={{ ...styles.iconBox, backgroundColor: 'rgba(16, 185, 129, 0.1)' }}>🟢</div>
          <div>
            <div style={styles.metricLabel}>Disponibles</div>
            <div style={{ ...styles.metricValue, color: 'var(--color-green)' }}>{availableCount}</div>
          </div>
        </div>
        <div style={{ ...styles.metricCard, borderLeft: '4px solid var(--color-blue)' }}>
          <div style={{ ...styles.iconBox, backgroundColor: 'rgba(37, 99, 235, 0.1)' }}>🔵</div>
          <div>
            <div style={styles.metricLabel}>En Ruta</div>
            <div style={{ ...styles.metricValue, color: 'var(--color-blue)' }}>{enRouteCount}</div>
          </div>
        </div>
        <div style={{ ...styles.metricCard, borderLeft: '4px solid var(--color-orange)' }}>
          <div style={{ ...styles.iconBox, backgroundColor: 'rgba(245, 158, 11, 0.1)' }}>🟠</div>
          <div>
            <div style={styles.metricLabel}>Ocupadas</div>
            <div style={{ ...styles.metricValue, color: 'var(--color-orange)' }}>{busyCount}</div>
          </div>
        </div>
        <div style={{ ...styles.metricCard, borderLeft: '4px solid var(--color-red)' }}>
          <div style={{ ...styles.iconBox, backgroundColor: 'rgba(239, 68, 68, 0.1)' }}>🔴</div>
          <div>
            <div style={styles.metricLabel}>Fuera de Servicio</div>
            <div style={{ ...styles.metricValue, color: 'var(--color-red)' }}>{outOfServiceCount}</div>
          </div>
        </div>
      </div>

      {/* Filtros e Inputs */}
      <div style={styles.filterBar}>
        <div style={styles.searchContainer}>
          <span style={styles.searchIcon}>🔍</span>
          <input
            type="text"
            placeholder="Buscar por código o nombre del oficial..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchInput}
          />
        </div>
        <div style={styles.selectContainer}>
          <label style={styles.selectLabel}>Estado:</label>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={styles.selectInput}
          >
            <option value="ALL">Todos los Estados</option>
            <option value="AVAILABLE">Disponible</option>
            <option value="EN_ROUTE">En Ruta</option>
            <option value="BUSY">Ocupada</option>
            <option value="OUT_OF_SERVICE">Fuera de Servicio</option>
          </select>
        </div>
      </div>

      {/* Tabla de Resultados */}
      {loading ? (
        <div style={styles.loadingContainer}>
          <div style={styles.spinner}></div>
          <p style={{ marginTop: '16px', color: 'var(--text-muted)' }}>Cargando unidades policiales...</p>
        </div>
      ) : error ? (
        <div style={styles.errorContainer}>
          <div style={styles.errorIcon}>⚠️</div>
          <h3 style={{ marginBottom: '8px' }}>Error de Conexión</h3>
          <p style={{ color: 'var(--text-muted)' }}>{error}</p>
          <button className="btn btn-secondary" style={{ marginTop: '16px' }} onClick={() => void fetchPatrols()}>
            Reintentar Conexión
          </button>
        </div>
      ) : filteredPatrols.length === 0 ? (
        <div style={styles.emptyContainer}>
          <p style={{ color: 'var(--text-muted)' }}>No se encontraron patrullas que coincidan con la búsqueda.</p>
        </div>
      ) : (
        <div style={styles.tableWrapper}>
          <table className="custom-table">
            <thead>
              <tr>
                <th>Código Unidad</th>
                <th>Oficial a Cargo</th>
                <th>Coordenadas</th>
                <th>Estado Actual</th>
                <th>Último Reporte</th>
                <th style={{ textAlign: 'right' }}>Acción Operativa</th>
              </tr>
            </thead>
            <tbody>
              {filteredPatrols.map((patrol) => (
                <tr key={patrol.id}>
                  <td style={{ fontWeight: '600', color: '#ffffff' }}>
                    {patrol.code}
                  </td>
                  <td>{patrol.officerName}</td>
                  <td>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                      📍 {patrol.latitude.toFixed(4)}, {patrol.longitude.toFixed(4)}
                    </span>
                  </td>
                  <td>{getStatusBadge(patrol.status)}</td>
                  <td style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    {patrol.lastUpdated ? new Date(patrol.lastUpdated).toLocaleString() : 'Sin registro'}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    {renderPatrolAction(patrol)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal para Crear Patrulla */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '550px' }}>
            <div className="modal-header">
              <h2 style={{ color: '#ffffff' }}>Registrar Nueva Patrulla</h2>
              <button className="modal-close-btn" onClick={() => setIsModalOpen(false)}>×</button>
            </div>
            <form onSubmit={handleCreatePatrol}>
              <div style={styles.formGroup}>
                <label style={styles.formLabel}>Código de Unidad *</label>
                <input
                  type="text"
                  placeholder="Ej. P-105"
                  value={newPatrol.code}
                  onChange={(e) => setNewPatrol({ ...newPatrol, code: e.target.value })}
                  style={styles.formInput}
                  required
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.formLabel}>Nombre del Oficial a Cargo *</label>
                <input
                  type="text"
                  placeholder="Ej. Sgto. Carlos Mendez"
                  value={newPatrol.officerName}
                  onChange={(e) => setNewPatrol({ ...newPatrol, officerName: e.target.value })}
                  style={styles.formInput}
                  required
                />
              </div>

              {/* MINIMAPA PARA SELECCIÓN DE POSICIÓN */}
              <div style={styles.formGroup}>
                <label style={styles.formLabel}>Ubicación de la Patrulla *</label>
                <div
                  style={{
                    height: '250px',
                    marginTop: '4px',
                    borderRadius: '8px',
                    overflow: 'hidden',
                    border: '1px solid var(--border)'
                  }}
                >
                  <MapView
                    incidents={[]}
                    onSelectPosition={handleMapSelect}
                    selectedPosition={
                      newPatrol.latitude !== '' && newPatrol.longitude !== ''
                        ? [parseFloat(newPatrol.latitude), parseFloat(newPatrol.longitude)]
                        : null
                    }
                    selectionType="patrol"
                    height="250px"
                    showCounters={false}
                  />
                </div>
                {newPatrol.latitude && newPatrol.longitude ? (
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '8px', marginBottom: '0px' }}>
                    📍 Coordenadas seleccionadas: {parseFloat(newPatrol.latitude).toFixed(6)}, {parseFloat(newPatrol.longitude).toFixed(6)}
                  </p>
                ) : (
                  <p style={{ fontSize: '0.85rem', color: 'var(--color-orange)', marginTop: '8px', marginBottom: '0px' }}>
                    ⚠️ Haz clic en el mapa para marcar la ubicación de la patrulla.
                  </p>
                )}
              </div>

              <div style={styles.formGroup}>
                <label style={styles.formLabel}>Estado Inicial</label>
                <select
                  value={newPatrol.status}
                  onChange={(e) =>
                    setNewPatrol({
                      ...newPatrol,
                      status: e.target.value as PatrolStatus,
                    })
                  }
                  style={styles.formInput}
                >
                  <option value="AVAILABLE">Disponible</option>
                  <option value="OUT_OF_SERVICE">Fuera de Servicio</option>
                </select>
              </div>

              <div style={styles.modalFooter}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsModalOpen(false)}
                  disabled={isSubmitting}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Guardando...' : 'Registrar Patrulla'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

const styles: Record<string, React.CSSProperties> = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: '24px',
  },
  headerRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: '2rem',
    fontWeight: '600',
    color: '#ffffff',
    marginBottom: '4px',
  },
  subtitle: {
    color: 'var(--text-muted)',
    fontSize: '0.95rem',
  },
  metricsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '16px',
  },
  metricCard: {
    backgroundColor: 'var(--bg-card)',
    border: '1px solid var(--border)',
    borderRadius: '12px',
    padding: '20px',
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
  },
  iconBox: {
    width: '48px',
    height: '48px',
    borderRadius: '10px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '1.5rem',
  },
  metricLabel: {
    fontSize: '0.85rem',
    color: 'var(--text-muted)',
    fontWeight: '500',
    marginBottom: '4px',
  },
  metricValue: {
    fontSize: '1.75rem',
    fontWeight: '700',
    color: '#ffffff',
    lineHeight: '1',
  },
  filterBar: {
    display: 'flex',
    gap: '16px',
    backgroundColor: 'var(--bg-card)',
    border: '1px solid var(--border)',
    borderRadius: '12px',
    padding: '16px',
    alignItems: 'center',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
  },
  searchContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    backgroundColor: 'rgba(255, 255, 255, 0.03)',
    border: '1px solid var(--border)',
    borderRadius: '8px',
    padding: '8px 12px',
    flex: '1 1 300px',
  },
  searchIcon: {
    fontSize: '1rem',
    color: 'var(--text-muted)',
  },
  searchInput: {
    border: 'none',
    background: 'none',
    color: '#ffffff',
    fontSize: '0.95rem',
    outline: 'none',
    width: '100%',
  },
  selectContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  selectLabel: {
    color: 'var(--text-muted)',
    fontSize: '0.9rem',
  },
  selectInput: {
    backgroundColor: 'rgba(255, 255, 255, 0.03)',
    border: '1px solid var(--border)',
    borderRadius: '8px',
    padding: '8px 12px',
    color: '#ffffff',
    outline: 'none',
    fontSize: '0.95rem',
    cursor: 'pointer',
  },
  tableWrapper: {
    backgroundColor: 'var(--bg-card)',
    border: '1px solid var(--border)',
    borderRadius: '12px',
    overflow: 'hidden',
    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
  },
  loadingContainer: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '80px',
    backgroundColor: 'var(--bg-card)',
    border: '1px solid var(--border)',
    borderRadius: '12px',
  },
  spinner: {
    width: '40px',
    height: '40px',
    border: '3px solid rgba(255, 255, 255, 0.05)',
    borderTopColor: 'var(--color-blue)',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  },
  errorContainer: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '60px',
    backgroundColor: 'var(--bg-card)',
    border: '1px solid rgba(239, 68, 68, 0.2)',
    borderRadius: '12px',
    textAlign: 'center',
  },
  errorIcon: {
    fontSize: '3rem',
    marginBottom: '16px',
  },
  emptyContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '60px',
    backgroundColor: 'var(--bg-card)',
    border: '1px solid var(--border)',
    borderRadius: '12px',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
    marginBottom: '16px',
  },
  formLabel: {
    color: 'var(--text-muted)',
    fontSize: '0.88rem',
    fontWeight: '500',
  },
  formInput: {
    backgroundColor: 'rgba(255, 255, 255, 0.03)',
    border: '1px solid var(--border)',
    borderRadius: '8px',
    padding: '10px 14px',
    color: '#ffffff',
    outline: 'none',
    fontSize: '0.95rem',
  },
  row: {
    display: 'flex',
    gap: '16px',
  },
  modalFooter: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '12px',
    marginTop: '24px',
    borderTop: '1px solid var(--border)',
    paddingTop: '20px',
  },
};

export default Patrullas;
