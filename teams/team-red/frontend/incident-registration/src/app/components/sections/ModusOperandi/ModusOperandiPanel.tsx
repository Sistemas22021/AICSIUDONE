import { useState } from 'react'
import { X, ThumbsUp, Pencil, ThumbsDown, History as HistoryIcon, RefreshCw } from 'lucide-react'
import { NeonButton } from '../../ui/NeonButton'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonConfirmModal } from '../../ui/NeonConfirmModal'
import { useNeonToast } from '../../ui/NeonToast'
import { usePropuestaModusOperandi } from '../../../hooks/usePropuestaModusOperandi'
import {
    fetchHistorialMO,
    aprobarPropuestaMO,
    corregirPropuestaMO,
    rechazarPropuestaMO,
} from '../../../services/modusOperandiService'
import { PropuestaMOCard } from './PropuestaMOCard'
import { HistorialMOList } from './HistorialMOList'
import { EstadoAnalisisMO } from '../../ui/EstadoAnalisisMO'
import type { PropuestaModusOperandi } from '../../../types/api.types'

type FormularioActivo = null | 'corregir' | 'rechazar'

interface ModusOperandiPanelProps {
    isOpen: boolean
    onClose: () => void
    expedienteId: string
    folioExpediente: string
    /** Id del Analista Criminal autenticado. En este proyecto no hay contexto de sesión aún,
     *  así que se recibe como prop desde quien abre el panel (p. ej. sesión guardada en localStorage). */
    analistaId: number
}

/** Panel principal de HU3: propuesta vigente + historial + acciones de validación experta. */
export const ModusOperandiPanel = ({
                                       isOpen,
                                       onClose,
                                       expedienteId,
                                       folioExpediente,
                                       analistaId,
                                   }: ModusOperandiPanelProps) => {
    const { propuesta, estadoCarga, refetch } = usePropuestaModusOperandi(isOpen ? expedienteId : null)
    const { showToast, ToastContainer } = useNeonToast()

    const [mostrarHistorial, setMostrarHistorial] = useState(false)
    const [historial, setHistorial] = useState<PropuestaModusOperandi[]>([])
    const [cargandoHistorial, setCargandoHistorial] = useState(false)

    const [formularioActivo, setFormularioActivo] = useState<FormularioActivo>(null)
    const [confirmarAprobar, setConfirmarAprobar] = useState(false)
    const [enviando, setEnviando] = useState(false)

    const [caracteristicasComunes, setCaracteristicasComunes] = useState('')
    const [posibleFirma, setPosibleFirma] = useState('')
    const [consistenciaHorarioZona, setConsistenciaHorarioZona] = useState('')
    const [justificacion, setJustificacion] = useState('')
    const [clasificacionManual, setClasificacionManual] = useState('')

    if (!isOpen) return null

    const iniciarCorregir = () => {
        if (!propuesta) return
        setCaracteristicasComunes(propuesta.caracteristicasComunes ?? '')
        setPosibleFirma(propuesta.posibleFirma ?? '')
        setConsistenciaHorarioZona(propuesta.consistenciaHorarioZona ?? '')
        setJustificacion('')
        setFormularioActivo('corregir')
    }

    const iniciarRechazar = () => {
        setClasificacionManual('')
        setJustificacion('')
        setFormularioActivo('rechazar')
    }

    const cargarHistorial = async () => {
        setCargandoHistorial(true)
        try {
            setHistorial(await fetchHistorialMO(expedienteId))
            setMostrarHistorial(true)
        } catch (err) {
            console.error('[ModusOperandiPanel] Error cargando historial', err)
            showToast('No se pudo cargar el historial de MO.', 'error')
        } finally {
            setCargandoHistorial(false)
        }
    }

    const handleAprobar = async () => {
        if (!propuesta) return
        setEnviando(true)
        try {
            await aprobarPropuestaMO(expedienteId, propuesta.id, { analistaId })
            showToast('Propuesta de MO aprobada.', 'success')
            setConfirmarAprobar(false)
            refetch()
        } catch (err) {
            console.error('[ModusOperandiPanel] Error aprobando propuesta', err)
            showToast('No se pudo aprobar la propuesta.', 'error')
        } finally {
            setEnviando(false)
        }
    }

    const handleCorregir = async () => {
        if (!propuesta) return
        if (!justificacion.trim()) {
            showToast('La justificación es obligatoria para corregir.', 'error')
            return
        }
        setEnviando(true)
        try {
            await corregirPropuestaMO(expedienteId, propuesta.id, {
                analistaId,
                caracteristicasComunes,
                posibleFirma,
                consistenciaHorarioZona,
                justificacion,
            })
            showToast('Propuesta de MO corregida.', 'success')
            setFormularioActivo(null)
            refetch()
        } catch (err) {
            console.error('[ModusOperandiPanel] Error corrigiendo propuesta', err)
            showToast('No se pudo corregir la propuesta.', 'error')
        } finally {
            setEnviando(false)
        }
    }

    const handleRechazar = async () => {
        if (!propuesta) return
        if (!clasificacionManual.trim() || !justificacion.trim()) {
            showToast('Clasificación manual y justificación son obligatorias.', 'error')
            return
        }
        setEnviando(true)
        try {
            await rechazarPropuestaMO(expedienteId, propuesta.id, {
                analistaId,
                clasificacionManual,
                justificacion,
            })
            showToast('Propuesta de MO rechazada.', 'success')
            setFormularioActivo(null)
            refetch()
        } catch (err) {
            console.error('[ModusOperandiPanel] Error rechazando propuesta', err)
            showToast('No se pudo rechazar la propuesta.', 'error')
        } finally {
            setEnviando(false)
        }
    }

    const puedeValidar = !!propuesta && !propuesta.revisadoPorExperto && propuesta.estado !== 'SIN_COINCIDENCIAS'

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-[#020818]/92 backdrop-blur-md" onClick={onClose} />

            <div
                className="relative w-full max-w-3xl max-h-[92vh] flex flex-col rounded border-2 border-cyan-400/60 bg-[#060B10]/98 backdrop-blur-sm overflow-hidden"
                style={{ boxShadow: '0 4px 30px rgba(51,153,255,0.5), 0 8px 60px rgba(51,153,255,0.25)' }}
            >
                {/* Cabecera */}
                <div className="flex-shrink-0 px-6 pt-5 pb-4 border-b border-cyan-400/20 flex items-start justify-between gap-4">
                    <div>
                        <div
                            className="text-sm uppercase tracking-[0.18em] text-cyan-300 font-semibold mb-1"
                            style={{ textShadow: '0 0 12px rgba(51,153,255,0.7)', fontFamily: 'Orbitron, monospace' }}
                        >
                            Modus Operandi — {folioExpediente}
                        </div>
                        <EstadoAnalisisMO estadoCarga={estadoCarga} estado={propuesta?.estado} />
                    </div>
                    <div className="flex items-center gap-2 flex-shrink-0">
                        <button
                            onClick={refetch}
                            className="p-2 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 transition-all"
                            title="Actualizar"
                        >
                            <RefreshCw size={14} />
                        </button>
                        <button
                            onClick={onClose}
                            className="p-2 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 transition-all"
                        >
                            <X size={16} />
                        </button>
                    </div>
                </div>

                {/* Cuerpo */}
                <div className="flex-1 overflow-auto px-6 py-5 flex flex-col gap-5">
                    {estadoCarga === 'analizando' && (
                        <p className="text-sm text-cyan-500/80">
                            El sistema está generando el análisis de Modus Operandi para este expediente. Esto puede tardar
                            unos segundos (HU2 corre de forma asíncrona en el servidor)…
                        </p>
                    )}

                    {estadoCarga === 'sin_analisis' && (
                        <p className="text-sm text-cyan-500/80">
                            No se generó ninguna propuesta de MO para este expediente todavía. Verifica que el expediente
                            tenga descripción del hecho, o intenta actualizar.
                        </p>
                    )}

                    {estadoCarga === 'error' && (
                        <p className="text-sm text-red-400">
                            Ocurrió un error consultando el análisis de MO. Intenta actualizar.
                        </p>
                    )}

                    {propuesta && <PropuestaMOCard propuesta={propuesta} />}

                    {propuesta?.revisadoPorExperto && (
                        <p className="text-[11px] text-cyan-500/70 italic">
                            Esta propuesta ya fue revisada por un experto; el sistema no la sobreescribirá automáticamente
                            en futuros re-análisis.
                        </p>
                    )}

                    {/* Acciones de validación (HU3) */}
                    {puedeValidar && formularioActivo === null && (
                        <div className="flex flex-wrap gap-3">
                            <NeonButton variant="success" icon={<ThumbsUp size={13} />} onClick={() => setConfirmarAprobar(true)}>
                                Aprobar
                            </NeonButton>
                            <NeonButton variant="outline" icon={<Pencil size={13} />} onClick={iniciarCorregir}>
                                Corregir
                            </NeonButton>
                            <NeonButton variant="danger" icon={<ThumbsDown size={13} />} onClick={iniciarRechazar}>
                                Rechazar
                            </NeonButton>
                        </div>
                    )}

                    {/* Formulario: Corregir */}
                    {formularioActivo === 'corregir' && (
                        <div className="flex flex-col gap-3 border border-cyan-400/20 rounded p-4 bg-[#04101E]/40">
                            <NeonTextarea
                                label="Características comunes"
                                value={caracteristicasComunes}
                                onChange={e => setCaracteristicasComunes(e.target.value)}
                                rows={2}
                            />
                            <NeonTextarea
                                label="Posible firma del perpetrador"
                                value={posibleFirma}
                                onChange={e => setPosibleFirma(e.target.value)}
                                rows={2}
                            />
                            <NeonTextarea
                                label="Consistencia de horario / zona"
                                value={consistenciaHorarioZona}
                                onChange={e => setConsistenciaHorarioZona(e.target.value)}
                                rows={2}
                            />
                            <NeonTextarea
                                label="Justificación"
                                required
                                value={justificacion}
                                onChange={e => setJustificacion(e.target.value)}
                                rows={2}
                                placeholder="Explica por qué corriges la propuesta generada por la IA…"
                            />
                            <div className="flex gap-3 justify-end">
                                <NeonButton variant="outline" onClick={() => setFormularioActivo(null)} disabled={enviando}>
                                    Cancelar
                                </NeonButton>
                                <NeonButton variant="success" onClick={handleCorregir} disabled={enviando}>
                                    {enviando ? 'Guardando…' : 'Guardar corrección'}
                                </NeonButton>
                            </div>
                        </div>
                    )}

                    {/* Formulario: Rechazar */}
                    {formularioActivo === 'rechazar' && (
                        <div className="flex flex-col gap-3 border border-red-400/20 rounded p-4 bg-red-500/[0.04]">
                            <NeonTextarea
                                label="Clasificación manual del Modus Operandi"
                                required
                                value={clasificacionManual}
                                onChange={e => setClasificacionManual(e.target.value)}
                                rows={2}
                                placeholder="Ingresa la clasificación correcta a mano…"
                            />
                            <NeonTextarea
                                label="Justificación"
                                required
                                value={justificacion}
                                onChange={e => setJustificacion(e.target.value)}
                                rows={2}
                                placeholder="Explica por qué rechazas la propuesta generada por la IA…"
                            />
                            <div className="flex gap-3 justify-end">
                                <NeonButton variant="outline" onClick={() => setFormularioActivo(null)} disabled={enviando}>
                                    Cancelar
                                </NeonButton>
                                <NeonButton variant="danger" onClick={handleRechazar} disabled={enviando}>
                                    {enviando ? 'Guardando…' : 'Confirmar rechazo'}
                                </NeonButton>
                            </div>
                        </div>
                    )}

                    {/* Historial (HU3, CA5) */}
                    <div className="pt-2 border-t border-cyan-400/10">
                        {!mostrarHistorial ? (
                            <NeonButton
                                variant="ghost"
                                icon={<HistoryIcon size={13} />}
                                onClick={cargarHistorial}
                                disabled={cargandoHistorial}
                            >
                                {cargandoHistorial ? 'Cargando historial…' : 'Ver historial de versiones'}
                            </NeonButton>
                        ) : (
                            <>
                                <div className="text-[11px] uppercase tracking-wider text-cyan-400 mb-2">Historial</div>
                                <HistorialMOList historial={historial} />
                            </>
                        )}
                    </div>
                </div>
            </div>

            <NeonConfirmModal
                isOpen={confirmarAprobar}
                title="Aprobar Modus Operandi"
                message="¿Confirmas que apruebas esta propuesta tal como fue generada por la IA?"
                confirmLabel="Aprobar"
                confirmVariant="success"
                onConfirm={handleAprobar}
                onCancel={() => setConfirmarAprobar(false)}
            />

            <ToastContainer />
        </div>
    )
}