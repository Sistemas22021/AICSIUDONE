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
  const [stats, setStats] = useState({ nodos: 0, aristas: 0 });
  const [detalle, setDetalle] = useState<NodoDetalle | null>(null);
  const [vinculoDetalle, setVinculoDetalle] = useState<any>(null);

  // Color hash desde nombre
  const avatarColor = (seed: string) => {
    let h = 0;
    for (let i = 0; i < seed.length; i++) h = seed.charCodeAt(i) + ((h << 5) - h);
    const hue = Math.abs(h) % 360;
    return `hsl(${hue}, 60%, 50%)`;
  };

  const iniciales = (label: string) => {
    const parts = label.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  // Avatar persona — círculo con gradiente + iniciales + ojos esquemáticos
  const avatarPersona = (label: string, sospechoso: boolean) => {
    const color = sospechoso ? '#DC2626' : avatarColor(label);
    const ini = iniciales(label);
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <linearGradient id="g${ini}" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="${color}" stop-opacity="1"/>
            <stop offset="100%" stop-color="#1E293B" stop-opacity="1"/>
          </linearGradient>
          <filter id="sh${ini}" x="-50%" y="-50%" width="200%" height="200%">
            <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.5"/>
          </filter>
        </defs>
        <circle cx="60" cy="60" r="56" fill="url(#g${ini})"
          stroke="${sospechoso ? '#EF4444' : '#0F172A'}" stroke-width="4" filter="url(#sh${ini})"/>
        ${sospechoso
          ? '<circle cx="60" cy="60" r="58" fill="none" stroke="#EF4444" stroke-width="2" stroke-dasharray="3 3" opacity="0.7"/>'
          : ''
        }
        <text x="60" y="62" text-anchor="middle" dy="0.35em"
          font-family="Inter, sans-serif" font-size="38" font-weight="800" fill="white"
          letter-spacing="-1">${ini}</text>
        ${sospechoso
          ? '<circle cx="92" cy="28" r="10" fill="#EF4444" stroke="white" stroke-width="2"/><text x="92" y="32" text-anchor="middle" font-size="14" font-weight="900" fill="white">!</text>'
          : ''
        }
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Vehículo — silueta de auto vista frontal con placa
  const iconVehiculo = (placa: string, estado: string) => {
    const robado = estado === 'ROBADO' || estado === 'DESAPARECIDO';
    const apoyo = estado === 'VEHICULO_APOYO' || estado === 'BAJO_OBSERVACION';
    const bg = robado ? '#7F1D1D' : apoyo ? '#78350F' : '#1E293B';
    const accent = robado ? '#EF4444' : apoyo ? '#F59E0B' : '#94A3B8';
    const placaCorta = placa.length > 7 ? placa.slice(0, 7) : placa;

    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <linearGradient id="bg-${placa}" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="${bg}"/>
            <stop offset="100%" stop-color="#0F172A"/>
          </linearGradient>
          <filter id="sh-${placa}"><feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.5"/></filter>
        </defs>
        <rect x="6" y="6" width="108" height="108" rx="14" fill="url(#bg-${placa})"
          stroke="${accent}" stroke-width="3" filter="url(#sh-${placa})"/>

        <!-- Cuerpo del auto -->
        <path d="M 22 70 Q 22 52 32 50 L 88 50 Q 98 52 98 70 L 98 80 Q 98 84 94 84 L 86 84 Q 82 84 82 80
                 L 38 80 Q 38 84 34 84 L 26 84 Q 22 84 22 80 Z"
              fill="white" opacity="0.95"/>
        <!-- Parabrisas -->
        <path d="M 36 50 L 42 36 L 78 36 L 84 50 Z" fill="${accent}" opacity="0.4"/>
        <!-- Ruedas -->
        <circle cx="36" cy="80" r="6" fill="#0F172A" stroke="${accent}" stroke-width="1.5"/>
        <circle cx="84" cy="80" r="6" fill="#0F172A" stroke="${accent}" stroke-width="1.5"/>
        <!-- Faros -->
        <rect x="22" y="64" width="6" height="3" fill="#FBBF24"/>
        <rect x="92" y="64" width="6" height="3" fill="#FBBF24"/>

        <!-- Placa abajo -->
        <rect x="20" y="94" width="80" height="16" rx="2" fill="#FBBF24" stroke="#0F172A" stroke-width="1"/>
        <text x="60" y="106" text-anchor="middle" font-family="monospace" font-size="13"
              font-weight="800" fill="#0F172A" letter-spacing="1">${placaCorta}</text>

        ${robado
          ? '<circle cx="98" cy="22" r="11" fill="#EF4444" stroke="white" stroke-width="2"/><text x="98" y="27" text-anchor="middle" font-size="14" font-weight="900" fill="white">!</text>'
          : ''
        }
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Ubicación — diamante con icono según tipo
  const iconUbicacion = (tipo: string, sospechoso: boolean) => {
    const bg = sospechoso ? '#7F1D1D' : '#064E3B';
    const accent = sospechoso ? '#EF4444' : '#10B981';
    const emoji =
      tipo === 'TALLER' || tipo === 'GALPON'
        ? '🔧'
        : tipo === 'CAJERO'
        ? '🏧'
        : tipo === 'DOMICILIO'
        ? '🏠'
        : tipo === 'COMERCIO'
        ? '🏪'
        : tipo === 'TRANSPORTE_PUBLICO'
        ? '🚌'
        : tipo === 'TERRENO_BALDIO'
        ? '🌾'
        : '📍';
    const tipoLabel: Record<string, string> = {
      TALLER: 'TALLER',
      GALPON: 'GALPÓN',
      TERRENO_BALDIO: 'BALDÍO',
      DOMICILIO: 'CASA',
      CAJERO: 'CAJERO',
      TRANSPORTE_PUBLICO: 'BUS',
      COMERCIO: 'COMERCIO',
      OTRO: 'OTRO',
    };
    const label = tipoLabel[tipo] || 'POI';

    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <filter id="shu${tipo}"><feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.5"/></filter>
        </defs>
        <path d="M 60 8 L 112 60 L 60 112 L 8 60 Z"
              fill="${bg}" stroke="${accent}" stroke-width="4" filter="url(#shu${tipo})"/>
        <path d="M 60 14 L 106 60 L 60 106 L 14 60 Z"
              fill="none" stroke="white" stroke-width="1" opacity="0.2"/>
        <text x="60" y="68" text-anchor="middle" font-size="38">${emoji}</text>
        <text x="60" y="92" text-anchor="middle" font-family="Inter" font-size="11"
              font-weight="800" fill="white" letter-spacing="0.5">${label}</text>

        ${sospechoso
          ? '<circle cx="100" cy="20" r="10" fill="#EF4444" stroke="white" stroke-width="2"/><text x="100" y="24" text-anchor="middle" font-size="14" font-weight="900" fill="white">!</text>'
          : ''
        }
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

  // Suceso — triángulo de advertencia
  const iconSuceso = (id: string, tipo: string) => {
    const emoji =
      tipo === 'ROBO_VEHICULO'
        ? '🚗'
        : tipo === 'DESAPARICION'
        ? '❓'
        : tipo === 'AVISTAMIENTO'
        ? '👁'
        : '⚠';

    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
        <defs>
          <filter id="shs${id}"><feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.5"/></filter>
        </defs>
        <path d="M 60 8 L 112 104 L 8 104 Z"
              fill="#7F1D1D" stroke="#EF4444" stroke-width="4" filter="url(#shs${id})"/>
        <path d="M 60 18 L 102 96 L 18 96 Z"
              fill="none" stroke="#FBBF24" stroke-width="1" opacity="0.4"/>
        <text x="60" y="78" text-anchor="middle" font-size="38">${emoji}</text>
        <text x="60" y="100" text-anchor="middle" font-family="monospace"
              font-size="10" font-weight="800" fill="#FBBF24">EV-${id}</text>
      </svg>`;
    return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
  };

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
          else if (d.tipo === 'SUCESO')
            imagen = iconSuceso(d.id.split('_')[1] || '', d.subtipo || '');
          return { group: 'nodes' as const, data: { ...d, imagen } };
        }),
        ...data.edges.map((e) => ({ group: 'edges' as const, data: e.data })),
      ];

      if (cyRef.current) cyRef.current.destroy();

      cyRef.current = cytoscape({
        container: contRef.current!,
        elements: elementos,
        style: [
          {
            selector: 'node',
            style: {
              label: 'data(label)',
              color: '#F1F5F9',
              'font-size': '11px',
              'font-weight': 700,
              'font-family': 'Inter, sans-serif',
              'text-valign': 'bottom',
              'text-halign': 'center',
              'text-margin-y': 10,
              'text-outline-width': 3,
              'text-outline-color': '#0F172A',
              'background-image': 'data(imagen)',
              'background-fit': 'contain',
              'background-clip': 'none',
              'background-opacity': 0,
              'border-width': 0,
              width: 70,
              height: 70,
              'overlay-padding': 8,
            },
          },
          { selector: 'node[tipo = "PERSONA"]', style: { width: 64, height: 64 } },
          { selector: 'node[tipo = "VEHICULO"]', style: { width: 70, height: 70 } },
          { selector: 'node[tipo = "UBICACION"]', style: { width: 76, height: 76 } },
          { selector: 'node[tipo = "SUCESO"]', style: { width: 66, height: 66 } },
          {
            selector: 'node[?sospechoso]',
            style: {
              'text-outline-color': '#7F1D1D',
              color: '#FECACA',
            },
          },
          {
            selector: 'node:selected',
            style: {
              'overlay-color': '#FBBF24',
              'overlay-opacity': 0.3,
              'overlay-padding': 12,
            },
          },
          {
            selector: 'edge',
            style: {
              width: 1.5,
              'curve-style': 'bezier',
              'line-color': '#475569',
              'target-arrow-color': '#475569',
              'target-arrow-shape': 'triangle',
              'arrow-scale': 0.9,
              opacity: 0.6,
            },
          },
          {
            selector: 'edge[tipo = "HILO_ROJO"]',
            style: {
              'line-color': '#EF4444',
              'target-arrow-color': '#EF4444',
              width: 3.5,
              'line-style': 'solid',
              opacity: 0.95,
              'overlay-color': '#EF4444',
              'overlay-opacity': 0.05,
              'overlay-padding': 4,
            },
          },
          {
            selector: 'edge[tipo = "DIRECTO"]',
            style: { 'line-style': 'dashed', 'line-color': '#64748B', opacity: 0.4 },
          },
          {
            selector: 'edge:selected',
            style: { width: 5.5, 'line-color': '#FBBF24', 'target-arrow-color': '#FBBF24' },
          },
        ],
        layout: {
          name: 'cose',
          animate: true,
          animationDuration: 1000,
          idealEdgeLength: () => 180,
          nodeRepulsion: () => 80000,
          nodeOverlap: 30,
          padding: 60,
          randomize: true,
          componentSpacing: 200,
          edgeElasticity: () => 200,
          gravity: 0.15,
          numIter: 2500,
        } as any,
        minZoom: 0.2,
        maxZoom: 4,
        wheelSensitivity: 0.2,
      });

      cyRef.current.on('tap', 'node', (e) => {
        const d = e.target.data();
        setDetalle({
          id: d.id,
          label: d.label,
          tipo: d.tipo,
          subtipo: d.subtipo,
          sospechoso: d.sospechoso,
        });
        setVinculoDetalle(null);
      });

      cyRef.current.on('tap', 'edge', (e) => {
        const d = e.target.data();
        setVinculoDetalle(d);
        setDetalle(null);
      });

      cyRef.current.on('tap', (e) => {
        if (e.target === cyRef.current) {
          setDetalle(null);
          setVinculoDetalle(null);
        }
      });

      setStats({ nodos: data.nodes.length, aristas: data.edges.length });
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
  const centrar = () => cyRef.current?.fit(undefined, 60);

  useEffect(() => {
    cargarGrafo();
    return () => {
      cyRef.current?.destroy();
    };
  }, []);

  const traducirTipo = (t: string) =>
    ({
      PERSONA: 'Persona',
      VEHICULO: 'Vehículo',
      UBICACION: 'Ubicación',
      SUCESO: 'Suceso',
    }[t] || t);

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
          <span className="badge-pill alerta">VÍNCULOS: {stats.aristas}</span>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-secondary" onClick={cargarGrafo} disabled={cargando}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            refresh
          </span>
          Recargar grafo
        </button>
        <button className="btn-primary" onClick={ejecutarMotor} disabled={cargando}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            bolt
          </span>
          Ejecutar motor y recargar
        </button>
        {msg && (
          <span
            style={{
              marginLeft: 'auto',
              alignSelf: 'center',
              fontSize: 12,
              color: msg.includes('Error') ? 'var(--red-500)' : 'var(--tertiary)',
            }}
          >
            {msg}
          </span>
        )}
      </div>

      <div className="grafo-wrapper">
        <div style={{ position: 'relative' }}>
          <div ref={contRef} className="grafo-canvas" />

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
          {/* AGREGAR ESTE NUEVO PANEL ANTES DEL DE DETALLE */}
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
                  display: 'flex', alignItems: 'center', gap: 10,
                  padding: '6px 10px', background: 'var(--slate-950)',
                  border: '1px solid var(--slate-800)', cursor: 'pointer', fontSize: 11,
                }}>
                  <input type="checkbox" defaultChecked
                    onChange={(e) => {
                      cyRef.current?.nodes(`[tipo = "${f.tipo}"]`).style('display', e.target.checked ? 'element' : 'none');
                    }}
                    style={{ width: 'auto' }} />
                  <span className="material-symbols-outlined" style={{ fontSize: 14, color: 'var(--red-500)' }}>
                    {f.icon}
                  </span>
                  <span style={{ color: 'white', fontWeight: 600, textTransform: 'uppercase' }}>
                    {f.label}
                  </span>
                </label>
              ))}
            </div>

            <div style={{ marginTop: 12, paddingTop: 10, borderTop: '1px solid var(--slate-800)' }}>
              <label style={{
                display: 'flex', alignItems: 'center', gap: 10,
                padding: '6px 10px', cursor: 'pointer', fontSize: 11,
              }}>
                <input type="checkbox" defaultChecked
                  onChange={(e) => {
                    cyRef.current?.edges('[tipo = "DIRECTO"]').style('display', e.target.checked ? 'element' : 'none');
                  }}
                  style={{ width: 'auto' }} />
                <span style={{ color: 'var(--slate-400)', textTransform: 'uppercase', fontWeight: 600 }}>
                  Mostrar relaciones directas
                </span>
              </label>
              <label style={{
                display: 'flex', alignItems: 'center', gap: 10,
                padding: '6px 10px', cursor: 'pointer', fontSize: 11,
              }}>
                <input type="checkbox" defaultChecked
                  onChange={(e) => {
                    cyRef.current?.edges('[tipo = "HILO_ROJO"]').style('display', e.target.checked ? 'element' : 'none');
                  }}
                  style={{ width: 'auto' }} />
                <span style={{ color: 'var(--red-500)', textTransform: 'uppercase', fontWeight: 600 }}>
                  Mostrar Hilo Rojo
                </span>
              </label>
            </div>
          </div>
          <div className="side-panel">
            <h4>Detalle de Entidad</h4>
            {!detalle && !vinculoDetalle && (
              <div className="detail-pre">
                Hacé click en un nodo o vínculo del grafo para ver su información acá.
              </div>
            )}
            {detalle && (
              <div className="dossier">
                <div className="dossier-header">
                  <div
                    className="dossier-avatar"
                    style={{
                      background:
                        detalle.tipo === 'PERSONA'
                          ? avatarColor(detalle.label)
                          : detalle.tipo === 'VEHICULO'
                          ? '#F59E0B'
                          : detalle.tipo === 'UBICACION'
                          ? '#10B981'
                          : '#DC2626',
                    }}
                  >
                    {detalle.tipo === 'PERSONA' && iniciales(detalle.label)}
                    {detalle.tipo === 'VEHICULO' && (
                      <span className="material-symbols-outlined">directions_car</span>
                    )}
                    {detalle.tipo === 'UBICACION' && (
                      <span className="material-symbols-outlined">place</span>
                    )}
                    {detalle.tipo === 'SUCESO' && (
                      <span className="material-symbols-outlined">warning</span>
                    )}
                  </div>
                  <div>
                    <div className="dossier-name">{detalle.label}</div>
                    <div className="dossier-type">{traducirTipo(detalle.tipo)}</div>
                    {detalle.subtipo && (
                      <div className="dossier-subtype">{detalle.subtipo}</div>
                    )}
                  </div>
                </div>
                <div className="dossier-fields">
                  <div className="dossier-field">
                    <span>ID</span>
                    <strong>{detalle.id}</strong>
                  </div>
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
                    <div className="dossier-type">Hilo Rojo</div>
                  </div>
                </div>
                <div className="dossier-fields">
                  <div className="dossier-field">
                    <span>Regla</span>
                    <strong>{vinculoDetalle.regla}</strong>
                  </div>
                  <div className="dossier-field">
                    <span>Origen</span>
                    <strong>{vinculoDetalle.source}</strong>
                  </div>
                  <div className="dossier-field">
                    <span>Destino</span>
                    <strong>{vinculoDetalle.target}</strong>
                  </div>
                  {vinculoDetalle.score && (
                    <div className="dossier-field">
                      <span>Puntuación</span>
                      <strong>{Number(vinculoDetalle.score).toFixed(2)}</strong>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

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
              <span
                className="legend-shape triangle"
                style={{ borderBottomColor: '#EF4444' }}
              ></span>
              Sucesos
            </div>
            <div className="legend-hilo">
              <div className="legend-hilo-line"></div>
              Hilo Rojo (vínculo)
            </div>
            <div
              className="legend-row"
              style={{ color: 'var(--slate-500)', marginTop: 8 }}
            >
              <span
                style={{
                  display: 'inline-block',
                  width: 18,
                  height: 1.5,
                  background: '#64748B',
                  borderTop: '1px dashed #64748B',
                }}
              ></span>
              Relación directa
            </div>
          </div>

          <div className="side-panel">
            <h4>Controles</h4>
            <div style={{ fontSize: 11, color: 'var(--slate-400)', lineHeight: 1.8 }}>
              <div>
                <strong style={{ color: 'white' }}>Click:</strong> Ver detalle
              </div>
              <div>
                <strong style={{ color: 'white' }}>Arrastrar nodo:</strong> Reposicionar
              </div>
              <div>
                <strong style={{ color: 'white' }}>Rueda:</strong> Zoom in/out
              </div>
              <div>
                <strong style={{ color: 'white' }}>Botón centrar:</strong> Ajustar vista
              </div>
              <div style={{ marginTop: 8, color: 'var(--red-500)', fontWeight: 600 }}>
                ⚠️ Las líneas rojas son vínculos del Hilo Rojo
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}