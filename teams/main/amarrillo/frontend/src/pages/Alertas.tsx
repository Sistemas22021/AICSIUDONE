import { useEffect, useState } from 'react';
import { alertaService } from '../services/api';
import type { Alerta, EstadoAlerta } from '../types';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import ModalDetalle from '../components/ModalDetalle';
import { exportarCSV } from '../services/exportar';
import { useConfirm } from '../services/ConfirmContext';
import { useToast } from '../services/ToastContext';

const ESTADOS: EstadoAlerta[] = ['PENDIENTE', 'EN_REVISION', 'CONFIRMADA', 'DESCARTADA'];

const estadoLabel: Record<string, string> = {
  PENDIENTE: 'Pendiente', EN_REVISION: 'En revisión',
  CONFIRMADA: 'Confirmada', DESCARTADA: 'Descartada',
};

const riesgoLabel: Record<string, string> = {
  CRITICO: 'Crítico', ALTO: 'Alto', MEDIO: 'Medio', BAJO: 'Bajo',
};

const tipoIcono: Record<string, string> = {
  NODO_LOGISTICO: 'warehouse', VEHICULO_APOYO: 'directions_car',
  INTERMEDIARIO: 'person_search', POI_DESAPARICION: 'person_off',
  MODUS_OPERANDI: 'fingerprint',
};

const tipoLabel: Record<string, string> = {
  NODO_LOGISTICO: 'Nodo logístico',
  VEHICULO_APOYO: 'Vehículo de apoyo',
  INTERMEDIARIO: 'Intermediario',
  POI_DESAPARICION: 'Punto de desaparición',
  MODUS_OPERANDI: 'Modus operandi',
};

export default function Alertas() {
  const [lista, setLista] = useState<Alerta[]>([]);
  const [soloPendientes, setSoloPendientes] = useState(false);
  const [filtroNivel, setFiltroNivel] = useState<string>('');
  const [detalle, setDetalle] = useState<Alerta | null>(null);
  const confirmar = useConfirm();
  const toast = useToast();

  const cargar = async () => setLista(await alertaService.listar(soloPendientes));
  useEffect(() => { cargar(); }, [soloPendientes]);

  const cambiar = async (id: number, estado: EstadoAlerta) => {
    await alertaService.cambiarEstado(id, estado);
    await cargar();
  };

  const desplegar = async (a: Alerta) => {
    const ok = await confirmar({
      titulo: 'Desplegar respuesta',
      mensaje: `¿Marcar la alerta NX-${String(a.id).padStart(5, '0')} como CONFIRMADA y desplegar respuesta?`,
      textoConfirmar: 'Desplegar',
    });
    if (ok) {
      await cambiar(a.id, 'CONFIRMADA');
      toast.exito(`Alerta NX-${String(a.id).padStart(5, '0')} confirmada`);
    }
  };

  const analizar = async (a: Alerta) => {
    const ok = await confirmar({
      titulo: 'Pasar a revisión',
      mensaje: `¿Pasar la alerta NX-${String(a.id).padStart(5, '0')} a EN_REVISION?`,
      textoConfirmar: 'Analizar',
    });
    if (ok) {
      await cambiar(a.id, 'EN_REVISION');
      toast.info(`Alerta NX-${String(a.id).padStart(5, '0')} en revisión`);
    }
  };

  const descartar = async (a: Alerta) => {
    const ok = await confirmar({
      titulo: 'Descartar alerta',
      mensaje: `¿Descartar la alerta NX-${String(a.id).padStart(5, '0')}?`,
      textoConfirmar: 'Descartar',
      peligro: true,
    });
    if (ok) {
      await cambiar(a.id, 'DESCARTADA');
      toast.info(`Alerta NX-${String(a.id).padStart(5, '0')} descartada`);
    }
  };

  const exportar = () => {
    exportarCSV(
      filtradas.map(a => ({
        ID: `NX-${String(a.id).padStart(5, '0')}`,
        Tipo: tipoLabel[a.tipo] || a.tipo,
        Titulo: a.titulo,
        Descripcion: a.descripcion || '',
        Riesgo: riesgoLabel[a.nivelRiesgo],
        Estado: estadoLabel[a.estado],
        FechaCreacion: new Date(a.creadaEn).toLocaleString('es-ES'),
      })),
      'alertas'
    );
  };

  const filtradas = lista.filter(a => {
    if (filtroNivel && a.nivelRiesgo !== filtroNivel) return false;
    return true;
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtradas, 10);

  const criticas = lista.filter(a => a.nivelRiesgo === 'CRITICO').length;
  const altas = lista.filter(a => a.nivelRiesgo === 'ALTO').length;
  const pendientes = lista.filter(a => a.estado === 'PENDIENTE').length;
  const confirmadas = lista.filter(a => a.estado === 'CONFIRMADA').length;

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Panel de Alertas</h1>
          <p className="page-subtitle">
            Monitoreo de inteligencia en tiempo real. Análisis predictivo y detección de patrones.
          </p>
        </div>
        <div className="toolbar" style={{ margin: 0, padding: 4, background: 'var(--slate-900)', border: '1px solid var(--slate-800)' }}>
          <button className={`btn-ghost ${!soloPendientes ? 'active' : ''}`}
            onClick={() => setSoloPendientes(false)}>Todas</button>
          <button className={`btn-ghost ${soloPendientes ? 'active' : ''}`}
            onClick={() => setSoloPendientes(true)}>Solo pendientes</button>
        </div>
      </div>

      <div className="toolbar">
        <button className="btn-ghost" onClick={exportar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>download</span>
          Exportar CSV
        </button>
      </div>

      <div className="alertas-layout">
        <div>
          <div className="alerta-stats-panel">
            <h4 style={{
              fontSize: 10, fontWeight: 700, color: 'var(--slate-500)',
              textTransform: 'uppercase', letterSpacing: '0.15em', margin: '0 0 14px',
            }}>
              Estado del sistema
            </h4>
            <div className="alerta-stat-row">
              <span className="alerta-stat-row-label">Alertas críticas</span>
              <span className="alerta-stat-row-value danger">{criticas}</span>
            </div>
            <div className="alerta-stat-row">
              <span className="alerta-stat-row-label">Riesgo alto</span>
              <span className="alerta-stat-row-value danger">{altas}</span>
            </div>
            <div className="alerta-stat-row">
              <span className="alerta-stat-row-label">Pendientes</span>
              <span className="alerta-stat-row-value tertiary">{pendientes}</span>
            </div>
            <div className="alerta-stat-row">
              <span className="alerta-stat-row-label">Confirmadas</span>
              <span className="alerta-stat-row-value">{confirmadas}</span>
            </div>
            <div className="alerta-stat-row">
              <span className="alerta-stat-row-label">Total</span>
              <span className="alerta-stat-row-value mono">{lista.length}</span>
            </div>
          </div>

          <div className="alerta-stats-panel">
            <h4 style={{
              fontSize: 10, fontWeight: 700, color: 'var(--slate-500)',
              textTransform: 'uppercase', letterSpacing: '0.15em', margin: '0 0 14px',
            }}>
              Filtrar por nivel
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {[
                { k: '', label: 'Todos' },
                { k: 'CRITICO', label: 'Crítico' },
                { k: 'ALTO', label: 'Alto' },
                { k: 'MEDIO', label: 'Medio' },
                { k: 'BAJO', label: 'Bajo' },
              ].map(opt => (
                <button key={opt.k}
                  className={`btn-ghost ${filtroNivel === opt.k ? 'active' : ''}`}
                  onClick={() => setFiltroNivel(opt.k)}
                  style={{ justifyContent: 'flex-start', padding: '6px 10px' }}>
                  {opt.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div>
          {visibles.length === 0 && (
            <div className="card" style={{ textAlign: 'center', padding: 48 }}>
              <span className="material-symbols-outlined" style={{ fontSize: 48, color: 'var(--slate-700)' }}>
                check_circle
              </span>
              <div style={{ marginTop: 12, color: 'white', fontWeight: 700 }}>
                No hay alertas {soloPendientes ? 'pendientes' : ''}
              </div>
              <div style={{ fontSize: 12, color: 'var(--slate-500)', marginTop: 6 }}>
                {filtroNivel ? 'Probá cambiando el filtro de nivel.'
                  : 'El sistema está limpio. Ejecutá el motor desde el panel principal para detectar nuevos patrones.'}
              </div>
            </div>
          )}

          {visibles.map(a => (
            <div key={a.id} className={`alerta-card ${a.nivelRiesgo.toLowerCase()}`}>
              <span className="alerta-watermark material-symbols-outlined">
                {tipoIcono[a.tipo] || 'warning'}
              </span>
              <div className="alerta-card-row">
                <div className="alerta-card-body">
                  <div className="alerta-card-badges">
                    <span className="alerta-id">NX-{String(a.id).padStart(5, '0')}</span>
                    <span className={`badge ${a.nivelRiesgo === 'CRITICO' || a.nivelRiesgo === 'ALTO' ? 'robado' : 'apoyo'}`}>
                      Riesgo: {riesgoLabel[a.nivelRiesgo]}
                    </span>
                    <span className="badge muted">Estado: {estadoLabel[a.estado]}</span>
                    <span className="badge muted">{tipoLabel[a.tipo] || a.tipo}</span>
                  </div>
                  <h2 className="alerta-card-title">{a.titulo}</h2>
                  <p className="alerta-card-desc">{a.descripcion}</p>
                </div>
                <div className="alerta-card-side">
                  <div className="alerta-timestamp">
                    <span className="alerta-timestamp-label">Detectada</span>
                    {new Date(a.creadaEn).toLocaleString('es-ES')}
                  </div>
                  <div style={{ display: 'flex', gap: 6 }}>
                    <button className="btn-icon" onClick={() => setDetalle(a)} title="Ver detalle">
                      <span className="material-symbols-outlined" style={{ fontSize: 18 }}>visibility</span>
                    </button>
                    {a.estado === 'PENDIENTE' && (
                      <>
                        <button className="btn-secondary" onClick={() => analizar(a)}
                          style={{ fontSize: 10, padding: '6px 12px' }}>
                          Analizar
                        </button>
                        {(a.nivelRiesgo === 'CRITICO' || a.nivelRiesgo === 'ALTO') && (
                          <button className="btn-primary" onClick={() => desplegar(a)}
                            style={{ fontSize: 10, padding: '6px 12px' }}>
                            Desplegar
                          </button>
                        )}
                      </>
                    )}
                    {a.estado === 'EN_REVISION' && (
                      <>
                        <button className="btn-primary" onClick={() => cambiar(a.id, 'CONFIRMADA')}
                          style={{ fontSize: 10, padding: '6px 12px' }}>
                          Confirmar
                        </button>
                        <button className="btn-ghost" onClick={() => descartar(a)}
                          style={{ fontSize: 10, padding: '6px 12px' }}>
                          Descartar
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}

          {total > porPagina && (
            <div style={{ marginTop: 16, background: 'var(--slate-900)', border: '1px solid var(--slate-800)' }}>
              <Paginacion total={total} pagina={pagina} porPagina={porPagina}
                onCambiar={setPagina} label="alertas" />
            </div>
          )}
        </div>
      </div>

      {/* Modal Detalle */}
      {detalle && (
        <ModalDetalle abierto={!!detalle} onClose={() => setDetalle(null)}
          titulo={detalle.titulo}
          subtitulo={`NX-${String(detalle.id).padStart(5, '0')} · ${tipoLabel[detalle.tipo] || detalle.tipo}`}
          icono={tipoIcono[detalle.tipo] || 'warning'}
          avatar={
            <div className="dossier-avatar" style={{
              background: detalle.nivelRiesgo === 'CRITICO' || detalle.nivelRiesgo === 'ALTO'
                ? 'var(--red-600)' : 'var(--amber)',
            }}>
              <span className="material-symbols-outlined">{tipoIcono[detalle.tipo] || 'warning'}</span>
            </div>
          }
          campos={[
            { etiqueta: 'ID', valor: `NX-${String(detalle.id).padStart(5, '0')}`, mono: true, destacado: true },
            { etiqueta: 'Tipo de alerta', valor: tipoLabel[detalle.tipo] || detalle.tipo },
            { etiqueta: 'Nivel de riesgo', valor: riesgoLabel[detalle.nivelRiesgo],
              destacado: detalle.nivelRiesgo === 'CRITICO' || detalle.nivelRiesgo === 'ALTO' },
            { etiqueta: 'Estado', valor: estadoLabel[detalle.estado] },
            { etiqueta: 'Detectada', valor: new Date(detalle.creadaEn).toLocaleString('es-ES'), mono: true },
            { etiqueta: 'Vínculo asociado', valor: detalle.vinculoId ? `#${detalle.vinculoId}` : 'Sin vínculo', mono: true },
          ]}
          extra={
            detalle.descripcion ? (
              <div style={{ marginTop: 16 }}>
                <h4 style={{
                  fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase',
                  letterSpacing: '0.1em', margin: '0 0 8px',
                }}>
                  Descripción del análisis
                </h4>
                <div style={{
                  padding: 12, background: 'var(--slate-950)',
                  border: '1px solid var(--red-500)', borderLeft: '3px solid var(--red-500)',
                  fontSize: 13, color: 'var(--slate-300)', lineHeight: 1.6,
                }}>
                  {detalle.descripcion}
                </div>
              </div>
            ) : null
          }
          acciones={
            <>
              <select value={detalle.estado}
                onChange={(e) => {
                  cambiar(detalle.id, e.target.value as EstadoAlerta);
                  setDetalle({ ...detalle, estado: e.target.value as EstadoAlerta });
                }}
                style={{ width: 160, fontSize: 11 }}>
                {ESTADOS.map(e => <option key={e} value={e}>{estadoLabel[e]}</option>)}
              </select>
            </>
          }
        />
      )}
    </>
  );
}