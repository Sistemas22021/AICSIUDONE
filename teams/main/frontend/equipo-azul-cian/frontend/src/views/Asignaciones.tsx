import React, { useState, useEffect } from 'react';
import './Asignaciones.css';

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
        fetch('http://localhost:8080/api/incidents'),
        fetch('http://localhost:8080/api/patrols'),
        fetch('http://localhost:8080/api/assignments')
      ]);

      if (!resIncidents.ok || !resPatrols.ok || !resAssignments.ok) {
        throw new Error('Error al obtener datos del servidor');
      }

      const dataIncidents: Incident[] = await resIncidents.json();
      const dataPatrols: Patrol[] = await resPatrols.json();
      const dataAssignments: Assignment[] = await resAssignments.json();

      const activeIncidents = dataIncidents.filter(i => i.status !== 'CLOSED');
      const availablePatrols = dataPatrols.filter(p => p.status === 'AVAILABLE');

      setIncidents(activeIncidents);
      setPatrols(availablePatrols);
      setAssignments(dataAssignments);
      
      if (activeIncidents.length > 0) setSelectedIncident(activeIncidents[0].id.toString());
      if (availablePatrols.length > 0) setSelectedPatrol(availablePatrols[0].id.toString());

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
      const res = await fetch('http://localhost:8080/api/assignments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
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
