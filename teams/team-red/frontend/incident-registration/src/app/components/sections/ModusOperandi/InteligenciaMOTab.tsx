import { useState } from 'react'
import { NeonPanel } from '../../ui/NeonPanel'
import { InteligenciaSubTabs } from './InteligenciaSubTabs'
import { AnalisisMOTab } from './AnalisisMOTab'
import { ValidarMOTab } from './ValidarMOTab'
import { CasosPanel } from './CasosPanel'
import { FirmaConductualTab } from '../FirmaConductual/FirmaConductualTab'
import { BuscarPatronesTab } from './BuscarPatronesTab'

type SubTab = 'analisis' | 'validar' | 'buscar' | 'firma' | 'casos'

export const InteligenciaMOTab = () => {
    const [subTab, setSubTab] = useState<SubTab>('analisis')

    return (
        <NeonPanel title="Inteligencia IA / Modus Operandi">
            <InteligenciaSubTabs activo={subTab} onChange={setSubTab} />
            {subTab === 'analisis' && <AnalisisMOTab />}
            {subTab === 'validar' && <ValidarMOTab />}
            {subTab === 'firma' && <FirmaConductualTab />}
            {subTab === 'buscar' && <BuscarPatronesTab />}
            {subTab === 'casos' && <CasosPanel />}
        </NeonPanel>
    )
}