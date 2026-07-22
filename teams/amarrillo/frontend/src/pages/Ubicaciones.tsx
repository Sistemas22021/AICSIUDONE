import { useEffect, useMemo, useState } from 'react';
import { ubicacionService } from '../services/api';
import type { Ubicacion, TipoUbicacion } from '../types';
import { usePaginacion } from '../services/usePaginacion';
import Paginacion from '../components/Paginacion';
import MapaTactical, { PuntoMapa } from '../components/MapaTactical';
import ModalConfirmar from '../components/ModalConfirmar';
import ModalDetalle from '../components/ModalDetalle';
import { exportarCSV } from '../services/exportar';

const TIPOS: TipoUbicacion[] = [
  'TALLER', 'GALPON', 'TERRENO_BALDIO', 'DOMICILIO',
  'CAJERO', 'TRANSPORTE_PUBLICO', 'COMERCIO', 'OTRO',
];

const tipoLabel: Record<string, string> = {
  TALLER: 'Taller mecánico', GALPON: 'Galpón',
  TERRENO_BALDIO: 'Terreno baldío', DOMICILIO: 'Domicilio',
  CAJERO: 'Cajero automático', TRANSPORTE_PUBLICO: 'Transporte público',
  COMERCIO: 'Comercio', OTRO: 'Otro',
};

const tipoIcono: Record<string, string> = {
  TALLER: 'build', GALPON: 'warehouse', TERRENO_BALDIO: 'landscape',
  DOMICILIO: 'home', CAJERO: 'local_atm',
  TRANSPORTE_PUBLICO: 'directions_bus', COMERCIO: 'storefront', OTRO: 'place',
};

export default function Ubicaciones() {
  const [lista, setLista] = useState<Ubicacion[]>([]);
  const [filtro, setFiltro] = useState('');
  const [filtroTipo, setFiltroTipo] = useState<string>('');
  const [err, setErr] = useState('');
  const [ok, setOk] = useState('');
  const [detalle, setDetalle] = useState<Ubicacion | null>(null);
  const [aEliminar, setAEliminar] = useState<Ubicacion | null>(null);

  const cargar = async () => setLista(await ubicacionService.listar());
  useEffect(() => { cargar(); }, []);

  const confirmarEliminar = async () => {
    if (!aEliminar) return;
    try {
      await ubicacionService.eliminar(aEliminar.id!);
      setAEliminar(null);
      setOk('Ubicación eliminada');
      setTimeout(() => setOk(''), 3000);
      await cargar();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo eliminar');
      setAEliminar(null);
    }
  };

  const exportar = () => {
    exportarCSV(
      filtradas.map(u => ({
        ID: u.id, Direccion: u.direccion || '',
        Latitud: u.latitud, Longitud: u.longitud,
        Tipo: tipoLabel[u.tipo || 'OTRO'],
        Sospechoso: u.nodoSospechoso ? 'Sí' : 'No',
      })),
      'ubicaciones'
    );
  };

  const filtradas = lista.filter((u) => {
    if (filtroTipo && u.tipo !== filtroTipo) return false;
    if (!filtro.trim()) return true;
    const q = filtro.toLowerCase();
    return u.direccion?.toLowerCase().includes(q) || u.tipo?.toLowerCase().includes(q);
  });

  const { visibles, pagina, setPagina, total, porPagina } = usePaginacion(filtradas, 10);

  const puntos: PuntoMapa[] = useMemo(
    () => filtradas.map((u) => ({
      id: u.id!, lat: u.latitud, lng: u.longitud, tipo: u.tipo || 'OTRO',
      titulo: u.direccion || `Ubicación #${u.id}`,
      subtitulo: tipoLabel[u.tipo || 'OTRO'],
      sospechoso: u.nodoSospechoso,
      campos: [
        { etiqueta: 'ID', valor: `LOC-${String(u.id).padStart(4, '0')}` },
        { etiqueta: 'Tipo', valor: tipoLabel[u.tipo || 'OTRO'] },
        { etiqueta: 'Estado', valor: u.nodoSospechoso ? 'SOSPECHOSO' : 'NORMAL' },
      ],
    })), [filtradas]);

  const sospechosas = lista.filter(u => u.nodoSospechoso).length;

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Inteligencia Geoespacial</h1>
          <p className="page-subtitle">Registro y gestión de ubicaciones de interés criminal.</p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">UBICACIONES: {lista.length}</span>
          {sospechosas > 0 && <span className="badge-pill alerta">SOSPECHOSAS: {sospechosas}</span>}
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

      {/* Mapa a ancho completo (las ubicaciones se crean desde los mapas de Sucesos) */}
      <div style={{ marginBottom: 20 }}>
        <MapaTactical puntos={puntos} altura={360}
          hudLabel="Sistema GIS activo"
          hudValor={`${puntos.length} ubicaciones`}
          emptyMessage="Sin ubicaciones georreferenciadas" />
      </div>

      {/* Tabla */}
      <div className="table-wrap">
        <div className="table-header">
          <div className="table-header-title">
            <span className="material-symbols-outlined">list_alt</span>
            <h3>Historial de Ubicaciones</h3>
          </div>
          <div className="table-header-actions">
            <select value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value)}
              style={{ width: 160, fontSize: 11, padding: '6px 24px 6px 10px' }}>
              <option value="">Todos los tipos</option>
              {TIPOS.map((t) => <option key={t} value={t}>{tipoLabel[t]}</option>)}
            </select>
            <div className="filter-input-wrap">
              <span className="material-symbols-outlined">filter_list</span>
              <input type="text" placeholder="Filtrar..."
                value={filtro} onChange={(e) => setFiltro(e.target.value)} />
            </div>
          </div>
        </div>
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>ID</th><th>Dirección</th><th>Lat / Lng</th>
                <th>Tipo</th><th>Estado</th><th className="right">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map((u) => (
                <tr key={u.id} style={u.nodoSospechoso ? { borderLeft: '3px solid var(--red-500)' } : undefined}>
                  <td className="mono">LOC-{String(u.id).padStart(4, '0')}</td>
                  <td>
                    <div className="row-avatar">
                      <div className={`avatar-sm ubicacion ${u.nodoSospechoso ? 'sospechoso' : ''}`}>
                        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                          {tipoIcono[u.tipo || 'OTRO']}
                        </span>
                      </div>
                      <div>
                        <div className="row-name">{u.direccion || 'Sin dirección'}</div>
                        <div className="row-sub">{tipoLabel[u.tipo || 'OTRO']}</div>
                      </div>
                    </div>
                  </td>
                  <td className="mono" style={{ fontSize: 11, color: 'var(--slate-400)' }}>
                    {u.latitud.toFixed(4)} / {u.longitud.toFixed(4)}
                  </td>
                  <td><span className="badge muted">{tipoLabel[u.tipo || 'OTRO']}</span></td>
                  <td>
                    {u.nodoSospechoso ? (
                      <span className="badge sospechoso">
                        <span className="material-symbols-outlined" style={{ fontSize: 12 }}>warning</span>
                        {' '}Sospechoso
                      </span>
                    ) : <span className="badge normal">Normal</span>}
                  </td>
                  <td className="right">
                    <div className="table-actions">
                      <button className="btn-icon" onClick={() => setDetalle(u)} title="Ver detalle">
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>visibility</span>
                      </button>
                      <button className="btn-icon danger" onClick={() => setAEliminar(u)} title="Eliminar">
                        <span className="material-symbols-outlined" style={{ fontSize: 18 }}>delete</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {visibles.length === 0 && (
                <tr><td colSpan={6} className="table-empty">
                  {filtro || filtroTipo ? 'Sin resultados' : 'Sin ubicaciones registradas'}
                </td></tr>
              )}
            </tbody>
          </table>
        </div>
        <Paginacion total={total} pagina={pagina} porPagina={porPagina}
          onCambiar={setPagina} label="ubicaciones" />
      </div>

      {/* Modal Detalle */}
      {detalle && (
        <ModalDetalle abierto={!!detalle} onClose={() => setDetalle(null)}
          titulo={detalle.direccion || `Ubicación #${detalle.id}`}
          subtitulo={tipoLabel[detalle.tipo || 'OTRO']}
          icono={tipoIcono[detalle.tipo || 'OTRO']}
          campos={[
            { etiqueta: 'ID', valor: `LOC-${String(detalle.id).padStart(4, '0')}`, mono: true },
            { etiqueta: 'Dirección', valor: detalle.direccion || '—' },
            { etiqueta: 'Latitud', valor: detalle.latitud.toFixed(6), mono: true },
            { etiqueta: 'Longitud', valor: detalle.longitud.toFixed(6), mono: true },
            { etiqueta: 'Tipo', valor: tipoLabel[detalle.tipo || 'OTRO'] },
            {
              etiqueta: 'Estado',
              valor: detalle.nodoSospechoso ? 'Sospechoso' : 'Normal',
              destacado: detalle.nodoSospechoso,
            },
          ]}
        />
      )}

      <ModalConfirmar abierto={!!aEliminar} titulo="¿Eliminar ubicación?"
        mensaje={aEliminar ? `Vas a eliminar "${aEliminar.direccion || `Ubi #${aEliminar.id}`}".` : ''}
        onConfirmar={confirmarEliminar} onCancelar={() => setAEliminar(null)}
        textoConfirmar="Eliminar" peligro />
    </>
  );
}
