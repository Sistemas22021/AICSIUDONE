import { NeonButton } from './NeonButton'
import { NeonPanel } from './NeonPanel'

interface NeonConfirmModalProps {
    isOpen: boolean
    title: string
    message: string
    onConfirm: () => void
    onCancel: () => void
}

export const NeonConfirmModal = ({ isOpen, title, message, onConfirm, onCancel }: NeonConfirmModalProps) => {
    if (!isOpen) return null

    return (
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            backdropFilter: 'blur(4px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
        }}>
            <NeonPanel style={{ maxWidth: '400px', width: '90%', padding: '0' }}>
                <div style={{ padding: '24px' }}>
                    <h3 style={{ color: '#ff00ff', marginBottom: '16px', textAlign: 'center' }}>{title}</h3>
                    <p style={{ color: '#00ffffaa', textAlign: 'center', marginBottom: '24px' }}>
                        {message}
                    </p>
                    <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
                        <NeonButton
                            onClick={onCancel}
                            style={{ backgroundColor: '#00ffff33', color: '#00ffff' }}
                        >
                            Cancelar
                        </NeonButton>
                        <NeonButton
                            onClick={onConfirm}
                            style={{ backgroundColor: '#ff000033', color: '#ff0000' }}
                        >
                            Borrar Todo
                        </NeonButton>
                    </div>
                </div>
            </NeonPanel>
        </div>
    )
}