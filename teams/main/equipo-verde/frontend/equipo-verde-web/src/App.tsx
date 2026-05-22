import { AuthGuard } from './guards/AuthGuard';
import { HomePage } from './components/HomePage';

function App() {
  return (
    <AuthGuard>
      <HomePage />
    </AuthGuard>
  );
}

export default App;
