import { Percent, Bot, Fingerprint, Clock3, FileText } from 'lucide-react'
import { NeonPanel } from '../../ui/NeonPanel'
import { EstadoAnalisisMO } from '../../ui/EstadoAnalisisMO'
import type { PropuestaModusOperandi } from '../../../types/api.types'

interface PropuestaMOCardProps {
    propuesta: PropuestaModusOperandi
}

function formatFechaHora(iso: string): string {
    return new Date(iso).toLocaleString('es-VE', {
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
    })
}

/** Muestra el contenido generado por HU2 (características, firma, horario/zona, confianza, similares). */
export const PropuestaMOCard = ({ propuesta }: PropuestaMOCardProps) => {
    const sinCoincidencias = propuesta.estado === 'SIN_COINCIDENCIAS'

    return (
        <NeonPanel title={`Modus Operandi — versión ${propuesta.version}`} subtitle={`Folio ${propuesta.folioExpediente}`}>
            <div className="flex items-center justify-between mb-4">
                <EstadoAnalisisMO estadoCarga="listo" estado={propuesta.estado} />
                <span className="text-[10px] text-cyan-600 uppercase tracking-wider">
          Generado {formatFechaHora(propuesta.fechaGeneracion)}
        </span>
            </div>

            {sinCoincidencias ? (
                <p className="text-sm text-cyan-500/80 italic">{propuesta.resumenGenerado ?? 'MO sin coincidencias previas'}</p>
            ) : (
                <div className="grid gap-4">
                    <Campo icon={<Fingerprint size={13} />} etiqueta="Características comunes" valor={propuesta.caracteristicasComunes} />
                    <Campo icon={<Bot size={13} />} etiqueta="Posible firma del perpetrador" valor={propuesta.posibleFirma} />
                    <Campo icon={<Clock3 size={13} />} etiqueta="Consistencia de horario / zona" valor={propuesta.consistenciaHorarioZona} />
                    <Campo icon={<FileText size={13} />} etiqueta="Resumen" valor={propuesta.resumenGenerado} />

                    <div className="flex items-center gap-2">
                        <Percent size={13} className="text-cyan-400" />
                        <span className="text-[11px] uppercase tracking-wider text-cyan-400">Nivel de confianza</span>
                        <span className="text-sm font-semibold text-cyan-300">
              {propuesta.nivelConfianza !== null ? `${propuesta.nivelConfianza.toFixed(1)}%` : 'N/D'}
            </span>
                    </div>

                    {propuesta.expedientesSimilares.length > 0 && (
                        <div>
              <span className="text-[11px] uppercase tracking-wider text-cyan-400 block mb-2">
                Expedientes similares ({propuesta.expedientesSimilares.length})
              </span>
                            <ul className="flex flex-col gap-1.5">
                                {propuesta.expedientesSimilares.map(s => (
                                    <li key={s.expedienteId} className="flex items-center justify-between text-xs border border-cyan-400/15 rounded px-3 py-1.5 bg-[#04101E]/40">
                                        <span className="text-cyan-300">{s.folio}</span>
                                        <span className="text-cyan-500">{s.similitudPorcentaje.toFixed(1)}% similitud</span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    {(propuesta.modeloEmbedding || propuesta.modeloChat) && (
                        <span className="text-[10px] text-cyan-700 uppercase tracking-wider">
              Modelos: {propuesta.modeloEmbedding ?? '—'} / {propuesta.modeloChat ?? '—'}
            </span>
                    )}
                </div>
            )}
        </NeonPanel>
    )
}

function Campo({ icon, etiqueta, valor }: { icon: React.ReactNode; etiqueta: string; valor: string | null }) {
    return (
        <div>
            <div className="flex items-center gap-1.5 text-[11px] uppercase tracking-wider text-cyan-400 mb-1">
                {icon}
                {etiqueta}
            </div>
            <p className="text-sm text-cyan-200/90 leading-relaxed">{valor ?? '—'}</p>
        </div>
    )
}