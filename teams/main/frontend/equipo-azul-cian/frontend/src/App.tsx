import React, { useState } from 'react';
import './App.css';
import Layout from './Layout';
import Inicio from './views/Inicio';
import Incidentes from './views/Incidentes';
import Patrullas from './views/Patrullas';
import Mapa from './views/Mapa';
import Asignaciones from './views/Asignaciones';
import { AuthProvider } from './shared/authContext';
import ProtectedRoute from './shared/ProtectedRoute';

const AppContent: React.FC = () => {
  const [currentView, setCurrentView] = useState<string>('inicio');

  const renderView = (): React.ReactNode => {
    switch (currentView) {
      case 'inicio':
        return <Inicio />;
      case 'incidentes':
        return (
          <ProtectedRoute allowedRoles={['SUPERVISOR', 'CENTRO_MANDO', 'OFICIAL_PATRULLA']}>
            <Incidentes />
          </ProtectedRoute>
        );
      case 'patrullas':
        return (
          <ProtectedRoute allowedRoles={['SUPERVISOR', 'CENTRO_MANDO']}>
            <Patrullas />
          </ProtectedRoute>
        );
      case 'mapa':
        return (
          <ProtectedRoute allowedRoles={['SUPERVISOR', 'CENTRO_MANDO', 'OFICIAL_PATRULLA']}>
            <Mapa />
          </ProtectedRoute>
        );
      case 'asignaciones':
        return (
          <ProtectedRoute allowedRoles={['SUPERVISOR', 'CENTRO_MANDO']}>
            <Asignaciones />
          </ProtectedRoute>
        );
      default:
        return <Inicio />;
    }
  };

  return (
    <Layout currentView={currentView} onViewChange={setCurrentView}>
      {renderView()}
    </Layout>
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;

