import React from 'react';

const Inicio: React.FC = () => {
  return (
    <div>
      <h1 style={{ marginBottom: '8px', fontSize: '2rem', fontWeight: 600 }}>Panel de Control</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Resumen operativo en tiempo real</p>
      <div style={{ padding: '40px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px' }}>
        <p>Cargando información del panel...</p>
      </div>
    </div>
  );
};

export default Inicio;
