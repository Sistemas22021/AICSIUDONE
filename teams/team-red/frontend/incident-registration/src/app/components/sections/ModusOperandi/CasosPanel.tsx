import { useState } from 'react'
import { NeonPanel } from '../../ui/NeonPanel'
import { NeonButton } from '../../ui/NeonButton'
import { NeonInput } from '../../ui/NeonInput'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonCheckbox } from '../../ui/NeonCheckbox'
import { useCasos } from '../../../hooks/useCasos'
import { useExpedientesActivos } from '../../../hooks/useExpedientesActivos'
import { crearCaso } from '../../../services/casoService'

export const CasosPanel = () => {
    const { casos, loading: cargandoCasos, refetch } = useCasos()
    const { expedientes, loading: cargandoExpedientes } = useExpedientesActivos()
    const [creadoPorId, setCreadoPorId] = useState('')
    const [motivo, setMotivo] = useState('')
    const [seleccionados, setSeleccionados] = useState<string[]>([])
    const [enviando, setEnviando] = useState(false)
    const [mensaje, setMensaje] = useState<{ tipo: 'ok' | 'error'; texto: string } | null>(null)

    const toggleSeleccion = (id: string) =>
        setSeleccionados(prev => prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id])

    const puedeEnviar = creadoPorId.trim() !== '' && motivo.trim() !== '' && seleccionados.length >= 2

    const handleCrear = async () => {
        if (!puedeEnviar) return
        setEnviando(true)
        setMensaje(null)
        try {
            await crearCaso({
                creadoPorIdentificacion: Number(creadoPorId),
                expedienteIds: seleccionados.map(Number),
                motivo,
            })
            setMensaje({ tipo: 'ok', texto: 'Caso creado correctamente.' })
            setMotivo('')
            setSeleccionados([])
            refetch()
        } catch (err) {
            console.error(err)
            setMensaje({ tipo: 'error', texto: 'No se pudo crear el caso. Verifique los datos e intente de nuevo.' })
        } finally {
            setEnviando(false)
        }
    }

    return (
        <div className="space-y-6">
            <NeonPanel title="Nuevo caso" subtitle="Agrupa dos o más expedientes relacionados">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                    <NeonInput
                        label="Identificación  del usuario que agrupa"
                        required
                        type="text"
                        value={creadoPorId}
                        onChange={(e) => setCreadoPorId(e.target.value)}
                    />
                </div>

                <NeonTextarea
                    label="Motivo de la agrupación"
                    required
                    showCount
                    maxCount={500}
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                    placeholder="Ej: Mismo patrón de robo nocturno por ventana trasera en la misma zona."
                />

                <div className="mt-4">
                    <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium mb-2 block">
                        Expedientes a agrupar (mínimo 2){seleccionados.length > 0 && ` — ${seleccionados.length} seleccionados`}
                    </label>
                    <div className="max-h-56 overflow-y-auto border border-cyan-400/30 rounded p-3 space-y-2">
                        {cargandoExpedientes && <p className="text-xs text-cyan-500">Cargando expedientes…</p>}
                        {expedientes.map(e => (
                            <NeonCheckbox
                                key={e.id}
                                label={`${e.folioCOPP} — ${e.tipoDelito ?? 'Sin tipo'} (${e.estatus})`}
                                checked={seleccionados.includes(e.id)}
                                onChange={() => toggleSeleccion(e.id)}
                            />
                        ))}
                    </div>
                </div>

                {mensaje && (
                    <p className={`text-xs mt-3 ${mensaje.tipo === 'ok' ? 'text-emerald-400' : 'text-red-400'}`}>
                        {mensaje.tipo === 'ok' ? '✓' : '⚠'} {mensaje.texto}
                    </p>
                )}

                <div className="mt-4 flex justify-end">
                    <NeonButton onClick={handleCrear} disabled={!puedeEnviar || enviando}>
                        {enviando ? 'Creando…' : 'Crear caso'}
                    </NeonButton>
                </div>
            </NeonPanel>

            <NeonPanel title="Casos registrados" subtitle={cargandoCasos ? 'Cargando…' : `${casos.length} caso(s)`}>
                {casos.length === 0 && !cargandoCasos && (
                    <p className="text-xs text-cyan-500">Todavía no hay casos agrupados.</p>
                )}
                <div className="space-y-3">
                    {casos.map(c => (
                        <div key={c.id} className="border border-cyan-400/25 rounded p-3">
                            <div className="flex justify-between items-center mb-1">
                                <span className="text-cyan-300 font-semibold text-sm">{c.codigoCaso}</span>
                                <span className="text-[10px] text-cyan-500">{new Date(c.fechaCreacion).toLocaleString()}</span>
                            </div>
                            <p className="text-xs text-cyan-400/80 mb-2">{c.motivo}</p>
                            <div className="flex flex-wrap gap-2">
                                {c.expedientes.map(e => (
                                    <span key={e.id} className="text-[10px] px-2 py-1 border border-cyan-400/30 rounded text-cyan-300">
                    {e.folio}
                  </span>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            </NeonPanel>
        </div>
    )
}