import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  sucesoService, vehiculoService, ubicacionService, personaService, desaparecidaService,
} from '../services/api';
import type {
  Suceso, Vehiculo, Ubicacion, Persona, TipoSuceso, PersonaDesaparecida,
} from '../types';
import { fileUrl } from '../services/files';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import MapaTactical, { PuntoMapa } from '../components/MapaTactical';
import ModalDetalle from '../components/ModalDetalle';
import ModalConfirmar from '../components/ModalConfirmar';
import { exportarCSV } from '../services/exportar';
import FormSucesoPorTipo from '../components/FormSucesoPorTipo';
import GaleriaFotos from '../components/GaleriaFotos';
import Modal from '../components/Modal';
import FormularioDesaparecida from '../components/FormularioDesaparecida';

const TIPOS: TipoSuceso[] = ['ROBO_VEHICULO', 'DESAPARICION', 'AVISTAMIENTO', 'TRANSACCION'];
const tipoLabel: Record<string, string> = {
  ROBO_VEHICULO: 'Robo de vehículo',
  DESAPARICION: 'Desaparición',
  AVISTAMIENTO: 'Avistamiento',
  TRANSACCION: 'Transacción',
};

const estadoDesapLabel: Record<string, string> = {
  BUSCADA: 'Buscada',
  ENCONTRADA_VIVA: 'Encontrada viva',
  ENCONTRADA_FALLECIDA: 'Encontrada fallecida',
  ARCHIVADA: 'Archivada',
};

/**
 * Fila unificada de la tabla: puede ser un suceso o una desaparicion.
 * Se marca con _origen para ramificar las acciones (ver/eliminar).
 */
interface FilaUnificada {
  _origen: 'suceso' | 'desaparicion';
  id: number;
  tipo: string;
  tipoLabel: string;
  fecha: string;
  vehiculoTexto: string;
  vehiculoSub: string;
  personaTexto: string;
  modus: string;
  suceso?: Suceso;
  desaparecida?: PersonaDesaparecida;
}

export default function Sucesos() {
  const [searchParams] = useSearchParams();
  const [sucesos, setSucesos] = useState<Suceso[]>([]);
  const [desaparecidas, setDesaparecidas] = useState<PersonaDesaparecida[]>([]);
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([]);
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [filtro, setFiltro] = useState('');
  const [filtroTipo, setFiltroTipo] = useState<string>('');
  const [filtroVehiculo, setFiltroVehiculo] = useState<string>('');
  const [filtroPersona, setFiltroPersona] = useState<string>('');
  const [filtroUbicacion, setFiltroUbicacion] = useState<string>('');
  const [ok, setOk] = useState('');
  const [err, setErr] = useState('');
  const [detalle, setDetalle] = useState<FilaUnificada | null>(null);
  const [aEliminar, setAEliminar] = useState<FilaUnificada | null>(null);
  const [editandoDesap, setEditandoDesap] = useState<PersonaDesaparecida | null>(null);
  const [editandoSuceso, setEditandoSuceso] = useState<Suceso | null>(null);
  const [formSuceso, setFormSuceso] = useState({ fechaHora: '', modusOperandi: '', descripcion: '' });

  const cargar = async () => {
    try { setSucesos(await sucesoService.listar()); } catch (e) { console.error('Sucesos:', e); }
    try { setDesaparecidas(await desaparecidaService.listar()); } catch (e) { console.error('Desaparecidas:', e); }
    try { setVehiculos(await vehiculoService.listar()); } catch (e) { console.error('Vehiculos:', e); }
    try { setUbicaciones(await ubicacionService.listar()); } catch (e) { console.error('Ubicaciones:', e); }
    try { setPersonas(await personaService.listar()); } catch (e) { console.error('Personas:', e); }
  };

  useEffect(() => {
    cargar();
    if (searchParams.get('nueva') === '1') {
      setTimeout(() => {
        document.getElementById('form-suceso')?.scrollIntoView({ behavior: 'smooth' });
      }, 300);
    }
  }, []);

  const filasSuceso: FilaUnificada[] = sucesos.map(s => ({
    _origen: 'suceso',
    id: s.id!,
    tipo: s.tipo,
    tipoLabel: tipoLabel[s.tipo] || s.tipo,
    fecha: s.fechaHora,
    vehiculoTexto: s.vehiculo?.placa || '',
    vehiculoSub: s.vehiculo ? `${s.vehiculo.marca} ${s.vehiculo.modelo}` : '',
    personaTexto: s.victima ? `${s.victima.nombre} ${s.victima.apellido}` : '',
    modus: s.modusOperandi || '',
    suceso: s,
  }));

  const filasDesap: FilaUnificada[] = desaparecidas.map(d => ({
    _origen: 'desaparicion',
    id: d.id!,
    tipo: 'DESAPARICION',
    tipoLabel: 'Desaparición',
    fecha: d.fechaDesaparicion,
    vehiculoTexto: '',
    vehiculoSub: '',
    personaTexto: `${d.nombre} ${d.apellido}`,
    modus: '',
    desaparecida: d,
  }));

  const todas = [...filasSuceso, ...filasDesap].sort(
    (a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
  );

  const filtrados = todas.filter(f => {
    if (filtroTipo && f.tipo !== filtroTipo) return false;
    if (filtroVehiculo && String(f.suceso?.vehiculo?.id) !== filtroVehiculo) return false;
    if (filtroPersona && String(f.suceso?.victima?.id) !== filtroPersona) return false;
    if (filtroUbicacion && String(f.suceso?.ubicacion?.id) !== filtroUbicacion) return false;
    if (!filtro.trim()) return true;
    const q = filtro.toLowerCase();
    return f.tipoLabel.toLowerCase().includes(q) ||
      f.modus.toLowerCase().includes(q) ||
      f.vehiculoTexto.toLowerCase().includes(q) ||
      f.personaTexto.toLowerCase().includes(q) ||
      (f.suceso?.descripcion?.toLowerCase().includes(q) ?? false);
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtrados, 10);

  const confirmarEliminar = async () => {
    if (!aEliminar) return;
    try {
      if (aEliminar._origen === 'suceso') {
        await sucesoService.eliminar(aEliminar.id);
      } else {
        await desaparecidaService.eliminar(aEliminar.id);
      }
      setAEliminar(null);
      setOk('Registro eliminado');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo eliminar');
      setAEliminar(null);
    }
  };

  const exportar = () => {
    exportarCSV(
      filtrados.map(f => ({
        ID: f._origen === 'suceso' ? `EV-${String(f.id).padStart(4, '0')}` : `DS-${String(f.id).padStart(4, '0')}`,
        Tipo: f.tipoLabel,
        Fecha: new Date(f.fecha).toLocaleString('es-ES'),
        Vehiculo: f.vehiculoTexto,
        Persona: f.personaTexto,
        Modus: f.modus,
      })),
      'sucesos'
    );
  };

  const puntos: PuntoMapa[] = useMemo(() => {
    const deSucesos: PuntoMapa[] = sucesos
      .filter(s => s.ubicacion?.latitud && s.ubicacion?.longitud)
      .map(s => ({
        id: s.id!, lat: s.ubicacion!.latitud, lng: s.ubicacion!.longitud,
        tipo: 'SUCESO',
        titulo: tipoLabel[s.tipo],
        subtitulo: `EV-${String(s.id).padStart(4, '0')}`,
        sospechoso: s.tipo === 'ROBO_VEHICULO',
        campos: [
          { etiqueta: 'Fecha', valor: new Date(s.fechaHora).toLocaleString('es-ES') },
          { etiqueta: 'Vehículo', valor: s.vehiculo ? s.vehiculo.placa : '—' },
          { etiqueta: 'Víctima', valor: s.victima ? `${s.victima.nombre} ${s.victima.apellido}` : '—' },
          { etiqueta: 'Modus', valor: s.modusOperandi || '—' },
        ],
      }));
    const deDesap: PuntoMapa[] = desaparecidas
      .filter(d => d.ultimaUbicacion?.latitud && d.ultimaUbicacion?.longitud)
      .map(d => ({
        id: d.id!, lat: d.ultimaUbicacion!.latitud, lng: d.ultimaUbicacion!.longitud,
        tipo: 'DESAPARECIDA',
        titulo: `${d.nombre} ${d.apellido}`,
        subtitulo: `DS-${String(d.id).padStart(4, '0')}`,
        sospechoso: d.prioridad === 'CRITICA' || d.prioridad === 'ALTA',
        campos: [
          { etiqueta: 'Documento', valor: d.documento },
          { etiqueta: 'Estado', valor: estadoDesapLabel[d.estado] || d.estado },
          { etiqueta: 'Fecha', valor: new Date(d.fechaDesaparicion).toLocaleString('es-ES') },
        ],
      }));
    return [...deSucesos, ...deDesap];
  }, [sucesos, desaparecidas]);

  const totalRegistros = sucesos.length + desaparecidas.length;
  const ultimaSemana = todas.filter(f => {
    const d = new Date(f.fecha);
    return d.getTime() > Date.now() - 7 * 24 * 3600 * 1000;
  }).length;
  const robos = sucesos.filter(s => s.tipo === 'ROBO_VEHICULO').length;
  const desapCount = desaparecidas.length;

  const abrirEditarSuceso = (s: Suceso) => {
    setEditandoSuceso(s);
    setFormSuceso({
      fechaHora: s.fechaHora ? new Date(s.fechaHora).toISOString().slice(0, 16) : '',
      modusOperandi: s.modusOperandi || '',
      descripcion: s.descripcion || '',
    });
  };

  const guardarEdicionSuceso = async () => {
    if (!editandoSuceso) return;
    try {
      await sucesoService.actualizar(editandoSuceso.id!, {
        tipo: editandoSuceso.tipo,
        fechaHora: formSuceso.fechaHora,
        modusOperandi: formSuceso.modusOperandi,
        descripcion: formSuceso.descripcion,
      } as any);
      setEditandoSuceso(null);
      setOk('Suceso actualizado correctamente');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo actualizar el suceso');
    }
  };

  const limpiarFiltros = () => {
    setFiltro(''); setFiltroTipo(''); setFiltroVehiculo('');
    setFiltroPersona(''); setFiltroUbicacion('');
  };
  const filtrosActivos = filtro || filtroTipo || filtroVehiculo || filtroPersona || filtroUbicacion;

  const idEtiqueta = (f: FilaUnificada) =>
    f._origen === 'suceso' ? `EV-${String(f.id).padStart(4, '0')}` : `DS-${String(f.id).padStart(4, '0')}`;


  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Registro de Sucesos</h1>
          <p className="page-subtitle">Gestión y monitoreo de incidentes criminales en tiempo real.</p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">TOTAL: {totalRegistros}</span>
          <span className="badge-pill alerta">ÚLTIMA SEMANA: {ultimaSemana}</span>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-ghost" onClick={exportar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>download</span>
          Exportar CSV
        </button>
      </div>

      <div className="bento-grid" id="form-suceso">
        <div className="bento-col-5">
          <div className="form-card" style={{ height: '100%' }}>
            <div className="card-header" style={{ borderBottom: 'none', paddingBottom: 0, marginBottom: 20 }}>
              <span className="material-symbols-outlined">app_registration</span>
              <h3 className="card-title">Registrar suceso</h3>
            </div>
            <FormSucesoPorTipo
              onGuardado={() => {
                setOk('Suceso registrado correctamente');
                setTimeout(() => setOk(''), 3000);
                cargar();
              }}
            />
            {ok && <div className="success" style={{ marginTop: 12 }}>{ok}</div>}
            {err && <div className="error" style={{ marginTop: 12 }}>{err}</div>}
          </div>
        </div>
        <div className="bento-col-7">
          <div style={{ display: 'flex', flexDirection: 'column', gap: 20, height: '100%' }}>
            <MapaTactical puntos={puntos} altura={300}
              hudLabel="Mapa de sucesos"
              hudValor={`${puntos.length} ubicaciones en el mapa`}
              emptyMessage="Sin sucesos georreferenciados" />
            <div className="mini-stats">
              <div className="mini-stat" style={{ borderLeft: '4px solid var(--red-500)' }}>
                <div className="mini-stat-label">Registros totales</div>
                <div className="mini-stat-value">{totalRegistros}</div>
                <div className="mini-stat-change">+{ultimaSemana} última semana</div>
              </div>
              <div className="mini-stat">
                <div className="mini-stat-label">Robos de vehículo</div>
                <div className="mini-stat-value danger">{robos}</div>
                <div className="mini-stat-change danger">Activos</div>
              </div>
              <div className="mini-stat">
                <div className="mini-stat-label">Desapariciones</div>
                <div className="mini-stat-value tertiary">{desapCount}</div>
                <div className="mini-stat-change">En investigación</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="form-card" style={{ marginBottom: 20, padding: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
          <span className="material-symbols-outlined" style={{ color: 'var(--red-500)', fontSize: 18 }}>filter_alt</span>
          <span style={{ fontSize: 11, fontWeight: 700, color: 'white', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
            Filtros avanzados
          </span>
          {filtrosActivos && (
            <button type="button" className="btn-ghost"
              style={{ marginLeft: 'auto', fontSize: 10, padding: '4px 10px' }}
              onClick={limpiarFiltros}>
              <span className="material-symbols-outlined" style={{ fontSize: 12 }}>clear</span>
              Limpiar filtros
            </button>
          )}
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 10 }}>
          <select value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value)}>
            <option value="">Todos los tipos</option>
            {TIPOS.map(t => <option key={t} value={t}>{tipoLabel[t]}</option>)}
          </select>
          <select value={filtroVehiculo} onChange={(e) => setFiltroVehiculo(e.target.value)}>
            <option value="">Cualquier vehículo</option>
            {vehiculos.map(v => (
              <option key={v.id} value={v.id}>{v.placa} — {v.marca} {v.modelo}</option>
            ))}
          </select>
          <select value={filtroPersona} onChange={(e) => setFiltroPersona(e.target.value)}>
            <option value="">Cualquier víctima</option>
            {personas.map(p => (
              <option key={p.id} value={p.id}>{p.nombre} {p.apellido}</option>
            ))}
          </select>
          <select value={filtroUbicacion} onChange={(e) => setFiltroUbicacion(e.target.value)}>
            <option value="">Cualquier ubicación</option>
            {ubicaciones.map(u => (
              <option key={u.id} value={u.id}>{u.direccion || `Ubi #${u.id}`}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="table-wrap">
        <div className="table-header">
          <div className="table-header-title">
            <span className="material-symbols-outlined">format_list_bulleted</span>
            <h3>Historial de Sucesos</h3>
          </div>
          <div className="table-header-actions">
            <div className="filter-input-wrap">
              <span className="material-symbols-outlined">search</span>
              <input type="text" placeholder="Buscar por texto libre..."
                value={filtro} onChange={(e) => setFiltro(e.target.value)} />
            </div>
          </div>
        </div>
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Tipo</th><th>Fecha</th>
                <th>Vehículo</th><th>Persona</th><th>Modus</th>
                <th className="right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map(f => (
                <tr key={`${f._origen}-${f.id}`}>
                  <td className="mono" style={{ fontWeight: 700 }}>
                    #{idEtiqueta(f)}
                  </td>
                  <td style={{ color: 'white', fontWeight: 600 }}>{f.tipoLabel}</td>
                  <td style={{ fontSize: 11, color: 'var(--slate-400)' }}>
                    {new Date(f.fecha).toLocaleString('es-ES', {
                      day: '2-digit', month: 'short', year: 'numeric',
                      hour: '2-digit', minute: '2-digit',
                    }).toUpperCase()}
                  </td>
                  <td style={{ fontSize: 12 }}>
                    {f.vehiculoTexto ? (
                      <>
                        <span style={{ color: 'white', fontFamily: 'var(--font-mono)' }}>{f.vehiculoTexto}</span>
                        <div className="row-sub">{f.vehiculoSub}</div>
                      </>
                    ) : <span style={{ color: 'var(--slate-600)' }}>—</span>}
                  </td>
                  <td style={{ fontSize: 12 }}>
                    {f.personaTexto || <span style={{ color: 'var(--slate-600)' }}>—</span>}
                  </td>
                  <td>
                    {f.modus ? (
                      <span className="badge robado" style={{ fontFamily: 'var(--font-mono)' }}>{f.modus}</span>
                    ) : '—'}
                  </td>
                  <td className="right">
                    <div className="table-actions">
                      <button className="btn-icon" onClick={() => setDetalle(f)} title="Ver detalle">
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>visibility</span>
                      </button>
                      {f._origen === 'desaparicion' && f.desaparecida && (
                        <button className="btn-icon" onClick={() => setEditandoDesap(f.desaparecida!)} title="Editar">
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>edit</span>
                        </button>
                      )}
                      {f._origen === 'suceso' && f.suceso && (
                        <button className="btn-icon" onClick={() => abrirEditarSuceso(f.suceso!)} title="Editar">
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>edit</span>
                        </button>
                      )}
                      <button className="btn-icon danger" onClick={() => setAEliminar(f)} title="Eliminar">
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>delete</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {visibles.length === 0 && (
                <tr><td colSpan={7} className="table-empty">
                  {filtrosActivos ? 'Sin resultados' : 'Sin registros'}
                </td></tr>
              )}
            </tbody>
          </table>
        </div>
        <Paginacion total={total} pagina={pagina} porPagina={porPagina}
          onCambiar={setPagina} label="registros" />
      </div>

      {detalle && detalle._origen === 'suceso' && detalle.suceso && (
        <ModalDetalle abierto={true} onClose={() => setDetalle(null)}
          titulo={tipoLabel[detalle.suceso.tipo]}
          subtitulo={`EV-${String(detalle.suceso.id).padStart(4, '0')}`}
          icono="event_note"
          campos={[
            { etiqueta: 'ID', valor: `EV-${String(detalle.suceso.id).padStart(4, '0')}`, mono: true, destacado: true },
            { etiqueta: 'Tipo', valor: tipoLabel[detalle.suceso.tipo] },
            { etiqueta: 'Fecha y hora', valor: new Date(detalle.suceso.fechaHora).toLocaleString('es-ES'), mono: true },
            { etiqueta: 'Modus operandi', valor: detalle.suceso.modusOperandi || '—', mono: true },
            { etiqueta: 'Vehículo', valor: detalle.suceso.vehiculo ? `${detalle.suceso.vehiculo.placa} — ${detalle.suceso.vehiculo.marca} ${detalle.suceso.vehiculo.modelo}` : '—' },
            { etiqueta: 'Víctima', valor: detalle.suceso.victima ? `${detalle.suceso.victima.nombre} ${detalle.suceso.victima.apellido}` : '—' },
            { etiqueta: 'Ubicación del hecho', valor: detalle.suceso.ubicacion?.direccion || '—' },
            { etiqueta: 'Última ubicación', valor: detalle.suceso.ubicacionUltima?.direccion || '—' },
          ]}
          extra={
            detalle.suceso.descripcion ? (
              <div style={{ marginTop: 16 }}>
                <h4 style={{
                  fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase',
                  letterSpacing: '0.1em', margin: '0 0 8px',
                }}>
                  Descripción detallada
                </h4>
                <div style={{
                  padding: 12, background: 'var(--slate-950)',
                  border: '1px solid var(--slate-800)', fontSize: 13,
                  color: 'var(--slate-300)', lineHeight: 1.6,
                }}>
                  {detalle.suceso.descripcion}
                </div>
              </div>
            ) : null
          }
        />
      )}

      {detalle && detalle._origen === 'desaparicion' && detalle.desaparecida && (
        <ModalDetalle abierto={true} onClose={() => setDetalle(null)}
          titulo={`${detalle.desaparecida.nombre} ${detalle.desaparecida.apellido}`}
          subtitulo={`DS-${String(detalle.desaparecida.id).padStart(4, '0')} · Desaparición`}
          icono="person_search"
          avatar={
            detalle.desaparecida.fotoUrl ? (
              <div className="dossier-avatar" style={{ overflow: 'hidden', padding: 0 }}>
                <img src={fileUrl(detalle.desaparecida.fotoUrl) || ''} alt="Foto"
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              </div>
            ) : (
              <div className="dossier-avatar" style={{ background: 'var(--slate-700)' }}>
                <span className="material-symbols-outlined">person_search</span>
              </div>
            )
          }
          campos={[
            { etiqueta: 'Documento', valor: detalle.desaparecida.documento, mono: true, destacado: true },
            { etiqueta: 'Alias', valor: detalle.desaparecida.alias || '—' },
            { etiqueta: 'Estado', valor: estadoDesapLabel[detalle.desaparecida.estado] || detalle.desaparecida.estado, destacado: detalle.desaparecida.estado === 'BUSCADA' },
            { etiqueta: 'Prioridad', valor: detalle.desaparecida.prioridad },
            { etiqueta: 'Fecha desaparición', valor: new Date(detalle.desaparecida.fechaDesaparicion).toLocaleString('es-ES'), mono: true },
            { etiqueta: 'Última ubicación', valor: detalle.desaparecida.ultimaUbicacion?.direccion || '—' },
            { etiqueta: 'Reportante', valor: detalle.desaparecida.reportanteNombre || '—' },
          ]}
          extra={
            <div>
              {detalle.desaparecida.circunstancias && (
                <div style={{ marginBottom: 16 }}>
                  <h4 style={{
                    fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase',
                    letterSpacing: '0.1em', margin: '0 0 8px',
                  }}>
                    Circunstancias
                  </h4>
                  <div style={{
                    padding: 12, background: 'var(--slate-950)',
                    border: '1px solid var(--slate-800)', fontSize: 13,
                    color: 'var(--slate-300)', lineHeight: 1.6,
                  }}>
                    {detalle.desaparecida.circunstancias}
                  </div>
                </div>
              )}
              <h4 style={{
                fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase',
                letterSpacing: '0.1em', margin: '0 0 10px',
              }}>
                Galería de fotos
              </h4>
              <GaleriaFotos desaparecidaId={detalle.desaparecida.id} onCambio={cargar} />
            </div>
          }
        />
      )}

      <Modal
        abierto={!!editandoDesap}
        onClose={() => setEditandoDesap(null)}
        titulo="Editar caso de desaparición"
        icono="person_search"
        ancho={780}
      >
        {editandoDesap && (
          <FormularioDesaparecida
            inicial={editandoDesap}
            ubicaciones={ubicaciones}
            onGuardado={() => {
              setEditandoDesap(null);
              setOk('Caso actualizado correctamente');
              setTimeout(() => setOk(''), 3000);
              cargar();
            }}
            onCancelar={() => setEditandoDesap(null)}
          />
        )}
      </Modal>

      <Modal
        abierto={!!editandoSuceso}
        onClose={() => setEditandoSuceso(null)}
        titulo="Editar suceso"
        icono="edit"
        ancho={560}
      >
        {editandoSuceso && (
          <div>
            <div style={{ marginBottom: 12, fontSize: 12, color: 'var(--slate-500)' }}>
              Editando {tipoLabel[editandoSuceso.tipo]} · EV-{String(editandoSuceso.id).padStart(4, '0')}.
              El vehículo, la víctima y la ubicación no se modifican acá.
            </div>
            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Fecha y hora</label>
              <input type="datetime-local" value={formSuceso.fechaHora}
                max={new Date().toISOString().slice(0, 16)}
                onChange={(e) => setFormSuceso({ ...formSuceso, fechaHora: e.target.value })} />
            </div>
            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Modus operandi</label>
              <input value={formSuceso.modusOperandi}
                onChange={(e) => setFormSuceso({ ...formSuceso, modusOperandi: e.target.value })}
                placeholder="Descripción breve del modus" />
            </div>
            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Descripción</label>
              <textarea rows={4} value={formSuceso.descripcion}
                onChange={(e) => setFormSuceso({ ...formSuceso, descripcion: e.target.value })}
                placeholder="Descripción detallada del suceso" />
            </div>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 16 }}>
              <button className="btn-ghost" onClick={() => setEditandoSuceso(null)}>Cancelar</button>
              <button className="btn-primary" onClick={guardarEdicionSuceso}>
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>save</span>
                Guardar cambios
              </button>
            </div>
          </div>
        )}
      </Modal>

      <ModalConfirmar abierto={!!aEliminar} titulo="¿Eliminar registro?"
        mensaje={aEliminar
          ? `Vas a eliminar ${idEtiqueta(aEliminar)}${aEliminar._origen === 'desaparicion' ? ` (${aEliminar.personaTexto})` : ''}.`
          : ''}
        onConfirmar={confirmarEliminar} onCancelar={() => setAEliminar(null)}
        textoConfirmar="Eliminar" peligro />
    </>
  );
}
