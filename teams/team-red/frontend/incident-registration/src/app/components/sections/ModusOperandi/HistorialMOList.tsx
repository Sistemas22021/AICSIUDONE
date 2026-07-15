import { History } from 'lucide-react'
import { EstadoAnalisisMO } from '../../ui/EstadoAnalisisMO'
import type { PropuestaModusOperandi } from '../../../types/api.types'

interface HistorialMOListProps {
    historial: PropuestaModusOperandi[]
}

function formatFechaHora(iso: string): string {
    return new Date(iso).toLocaleString('es-VE', {
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
    })
}

/** Historial de versiones de MO de un expediente (HU3, CA5). */
export const HistorialMOList = ({ historial }: HistorialMOListProps) => {
    if (historial.length === 0) {
        return <p className="text-xs text-cyan-600">Sin historial disponible.</p>
    }

    return (
        <div className="flex flex-col gap-2">
            {historial.map(p => (
                <div
                    key={p.id}
                    className={[
                        'border rounded px-3 py-2.5 text-xs',
                        p.vigente ? 'border-cyan-400/40 bg-cyan-400/[0.04]' : 'border-cyan-400/10 bg-[#04101E]/30',
                    ].join(' ')}
                >
                    <div className="flex items-center justify-between mb-1.5">
            <span className="flex items-center gap-1.5 text-cyan-300 font-medium">
              <History size={11} />
              Versión {p.version} {p.vigente && <span className="text-cyan-500">(vigente)</span>}
            </span>
                        <EstadoAnalisisMO estadoCarga="listo" estado={p.estado} />
                    </div>
                    <div className="text-cyan-600 text-[10px] uppercase tracking-wider">
                        Generado {formatFechaHora(p.fechaGeneracion)}
                    </div>
                    {p.revisadoPorExperto && (
                        <div className="mt-1.5 text-cyan-400/90">
                            Revisado por <span className="font-medium">{p.analistaRevisorNombre ?? `analista #${p.analistaRevisorId}`}</span>
                            {p.fechaRevision && <> el {formatFechaHora(p.fechaRevision)}</>}
                            {p.justificacionRevision && (
                                <p className="mt-1 text-cyan-500/80 italic">"{p.justificacionRevision}"</p>
                            )}
                            {p.clasificacionManual && (
                                <p className="mt-1 text-cyan-500/80">Clasificación manual: {p.clasificacionManual}</p>
                            )}
                        </div>
                    )}
                </div>
            ))}
        </div>
    )
}