import React, { useState } from 'react';
import './App.css';
import Layout from './Layout';
import Inicio from './views/Inicio';
import Incidentes from './views/Incidentes';
import Patrullas from './views/Patrullas';
import Mapa from './views/Mapa';
import Asignaciones from './views/Asignaciones';

const App: React.FC = () => {
  const [currentView, setCurrentView] = useState<string>('inicio');

  const renderView = (): React.ReactNode => {
    switch (currentView) {
      case 'inicio':
        return <Inicio />;
      case 'incidentes':
        return <Incidentes />;
      case 'patrullas':
        return <Patrullas />;
      case 'mapa':
        return <Mapa />;
      case 'asignaciones':
        return <Asignaciones />;
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

export default App;
