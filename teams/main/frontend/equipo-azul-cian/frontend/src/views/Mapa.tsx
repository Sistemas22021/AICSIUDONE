import React, { useEffect, useState } from 'react';
import MapView from '../components/map/MapView';
import type { Incident } from '../types/incident';
import type { Patrol } from '../types/patrol';

const Mapa: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [patrols, setPatrols] = useState<Patrol[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);

      const [incidentsRes, patrolsRes] = await Promise.all([
        fetch('http://localhost:8080/api/incidents'),
        fetch('http://localhost:8080/api/patrols')
      ]);

      if (!incidentsRes.ok || !patrolsRes.ok) {
        throw new Error('Error al obtener datos del servidor');
      }

      const incidentsData: Incident[] = await incidentsRes.json();
      const patrolsData: Patrol[] = await patrolsRes.json();

      setIncidents(incidentsData);
      setPatrols(patrolsData);
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

      {!loading && (
        <MapView incidents={incidents} patrols={patrols} />
      )}
    </div>
  );
};

export default Mapa;