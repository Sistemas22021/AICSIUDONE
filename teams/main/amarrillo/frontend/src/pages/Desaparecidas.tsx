import { useEffect, useMemo, useState } from 'react';
import {
  desaparecidaService, ubicacionService,
} from '../services/api';
import type {
  PersonaDesaparecida, EstadoDesaparicion, PrioridadDesaparicion, Ubicacion,
} from '../types';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import MapaTactical, { PuntoMapa } from '../components/MapaTactical';
import ModalConfirmar from '../components/ModalConfirmar';
import Modal from '../components/Modal';
import ModalDesaparecida from '../components/ModalDesaparecida';
import FormularioDesaparecida from '../components/FormularioDesaparecida';
import { exportarCSV } from '../services/exportar';
import { fileUrl } from '../services/files';

const estadoLabel: Record<EstadoDesaparicion, string> = {
  BUSCADA: 'Buscada',
  ENCONTRADA_VIVA: 'Encontrada con vida',
  ENCONTRADA_FALLECIDA: 'Encontrada fallecida',
  ARCHIVADA: 'Archivada',
};

const prioridadLabel: Record<PrioridadDesaparicion, string> = {
  CRITICA: 'Crítica',
  ALTA: 'Alta',
  MEDIA: 'Media',
  BAJA: 'Baja',
};

const prioridadColor: Record<PrioridadDesaparicion, string> = {
  CRITICA: '#DC2626',
  ALTA: '#F59E0B',
  MEDIA: '#3B82F6',
  BAJA: '#64748B',
};

const calcularDiasDesaparicion = (fecha: string): number => {
  const desde = new Date(fecha).getTime();
  const ahora = Date.now();
  return Math.floor((ahora - desde) / (1000 * 60 * 60 * 24));
};

const iniciales = (n: string, a: string) =>
  `${n.charAt(0)}${a.charAt(0)}`.toUpperCase();

export default function Desaparecidas() {
  const [lista, setLista] = useState<PersonaDesaparecida[]>([]);
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);
  const [filtroEstado, setFiltroEstado] = useState<string>('');
  const [filtroPrioridad, setFiltroPrioridad] = useState<string>('');
  const [filtro, setFiltro] = useState('');
  const [vista, setVista] = useState<'galeria' | 'tabla'>('galeria');

  // Modales
  const [formAbierto, setFormAbierto] = useState(false);
  const [editando, setEditando] = useState<PersonaDesaparecida | null>(null);
  const [detalle, setDetalle] = useState<PersonaDesaparecida | null>(null);
  const [aEliminar, setAEliminar] = useState<PersonaDesaparecida | null>(null);

  const cargar = async () => {
    try {
      const data = await desaparecidaService.listar();
      setLista(data);
    } catch (e) { console.error(e); }
    try {
      const ubis = await ubicacionService.listar();
      setUbicaciones(ubis);
    } catch (e) { console.error(e); }
  };

  useEffect(() => { cargar(); }, []);

  const filtradas = lista.filter((p) => {
    if (filtroEstado && p.estado !== filtroEstado) return false;
    if (filtroPrioridad && p.prioridad !== filtroPrioridad) return false;
    if (!filtro.trim()) return true;
    const q = filtro.toLowerCase();
    return (
      p.nombre.toLowerCase().includes(q) ||
      p.apellido.toLowerCase().includes(q) ||
      p.documento.toLowerCase().includes(q) ||
      p.alias?.toLowerCase().includes(q) ||
      p.circunstancias?.toLowerCase().includes(q)
    );
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtradas, vista === 'galeria' ? 12 : 10);

  const puntos: PuntoMapa[] = useMemo(
    () => filtradas
      .filter(p => p.ultimaUbicacion?.latitud && p.ultimaUbicacion?.longitud)
      .map(p => ({
        id: p.id!,
        lat: p.ultimaUbicacion!.latitud,
        lng: p.ultimaUbicacion!.longitud,
        tipo: 'DESAPARECIDA',
        titulo: `${p.nombre} ${p.apellido}`,
        subtitulo: `Última ubicación · ${calcularDiasDesaparicion(p.fechaDesaparicion)} días`,
        sospechoso: p.prioridad === 'CRITICA' || p.prioridad === 'ALTA',
        campos: [
          { etiqueta: 'Documento', valor: p.documento },
          { etiqueta: 'Estado', valor: estadoLabel[p.estado] },
          { etiqueta: 'Prioridad', valor: prioridadLabel[p.prioridad] },
          { etiqueta: 'Fecha', valor: new Date(p.fechaDesaparicion).toLocaleDateString('es-ES') },
        ],
      })), [filtradas]);

  const buscadas = lista.filter(p => p.estado === 'BUSCADA').length;
  const criticas = lista.filter(p => p.estado === 'BUSCADA' && p.prioridad === 'CRITICA').length;
  const encontradas = lista.filter(p => p.estado === 'ENCONTRADA_VIVA').length;

  const exportar = () => {
    exportarCSV(
      filtradas.map(p => ({
        Documento: p.documento,
        Nombre: p.nombre,
        Apellido: p.apellido,
        Estado: estadoLabel[p.estado],
        Prioridad: prioridadLabel[p.prioridad],
        FechaDesaparicion: new Date(p.fechaDesaparicion).toLocaleString('es-ES'),
        Dias: calcularDiasDesaparicion(p.fechaDesaparicion),
        UltimaUbicacion: p.ultimaUbicacion?.direccion || '',
        Reportante: p.reportanteNombre || '',
        Telefono: p.reportanteTelefono || '',
      })),
      'desaparecidas'
    );
  };

  const confirmarEliminar = async () => {
    if (!aEliminar) return;
    try {
      await desaparecidaService.eliminar(aEliminar.id!);
      setAEliminar(null);
      await cargar();
    } catch (e) { console.error(e); }
  };

  const onGuardado = async () => {
    setFormAbierto(false);
    setEditando(null);
    await cargar();
  };

  const editar = (p: PersonaDesaparecida) => {
    setEditando(p);
    setFormAbierto(true);
    setDetalle(null);
  };

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Personas Desaparecidas</h1>
          <p className="page-subtitle">
            Registro y seguimiento activo de casos de desaparición.
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">TOTAL: {lista.length}</span>
          <span className="badge-pill alerta">BUSCADAS: {buscadas}</span>
          {criticas > 0 && <span className="badge-pill alerta">CRÍTICAS: {criticas}</span>}
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-primary" onClick={() => { setEditando(null); setFormAbierto(true); }}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>person_add</span>
          Reportar desaparición
        </button>
        <button className="btn-ghost" onClick={exportar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>download</span>
          Exportar CSV
        </button>
        <div style={{ marginLeft: 'auto', display: 'flex', gap: 4, padding: 4, background: 'var(--slate-900)', border: '1px solid var(--slate-800)' }}>
          <button
            className={`btn-ghost ${vista === 'galeria' ? 'active' : ''}`}
            onClick={() => setVista('galeria')}
            style={{ fontSize: 11 }}>
            <span className="material-symbols-outlined" style={{ fontSize: 14 }}>grid_view</span>
            Galería
          </button>
          <button
            className={`btn-ghost ${vista === 'tabla' ? 'active' : ''}`}
            onClick={() => setVista('tabla')}
            style={{ fontSize: 11 }}>
            <span className="material-symbols-outlined" style={{ fontSize: 14 }}>list</span>
            Tabla
          </button>
        </div>
      </div>

      {/* Mini stats */}
      <div className="mini-stats" style={{ marginBottom: 20 }}>
        <div className="mini-stat" style={{ borderLeft: '4px solid var(--red-500)' }}>
          <div className="mini-stat-label">Casos activos</div>
          <div className="mini-stat-value danger">{buscadas}</div>
          <div className="mini-stat-change danger">Búsqueda en curso</div>
        </div>
        <div className="mini-stat">
          <div className="mini-stat-label">Crítica prioridad</div>
          <div className="mini-stat-value danger">{criticas}</div>
          <div className="mini-stat-change danger">Atención inmediata</div>
        </div>
        <div className="mini-stat">
          <div className="mini-stat-label">Encontradas con vida</div>
          <div className="mini-stat-value tertiary">{encontradas}</div>
          <div className="mini-stat-change">Casos resueltos</div>
        </div>
        <div className="mini-stat">
          <div className="mini-stat-label">Total registros</div>
          <div className="mini-stat-value">{lista.length}</div>
          <div className="mini-stat-change">Histórico</div>
        </div>
      </div>

      {/* Mapa */}
      {puntos.length > 0 && (
        <div style={{ marginBottom: 20 }}>
          <MapaTactical
            puntos={puntos}
            altura={320}
            hudLabel="Últimas ubicaciones conocidas"
            hudValor={`${puntos.length} desapariciones georreferenciadas`}
            emptyMessage="Sin ubicaciones registradas"
          />
        </div>
      )}

      {/* Filtros */}
      <div className="form-card" style={{ marginBottom: 20, padding: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
          <span className="material-symbols-outlined" style={{ color: 'var(--red-500)', fontSize: 18 }}>filter_alt</span>
          <span style={{ fontSize: 11, fontWeight: 700, color: 'white', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
            Filtros
          </span>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 10 }}>
          <select value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
            <option value="">Todos los estados</option>
            <option value="BUSCADA">Buscada</option>
            <option value="ENCONTRADA_VIVA">Encontrada con vida</option>
            <option value="ENCONTRADA_FALLECIDA">Encontrada fallecida</option>
            <option value="ARCHIVADA">Archivada</option>
          </select>
          <select value={filtroPrioridad} onChange={(e) => setFiltroPrioridad(e.target.value)}>
            <option value="">Todas las prioridades</option>
            <option value="CRITICA">Crítica</option>
            <option value="ALTA">Alta</option>
            <option value="MEDIA">Media</option>
            <option value="BAJA">Baja</option>
          </select>
          <div className="filter-input-wrap">
            <span className="material-symbols-outlined">search</span>
            <input
              type="text"
              placeholder="Buscar por nombre, documento..."
              value={filtro}
              onChange={(e) => setFiltro(e.target.value)}
            />
          </div>
        </div>
      </div>

      {/* Vista Galería */}
      {vista === 'galeria' && (
        <>
          {visibles.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: 60 }}>
              <span className="material-symbols-outlined" style={{ fontSize: 56, color: 'var(--slate-700)' }}>
                person_search
              </span>
              <div style={{ marginTop: 12, color: 'white', fontWeight: 700, fontSize: 16 }}>
                {lista.length === 0 ? 'No hay personas desaparecidas registradas' : 'Sin resultados'}
              </div>
              <div style={{ fontSize: 12, color: 'var(--slate-500)', marginTop: 6 }}>
                {lista.length === 0 ? 'Reportá un caso para comenzar el seguimiento.' : 'Probá ajustando los filtros.'}
              </div>
            </div>
          ) : (
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
              gap: 16,
            }}>
              {visibles.map(p => {
                const dias = calcularDiasDesaparicion(p.fechaDesaparicion);
                const esBuscada = p.estado === 'BUSCADA';
                return (
                  <div
                    key={p.id}
                    onClick={() => setDetalle(p)}
                    style={{
                      background: 'var(--slate-900)',
                      border: '1px solid var(--slate-800)',
                      borderLeft: `3px solid ${prioridadColor[p.prioridad]}`,
                      cursor: 'pointer',
                      transition: 'all 0.15s',
                      overflow: 'hidden',
                      position: 'relative',
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.borderColor = 'var(--red-500)'}
                    onMouseLeave={(e) => e.currentTarget.style.borderColor = 'var(--slate-800)'}
                  >
                    {/* Foto */}
                    <div style={{
                      width: '100%',
                      height: 200,
                      background: 'var(--slate-950)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      borderBottom: '1px solid var(--slate-800)',
                      position: 'relative',
                    }}>
                      {p.fotoUrl ? (
                        <img
                          src={fileUrl(p.fotoUrl)}
                          alt={`${p.nombre} ${p.apellido}`}
                          style={{
                            width: '100%',
                            height: '100%',
                            objectFit: 'cover',
                            filter: !esBuscada ? 'grayscale(60%)' : 'none',
                          }}
                        />
                      ) : (
                        <div style={{
                          width: 80,
                          height: 80,
                          borderRadius: '50%',
                          background: 'var(--slate-800)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: 'white',
                          fontWeight: 700,
                          fontSize: 28,
                        }}>
                          {iniciales(p.nombre, p.apellido)}
                        </div>
                      )}

                      {/* Badge prioridad arriba derecha */}
                      <div style={{
                        position: 'absolute',
                        top: 10,
                        right: 10,
                        background: prioridadColor[p.prioridad],
                        color: 'white',
                        fontSize: 10,
                        fontWeight: 700,
                        padding: '4px 10px',
                        textTransform: 'uppercase',
                        letterSpacing: '0.05em',
                      }}>
                        {prioridadLabel[p.prioridad]}
                      </div>

                      {/* Banner estado abajo */}
                      <div style={{
                        position: 'absolute',
                        bottom: 0,
                        left: 0,
                        right: 0,
                        background: esBuscada ? 'rgba(220, 38, 38, 0.85)' : 'rgba(15, 23, 42, 0.85)',
                        color: 'white',
                        textAlign: 'center',
                        padding: '6px 0',
                        fontSize: 10,
                        fontWeight: 700,
                        textTransform: 'uppercase',
                        letterSpacing: '0.1em',
                      }}>
                        {esBuscada ? `🔍 BUSCADA · ${dias} DÍAS` : estadoLabel[p.estado]}
                      </div>
                    </div>

                    {/* Datos */}
                    <div style={{ padding: 14 }}>
                      <div style={{ color: 'white', fontWeight: 700, fontSize: 15, marginBottom: 4 }}>
                        {p.nombre} {p.apellido}
                      </div>
                      {p.alias && (
                        <div style={{
                          color: 'var(--slate-500)',
                          fontSize: 11,
                          fontStyle: 'italic',
                          marginBottom: 6,
                        }}>
                          "{p.alias}"
                        </div>
                      )}
                      <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        fontSize: 11,
                        color: 'var(--slate-400)',
                        fontFamily: 'var(--font-mono)',
                      }}>
                        <span>#{p.documento}</span>
                        <span>
                          {new Date(p.fechaDesaparicion).toLocaleDateString('es-ES', {
                            day: '2-digit', month: 'short', year: '2-digit',
                          })}
                        </span>
                      </div>
                      {p.ultimaUbicacion && (
                        <div style={{
                          marginTop: 8,
                          paddingTop: 8,
                          borderTop: '1px solid var(--slate-800)',
                          fontSize: 11,
                          color: 'var(--slate-400)',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 4,
                        }}>
                          <span className="material-symbols-outlined" style={{ fontSize: 12, color: 'var(--red-500)' }}>place</span>
                          <span style={{
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                          }}>
                            {p.ultimaUbicacion.direccion || `Ubi #${p.ultimaUbicacion.id}`}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {total > porPagina && (
            <div style={{ marginTop: 20, background: 'var(--slate-900)', border: '1px solid var(--slate-800)' }}>
              <Paginacion total={total} pagina={pagina} porPagina={porPagina}
                onCambiar={setPagina} label="desaparecidas" />
            </div>
          )}
        </>
      )}

      {/* Vista Tabla */}
      {vista === 'tabla' && (
        <div className="table-wrap">
          <div className="table-header">
            <div className="table-header-title">
              <span className="material-symbols-outlined">list_alt</span>
              <h3>Listado de Desapariciones</h3>
            </div>
          </div>
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Foto</th>
                  <th>Documento</th>
                  <th>Nombre</th>
                  <th>Estado</th>
                  <th>Prioridad</th>
                  <th>Fecha desaparición</th>
                  <th>Días</th>
                  <th className="right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {visibles.map(p => {
                  const dias = calcularDiasDesaparicion(p.fechaDesaparicion);
                  return (
                    <tr key={p.id} style={{ borderLeft: `3px solid ${prioridadColor[p.prioridad]}` }}>
                      <td style={{ width: 50 }}>
                        {p.fotoUrl ? (
                          <img src={fileUrl(p.fotoUrl)} alt={p.nombre} style={{ width: 36, height: 36, objectFit: 'cover', borderRadius: 4 }} />
                        ) : (
                          <div className="avatar-sm">{iniciales(p.nombre, p.apellido)}</div>
                        )}
                      </td>
                      <td className="mono">#{p.documento}</td>
                      <td>
                        <div className="row-name">{p.nombre} {p.apellido}</div>
                        {p.alias && <div className="row-sub">"{p.alias}"</div>}
                      </td>
                      <td><span className="badge muted">{estadoLabel[p.estado]}</span></td>
                      <td>
                        <span className="badge" style={{ background: prioridadColor[p.prioridad], color: 'white', borderColor: prioridadColor[p.prioridad] }}>
                          {prioridadLabel[p.prioridad]}
                        </span>
                      </td>
                      <td className="mono" style={{ fontSize: 11 }}>
                        {new Date(p.fechaDesaparicion).toLocaleDateString('es-ES')}
                      </td>
                      <td>
                        <span style={{ color: dias > 30 ? 'var(--red-500)' : 'var(--slate-400)', fontWeight: 700 }}>
                          {dias} días
                        </span>
                      </td>
                      <td className="right">
                        <div className="table-actions">
                          <button className="btn-icon" onClick={() => setDetalle(p)} title="Ver detalle">
                            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>visibility</span>
                          </button>
                          <button className="btn-icon" onClick={() => editar(p)} title="Editar">
                            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>edit</span>
                          </button>
                          <button className="btn-icon danger" onClick={() => setAEliminar(p)} title="Eliminar">
                            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>delete</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
                {visibles.length === 0 && (
                  <tr><td colSpan={8} className="table-empty">Sin resultados</td></tr>
                )}
              </tbody>
            </table>
          </div>
          <Paginacion total={total} pagina={pagina} porPagina={porPagina}
            onCambiar={setPagina} label="desaparecidas" />
        </div>
      )}

      {/* Modal formulario */}
      <Modal
        abierto={formAbierto}
        onClose={() => { setFormAbierto(false); setEditando(null); }}
        titulo={editando ? 'Editar caso' : 'Reportar nueva desaparición'}
        icono="person_add"
        ancho={780}
      >
        <FormularioDesaparecida
          inicial={editando}
          ubicaciones={ubicaciones}
          onGuardado={onGuardado}
          onCancelar={() => { setFormAbierto(false); setEditando(null); }}
        />
      </Modal>

      {/* Modal detalle */}
      {detalle && (
        <ModalDesaparecida
          persona={detalle}
          abierto={!!detalle}
          onClose={() => setDetalle(null)}
          onEditar={() => editar(detalle)}
          onCambiarEstado={async (estado) => {
            await desaparecidaService.cambiarEstado(detalle.id!, estado);
            await cargar();
            setDetalle(null);
          }}
        />
      )}

      <ModalConfirmar
        abierto={!!aEliminar}
        titulo="¿Eliminar caso?"
        mensaje={
          aEliminar
            ? `Vas a eliminar el caso de ${aEliminar.nombre} ${aEliminar.apellido}. Esta acción no se puede deshacer.`
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