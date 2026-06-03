import React from 'react';

const Incidentes = () => {
  return (
    <div>
      <h1 style={{ marginBottom: '8px', fontSize: '2rem', fontWeight: 600 }}>Gestión de Incidentes</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Registro y seguimiento</p>
      <div style={{ padding: '40px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px' }}>
        <p>Cargando lista de incidentes...</p>
      </div>
    </div>
  );
};

export default Incidentes;
