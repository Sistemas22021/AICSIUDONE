import React, { useState, useEffect } from 'react';
import './Asignaciones.css';
import { fetchWithRetry } from '../utils/fetchWithRetry';
import { API_BASE_URL } from '../config';

interface Incident {
  id: number;
  type: string;
  description: string;
  status: string;
}

interface Patrol {
  id: number;
  code: string;
  officerName: string;
  status: string;
}

interface Assignment {
  id: number;
  incident: Incident;
  patrol: Patrol;
}

const Asignaciones: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [patrols, setPatrols] = useState<Patrol[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [selectedIncident, setSelectedIncident] = useState<string>('');
  const [selectedPatrol, setSelectedPatrol] = useState<string>('');

  const fetchData = async (): Promise<void> => {
    try {
      setLoading(true);
      const [resIncidents, resPatrols, resAssignments] = await Promise.all([
        fetchWithRetry(`${API_BASE_URL}/incidents`),
        fetchWithRetry(`${API_BASE_URL}/patrols`),
        fetchWithRetry(`${API_BASE_URL}/assignments`)
      ]);

      if (!resIncidents.ok || !resPatrols.ok || !resAssignments.ok) {
        throw new Error('Error al obtener datos del servidor');
      }

      const dataIncidents: Incident[] = await resIncidents.json();
      const dataPatrols: Patrol[] = await resPatrols.json();
      const dataAssignments: Assignment[] = await resAssignments.json();

      // Filtrar incidentes que solo estén en estado ACTIVE y que no hayan sido asignados previamente
      const assignedIncidentIds = new Set(
        dataAssignments.map(a => a.incident.id)
      );
      
      const activeIncidents = dataIncidents.filter(i =>
        i.status === 'ACTIVE' &&
        !assignedIncidentIds.has(i.id)
      );
      
      const availablePatrols = dataPatrols.filter(
        p => p.status === 'AVAILABLE'
      );

      setIncidents(activeIncidents);
      setPatrols(availablePatrols);
      setAssignments(dataAssignments);
      
      // Corrección segura usando .at(0) para evitar errores de tipo 'undefined'
      const firstIncident = activeIncidents.at(0);
      const firstPatrol = availablePatrols.at(0);

      // Mejora para limpiar la selección si las listas quedan vacías
      setSelectedIncident(
        firstIncident ? firstIncident.id.toString() : ''
      );
      
      setSelectedPatrol(
        firstPatrol ? firstPatrol.id.toString() : ''
      );

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
    void fetchData();
  }, []);

  const handleAssign = async (): Promise<void> => {
    if (!selectedIncident || !selectedPatrol) return;
    
    try {
      const res = await fetch(`${API_BASE_URL}/assignments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Bypass-Tunnel-Reminder': 'true'
        },
        body: JSON.stringify({
          incidentId: parseInt(selectedIncident),
          patrolId: parseInt(selectedPatrol)
        })
      });

      if (!res.ok) {
        throw new Error('Error al asignar la patrulla');
      }

      await fetchData();
    } catch (err) {
      if (err instanceof Error) {
        alert(err.message);
      } else {
        alert('Error al asignar');
      }
    }
  };

  // =========================================
  // MARK ARRIVAL
  // =========================================
  const handleMarkArrival = async (patrolId: number): Promise<void> => {
    try {
      const res = await fetch(
        `${API_BASE_URL}/patrols/${patrolId}/arrive`,
        {
          method: 'PATCH',
          headers: {
            'Bypass-Tunnel-Reminder': 'true'
          }
        }
      );

      if (!res.ok) {
        throw new Error('No se pudo registrar la llegada de la patrulla');
      }

      // Refrescar datos para actualizar los estados en la UI
      await fetchData();
    } catch (err) {
      if (err instanceof Error) {
        alert(err.message);
      } else {
        alert('Error al registrar la llegada');
      }
    }
  };

  const formatIncidentId = (id: number): string => {
    return `INC-${id.toString().padStart(3, '0')}`;
  };

  return (
    <div className="asignaciones-container">
      <div className="asignaciones-section">
        <h2 className="section-title">Nueva Asignación</h2>
        
        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Cargando datos...</p>
        ) : error ? (
          <p style={{ color: 'var(--color-red)' }}>{error}</p>
        ) : (
          <div>
            <div className="form-group">
              <label className="form-label">Incidente</label>
              <select 
                className="form-select" 
                value={selectedIncident}
                onChange={(e) => setSelectedIncident(e.target.value)}
              >
                <option value="" disabled>Seleccione un incidente</option>
                {incidents.map(inc => (
                  <option key={inc.id} value={inc.id}>
                    {formatIncidentId(inc.id)} — {inc.type} ({inc.description})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Patrulla</label>
              <select 
                className="form-select" 
                value={selectedPatrol}
                onChange={(e) => setSelectedPatrol(e.target.value)}
              >
                <option value="" disabled>Seleccione una patrulla</option>
                {patrols.map(pat => (
                  <option key={pat.id} value={pat.id}>
                    {pat.code} — {pat.officerName}
                  </option>
                ))}
              </select>
            </div>

            <button 
              className="btn btn-primary btn-submit"
              onClick={() => void handleAssign()}
              disabled={!selectedIncident || !selectedPatrol}
            >
              Asignar Patrulla
            </button>
          </div>
        )}
      </div>

      <div className="asignaciones-section">
        <h2 className="section-title">Asignaciones Actuales</h2>
        
        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Cargando asignaciones...</p>
        ) : assignments.length === 0 ? (
          <div className="empty-state">No hay asignaciones actuales.</div>
        ) : (
          <div className="assignments-list">
            {assignments.map(assignment => (
              <div key={assignment.id} className="assignment-card">
                <div className="assignment-info">
                  <div className="assignment-title">
                    {formatIncidentId(assignment.incident.id)}
                    <span className="assignment-arrow">→</span>
                    {assignment.patrol.code}
                  </div>
                  <div className="assignment-details">
                    {assignment.incident.type} — {assignment.patrol.officerName}
                  </div>
                </div>
                <div className="assignment-status">
                  <span className="badge badge-orange">En Proceso</span>
                  
                  {/* Control inteligente del flujo del estado de la patrulla */}
                  {assignment.patrol.status === 'EN_ROUTE' ? (
                    <button
                      className="btn btn-primary"
                      style={{ marginTop: '8px', display: 'block', width: '100%' }}
                      onClick={() => void handleMarkArrival(assignment.patrol.id)}
                    >
                      Marcar llegada
                    </button>
                  ) : (
                    <span 
                      className="arrival-confirmed" 
                      style={{ marginTop: '8px', display: 'block', color: '#2ec4b6', fontSize: '0.9em', fontWeight: 'bold' }}
                    >
                      ✅ Llegó al incidente
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Asignaciones;
