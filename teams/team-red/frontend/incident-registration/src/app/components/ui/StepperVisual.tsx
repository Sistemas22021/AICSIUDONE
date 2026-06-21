interface StepperVisualProps {
    pasoActual: number
    pasosCompletados: number[]
    labels: string[]
}

export const StepperVisual = ({ pasoActual, pasosCompletados, labels }: StepperVisualProps) => {
    return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '24px', flexWrap: 'wrap' }}>
            {labels.map((label, index) => {
                const pasoNumero = index + 1
                const isCompleted = pasosCompletados.includes(pasoNumero)
                const isActive = pasoActual === pasoNumero

                return (
                    <div key={index} style={{ display: 'flex', alignItems: 'center' }}>
                        <div style={{ textAlign: 'center' }}>
                            <div
                                style={{
                                    width: '40px',
                                    height: '40px',
                                    borderRadius: '50%',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    backgroundColor: isCompleted ? '#00ff00' : (isActive ? '#00ffff' : '#1a1a1a'),
                                    border: `2px solid ${isActive ? '#00ffff' : '#00ffff33'}`,
                                    color: isCompleted || isActive ? '#000' : '#00ffffaa',
                                    fontWeight: 'bold',
                                    fontSize: '18px',
                                }}
                            >
                                {isCompleted ? '✓' : pasoNumero}
                            </div>
                            <div style={{ fontSize: '10px', marginTop: '4px', color: isActive ? '#00ffff' : '#00ffffaa' }}>
                                {label}
                            </div>
                        </div>
                        {index < labels.length - 1 && (
                            <div style={{
                                width: '40px',
                                height: '2px',
                                backgroundColor: isCompleted ? '#00ff00' : '#00ffff33',
                                margin: '0 4px'
                            }} />
                        )}
                    </div>
                )
            })}
        </div>
    )
}