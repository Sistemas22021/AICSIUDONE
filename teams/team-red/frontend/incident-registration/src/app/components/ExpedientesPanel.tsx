import { useState, useEffect } from 'react'
import {
  X, RefreshCw, AlertTriangle, Search,
  ChevronUp, ChevronDown, Dot,
} from 'lucide-react'
import { NeonInput }  from './ui/NeonInput'
import { NeonSelect } from './ui/NeonSelect'
import { useExpedientesActivos }  from '../hooks/useExpedientesActivos'
import { useExpedientesFiltros }  from '../hooks/useExpedientesFiltros'
import { ModusOperandiPanel } from './sections/ModusOperandi/ModusOperandiPanel'
import type { ExpedienteActivo, EstatusExpediente } from '../types/api.types'
import type { SortCol } from '../hooks/useExpedientesFiltros'



const ESTATUS_STYLES: Record<EstatusExpediente, { badge: string; dot: string; label: string }> = {
  BORRADOR:                { badge: 'bg-gray-500/20    text-gray-400    border-gray-500/40',    dot: 'bg-gray-400',    label: 'Borrador' },
  EN_VALIDACION:            { badge: 'bg-amber-500/20   text-amber-400   border-amber-500/40',   dot: 'bg-amber-400',   label: 'En Validación' },
  ASIGNADO_A_EQUIPO:        { badge: 'bg-sky-500/20     text-sky-400     border-sky-500/40',     dot: 'bg-sky-400',     label: 'Asignado a Equipo' },
  INVESTIGACION_ACTIVA:     { badge: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/40', dot: 'bg-emerald-400', label: 'Investigación Activa' },
  EN_REVISION:               { badge: 'bg-amber-500/20   text-amber-400   border-amber-500/40',   dot: 'bg-amber-400',   label: 'En Revisión' },
  PROCESADO_Y_SELLADO:       { badge: 'bg-cyan-500/20    text-cyan-400    border-cyan-500/40',    dot: 'bg-cyan-400',    label: 'Sellado' },
  SOLICITUD_DE_REAPERTURA:  { badge: 'bg-red-500/20     text-red-400     border-red-500/40',     dot: 'bg-red-400',     label: 'Solicitud de Reapertura' },
  CERRADO:                  { badge: 'bg-gray-500/20    text-gray-400    border-gray-500/40',    dot: 'bg-gray-400',    label: 'Cerrado' },
  ARCHIVADO:                { badge: 'bg-gray-500/20    text-gray-400    border-gray-500/40',    dot: 'bg-gray-400',    label: 'Archivado' },
}

const ESTADOS_INACTIVOS: EstatusExpediente[] = ['BORRADOR', 'CERRADO', 'SOLICITUD_DE_REAPERTURA', 'ARCHIVADO']

function getEstiloEstatus(estatus: EstatusExpediente) {
  return ESTATUS_STYLES[estatus] ?? {
    badge: 'bg-gray-500/20 text-gray-400 border-gray-500/40',
    dot:   'bg-gray-400',
    label: estatus,
  }
}

function formatFecha(iso: string | null): string {
  if (!iso) return 'Sin fecha'
  return new Date(iso).toLocaleDateString('es-VE', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })
}

function tiempoTranscurrido(date: Date): string {
  const seg = Math.floor((Date.now() - date.getTime()) / 1000)
  if (seg < 10)  return 'ahora mismo'
  if (seg < 60)  return `hace ${seg}s`
  return `hace ${Math.floor(seg / 60)}m`
}

interface ExpedientesPanelProps {
  isOpen:  boolean
  onClose: () => void
  /** Abre el formulario de registro con el expediente seleccionado */
  onAbrirExpediente?: (expediente: ExpedienteActivo) => void
}

// ─── Componente ───────────────────────────────────────────────────────────────

export const ExpedientesPanel = ({
  isOpen,
  onClose,
  onAbrirExpediente,
}: ExpedientesPanelProps) => {

  const { expedientes, loading, usingMock, ultimaActualizacion, refetch } =
    useExpedientesActivos()

  // Lógica de filtrado y ordenamiento delegada al hook
  const {
    filtrados,
    busqueda,      setBusqueda,
    filtroEstatus, setFiltroEstatus,
    soloAlertas,   setSoloAlertas,
    sortCol,       sortAsc, toggleSort,
  } = useExpedientesFiltros(expedientes)


  const [, setTick] = useState(0)
  const [expedienteMO, setExpedienteMO] = useState<ExpedienteActivo | null>(null)
  const analistaIdActual = 1
  useEffect(() => {
    if (!isOpen) return
    const t = setInterval(() => setTick(n => n + 1), 5000)
    return () => clearInterval(t)
  }, [isOpen])

  if (!isOpen) return null

  const totalActivos = expedientes.filter(e => !ESTADOS_INACTIVOS.includes(e.estatus)).length
  const totalAlertas = expedientes.filter(e => e.tieneAlertaPatron).length


  const Th = ({ col, label }: { col: SortCol; label: string }) => (
    <th
      className="text-left py-2.5 px-3 text-[10px] uppercase tracking-[0.12em] text-cyan-400 font-semibold cursor-pointer select-none whitespace-nowrap hover:text-cyan-300 transition-colors"
      onClick={() => toggleSort(col)}
    >
      <span className="flex items-center gap-1">
        {label}
        {sortCol === col
          ? sortAsc
            ? <ChevronUp size={10}  className="text-cyan-400" />
            : <ChevronDown size={10} className="text-cyan-400" />
          : <ChevronDown size={10} className="text-cyan-400/30" />
        }
      </span>
    </th>
  )

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-[#020818]/92 backdrop-blur-md" onClick={onClose} />

      {/* Panel principal */}
      <div
        className="relative w-full max-w-6xl max-h-[92vh] flex flex-col rounded border-2 border-cyan-400/60 bg-[#060B10]/98 backdrop-blur-sm overflow-hidden"
        style={{ boxShadow: '0 4px 30px rgba(51,153,255,0.5), 0 8px 60px rgba(51,153,255,0.25), inset 0 2px 5px rgba(51,153,255,0.08)' }}
      >

        {/* ══ CABECERA ══════════════════════════════════════════════════════════ */}
        <div className="flex-shrink-0 px-6 pt-5 pb-4 border-b border-cyan-400/20">
          <div className="flex items-start justify-between gap-4">

            {/* Título + indicadores */}
            <div>
              <div
                className="text-sm uppercase tracking-[0.18em] text-cyan-300 font-semibold mb-2"
                style={{ textShadow: '0 0 12px rgba(51,153,255,0.7), 0 0 25px rgba(51,153,255,0.4)', fontFamily: 'Orbitron, monospace' }}
              >
                Expedientes Activos
              </div>

              <div className="flex flex-wrap items-center gap-3">
                {/* Contador activos */}
                <div className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider text-cyan-500">
                  <span
                    className="inline-block w-2 h-2 rounded-full bg-emerald-400"
                    style={{ boxShadow: '0 0 6px rgba(0,255,136,0.7)' }}
                  />
                  {totalActivos} activos
                </div>

                {/* Contador alertas */}
                {totalAlertas > 0 && (
                  <div className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider text-amber-400">
                    <AlertTriangle size={11} style={{ filter: 'drop-shadow(0 0 4px rgba(255,170,0,0.7))' }} />
                    {totalAlertas} alerta{totalAlertas > 1 ? 's' : ''} pendiente{totalAlertas > 1 ? 's' : ''}
                  </div>
                )}

                {/* Estado en vivo */}
                <div className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider text-cyan-600">
                  <Dot
                    size={16}
                    className="text-cyan-400 animate-pulse"
                    style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.7))' }}
                  />
                  {ultimaActualizacion
                    ? `Actualizado ${tiempoTranscurrido(ultimaActualizacion)}`
                    : 'Cargando…'}
                </div>

                {/* Aviso mock */}
                {usingMock && (
                  <div className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider text-amber-400/70">
                    <AlertTriangle size={10} />
                    Datos locales
                  </div>
                )}
              </div>
            </div>

            {/* Acciones */}
            <div className="flex items-center gap-2 flex-shrink-0">
              <button
                onClick={refetch}
                disabled={loading}
                className="p-2 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 hover:border-cyan-400 transition-all disabled:opacity-40"
                title="Actualizar ahora"
              >
                <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
              </button>
              <button
                onClick={onClose}
                className="p-2 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 hover:border-cyan-400 transition-all"
              >
                <X size={16} />
              </button>
            </div>
          </div>
        </div>

        {/* ══ FILTROS ═══════════════════════════════════════════════════════════ */}
        <div className="flex-shrink-0 px-6 py-3 border-b border-cyan-400/15 bg-[#04101E]/40">
          <div className="flex flex-wrap gap-3 items-end">
            {/* Búsqueda libre */}
            <div className="flex-1 min-w-[200px]">
              <NeonInput
                placeholder="Buscar por folio, delito, investigador, sector…"
                value={busqueda}
                onChange={e => setBusqueda(e.target.value)}
              />
            </div>

            {/* Filtro estatus */}
            <div className="w-40">
              <NeonSelect
                  options={[
                    { value: '',                       label: 'Todos' },
                    { value: 'EN_VALIDACION',          label: 'En Validación' },
                    { value: 'ASIGNADO_A_EQUIPO',      label: 'Asignado a Equipo' },
                    { value: 'INVESTIGACION_ACTIVA',   label: 'Investigación Activa' },
                    { value: 'EN_REVISION',            label: 'En Revisión' },
                    { value: 'PROCESADO_Y_SELLADO',    label: 'Sellado' },
                    { value: 'SOLICITUD_DE_REAPERTURA',label: 'Solicitud de Reapertura' },
                    { value: 'CERRADO',                label: 'Cerrado' },
                    { value: 'ARCHIVADO',              label: 'Archivado' },
                  ]}
                  value={filtroEstatus}
                  onChange={e => setFiltroEstatus(e.target.value as EstatusExpediente | '')}
              />
            </div>

            {/* Toggle solo alertas */}
            <button
              onClick={() => setSoloAlertas(v => !v)}
              className={[
                'flex items-center gap-1.5 px-3 py-2.5 border rounded text-[10px] uppercase tracking-wider font-medium transition-all',
                soloAlertas
                  ? 'border-amber-400/70 bg-amber-400/15 text-amber-300'
                  : 'border-cyan-400/30 text-cyan-500 hover:border-cyan-400/60 hover:text-cyan-400',
              ].join(' ')}
            >
              <AlertTriangle size={11} />
              Solo alertas
            </button>
          </div>
        </div>

        {/* ══ TABLA ═════════════════════════════════════════════════════════════ */}
        <div className="flex-1 overflow-auto">
          {loading ? (
            <div className="flex items-center justify-center py-20 gap-3 text-cyan-500">
              <RefreshCw size={16} className="animate-spin" />
              <span className="text-[11px] uppercase tracking-wider">Cargando expedientes…</span>
            </div>
          ) : filtrados.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-20 gap-3 text-cyan-600">
              <Search size={24} className="opacity-40" />
              <span className="text-[11px] uppercase tracking-wider">
                No se encontraron expedientes con los filtros aplicados
              </span>
            </div>
          ) : (
            <table className="w-full text-xs min-w-[800px]">
              <thead className="sticky top-0 z-10">
                <tr
                  className="border-b-2 border-cyan-400/30"
                  style={{ background: 'rgba(4,16,30,0.97)' }}
                >
                  {/* Columna alerta — sin sort */}
                  <th className="py-2.5 px-3 w-8" />
                  <Th col="folioCOPP"            label="Folio COPP" />
                  <Th col="tipoDelito"            label="Delito" />
                  <Th col="fechaHecho"            label="Fecha Hecho" />
                  <Th col="investigadorAsignado"  label="Investigador" />
                  <Th col="estatus"               label="Estatus" />
                  {/* Acción */}
                  <th className="py-2.5 px-3 text-[10px] uppercase tracking-wider text-cyan-400 text-center">
                    Acción
                  </th>
                </tr>
              </thead>
              <tbody>
                {filtrados.map((exp, i) => {
                  const estiloEstatus = getEstiloEstatus(exp.estatus)
                  return (
                    <tr
                      key={exp.id}
                      className={[
                        'border-b border-cyan-400/10 transition-colors',
                        'hover:bg-cyan-400/[0.06] cursor-pointer group',
                        i % 2 === 0 ? 'bg-[#04101E]/20' : '',
                      ].join(' ')}
                    >
                      {/* Indicador de alerta */}
                      <td className="py-3 px-3 text-center">
                        {exp.tieneAlertaPatron && (
                          <span title="Alerta de patrón pendiente de revisión">
                            <AlertTriangle
                              size={13}
                              className="text-amber-400 mx-auto"
                              style={{ filter: 'drop-shadow(0 0 4px rgba(255,170,0,0.8))' }}
                            />
                          </span>
                        )}
                      </td>

                      {/* Folio COPP */}
                      <td className="py-3 px-3 font-medium text-cyan-300 whitespace-nowrap">
                        {exp.folioCOPP}
                      </td>

                      {/* Tipo + subtipo */}
                      <td className="py-3 px-3">
                        <div className="text-cyan-300 leading-tight">{exp.subtipoDelito ?? 'Sin especificar'}</div>
                        <div className="text-[10px] text-cyan-600 mt-0.5">
                          {exp.tipoDelito ? (exp.tipoDelito.split('—')[1]?.trim() ?? exp.tipoDelito) : 'Sin tipo de delito'}
                        </div>
                      </td>

                      {/* Fecha */}
                      <td className="py-3 px-3 text-cyan-400 whitespace-nowrap">
                        {formatFecha(exp.fechaHecho)}
                        <div className="text-[10px] text-cyan-600 mt-0.5">{exp.municipio}</div>
                      </td>

                      {/* Investigador */}
                      <td className="py-3 px-3 text-cyan-400 whitespace-nowrap">
                        {exp.investigadorAsignado}
                      </td>

                      {/* Estatus */}
                      <td className="py-3 px-3">
                        <span
                          className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border ${estiloEstatus.badge}`}
                        >
                          <span
                            className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${estiloEstatus.dot}`}
                            style={!ESTADOS_INACTIVOS.includes(exp.estatus)? { boxShadow: '0 0 5px rgba(0,255,136,0.6)' } : undefined}
                          />
                          {estiloEstatus.label}
                        </span>
                      </td>

                      {/* Acción */}
                      <td className="py-3 px-3 text-center">
                        <button
                          onClick={() => onAbrirExpediente?.(exp)}
                          className="px-3 py-1 border border-cyan-400/40 rounded-sm text-[10px] uppercase tracking-wider text-cyan-400 hover:bg-cyan-400/15 hover:border-cyan-400 hover:text-cyan-300 transition-all"
                          style={{ boxShadow: '0 1px 4px rgba(51,153,255,0.15)' }}
                        >
                          Abrir
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          )}
        </div>

        {/* ══ PIE ═══════════════════════════════════════════════════════════════ */}
        <div className="flex-shrink-0 px-6 py-2.5 border-t border-cyan-400/15 bg-[#04101E]/40 flex items-center justify-between">
          <span className="text-[10px] text-cyan-600 uppercase tracking-wider">
            {filtrados.length} resultado{filtrados.length !== 1 ? 's' : ''}
            {filtrados.length !== expedientes.length && ` de ${expedientes.length}`}
          </span>
          <span className="text-[10px] text-cyan-700 uppercase tracking-wider">
            Actualización automática cada 30s
          </span>
        </div>

      </div>
    </div>
  )
}
