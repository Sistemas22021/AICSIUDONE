import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import { useAuth } from '../services/AuthContext';
import { esSSO } from '../services/authMode';
import { redirigirALogin } from '../services/ssoAuth';

export default function Login() {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [err, setErr] = useState('');
  const [cargando, setCargando] = useState(false);
  const { login } = useAuth();
  const nav = useNavigate();

  // En modo SSO no hay formulario local: se redirige al login del profesor.
  useEffect(() => {
    if (esSSO()) {
      // Pequeño delay para que se vea el mensaje antes de redirigir
      const t = setTimeout(() => redirigirALogin(), 800);
      return () => clearTimeout(t);
    }
  }, []);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErr('');
    setCargando(true);
    try {
      const sesion = await authService.login(username, password);
      login(sesion);
      nav('/');
    } catch {
      setErr('Credenciales inválidas. Verificá usuario y contraseña.');
    } finally {
      setCargando(false);
    }
  };

  // ---- Vista SSO: redireccion al acceso centralizado ----
  if (esSSO()) {
    return (
      <div className="login-page">
        <div className="login-box" style={{ textAlign: 'center' }}>
          <div className="login-header" style={{ justifyContent: 'center' }}>
            <div className="login-logo">
              <span className="material-symbols-outlined">security</span>
            </div>
            <div>
              <div className="login-title">Nexo Criminal</div>
              <div className="login-subtitle">Acceso centralizado (SSO)</div>
            </div>
          </div>
          <p className="login-desc" style={{ marginTop: 16 }}>
            Redirigiendo al sistema de autenticación centralizada...
          </p>
          <button className="btn-primary" style={{ width: '100%', marginTop: 10 }}
            onClick={() => redirigirALogin()}>
            Ir al acceso ahora
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>arrow_forward</span>
          </button>
        </div>
      </div>
    );
  }

  // ---- Vista local: formulario propio de Nexo Criminal (sin cambios) ----
  return (
    <div className="login-page">
      <form className="login-box" onSubmit={onSubmit}>
        <div className="login-header">
          <div className="login-logo">
            <span className="material-symbols-outlined">security</span>
          </div>
          <div>
            <div className="login-title">Nexo Criminal</div>
            <div className="login-subtitle">Precision Intelligence</div>
          </div>
        </div>

        <p className="login-desc">
          Acceso restringido. Autenticación requerida para ingresar al sistema
          de inteligencia de vínculos.
        </p>

        <div className="form-group" style={{ marginBottom: 14 }}>
          <label className="form-label">Agent ID</label>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            placeholder="admin2"
          />
        </div>

        <div className="form-group" style={{ marginBottom: 14 }}>
          <label className="form-label">Access Key</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
        </div>

        {err && <div className="error">{err}</div>}

        <button
          type="submit"
          className="btn-primary"
          style={{ width: '100%', marginTop: 10 }}
          disabled={cargando}
        >
          {cargando ? 'AUTHENTICATING...' : 'AUTHENTICATE'}
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            arrow_forward
          </span>
        </button>

        <div className="login-hint">
          DEMO ACCESS: <strong>admin</strong> / <strong>admin123</strong>
        </div>
      </form>
    </div>
  );
}
