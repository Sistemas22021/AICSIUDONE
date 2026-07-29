// frontend/src/pages/Personas.tsx
import { useEffect, useState } from 'react';
import { personaService, relacionService } from '../services/api';
import type { Persona, RolPersona, Relacion, TipoRelacion } from '../types';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import ModalDetalle from '../components/ModalDetalle';
import ModalConfirmar from '../components/ModalConfirmar';
import Modal from '../components/Modal';
import { exportarCSV } from '../services/exportar';
import FormPersona from '../components/FormPersona';
import { useAuth } from '../services/AuthContext';
import { puedeEditar, puedeEliminar } from '../services/permisos';

const ROLES: RolPersona[] = ['VICTIMA', 'SOSPECHOSO', 'TESTIGO', 'PROPIETARIO', 'INTERMEDIARIO'];

const TIPOS_REL: TipoRelacion[] = [
  'FAMILIAR', 'AMIGO', 'LABORAL', 'CONTACTO_TELEFONICO', 'REDES_SOCIALES', 'OTRO',
];

const rolBadge = (rol: RolPersona) => {
  const m: Record<string, string> = {
    VICTIMA: 'muted',
    SOSPECHOSO: 'robado',
    TESTIGO: 'normal',
    PROPIETARIO: 'normal',
    INTERMEDIARIO: 'apoyo',
  };
  return m[rol] || 'muted';
};

const rolLabel: Record<string, string> = {
  VICTIMA: 'Víctima',
  SOSPECHOSO: 'Sospechoso',
  TESTIGO: 'Testigo',
  PROPIETARIO: 'Propietario',
  INTERMEDIARIO: 'Intermediario',
};

const relLabel: Record<string, string> = {
  FAMILIAR: 'Familiar',
  AMIGO: 'Amigo',
  LABORAL: 'Laboral',
  CONTACTO_TELEFONICO: 'Contacto telefónico',
  REDES_SOCIALES: 'Redes sociales',
  OTRO: 'Otro',
};

const iniciales = (n: string, a: string) =>
  `${n.charAt(0)}${a.charAt(0)}`.toUpperCase();

export default function Personas() {
  const { user } = useAuth();

  const [lista, setLista] = useState<Persona[]>([]);
  const [relaciones, setRelaciones] = useState<Relacion[]>([]);
  const [filtro, setFiltro] = useState('');
  const [filtroRol, setFiltroRol] = useState<string>('');
  const [form, setForm] = useState<Persona>({
    documento: '',
    nombre: '',
    apellido: '',
    rol: 'VICTIMA',
  });
  const [err, setErr] = useState('');
  const [ok, setOk] = useState('');
  const [editandoId, setEditandoId] = useState<number | null>(null);

  // Modales
  const [detalle, setDetalle] = useState<Persona | null>(null);
  const [aEliminar, setAEliminar] = useState<Persona | null>(null);
  const [intermediariosAbierto, setIntermediariosAbierto] = useState(false);
  const [vId, setVId] = useState<string>('');
  const [sId, setSId] = useState<string>('');
  const [resultadoIntermediarios, setResultadoIntermediarios] = useState<Persona[] | null>(null);
  const [busqIntermediarios, setBusqIntermediarios] = useState(false);

  // Crear relación
  const [relacionAbierto, setRelacionAbierto] = useState(false);
  const [personaParaRelacion, setPersonaParaRelacion] = useState<Persona | null>(null);
  const [relForm, setRelForm] = useState<{ otraId: string; tipo: TipoRelacion; peso: number }>({
    otraId: '',
    tipo: 'FAMILIAR',
    peso: 3,
  });

  const cargar = async () => {
    setLista(await personaService.listar());
    try {
      setRelaciones(await relacionService.listar());
    } catch {
      // El endpoint puede fallar si está vacío
    }
  };

  useEffect(() => {
    cargar();
  }, []);

  const editar = (p: Persona) => {
    setEditandoId(p.id!);
    setForm(p);
    setErr('');
    setOk('');
  };

  const cancelar = () => {
    setEditandoId(null);
    setForm({ documento: '', nombre: '', apellido: '', rol: 'VICTIMA' });
  };

  const confirmarEliminar = async () => {
    if (!aEliminar) return;
    try {
      await personaService.eliminar(aEliminar.id!);
      setAEliminar(null);
      setOk('Persona eliminada');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo eliminar');
      setAEliminar(null);
    }
  };

  const buscarIntermediarios = async () => {
    if (!vId || !sId) return;
    setBusqIntermediarios(true);
    try {
      const r = await personaService.intermediarios(Number(vId), Number(sId));
      setResultadoIntermediarios(r);
    } catch {
      setResultadoIntermediarios([]);
    } finally {
      setBusqIntermediarios(false);
    }
  };

  const crearRelacion = async () => {
    if (!personaParaRelacion || !relForm.otraId) return;
    const otra = lista.find((p) => p.id === Number(relForm.otraId));
    if (!otra) return;
    try {
      await relacionService.crear({
        personaA: personaParaRelacion,
        personaB: otra,
        tipoRelacion: relForm.tipo,
        peso: relForm.peso,
      });
      setRelacionAbierto(false);
      setPersonaParaRelacion(null);
      setRelForm({ otraId: '', tipo: 'FAMILIAR', peso: 3 });
      setOk('Relación creada correctamente');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al crear relación');
    }
  };

  const exportar = () => {
    exportarCSV(
      filtradas.map((p) => ({
        Documento: p.documento,
        Nombre: p.nombre,
        Apellido: p.apellido,
        Alias: p.alias || '',
        Rol: rolLabel[p.rol],
        Telefono: p.telefono || '',
      })),
      'personas',
      [
        { campo: 'Documento', etiqueta: 'Documento' },
        { campo: 'Nombre', etiqueta: 'Nombre' },
        { campo: 'Apellido', etiqueta: 'Apellido' },
        { campo: 'Alias', etiqueta: 'Alias' },
        { campo: 'Rol', etiqueta: 'Rol' },
        { campo: 'Telefono', etiqueta: 'Teléfono' },
      ]
    );
  };

  // Filtros combinados
  const filtradas = lista.filter((p) => {
    if (filtroRol && p.rol !== filtroRol) return false;
    if (!filtro.trim()) return true;
    const q = filtro.toLowerCase();
    return (
      p.nombre.toLowerCase().includes(q) ||
      p.apellido.toLowerCase().includes(q) ||
      p.documento.toLowerCase().includes(q) ||
      p.alias?.toLowerCase().includes(q) ||
      p.rol.toLowerCase().includes(q)
    );
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtradas, 10);

  // Helpers para detalle
  const relacionesDePersona = (id: number) =>
    relaciones.filter((r) => r.personaA?.id === id || r.personaB?.id === id);

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Gestión de Personas</h1>
          <p className="page-subtitle">
            Base de datos centralizada para identificación y asignación de roles.
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">REGISTROS: {lista.length}</span>
          <span className="badge-pill alerta">
            SOSPECHOSOS: {lista.filter((p) => p.rol === 'SOSPECHOSO').length}
          </span>
        </div>
      </div>

      {/* Toolbar de acciones avanzadas */}
      <div className="toolbar">
        <button className="btn-secondary" onClick={() => setIntermediariosAbierto(true)}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            person_search
          </span>
          Buscar intermediarios
        </button>
        <button className="btn-ghost" onClick={exportar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            download
          </span>
          Exportar CSV
        </button>
      </div>

      {ok && <div className="success" style={{ marginBottom: 16 }}>{ok}</div>}
      {err && <div className="error" style={{ marginBottom: 16 }}>{err}</div>}

      {/* Tabla */}
      <div className="table-wrap">
        <div className="table-header">
          <div className="table-header-title">
            <span className="material-symbols-outlined">database</span>
            <h3>Personas Registradas</h3>
          </div>
          <div className="table-header-actions">
            <select
              value={filtroRol}
              onChange={(e) => setFiltroRol(e.target.value)}
              style={{ width: 160, fontSize: 11, padding: '6px 24px 6px 10px' }}
            >
              <option value="">Todos los roles</option>
              {ROLES.map((r) => (
                <option key={r} value={r}>
                  {rolLabel[r]}
                </option>
              ))}
            </select>
            <div className="filter-input-wrap">
              <span className="material-symbols-outlined">filter_list</span>
              <input
                type="text"
                placeholder="Filtrar resultados..."
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
                <th>Documento</th>
                <th>Nombre</th>
                <th>Alias</th>
                <th>Rol</th>
                <th>Teléfono</th>
                <th>Vínculos</th>
                <th className="right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map((p) => (
                <tr key={p.id}>
                  <td className="mono">#{p.documento}</td>
                  <td>
                    <div className="row-avatar">
                      <div className="avatar-sm">{iniciales(p.nombre, p.apellido)}</div>
                      <div>
                        <div className="row-name">
                          {p.nombre} {p.apellido}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td style={{ fontStyle: 'italic', color: 'var(--slate-500)' }}>
                    {p.alias ? `"${p.alias}"` : '—'}
                  </td>
                  <td>
                    <span className={`badge ${rolBadge(p.rol)}`}>{rolLabel[p.rol]}</span>
                  </td>
                  <td className="mono" style={{ color: 'var(--slate-400)', fontSize: 11 }}>
                    {p.telefono || '—'}
                  </td>
                  <td style={{ fontSize: 11, color: 'var(--slate-400)' }}>
                    {relacionesDePersona(p.id!).length} relaciones
                  </td>
                  <td className="right">
                    <div className="table-actions">
                      <button
                        className="btn-icon"
                        onClick={() => setDetalle(p)}
                        title="Ver detalle"
                      >
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                          visibility
                        </span>
                      </button>
                      {puedeEditar(user?.rol) && (
                        <button
                          className="btn-icon"
                          onClick={() => {
                            setPersonaParaRelacion(p);
                            setRelacionAbierto(true);
                          }}
                          title="Crear relación"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                            group_add
                          </span>
                        </button>
                      )}
                      {puedeEditar(user?.rol) && (
                        <button
                          className="btn-icon"
                          onClick={() => editar(p)}
                          title="Editar"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                            edit
                          </span>
                        </button>
                      )}
                      {puedeEliminar(user?.rol) && (
                        <button
                          className="btn-icon danger"
                          onClick={() => setAEliminar(p)}
                          title="Eliminar"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                            delete
                          </span>
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {visibles.length === 0 && (
                <tr>
                  <td colSpan={7} className="table-empty">
                    {filtro || filtroRol ? 'Sin resultados' : 'Sin personas registradas'}
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
          label="personas"
        />
      </div>

      {/* Modal Detalle */}
      {detalle && (
        <ModalDetalle
          abierto={!!detalle}
          onClose={() => setDetalle(null)}
          titulo={`${detalle.nombre} ${detalle.apellido}`}
          subtitulo={rolLabel[detalle.rol]}
          icono="person"
          avatar={
            <div
              className="dossier-avatar"
              style={{
                background:
                  detalle.rol === 'SOSPECHOSO' ? 'var(--red-600)' : 'var(--slate-700)',
              }}
            >
              {iniciales(detalle.nombre, detalle.apellido)}
            </div>
          }
          campos={[
            { etiqueta: 'Documento', valor: detalle.documento, mono: true },
            { etiqueta: 'Alias', valor: detalle.alias || '—' },
            { etiqueta: 'Teléfono', valor: detalle.telefono || '—', mono: true },
            {
              etiqueta: 'Fecha nacimiento',
              valor: detalle.fechaNacimiento || '—',
            },
            {
              etiqueta: 'Rol',
              valor: rolLabel[detalle.rol],
              destacado: detalle.rol === 'SOSPECHOSO',
            },
            {
              etiqueta: 'Vínculos sociales',
              valor: `${relacionesDePersona(detalle.id!).length} relaciones`,
            },
          ]}
          extra={
            relacionesDePersona(detalle.id!).length > 0 && (
              <div style={{ marginBottom: 16 }}>
                <h4
                  style={{
                    fontSize: 11,
                    color: 'var(--slate-500)',
                    textTransform: 'uppercase',
                    letterSpacing: '0.1em',
                    margin: '0 0 10px',
                  }}
                >
                  Red de relaciones
                </h4>
                {relacionesDePersona(detalle.id!).map((r) => {
                  const otro = r.personaA?.id === detalle.id ? r.personaB : r.personaA;
                  return (
                    <div
                      key={r.id}
                      style={{
                        padding: 8,
                        background: 'var(--slate-950)',
                        border: '1px solid var(--slate-800)',
                        marginBottom: 6,
                        display: 'flex',
                        justifyContent: 'space-between',
                        fontSize: 12,
                      }}
                    >
                      <span style={{ color: 'white' }}>
                        {otro?.nombre} {otro?.apellido}
                      </span>
                      <span className="badge muted">{relLabel[r.tipoRelacion]}</span>
                    </div>
                  );
                })}
              </div>
            )
          }
          acciones={
            puedeEditar(user?.rol) ? (
              <>
                <button
                  className="btn-ghost"
                  onClick={() => {
                    editar(detalle);
                    setDetalle(null);
                  }}
                >
                  <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                    edit
                  </span>
                  Editar
                </button>
                <button
                  className="btn-secondary"
                  onClick={() => {
                    setPersonaParaRelacion(detalle);
                    setDetalle(null);
                    setRelacionAbierto(true);
                  }}
                >
                  <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                    group_add
                  </span>
                  Crear relación
                </button>
              </>
            ) : null
          }
        />
      )}

      {/* Modal Editar persona */}
      <Modal
        abierto={editandoId !== null}
        onClose={cancelar}
        titulo="Editar persona"
        icono="edit"
        ancho={620}
      >
        <FormPersona
          key={editandoId ?? 'editar'}
          personaInicial={editandoId ? form : null}
          onGuardado={() => {
            setOk('Persona actualizada correctamente');
            setEditandoId(null);
            cargar();
            setTimeout(() => setOk(''), 3000);
          }}
          onCancelar={cancelar}
          textoGuardar="Actualizar registro"
        />
      </Modal>

      {/* Modal de eliminación */}
      <ModalConfirmar
        abierto={!!aEliminar}
        titulo="¿Eliminar persona?"
        mensaje={
          aEliminar
            ? `Vas a eliminar a ${aEliminar.nombre} ${aEliminar.apellido} (${aEliminar.documento}). Esta acción no se puede deshacer.`
            : ''
        }
        onConfirmar={confirmarEliminar}
        onCancelar={() => setAEliminar(null)}
        textoConfirmar="Eliminar"
        peligro
      />

      {/* Modal Buscar intermediarios */}
      <Modal
        abierto={intermediariosAbierto}
        onClose={() => {
          setIntermediariosAbierto(false);
          setVId('');
          setSId('');
          setResultadoIntermediarios(null);
        }}
        titulo="Buscar intermediarios"
        icono="person_search"
        ancho={620}
      >
        <p style={{ color: 'var(--slate-400)', fontSize: 13, marginBottom: 20 }}>
          Encontrá personas que conectan a una víctima con un sospechoso a través de la red
          social. El motor busca caminos de hasta 2 grados de separación.
        </p>
        <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)' }}>
          <div className="form-group">
            <label className="form-label">Víctima</label>
            <select value={vId} onChange={(e) => setVId(e.target.value)}>
              <option value="">— Seleccionar —</option>
              {lista
                .filter((p) => p.rol === 'VICTIMA')
                .map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.nombre} {p.apellido}
                  </option>
                ))}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Sospechoso</label>
            <select value={sId} onChange={(e) => setSId(e.target.value)}>
              <option value="">— Seleccionar —</option>
              {lista
                .filter((p) => p.rol === 'SOSPECHOSO')
                .map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.nombre} {p.apellido}
                  </option>
                ))}
            </select>
          </div>
        </div>
        <button
          className="btn-primary"
          onClick={buscarIntermediarios}
          disabled={!vId || !sId || busqIntermediarios}
          style={{ width: '100%', marginTop: 16 }}
        >
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            search
          </span>
          {busqIntermediarios ? 'Buscando...' : 'Buscar caminos'}
        </button>

        {resultadoIntermediarios !== null && (
          <div style={{ marginTop: 20 }}>
            <h4
              style={{
                fontSize: 11,
                color: 'var(--red-500)',
                textTransform: 'uppercase',
                letterSpacing: '0.1em',
                margin: '0 0 10px',
              }}
            >
              {resultadoIntermediarios.length === 0
                ? 'Sin caminos encontrados'
                : `${resultadoIntermediarios.length} intermediarios detectados`}
            </h4>
            {resultadoIntermediarios.length === 0 ? (
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
                No se encontraron personas que conecten a la víctima y el sospechoso a través
                de la red social. Probá registrando más relaciones.
              </div>
            ) : (
              resultadoIntermediarios.map((p) => (
                <div
                  key={p.id}
                  style={{
                    padding: 12,
                    background: 'var(--slate-950)',
                    border: '1px solid var(--red-500)',
                    borderLeft: '3px solid var(--red-500)',
                    marginBottom: 6,
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div className="row-avatar">
                    <div className="avatar-sm" style={{ background: 'var(--red-600)' }}>
                      {iniciales(p.nombre, p.apellido)}
                    </div>
                    <div>
                      <div className="row-name">
                        {p.nombre} {p.apellido}
                      </div>
                      <div className="row-sub">{p.documento}</div>
                    </div>
                  </div>
                  <span className={`badge ${rolBadge(p.rol)}`}>{rolLabel[p.rol]}</span>
                </div>
              ))
            )}
          </div>
        )}
      </Modal>

      {/* Modal Crear relación */}
      <Modal
        abierto={relacionAbierto}
        onClose={() => {
          setRelacionAbierto(false);
          setPersonaParaRelacion(null);
          setRelForm({ otraId: '', tipo: 'FAMILIAR', peso: 3 });
        }}
        titulo="Crear relación social"
        icono="group_add"
        ancho={520}
      >
        {personaParaRelacion && (
          <>
            <div
              style={{
                padding: 12,
                background: 'var(--slate-950)',
                border: '1px solid var(--slate-800)',
                marginBottom: 16,
                display: 'flex',
                alignItems: 'center',
                gap: 12,
              }}
            >
              <div className="avatar-sm">
                {iniciales(personaParaRelacion.nombre, personaParaRelacion.apellido)}
              </div>
              <div>
                <div style={{ color: 'white', fontWeight: 600 }}>
                  {personaParaRelacion.nombre} {personaParaRelacion.apellido}
                </div>
                <div style={{ fontSize: 10, color: 'var(--slate-500)' }}>
                  Esta persona será la persona A
                </div>
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Conectar con (persona B)</label>
              <select
                value={relForm.otraId}
                onChange={(e) => setRelForm({ ...relForm, otraId: e.target.value })}
              >
                <option value="">— Seleccionar —</option>
                {lista
                  .filter((p) => p.id !== personaParaRelacion.id)
                  .map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nombre} {p.apellido} ({rolLabel[p.rol]})
                    </option>
                  ))}
              </select>
            </div>

            <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)' }}>
              <div className="form-group">
                <label className="form-label">Tipo de relación</label>
                <select
                  value={relForm.tipo}
                  onChange={(e) => setRelForm({ ...relForm, tipo: e.target.value as TipoRelacion })}
                >
                  {TIPOS_REL.map((t) => (
                    <option key={t} value={t}>
                      {relLabel[t]}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Peso (1-5)</label>
                <input
                  type="number"
                  min="1"
                  max="5"
                  value={relForm.peso}
                  onChange={(e) => setRelForm({ ...relForm, peso: Number(e.target.value) })}
                />
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
              <button
                className="btn-ghost"
                onClick={() => {
                  setRelacionAbierto(false);
                  setPersonaParaRelacion(null);
                }}
              >
                Cancelar
              </button>
              <button className="btn-primary" onClick={crearRelacion} disabled={!relForm.otraId}>
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                  save
                </span>
                Crear relación
              </button>
            </div>
          </>
        )}
      </Modal>
    </>
  );
}
