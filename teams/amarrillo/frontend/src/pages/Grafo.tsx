import { useEffect, useRef, useState } from 'react';
import cytoscape, { Core, ElementDefinition } from 'cytoscape';
import { grafoService, engineService } from '../services/api';

interface NodoDetalle {
  id: string;
  label: string;
  tipo: string;
  subtipo?: string;
  sospechoso?: boolean;
}

export default function Grafo() {
  const contRef = useRef<HTMLDivElement>(null);
  const cyRef = useRef<Core | null>(null);
  const [cargando, setCargando] = useState(false);
  const [msg, setMsg] = useState('');
  const [stats, setStats] = useState({ nodos: 0, aristas: 0, hilos: 0, sospechosos: 0 });
  const [detalle, setDetalle] = useState<NodoDetalle | null>(null);
  const [vinculoDetalle, setVinculoDetalle] = useState<any>(null);
  const [focusActivo, setFocusActivo] = useState(false);

  // ==========================================================
  //  Generadores de iconos SVG por tipo de nodo
  // ==========================================================
  const avatarColor = (seed: string) => {
    let h = 0;
    for (let i = 0; i < seed.length; i++) h = seed.charCodeAt(i) + ((h << 5) - h);
    const hue = Math.abs(h) % 360;
    return `hsl(${hue}, 55%, 52%)`;
  };

  const iniciales = (label: string) => {
    const parts = label.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  // Persona -> circulo con anillo, iniciales, aro punteado si es sospechoso
  const avatarPersona = (label: string, sospechoso: boolean) => {
    const color = sospechoso ? '#DC2626' : avatarColor(label);
    const ini = iniciales(label);
    const uid = ini + Math.abs(label.length);
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <linearGradient id="gp${uid}" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="${color}"/>
            <stop offset="100%" stop-color="#0F172A"/>
          </linearGradient>
        </defs>
        <circle cx="60" cy="60" r="54" fill="url(#gp${uid})"
          stroke="${sospechoso ? '#EF4444' : '#3B82F6'}" stroke-width="5"/>
        ${sospechoso
          ? '<circle cx="60" cy="60" r="58" fill="none" stroke="#EF4444" stroke-width="2" stroke-dasharray="4 4" opacity="0.7"/>'
          : ''}
        <text x="60" y="62" text-anchor="middle" dy="0.35em"
          font-family="Inter, sans-serif" font-size="40" font-weight="800" fill="white"
          letter-spacing="-1">${ini}</text>
        ${sospechoso
          ? '<circle cx="94" cy="26" r="12" fill="#EF4444" stroke="#0F172A" stroke-width="2"/><text x="94" y="31" text-anchor="middle" font-size="16" font-weight="900" fill="white">!</text>'
          : ''}
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Vehiculo -> tarjeta con silueta de auto y placa
  const iconVehiculo = (placa: string, estado: string) => {
    const robado = estado === 'ROBADO' || estado === 'DESAPARECIDO';
    const apoyo = estado === 'VEHICULO_APOYO' || estado === 'BAJO_OBSERVACION';
    const bg = robado ? '#7F1D1D' : apoyo ? '#78350F' : '#1E293B';
    const accent = robado ? '#EF4444' : apoyo ? '#F59E0B' : '#94A3B8';
    const placaCorta = placa.length > 7 ? placa.slice(0, 7) : placa;
    const uid = placa.replace(/[^a-zA-Z0-9]/g, '');
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <linearGradient id="gv${uid}" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="${bg}"/>
            <stop offset="100%" stop-color="#0F172A"/>
          </linearGradient>
        </defs>
        <rect x="8" y="8" width="104" height="104" rx="16" fill="url(#gv${uid})"
          stroke="${accent}" stroke-width="4"/>
        <path d="M 24 66 Q 24 50 34 48 L 86 48 Q 96 50 96 66 L 96 76 Q 96 80 92 80 L 84 80 Q 80 80 80 76
                 L 40 76 Q 40 80 36 80 L 28 80 Q 24 80 24 76 Z" fill="white" opacity="0.95"/>
        <path d="M 38 48 L 44 36 L 76 36 L 82 48 Z" fill="${accent}" opacity="0.45"/>
        <circle cx="38" cy="76" r="6" fill="#0F172A" stroke="${accent}" stroke-width="1.5"/>
        <circle cx="82" cy="76" r="6" fill="#0F172A" stroke="${accent}" stroke-width="1.5"/>
        <rect x="22" y="90" width="76" height="16" rx="2" fill="#FBBF24" stroke="#0F172A" stroke-width="1"/>
        <text x="60" y="102" text-anchor="middle" font-family="monospace" font-size="13"
              font-weight="800" fill="#0F172A" letter-spacing="1">${placaCorta}</text>
        ${robado
          ? '<circle cx="100" cy="22" r="12" fill="#EF4444" stroke="#0F172A" stroke-width="2"/><text x="100" y="27" text-anchor="middle" font-size="15" font-weight="900" fill="white">!</text>'
          : ''}
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Ubicacion -> diamante con icono
  const iconUbicacion = (tipo: string, sospechoso: boolean) => {
    const bg = sospechoso ? '#7F1D1D' : '#064E3B';
    const accent = sospechoso ? '#EF4444' : '#10B981';
    const emoji =
      tipo === 'TALLER' || tipo === 'GALPON' ? '🔧'
      : tipo === 'CAJERO' ? '🏧'
      : tipo === 'DOMICILIO' ? '🏠'
      : tipo === 'COMERCIO' ? '🏪'
      : tipo === 'TRANSPORTE_PUBLICO' ? '🚌'
      : tipo === 'TERRENO_BALDIO' ? '🌾' : '📍';
    const tipoLabel: Record<string, string> = {
      TALLER: 'TALLER', GALPON: 'GALPÓN', TERRENO_BALDIO: 'BALDÍO', DOMICILIO: 'CASA',
      CAJERO: 'CAJERO', TRANSPORTE_PUBLICO: 'BUS', COMERCIO: 'COMERCIO', OTRO: 'OTRO',
    };
    const label = tipoLabel[tipo] || 'POI';
    const uid = tipo + (sospechoso ? 's' : 'n');
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <path d="M 60 6 L 114 60 L 60 114 L 6 60 Z"
              fill="${bg}" stroke="${accent}" stroke-width="4"/>
        <path d="M 60 14 L 106 60 L 60 106 L 14 60 Z"
              fill="none" stroke="white" stroke-width="1" opacity="0.15"/>
        <text x="60" y="66" text-anchor="middle" font-size="34" id="e${uid}">${emoji}</text>
        <text x="60" y="90" text-anchor="middle" font-family="Inter" font-size="11"
              font-weight="800" fill="white" letter-spacing="0.5">${label}</text>
        ${sospechoso
          ? '<circle cx="100" cy="20" r="11" fill="#EF4444" stroke="#0F172A" stroke-width="2"/><text x="100" y="24" text-anchor="middle" font-size="14" font-weight="900" fill="white">!</text>'
          : ''}
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Suceso -> triangulo de advertencia
  const iconSuceso = (id: string, tipo: string) => {
    const emoji =
      tipo === 'ROBO_VEHICULO' ? '🚗'
      : tipo === 'DESAPARICION' ? '❓'
      : tipo === 'AVISTAMIENTO' ? '👁' : '⚠';
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <path d="M 60 8 L 112 104 L 8 104 Z"
              fill="#7F1D1D" stroke="#EF4444" stroke-width="4" stroke-linejoin="round"/>
        <path d="M 60 20 L 100 96 L 20 96 Z"
              fill="none" stroke="#FBBF24" stroke-width="1" opacity="0.35"/>
        <text x="60" y="80" text-anchor="middle" font-size="36">${emoji}</text>
        <text x="60" y="100" text-anchor="middle" font-family="monospace"
              font-size="10" font-weight="800" fill="#FBBF24">EV-${id}</text>
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // ==========================================================
  //  Carga y render del grafo
  // ==========================================================
  const cargarGrafo = async () => {
    setCargando(true);
    try {
      const data = await grafoService.completo();

      const elementos: ElementDefinition[] = [
        ...data.nodes.map((n) => {
          const d: any = n.data;
          let imagen = '';
          if (d.tipo === 'PERSONA') imagen = avatarPersona(d.label, !!d.sospechoso);
          else if (d.tipo === 'VEHICULO') imagen = iconVehiculo(d.label, d.subtipo || 'NORMAL');
          else if (d.tipo === 'UBICACION') imagen = iconUbicacion(d.subtipo || 'OTRO', !!d.sospechoso);
          else if (d.tipo === 'SUCESO') imagen = iconSuceso(d.id.split('_')[1] || '', d.subtipo || '');
          // grado de sospecha para halo
          const esSospechoso =
            !!d.sospechoso ||
            d.subtipo === 'ROBADO' ||
            d.subtipo === 'SOSPECHOSO' ||
            d.subtipo === 'DESAPARICION';
          return { group: 'nodes' as const, data: { ...d, imagen, halo: esSospechoso ? 1 : 0 } };
        }),
        ...data.edges.map((e) => {
          const d: any = e.data;
          // normalizar score a grosor (2 a 7)
          const score = Number(d.score) || 0;
          const peso = d.tipo === 'HILO_ROJO' ? Math.max(2.5, Math.min(7, 2.5 + score * 4.5)) : 1.5;
          const critico = d.tipo === 'HILO_ROJO' && score >= 0.7 ? 1 : 0;
          return { group: 'edges' as const, data: { ...d, peso, critico } };
        }),
      ];

      if (cyRef.current) cyRef.current.destroy();

      cyRef.current = cytoscape({
        container: contRef.current!,
        elements: elementos,
        style: [
          // ---- NODOS ----
          {
            selector: 'node',
            style: {
              label: 'data(label)',
              color: '#E2E8F0',
              'font-size': '11px',
              'font-weight': 700,
              'font-family': 'Inter, sans-serif',
              'text-valign': 'bottom',
              'text-halign': 'center',
              'text-margin-y': 8,
              'text-outline-width': 3,
              'text-outline-color': '#020617',
              'background-image': 'data(imagen)',
              'background-fit': 'contain',
              'background-clip': 'none',
              'background-opacity': 0,
              'border-width': 0,
              width: 66,
              height: 66,
              'transition-property': 'opacity, width, height',
              'transition-duration': '0.25s' as any,
            },
          },
          { selector: 'node[tipo = "PERSONA"]', style: { width: 62, height: 62 } },
          { selector: 'node[tipo = "VEHICULO"]', style: { width: 68, height: 68 } },
          { selector: 'node[tipo = "UBICACION"]', style: { width: 74, height: 74 } },
          { selector: 'node[tipo = "SUCESO"]', style: { width: 64, height: 64 } },
          // halo rojo pulsante (via overlay) para sospechosos
          {
            selector: 'node[halo = 1]',
            style: {
              'overlay-color': '#EF4444',
              'overlay-opacity': 0.18,
              'overlay-padding': 10,
              color: '#FECACA',
              'text-outline-color': '#450a0a',
            },
          },
          {
            selector: 'node:selected',
            style: {
              'overlay-color': '#FBBF24',
              'overlay-opacity': 0.35,
              'overlay-padding': 14,
            },
          },
          // atenuado (para focus al hover)
          { selector: 'node.atenuado', style: { opacity: 0.12 } },
          { selector: 'node.resaltado', style: { 'overlay-color': '#38BDF8', 'overlay-opacity': 0.2, 'overlay-padding': 12 } },
          // ---- ARISTAS ----
          {
            selector: 'edge',
            style: {
              width: 'data(peso)',
              'curve-style': 'bezier',
              'line-color': '#475569',
              'target-arrow-color': '#475569',
              'target-arrow-shape': 'triangle',
              'arrow-scale': 0.8,
              opacity: 0.55,
              'transition-property': 'opacity, width, line-color',
              'transition-duration': '0.25s' as any,
            },
          },
          {
            selector: 'edge[tipo = "HILO_ROJO"]',
            style: {
              'line-color': '#EF4444',
              'target-arrow-color': '#EF4444',
              opacity: 0.9,
            },
          },
          // criticos: hilo rojo animado (flujo)
          {
            selector: 'edge[critico = 1]',
            style: {
              'line-style': 'dashed',
              'line-dash-pattern': [10, 6],
              'line-color': '#F87171',
              'target-arrow-color': '#F87171',
            },
          },
          {
            selector: 'edge[tipo = "DIRECTO"]',
            style: { 'line-style': 'dashed', 'line-color': '#64748B', opacity: 0.35 },
          },
          { selector: 'edge:selected', style: { width: 6, 'line-color': '#FBBF24', 'target-arrow-color': '#FBBF24', opacity: 1 } },
          { selector: 'edge.atenuado', style: { opacity: 0.06 } },
          { selector: 'edge.resaltado', style: { 'line-color': '#38BDF8', 'target-arrow-color': '#38BDF8', opacity: 1, width: 5 } },
        ],
        layout: {
          name: 'cose',
          animate: true,
          animationDuration: 900,
          idealEdgeLength: () => 150,
          nodeRepulsion: () => 120000,
          nodeOverlap: 40,
          padding: 70,
          randomize: true,
          componentSpacing: 240,
          edgeElasticity: () => 160,
          gravity: 0.2,
          numIter: 3000,
          coolingFactor: 0.95,
          initialTemp: 220,
        } as any,
        minZoom: 0.15,
        maxZoom: 4,
        wheelSensitivity: 0.2,
      });

      const cy = cyRef.current;

      // ---- animacion del flujo en hilos criticos ----
      let offset = 0;
      const criticos = cy.edges('[critico = 1]');
      if (criticos.length > 0) {
        const animar = () => {
          offset = (offset + 1) % 16;
          criticos.style('line-dash-offset', -offset);
          (cy as any)._flujo = requestAnimationFrame(animar);
        };
        animar();
      }

      // ---- focus al hover: resalta el nodo y sus vecinos ----
      cy.on('mouseover', 'node', (e) => {
        const nodo = e.target;
        const vecindario = nodo.closedNeighborhood();
        cy.elements().addClass('atenuado');
        vecindario.removeClass('atenuado');
        nodo.addClass('resaltado');
        vecindario.edges().addClass('resaltado');
        setFocusActivo(true);
      });
      cy.on('mouseout', 'node', () => {
        cy.elements().removeClass('atenuado resaltado');
        setFocusActivo(false);
      });

      // ---- clicks ----
      cy.on('tap', 'node', (e) => {
        const d = e.target.data();
        setDetalle({ id: d.id, label: d.label, tipo: d.tipo, subtipo: d.subtipo, sospechoso: d.sospechoso });
        setVinculoDetalle(null);
      });
      cy.on('tap', 'edge', (e) => {
        setVinculoDetalle(e.target.data());
        setDetalle(null);
      });
      cy.on('tap', (e) => {
        if (e.target === cy) { setDetalle(null); setVinculoDetalle(null); }
      });

      const hilos = data.edges.filter((e: any) => e.data.tipo === 'HILO_ROJO').length;
      const sosp = data.nodes.filter((n: any) =>
        n.data.sospechoso || n.data.subtipo === 'ROBADO' || n.data.subtipo === 'SOSPECHOSO').length;
      setStats({ nodos: data.nodes.length, aristas: data.edges.length, hilos, sospechosos: sosp });
      setMsg('');
    } catch (e) {
      setMsg('Error al cargar el grafo');
    } finally {
      setCargando(false);
    }
  };

  const ejecutarMotor = async () => {
    setCargando(true);
    setMsg('Ejecutando motor...');
    try {
      const r = await engineService.ejecutarTodo();
      setMsg(`Motor ejecutado: ${r.totalVinculos} vínculos, ${r.totalAlertas} alertas`);
      await cargarGrafo();
    } catch {
      setMsg('Error al ejecutar el motor');
    } finally {
      setCargando(false);
    }
  };

  const zoomIn = () => cyRef.current?.zoom(cyRef.current.zoom() * 1.25);
  const zoomOut = () => cyRef.current?.zoom(cyRef.current.zoom() * 0.8);
  const centrar = () => cyRef.current?.fit(undefined, 70);
  const reorganizar = () => {
    cyRef.current?.layout({
      name: 'cose', animate: true, animationDuration: 900,
      idealEdgeLength: () => 150, nodeRepulsion: () => 120000,
      padding: 70, randomize: true, componentSpacing: 240, gravity: 0.2, numIter: 3000,
    } as any).run();
  };

  useEffect(() => {
    cargarGrafo();
    return () => {
      const cy = cyRef.current as any;
      if (cy?._flujo) cancelAnimationFrame(cy._flujo);
      cyRef.current?.destroy();
    };
  }, []);

  const traducirTipo = (t: string) =>
    ({ PERSONA: 'Persona', VEHICULO: 'Vehículo', UBICACION: 'Ubicación', SUCESO: 'Suceso' }[t] || t);

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Grafo Red Thread</h1>
          <p className="page-subtitle">
            Visualización de vínculos descubiertos por el motor de inteligencia
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">NODOS: {stats.nodos}</span>
          <span className="badge-pill alerta">HILO ROJO: {stats.hilos}</span>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-secondary" onClick={cargarGrafo} disabled={cargando}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>refresh</span>
          Recargar grafo
        </button>
        <button className="btn-primary" onClick={ejecutarMotor} disabled={cargando}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>bolt</span>
          Ejecutar motor y recargar
        </button>
        <button className="btn-ghost" onClick={reorganizar} disabled={cargando}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>scatter_plot</span>
          Reorganizar
        </button>
        {msg && (
          <span style={{
            marginLeft: 'auto', alignSelf: 'center', fontSize: 12,
            color: msg.includes('Error') ? 'var(--red-500)' : 'var(--tertiary)',
          }}>
            {msg}
          </span>
        )}
      </div>

      <div className="grafo-wrapper">
        <div style={{ position: 'relative' }}>
          <div ref={contRef} className="grafo-canvas grafo-radar" />

          {/* HUD superior: hint de focus */}
          {focusActivo && (
            <div className="grafo-hud-focus">
              <span className="material-symbols-outlined" style={{ fontSize: 14 }}>center_focus_strong</span>
              Modo foco: resaltando conexiones directas
            </div>
          )}

          {/* Mini-stats flotantes */}
          <div className="grafo-hud-stats">
            <div className="grafo-hud-stat">
              <span className="grafo-hud-num">{stats.nodos}</span>
              <span className="grafo-hud-lbl">nodos</span>
            </div>
            <div className="grafo-hud-stat danger">
              <span className="grafo-hud-num">{stats.hilos}</span>
              <span className="grafo-hud-lbl">hilos rojos</span>
            </div>
            <div className="grafo-hud-stat danger">
              <span className="grafo-hud-num">{stats.sospechosos}</span>
              <span className="grafo-hud-lbl">sospechosos</span>
            </div>
          </div>

          <div className="grafo-zoom-controls">
            <button className="btn-icon" onClick={zoomIn} title="Acercar">
              <span className="material-symbols-outlined">add</span>
            </button>
            <div style={{ width: 24, height: 1, background: 'var(--slate-800)' }}></div>
            <button className="btn-icon" onClick={zoomOut} title="Alejar">
              <span className="material-symbols-outlined">remove</span>
            </button>
            <div style={{ width: 24, height: 1, background: 'var(--slate-800)' }}></div>
            <button className="btn-icon" onClick={centrar} title="Centrar">
              <span className="material-symbols-outlined">filter_center_focus</span>
            </button>
          </div>
        </div>

        <div className="grafo-side">
          {/* Filtros */}
          <div className="side-panel">
            <h4>Filtros del grafo</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {[
                { tipo: 'PERSONA', label: 'Personas', icon: 'group' },
                { tipo: 'VEHICULO', label: 'Vehículos', icon: 'directions_car' },
                { tipo: 'UBICACION', label: 'Ubicaciones', icon: 'place' },
                { tipo: 'SUCESO', label: 'Sucesos', icon: 'warning' },
              ].map(f => (
                <label key={f.tipo} style={{
                  display: 'flex', alignItems: 'center', gap: 10, padding: '6px 10px',
                  background: 'var(--slate-950)', border: '1px solid var(--slate-800)',
                  cursor: 'pointer', fontSize: 11,
                }}>
                  <input type="checkbox" defaultChecked
                    onChange={(e) => cyRef.current?.nodes(`[tipo = "${f.tipo}"]`)
                      .style('display', e.target.checked ? 'element' : 'none')}
                    style={{ width: 'auto' }} />
                  <span className="material-symbols-outlined" style={{ fontSize: 14, color: 'var(--red-500)' }}>
                    {f.icon}
                  </span>
                  <span style={{ color: 'white', fontWeight: 600, textTransform: 'uppercase' }}>{f.label}</span>
                </label>
              ))}
            </div>
            <div style={{ marginTop: 12, paddingTop: 10, borderTop: '1px solid var(--slate-800)' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '6px 10px', cursor: 'pointer', fontSize: 11 }}>
                <input type="checkbox" defaultChecked
                  onChange={(e) => cyRef.current?.edges('[tipo = "DIRECTO"]')
                    .style('display', e.target.checked ? 'element' : 'none')}
                  style={{ width: 'auto' }} />
                <span style={{ color: 'var(--slate-400)', textTransform: 'uppercase', fontWeight: 600 }}>
                  Mostrar relaciones directas
                </span>
              </label>
              <label style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '6px 10px', cursor: 'pointer', fontSize: 11 }}>
                <input type="checkbox" defaultChecked
                  onChange={(e) => cyRef.current?.edges('[tipo = "HILO_ROJO"]')
                    .style('display', e.target.checked ? 'element' : 'none')}
                  style={{ width: 'auto' }} />
                <span style={{ color: 'var(--red-500)', textTransform: 'uppercase', fontWeight: 600 }}>
                  Mostrar Hilo Rojo
                </span>
              </label>
            </div>
          </div>

          {/* Detalle */}
          <div className="side-panel">
            <h4>Detalle de Entidad</h4>
            {!detalle && !vinculoDetalle && (
              <div className="detail-pre">
                Pasá el cursor sobre un nodo para ver sus conexiones, o hacé click para ver su ficha.
              </div>
            )}
            {detalle && (
              <div className="dossier">
                <div className="dossier-header">
                  <div className="dossier-avatar" style={{
                    background:
                      detalle.tipo === 'PERSONA' ? avatarColor(detalle.label)
                      : detalle.tipo === 'VEHICULO' ? '#F59E0B'
                      : detalle.tipo === 'UBICACION' ? '#10B981' : '#DC2626',
                  }}>
                    {detalle.tipo === 'PERSONA' && iniciales(detalle.label)}
                    {detalle.tipo === 'VEHICULO' && <span className="material-symbols-outlined">directions_car</span>}
                    {detalle.tipo === 'UBICACION' && <span className="material-symbols-outlined">place</span>}
                    {detalle.tipo === 'SUCESO' && <span className="material-symbols-outlined">warning</span>}
                  </div>
                  <div>
                    <div className="dossier-name">{detalle.label}</div>
                    <div className="dossier-type">{traducirTipo(detalle.tipo)}</div>
                    {detalle.subtipo && <div className="dossier-subtype">{detalle.subtipo}</div>}
                  </div>
                </div>
                <div className="dossier-fields">
                  <div className="dossier-field"><span>ID</span><strong>{detalle.id}</strong></div>
                  {detalle.sospechoso && (
                    <div className="dossier-alert">
                      <span className="material-symbols-outlined">warning</span>
                      Marcado como sospechoso por el motor
                    </div>
                  )}
                </div>
              </div>
            )}
            {vinculoDetalle && (
              <div className="dossier">
                <div className="dossier-header">
                  <div className="dossier-avatar" style={{ background: '#DC2626' }}>
                    <span className="material-symbols-outlined">link</span>
                  </div>
                  <div>
                    <div className="dossier-name">Vínculo detectado</div>
                    <div className="dossier-type">{vinculoDetalle.tipo === 'HILO_ROJO' ? 'Hilo Rojo' : 'Relación directa'}</div>
                  </div>
                </div>
                <div className="dossier-fields">
                  <div className="dossier-field"><span>Regla</span><strong>{vinculoDetalle.regla}</strong></div>
                  <div className="dossier-field"><span>Origen</span><strong>{vinculoDetalle.source}</strong></div>
                  <div className="dossier-field"><span>Destino</span><strong>{vinculoDetalle.target}</strong></div>
                  {vinculoDetalle.score && (
                    <div className="dossier-field">
                      <span>Puntuación</span>
                      <strong>{Number(vinculoDetalle.score).toFixed(2)}</strong>
                    </div>
                  )}
                  {vinculoDetalle.critico === 1 && (
                    <div className="dossier-alert">
                      <span className="material-symbols-outlined">priority_high</span>
                      Vínculo crítico (score alto)
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Leyenda */}
          <div className="side-panel">
            <h4>Leyenda</h4>
            <div className="legend-row">
              <span className="legend-shape circle" style={{ background: '#3B82F6' }}></span>
              Personas
            </div>
            <div className="legend-row">
              <span className="legend-shape square" style={{ background: '#F59E0B' }}></span>
              Vehículos
            </div>
            <div className="legend-row">
              <span className="legend-shape diamond" style={{ background: '#10B981' }}></span>
              Ubicaciones
            </div>
            <div className="legend-row">
              <span className="legend-shape triangle" style={{ borderBottomColor: '#EF4444' }}></span>
              Sucesos
            </div>
            <div className="legend-hilo">
              <div className="legend-hilo-line"></div>
              Hilo Rojo (grosor = score)
            </div>
            <div className="legend-row" style={{ color: 'var(--slate-500)', marginTop: 8 }}>
              <span style={{
                display: 'inline-block', width: 18, height: 0,
                borderTop: '2px dashed #F87171',
              }}></span>
              Vínculo crítico (animado)
            </div>
            <div className="legend-row" style={{ color: 'var(--slate-500)' }}>
              <span style={{
                display: 'inline-block', width: 18, height: 0,
                borderTop: '1.5px dashed #64748B',
              }}></span>
              Relación directa
            </div>
          </div>

          {/* Controles */}
          <div className="side-panel">
            <h4>Controles</h4>
            <div style={{ fontSize: 11, color: 'var(--slate-400)', lineHeight: 1.8 }}>
              <div><strong style={{ color: 'white' }}>Hover:</strong> Resaltar conexiones</div>
              <div><strong style={{ color: 'white' }}>Click:</strong> Ver ficha detallada</div>
              <div><strong style={{ color: 'white' }}>Arrastrar:</strong> Reposicionar nodo</div>
              <div><strong style={{ color: 'white' }}>Rueda:</strong> Zoom in/out</div>
              <div><strong style={{ color: 'white' }}>Reorganizar:</strong> Recalcular layout</div>
              <div style={{ marginTop: 8, color: 'var(--red-500)', fontWeight: 600 }}>
                Las líneas rojas son vínculos del Hilo Rojo
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
