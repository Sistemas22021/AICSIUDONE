import { useEffect, useRef, useState } from 'react'
import { ChevronDown, Check } from 'lucide-react'
import { useDelitoCategories } from '../../hooks/useDelitoCategories'

interface FiltroTipoDelitoMultiSelectProps {
    seleccionados: string[]
    onChange: (tipos: string[]) => void
}

/**
 * Dropdown de selección múltiple para filtrar expedientes por tipo de delito.
 * Reutiliza el catálogo ya expuesto por useDelitoCategories.
 */
export function FiltroTipoDelitoMultiSelect({ seleccionados, onChange }: FiltroTipoDelitoMultiSelectProps) {
    const { tipos, loading } = useDelitoCategories()
    const [abierto, setAbierto] = useState(false)
    const contenedorRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        function alClicFuera(e: MouseEvent) {
            if (contenedorRef.current && !contenedorRef.current.contains(e.target as Node)) {
                setAbierto(false)
            }
        }
        document.addEventListener('mousedown', alClicFuera)
        return () => document.removeEventListener('mousedown', alClicFuera)
    }, [])

    const alternar = (label: string) => {
        onChange(
            seleccionados.includes(label)
                ? seleccionados.filter(t => t !== label)
                : [...seleccionados, label],
        )
    }

    return (
        <div className="relative w-52" ref={contenedorRef}>
            <button
                type="button"
                onClick={() => setAbierto(v => !v)}
                className={[
                    'w-full flex items-center justify-between gap-2 px-3 py-2.5 border rounded',
                    'text-[10px] uppercase tracking-wider font-medium transition-all',
                    seleccionados.length > 0
                        ? 'border-cyan-400/70 bg-cyan-400/15 text-cyan-300'
                        : 'border-cyan-400/30 text-cyan-500 hover:border-cyan-400/60 hover:text-cyan-400',
                ].join(' ')}
            >
                <span className="truncate">
                    {seleccionados.length === 0 ? 'Tipo de delito' : `Delito (${seleccionados.length})`}
                </span>
                <ChevronDown size={12} className="flex-shrink-0" />
            </button>

            {abierto && (
                <div
                    className="absolute z-20 mt-1 w-full max-h-56 overflow-auto rounded border border-cyan-400/40 bg-[#080D13] p-2 space-y-1"
                    style={{ boxShadow: '0 4px 16px rgba(51,153,255,0.25)' }}
                >
                    {loading && <div className="text-[10px] text-cyan-600 px-1 py-1">Cargando catálogo…</div>}
                    {!loading && tipos.length === 0 && (
                        <div className="text-[10px] text-cyan-600 px-1 py-1">Sin tipos de delito disponibles</div>
                    )}
                    {tipos.map(tipo => {
                        const activo = seleccionados.includes(tipo.label)
                        return (
                            <label
                                key={tipo.value}
                                className="flex items-center gap-2 px-1.5 py-1 rounded cursor-pointer hover:bg-cyan-400/10"
                            >
                                <span
                                    className={[
                                        'w-3.5 h-3.5 flex-shrink-0 border rounded-sm flex items-center justify-center',
                                        activo ? 'border-cyan-400 bg-cyan-400/20' : 'border-cyan-400/40',
                                    ].join(' ')}
                                >
                                    {activo && <Check size={9} className="text-cyan-400" />}
                                </span>
                                <input type="checkbox" className="sr-only" checked={activo} onChange={() => alternar(tipo.label)} />
                                <span className="text-[11px] text-cyan-300 truncate">{tipo.label}</span>
                            </label>
                        )
                    })}
                </div>
            )}
        </div>
    )
}