// frontend/src/pages/Vehiculos.tsx
import { useEffect, useState } from 'react';
import {
  personaService,
  vehiculoService,
  sucesoService,
  ubicacionService,
} from '../services/api';
import api from '../services/api';
import type {
  Persona, Vehiculo, EstadoVehiculo, Suceso, Ubicacion,
} from '../types';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import MapaTactical, { PuntoMapa } from '../components/MapaTactical';
import ModalDetalle from '../components/ModalDetalle';
import ModalConfirmar from '../components/ModalConfirmar';
import Modal from '../components/Modal';
import { exportarCSV } from '../services/exportar';
import FormVehiculo from '../components/FormVehiculo';

const ESTADOS: EstadoVehiculo[] = [
  'NORMAL', 'ROBADO', 'RECUPERADO', 'DESAPARECIDO', 'BAJO_OBSERVACION', 'VEHICULO_APOYO',
];

const ESTADOS_SELECCIONABLES: EstadoVehiculo[] = [
  'ROBADO', 'RECUPERADO', 'DESAPARECIDO', 'BAJO_OBSERVACION',
];

const COLORES: Record<string, string> = {
  Blanco: '#F1F5F9', Negro: '#1E293B', Gris: '#64748B', Rojo: '#DC2626',
  Azul: '#2563EB', Plata: '#94A3B8', Verde: '#059669', Amarillo: '#F59E0B',
};

const estadoLabel = (e?: EstadoVehiculo): string => {
  const map: Record<string, string> = {
    NORMAL: 'Normal', ROBADO: 'Robado', RECUPERADO: 'Recuperado',
    DESAPARECIDO: 'Desaparecido', BAJO_OBSERVACION: 'En observación',
    VEHICULO_APOYO: 'Vehículo apoyo',
  };
  return e ? map[e] : '—';
};

interface Avistamiento {
  id: number;
  vehiculo: Vehiculo;
  ubicacion: Ubicacion;
  fechaHora: string;
  fuente?: string;
}

export default function Vehiculos() {
  const [lista, setLista] = useState<Vehiculo[]>([]);
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);
  const [sucesos, setSucesos] = useState<Suceso[]>([]);
  const [puntos, setPuntos] = useState<PuntoMapa[]>([]);
  const [filtro, setFiltro] = useState('');
  const [filtroEstado, setFiltroEstado] = useState<string>('');
  const [err, setErr] = useState('');
  const [ok, setOk] = useState('');
  const [detalle, setDetalle] = useState<Vehiculo | null>(null);
  const [aEliminar, setAEliminar] = useState<Vehiculo | null>(null);
  const [editando, setEditando] = useState<Vehiculo | null>(null);
  const [avistamientosVehiculo, setAvistamientosVehiculo] = useState<Avistamiento[]>([]);
  const [avAbierto, setAvAbierto] = useState(false);
  const [avForm, setAvForm] = useState<{ ubicacionId: string; fechaHora: string; fuente: string }>({
    ubicacionId: '',
    fechaHora: new Date().toISOString().slice(0, 16),
    fuente: 'CAMARA_URBANA',
  });

  const cargar = async () => {
    let sList: Suceso[] = [];
    try {
      setLista(await vehiculoService.listar());
    } catch (e) {
      console.error('Error cargando vehiculos:', e);
      setLista([]);
    }
    try {
      setUbicaciones(await ubicacionService.listar());
    } catch (e) {
      console.error('Error cargando ubicaciones:', e);
      setUbicaciones([]);
    }
    try {
      sList = await sucesoService.listar();
      setSucesos(sList);
    } catch (e) {
      console.error('Error cargando sucesos:', e);
      setSucesos([]);
    }

    // Construir puntos del mapa
    const ptos: PuntoMapa[] = [];
    sList.forEach((suc) => {
      if (suc.tipo === 'ROBO_VEHICULO' && suc.ubicacion && suc.vehiculo) {
        ptos.push({
          id: `s-${suc.id}`,
          lat: suc.ubicacion.latitud,
          lng: suc.ubicacion.longitud,
          tipo: 'VEHICULO_ROBADO',
          titulo: `${suc.vehiculo.placa} — ${suc.vehiculo.marca} ${suc.vehiculo.modelo}`,
          subtitulo: 'Ubicación del robo',
          sospechoso: true,
          campos: [
            { etiqueta: 'Suceso', valor: `EV-${String(suc.id).padStart(4, '0')}` },
            { etiqueta: 'Fecha', valor: new Date(suc.fechaHora).toLocaleDateString('es-ES') },
            { etiqueta: 'Modus', valor: suc.modusOperandi || '—' },
            { etiqueta: 'Dirección', valor: suc.ubicacion.direccion || '—' },
          ],
        });
      }
    });
    setPuntos(ptos);
  };

  useEffect(() => {
    cargar();
  }, []);

  const cambiarEstado = async (id: number, estado: EstadoVehiculo) => {
    await vehiculoService.cambiarEstado(id, estado);
    await cargar();
  };

  const confirmarEliminar = async () => {
    if (!aEliminar) return;
    try {
      await vehiculoService.eliminar(aEliminar.id!);
      setAEliminar(null);
      setOk('Vehículo eliminado');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo eliminar');
      setAEliminar(null);
    }
  };

  const verDetalle = async (v: Vehiculo) => {
    setDetalle(v);
    try {
      const { data } = await api.get(`/avistamientos/vehiculo/${v.id}`);
      setAvistamientosVehiculo(data);
    } catch {
      setAvistamientosVehiculo([]);
    }
  };

  const crearAvistamiento = async () => {
    if (!detalle || !avForm.ubicacionId) return;
    try {
      await api.post('/avistamientos', {
        vehiculo: { id: detalle.id },
        ubicacion: { id: Number(avForm.ubicacionId) },
        fechaHora: avForm.fechaHora,
        fuente: avForm.fuente,
      });
      setAvAbierto(false);
      setAvForm({
        ubicacionId: '',
        fechaHora: new Date().toISOString().slice(0, 16),
        fuente: 'CAMARA_URBANA',
      });
      const { data } = await api.get(`/avistamientos/vehiculo/${detalle.id}`);
      setAvistamientosVehiculo(data);
      setOk('Avistamiento registrado');
      setTimeout(() => setOk(''), 3000);
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo registrar el avistamiento');
    }
  };

  const exportar = () => {
    exportarCSV(
      filtrados.map((v) => ({
        Placa: v.placa,
        Marca: v.marca,
        Modelo: v.modelo,
        Anio: v.anio || '',
        Color: v.color || '',
        Chasis: v.chasis || '',
        Estado: estadoLabel(v.estado),
        Propietario: v.propietario ? `${v.propietario.nombre} ${v.propietario.apellido}` : '',
      })),
      'vehiculos'
    );
  };

  const badgeClase = (e?: EstadoVehiculo) => {
    if (e === 'ROBADO' || e === 'DESAPARECIDO') return 'sospechoso';
    if (e === 'VEHICULO_APOYO' || e === 'BAJO_OBSERVACION') return 'apoyo';
    if (e === 'RECUPERADO') return 'normal';
    return 'muted';
  };

  const filtrados = lista.filter((v) => {
    if (filtroEstado && v.estado !== filtroEstado) return false;
    if (!filtro.trim()) return true;
    const q = filtro.toLowerCase();
    return (
      v.placa.toLowerCase().includes(q) ||
      v.marca.toLowerCase().includes(q) ||
      v.modelo.toLowerCase().includes(q) ||
      v.propietario?.nombre?.toLowerCase().includes(q) ||
      v.propietario?.apellido?.toLowerCase().includes(q)
    );
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtrados, 10);

  const robados = lista.filter((v) => v.estado === 'ROBADO').length;
  const apoyo = lista.filter((v) => v.estado === 'VEHICULO_APOYO').length;

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Inteligencia Vehicular</h1>
          <p className="page-subtitle">
            Base de datos de vehículos registrados y monitoreo en tiempo real.
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill live">Monitoreo activo</span>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-ghost" onClick={exportar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>download</span>
          Exportar CSV
        </button>
      </div>

      {ok && <div className="success" style={{ marginBottom: 16 }}>{ok}</div>}
      {err && <div className="error" style={{ marginBottom: 16 }}>{err}</div>}

      {/* Mapa + stats (sin formulario: los vehiculos se crean desde Sucesos) */}
      <div className="bento-grid">
        <div className="bento-col-12" style={{ gridColumn: '1 / -1' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
            <MapaTactical
              puntos={puntos}
              altura={300}
              hudLabel="Mapa de robos vehiculares"
              hudValor={`${puntos.length} puntos detectados`}
              emptyMessage="Sin robos georreferenciados"
            />
            <div className="mini-stats">
              <div className="mini-stat">
                <div className="mini-stat-label">Flota total</div>
                <div className="mini-stat-value">{lista.length}</div>
                <div className="mini-stat-change">Registros vigentes</div>
              </div>
              <div className="mini-stat">
                <div className="mini-stat-label">Reportes de robo</div>
                <div className="mini-stat-value danger">{robados}</div>
                <div className="mini-stat-change danger">Urgente</div>
              </div>
              <div className="mini-stat">
                <div className="mini-stat-label">En observación</div>
                <div className="mini-stat-value tertiary">{apoyo}</div>
                <div className="mini-stat-change">Vigilancia</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Tabla */}
      <div className="table-wrap" style={{ marginTop: 20 }}>
        <div className="table-header">
          <div className="table-header-title">
            <span className="material-symbols-outlined">database</span>
            <h3>Listado de Vehículos Registrados</h3>
          </div>
          <div className="table-header-actions">
            <select
              value={filtroEstado}
              onChange={(e) => setFiltroEstado(e.target.value)}
              style={{ width: 160, fontSize: 11, padding: '6px 24px 6px 10px' }}
            >
              <option value="">Todos los estados</option>
              {ESTADOS.map((e) => (
                <option key={e} value={e}>
                  {estadoLabel(e as EstadoVehiculo)}
                </option>
              ))}
            </select>
            <div className="filter-input-wrap">
              <span className="material-symbols-outlined">filter_list</span>
              <input
                type="text"
                placeholder="Buscar placa, marca, propietario..."
                value={filtro}
                onChange={(e) => setFiltro(e.target.value)}
              />
            </div>
          </div>
        </div>
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>Placa</th>
                <th>Marca / Modelo</th>
                <th>Año</th>
                <th>Color</th>
                <th>Estado</th>
                <th>Propietario</th>
                <th className="right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map((v) => (
                <tr
                  key={v.id}
                  style={
                    v.estado === 'ROBADO'
                      ? { borderLeft: '3px solid var(--red-500)' }
                      : undefined
                  }
                >
                  <td className="mono" style={{ fontWeight: 700 }}>
                    {v.placa}
                  </td>
                  <td>
                    <div className="row-avatar">
                      <div className={`avatar-sm vehiculo ${v.estado === 'ROBADO' ? 'robado' : ''}`}>
                        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                          directions_car
                        </span>
                      </div>
                      <div>
                        <div className="row-name">
                          {v.marca} {v.modelo}
                        </div>
                        <div className="row-sub">
                          {v.estado === 'ROBADO' ? 'REPORTE DE ROBO' : 'VEHÍCULO'}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td style={{ color: 'var(--slate-300)' }}>{v.anio || '—'}</td>
                  <td>
                    {v.color ? (
                      <>
                        <span
                          className="color-dot"
                          style={{ background: COLORES[v.color] || '#94A3B8' }}
                        ></span>
                        <span style={{ fontSize: 12, color: 'var(--slate-400)' }}>{v.color}</span>
                      </>
                    ) : (
                      '—'
                    )}
                  </td>
                  <td>
                    <span className={`badge ${badgeClase(v.estado)}`}>{estadoLabel(v.estado)}</span>
                  </td>
                  <td style={{ color: 'var(--slate-400)', fontSize: 12 }}>
                    {v.propietario
                      ? `${v.propietario.nombre} ${v.propietario.apellido}`
                      : <em style={{ color: 'var(--slate-600)' }}>Sin asignar</em>}
                  </td>
                  <td className="right">
                    <div className="table-actions" style={{ gap: 8 }}>
                      <button
                        className="btn-icon"
                        onClick={() => verDetalle(v)}
                        title="Ver detalle"
                      >
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                          visibility
                        </span>
                      </button>
                      <button
                        className="btn-icon"
                        onClick={() => setEditando(v)}
                        title="Editar"
                      >
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                          edit
                        </span>
                      </button>
                      <select
                        value={v.estado}
                        onChange={(e) => cambiarEstado(v.id!, e.target.value as EstadoVehiculo)}
                        style={{ width: 140, fontSize: 11, padding: '6px 24px 6px 10px' }}
                      >
                        {Array.from(new Set([
                          ...(v.estado ? [v.estado] : []),
                          ...ESTADOS_SELECCIONABLES,
                        ])).map((e) => (
                          <option key={e} value={e}>
                            {estadoLabel(e as EstadoVehiculo)}
                          </option>
                        ))}
                      </select>
                      <button
                        className="btn-icon danger"
                        onClick={() => setAEliminar(v)}
                        title="Eliminar"
                      >
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                          delete
                        </span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {visibles.length === 0 && (
                <tr>
                  <td colSpan={7} className="table-empty">
                    {filtro || filtroEstado ? 'Sin resultados' : 'Sin vehículos registrados'}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        <Paginacion
          total={total}
          pagina={pagina}
          porPagina={porPagina}
          onCambiar={setPagina}
          label="vehículos"
        />
      </div>

      {/* Modal Editar vehiculo */}
      <Modal
        abierto={editando !== null}
        onClose={() => setEditando(null)}
        titulo="Editar vehículo"
        icono="edit"
        ancho={620}
      >
        <FormVehiculo
          key={editando?.id ?? 'editar'}
          vehiculoInicial={editando}
          mostrarPropietario
          onGuardado={() => {
            setEditando(null);
            setOk('Vehículo actualizado correctamente');
            setTimeout(() => setOk(''), 3000);
            cargar();
          }}
          onCancelar={() => setEditando(null)}
          textoGuardar="Actualizar registro"
        />
      </Modal>

      {/* Modal Detalle Vehículo */}
      {detalle && (
        <ModalDetalle
          abierto={!!detalle}
          onClose={() => {
            setDetalle(null);
            setAvistamientosVehiculo([]);
          }}
          titulo={detalle.placa}
          subtitulo={`${detalle.marca} ${detalle.modelo}`}
          icono="directions_car"
          avatar={
            <div
              className="dossier-avatar"
              style={{
                background: detalle.estado === 'ROBADO' ? 'var(--red-600)' : 'var(--amber)',
              }}
            >
              <span className="material-symbols-outlined">directions_car</span>
            </div>
          }
          campos={[
            { etiqueta: 'Placa', valor: detalle.placa, mono: true, destacado: true },
            { etiqueta: 'Chasis', valor: detalle.chasis || '—', mono: true },
            { etiqueta: 'Marca', valor: detalle.marca },
            { etiqueta: 'Modelo', valor: detalle.modelo },
            { etiqueta: 'Año', valor: detalle.anio?.toString() || '—' },
            { etiqueta: 'Color', valor: detalle.color || '—' },
            {
              etiqueta: 'Estado',
              valor: estadoLabel(detalle.estado),
              destacado: detalle.estado === 'ROBADO',
            },
            {
              etiqueta: 'Propietario',
              valor: detalle.propietario
                ? `${detalle.propietario.nombre} ${detalle.propietario.apellido}`
                : 'Sin asignar',
            },
            { etiqueta: 'Avistamientos', valor: avistamientosVehiculo.length.toString() },
          ]}
          extra={
            <div>
              {detalle.declaracion && (
                <div style={{ marginBottom: 16 }}>
                  <h4 style={{
                    fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase',
                    letterSpacing: '0.1em', margin: '0 0 8px',
                  }}>
                    Declaración
                  </h4>
                  <div style={{
                    padding: 12, background: 'var(--slate-950)',
                    border: '1px solid var(--slate-800)', fontSize: 13,
                    color: 'var(--slate-300)', lineHeight: 1.6,
                  }}>
                    {detalle.declaracion}
                  </div>
                </div>
              )}
              <h4
                style={{
                  fontSize: 11,
                  color: 'var(--slate-500)',
                  textTransform: 'uppercase',
                  letterSpacing: '0.1em',
                  margin: '0 0 10px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <span>Historial de avistamientos</span>
                <button
                  className="btn-ghost"
                  style={{ fontSize: 10, padding: '4px 10px' }}
                  onClick={() => setAvAbierto(true)}
                >
                  <span className="material-symbols-outlined" style={{ fontSize: 12 }}>
                    add
                  </span>
                  Registrar avistamiento
                </button>
              </h4>
              {avistamientosVehiculo.length === 0 ? (
                <div
                  style={{
                    padding: 16,
                    textAlign: 'center',
                    background: 'var(--slate-950)',
                    border: '1px solid var(--slate-800)',
                    color: 'var(--slate-500)',
                    fontSize: 12,
                  }}
                >
                  Sin avistamientos registrados
                </div>
              ) : (
                avistamientosVehiculo.map((av) => (
                  <div
                    key={av.id}
                    style={{
                      padding: 10,
                      background: 'var(--slate-950)',
                      border: '1px solid var(--slate-800)',
                      marginBottom: 6,
                      fontSize: 11,
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'white', fontWeight: 600 }}>
                        {av.ubicacion?.direccion || `Ubicación #${av.ubicacion?.id}`}
                      </span>
                      <span className="badge muted">{av.fuente || 'Manual'}</span>
                    </div>
                    <div
                      style={{
                        color: 'var(--slate-500)',
                        fontFamily: 'var(--font-mono)',
                        marginTop: 4,
                      }}
                    >
                      {new Date(av.fechaHora).toLocaleString('es-ES')}
                    </div>
                  </div>
                ))
              )}
            </div>
          }
          acciones={
            <button
              className="btn-secondary"
              onClick={() => {
                setDetalle(null);
                setAvistamientosVehiculo([]);
              }}
            >
              Cerrar
            </button>
          }
        />
      )}

      {/* Modal Crear avistamiento */}
      <Modal
        abierto={avAbierto}
        onClose={() => setAvAbierto(false)}
        titulo="Registrar avistamiento"
        icono="visibility"
        ancho={520}
      >
        {detalle && (
          <>
            <div
              style={{
                padding: 12,
                background: 'var(--slate-950)',
                border: '1px solid var(--slate-800)',
                marginBottom: 16,
              }}
            >
              <div style={{ color: 'white', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>
                {detalle.placa}
              </div>
              <div style={{ fontSize: 11, color: 'var(--slate-500)', marginTop: 2 }}>
                {detalle.marca} {detalle.modelo}
              </div>
            </div>
            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Ubicación del avistamiento</label>
              <select
                value={avForm.ubicacionId}
                onChange={(e) => setAvForm({ ...avForm, ubicacionId: e.target.value })}
              >
                <option value="">— Seleccionar —</option>
                {ubicaciones.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.direccion || `Ubi #${u.id}`}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)' }}>
              <div className="form-group">
                <label className="form-label">Fecha y hora</label>
                <input
                  type="datetime-local"
                  value={avForm.fechaHora}
                  onChange={(e) => setAvForm({ ...avForm, fechaHora: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Fuente</label>
                <select
                  value={avForm.fuente}
                  onChange={(e) => setAvForm({ ...avForm, fuente: e.target.value })}
                >
                  <option value="CAMARA_URBANA">Cámara urbana</option>
                  <option value="LECTOR_PATENTES">Lector de patentes</option>
                  <option value="REPORTE_CIUDADANO">Reporte ciudadano</option>
                  <option value="PATRULLA">Patrulla</option>
                  <option value="OTRO">Otro</option>
                </select>
              </div>
            </div>
            <div
              style={{
                display: 'flex',
                gap: 8,
                justifyContent: 'flex-end',
                marginTop: 20,
                paddingTop: 16,
                borderTop: '1px solid var(--slate-800)',
              }}
            >
              <button className="btn-ghost" onClick={() => setAvAbierto(false)}>
                Cancelar
              </button>
              <button
                className="btn-primary"
                onClick={crearAvistamiento}
                disabled={!avForm.ubicacionId}
              >
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                  save
                </span>
                Registrar
              </button>
            </div>
          </>
        )}
      </Modal>

      <ModalConfirmar
        abierto={!!aEliminar}
        titulo="¿Eliminar vehículo?"
        mensaje={
          aEliminar
            ? `Vas a eliminar ${aEliminar.placa} (${aEliminar.marca} ${aEliminar.modelo}). Esta acción no se puede deshacer.`
            : ''
        }
        onConfirmar={confirmarEliminar}
        onCancelar={() => setAEliminar(null)}
        textoConfirmar="Eliminar"
        peligro
      />
    </>
  );
}
