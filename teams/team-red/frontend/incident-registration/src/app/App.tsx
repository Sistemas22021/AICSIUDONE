import { useState } from 'react'
import { FormProvider } from './context/FormContext'
import { Header } from './components/Header'
import { TabNavigation } from './components/TabNavigation'
import { RegistroDelHecho } from './components/sections/RegistroDelHecho'
import { ExpedientesPanel } from './components/ExpedientesPanel'
import { EscenaDelCrimen } from './components/sections/EscenaDelCrimen'
import { ModusOperandiIA } from './components/sections/ModusOperandi/ModusOperandiIA'
import type { ExpedienteActivo } from './types/api.types'

type ActiveTab = 'registro' | 'escena' | 'inteligencia'

export default function App() {
    const [activeTab, setActiveTab]     = useState<ActiveTab>('registro')
    const [isPanelOpen, setIsPanelOpen] = useState(false)
    const [expedienteSeleccionado, setExpedienteSeleccionado] = useState<{
        id: number
        folio: string
    } | null>(null)

    const handleAbrirEnModuloB = (exp: ExpedienteActivo) => {
        setExpedienteSeleccionado({ id: Number(exp.id), folio: exp.folioCOPP })
        setActiveTab('escena')
        setIsPanelOpen(false)
    }

    return (
        <FormProvider>
            <div className="min-h-screen text-cyan-300">
                <Header onSearchClick={() => setIsPanelOpen(true)} />
                <TabNavigation activeTab={activeTab} onTabChange={setActiveTab} />

                <div className="w-[95%] max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
                    {activeTab === 'registro' && <RegistroDelHecho />}

                    {activeTab === 'escena' && (
                        <div className="pb-6">
                            <EscenaDelCrimen
                                expedienteIdInicial={expedienteSeleccionado?.id}
                                folioInicial={expedienteSeleccionado?.folio}
                            />
                        </div>
                    )}

                    {activeTab === 'inteligencia' && <ModusOperandiIA />}
                </div>

                <ExpedientesPanel
                    isOpen={isPanelOpen}
                    onClose={() => setIsPanelOpen(false)}
                    onAbrirExpediente={handleAbrirEnModuloB}
                />
            </div>
        </FormProvider>
    )
}