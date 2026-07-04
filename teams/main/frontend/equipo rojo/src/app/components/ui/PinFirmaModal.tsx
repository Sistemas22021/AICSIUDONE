import { useState } from 'react'
import { NeonButton } from './NeonButton'
import { NeonPanel } from './NeonPanel'
import { NeonInput } from './NeonInput'

interface PinFirmaModalProps {
    isOpen: boolean
    numeroPaso: number
    nombrePaso: string
    onConfirmar: (pin: string) => Promise<void>
    onCancelar: () => void
}

/**
 * Modal de firma digital por PIN.
 * capturar y enviar el PIN del investigador.
 */
export const PinFirmaModal = ({
                                  isOpen,
                                  numeroPaso,
                                  nombrePaso,
                                  onConfirmar,
                                  onCancelar,
                              }: PinFirmaModalProps) => {
    const [pin, setPin] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [cargando, setCargando] = useState(false)

    if (!isOpen) return null

    const handleConfirmar = async () => {
        if (!pin.trim()) {
            setError('El PIN es obligatorio.')
            return
        }
        setCargando(true)
        setError(null)
        try {
            await onConfirmar(pin)
            setPin('')
        } catch (e: any) {
            setError(e?.message ?? 'PIN incorrecto. Intento registrado.')
        } finally {
            setCargando(false)
        }
    }

    const handleCancelar = () => {
        setPin('')
        setError(null)
        onCancelar()
    }

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.85)',
            backdropFilter: 'blur(4px)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            zIndex: 1100,
        }}>
            <NeonPanel style={{ maxWidth: '380px', width: '90%' }}>
                <div style={{ padding: '28px', textAlign: 'center' }}>
                    <div style={{ fontSize: '32px', marginBottom: '8px' }}>🔐</div>
                    <h3 style={{ color: '#ff00ff', marginBottom: '4px' }}>
                        Firma Digital — Paso {numeroPaso}
                    </h3>
                    <p style={{ color: '#00ffffaa', fontSize: '13px', marginBottom: '20px' }}>
                        {nombrePaso}
                    </p>
                    <p style={{ color: '#ffffff88', fontSize: '12px', marginBottom: '20px' }}>
                        Ingresa tu PIN personal para confirmar tu identidad y cerrar este paso.
                        El intento quedará registrado con timestamp.
                    </p>

                    <NeonInput
                        label="PIN Personal"
                        type="password"
                        value={pin}
                        onChange={(e: any) => setPin(e.target.value)}
                        placeholder="••••••"
                        style={{ marginBottom: '12px' }}
                    />

                    {error && (
                        <p style={{
                            color: '#ff4444',
                            fontSize: '12px',
                            marginBottom: '16px',
                            padding: '8px',
                            background: '#ff000011',
                            borderRadius: '4px',
                            border: '1px solid #ff444444',
                        }}>
                            ⚠️ {error}
                        </p>
                    )}

                    <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                        <NeonButton
                            onClick={handleCancelar}
                            disabled={cargando}
                            style={{ background: '#ffffff11', color: '#ffffff88' }}
                        >
                            Cancelar
                        </NeonButton>
                        <NeonButton
                            onClick={handleConfirmar}
                            disabled={cargando || !pin.trim()}
                            style={{ background: '#ff00ff22', color: '#ff00ff' }}
                        >
                            {cargando ? 'Verificando...' : 'Firmar paso'}
                        </NeonButton>
                    </div>
                </div>
            </NeonPanel>
        </div>
    )
}