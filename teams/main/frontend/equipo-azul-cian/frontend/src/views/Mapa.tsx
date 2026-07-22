import React, { useEffect, useState } from 'react';
import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';
import type { Assignment } from '../types/assignment';

import { createMapAdapter } from '../adapters/MapAdapterFactory';
import { API_BASE_URL } from '../config';

const Mapa: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [patrols, setPatrols] = useState<Patrol[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);

      const [incidentsRes, patrolsRes, assignmentsRes] = await Promise.all([
        fetch(`${API_BASE_URL}/incidents`),
        fetch(`${API_BASE_URL}/patrols`),
        fetch(`${API_BASE_URL}/assignments`)
      ]);

      if (!incidentsRes.ok || !patrolsRes.ok || !assignmentsRes.ok) {
        throw new Error('Error al obtener datos del servidor');
      }

      const incidentsData: Incident[] = await incidentsRes.json();
      const patrolsData: Patrol[] = await patrolsRes.json();
      const assignmentsData: Assignment[] = await assignmentsRes.json();

      setIncidents(incidentsData);
      setPatrols(patrolsData);
      setAssignments(assignmentsData);
      setError(null);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error al cargar datos del mapa');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchData();
  }, []);

  // ==========================
  // ADAPTER DINÁMICO (.env)
  // ==========================
  const mapAdapter = createMapAdapter();

  return (
    <div>
      <h1 style={{ marginBottom: 8, fontSize: '2rem', fontWeight: 600 }}>
        Mapa Operativo
      </h1>

      <p style={{ color: 'var(--text-muted)', marginBottom: 24 }}>
        Centro de Monitoreo en Tiempo Real
      </p>

      {loading && (
        <div style={{ marginBottom: 20, color: '#94a3b8' }}>
          Cargando datos...
        </div>
      )}

      {error && (
        <div style={{ marginBottom: 20, color: '#ef4444' }}>
          {error}
        </div>
      )}

      {!loading &&
        mapAdapter.render({
          incidents,
          patrols,
          assignments
        })}
    </div>
  );
};

export default Mapa;