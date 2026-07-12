import { useState } from 'react'
import { CasosPanel } from './CasosPanel'

type SubTab = 'analisis' | 'busqueda' | 'casos'

const SUB_TABS: { id: SubTab; label: string }[] = [
    { id: 'analisis', label: 'Análisis de MO' },
    { id: 'busqueda', label: 'Búsqueda semántica' },
    { id: 'casos', label: 'Casos' },
]

export const ModusOperandiIA = () => {
    const [subTab, setSubTab] = useState<SubTab>('analisis')

    return (
        <div className="pb-6 space-y-4">
            <div className="flex gap-1 border-b border-cyan-400/20">
                {SUB_TABS.map(t => (
                    <button
                        key={t.id}
                        onClick={() => setSubTab(t.id)}
                        className={`px-3 py-2 text-[10px] uppercase tracking-[0.12em] transition-colors ${
                            subTab === t.id ? 'text-cyan-300 border-b-2 border-cyan-400' : 'text-cyan-600 hover:text-cyan-400'
                        }`}
                    >
                        {t.label}
                    </button>
                ))}
            </div>

            {subTab === 'casos' && <CasosPanel />}
        </div>
    )
}