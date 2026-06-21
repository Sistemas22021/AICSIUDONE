import { NeonButton } from './NeonButton'

interface AlertaIntegridadProps {
    alertas: Array<{ evidenciaId: string; mensaje: string; integro: boolean }>
    onLimpiar: () => void
}

export const AlertaIntegridad = ({ alertas, onLimpiar }: AlertaIntegridadProps) => {
    if (alertas.length === 0) return null

    return (
        <div style={{
            backgroundColor: '#ff000022',
            border: '1px solid #ff0000',
            borderRadius: '8px',
            padding: '16px',
            marginBottom: '16px'
        }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <h4 style={{ color: '#ff0000', margin: 0 }}>⚠️ Alertas de Integridad</h4>
                <NeonButton onClick={onLimpiar} style={{ backgroundColor: '#ff000033', padding: '4px 12px' }}>
                    Limpiar
                </NeonButton>
            </div>
            {alertas.map((alerta, idx) => (
                <div key={idx} style={{ fontSize: '12px', marginBottom: '8px', color: '#ff8888' }}>
                    • {alerta.mensaje}
                </div>
            ))}
        </div>
    )
}