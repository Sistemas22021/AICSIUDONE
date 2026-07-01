import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  alertaService, personaService, vehiculoService,
  sucesoService, engineService, ubicacionService,
} from '../services/api';
import type { Alerta } from '../types';
import { ModalConfiguracion } from '../components/Modales';
import { usePrefs } from '../services/PrefsContext';

export default function Dashboard() {
  const { t } = usePrefs();
  const nav = useNavigate();
  const [stats, setStats] = useState({
    personas: 0, vehiculos: 0, robados: 0,
    sucesos: 0, ubicaciones: 0, sospechosas: 0, alertas: 0,
  });
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  const [msg, setMsg] = useState('');
  const [cargando, setCargando] = useState(false);
  const [configAbierta, setConfigAbierta] = useState(false);
  const [ultimaEjecucion, setUltimaEjecucion] = useState<Date | null>(null);

  const cargar = async () => {
    try {
      const [pers, veh, robs, suc, ubis, al] = await Promise.all([
        personaService.listar().catch(() => []),
        vehiculoService.listar().catch(() => []),
        vehiculoService.listar('ROBADO').catch(() => []),
        sucesoService.listar().catch(() => []),
        ubicacionService.listar().catch(() => []),
        alertaService.listar(true).catch(() => []),
      ]);
      setStats({
        personas: pers.length,
        vehiculos: veh.length,
        robados: robs.length,
        sucesos: suc.length,
        ubicaciones: ubis.length,
        sospechosas: ubis.filter((u) => u.nodoSospechoso).length,
        alertas: al.length,
      });
      setAlertas(al.slice(0, 5));
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    cargar();
    const stored = localStorage.getItem('nexo_ultima_ejecucion');
    if (stored) setUltimaEjecucion(new Date(stored));
  }, []);

  const ejecutarMotor = async () => {
    setCargando(true);
    setMsg('');
    try {
      const r = await engineService.ejecutarTodo();
      const ahora = new Date();
      setUltimaEjecucion(ahora);
      localStorage.setItem('nexo_ultima_ejecucion', ahora.toISOString());
      setMsg(
        `${t('Motor ejecutado:')} ${r.totalVinculos} ${t('vínculos nuevos y')} ${r.totalAlertas} ${t('alertas generadas en')} ${r.reglasEjecutadas} ${t('reglas')}.`
      );
      await cargar();
    } catch {
      setMsg(t('Error al ejecutar el motor. Verificá que el backend esté corriendo.'));
    } finally {
      setCargando(false);
    }
  };

  const nivelAmenaza = stats.alertas > 5 ? 'ELEVADO' : stats.alertas > 0 ? 'MODERADO' : 'BAJO';

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">{t('Panel de Operaciones')}</h1>
          <p className="page-subtitle">
            {t('Unidad de Inteligencia: Vigilancia y Análisis 7-B')}
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">NODOS_ACTIVOS: {stats.personas + stats.vehiculos}</span>
          <span className="badge-pill alerta">NIVEL_AMENAZA: {nivelAmenaza}</span>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card" onClick={() => nav('/personas')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Personas registradas')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">group</span>
            </span>
          </div>
          <div className="stat-value">{stats.personas.toLocaleString('es-ES')}</div>
          <div className="stat-change muted">{t('Click para ver detalle')}</div>
        </div>

        <div className="stat-card" onClick={() => nav('/vehiculos')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Vehículos registrados')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">directions_car</span>
            </span>
          </div>
          <div className="stat-value">{stats.vehiculos.toLocaleString('es-ES')}</div>
          <div className="stat-change muted">{t('Registro verificado')}</div>
        </div>

        <div className="stat-card danger" onClick={() => nav('/vehiculos')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Vehículos robados')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">minor_crash</span>
            </span>
          </div>
          <div className="stat-value danger">{stats.robados}</div>
          <div className="stat-change negative">{t('Búsquedas activas')}</div>
        </div>

        <div className="stat-card" onClick={() => nav('/sucesos')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Sucesos registrados')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">analytics</span>
            </span>
          </div>
          <div className="stat-value">{stats.sucesos.toLocaleString('es-ES')}</div>
          <div className="stat-change muted">{t('Historial completo')}</div>
        </div>

        <div className="stat-card" onClick={() => nav('/ubicaciones')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Ubicaciones')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">distance</span>
            </span>
          </div>
          <div className="stat-value">{stats.ubicaciones}</div>
          <div className="stat-change muted">
            {stats.sospechosas} {t('sospechosas')}
          </div>
        </div>

        <div className="stat-card" onClick={() => nav('/alertas')} style={{ cursor: 'pointer' }}>
          <div className="stat-card-top">
            <span className="stat-label">{t('Alertas pendientes')}</span>
            <span className="stat-icon">
              <span className="material-symbols-outlined">notifications_active</span>
            </span>
          </div>
          <div className="stat-value danger">{stats.alertas}</div>
          <div className="stat-change negative">{t('Requieren revisión')}</div>
        </div>
      </div>

      <div className="engine-banner">
        <div className="engine-banner-content">
          <div className="engine-banner-title">
            <span className="material-symbols-outlined">hub</span>
            <h2>{t('Motor Red Thread')}</h2>
          </div>
          <p className="engine-banner-desc">
            {t(
              'Ejecuta un análisis avanzado cruzando personas, vehículos y ubicaciones para descubrir vínculos ocultos. El motor aplica 4 reglas heurísticas: nodo logístico, escolta vehicular, círculo de confianza y modus operandi.'
            )}
          </p>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            <button className="btn-primary" onClick={ejecutarMotor} disabled={cargando}>
              {cargando ? t('EJECUTANDO...') : t('EJECUTAR MOTOR COMPLETO')}
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>play_arrow</span>
            </button>
            <button className="btn-ghost" onClick={() => setConfigAbierta(true)}>
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>tune</span>
              {t('CONFIGURAR REGLAS')}
            </button>
            <button className="btn-ghost" onClick={() => nav('/grafo')}>
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>hub</span>
              {t('VER GRAFO')}
            </button>
          </div>
          {msg && (
            <div className={msg.includes('Error') ? 'error' : 'success'} style={{ marginTop: 14 }}>
              {msg}
            </div>
          )}
          {ultimaEjecucion && !msg && (
            <div style={{
              marginTop: 14, fontSize: 11, color: 'var(--slate-500)',
              fontFamily: 'var(--font-mono)', textTransform: 'uppercase', letterSpacing: '0.05em',
            }}>
              {t('Última ejecución')}: {ultimaEjecucion.toLocaleString('es-ES')}
            </div>
          )}
        </div>
        <div className="engine-visual">
          <div className="engine-visual-ring">
            <span className="material-symbols-outlined">psychology</span>
          </div>
          <div className="engine-visual-status">
            {cargando ? t('EN EJECUCIÓN') : t('EN REPOSO')}
          </div>
          <div style={{
            fontFamily: 'var(--font-mono)', fontSize: 9,
            color: 'var(--slate-600)', marginTop: 4,
          }}>
            v4.2.1-stable
          </div>
        </div>
      </div>

      <div className="table-wrap">
        <div className="table-header">
          <div className="table-header-title">
            <span className="material-symbols-outlined">notifications_active</span>
            <h3>{t('Últimas alertas pendientes')}</h3>
          </div>
          <button className="btn-ghost" onClick={() => nav('/alertas')}>
            {t('Ver todas')}
            <span className="material-symbols-outlined" style={{ fontSize: 14 }}>arrow_forward</span>
          </button>
        </div>
        <div style={{ padding: alertas.length === 0 ? 0 : 20 }}>
          {alertas.length === 0 ? (
            <div className="table-empty">
              <span className="material-symbols-outlined" style={{ fontSize: 48, color: 'var(--slate-700)' }}>
                check_circle
              </span>
              <div style={{ marginTop: 10, color: 'white', fontWeight: 700 }}>
                {t('No hay alertas pendientes')}
              </div>
              <div style={{ fontSize: 12, marginTop: 4 }}>
                {t('El sistema está limpio. Ejecutá el motor para detectar nuevas amenazas.')}
              </div>
            </div>
          ) : (
            alertas.map((a) => (
              <div
                key={a.id}
                className={`alerta ${a.nivelRiesgo.toLowerCase()}`}
                style={{ cursor: 'pointer' }}
                onClick={() => nav('/alertas')}
              >
                <div className="alerta-row">
                  <div className="alerta-body">
                    <div className="alerta-badges">
                      <span className="alerta-id">NX-{String(a.id).padStart(5, '0')}</span>
                      <span className="badge robado">{a.tipo}</span>
                      <span className="badge muted">{a.nivelRiesgo}</span>
                    </div>
                    <h3 className="alerta-titulo">{a.titulo}</h3>
                    <p className="alerta-desc">{a.descripcion}</p>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      <ModalConfiguracion abierto={configAbierta} onClose={() => setConfigAbierta(false)} />
    </>
  );
}