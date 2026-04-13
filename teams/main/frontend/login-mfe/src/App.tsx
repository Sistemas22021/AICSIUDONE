import { LoginForm } from './components/LoginForm';

/**
 * App — Punto de entrada del Login MFE.
 *
 * El Login MFE es una SPA de responsabilidad única:
 * 1. Recibe el parámetro ?redirect=<url> de la Consumer App
 * 2. Autentica al usuario
 * 3. Redirige de vuelta con el accessToken
 */
function App() {
  return <LoginForm />;
}

export default App;
