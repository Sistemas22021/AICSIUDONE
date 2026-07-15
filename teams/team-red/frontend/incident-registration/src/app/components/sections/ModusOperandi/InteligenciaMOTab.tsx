import { useState } from 'react'
import { NeonPanel } from '../../ui/NeonPanel'
import { InteligenciaSubTabs } from './InteligenciaSubTabs'
import { AnalisisMOTab } from './AnalisisMOTab'
import { ValidarMOTab } from './ValidarMOTab'


type SubTab = 'analisis' | 'validar' | 'buscar' | 'futuras'

/** Contenido de la Pestaña C ("INTELIGENCIA IA / MODUS OPERANDI"), con sub-pestañas por HU. */
export const InteligenciaMOTab = () => {
    const [subTab, setSubTab] = useState<SubTab>('analisis')

    return (
        <NeonPanel title="Inteligencia IA / Modus Operandi">
            <InteligenciaSubTabs activo={subTab} onChange={setSubTab} />
            {subTab === 'analisis' && <AnalisisMOTab />}
            {subTab === 'validar' && <ValidarMOTab />}
        </NeonPanel>
    )
}