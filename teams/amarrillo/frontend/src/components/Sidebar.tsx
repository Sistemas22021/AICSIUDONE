import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/AuthContext';
import { usePrefs } from '../services/PrefsContext';

export default function Sidebar() {
  const { user, logout } = useAuth();
  const { t } = usePrefs();
  const nav = useNavigate();

  const link = ({ isActive }: { isActive: boolean }) =>
    isActive ? 'nav-link activo' : 'nav-link';

  const nuevaInvestigacion = () => nav('/sucesos?nueva=1');

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo">
          <span className="material-symbols-outlined">security</span>
        </div>
        <div>
          <div className="sidebar-title">Nexo Criminal</div>
          <div className="sidebar-subtitle">{t('Inteligencia de Precisión')}</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        {/* OPERACIÓN */}
        <div className="nav-group-label">{t('Operación')}</div>
        <NavLink to="/" end className={link}>
          <span className="material-symbols-outlined">dashboard</span>
          {t('Panel de Control')}
        </NavLink>

        {/* EVENTOS */}
        <div className="nav-group-label">{t('Eventos')}</div>
        <NavLink to="/sucesos" className={link}>
          <span className="material-symbols-outlined">event_note</span>
          {t('Sucesos')}
        </NavLink>
        <NavLink to="/desaparecidas" className={link}>
          <span className="material-symbols-outlined">person_search</span>
          {t('Personas Desaparecidas')}
        </NavLink>

        {/* ENTIDADES */}
        <div className="nav-group-label">{t('Entidades')}</div>
        <NavLink to="/personas" className={link}>
          <span className="material-symbols-outlined">group</span>
          {t('Personas')}
        </NavLink>
        <NavLink to="/vehiculos" className={link}>
          <span className="material-symbols-outlined">directions_car</span>
          {t('Vehículos')}
        </NavLink>
        <NavLink to="/ubicaciones" className={link}>
          <span className="material-symbols-outlined">distance</span>
          {t('Ubicaciones')}
        </NavLink>

        {/* INTELIGENCIA */}
        <div className="nav-group-label">{t('Inteligencia')}</div>
        <NavLink to="/alertas" className={link}>
          <span className="material-symbols-outlined">notifications_active</span>
          {t('Alertas')}
        </NavLink>
        <NavLink to="/grafo" className={link}>
          <span className="material-symbols-outlined">hub</span>
          {t('Grafo Red Thread')}
        </NavLink>
        <NavLink to="/asistente-ia" className={link}>
          <span className="material-symbols-outlined">psychology</span>
          {t('Asistente IA')}
        </NavLink>
        <NavLink to="/testimonios" className={link}>
          <span className="material-symbols-outlined">record_voice_over</span>
          {t('Testimonios')}
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <button className="btn-investigation" onClick={nuevaInvestigacion}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>add_circle</span>
          {t('Nueva Investigación')}
        </button>

        <div className="user-card">
          <div className="user-avatar">
            <span className="material-symbols-outlined">account_circle</span>
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="user-info-name">{user?.nombreCompleto || user?.username}</div>
            <div className="user-info-role">{user?.rol}</div>
          </div>
          <button className="btn-logout" onClick={logout} title={t('Cerrar sesión')}>
            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>logout</span>
          </button>
        </div>
      </div>
    </aside>
  );
}