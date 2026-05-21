//Pestañas A / B / C para navegar entre los tres módulos
type ActiveTab = 'registro' | 'escena' | 'inteligencia'

interface TabNavigationProps {
  activeTab: ActiveTab
  onTabChange: (tab: ActiveTab) => void
}

const TABS: { id: ActiveTab; label: string }[] = [
  { id: 'registro',     label: 'A — REGISTRO DEL HECHO' },
  { id: 'escena',       label: 'B — ESCENA Y EVIDENCIA' },
  { id: 'inteligencia', label: 'C — INTELIGENCIA IA / MODUS OPERANDI' },
]

export const TabNavigation = ({ activeTab, onTabChange }: TabNavigationProps) => (
  <div className="w-[95%] max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8 pb-3">
    <div className="flex gap-1 border-b-2 border-cyan-400/30 overflow-x-auto scrollbar-thin">
      {TABS.map((tab) => {
        const isActive = activeTab === tab.id
        return (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={`
              px-3 sm:px-5 py-2.5 text-[10px] sm:text-[11px] uppercase tracking-[0.12em]
              transition-all duration-300 relative font-medium
              rounded-t whitespace-nowrap flex-shrink-0
              ${
                isActive
                  ? 'text-cyan-300 bg-cyan-400/15 border-2 border-b-0 border-cyan-400'
                  : 'text-cyan-500 hover:text-cyan-300 hover:bg-cyan-400/5 border-2 border-transparent'
              }
            `}
            style={
              isActive
                ? {
                    textShadow: '0 0 12px rgba(51,153,255,0.7), 0 0 25px rgba(51,153,255,0.4)',
                    boxShadow: '0 -5px 20px rgba(51,153,255,0.25), inset 0 1px 3px rgba(51,153,255,0.08)',
                  }
                : undefined
            }
          >
            {tab.label}
            {isActive && (
              <div
                className="absolute bottom-0 left-0 right-0 h-0.5 bg-cyan-400"
                style={{ boxShadow: '0 0 15px rgba(51,153,255,0.9), 0 0 30px rgba(51,153,255,0.5)' }}
              />
            )}
          </button>
        )
      })}
    </div>
  </div>
)
