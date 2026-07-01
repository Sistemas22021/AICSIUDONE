import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './services/AuthContext';
import { PrefsProvider } from './services/PrefsContext';
import { ToastProvider } from './services/ToastContext';
import { ConfirmProvider } from './services/ConfirmContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Personas from './pages/Personas';
import Vehiculos from './pages/Vehiculos';
import Ubicaciones from './pages/Ubicaciones';
import Sucesos from './pages/Sucesos';
import Alertas from './pages/Alertas';
import Desaparecidas from './pages/Desaparecidas';
import AsistenteIA from './pages/AsistenteIA';
import Grafo from './pages/Grafo';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import FooterStatus from './components/FooterStatus';


function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <TopBar />
        <div className="page">{children}</div>
        <FooterStatus />
      </div>
    </div>
  );
}

function ProtectedRoutes() {
  const { user, cargando } = useAuth();

  // Mientras se resuelve la sesion (importante en SSO: refresh asincrono),
  // no decidir todavia para evitar parpadeo a /login.
  if (cargando) {
    return (
      <div className="login-page">
        <div className="login-box" style={{ textAlign: 'center' }}>
          <div className="login-title">Nexo Criminal</div>
          <p className="login-desc" style={{ marginTop: 12 }}>Verificando sesión...</p>
        </div>
      </div>
    );
  }

  if (!user) return <Navigate to="/login" replace />;
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/personas" element={<Personas />} />
        <Route path="/vehiculos" element={<Vehiculos />} />
        <Route path="/ubicaciones" element={<Ubicaciones />} />
        <Route path="/sucesos" element={<Sucesos />} />
        <Route path="/alertas" element={<Alertas />} />
        <Route path="/desaparecidas" element={<Desaparecidas />} />
        <Route path="/asistente-ia" element={<AsistenteIA />} />
        <Route path="/grafo" element={<Grafo />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <PrefsProvider>
        <ToastProvider>
          <ConfirmProvider>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/*" element={<ProtectedRoutes />} />
            </Routes>
          </ConfirmProvider>
        </ToastProvider>
      </PrefsProvider>
    </AuthProvider>
  );
}
