import { useState, useEffect } from 'react'
import { NeonButton } from '../../ui/NeonButton'
import { NeonPanel } from '../../ui/NeonPanel'
import { NeonConfirmModal } from '../../ui/NeonConfirmModal'

interface EscenaGuardada {
    id: string
    completadoEn: string
    evidencias: Array<{ tipo: string; descripcion: string }>
    escenaNegativa: Array<{ elemento: string; lugar: string; resultado: string }>
    perimetro: { sellado: boolean; agentes: number; horaCierre: string }
    recoleccion: { hora: string; embalaje: string }
    liberacion: { hora: string; pin: string }
}

export const HistorialEscenas = () => {
    const [escenas, setEscenas] = useState<EscenaGuardada[]>([])
    const [escenaSeleccionada, setEscenaSeleccionada] = useState<EscenaGuardada | null>(null)
    const [mostrarModalBorrar, setMostrarModalBorrar] = useState(false)

    const cargarHistorial = () => {
        const historial = JSON.parse(localStorage.getItem('escenas_completadas') || '[]')
        setEscenas(historial.reverse())
    }

    useEffect(() => {
        cargarHistorial()
    }, [])

    const eliminarEscena = (id: string) => {
        const nuevasEscenas = escenas.filter(e => e.id !== id)
        localStorage.setItem('escenas_completadas', JSON.stringify(nuevasEscenas))
        cargarHistorial()
        if (escenaSeleccionada?.id === id) setEscenaSeleccionada(null)
    }

    const borrarTodo = () => {
        localStorage.removeItem('escenas_completadas')
        cargarHistorial()
        setEscenaSeleccionada(null)
        setMostrarModalBorrar(false)
    }

    return (
        <>
            <NeonPanel>
                <div style={{ padding: '20px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                        <h2 style={{ color: '#00ffff' }}>📋 Historial de Escenas</h2>
                        <NeonButton
                            onClick={() => setMostrarModalBorrar(true)}
                            style={{ backgroundColor: '#ff000033' }}
                        >
                            Borrar Todo
                        </NeonButton>
                    </div>

                    {escenas.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '40px', color: '#00ffffaa' }}>
                            No hay escenas guardadas aún.
                            <br />
                            Completa una escena y se guardará automáticamente.
                        </div>
                    ) : (
                        <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
                            {/* Lista de escenas */}
                            <div style={{ flex: 1, minWidth: '250px' }}>
                                <h3>Escenas guardadas ({escenas.length})</h3>
                                <div style={{ maxHeight: '500px', overflowY: 'auto' }}>
                                    {escenas.map((escena, idx) => (
                                        <div
                                            key={escena.id}
                                            onClick={() => setEscenaSeleccionada(escena)}
                                            style={{
                                                border: `1px solid ${escenaSeleccionada?.id === escena.id ? '#00ffff' : '#00ffff33'}`,
                                                padding: '12px',
                                                marginBottom: '8px',
                                                borderRadius: '8px',
                                                cursor: 'pointer',
                                                backgroundColor: escenaSeleccionada?.id === escena.id ? '#00ffff11' : 'transparent'
                                            }}
                                        >
                                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                <div>
                                                    <strong>Escena #{escenas.length - idx}</strong>
                                                    <div style={{ fontSize: '12px', color: '#00ffffaa' }}>
                                                        {escena.completadoEn}
                                                    </div>
                                                </div>
                                                <NeonButton
                                                    onClick={(e: any) => {
                                                        e.stopPropagation()
                                                        eliminarEscena(escena.id)
                                                    }}
                                                    style={{
                                                        backgroundColor: '#ff000033',
                                                        padding: '4px 8px',
                                                        fontSize: '12px'
                                                    }}
                                                >
                                                    🗑️
                                                </NeonButton>
                                            </div>
                                            <div style={{ fontSize: '12px', marginTop: '8px' }}>
                                                🔬 {escena.evidencias.length} evidencias | ⚠️ {escena.escenaNegativa.length} negativas
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Detalle de la escena seleccionada */}
                            <div style={{ flex: 2, minWidth: '300px' }}>
                                {escenaSeleccionada ? (
                                    <div>
                                        <h3>Detalles de la escena</h3>
                                        <div style={{
                                            backgroundColor: '#0a0a0a',
                                            padding: '16px',
                                            borderRadius: '8px',
                                            maxHeight: '500px',
                                            overflowY: 'auto'
                                        }}>
                                            <p><strong>📅 Completado:</strong> {escenaSeleccionada.completadoEn}</p>

                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />

                                            <h4>🔒 Perímetro</h4>
                                            <p>Sellado: {escenaSeleccionada.perimetro.sellado ? 'Sí' : 'No'}</p>
                                            <p>Agentes: {escenaSeleccionada.perimetro.agentes}</p>
                                            <p>Hora cierre: {escenaSeleccionada.perimetro.horaCierre}</p>

                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />

                                            <h4>🔬 Evidencias ({escenaSeleccionada.evidencias.length})</h4>
                                            {escenaSeleccionada.evidencias.map((ev, i) => (
                                                <div key={i} style={{ marginLeft: '16px', marginBottom: '8px' }}>
                                                    <strong>{ev.tipo}</strong>: {ev.descripcion}
                                                </div>
                                            ))}

                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />

                                            <h4>⚠️ Escenas Negativas ({escenaSeleccionada.escenaNegativa.length})</h4>
                                            {escenaSeleccionada.escenaNegativa.map((en, i) => (
                                                <div key={i} style={{ marginLeft: '16px', marginBottom: '8px' }}>
                                                    <strong>{en.elemento}</strong> - {en.lugar}<br />
                                                    <span style={{ fontSize: '12px' }}>Resultado: {en.resultado}</span>
                                                </div>
                                            ))}

                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />

                                            <h4>📦 Recolección</h4>
                                            <p>Hora: {escenaSeleccionada.recoleccion.hora}</p>
                                            <p>Embalaje: {escenaSeleccionada.recoleccion.embalaje}</p>

                                            <h4>🔓 Liberación</h4>
                                            <p>Hora: {escenaSeleccionada.liberacion.hora}</p>
                                        </div>
                                    </div>
                                ) : (
                                    <div style={{ textAlign: 'center', padding: '40px', color: '#00ffffaa' }}>
                                        Selecciona una escena para ver los detalles
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </NeonPanel>

            {/* Modal de confirmación */}
            <NeonConfirmModal
                isOpen={mostrarModalBorrar}
                title="⚠️ Borrar todo el historial"
                message="¿Estás seguro de que quieres eliminar TODAS las escenas guardadas? Esta acción no se puede deshacer."
                onConfirm={borrarTodo}
                onCancel={() => setMostrarModalBorrar(false)}
            />
        </>
    )
}