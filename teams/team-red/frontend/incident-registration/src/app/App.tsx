import { useState } from 'react'
import { FormProvider } from './context/FormContext'
import { Header } from './components/Header'
import { useNeonToast } from './components/ui/NeonToast'
import { TabNavigation } from './components/TabNavigation'
import { RegistroDelHecho } from './components/sections/RegistroDelHecho'
import { ExpedientesPanel } from './components/ExpedientesPanel'
import { EscenaDelCrimen } from './components/sections/EscenaDelCrimen'
import { InteligenciaMOTab } from './components/sections/ModusOperandi/InteligenciaMOTab'
import { ExpedienteActivoProvider } from './context/ExpedienteActivoContext'
import type { ExpedienteActivo } from './types/api.types'

type ActiveTab = 'registro' | 'escena' | 'inteligencia'

export default function App() {
    const [activeTab, setActiveTab]     = useState<ActiveTab>('registro')
    const [isPanelOpen, setIsPanelOpen] = useState(false)
    const [modoPanel, setModoPanel] = useState<'busqueda' | 'sellado'>('busqueda')
    const { showToast, ToastContainer } = useNeonToast()
    const [expedienteSeleccionado, setExpedienteSeleccionado] = useState<{
        id: number
        folio: string
    } | null>(null)

    const handleAbrirEnModuloB = (exp: ExpedienteActivo) => {
        setExpedienteSeleccionado({ id: Number(exp.id), folio: exp.folioCOPP })
        setActiveTab('escena')
        setIsPanelOpen(false)
    }

    const handleAbrirSellado = () => {
        setModoPanel('sellado')
        setIsPanelOpen(true)
    }

    const handleAbrirBusqueda = () => {
        setModoPanel('busqueda')
        setIsPanelOpen(true)
    }


    return (
        <FormProvider>
            <ExpedienteActivoProvider>
                <div className="min-h-screen text-cyan-300">
                    <ToastContainer />
                    <Header
                        onSearchClick={handleAbrirBusqueda}
                        onSellarClick={handleAbrirSellado}
                    />
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

                        {activeTab === 'inteligencia' && (
                            <div className="pb-6">
                                <InteligenciaMOTab />
                            </div>
                        )}
                    </div>

                    <ExpedientesPanel
                        isOpen={isPanelOpen}
                        onClose={() => setIsPanelOpen(false)}
                        onAbrirExpediente={handleAbrirEnModuloB}
                        modo={modoPanel}
                        showToast={showToast}
                    />
                </div>
            </ExpedienteActivoProvider>
        </FormProvider>
    )
}