import { useState } from 'react'
import { NeonPanel } from '../../ui/NeonPanel'
import { InteligenciaSubTabs } from './InteligenciaSubTabs'
import { AnalisisMOTab } from './AnalisisMOTab'
import { ValidarMOTab } from './ValidarMOTab'
import { CasosPanel } from './CasosPanel'

type SubTab = 'analisis' | 'validar' | 'buscar' | 'casos'

export const InteligenciaMOTab = () => {
    const [subTab, setSubTab] = useState<SubTab>('analisis')

    return (
        <NeonPanel title="Inteligencia IA / Modus Operandi">
            <InteligenciaSubTabs activo={subTab} onChange={setSubTab} />
            {subTab === 'analisis' && <AnalisisMOTab />}
            {subTab === 'validar' && <ValidarMOTab />}
            {subTab === 'casos' && <CasosPanel />}
        </NeonPanel>
    )
}