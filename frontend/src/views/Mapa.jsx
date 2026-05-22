import React from 'react';

const Mapa = () => {
  return (
    <div>
      <h1 style={{ marginBottom: '8px', fontSize: '2rem', fontWeight: 600 }}>Mapa Operativo</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Distribución espacial en tiempo real</p>
      <div style={{ padding: '40px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px' }}>
        <p>Cargando mapa táctico...</p>
      </div>
    </div>
  );
};

export default Mapa;
