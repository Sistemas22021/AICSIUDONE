import { useState } from 'react'
import { Search, FileSearch, Loader2 } from 'lucide-react'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonButton } from '../../ui/NeonButton'
import { useNeonToast } from '../../ui/NeonToast'
import { useAuth } from '../../../context/AuthContext'
import { buscarPatrones } from '../../../services/patronBusquedaService'
import { ModusOperandiContent } from './ModusOperandiContent'
import { FirmaConductualContent } from '../FirmaConductual/FirmaConductualContent'
import type { PatronBusquedaResultado } from '../../../types/api.types'

function formatFecha(iso: string | null): string {
    if (!iso) return 'Sin fecha'
    return new Date(iso).toLocaleDateString('es-VE', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

/**
 * HU "Buscar patrones por MO y firma conductual": permite buscar por MO
 * validado, por firma conductual, o por ambos combinados (CA1), usando
 * similitud semántica vía embeddings (CA2). Los resultados muestran folio,
 * tipo de delito, fecha, similitud e investigador asignado (CA3), ordenados
 * de forma descendente por similitud (CA4), y permiten abrir el expediente
 * completo (CA5).
 */
export const BuscarPatronesTab = () => {
    const { userId } = useAuth()
    const { showToast, ToastContainer } = useNeonToast()

    const [textoMO, setTextoMO] = useState('')
    const [textoFirma, setTextoFirma] = useState('')
    const [buscando, setBuscando] = useState(false)
    const [yaBusco, setYaBusco] = useState(false)
    const [resultados, setResultados] = useState<PatronBusquedaResultado[]>([])
    const [seleccionado, setSeleccionado] = useState<PatronBusquedaResultado | null>(null)

    const handleBuscar = async () => {
        if (!textoMO.trim() && !textoFirma.trim()) {
            showToast('Ingresa el MO validado, la firma conductual, o ambos.', 'error')
            return
        }
        setBuscando(true)
        try {
            const data = await buscarPatrones({
                textoMO: textoMO.trim() || undefined,
                textoFirmaConductual: textoFirma.trim() || undefined,
                limite: 20,
            })
            setResultados(data)
            setYaBusco(true)
        } catch (err) {
            console.error('[BuscarPatronesTab] Error buscando patrones', err)
            showToast('No se pudo completar la búsqueda de patrones.', 'error')
        } finally {
            setBuscando(false)
        }
    }

    if (seleccionado) {
        return (
            <div>
                <button
                    onClick={() => setSeleccionado(null)}
                    className="mb-4 text-[11px] uppercase tracking-wider text-cyan-500 hover:text-cyan-300 transition-colors"
                >
                    ← Volver a los resultados
                </button>
                <div className="flex flex-col gap-8">
                    <ModusOperandiContent
                        expedienteId={String(seleccionado.expedienteId)}
                        folioExpediente={seleccionado.folio}
                        analistaId={userId ?? ''}
                        soloLectura
                    />
                    <div className="pt-6 border-t border-cyan-400/10">
                        <FirmaConductualContent
                            expedienteId={String(seleccionado.expedienteId)}
                            folioExpediente={seleccionado.folio}
                            analistaId={userId ?? ''}
                        />
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-5">
            <ToastContainer />
            <p className="text-[12px] text-cyan-500/80">
                Busca casos con Modus Operandi validado y/o firma conductual similares mediante análisis
                semántico, para identificar series delictivas, posibles autores recurrentes y tendencias de
                criminalidad.
            </p>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <NeonTextarea
                    label="MO validado"
                    placeholder="Describe el modus operandi a buscar (forma de actuar, patrón detectado, etc.)…"
                    value={textoMO}
                    onChange={e => setTextoMO(e.target.value)}
                    rows={4}
                />
                <NeonTextarea
                    label="Firma conductual"
                    placeholder="Describe la firma conductual a buscar (comportamiento, elementos distintivos, etc.)…"
                    value={textoFirma}
                    onChange={e => setTextoFirma(e.target.value)}
                    rows={4}
                />
            </div>

            <div>
                <NeonButton
                    variant="primary"
                    icon={buscando ? <Loader2 size={13} className="animate-spin" /> : <Search size={13} />}
                    onClick={handleBuscar}
                    disabled={buscando}
                >
                    {buscando ? 'Buscando…' : 'Buscar patrones'}
                </NeonButton>
            </div>

            {yaBusco && !buscando && resultados.length === 0 && (
                <p className="text-sm text-cyan-500/70">No se encontraron casos con patrones similares.</p>
            )}

            {resultados.length > 0 && (
                <div className="flex flex-col gap-2">
                    <div className="text-[11px] uppercase tracking-wider text-cyan-400 mb-1">
                        {resultados.length} resultado{resultados.length === 1 ? '' : 's'}, ordenados por similitud
                    </div>
                    {resultados.map(r => (
                        <button
                            key={r.expedienteId}
                            onClick={() => setSeleccionado(r)}
                            className="flex items-center justify-between gap-3 border border-cyan-400/15 rounded px-4 py-3 text-left hover:border-cyan-400/50 hover:bg-cyan-400/5 transition-all"
                        >
                            <div className="flex items-center gap-3 min-w-0">
                                <FileSearch size={14} className="text-cyan-500 flex-shrink-0" />
                                <div className="min-w-0">
                                    <div className="text-sm text-cyan-200 truncate">{r.folio}</div>
                                    <div className="text-[11px] text-cyan-600 truncate">
                                        {r.tipoDelito ?? 'Sin tipo'} — {formatFecha(r.fechaHecho)} — Investigador: {r.investigadorAsignado}
                                    </div>
                                </div>
                            </div>
                            <div className="flex items-center gap-3 flex-shrink-0">
                                <div className="text-right">
                                    <div className="text-sm text-cyan-300 font-semibold">{r.similitudPorcentaje.toFixed(1)}%</div>
                                    <div className="text-[10px] text-cyan-600 uppercase tracking-wider">similitud</div>
                                </div>
                                <Search size={13} className="text-cyan-600" />
                            </div>
                        </button>
                    ))}
                </div>
            )}
        </div>
    )
}