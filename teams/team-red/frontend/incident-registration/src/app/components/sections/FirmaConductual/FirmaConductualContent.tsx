import { useEffect, useState } from 'react'
import { History as HistoryIcon, Save } from 'lucide-react'
import { NeonButton } from '../../ui/NeonButton'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { useNeonToast } from '../../ui/NeonToast'
import {
    fetchFirmaVigente,
    fetchHistorialFirma,
    registrarFirmaConductual,
} from '../../../services/firmaConductualService'
import type { FirmaConductual } from '../../../types/api.types'

interface FirmaConductualContentProps {
    expedienteId: string
    folioExpediente: string
    analistaId: string
}

type CampoFirma =
    | 'comportamientoPreDelictivo'
    | 'metodoAproximacion'
    | 'metodoAtaque'
    | 'comportamientoPostDelictivo'
    | 'elementosDistintivos'

const CAMPOS: { key: CampoFirma; label: string; placeholder: string }[] = [
    { key: 'comportamientoPreDelictivo', label: 'Comportamiento pre-delictivo', placeholder: 'Qué hizo el autor antes de cometer el hecho…' },
    { key: 'metodoAproximacion', label: 'Método de aproximación', placeholder: 'Cómo se acercó a la víctima o al lugar…' },
    { key: 'metodoAtaque', label: 'Método de ataque', placeholder: 'Cómo ejecutó el hecho…' },
    { key: 'comportamientoPostDelictivo', label: 'Comportamiento post-delictivo', placeholder: 'Qué hizo después (huida, contacto posterior, etc.)…' },
    { key: 'elementosDistintivos', label: 'Elementos distintivos', placeholder: 'Firma personal, objetos dejados, rituales…' },
]

const VACIO: Record<CampoFirma, string> = {
    comportamientoPreDelictivo: '',
    metodoAproximacion: '',
    metodoAtaque: '',
    comportamientoPostDelictivo: '',
    elementosDistintivos: '',
}

export const FirmaConductualContent = ({ expedienteId, folioExpediente, analistaId }: FirmaConductualContentProps) => {
    const { showToast, ToastContainer } = useNeonToast()
    const [vigente, setVigente] = useState<FirmaConductual | null>(null)
    const [cargando, setCargando] = useState(true)
    const [guardando, setGuardando] = useState(false)
    const [campos, setCampos] = useState<Record<CampoFirma, string>>(VACIO)
    const [mostrarHistorial, setMostrarHistorial] = useState(false)
    const [historial, setHistorial] = useState<FirmaConductual[]>([])

    const cargar = async () => {
        setCargando(true)
        const f = await fetchFirmaVigente(expedienteId)
        setVigente(f)
        setCampos(f ? {
            comportamientoPreDelictivo: f.comportamientoPreDelictivo ?? '',
            metodoAproximacion: f.metodoAproximacion ?? '',
            metodoAtaque: f.metodoAtaque ?? '',
            comportamientoPostDelictivo: f.comportamientoPostDelictivo ?? '',
            elementosDistintivos: f.elementosDistintivos ?? '',
        } : VACIO)
        setCargando(false)
    }

    useEffect(() => { cargar() }, [expedienteId])

    const cargarHistorial = async () => {
        setHistorial(await fetchHistorialFirma(expedienteId))
        setMostrarHistorial(true)
    }

    const puedeGuardar = Object.values(campos).some(v => v.trim() !== '')

    const handleGuardar = async () => {
        if (!puedeGuardar) {
            showToast('Completa al menos un campo de la firma conductual.', 'error')
            return
        }
        setGuardando(true)
        try {
            const nueva = await registrarFirmaConductual(expedienteId, { analistaId, ...campos })
            showToast(vigente ? 'Firma conductual actualizada (nueva versión).' : 'Firma conductual registrada.', 'success')
            setVigente(nueva)
            setCampos(VACIO)
            if (mostrarHistorial) await cargarHistorial()
        } catch (err) {
            console.error('[FirmaConductualContent] Error guardando', err)
            showToast('No se pudo guardar la firma conductual.', 'error')
        } finally {
            setGuardando(false)
        }
    }

    return (
        <div className="flex flex-col gap-5">
            <div>
                <div
                    className="text-sm uppercase tracking-[0.18em] text-cyan-300 font-semibold mb-1"
                    style={{ textShadow: '0 0 12px rgba(51,153,255,0.7)', fontFamily: 'Orbitron, monospace' }}
                >
                    Firma Conductual — {folioExpediente}
                </div>
                {vigente && (
                    <p className="text-[11px] text-cyan-500/70">
                        Versión vigente: v{vigente.version} · registrada por {vigente.analistaNombre} el{' '}
                        {new Date(vigente.fechaRegistro).toLocaleString()}
                    </p>
                )}
            </div>

            {cargando ? (
                <p className="text-sm text-cyan-500/70">Cargando…</p>
            ) : (
                <div className="flex flex-col gap-3">
                    {CAMPOS.map(c => (
                        <NeonTextarea
                            key={c.key}
                            label={c.label}
                            placeholder={c.placeholder}
                            rows={2}
                            value={campos[c.key]}
                            onChange={e => setCampos(prev => ({ ...prev, [c.key]: e.target.value }))}
                        />
                    ))}
                    <div className="flex justify-end">
                        <NeonButton
                            icon={<Save size={13} />}
                            onClick={handleGuardar}
                            disabled={guardando || !puedeGuardar}
                        >
                            {guardando ? 'Guardando…' : vigente ? 'Guardar nueva versión' : 'Registrar firma conductual'}
                        </NeonButton>
                    </div>
                </div>
            )}

            <div className="pt-2 border-t border-cyan-400/10">
                {!mostrarHistorial ? (
                    <NeonButton variant="ghost" icon={<HistoryIcon size={13} />} onClick={cargarHistorial}>
                        Ver historial de versiones
                    </NeonButton>
                ) : (
                    <div className="flex flex-col gap-2">
                        <div className="text-[11px] uppercase tracking-wider text-cyan-400 mb-1">Historial</div>
                        {historial.map(h => (
                            <div key={h.id} className="border border-cyan-400/15 rounded p-2 text-[11px] text-cyan-400/80">
                                v{h.version} — {h.analistaNombre} — {new Date(h.fechaRegistro).toLocaleString()}
                                {h.vigente && <span className="ml-2 text-emerald-400">(vigente)</span>}
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <ToastContainer />
        </div>
    )
}
