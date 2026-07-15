type SubTab = 'analisis' | 'validar' | 'buscar' | 'futuras'

interface InteligenciaSubTabsProps {
    activo: SubTab
    onChange: (tab: SubTab) => void
}

const SUB_TABS: { id: SubTab; label: string }[] = [
    { id: 'analisis', label: 'Análisis de MO' },
    { id: 'validar',  label: 'Validar MO' },
]

export const InteligenciaSubTabs = ({ activo, onChange }: InteligenciaSubTabsProps) => (
    <div className="flex gap-1 border-b border-cyan-400/20 mb-5">
        {SUB_TABS.map(tab => {
            const isActive = activo === tab.id
            return (
                <button
                    key={tab.id}
                    onClick={() => onChange(tab.id)}
                    className={`px-4 py-2 text-[11px] uppercase tracking-wider transition-all border-b-2 ${
                        isActive
                            ? 'text-cyan-300 border-cyan-400'
                            : 'text-cyan-600 border-transparent hover:text-cyan-400'
                    }`}
                >
                    {tab.label}
                </button>
            )
        })}
    </div>
)