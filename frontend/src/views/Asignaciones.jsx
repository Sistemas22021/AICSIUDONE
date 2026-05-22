import React from 'react';

const Asignaciones = () => {
  return (
    <div>
      <h1 style={{ marginBottom: '8px', fontSize: '2rem', fontWeight: 600 }}>Asignaciones</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Control de despacho de patrullas</p>
      <div style={{ padding: '40px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px' }}>
        <p>Cargando despacho...</p>
      </div>
    </div>
  );
};

export default Asignaciones;
