import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  personaService, vehiculoService, ubicacionService, sucesoService,
} from '../services/api';
import { useAuth } from '../services/AuthContext';
import { usePrefs } from '../services/PrefsContext';
import {
  ModalConfiguracion, ModalManual, ModalTutorial, ModalAPI,
  ModalPerfil, ModalCambiarPassword,
} from './Modales';

interface Resultado {
  tipo: 'persona' | 'vehiculo' | 'ubicacion' | 'suceso';
  id: number;
  titulo: string;
  subtitulo: string;
}

type ModalAbierto =
  | null | 'configuracion' | 'manual' | 'tutorial'
  | 'api' | 'perfil' | 'password';

type TabConfig = 'general' | 'motor';

export default function TopBar() {
  const { t } = usePrefs();
  const [query, setQuery] = useState('');
  const [resultados, setResultados] = useState<Resultado[]>([]);
  const [mostrando, setMostrando] = useState(false);
  const [menu, setMenu] = useState<string | null>(null);
  const [modal, setModal] = useState<ModalAbierto>(null);
  const [pestañaConfig, setPestañaConfig] = useState<TabConfig>('general');

  const nav = useNavigate();
  const { user, logout } = useAuth();
  const boxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (!boxRef.current?.contains(e.target as Node)) {
        setMostrando(false);
        setMenu(null);
      }
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  useEffect(() => {
    if (!query.trim()) {
      setResultados([]);
      return;
    }
    const id = setTimeout(async () => {
      try {
        const [pers, veh, ubis, suc] = await Promise.all([
          personaService.listar().catch(() => []),
          vehiculoService.listar().catch(() => []),
          ubicacionService.listar().catch(() => []),
          sucesoService.listar().catch(() => []),
        ]);
        const q = query.toLowerCase();
        const res: Resultado[] = [];

        pers.filter(p =>
          p.nombre.toLowerCase().includes(q) ||
          p.apellido.toLowerCase().includes(q) ||
          p.documento.toLowerCase().includes(q) ||
          p.alias?.toLowerCase().includes(q)
        ).slice(0, 4).forEach(p => res.push({
          tipo: 'persona', id: p.id!,
          titulo: `${p.nombre} ${p.apellido}`,
          subtitulo: `${p.rol} · ${p.documento}`,
        }));

        veh.filter(v =>
          v.placa.toLowerCase().includes(q) ||
          v.marca.toLowerCase().includes(q) ||
          v.modelo.toLowerCase().includes(q)
        ).slice(0, 4).forEach(v => res.push({
          tipo: 'vehiculo', id: v.id!,
          titulo: v.placa,
          subtitulo: `${v.marca} ${v.modelo} · ${v.estado}`,
        }));

        ubis.filter(u => u.direccion?.toLowerCase().includes(q))
          .slice(0, 4).forEach(u => res.push({
            tipo: 'ubicacion', id: u.id!,
            titulo: u.direccion || `Ubicación #${u.id}`,
            subtitulo: u.tipo || 'OTRO',
          }));

        suc.filter(s =>
          s.tipo.toLowerCase().includes(q) ||
          s.modusOperandi?.toLowerCase().includes(q) ||
          s.descripcion?.toLowerCase().includes(q)
        ).slice(0, 4).forEach(s => res.push({
          tipo: 'suceso', id: s.id!,
          titulo: `${s.tipo} #${s.id}`,
          subtitulo: s.modusOperandi || new Date(s.fechaHora).toLocaleDateString('es-ES'),
        }));

        setResultados(res);
      } catch (e) { console.error(e); }
    }, 300);
    return () => clearTimeout(id);
  }, [query]);

  const seleccionar = (r: Resultado) => {
    setQuery('');
    setMostrando(false);
    const rutas: Record<string, string> = {
      persona: '/personas', vehiculo: '/vehiculos',
      ubicacion: '/ubicaciones', suceso: '/sucesos',
    };
    nav(rutas[r.tipo]);
  };

  const iconoTipo = (tp: string) => {
    if (tp === 'persona') return 'person';
    if (tp === 'vehiculo') return 'directions_car';
    if (tp === 'ubicacion') return 'place';
    return 'event_note';
  };

  const tipoLabelMap: Record<string, string> = {
    persona: 'persona',
    vehiculo: 'vehículo',
    ubicacion: 'ubicación',
    suceso: 'suceso',
  };

  const abrirConfig = (tab: TabConfig) => {
    setMenu(null);
    setPestañaConfig(tab);
    setModal('configuracion');
  };

  const abrirModal = (m: ModalAbierto) => {
    setMenu(null);
    setModal(m);
  };

  return (
    <>
      <header className="topbar" ref={boxRef}>
        <div className="search-box">
          <span className="material-symbols-outlined">search</span>
          <input
            type="text"
            placeholder="Buscar personas, vehículos, ubicaciones, sucesos..."
            value={query}
            onChange={(e) => { setQuery(e.target.value); setMostrando(true); }}
            onFocus={() => setMostrando(true)}
          />
          {mostrando && resultados.length > 0 && (
            <div className="search-results">
              {resultados.map((r, i) => (
                <div key={`${r.tipo}-${r.id}-${i}`} className="search-result" onClick={() => seleccionar(r)}>
                  <span className="material-symbols-outlined">{iconoTipo(r.tipo)}</span>
                  <div style={{ flex: 1 }}>
                    <div className="search-result-title">{r.titulo}</div>
                    <div className="search-result-sub">{r.subtitulo}</div>
                  </div>
                  <span className="badge muted">{tipoLabelMap[r.tipo]}</span>
                </div>
              ))}
            </div>
          )}
          {mostrando && query.trim() && resultados.length === 0 && (
            <div className="search-results">
              <div className="search-result" style={{ color: 'var(--slate-500)' }}>
                Sin resultados para "{query}"
              </div>
            </div>
          )}
        </div>

        <div className="topbar-actions">
          {/* Configuración */}
          <div className="topbar-menu-wrap">
            <span className="material-symbols-outlined" title="Configuración"
              onClick={() => setMenu(menu === 'config' ? null : 'config')}>
              settings
            </span>
            {menu === 'config' && (
              <div className="dropdown">
                <div className="dropdown-title">Configuración</div>
                <div className="dropdown-item" onClick={() => abrirConfig('general')}>
                  <span className="material-symbols-outlined">tune</span>
                  Preferencias del sistema
                </div>
                <div className="dropdown-item" onClick={() => abrirConfig('general')}>
                  <span className="material-symbols-outlined">palette</span>
                  Tema de la interfaz
                </div>
                <div className="dropdown-divider"></div>
                <div className="dropdown-item" onClick={() => abrirConfig('motor')}>
                  <span className="material-symbols-outlined">hub</span>
                  Umbrales del motor
                </div>
              </div>
            )}
          </div>

          {/* Ayuda */}
          <div className="topbar-menu-wrap">
            <span className="material-symbols-outlined" title="Ayuda"
              onClick={() => setMenu(menu === 'help' ? null : 'help')}>
              help_outline
            </span>
            {menu === 'help' && (
              <div className="dropdown">
                <div className="dropdown-title">Centro de ayuda</div>
                <div className="dropdown-item" onClick={() => abrirModal('manual')}>
                  <span className="material-symbols-outlined">book</span>
                  Manual de usuario
                </div>
                <div className="dropdown-item" onClick={() => abrirModal('tutorial')}>
                  <span className="material-symbols-outlined">play_circle</span>
                  Tutorial rápido
                </div>
                <div className="dropdown-item" onClick={() => abrirModal('api')}>
                  <span className="material-symbols-outlined">api</span>
                  Documentación API
                </div>
                <div className="dropdown-divider"></div>
                <div className="dropdown-item" style={{ color: 'var(--slate-500)' }}>
                  <span className="material-symbols-outlined">info</span>
                  Versión 0.1.0-MVP
                </div>
              </div>
            )}
          </div>

          {/* Perfil */}
          <div className="topbar-menu-wrap">
            <span className="material-symbols-outlined" title="Perfil"
              onClick={() => setMenu(menu === 'perfil' ? null : 'perfil')}>
              account_circle
            </span>
            {menu === 'perfil' && (
              <div className="dropdown">
                <div className="dropdown-user">
                  <div className="user-avatar" style={{ background: 'var(--red-600)', color: 'white' }}>
                    {(user?.nombreCompleto || user?.username || '??').slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <div className="dropdown-user-name">{user?.nombreCompleto || user?.username}</div>
                    <div className="dropdown-user-role">{user?.rol}</div>
                  </div>
                </div>
                <div className="dropdown-divider"></div>
                <div className="dropdown-item" onClick={() => abrirModal('perfil')}>
                  <span className="material-symbols-outlined">person</span>
                  Mi perfil
                </div>
                <div className="dropdown-item" onClick={() => abrirModal('password')}>
                  <span className="material-symbols-outlined">vpn_key</span>
                  Cambiar contraseña
                </div>
                <div className="dropdown-divider"></div>
                <div className="dropdown-item danger" onClick={() => { setMenu(null); logout(); }}>
                  <span className="material-symbols-outlined">logout</span>
                  Cerrar sesión
                </div>
              </div>
            )}
          </div>

          <div className="system-status">
            <span className="status-dot"></span>
            <span className="status-text">Sistema en línea</span>
          </div>
        </div>
      </header>

      <ModalConfiguracion
        abierto={modal === 'configuracion'}
        onClose={() => setModal(null)}
        pestañaInicial={pestañaConfig}
      />
      <ModalManual abierto={modal === 'manual'} onClose={() => setModal(null)} />
      <ModalTutorial abierto={modal === 'tutorial'} onClose={() => setModal(null)} />
      <ModalAPI abierto={modal === 'api'} onClose={() => setModal(null)} />
      <ModalPerfil abierto={modal === 'perfil'} onClose={() => setModal(null)} />
      <ModalCambiarPassword abierto={modal === 'password'} onClose={() => setModal(null)} />
    </>
  );
}