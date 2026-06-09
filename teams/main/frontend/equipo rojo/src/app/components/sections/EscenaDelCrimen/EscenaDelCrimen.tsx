import { useState } from 'react'
import { useEscenaCrimen } from '../../../hooks/useEscenaCrimen'
import { NeonButton } from '../../ui/NeonButton'
import { NeonInput } from '../../ui/NeonInput'
import { NeonSelect } from '../../ui/NeonSelect'
import { NeonPanel } from '../../ui/NeonPanel'
import { HistorialEscenas } from './HistorialEscenas'
import { tiposEvidencia, tiposEmbalaje, resultadoNegativo } from './index'

export const EscenaDelCrimen = () => {
    const [mostrarHistorial, setMostrarHistorial] = useState(false)

    const {
        state,
        canCompletarPaso1,
        canCompletarPaso2,
        canCompletarPaso3,
        canCompletarPaso4,
        completarPaso1,
        completarPaso2,
        completarPaso3,
        completarPaso4,
        addEvidencia,
        removeEvidencia,
        updateEvidencia,
        addEscenaNegativa,
        removeEscenaNegativa,
        updateEscenaNegativa,
        updatePerimetro,
        updateRecoleccion,
        updateLiberacion,
        isPaso1Completado,
        isPaso2Completado,
        resetEscena,
    } = useEscenaCrimen()

    const pasosCompletados = [
        state.paso1_completado,
        state.paso2_completado,
        state.paso3_completado,
        state.paso4_completado,
    ].filter(Boolean).length

    const progreso = (pasosCompletados / 4) * 100

    // Verificar si hay al menos una evidencia válida
    const tieneEvidenciaValida = state.evidencias.some(ev => ev.tipo && ev.descripcion)

    // Verificar si hay al menos una escena negativa válida
    const tieneEscenaNegativaValida = state.escenaNegativa.some(en => en.elemento && en.lugar && en.resultado)

    // Mensaje de validación para paso 2
    const getValidacionPaso2 = () => {
        if (!tieneEvidenciaValida) return '❌ Necesitas al menos una evidencia con tipo y descripción'
        if (!tieneEscenaNegativaValida) return '❌ Necesitas al menos un registro de escena negativa completo'
        return '✅ Puedes completar el paso 2'
    }

    const renderPasoActual = () => {
        switch (state.paso_actual) {
            case 1:
                return (
                    <NeonPanel>
                        <h3>Paso 1: Aseguramiento del perímetro</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                            <NeonSelect
                                label="Sellado"
                                options={[
                                    { value: 'true', label: 'Sí' },
                                    { value: 'false', label: 'No' },
                                ]}
                                value={String(state.perimetro.sellado)}
                                onChange={(e: any) => updatePerimetro({ sellado: e.target.value === 'true' })}
                                disabled={isPaso1Completado}
                            />
                            <NeonInput
                                label="Agentes asignados"
                                type="number"
                                value={state.perimetro.agentes}
                                onChange={(e: any) => updatePerimetro({ agentes: parseInt(e.target.value) || 0 })}
                                disabled={isPaso1Completado}
                            />
                            <NeonInput
                                label="Hora de cierre"
                                type="time"
                                value={state.perimetro.horaCierre}
                                onChange={(e: any) => updatePerimetro({ horaCierre: e.target.value })}
                                disabled={isPaso1Completado}
                            />
                            <NeonButton
                                onClick={completarPaso1}
                                disabled={!canCompletarPaso1 || state.paso1_completado}
                            >
                                {state.paso1_completado ? '✓ Completado' : 'Completar Paso 1'}
                            </NeonButton>
                        </div>
                    </NeonPanel>
                )

            case 2:
                return (
                    <NeonPanel>
                        <h3>Paso 2: Documentación</h3>

                        {/* Evidencias */}
                        <div style={{ marginBottom: '32px' }}>
                            <h4>Evidencias</h4>
                            {state.evidencias.map((ev) => (
                                <div
                                    key={ev.id}
                                    style={{
                                        border: '1px solid #00ffff33',
                                        padding: '16px',
                                        marginBottom: '12px',
                                        borderRadius: '8px',
                                    }}
                                >
                                    <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonSelect
                                                label="Tipo"
                                                options={tiposEvidencia.map((t: string) => ({ value: t, label: t }))}
                                                value={ev.tipo}
                                                onChange={(e: any) => updateEvidencia(ev.id, { tipo: e.target.value })}
                                                disabled={isPaso2Completado}
                                            />
                                        </div>
                                        <div style={{ flex: 3, minWidth: '200px' }}>
                                            <NeonInput
                                                label="Descripción"
                                                value={ev.descripcion}
                                                onChange={(e: any) => updateEvidencia(ev.id, { descripcion: e.target.value })}
                                                disabled={isPaso2Completado}
                                            />
                                        </div>
                                        {!isPaso2Completado && state.evidencias.length > 1 && (
                                            <div style={{ flex: 0 }}>
                                                <NeonButton onClick={() => removeEvidencia(ev.id)}>
                                                    Eliminar
                                                </NeonButton>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}
                            {!isPaso2Completado && (
                                <NeonButton onClick={addEvidencia}>
                                    + Agregar Evidencia
                                </NeonButton>
                            )}
                        </div>

                        {/* Escena Negativa */}
                        <div style={{ marginBottom: '32px' }}>
                            <h4>Escena Negativa</h4>
                            {state.escenaNegativa.map((en) => (
                                <div
                                    key={en.id}
                                    style={{
                                        border: '1px solid #ff00ff33',
                                        padding: '16px',
                                        marginBottom: '12px',
                                        borderRadius: '8px',
                                    }}
                                >
                                    <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonInput
                                                label="Elemento buscado"
                                                value={en.elemento}
                                                onChange={(e: any) => updateEscenaNegativa(en.id, { elemento: e.target.value })}
                                                disabled={isPaso2Completado}
                                            />
                                        </div>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonInput
                                                label="Área inspeccionada"
                                                value={en.lugar}
                                                onChange={(e: any) => updateEscenaNegativa(en.id, { lugar: e.target.value })}
                                                disabled={isPaso2Completado}
                                            />
                                        </div>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonSelect
                                                label="Resultado"
                                                options={resultadoNegativo.map((r: string) => ({ value: r, label: r }))}
                                                value={en.resultado}
                                                onChange={(e: any) => updateEscenaNegativa(en.id, { resultado: e.target.value })}
                                                disabled={isPaso2Completado}
                                            />
                                        </div>
                                        {!isPaso2Completado && state.escenaNegativa.length > 1 && (
                                            <div style={{ flex: 0 }}>
                                                <NeonButton onClick={() => removeEscenaNegativa(en.id)}>
                                                    Eliminar
                                                </NeonButton>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}
                            {!isPaso2Completado && (
                                <NeonButton onClick={addEscenaNegativa}>
                                    + Agregar Escena Negativa
                                </NeonButton>
                            )}
                        </div>

                        {/* Mensaje de validación */}
                        <div style={{
                            padding: '12px',
                            marginBottom: '16px',
                            borderRadius: '4px',
                            backgroundColor: canCompletarPaso2 ? '#00ff0033' : '#ff000033',
                            color: canCompletarPaso2 ? '#00ff00' : '#ff0000'
                        }}>
                            {getValidacionPaso2()}
                        </div>

                        <NeonButton
                            onClick={completarPaso2}
                            disabled={!canCompletarPaso2 || state.paso2_completado}
                        >
                            {state.paso2_completado ? '✓ Completado' : 'Completar Paso 2'}
                        </NeonButton>
                    </NeonPanel>
                )

            case 3:
                return (
                    <NeonPanel>
                        <h3>Paso 3: Recolección de evidencias</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                            <NeonInput
                                label="Hora de recolección"
                                type="time"
                                value={state.recoleccion.hora}
                                onChange={(e: any) => updateRecoleccion({ hora: e.target.value })}
                                disabled={state.paso3_completado}
                            />
                            <NeonSelect
                                label="Tipo de embalaje"
                                options={tiposEmbalaje.map((t: string) => ({ value: t, label: t }))}
                                value={state.recoleccion.embalaje}
                                onChange={(e: any) => updateRecoleccion({ embalaje: e.target.value })}
                                disabled={state.paso3_completado}
                            />
                            <NeonButton
                                onClick={completarPaso3}
                                disabled={!canCompletarPaso3 || state.paso3_completado}
                            >
                                {state.paso3_completado ? '✓ Completado' : 'Completar Paso 3'}
                            </NeonButton>
                        </div>
                    </NeonPanel>
                )

            case 4:
                return (
                    <NeonPanel>
                        {!state.paso4_completado ? (
                            <>
                                <h3>Paso 4: Liberación del lugar</h3>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                                    <NeonInput
                                        label="Hora de liberación"
                                        type="time"
                                        value={state.liberacion.hora}
                                        onChange={(e: any) => updateLiberacion({ hora: e.target.value })}
                                        disabled={state.paso4_completado}
                                    />
                                    <NeonInput
                                        label="PIN de seguridad"
                                        type="password"
                                        value={state.liberacion.pin}
                                        onChange={(e: any) => updateLiberacion({ pin: e.target.value })}
                                        disabled={state.paso4_completado}
                                    />
                                    <NeonButton
                                        onClick={completarPaso4}
                                        disabled={!canCompletarPaso4}
                                    >
                                        Completar Paso 4
                                    </NeonButton>
                                </div>
                            </>
                        ) : (
                            <div style={{ textAlign: 'center', padding: '20px' }}>
                                <div style={{
                                    backgroundColor: '#0a2a0a',
                                    border: '2px solid #00ff00',
                                    borderRadius: '12px',
                                    padding: '24px',
                                    marginBottom: '24px'
                                }}>
                                    <h2 style={{ color: '#00ff00', marginBottom: '16px' }}>✅ ¡Escena del Crimen Completada!</h2>
                                    <div style={{
                                        textAlign: 'left',
                                        backgroundColor: '#0a0a0a',
                                        padding: '16px',
                                        borderRadius: '8px',
                                        marginBottom: '16px'
                                    }}>
                                        <h4 style={{ color: '#00ffff', marginBottom: '12px' }}>📋 Resumen de la escena:</h4>
                                        <div style={{ fontSize: '14px', lineHeight: '1.6' }}>
                                            <p><strong style={{ color: '#00ff00' }}>🔒 Perímetro:</strong> {state.perimetro.sellado ? 'Sellado ✓' : 'No sellado'}</p>
                                            <p><strong style={{ color: '#00ff00' }}>👮 Agentes:</strong> {state.perimetro.agentes}</p>
                                            <p><strong style={{ color: '#00ff00' }}>⏰ Hora cierre:</strong> {state.perimetro.horaCierre}</p>
                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />
                                            <p><strong style={{ color: '#00ff00' }}>🔬 Evidencias:</strong> {state.evidencias.length}</p>
                                            <ul style={{ marginLeft: '20px', marginBottom: '12px' }}>
                                                {state.evidencias.map((ev, idx) => (
                                                    <li key={idx}>
                                                        <strong>{ev.tipo}</strong>: {ev.descripcion.length > 50 ? ev.descripcion.substring(0, 50) + '...' : ev.descripcion}
                                                    </li>
                                                ))}
                                            </ul>
                                            <p><strong style={{ color: '#00ff00' }}>⚠️ Escenas negativas:</strong> {state.escenaNegativa.length}</p>
                                            <ul style={{ marginLeft: '20px' }}>
                                                {state.escenaNegativa.map((en, idx) => (
                                                    <li key={idx}>
                                                        <strong>{en.elemento}</strong> - {en.lugar}: {en.resultado}
                                                    </li>
                                                ))}
                                            </ul>
                                            <hr style={{ borderColor: '#00ffff33', margin: '12px 0' }} />
                                            <p><strong style={{ color: '#00ff00' }}>📦 Recolección:</strong> {state.recoleccion.hora} - {state.recoleccion.embalaje}</p>
                                            <p><strong style={{ color: '#00ff00' }}>🔓 Liberación:</strong> {state.liberacion.hora}</p>
                                        </div>
                                    </div>
                                </div>
                                <NeonButton onClick={() => {
                                    const escenaCompletada = {
                                        ...state,
                                        completadoEn: new Date().toLocaleString(),
                                        id: crypto.randomUUID()
                                    }
                                    const historial = JSON.parse(localStorage.getItem('escenas_completadas') || '[]')
                                    historial.push(escenaCompletada)
                                    localStorage.setItem('escenas_completadas', JSON.stringify(historial))
                                    console.log('✅ Escena guardada. Total:', historial.length)
                                    resetEscena()
                                }}>
                                    + Nueva Escena
                                </NeonButton>
                                <p style={{ fontSize: '12px', marginTop: '16px', color: '#00ffffaa' }}>
                                    Los datos han sido guardados en localStorage
                                </p>
                            </div>
                        )}
                    </NeonPanel>
                )

            default:
                return <NeonPanel>Proceso completado 🎉</NeonPanel>
        }
    }

    return (
        <div style={{ padding: '24px' }}>
            {/* Título y botón de historial juntos */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 style={{ fontSize: '24px', color: '#00ffff' }}>Escena del Crimen</h2>
                <NeonButton onClick={() => setMostrarHistorial(!mostrarHistorial)}>
                    {mostrarHistorial ? '📋 Ocultar Historial' : '📋 Ver Historial'}
                </NeonButton>
            </div>

            {/* Historial (se muestra/oculta con el botón) */}
            {mostrarHistorial && (
                <div style={{ marginBottom: '24px' }}>
                    <HistorialEscenas />
                </div>
            )}

            {/* Barra de progreso */}
            <div style={{ marginBottom: '24px' }}>
                <div style={{ backgroundColor: '#1a1a1a', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
                    <div style={{ backgroundColor: '#00ffff', width: `${progreso}%`, height: '100%', transition: 'width 0.3s' }} />
                </div>
                <p style={{ marginTop: '8px', fontSize: '14px', color: '#00ffffaa' }}>
                    Paso {state.paso_actual} de 4 • {pasosCompletados} completados
                </p>
            </div>

            {renderPasoActual()}
        </div>
    )
}