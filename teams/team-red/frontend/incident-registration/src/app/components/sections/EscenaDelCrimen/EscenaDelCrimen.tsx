import { useState, useEffect } from 'react'
import { useEscenaCrimen } from '../../../hooks/useEscenaCrimen'
import { NeonButton } from '../../ui/NeonButton'
import { NeonInput } from '../../ui/NeonInput'
import { NeonSelect } from '../../ui/NeonSelect'
import { NeonPanel } from '../../ui/NeonPanel'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonConfirmModal } from '../../ui/NeonConfirmModal'
import { useNeonToast } from '../../ui/NeonToast'
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
    const [busquedaInvestigador, setBusquedaInvestigador] = useState('')
    const [buscandoInvestigador, setBuscandoInvestigador] = useState(false)
    const [errorBusqueda, setErrorBusqueda] = useState<string | null>(null)

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
        addEvidencia,
        removeEvidencia,
        updateEvidencia,
        updateLiberacion,
        addEscenaNegativa,
        removeEscenaNegativa,
        updateEscenaNegativa,
        setNoHayEscenaNegativa,
        verificarIntegridad,
        limpiarAlertas,
        resetEscena,
        setInvestigador,
    } = useEscenaCrimen()

    useEffect(() => {
        if (expedienteIdInicial && folioInicial && !state.expedienteId) {
            vincularExpediente(expedienteIdInicial, folioInicial)
        }
    }, [expedienteIdInicial, folioInicial])

    const { showToast, ToastContainer } = useNeonToast()
    const [mostrarConfirmLiberacion, setMostrarConfirmLiberacion] = useState(false)
    const [liberando, setLiberando] = useState(false)

    const pasosCompletadosArray = [
        state.paso1_completado ? 1 : null,
        state.paso2_completado ? 2 : null,
        state.paso3_completado ? 3 : null,
        state.paso4_completado ? 4 : null,
    ].filter(Boolean) as number[]

    {/*const progreso = (pasosCompletadosArray.length / 4) * 100*/}

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

    const handleVerificarIntegridad = () => {
        if (state.folioExpediente) {
            verificarIntegridad(state.folioExpediente)
        } else {
            alert('Primero vincula este formulario a un expediente usando el campo "Folio del Expediente"')
        }
    }

    const handleBuscarInvestigador = async () => {
        if (!busquedaInvestigador.trim()) return
        setBuscandoInvestigador(true)
        setErrorBusqueda(null)
        try {
            const { buscarInvestigadorPorCorreo, buscarInvestigadorPorNombre } = await import('../../../services/escenaService')
            const esCorreo = busquedaInvestigador.includes('@')
            const usuario = esCorreo
                ? await buscarInvestigadorPorCorreo(busquedaInvestigador.trim())
                : await buscarInvestigadorPorNombre(busquedaInvestigador.trim())
            setInvestigador(usuario.id, usuario.nombre)
            setBusquedaInvestigador('')
        } catch {
            setErrorBusqueda('No se encontró un investigador con ese correo o identificación.')
        } finally {
            setBuscandoInvestigador(false)
        }
    }

    const handleBuscarInvestigadorPaso4 = async () => {
        if (!busquedaInvestigador.trim()) return
        setBuscandoInvestigador(true)
        setErrorBusqueda(null)
        try {
            const { buscarInvestigadorPorCorreo, buscarInvestigadorPorNombre } = await import('../../../services/escenaService')
            const esCorreo = busquedaInvestigador.includes('@')
            const usuario = esCorreo
                ? await buscarInvestigadorPorCorreo(busquedaInvestigador.trim())
                : await buscarInvestigadorPorNombre(busquedaInvestigador.trim())
            updateLiberacion({
                investigadorResponsableId: usuario.id,
                investigadorNombre: usuario.nombre,
            })
            setBusquedaInvestigador('')
        } catch {
            setErrorBusqueda('No se encontró un investigador con ese correo o identificación.')
        } finally {
            setBuscandoInvestigador(false)
        }
    }

    const handleConfirmarLiberacion = async () => {
        setMostrarConfirmLiberacion(false)
        setLiberando(true)
        try {
            await completarPaso4()
            showToast('Escena liberada formalmente. El Guardia de turno ha sido notificado.', 'success')
        } catch (err: any) {
            const msg = err?.message?.includes('HTTP')
                ? err.message
                : 'Error al liberar la escena. Verifica los datos e intenta de nuevo.'
            showToast(`${msg}`, 'error')
        } finally {
            setLiberando(false)
        }
    }

    const renderPasoActual = () => {
        switch (state.paso_actual) {
            case 1:
                return (
                    <NeonPanel>
                        <h3>Paso 1: Aseguramiento del perímetro</h3>

                        {/* Opción: Escena completa vs Solo evidencia */}
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

                        {/* Folio del expediente - NUEVO */}
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

                        {/* Lookup del investigador responsable */}
                        <div style={{ marginBottom: '8px' }}>
                            {state.investigadorId ? (
                                <div style={{
                                    padding: '10px 14px',
                                    backgroundColor: '#00ff0011',
                                    border: '1px solid #00ff0044',
                                    borderRadius: '8px',
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                }}>
                                    <span style={{ color: '#00ff88', fontSize: '13px' }}>
                                        👤 Investigador: <strong>{state.investigadorNombre}</strong>
                                    </span>
                                    {!isPaso1Completado && (
                                        <NeonButton
                                            variant="ghost"
                                            onClick={() => setInvestigador(0, '')}
                                            style={{ fontSize: '11px', padding: '2px 8px' }}
                                        >
                                            Cambiar
                                        </NeonButton>
                                    )}
                                </div>
                            ) : (
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                    <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-end' }}>
                                        <div style={{ flex: 1 }}>
                                            <NeonInput
                                                label="Investigador responsable (correo o identificación) *"
                                                value={busquedaInvestigador}
                                                onChange={(e: any) => {
                                                    setBusquedaInvestigador(e.target.value)
                                                    setErrorBusqueda(null)
                                                }}
                                                onKeyDown={(e: any) => e.key === 'Enter' && handleBuscarInvestigador()}
                                                placeholder="correo@guardia.com  o  001-1234567-8"
                                                disabled={isPaso1Completado || buscandoInvestigador}
                                                error={!!errorBusqueda}
                                                errorMessage={errorBusqueda ?? undefined}
                                            />
                                        </div>
                                        <NeonButton
                                            variant="outline"
                                            onClick={handleBuscarInvestigador}
                                            disabled={!busquedaInvestigador.trim() || buscandoInvestigador || isPaso1Completado}
                                            style={{ whiteSpace: 'nowrap', marginBottom: errorBusqueda ? '22px' : '0' }}
                                        >
                                            {buscandoInvestigador ? '⏳' : '🔍 Verificar'}
                                        </NeonButton>
                                    </div>
                                </div>
                            )}
                        </div>

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

                        {/* Botón Verificar Integridad - NUEVO */}
                        <div style={{ marginBottom: '16px' }}>
                            <NeonButton onClick={handleVerificarIntegridad} style={{ backgroundColor: '#00ffff33' }}>
                                🔍 Verificar Integridad del Expediente
                            </NeonButton>
                        </div>

                        {/* Alertas de Integridad - NUEVO */}
                        <AlertaIntegridad alertas={state.alertasIntegridad} onLimpiar={limpiarAlertas} />

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
                                        </div>
                                        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <NeonInput
                                                    label="Ubicación"
                                                    value={ev.ubicacion}
                                                    onChange={(e: any) => updateEvidencia(ev.id, { ubicacion: e.target.value })}
                                                    disabled={isPaso2Completado}
                                                    placeholder="Lugar exacto donde se encontró"
                                                />
                                            </div>
                                            <div style={{ flex: 2, minWidth: '150px' }}>
                                                <NeonInput
                                                    label="Responsable (Investigador)"
                                                    value={ev.responsable}
                                                    onChange={(e: any) => updateEvidencia(ev.id, { responsable: e.target.value })}
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

                        {/* Escena Negativa */}
                        <div style={{ marginBottom: '32px' }}>
                            <h4>Escena Negativa</h4>

                            {/* Checkbox "Sin elementos negativos" - NUEVO */}
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
                            onClick={() => completarPaso2().catch(err => console.error('Error al persistir paso 2:', err))}
                            disabled={!canCompletarPaso2 || state.paso2_completado}
                        >
                            {state.paso2_completado ? '✓ Completado' : 'Completar Paso 2'}
                        </NeonButton>
                    </NeonPanel>
                )

            case 3:
                return (
                    <NeonPanel>
                        <h3>Paso 3: Recolección de evidencias (por elemento)</h3>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                            {state.evidencias.map((ev) => (
                                <div
                                    key={ev.id}
                                    style={{
                                        border: '1px solid #00ffff33',
                                        padding: '16px',
                                        borderRadius: '8px',
                                    }}
                                >
                                    <div style={{ marginBottom: '12px' }}>
                                        <strong style={{ color: '#00ffff' }}>{ev.numeroSecuencial}</strong> - {ev.tipo}
                                    </div>
                                    <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonSelect
                                                label="Tipo de embalaje"
                                                options={tiposEmbalaje.map((t: string) => ({ value: t, label: t }))}
                                                value={ev.embalaje || ''}
                                                onChange={(e: any) => updateEvidencia(ev.id, { embalaje: e.target.value })}
                                                disabled={state.paso3_completado}
                                            />
                                        </div>
                                        <div style={{ flex: 2, minWidth: '150px' }}>
                                            <NeonInput
                                                label="Responsable de la cadena"
                                                value={ev.responsable || ''}
                                                onChange={(e: any) => updateEvidencia(ev.id, { responsable: e.target.value })}
                                                disabled={state.paso3_completado}
                                                placeholder="Quién recibe la evidencia"
                                            />
                                        </div>
                                        <div style={{ flex: 1, minWidth: '120px' }}>
                                            <NeonInput
                                                label="Hora de recolección"
                                                type="time"
                                                value={ev.horaRecoleccion || ''}
                                                onChange={(e: any) => updateEvidencia(ev.id, { horaRecoleccion: e.target.value })}
                                                disabled={state.paso3_completado}
                                            />
                                        </div>
                                    </div>
                                </div>
                            ))}

                            {state.tipoEscena === 'solo_evidencia' && state.paso2_completado && (
                                <NeonButton
                                    onClick={completarPaso3}
                                    disabled={!canCompletarPaso3 || state.paso3_completado}
                                >
                                    {state.paso3_completado ? '✓ Completado' : 'Finalizar recolección'}
                                </NeonButton>
                            )}

                            {state.tipoEscena === 'escena_completa' && (
                                <NeonButton
                                    onClick={completarPaso3}
                                    disabled={!canCompletarPaso3 || state.paso3_completado}
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
                                <h3>Paso 4: Liberación de la escena</h3>

                                {/* Aviso de acción irreversible */}
                                <div style={{
                                    padding: '12px 16px',
                                    backgroundColor: '#ff990011',
                                    border: '1px solid #ff990044',
                                    borderRadius: '8px',
                                    marginBottom: '24px',
                                    fontSize: '13px',
                                    color: '#ffaa44',
                                }}>
                                    ⚠️ Esta acción es <strong>irreversible</strong>. Una vez registrada, la escena quedará
                                    sellada y el expediente actualizará su estado a <strong>LIBERADA</strong>.
                                </div>

                                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>

                                    {/* Investigador responsable de la liberación */}
                                    {state.liberacion.investigadorResponsableId ? (
                                        <div style={{
                                            padding: '10px 14px',
                                            backgroundColor: '#00ff0011',
                                            border: '1px solid #00ff0044',
                                            borderRadius: '8px',
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',
                                        }}>
                                            <span style={{ color: '#00ff88', fontSize: '13px' }}>
                                                👤 Investigador: <strong>{state.liberacion.investigadorNombre || `ID ${state.liberacion.investigadorResponsableId}`}</strong>
                                            </span>
                                            {!liberando && (
                                                <NeonButton
                                                    variant="ghost"
                                                    onClick={() => updateLiberacion({ investigadorResponsableId: null, investigadorNombre: undefined })}
                                                    style={{ fontSize: '11px', padding: '2px 8px' }}
                                                >
                                                    Cambiar
                                                </NeonButton>
                                            )}
                                        </div>
                                    ) : (
                                        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                            <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-end' }}>
                                                <div style={{ flex: 1 }}>
                                                    <NeonInput
                                                        label="Investigador responsable de la liberación (correo o identificación) *"
                                                        value={busquedaInvestigador}
                                                        onChange={(e: any) => {
                                                            setBusquedaInvestigador(e.target.value)
                                                            setErrorBusqueda(null)
                                                        }}
                                                        onKeyDown={(e: any) => e.key === 'Enter' && handleBuscarInvestigadorPaso4()}
                                                        placeholder="correo@guardia.com  o  001-1234567-8"
                                                        disabled={liberando}
                                                        error={!!errorBusqueda}
                                                        errorMessage={errorBusqueda ?? undefined}
                                                    />
                                                </div>
                                                <NeonButton
                                                    variant="outline"
                                                    onClick={handleBuscarInvestigadorPaso4}
                                                    disabled={!busquedaInvestigador.trim() || liberando}
                                                    style={{ whiteSpace: 'nowrap', marginBottom: errorBusqueda ? '22px' : '0' }}
                                                >
                                                    {buscandoInvestigador ? '⏳' : '🔍 Verificar'}
                                                </NeonButton>
                                            </div>
                                        </div>
                                    )}

                                    {/* Observaciones opcionales */}
                                    <NeonTextarea
                                        label="Observaciones (opcional)"
                                        value={state.liberacion.observaciones}
                                        onChange={(e: any) => updateLiberacion({ observaciones: e.target.value })}
                                        disabled={liberando}
                                        placeholder="Notas sobre el estado final de la escena, condiciones al momento de la liberación, etc."
                                        rows={4}
                                        showCount
                                        maxCount={2000}
                                        maxLength={2000}
                                    />

                                    {/* Validación visible */}
                                    <div style={{
                                        padding: '10px 14px',
                                        borderRadius: '6px',
                                        backgroundColor: canCompletarPaso4 ? '#00ff0022' : '#ff000022',
                                        color: canCompletarPaso4 ? '#00ff88' : '#ff6666',
                                        fontSize: '13px',
                                    }}>
                                        {canCompletarPaso4
                                            ? '✅ Listo para liberar la escena'
                                            : '❌ El ID del investigador responsable es obligatorio'}
                                    </div>

                                    <NeonButton
                                        variant="danger"
                                        onClick={() => setMostrarConfirmLiberacion(true)}
                                        disabled={!canCompletarPaso4 || liberando}
                                    >
                                        {liberando ? '⏳ Registrando liberación...' : '🔓 Liberar Escena'}
                                    </NeonButton>
                                </div>

                                {/* Modal de confirmación */}
                                <NeonConfirmModal
                                    isOpen={mostrarConfirmLiberacion}
                                    title="⚠️ Confirmar Liberación"
                                    message={`¿Confirmas la liberación formal de esta escena? Esta acción no puede deshacerse y quedará registrada con timestamp oficial.`}
                                    confirmLabel="Continuar"
                                    cancelLabel="Cancelar"
                                    confirmVariant="success"
                                    onConfirm={handleConfirmarLiberacion}
                                    onCancel={() => setMostrarConfirmLiberacion(false)}
                                />
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

                                            {/* Bloque de liberación */}
                                            <div style={{
                                                marginTop: '12px',
                                                padding: '12px',
                                                backgroundColor: '#00ff0011',
                                                border: '1px solid #00ff0033',
                                                borderRadius: '8px',
                                            }}>
                                                <p style={{ color: '#00ff88', marginBottom: '6px' }}><strong>🔓 Liberación oficial:</strong></p>
                                                {state.liberacion.investigadorNombre && (
                                                    <p><strong>👤 Investigador:</strong> {state.liberacion.investigadorNombre}</p>
                                                )}
                                                <p><strong>🕐 Hora de cierre:</strong> {state.liberacion.hora}</p>
                                                {state.liberacion.hashLiberacion && (
                                                    <p style={{ fontSize: '11px', wordBreak: 'break-all' }}>
                                                        <strong>🔑 Hash:</strong>{' '}
                                                        <span style={{ fontFamily: 'monospace', color: '#00ffffaa' }}>
                                                            {state.liberacion.hashLiberacion}
                                                        </span>
                                                    </p>
                                                )}
                                                {state.liberacion.observaciones && (
                                                    <p><strong>📝 Observaciones:</strong> {state.liberacion.observaciones}</p>
                                                )}
                                            </div>
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
            {/* Título y botón de historial */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 style={{ fontSize: '24px', color: '#00ffff' }}>Escena del Crimen</h2>
                <NeonButton onClick={() => setMostrarHistorial(!mostrarHistorial)}>
                    {mostrarHistorial ? '📋 Ocultar Historial' : '📋 Ver Historial'}
                </NeonButton>
            </div>

            {/* Historial */}
            {mostrarHistorial && (
                <div style={{ marginBottom: '24px' }}>
                    <HistorialEscenas />
                </div>
            )}

            {/* Stepper Visual - NUEVO */}
            <StepperVisual
                pasoActual={state.paso_actual}
                pasosCompletados={pasosCompletadosArray}
                labels={labelsPasos}
            />

            {renderPasoActual()}
            <ToastContainer />
        </div>
    )
}