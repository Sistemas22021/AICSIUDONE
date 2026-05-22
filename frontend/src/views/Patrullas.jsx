import React from 'react';

const Patrullas = () => {
  return (
    <div>
      <h1 style={{ marginBottom: '8px', fontSize: '2rem', fontWeight: 600 }}>Gestión de Patrullas</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Unidades policiales</p>
      <div style={{ padding: '40px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px' }}>
        <p>Cargando unidades policiales...</p>
      </div>
    </div>
  );
};

export default Patrullas;
