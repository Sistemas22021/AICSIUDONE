import { useState, useEffect } from 'react'
import { useEscenaCrimen } from '../../../hooks/useEscenaCrimen'
import { NeonButton } from '../../ui/NeonButton'
import { NeonInput } from '../../ui/NeonInput'
import { NeonSelect } from '../../ui/NeonSelect'
import { NeonPanel } from '../../ui/NeonPanel'
import { StepperVisual } from '../../ui/StepperVisual'
import { AlertaIntegridad } from '../../ui/AlertaIntegridad'
import { HistorialEscenas } from './HistorialEscenas'
import { tiposEvidencia, tiposEmbalaje, resultadoNegativo } from './index'

interface EscenaDelCrimenProps {
    expedienteIdInicial?: number
    folioInicial?: string
}

export const EscenaDelCrimen = ({ expedienteIdInicial, folioInicial }: EscenaDelCrimenProps) => {
    const [mostrarHistorial, setMostrarHistorial] = useState(false)
    const [mensajeIntegridad, setMensajeIntegridad] = useState<{
        texto: string
        tipo: 'info' | 'success' | 'warning' | 'error'
    } | null>(null)

    const {
        state,
        isPaso1Completado,
        isPaso2Completado,
        canCompletarPaso1,
        canCompletarPaso2,
        canCompletarPaso3,
        canCompletarPaso4,
        completarPaso1,
        completarPaso2,
        completarPaso3,
        completarPaso4,
        updatePerimetro,
        setTipoEscena,
        setFolioExpediente,
        vincularExpediente,
        setEscenaId,
        addEvidencia,
        removeEvidencia,
        updateEvidenciaPaso3,
        updateLiberacion,
        addEscenaNegativa,
        removeEscenaNegativa,
        updateEscenaNegativa,
        setNoHayEscenaNegativa,
        verificarIntegridad,
        limpiarAlertas,
        resetEscena,
        desbloquearPaso3,
        desbloquearPaso2,
    } = useEscenaCrimen()

    useEffect(() => {
        if (expedienteIdInicial && folioInicial && !state.expedienteId) {
            vincularExpediente(expedienteIdInicial, folioInicial)
                .catch((err: any) => console.error('Error al vincular expediente:', err))
        }
    }, [expedienteIdInicial, folioInicial, state.expedienteId, vincularExpediente])


    const pasosCompletadosArray = [
        state.paso1_completado ? 1 : null,
        state.paso2_completado ? 2 : null,
        state.paso3_completado ? 3 : null,
        state.paso4_completado ? 4 : null,
    ].filter(Boolean) as number[]

    const labelsPasos = ['Asegurar', 'Documentar', 'Recolectar', 'Liberar']

    // Verificar si hay al menos una evidencia válida
    const tieneEvidenciaValida = state.evidencias.some(ev => ev.tipo && ev.descripcion)

    // Verificar si hay al menos una escena negativa válida
    const tieneEscenaNegativaValida = state.escenaNegativa.some(en => en.elemento && en.lugar && en.resultado)

    const getValidacionPaso2 = () => {
        if (!tieneEvidenciaValida) return '❌ Necesitas al menos una evidencia con tipo y descripción'
        if (!state.noHayEscenaNegativa && !tieneEscenaNegativaValida) return '❌ Necesitas al menos un registro de escena negativa o marcar "Sin elementos negativos"'
        return '✅ Puedes completar el paso 2'
    }

    // ─── HANDLE VERIFICAR INTEGRIDAD ──────────────────────────────────────────
    const handleVerificarIntegridad = async () => {
        if (!state.folioExpediente) {
            setMensajeIntegridad({
                texto: '⚠️ Primero vincula este formulario a un expediente usando el campo "Folio del Expediente"',
                tipo: 'error'
            })
            return
        }

        // Si ya existe escenaId, verificar directamente
        if (state.escenaId) {
            try {
                setMensajeIntegridad({
                    texto: '🔄 Verificando integridad del expediente...',
                    tipo: 'info'
                })
                await verificarIntegridad(state.folioExpediente)

                if (state.alertasIntegridad.length > 0) {
                    const alertas = state.alertasIntegridad.filter(a => !a.integro)
                    if (alertas.length > 0) {
                        setMensajeIntegridad({
                            texto: `⚠️ Se detectaron ${alertas.length} evidencias con problemas de integridad`,
                            tipo: 'warning'
                        })
                    } else {
                        setMensajeIntegridad({
                            texto: '✅ Todas las evidencias han pasado la verificación de integridad',
                            tipo: 'success'
                        })
                    }
                } else {
                    setMensajeIntegridad({
                        texto: '✅ Verificación completada. Sin problemas de integridad.',
                        tipo: 'success'
                    })
                }
                return
            } catch (error) {
                console.error('❌ Error al verificar integridad:', error)
                setMensajeIntegridad({
                    texto: '❌ Error al verificar la integridad del expediente',
                    tipo: 'error'
                })
                return
            }
        }

        // Si no hay escenaId y hay expedienteId, crear la escena
        if (state.expedienteId) {
            try {
                setMensajeIntegridad({
                    texto: '🔄 Creando escena y verificando integridad...',
                    tipo: 'info'
                })
                const { crearEscena } = await import('../../../services/escenaService')
                const escena = await crearEscena({
                    expedienteId: state.expedienteId,
                    levantadaPorId: 1
                })
                setEscenaId(escena.id)

                // Verificar integridad después de crear la escena
                await verificarIntegridad(state.folioExpediente)
                setMensajeIntegridad({
                    texto: '✅ Escena creada y verificación completada',
                    tipo: 'success'
                })
            } catch (error) {
                console.error('❌ Error:', error)
                setMensajeIntegridad({
                    texto: '❌ Error al crear la escena o verificar integridad',
                    tipo: 'error'
                })
            }
            return
        }

        setMensajeIntegridad({
            texto: '⚠️ No se encontró un expediente válido. Verifica el folio.',
            tipo: 'error'
        })
    }

    const renderPasoActual = () => {
        switch (state.paso_actual) {
            case 1:
                return (
                    <NeonPanel>
                        <h3>Paso 1: Aseguramiento del perímetro</h3>

                        <div style={{ marginBottom: '24px' }}>
                            <label style={{ display: 'block', marginBottom: '8px', color: '#00ffff' }}>Tipo de procedimiento:</label>
                            <div style={{ display: 'flex', gap: '16px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <input
                                        type="radio"
                                        name="tipoEscena"
                                        checked={state.tipoEscena === 'escena_completa'}
                                        onChange={() => setTipoEscena('escena_completa')}
                                        disabled={isPaso1Completado}
                                    />
                                    Escena completa (con perímetro)
                                </label>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <input
                                        type="radio"
                                        name="tipoEscena"
                                        checked={state.tipoEscena === 'solo_evidencia'}
                                        onChange={() => setTipoEscena('solo_evidencia')}
                                        disabled={isPaso1Completado}
                                    />
                                    Solo recolección de evidencias
                                </label>
                            </div>
                        </div>

                        <div style={{ marginBottom: '16px' }}>
                            {state.expedienteId && (
                                <div style={{
                                    padding: '8px 12px',
                                    background: '#00ffff11',
                                    border: '1px solid #00ffff44',
                                    borderRadius: '6px',
                                    marginBottom: '8px',
                                    fontSize: '12px',
                                    color: '#00ffff',
                                }}>
                                    ✅ Expediente vinculado desde Búsqueda y Casos:{' '}
                                    <strong style={{ fontFamily: 'monospace' }}>{state.folioExpediente}</strong>
                                    {state.sincronizado && <span style={{ marginLeft: '8px', color: '#00ff88' }}>(sincronizado)</span>}
                                </div>
                            )}
                            <NeonInput
                                label="Folio del Expediente (EXP-XXXX-XXXXXXXX)"
                                value={state.folioExpediente || ''}
                                onChange={(e: any) => setFolioExpediente(e.target.value)}
                                disabled={isPaso1Completado || !!state.expedienteId}
                                placeholder="Ej: EXP-2026-92B0762D — o selecciona desde Búsqueda y Casos"
                            />
                        </div>

                        {state.tipoEscena === 'escena_completa' && (
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
                            </div>
                        )}

                        {state.tipoEscena === 'solo_evidencia' && (
                            <div style={{ padding: '16px', backgroundColor: '#00ffff11', borderRadius: '8px', marginBottom: '16px' }}>
                                <p style={{ color: '#00ffff', margin: 0 }}>📌 Modo "Solo evidencia" - No se requiere asegurar el perímetro.</p>
                            </div>
                        )}

                        <NeonButton
                            onClick={completarPaso1}
                            disabled={!canCompletarPaso1 || state.paso1_completado}
                            style={{ marginTop: '16px' }}
                        >
                            {state.paso1_completado ? '✓ Completado' : 'Completar Paso 1'}
                        </NeonButton>
                    </NeonPanel>
                )

            case 2:
                return (
                    <NeonPanel>
                        <h3>Paso 2: Documentación</h3>

                        {/* ── MENSAJE DE INTEGRIDAD ── */}
                        {mensajeIntegridad && (
                            <div style={{
                                padding: '12px 16px',
                                borderRadius: '8px',
                                marginBottom: '16px',
                                backgroundColor:
                                    mensajeIntegridad.tipo === 'success' ? '#00ff0033' :
                                        mensajeIntegridad.tipo === 'warning' ? '#ffaa0033' :
                                            mensajeIntegridad.tipo === 'error' ? '#ff000033' :
                                                '#00ffff33',
                                border: `1px solid ${
                                    mensajeIntegridad.tipo === 'success' ? '#00ff00' :
                                        mensajeIntegridad.tipo === 'warning' ? '#ffaa00' :
                                            mensajeIntegridad.tipo === 'error' ? '#ff0000' :
                                                '#00ffff'
                                }`,
                                color:
                                    mensajeIntegridad.tipo === 'success' ? '#00ff00' :
                                        mensajeIntegridad.tipo === 'warning' ? '#ffaa00' :
                                            mensajeIntegridad.tipo === 'error' ? '#ff0000' :
                                                '#00ffff',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <span>{mensajeIntegridad.texto}</span>
                                    <button
                                        onClick={() => setMensajeIntegridad(null)}
                                        style={{
                                            background: 'none',
                                            border: 'none',
                                            color: 'inherit',
                                            cursor: 'pointer',
                                            fontSize: '16px'
                                        }}
                                    >
                                        ✕
                                    </button>
                                </div>
                            </div>
                        )}

                        {/* BOTÓN DE DESBLOQUEO PARA PASO 2 */}
                        {state.paso2_completado && (
                            <div style={{
                                padding: '12px',
                                backgroundColor: '#ffaa0033',
                                borderRadius: '8px',
                                marginBottom: '16px',
                                border: '1px solid #ffaa00'
                            }}>
                                <p style={{ color: '#ffaa00', marginBottom: '8px' }}>
                                    ⚠️ Este paso ya está completado. Los campos están bloqueados.
                                </p>
                                <NeonButton
                                    onClick={desbloquearPaso2}
                                    style={{ backgroundColor: '#ffaa0033' }}
                                >
                                    🔓 Desbloquear para editar
                                </NeonButton>
                            </div>
                        )}

                        {state.folioExpediente && (
                            <div style={{
                                padding: '8px 12px',
                                background: '#00ffff11',
                                border: '1px solid #00ffff44',
                                borderRadius: '6px',
                                marginBottom: '16px',
                                fontSize: '12px',
                                color: '#00ffff',
                            }}>
                                📄 Expediente:{' '}
                                <strong style={{ fontFamily: 'monospace' }}>{state.folioExpediente}</strong>
                                {state.sincronizado && <span style={{ marginLeft: '8px', color: '#00ff88' }}>(sincronizado)</span>}
                            </div>
                        )}

                        <div style={{ marginBottom: '16px' }}>
                            <NeonButton onClick={handleVerificarIntegridad} style={{ backgroundColor: '#00ffff33' }}>
                                🔍 Verificar Integridad del Expediente
                            </NeonButton>
                        </div>

                        <AlertaIntegridad alertas={state.alertasIntegridad} onLimpiar={limpiarAlertas} />

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
                                    <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '8px', flexWrap: 'wrap' }}>
                                        <span style={{
                                            backgroundColor: '#00ffff22',
                                            padding: '4px 8px',
                                            borderRadius: '4px',
                                            fontFamily: 'monospace',
                                            fontSize: '12px'
                                        }}>
                                            {ev.numeroSecuencial || 'EV-???'}
                                        </span>
                                        {ev.hashIntegridad && (
                                            <span style={{
                                                backgroundColor: '#00ff0022',
                                                padding: '4px 8px',
                                                borderRadius: '4px',
                                                fontSize: '10px',
                                                fontFamily: 'monospace'
                                            }}>
                                                Hash: {ev.hashIntegridad.substring(0, 10)}...
                                            </span>
                                        )}
                                        {ev.timestamp && (
                                            <span style={{ fontSize: '10px', color: '#00ffffaa' }}>
                                                {new Date(ev.timestamp).toLocaleString()}
                                            </span>
                                        )}
                                    </div>
                                    <div style={{ display: 'flex', gap: '12px', flexDirection: 'column' }}>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <NeonSelect
                                                    label="Tipo"
                                                    options={tiposEvidencia.map((t: string) => ({ value: t, label: t }))}
                                                    value={ev.tipo}
                                                    onChange={(e: any) => updateEvidenciaPaso3(ev.id, { tipo: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                />
                                            </div>
                                            <div style={{ flex: 3, minWidth: '200px' }}>
                                                <NeonInput
                                                    label="Descripción"
                                                    value={ev.descripcion}
                                                    onChange={(e: any) => updateEvidenciaPaso3(ev.id, { descripcion: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                />
                                            </div>
                                        </div>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <NeonInput
                                                    label="Ubicación"
                                                    value={ev.ubicacion}
                                                    onChange={(e: any) => updateEvidenciaPaso3(ev.id, { ubicacion: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                    placeholder="Lugar exacto donde se encontró"
                                                />
                                            </div>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <NeonInput
                                                    label="Responsable (Investigador)"
                                                    value={ev.responsable}
                                                    onChange={(e: any) => updateEvidenciaPaso3(ev.id, { responsable: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                    placeholder="Nombre del investigador"
                                                />
                                            </div>
                                        </div>
                                        {!isPaso2Completado && state.evidencias.length > 1 && (
                                            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
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

                        <div style={{ marginBottom: '32px' }}>
                            <h4>Escena Negativa</h4>

                            <div style={{ marginBottom: '16px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        checked={state.noHayEscenaNegativa}
                                        onChange={(e) => setNoHayEscenaNegativa(e.target.checked)}
                                        disabled={isPaso2Completado}
                                    />
                                    <span style={{ color: '#00ffff' }}>✓ No hay elementos negativos a reportar</span>
                                </label>
                            </div>

                            {!state.noHayEscenaNegativa && state.escenaNegativa.map((en) => (
                                <div
                                    key={en.id}
                                    style={{
                                        border: '1px solid #ff00ff33',
                                        padding: '16px',
                                        marginBottom: '12px',
                                        borderRadius: '8px',
                                    }}
                                >
                                    <div style={{ display: 'flex', gap: '12px', flexDirection: 'column' }}>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
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
                                        </div>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                            <div style={{ flex: 2, minWidth: '200px' }}>
                                                <NeonSelect
                                                    label="Resultado"
                                                    options={resultadoNegativo.map((r: string) => ({ value: r, label: r }))}
                                                    value={en.resultado}
                                                    onChange={(e: any) => updateEscenaNegativa(en.id, { resultado: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                />
                                            </div>
                                            <div style={{ flex: 3, minWidth: '200px' }}>
                                                <NeonInput
                                                    label="Observación"
                                                    value={en.observacion}
                                                    onChange={(e: any) => updateEscenaNegativa(en.id, { observacion: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                    placeholder="Notas adicionales..."
                                                />
                                            </div>
                                        </div>
                                        {!isPaso2Completado && state.escenaNegativa.length > 1 && (
                                            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                                                <NeonButton onClick={() => removeEscenaNegativa(en.id)}>
                                                    Eliminar
                                                </NeonButton>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}

                            {!isPaso2Completado && !state.noHayEscenaNegativa && (
                                <NeonButton onClick={addEscenaNegativa}>
                                    + Agregar Escena Negativa
                                </NeonButton>
                            )}
                        </div>

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
                            onClick={() => completarPaso2().catch((err: any) => console.error('Error al persistir paso 2:', err))}
                            disabled={!canCompletarPaso2 || state.paso2_completado}
                        >
                            {state.paso2_completado ? '✓ Completado' : 'Completar Paso 2'}
                        </NeonButton>
                    </NeonPanel>
                )

            case 3:
                console.log('📦 Paso 3 - evidencias:', state.evidencias)
                return (
                    <NeonPanel>
                        <h3>Paso 3: Recolección de evidencias (por elemento)</h3>

                        {state.paso3_completado && (
                            <div style={{
                                padding: '12px',
                                backgroundColor: '#ffaa0033',
                                borderRadius: '8px',
                                marginBottom: '16px',
                                border: '1px solid #ffaa00'
                            }}>
                                <p style={{ color: '#ffaa00', marginBottom: '8px' }}>
                                    ⚠️ Este paso ya está completado. Los campos están bloqueados.
                                </p>
                                <NeonButton
                                    onClick={desbloquearPaso3}
                                    style={{ backgroundColor: '#ffaa0033' }}
                                >
                                    🔓 Desbloquear para editar
                                </NeonButton>
                            </div>
                        )}

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                            {state.evidencias.map((ev) => {
                                console.log(`🔍 Evidencia ${ev.numeroSecuencial}:`, {
                                    embalaje: ev.embalaje,
                                    responsable: ev.responsable,
                                    horaRecoleccion: ev.horaRecoleccion
                                })
                                return (
                                    <div
                                        key={ev.id}
                                        style={{
                                            border: '1px solid #00ffff33',
                                            padding: '16px',
                                            borderRadius: '8px',
                                        }}
                                    >
                                        <div style={{ marginBottom: '12px' }}>
                                            <strong style={{ color: '#00ffff' }}>{ev.numeroSecuencial}</strong> - {ev.tipo || 'Sin tipo'}
                                        </div>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <label style={{ display: 'block', marginBottom: '4px', color: '#00ffffaa', fontSize: '12px' }}>
                                                    Tipo de embalaje <span style={{ color: '#ff0000' }}>*</span>
                                                </label>
                                                <select
                                                    value={ev.embalaje || ''}
                                                    onChange={(e) => {
                                                        const valor = e.target.value
                                                        console.log(`📦 Cambiando embalaje a: ${valor}`)
                                                        updateEvidenciaPaso3(ev.id, { embalaje: valor })
                                                    }}
                                                    disabled={state.paso3_completado}
                                                    style={{
                                                        width: '100%',
                                                        padding: '8px',
                                                        backgroundColor: '#0a0a0a',
                                                        border: ev.embalaje ? '1px solid #00ffff33' : '1px solid #ff000066',
                                                        color: '#00ffff',
                                                        borderRadius: '4px',
                                                        fontSize: '14px'
                                                    }}
                                                >
                                                    {tiposEmbalaje.map((t: string) => (
                                                        <option key={t} value={t}>{t}</option>
                                                    ))}
                                                </select>
                                            </div>

                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <label style={{ display: 'block', marginBottom: '4px', color: '#00ffffaa', fontSize: '12px' }}>
                                                    Responsable de la cadena
                                                </label>
                                                <input
                                                    type="text"
                                                    value={ev.responsable || ''}
                                                    onChange={(e) => {
                                                        const valor = e.target.value
                                                        console.log(`👤 Cambiando responsable a: ${valor}`)
                                                        updateEvidenciaPaso3(ev.id, { responsable: valor })
                                                    }}
                                                    disabled={state.paso3_completado}
                                                    placeholder="Quién recibe la evidencia"
                                                    style={{
                                                        width: '100%',
                                                        padding: '8px',
                                                        backgroundColor: '#0a0a0a',
                                                        border: '1px solid #00ffff33',
                                                        color: '#00ffff',
                                                        borderRadius: '4px',
                                                        fontSize: '14px'
                                                    }}
                                                />
                                            </div>

                                            <div style={{ flex: 1, minWidth: '120px' }}>
                                                <label style={{ display: 'block', marginBottom: '4px', color: '#00ffffaa', fontSize: '12px' }}>
                                                    Hora de recolección <span style={{ color: '#ff0000' }}>*</span>
                                                </label>
                                                <input
                                                    type="time"
                                                    value={ev.horaRecoleccion || ''}
                                                    onChange={(e) => {
                                                        const valor = e.target.value
                                                        console.log(`🕐 Cambiando hora a: ${valor}`)
                                                        updateEvidenciaPaso3(ev.id, { horaRecoleccion: valor })
                                                    }}
                                                    disabled={state.paso3_completado}
                                                    style={{
                                                        width: '100%',
                                                        padding: '8px',
                                                        backgroundColor: '#0a0a0a',
                                                        border: ev.horaRecoleccion ? '1px solid #00ffff33' : '1px solid #ff000066',
                                                        color: '#00ffff',
                                                        borderRadius: '4px',
                                                        fontSize: '14px'
                                                    }}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                )
                            })}

                            {(() => {
                                const todasValidas = state.evidencias.every(ev =>
                                    ev.embalaje && ev.embalaje.trim() !== '' &&
                                    ev.horaRecoleccion && ev.horaRecoleccion.trim() !== ''
                                )

                                return (
                                    <div style={{
                                        padding: '12px',
                                        borderRadius: '4px',
                                        marginBottom: '16px',
                                        backgroundColor: todasValidas ? '#00ff0033' : '#ff000033',
                                        color: todasValidas ? '#00ff00' : '#ff0000'
                                    }}>
                                        {todasValidas
                                            ? '✅ Todos los campos obligatorios están completos'
                                            : '❌ Debes seleccionar embalaje y hora para cada evidencia'}
                                    </div>
                                )
                            })()}

                            {state.tipoEscena === 'solo_evidencia' && state.paso2_completado && (
                                <NeonButton
                                    onClick={async () => {
                                        console.log('🔄 Completando paso 3...')
                                        try {
                                            await completarPaso3()
                                        } catch (error) {
                                            console.error('❌ Error al completar paso 3:', error)
                                        }
                                    }}
                                    disabled={
                                        !canCompletarPaso3 ||
                                        state.paso3_completado ||
                                        !state.evidencias.every(ev => ev.embalaje && ev.embalaje.trim() !== '' && ev.horaRecoleccion && ev.horaRecoleccion.trim() !== '')
                                    }
                                >
                                    {state.paso3_completado ? '✓ Completado' : 'Finalizar recolección'}
                                </NeonButton>
                            )}

                            {state.tipoEscena === 'escena_completa' && (
                                <NeonButton
                                    onClick={async () => {
                                        console.log('🔄 Completando paso 3...')
                                        try {
                                            await completarPaso3()
                                        } catch (error) {
                                            console.error('❌ Error al completar paso 3:', error)
                                        }
                                    }}
                                    disabled={
                                        !canCompletarPaso3 ||
                                        state.paso3_completado ||
                                        !state.evidencias.every(ev => ev.embalaje && ev.embalaje.trim() !== '' && ev.horaRecoleccion && ev.horaRecoleccion.trim() !== '')
                                    }
                                >
                                    {state.paso3_completado ? '✓ Completado' : 'Completar Paso 3'}
                                </NeonButton>
                            )}
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
                                            <p><strong>📄 Folio expediente:</strong> {state.folioExpediente || 'No vinculado'}</p>
                                            <p><strong>🔒 Perímetro:</strong> {state.perimetro.sellado ? 'Sellado ✓' : 'No sellado'}</p>
                                            <p><strong>👮 Agentes:</strong> {state.perimetro.agentes}</p>
                                            <p><strong>🔬 Evidencias:</strong> {state.evidencias.length}</p>
                                            <ul style={{ marginLeft: '20px' }}>
                                                {state.evidencias.map((ev, idx) => (
                                                    <li key={idx}>
                                                        <strong>{ev.numeroSecuencial}</strong> - {ev.tipo}: {ev.descripcion.substring(0, 50)}...
                                                        <br />
                                                        <span style={{ fontSize: '11px' }}>📦 {ev.embalaje || 'Sin embalaje'} | 👤 {ev.responsable || 'Sin responsable'}</span>
                                                    </li>
                                                ))}
                                            </ul>
                                            <p><strong>⚠️ Escenas negativas:</strong> {state.noHayEscenaNegativa ? 'Sin elementos negativos' : state.escenaNegativa.length}</p>
                                            <p><strong>🔓 Liberación:</strong> {state.liberacion.hora}</p>
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
                                    resetEscena()
                                }}>
                                    + Nueva Escena
                                </NeonButton>
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
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 style={{ fontSize: '24px', color: '#00ffff' }}>Escena del Crimen</h2>
                <NeonButton onClick={() => setMostrarHistorial(!mostrarHistorial)}>
                    {mostrarHistorial ? '📋 Ocultar Historial' : '📋 Ver Historial'}
                </NeonButton>
            </div>

            {mostrarHistorial && (
                <div style={{ marginBottom: '24px' }}>
                    <HistorialEscenas />
                </div>
            )}

            <StepperVisual
                pasoActual={state.paso_actual}
                pasosCompletados={pasosCompletadosArray}
                labels={labelsPasos}
            />

            {renderPasoActual()}
        </div>
    )
}