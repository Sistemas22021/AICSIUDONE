import { useState, useEffect } from 'react'
import { AuthProvider, useAuth } from './context/AuthContext'
import { FormProvider } from './context/FormContext'
import { Header } from './components/Header'
import { useNeonToast } from './components/ui/NeonToast'
import { TabNavigation } from './components/TabNavigation'
import { RoleGuard } from './components/guards/RoleGuard'
import { RegistroDelHecho } from './components/sections/RegistroDelHecho'
import { ExpedientesPanel } from './components/ExpedientesPanel'
import { EscenaDelCrimen } from './components/sections/EscenaDelCrimen'
import { InteligenciaMOTab } from './components/sections/ModusOperandi/InteligenciaMOTab'
import { ExpedienteActivoProvider } from './context/ExpedienteActivoContext'
import type { ExpedienteActivo } from './types/api.types'

type ActiveTab = 'registro' | 'escena' | 'inteligencia'

function MainApp() {
    const [activeTab, setActiveTab] = useState<ActiveTab>('registro')
    const [isPanelOpen, setIsPanelOpen] = useState(false)
    const [modoPanel, setModoPanel] = useState<'busqueda' | 'sellado'>('busqueda')
    const { showToast, ToastContainer } = useNeonToast()
    const [expedienteSeleccionado, setExpedienteSeleccionado] = useState<{
        id: number
        folio: string
    } | null>(null)

    const { isOficial, isAnalista } = useAuth()

    // Definir qué pestañas están disponibles según el rol
    const tabsDisponibles = [
        { id: 'registro' as ActiveTab, label: 'A — REGISTRO DEL HECHO', visible: isOficial },
        { id: 'escena' as ActiveTab, label: 'B — ESCENA Y EVIDENCIA', visible: isAnalista },
        { id: 'inteligencia' as ActiveTab, label: 'C — INTELIGENCIA IA / MODUS OPERANDI', visible: isAnalista },
    ]

    const tabsVisibles = tabsDisponibles.filter(tab => tab.visible)

    // Si la pestaña actual no es visible, redirigir a la primera disponible
    useEffect(() => {
        const tabActualVisible = tabsVisibles.some(tab => tab.id === activeTab)
        if (!tabActualVisible && tabsVisibles.length > 0) {
            setActiveTab(tabsVisibles[0].id)
        }
    }, [activeTab, tabsVisibles])

    const handleAbrirEnModuloB = (exp: ExpedienteActivo) => {
        if (!isAnalista) {
            showToast('Solo los Analistas pueden acceder a la sección de Escenas.', 'error')
            return
        }
        setExpedienteSeleccionado({ id: Number(exp.id), folio: exp.folioCOPP })
        setActiveTab('escena')
        setIsPanelOpen(false)
    }

    const handleAbrirSellado = () => {
        if (!isOficial) {
            showToast('Solo los Oficiales pueden sellar expedientes.', 'error')
            return
        }
        setModoPanel('sellado')
        setIsPanelOpen(true)
    }

    const handleAbrirBusqueda = () => {
        setModoPanel('busqueda')
        setIsPanelOpen(true)
    }

    return (
        <div className="min-h-screen text-cyan-300">
            <ToastContainer />
            <Header
                onSearchClick={handleAbrirBusqueda}
                onSellarClick={handleAbrirSellado}
            />

            <TabNavigation
                activeTab={activeTab}
                onTabChange={setActiveTab}
                tabs={tabsVisibles}
            />

            <div className="w-[95%] max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8">
                {/* Tab A: Registro del Hecho - SOLO OFICIAL */}
                {activeTab === 'registro' && (
                    <RoleGuard allowedRoles={['OFICIAL']}>
                        <RegistroDelHecho />
                    </RoleGuard>
                )}

                {/* Tab B: Escena y Evidencia - SOLO ANALISTA */}
                {activeTab === 'escena' && (
                    <RoleGuard allowedRoles={['ANALISTA']}>
                        <div className="pb-6">
                            <EscenaDelCrimen
                                expedienteIdInicial={expedienteSeleccionado?.id}
                                folioInicial={expedienteSeleccionado?.folio}
                            />
                        </div>
                    </RoleGuard>
                )}

                {/* Tab C: Inteligencia IA / MO - SOLO ANALISTA */}
                {activeTab === 'inteligencia' && (
                    <RoleGuard allowedRoles={['ANALISTA']}>
                        <div className="pb-6">
                            <InteligenciaMOTab />
                        </div>
                    </RoleGuard>
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
    )
}

export default function App() {
    return (
        <AuthProvider>
            <FormProvider>
                <ExpedienteActivoProvider>
                    <MainApp />
                </ExpedienteActivoProvider>
            </FormProvider>
        </AuthProvider>
    )
}