// Formulario de "Registrar Firma Conductual del Caso" (módulo C — Inteligencia IA / Modus Operandi)
import { useState, useEffect } from 'react'
import { Brain, History, Save, Loader2, AlertTriangle, ShieldCheck, UserRound } from 'lucide-react'
import { NeonPanel }     from '../../ui/NeonPanel'
import { NeonInput }     from '../../ui/NeonInput'
import { NeonSelect }    from '../../ui/NeonSelect'
import { NeonTextarea }  from '../../ui/NeonTextarea'
import { NeonButton }    from '../../ui/NeonButton'
import { NeonConfirmModal } from '../../ui/NeonConfirmModal'
import { useNeonToast }  from '../../ui/NeonToast'
import { useFirmaConductual }  from '../../../hooks/useFirmaConductual'
import { useUsuarios }         from '../../../hooks/useUsuarios'
import {
    registrarFirmaConductual,
    type RegistrarFirmaConductualRequestDTO,
} from '../../../services/firmaConductualService'
import { HistorialFirmaConductual } from './HistorialFirmaConductual'
import { CAMPOS_FIRMA_CONDUCTUAL, type CampoFirmaConductualKey } from './index'

interface FirmaConductualProps {
    /** ID numérico del expediente, cuando se llega vinculado desde "Búsqueda y Casos" */
    expedienteIdInicial?: number
    /** Folio visible del expediente vinculado (solo informativo) */
    folioInicial?: string
}

type ValoresFormulario = Record<CampoFirmaConductualKey, string>

const VALORES_VACIOS: ValoresFormulario = {
    comportamientoPreDelictivo: '',
    metodoAproximacion: '',
    metodoAtaque: '',
    comportamientoPostDelictivo: '',
    elementosDistintivos: '',
}

export const FirmaConductual = ({ expedienteIdInicial, folioInicial }: FirmaConductualProps) => {
    const { showToast, ToastContainer } = useNeonToast()

    // ── Vinculación con el expediente ───────────────────────────────────────────
    const vinculadoDesdePanel = typeof expedienteIdInicial === 'number' && Number.isFinite(expedienteIdInicial)

    const [expedienteId, setExpedienteId] = useState<number | null>(
        vinculadoDesdePanel ? (expedienteIdInicial as number) : null,
    )
    const [folio, setFolio] = useState<string | null>(folioInicial ?? null)
    const [expedienteIdManual, setExpedienteIdManual] = useState<string>(
        expedienteId ? String(expedienteId) : '',
    )

    useEffect(() => {
        if (vinculadoDesdePanel) {
            setExpedienteId(expedienteIdInicial as number)
            setFolio(folioInicial ?? null)
            setExpedienteIdManual(String(expedienteIdInicial))
        }
    }, [expedienteIdInicial, folioInicial])

    const handleExpedienteIdManualChange = (valor: string) => {
        setExpedienteIdManual(valor)
        const parsed = Number(valor)
        if (valor.trim() !== '' && Number.isFinite(parsed) && parsed > 0) {
            setExpedienteId(Math.trunc(parsed))
            setFolio(null)
        } else {
            setExpedienteId(null)
        }
    }

    // ── Datos del expediente: firma vigente + historial ─────────────────────────
    const {
        firmaActual,
        historial,
        loadingActual,
        loadingHistorial,
        error: errorFirmaActual,
        recargarActual,
        cargarHistorial,
    } = useFirmaConductual(expedienteId)

    // ── Analistas disponibles ────────────────────────────────────────────────────
    const { usuarios, loading: loadingUsuarios, warning: usuariosWarning } = useUsuarios()
    const [analistaId, setAnalistaId] = useState<string>('')

    useEffect(() => {
        if (!analistaId && usuarios.length > 0) {
            setAnalistaId(String(usuarios[0].id))
        }
    }, [usuarios, analistaId])

    // ── Campos de la firma ───────────────────────────────────────────────────────
    const [valores, setValores] = useState<ValoresFormulario>(VALORES_VACIOS)
    const [prefilledParaExpediente, setPrefilledParaExpediente] = useState<number | null>(null)

    // Al cambiar de expediente, se limpia el formulario para no arrastrar datos de otro caso.
    useEffect(() => {
        setValores(VALORES_VACIOS)
        setPrefilledParaExpediente(null)
    }, [expedienteId])

    // Si el expediente ya tiene una firma vigente, se precarga como punto de partida editable.
    useEffect(() => {
        if (firmaActual && expedienteId && prefilledParaExpediente !== expedienteId) {
            setValores({
                comportamientoPreDelictivo: firmaActual.comportamientoPreDelictivo ?? '',
                metodoAproximacion: firmaActual.metodoAproximacion ?? '',
                metodoAtaque: firmaActual.metodoAtaque ?? '',
                comportamientoPostDelictivo: firmaActual.comportamientoPostDelictivo ?? '',
                elementosDistintivos: firmaActual.elementosDistintivos ?? '',
            })
            setPrefilledParaExpediente(expedienteId)
        }
    }, [firmaActual, expedienteId, prefilledParaExpediente])

    const actualizarCampo = (key: CampoFirmaConductualKey, valor: string) => {
        setValores(prev => ({ ...prev, [key]: valor }))
    }

    const hayAlMenosUnCampoCompleto = CAMPOS_FIRMA_CONDUCTUAL.some(c => valores[c.key].trim() !== '')

    // ── Historial ────────────────────────────────────────────────────────────────
    const [mostrarHistorial, setMostrarHistorial] = useState(false)

    const toggleHistorial = () => {
        const nuevoValor = !mostrarHistorial
        setMostrarHistorial(nuevoValor)
        if (nuevoValor) cargarHistorial()
    }

    // ── Envío del formulario ─────────────────────────────────────────────────────
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [mostrarConfirmNuevaVersion, setMostrarConfirmNuevaVersion] = useState(false)

    const construirPayload = (): RegistrarFirmaConductualRequestDTO => ({
        expedienteId: expedienteId as number,
        analistaId: Number(analistaId),
        comportamientoPreDelictivo: valores.comportamientoPreDelictivo.trim() || undefined,
        metodoAproximacion: valores.metodoAproximacion.trim() || undefined,
        metodoAtaque: valores.metodoAtaque.trim() || undefined,
        comportamientoPostDelictivo: valores.comportamientoPostDelictivo.trim() || undefined,
        elementosDistintivos: valores.elementosDistintivos.trim() || undefined,
    })

    const ejecutarRegistro = async () => {
        setMostrarConfirmNuevaVersion(false)
        setIsSubmitting(true)
        try {
            const resultado = await registrarFirmaConductual(construirPayload())
            showToast(`Firma conductual registrada correctamente (versión ${resultado.version}).`, 'success')
            await recargarActual()
            if (mostrarHistorial) await cargarHistorial()
        } catch (err) {
            const mensaje = err instanceof Error ? err.message : 'Error desconocido'
            showToast(`No se pudo registrar la firma conductual: ${mensaje}`, 'error')
        } finally {
            setIsSubmitting(false)
        }
    }

    const handleRegistrarClick = () => {
        if (!expedienteId) {
            showToast('Vincula un expediente (desde "Búsqueda y Casos") o ingresa su ID antes de registrar la firma conductual.', 'error')
            return
        }
        if (!analistaId) {
            showToast('Selecciona el analista responsable del registro.', 'error')
            return
        }
        if (!hayAlMenosUnCampoCompleto) {
            showToast('Debes completar al menos un campo de la firma conductual.', 'error')
            return
        }
        if (firmaActual) {
            setMostrarConfirmNuevaVersion(true)
            return
        }
        ejecutarRegistro()
    }

    const handleLimpiar = () => {
        setValores(VALORES_VACIOS)
    }

    const usuarioOptions = [
        { value: '', label: loadingUsuarios ? 'Cargando…' : '— Seleccione un analista —' },
        ...usuarios.map(u => ({ value: String(u.id), label: `${u.nombre} — ${u.identificacion}` })),
    ]

    // ─── Render ───────────────────────────────────────────────────────────────

    return (
        <div className="pb-6 space-y-4">
            <ToastContainer />

            {usuariosWarning && (
                <div className="flex items-center gap-2 px-3 py-2 border border-amber-400/40 rounded bg-amber-400/5 text-[10px] uppercase tracking-wider text-amber-400">
                    <AlertTriangle size={12} style={{ filter: 'drop-shadow(0 0 4px rgba(255,170,0,0.6))' }} />
                    {usuariosWarning}
                </div>
            )}

            <NeonPanel className="space-y-6">

                {/* ══ ENCABEZADO ═══════════════════════════════════════════ */}
                <div className="flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-center gap-2.5">
                        <Brain size={18} className="text-cyan-400" style={{ filter: 'drop-shadow(0 0 6px rgba(51,153,255,0.7))' }} />
                        <div>
                            <h3
                                className="text-sm uppercase tracking-[0.15em] text-cyan-300 font-semibold"
                                style={{ textShadow: '0 0 10px rgba(51,153,255,0.7), 0 0 20px rgba(51,153,255,0.4)' }}
                            >
                                Registrar Firma Conductual del Caso
                            </h3>
                            <p className="text-[10px] text-cyan-500/70 mt-1.5">
                                Perfil de comportamiento del sospechoso — Módulo C, Inteligencia IA / Modus Operandi
                            </p>
                        </div>
                    </div>

                    <NeonButton
                        variant="outline"
                        icon={<History size={13} />}
                        onClick={toggleHistorial}
                        disabled={!expedienteId}
                        title={!expedienteId ? 'Vincula un expediente para ver su historial' : undefined}
                    >
                        {mostrarHistorial ? 'Ocultar Historial' : 'Ver Historial'}
                    </NeonButton>
                </div>

                {/* ══ VINCULACIÓN CON EL EXPEDIENTE ═══════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Expediente <span className="text-red-400">*</span>
                    </div>

                    {vinculadoDesdePanel ? (
                        <div className="flex items-center gap-2 px-3 py-2.5 border border-cyan-400/40 rounded bg-cyan-400/5 text-[12px] text-cyan-300">
                            <ShieldCheck size={14} className="text-emerald-400 shrink-0" />
                            Expediente vinculado desde Búsqueda y Casos:{' '}
                            <strong className="font-mono">{folio ?? `#${expedienteId}`}</strong>
                        </div>
                    ) : (
                        <div className="max-w-xs">
                            <NeonInput
                                label="ID del Expediente"
                                type="number"
                                min={1}
                                placeholder="Ej: 12"
                                value={expedienteIdManual}
                                onChange={e => handleExpedienteIdManualChange(e.target.value)}
                            />
                            <p className="mt-1.5 text-[10px] text-cyan-700">
                                Abre un caso desde "Búsqueda y Casos" para vincularlo automáticamente, o ingresa aquí su ID.
                            </p>
                        </div>
                    )}
                </div>

                {/* ══ ESTADO ACTUAL DE LA FIRMA ════════════════════════════════ */}
                {expedienteId && loadingActual && (
                    <div className="flex items-center gap-2 text-[11px] text-cyan-500 uppercase tracking-wider">
                        <Loader2 size={13} className="animate-spin" /> Consultando firma conductual del expediente…
                    </div>
                )}

                {expedienteId && !loadingActual && errorFirmaActual && (
                    <div className="flex items-center gap-2 px-3 py-2 border border-red-400/40 rounded bg-red-400/5 text-[11px] text-red-300">
                        <AlertTriangle size={13} className="shrink-0" />
                        {errorFirmaActual}
                    </div>
                )}

                {expedienteId && !loadingActual && !errorFirmaActual && firmaActual && (
                    <div className="p-3 border border-emerald-400/40 rounded bg-emerald-400/5">
                        <div className="flex items-center gap-2 text-[11px] uppercase tracking-wider text-emerald-300 font-medium">
                            <ShieldCheck size={13} />
                            Firma vigente — versión {firmaActual.version}
                        </div>
                        <p className="mt-1.5 text-[11px] text-emerald-200/70">
                            Registrada el {new Date(firmaActual.fechaRegistro).toLocaleString('es-VE')}. Los campos se
                            precargaron con estos datos — puedes editarlos y registrar de nuevo para crear la versión{' '}
                            {firmaActual.version + 1}.
                        </p>
                    </div>
                )}

                {expedienteId && !loadingActual && !errorFirmaActual && !firmaActual && (
                    <div className="p-3 border border-cyan-400/20 rounded bg-cyan-400/5 text-[11px] text-cyan-400/80">
                        Este expediente aún no tiene una firma conductual registrada.
                    </div>
                )}

                {/* ══ ANALISTA RESPONSABLE ═════════════════════════════════════ */}
                <div className="max-w-md">
                    <NeonSelect
                        label="Analista Responsable"
                        required
                        options={usuarioOptions}
                        value={analistaId}
                        onChange={e => setAnalistaId(e.target.value)}
                        disabled={loadingUsuarios}
                    />
                    <p className="mt-1.5 flex items-center gap-1.5 text-[10px] text-cyan-700">
                        <UserRound size={11} /> Quien registra queda asociado como analista de esta versión.
                    </p>
                </div>

                {/* ══ CAMPOS DE LA FIRMA CONDUCTUAL ═══════════════════════════ */}
                <div className="space-y-4">
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 font-medium">
                        Patrón de Comportamiento <span className="text-red-400">*</span>
                        <span className="ml-2 normal-case text-[10px] text-cyan-600 font-normal">
                            (completa al menos un campo)
                        </span>
                    </div>

                    {CAMPOS_FIRMA_CONDUCTUAL.map(campo => (
                        <div key={campo.key}>
                            <NeonTextarea
                                label={campo.label}
                                placeholder={campo.placeholder}
                                rows={3}
                                showCount
                                maxCount={1000}
                                value={valores[campo.key]}
                                onChange={e => actualizarCampo(campo.key, e.target.value)}
                            />
                            <p className="mt-1 text-[10px] text-cyan-700">{campo.helper}</p>
                        </div>
                    ))}
                </div>

                {/* ══ ACCIONES ═════════════════════════════════════════════════ */}
                <div className="flex flex-col sm:flex-row justify-end gap-3 pt-2">
                    <NeonButton variant="outline" onClick={handleLimpiar} disabled={isSubmitting}>
                        Limpiar
                    </NeonButton>
                    <NeonButton
                        variant="primary"
                        onClick={handleRegistrarClick}
                        disabled={isSubmitting || !expedienteId}
                        icon={isSubmitting ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
                    >
                        {isSubmitting ? 'Registrando…' : 'Registrar Firma Conductual'}
                    </NeonButton>
                </div>
            </NeonPanel>

            {mostrarHistorial && expedienteId && (
                <HistorialFirmaConductual
                    historial={historial}
                    loading={loadingHistorial}
                    onClose={() => setMostrarHistorial(false)}
                />
            )}

            <NeonConfirmModal
                isOpen={mostrarConfirmNuevaVersion}
                title="⚠ Ya existe una firma vigente"
                message={
                    firmaActual
                        ? `Este expediente ya tiene una firma conductual vigente (versión ${firmaActual.version}). Registrar de nuevo creará la versión ${firmaActual.version + 1} y la actual pasará al historial. ¿Deseas continuar?`
                        : '¿Deseas continuar?'
                }
                confirmLabel="Registrar Nueva Versión"
                onConfirm={ejecutarRegistro}
                onCancel={() => setMostrarConfirmNuevaVersion(false)}
            />
        </div>
    )
}