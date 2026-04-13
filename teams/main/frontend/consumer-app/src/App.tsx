import { AuthGuard } from './guards/AuthGuard';
import { WelcomePage } from './components/WelcomePage';

/**
 * App — Punto de entrada de la Consumer App.
 *
 * Toda la app está envuelta en AuthGuard, que verifica automáticamente
 * si hay una sesión activa. Si no hay sesión, redirige al Login MFE.
 */
function App() {
  return (
    <AuthGuard>
      <WelcomePage />
    </AuthGuard>
  );
}

export default App;
