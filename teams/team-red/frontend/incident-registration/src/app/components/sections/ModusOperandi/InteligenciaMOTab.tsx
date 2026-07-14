import { useMemo, useState } from 'react'
import { Search, FileSearch } from 'lucide-react'
import { NeonInput } from '../../ui/NeonInput'
import { NeonPanel } from '../../ui/NeonPanel'
import { useExpedientesActivos } from '../../../hooks/useExpedientesActivos'
import { ModusOperandiContent } from './ModusOperandiContent'
import type { ExpedienteActivo } from '../../../types/api.types'

// TODO: reemplazar por el id del analista autenticado cuando exista un contexto de sesión.
const ANALISTA_ID_ACTUAL = 1

/** Contenido de la Pestaña C ("INTELIGENCIA IA / MODUS OPERANDI"): selector de expediente + HU2/HU3. */
export const InteligenciaMOTab = () => {
    const { expedientes, loading } = useExpedientesActivos()
    const [filtro, setFiltro] = useState('')
    const [seleccionado, setSeleccionado] = useState<ExpedienteActivo | null>(null)

    const filtrados = useMemo(() => {
        const q = filtro.trim().toLowerCase()
        if (!q) return expedientes
        return expedientes.filter(e => e.folioCOPP.toLowerCase().includes(q) || e.tipoDelito?.toLowerCase().includes(q))
    }, [filtro, expedientes])

    if (seleccionado) {
        return (
            <NeonPanel>
                <button
                    onClick={() => setSeleccionado(null)}
                    className="mb-4 text-[11px] uppercase tracking-wider text-cyan-500 hover:text-cyan-300 transition-colors"
                >
                    ← Volver al listado
                </button>
                <ModusOperandiContent
                    expedienteId={seleccionado.id}
                    folioExpediente={seleccionado.folioCOPP}
                    analistaId={ANALISTA_ID_ACTUAL}
                />
            </NeonPanel>
        )
    }

    return (
        <NeonPanel title="Inteligencia IA / Modus Operandi" subtitle="Selecciona un expediente para ver o validar su análisis de MO">
            <NeonInput
                placeholder="Buscar por folio o tipo de delito…"
                value={filtro}
                onChange={e => setFiltro(e.target.value)}
                className="mb-4"
            />

            {loading && <p className="text-sm text-cyan-500/70">Cargando expedientes…</p>}

            {!loading && filtrados.length === 0 && (
                <p className="text-sm text-cyan-500/70">No hay expedientes que coincidan con la búsqueda.</p>
            )}

            <div className="flex flex-col gap-2">
                {filtrados.map(exp => (
                    <button
                        key={exp.id}
                        onClick={() => setSeleccionado(exp)}
                        className="flex items-center justify-between gap-3 border border-cyan-400/15 rounded px-4 py-3 text-left hover:border-cyan-400/50 hover:bg-cyan-400/5 transition-all"
                    >
                        <div className="flex items-center gap-3 min-w-0">
                            <FileSearch size={14} className="text-cyan-500 flex-shrink-0" />
                            <div className="min-w-0">
                                <div className="text-sm text-cyan-200 truncate">{exp.folioCOPP}</div>
                                <div className="text-[11px] text-cyan-600 truncate">{exp.tipoDelito ?? 'Sin tipo'} — {exp.estatus}</div>
                            </div>
                        </div>
                        <Search size={13} className="text-cyan-600 flex-shrink-0" />
                    </button>
                ))}
            </div>
        </NeonPanel>
    )
}