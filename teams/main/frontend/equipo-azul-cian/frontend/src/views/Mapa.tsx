import React, { useEffect, useState } from 'react';
import MapView from '../components/map/MapView';
import type { Incident } from '../types/incident';

const Mapa: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchIncidents = async () => {
    try {
      setLoading(true);

      const res = await fetch(
        'http://localhost:8080/api/incidents'
      );

      const data: Incident[] = await res.json();

      setIncidents(data);
      setError(null);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error al cargar incidentes');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchIncidents();
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
          Cargando incidentes...
        </div>
      )}

      {error && (
        <div style={{ marginBottom: 20, color: '#ef4444' }}>
          {error}
        </div>
      )}

      {!loading && (
        <MapView incidents={incidents} />
      )}
    </div>
  );
};

export default Mapa;