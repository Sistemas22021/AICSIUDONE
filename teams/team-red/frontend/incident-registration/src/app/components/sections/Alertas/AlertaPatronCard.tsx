import { useState } from 'react'
import { CheckCircle2, XCircle, Percent, FileWarning, Users, Clock3 } from 'lucide-react'
import { NeonPanel } from '../../ui/NeonPanel'
import { NeonButton } from '../../ui/NeonButton'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonConfirmModal } from '../../ui/NeonConfirmModal'
import { EstadoAlertaBadge } from '../../ui/EstadoAlertaBadge'
import type { AlertaPatron } from '../../../types/api.types'

interface AlertaPatronCardProps {
    alerta: AlertaPatron
    onRevisar: (alertaId: number) => Promise<void>
    onDescartar: (alertaId: number, motivo: string) => Promise<void>
}

function formatFechaHora(iso: string): string {
    return new Date(iso).toLocaleString('es-VE', {
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
    })
}

/** Muestra el contenido de una alerta interna de patrón de MO: folios relacionados, resumen, confianza y acciones de revisar/descartar. */
export const AlertaPatronCard = ({ alerta, onRevisar, onDescartar }: AlertaPatronCardProps) => {
    const [confirmarRevisar, setConfirmarRevisar] = useState(false)
    const [mostrarDescartar, setMostrarDescartar] = useState(false)
    const [motivo, setMotivo] = useState('')
    const [enviando, setEnviando] = useState(false)

    const esPendiente = alerta.estado === 'PENDIENTE'

    const handleRevisar = async () => {
        setEnviando(true)
        try {
            await onRevisar(alerta.id)
            setConfirmarRevisar(false)
        } finally {
            setEnviando(false)
        }
    }

    const handleDescartar = async () => {
        setEnviando(true)
        try {
            await onDescartar(alerta.id, motivo)
            setMostrarDescartar(false)
            setMotivo('')
        } finally {
            setEnviando(false)
        }
    }

    return (
        <NeonPanel
            title={`Patrón de MO — Folio ${alerta.expedienteOrigenFolio}`}
            subtitle={`Generada ${formatFechaHora(alerta.fechaGeneracion)}`}
        >
            <div className="flex items-center justify-between mb-4">
                <EstadoAlertaBadge estado={alerta.estado} />
                <div className="flex items-center gap-1.5">
                    <Percent size={13} className="text-cyan-400" />
                    <span className="text-sm font-semibold text-cyan-300">
                        {alerta.nivelConfianza.toFixed(1)}% confianza
                    </span>
                </div>
            </div>

            <div className="mb-4">
                <div className="flex items-center gap-1.5 text-[11px] uppercase tracking-wider text-cyan-400 mb-1">
                    <FileWarning size={13} />
                    Resumen del patrón
                </div>
                <p className="text-sm text-cyan-200/90 leading-relaxed">{alerta.resumenPatron}</p>
            </div>

            <div className="mb-4">
                <span className="text-[11px] uppercase tracking-wider text-cyan-400 flex items-center gap-1.5 mb-2">
                    <Users size={13} />
                    Expedientes relacionados ({alerta.expedientesRelacionados.length})
                </span>
                <ul className="flex flex-col gap-1.5">
                    {alerta.expedientesRelacionados.map(s => (
                        <li
                            key={s.expedienteId}
                            className="flex items-center justify-between text-xs border border-cyan-400/15 rounded px-3 py-1.5 bg-[#04101E]/40"
                        >
                            <span className="text-cyan-300">{s.folio}</span>
                            <span className="text-cyan-500">{s.similitudPorcentaje.toFixed(1)}% similitud</span>
                        </li>
                    ))}
                </ul>
            </div>

            {!esPendiente && (
                <p className="text-[11px] text-cyan-500/70 italic flex items-center gap-1.5 mb-2">
                    <Clock3 size={12} />
                    {alerta.estado === 'REVISADA' ? 'Revisada' : 'Descartada'} por {alerta.atendidaPorNombre ?? 'un usuario'}
                    {alerta.fechaAtencion && ` el ${formatFechaHora(alerta.fechaAtencion)}`}
                    {alerta.motivoDescarte && ` — "${alerta.motivoDescarte}"`}
                </p>
            )}

            {esPendiente && !mostrarDescartar && (
                <div className="flex flex-wrap gap-3 pt-2">
                    <NeonButton variant="success" icon={<CheckCircle2 size={13} />} onClick={() => setConfirmarRevisar(true)}>
                        Marcar como revisada
                    </NeonButton>
                    <NeonButton variant="danger" icon={<XCircle size={13} />} onClick={() => setMostrarDescartar(true)}>
                        Descartar
                    </NeonButton>
                </div>
            )}

            {mostrarDescartar && (
                <div className="flex flex-col gap-3 border border-red-400/20 rounded p-4 bg-red-500/[0.04] mt-2">
                    <NeonTextarea
                        label="Motivo del descarte (opcional)"
                        value={motivo}
                        onChange={e => setMotivo(e.target.value)}
                        rows={2}
                        placeholder="Ej: coincidencia irrelevante, delitos de distinta naturaleza…"
                    />
                    <div className="flex gap-3 justify-end">
                        <NeonButton variant="outline" onClick={() => setMostrarDescartar(false)} disabled={enviando}>
                            Cancelar
                        </NeonButton>
                        <NeonButton variant="danger" onClick={handleDescartar} disabled={enviando}>
                            {enviando ? 'Guardando…' : 'Confirmar descarte'}
                        </NeonButton>
                    </div>
                </div>
            )}

            <NeonConfirmModal
                isOpen={confirmarRevisar}
                title="Marcar alerta como revisada"
                message="¿Confirmas que revisaste el patrón detectado y le darás seguimiento?"
                confirmLabel="Revisar"
                confirmVariant="success"
                onConfirm={handleRevisar}
                onCancel={() => setConfirmarRevisar(false)}
            />
        </NeonPanel>
    )
}