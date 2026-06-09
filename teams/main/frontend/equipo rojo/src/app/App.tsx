import { useState } from 'react'
import { FormProvider } from './context/FormContext'
import { Header } from './components/Header'
import { TabNavigation } from './components/TabNavigation'
import { RegistroDelHecho } from './components/sections/RegistroDelHecho'
import { ExpedientesPanel } from './components/ExpedientesPanel'
import { EscenaDelCrimen } from './components/sections/EscenaDelCrimen'
import { NeonPanel } from './components/ui/NeonPanel'

type ActiveTab = 'registro' | 'escena' | 'inteligencia'

const PLACEHOLDER_LABELS: Record<Exclude<ActiveTab, 'registro'>, string> = {
    escena:       'B — ESCENA Y EVIDENCIA',
    inteligencia: 'C — INTELIGENCIA IA / MODUS OPERANDI',
}

export default function App() {
    const [activeTab, setActiveTab] = useState<ActiveTab>('registro')
    const [isPanelOpen, setIsPanelOpen] = useState(false)

    return (
        <FormProvider>
            <div className="min-h-screen text-cyan-300">
                <Header onSearchClick={() => setIsPanelOpen(true)} />
                <TabNavigation activeTab={activeTab} onTabChange={setActiveTab} />

                <div className="w-[95%] max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
                    {activeTab === 'registro' && <RegistroDelHecho />}

                    {activeTab === 'escena' && (
                        <div className="pb-6">
                            <EscenaDelCrimen />
                        </div>
                    )}

                    {activeTab === 'inteligencia' && (
                        <div className="pb-6">
                            <NeonPanel className="py-16">
                                <div className="text-center space-y-3 max-w-lg mx-auto">
                                    <div
                                        className="text-xs uppercase tracking-[0.2em] text-cyan-500"
                                        style={{ fontFamily: 'Orbitron, monospace' }}
                                    >
                                        {PLACEHOLDER_LABELS.inteligencia}
                                    </div>
                                    <div className="flex items-center justify-center gap-3 mt-4">
                                        <div className="h-px flex-1 bg-cyan-400/20" />
                                        <span className="text-[10px] uppercase tracking-[0.15em] text-cyan-400/50">
                      Módulo en desarrollo
                    </span>
                                        <div className="h-px flex-1 bg-cyan-400/20" />
                                    </div>
                                </div>
                            </NeonPanel>
                        </div>
                    )}
                </div>

                <ExpedientesPanel
                    isOpen={isPanelOpen}
                    onClose={() => setIsPanelOpen(false)}
                    onAbrirExpediente={(exp) => {
                        console.log('Abrir expediente:', exp.folioCOPP)
                        setIsPanelOpen(false)
                    }}
                />
            </div>
        </FormProvider>
    )
}