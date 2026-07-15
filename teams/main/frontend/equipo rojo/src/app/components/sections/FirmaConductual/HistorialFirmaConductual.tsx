// Vista de historial (versiones) de la Firma Conductual de un expediente.
// A diferencia de HistorialEscenas (que lee de localStorage), este componente
// es puramente presentacional: recibe los datos ya obtenidos del backend
// (GET /api/firma-conductual/expediente/{id}/historial) vía props.
import { useState } from 'react'
import { X, ShieldCheck, Clock, Loader2 } from 'lucide-react'
import { NeonPanel } from '../../ui/NeonPanel'
import type { FirmaConductualResponseDTO } from '../../../services/firmaConductualService'
import { CAMPOS_FIRMA_CONDUCTUAL } from './index'

interface HistorialFirmaConductualProps {
    historial: FirmaConductualResponseDTO[]
    loading: boolean
    onClose?: () => void
}

export const HistorialFirmaConductual = ({ historial, loading, onClose }: HistorialFirmaConductualProps) => {
    const [seleccionadaId, setSeleccionadaId] = useState<number | null>(null)

    // Por defecto se muestra la vigente (o la más reciente) hasta que el usuario elija otra.
    const seleccionada =
        historial.find(f => f.id === seleccionadaId) ??
        historial.find(f => f.vigente) ??
        historial[0] ??
        null

    return (
        <NeonPanel>
            <div className="flex items-center justify-between mb-4">
                <h3
                    className="text-sm uppercase tracking-[0.15em] text-cyan-300 font-semibold"
                    style={{ textShadow: '0 0 10px rgba(51,153,255,0.7), 0 0 20px rgba(51,153,255,0.4)' }}
                >
                    Historial de la Firma Conductual
                </h3>
                {onClose && (
                    <button
                        onClick={onClose}
                        className="p-1.5 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 hover:border-cyan-400 transition-all"
                    >
                        <X size={14} />
                    </button>
                )}
            </div>

            {loading ? (
                <div className="flex items-center justify-center gap-2 py-10 text-cyan-500 text-[11px] uppercase tracking-wider">
                    <Loader2 size={14} className="animate-spin" /> Cargando historial…
                </div>
            ) : historial.length === 0 ? (
                <div className="text-center py-10 text-cyan-600 text-[11px] uppercase tracking-wider">
                    Aún no hay versiones registradas para este expediente.
                </div>
            ) : (
                <div className="flex gap-5 flex-wrap">
                    {/* Lista de versiones */}
                    <div className="flex-1 min-w-[220px] max-w-xs space-y-2 max-h-[420px] overflow-y-auto pr-1">
                        {historial.map(firma => {
                            const isSelected = seleccionada?.id === firma.id
                            return (
                                <button
                                    key={firma.id}
                                    onClick={() => setSeleccionadaId(firma.id)}
                                    className={[
                                        'w-full text-left p-3 border rounded transition-all',
                                        isSelected
                                            ? 'border-cyan-400 bg-cyan-400/10'
                                            : 'border-cyan-400/25 hover:border-cyan-400/50',
                                    ].join(' ')}
                                >
                                    <div className="flex items-center justify-between gap-2">
                                        <span className="text-[12px] font-medium text-cyan-200">
                                            Versión {firma.version}
                                        </span>
                                        {firma.vigente && (
                                            <span className="flex items-center gap-1 text-[9px] uppercase tracking-wider text-emerald-400 border border-emerald-400/40 rounded px-1.5 py-0.5">
                                                <ShieldCheck size={9} /> Vigente
                                            </span>
                                        )}
                                    </div>
                                    <div className="flex items-center gap-1 mt-1.5 text-[10px] text-cyan-600">
                                        <Clock size={10} />
                                        {new Date(firma.fechaRegistro).toLocaleString('es-VE')}
                                    </div>
                                </button>
                            )
                        })}
                    </div>

                    {/* Detalle de la versión seleccionada */}
                    <div className="flex-[2] min-w-[280px]">
                        {seleccionada ? (
                            <div className="p-4 border border-cyan-400/25 rounded bg-[#020810]/60 space-y-3 max-h-[420px] overflow-y-auto">
                                <div className="flex items-center justify-between pb-2 border-b border-cyan-400/15">
                                    <span className="text-[12px] font-semibold text-cyan-300">
                                        Versión {seleccionada.version}
                                    </span>
                                    <span className="text-[10px] text-cyan-600">
                                        Analista #{seleccionada.analistaId}
                                    </span>
                                </div>
                                {CAMPOS_FIRMA_CONDUCTUAL.map(campo => (
                                    <div key={campo.key}>
                                        <div className="text-[10px] uppercase tracking-wider text-cyan-500 mb-1">
                                            {campo.label}
                                        </div>
                                        <p className="text-[12px] text-cyan-200/90 whitespace-pre-wrap leading-relaxed">
                                            {seleccionada[campo.key] || (
                                                <span className="text-cyan-800 italic">Sin información</span>
                                            )}
                                        </p>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-10 text-cyan-600 text-[11px] uppercase tracking-wider">
                                Selecciona una versión para ver el detalle
                            </div>
                        )}
                    </div>
                </div>
            )}
        </NeonPanel>
    )
}