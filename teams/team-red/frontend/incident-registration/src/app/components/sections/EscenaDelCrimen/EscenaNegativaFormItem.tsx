import { NeonInput } from '../../ui/NeonInput'
import { NeonSelect } from '../../ui/NeonSelect'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonButton } from '../../ui/NeonButton'
import { resultadoNegativo } from './index'
import type { EscenaNegativaItem } from '../../../hooks/useEscenaCrimen'

interface EscenaNegativaFormItemProps {
    item: EscenaNegativaItem
    disabled: boolean
    canRemove: boolean
    onChange: (patch: Partial<EscenaNegativaItem>) => void
    onRemove: () => void
}

export const EscenaNegativaFormItem = ({
                                           item,
                                           disabled,
                                           canRemove,
                                           onChange,
                                           onRemove,
                                       }: EscenaNegativaFormItemProps) => (
    <div
        style={{
            border: '1px solid #00ffff33',
            padding: '16px',
            marginBottom: '12px',
            borderRadius: '8px',
        }}
    >
        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <div style={{ flex: 2, minWidth: '150px' }}>
                <NeonInput
                    label="Elemento buscado"
                    value={item.elemento}
                    onChange={(e: any) => onChange({ elemento: e.target.value })}
                    disabled={disabled}
                    maxLength={200}
                />
            </div>
            <div style={{ flex: 2, minWidth: '150px' }}>
                <NeonInput
                    label="Área inspeccionada"
                    value={item.lugar}
                    onChange={(e: any) => onChange({ lugar: e.target.value })}
                    disabled={disabled}
                    maxLength={200}
                />
            </div>
        </div>

        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginTop: '12px' }}>
            <div style={{ flex: 2, minWidth: '200px' }}>
                <NeonSelect
                    label="Resultado"
                    options={[
                        { value: '', label: '— Seleccione resultado —' },
                        ...resultadoNegativo.map((r: string) => ({ value: r, label: r })),
                    ]}
                    value={item.resultado}
                    onChange={(e: any) => onChange({ resultado: e.target.value })}
                    disabled={disabled}
                />
            </div>
            <div style={{ flex: 3, minWidth: '200px' }}>
                <NeonTextarea
                    label="Observación"
                    value={item.observacion}
                    onChange={(e: any) => onChange({ observacion: e.target.value })}
                    disabled={disabled}
                    placeholder="Notas adicionales..."
                    rows={2}
                    showCount
                    maxCount={500}
                    maxLength={500}
                />
            </div>
        </div>

        {canRemove && (
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '8px' }}>
                <NeonButton onClick={onRemove}>Eliminar</NeonButton>
            </div>
        )}
    </div>
)