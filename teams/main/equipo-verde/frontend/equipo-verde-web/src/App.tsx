import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import { AuthGuard } from './guards/AuthGuard';
import { DashboardLayout } from './components/DashboardLayout';
import { RegistroPage } from './pages/RegistroPage';
import { ExpedientePage } from './pages/ExpedientePage';
import { CorrelacionPage } from './pages/CorrelacionPage';
import { AuditoriaPage } from './pages/AuditoriaPage';

const theme = createTheme({
  palette: {
    primary: {
      main: '#0f172a', // Slate 900
    },
    secondary: {
      main: '#64748b', // Slate 500
    },
    background: {
      default: '#f8fafc', // Slate 50
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 800,
    },
    h2: {
      fontWeight: 800,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
  },
});

function App() {
  return (
    <AuthGuard>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <DashboardLayout>
          <Routes>
            <Route path="/" element={<RegistroPage />} />
            <Route path="/expedientes" element={<ExpedientePage />} />
            <Route path="/correlacion" element={<CorrelacionPage />} />
            <Route path="/auditoria" element={<AuditoriaPage />} />
          </Routes>
        </DashboardLayout>
      </ThemeProvider>
    </AuthGuard>
  );
}

export default App;
