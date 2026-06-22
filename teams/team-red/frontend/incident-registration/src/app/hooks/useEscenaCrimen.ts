// @ts-ignore
import { useState, useEffect } from 'react'

// --- TIPOS ---

export interface Evidencia {
    id: string
    numeroSecuencial: string  // EV-001, EV-002, etc.
    tipo: string
    descripcion: string
    ubicacion: string
    responsable: string  // Nombre del investigador
    embalaje: string
    horaRecoleccion: string
    hashIntegridad?: string
    hashLocal?: string
    timestamp?: string
    investigadorId?: number
    archivoNombre?: string
}

export interface EscenaNegativaItem {
    id: string
    elemento: string
    lugar: string
    resultado: string
    observacion: string
}

export interface EscenaCrimenState {
    paso_actual: 1 | 2 | 3 | 4
    paso1_completado: boolean
    paso2_completado: boolean
    paso3_completado: boolean
    paso4_completado: boolean
    tipoEscena: 'escena_completa' | 'solo_evidencia'
    folioExpediente: string | null
    expedienteId: number | null          // ID numérico del expediente en el backend
    escenaId: number | null              // ID de la Escena creada en el backend
    sincronizado: boolean                // true = estado viene del backend
    investigadorId: number | null
    investigadorNombre: string | null
    perimetro: {
        sellado: boolean
        agentes: number
        horaCierre: string
    }
    evidencias: Evidencia[]
    liberacion: {
        hora: string
        investigadorResponsableId: number | null
        observaciones: string
        hashLiberacion?: string
        investigadorNombre?: string
    }
    escenaNegativa: EscenaNegativaItem[]
    noHayEscenaNegativa: boolean
    alertasIntegridad: Array<{
        evidenciaId: string
        mensaje: string
        integro: boolean
    }>
}

const STORAGE_KEY = 'escena_crimen_local'

// Generador de número secuencial
let contadorEvidencias = 0

function generarNumeroSecuencial(): string {
    contadorEvidencias++
    return `EV-${String(contadorEvidencias).padStart(3, '0')}`
}

function makeInitialState(): EscenaCrimenState {
    contadorEvidencias = 0
    return {
        paso_actual: 1,
        paso1_completado: false,
        paso2_completado: false,
        paso3_completado: false,
        paso4_completado: false,
        tipoEscena: 'escena_completa',
        folioExpediente: null,
        expedienteId: null,
        escenaId: null,
        sincronizado: false,
        perimetro: {
            sellado: false,
            agentes: 0,
            horaCierre: '',
        },
        evidencias: [],
        liberacion: {
            hora: '',
            investigadorResponsableId: null,
            observaciones: '',
            hashLiberacion: undefined,
            investigadorNombre: undefined,
        },
        escenaNegativa: [],
        noHayEscenaNegativa: false,
        alertasIntegridad: [],
        investigadorId: null,
        investigadorNombre: null,
    }
}

// --- HOOK ---

export function useEscenaCrimen() {
    const [state, setState] = useState<EscenaCrimenState>(() => {
        const saved = localStorage.getItem(STORAGE_KEY)
        if (saved) {
            try {
                const parsed = JSON.parse(saved)
                if (!parsed.evidencias) parsed.evidencias = []
                parsed.evidencias = parsed.evidencias.map((e: any) => ({
                    id: e.id ?? crypto.randomUUID(),
                    numeroSecuencial: e.numeroSecuencial ?? '',
                    tipo: e.tipo ?? '',
                    descripcion: e.descripcion ?? '',
                    ubicacion: e.ubicacion ?? '',
                    responsable: e.responsable ?? '',
                    embalaje: e.embalaje ?? '',
                    horaRecoleccion: e.horaRecoleccion ?? '',
                    hashIntegridad: e.hashIntegridad,
                    timestamp: e.timestamp,
                }))

                if (!parsed.escenaNegativa) parsed.escenaNegativa = []
                parsed.escenaNegativa = parsed.escenaNegativa.map((en: any) => ({
                    id: en.id ?? crypto.randomUUID(),
                    elemento: en.elemento ?? '',
                    lugar: en.lugar ?? '',
                    resultado: en.resultado ?? '',
                    observacion: en.observacion ?? '',
                }))

                if (parsed.tipoEscena === undefined) parsed.tipoEscena = 'escena_completa'
                if (parsed.noHayEscenaNegativa === undefined) parsed.noHayEscenaNegativa = false
                parsed.liberacion = {
                    hora: parsed.liberacion?.hora || '',
                    investigadorResponsableId: parsed.liberacion?.investigadorResponsableId ?? null,
                    observaciones: parsed.liberacion?.observaciones ?? '',
                    hashLiberacion: parsed.liberacion?.hashLiberacion,
                    investigadorNombre: parsed.liberacion?.investigadorNombre,
                }
                if (parsed.alertasIntegridad === undefined) parsed.alertasIntegridad = []

                if (parsed.expedienteId === undefined) parsed.expedienteId = null
                if (parsed.escenaId === undefined) parsed.escenaId = null
                if (parsed.sincronizado === undefined) parsed.sincronizado = false
                if (parsed.investigadorId === undefined) parsed.investigadorId = null
                if (parsed.investigadorNombre === undefined) parsed.investigadorNombre = null

                if (parsed.evidencias.length > 0) {
                    const ultimo = parsed.evidencias[parsed.evidencias.length - 1]
                    const match = ultimo.numeroSecuencial?.match(/\d+/)
                    if (match) contadorEvidencias = parseInt(match[0])
                }
                return parsed
            } catch {
                return makeInitialState()
            }
        }
        return makeInitialState()
    })

    useEffect(() => {
        const escenaId = state.escenaId
        if (!escenaId) return
        import('../services/escenaService').then(({ obtenerEscena }) => {
            obtenerEscena(escenaId).then(escenaDTO => {
                const pasoMap: Record<string, 1 | 2 | 3 | 4> = {
                    ASEGURAR: 1, DOCUMENTAR: 2, RECOLECTAR: 3, LIBERAR: 4,
                }
                const pasoBackend = pasoMap[escenaDTO.pasoActual] ?? 1
                const completado = escenaDTO.estadoChecklist === 'COMPLETADO'
                setState(prev => ({
                    ...prev,
                    paso_actual: pasoBackend,
                    paso1_completado: pasoBackend > 1 || completado,
                    paso2_completado: pasoBackend > 2 || completado,
                    paso3_completado: pasoBackend > 3 || completado,
                    paso4_completado: completado,
                    sincronizado: true,
                }))
            }).catch(() => {
                localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
            })
        })
    }, [state.escenaId])

    const isPaso1Completado = state.paso1_completado
    const isPaso2Completado = state.paso2_completado
    const isPaso3Completado = state.paso3_completado
    const isPaso4Completado = state.paso4_completado

    const canCompletarPaso1 = (state.tipoEscena === 'solo_evidencia' ||
            (state.perimetro.sellado && state.perimetro.agentes > 0 && !!state.perimetro.horaCierre))
        && !!state.investigadorId

    const todasEvidenciasCompletas = state.evidencias.length > 0 && state.evidencias.every(e =>
        e.tipo.trim() !== '' &&
        e.descripcion.trim() !== '' &&
        e.ubicacion.trim() !== '' &&
        e.responsable.trim() !== ''
    )

    const escenaNegativaValida = state.noHayEscenaNegativa ||
        (state.escenaNegativa.length > 0 && state.escenaNegativa.every(en =>
            en.elemento.trim() !== '' && en.lugar.trim() !== '' && en.resultado.trim() !== ''
        ))

    const canCompletarPaso2 = todasEvidenciasCompletas && escenaNegativaValida
    const canCompletarPaso3 = state.tipoEscena === 'solo_evidencia' || state.paso2_completado
    const canCompletarPaso4 =
        state.liberacion.investigadorResponsableId !== null &&
        state.liberacion.investigadorResponsableId > 0

    // --- Acciones ---

    const setFolioExpediente = (folio: string) => {
        setState((prev: EscenaCrimenState) => ({ ...prev, folioExpediente: folio }))
    }

    const setInvestigador = (id: number, nombre: string) => {
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            investigadorId: id,
            investigadorNombre: nombre,
            liberacion: {
                ...prev.liberacion,
                investigadorResponsableId: id,
                investigadorNombre: nombre,
            },
        }))
    }

    const vincularExpediente = (expedienteId: number, folio: string) => {
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            expedienteId,
            folioExpediente: folio,
            sincronizado: false,
        }))
    }

    const setEscenaId = (id: number) => {
        setState((prev: EscenaCrimenState) => ({ ...prev, escenaId: id, sincronizado: true }))
    }

    const completarPaso1 = async () => {
        if (!canCompletarPaso1) return
        if (isPaso1Completado) return

        let escenaIdActual = state.escenaId

        if (state.expedienteId && !escenaIdActual) {
            if (!state.investigadorId) {
                throw new Error('Debes identificar al investigador responsable antes de iniciar el proceso.')
            }
            const { crearEscena, iniciarChecklistEscena } = await import('../services/escenaService')

            const nuevaEscena = await crearEscena({
                expedienteId: state.expedienteId,
                levantadaPorId: state.investigadorId,
            })
            escenaIdActual = nuevaEscena.id

            await iniciarChecklistEscena(escenaIdActual)

            setState((prev: EscenaCrimenState) => ({
                ...prev,
                escenaId: escenaIdActual,
                sincronizado: true,
            }))
        }

        if (escenaIdActual) {
            const { avanzarPasoEscena } = await import('../services/escenaService')
            await avanzarPasoEscena(escenaIdActual)
        }

        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaId: escenaIdActual ?? prev.escenaId,
            paso_actual: 2,
            paso1_completado: true,
        }))
    }

    const completarPaso2 = async () => {
        if (!canCompletarPaso2) return
        if (isPaso2Completado) return
        if (state.escenaId) {
            if (state.noHayEscenaNegativa) {
                await persistirNoHayEscenaNegativa()          // ← NUEVO
            } else {
                const pendientes = state.escenaNegativa.filter(en => en.id.includes('-'))
                for (const en of pendientes) {
                    await guardarEscenaNegativaEnBackend(en.id)
                }
            }
        }
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            paso_actual: 3,
            paso2_completado: true,
        }))
    }

    const completarPaso3 = async () => {
        if (!canCompletarPaso3) return
        if (isPaso3Completado) return
        if (state.escenaId) {
            const { avanzarPasoEscena } = await import('../services/escenaService')
            await avanzarPasoEscena(state.escenaId)
        }
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            paso_actual: 4,
            paso3_completado: true,
        }))
    }

    const completarPaso4 = async () => {
        if (!canCompletarPaso4) return
        if (isPaso4Completado) return

        if (state.escenaId && state.liberacion.investigadorResponsableId) {
            const { liberarEscena } = await import('../services/escenaService')
            const resultado = await liberarEscena(state.escenaId, {
                investigadorResponsableId: state.liberacion.investigadorResponsableId,
                observaciones: state.liberacion.observaciones || undefined,
            })
            setState((prev: EscenaCrimenState) => ({
                ...prev,
                paso4_completado: true,
                liberacion: {
                    ...prev.liberacion,
                    hora: resultado.horaLiberacion ?? new Date().toLocaleTimeString(),
                    hashLiberacion: resultado.hashLiberacion ?? undefined,
                    investigadorNombre: resultado.liberadaPor?.nombre ?? undefined,
                },
            }))
        } else {
            // Sin escenaId (modo offline)
            setState((prev: EscenaCrimenState) => ({
                ...prev,
                paso4_completado: true,
                liberacion: {
                    ...prev.liberacion,
                    hora: new Date().toLocaleTimeString(),
                },
            }))
        }
    }

    const updatePerimetro = (patch: Partial<EscenaCrimenState['perimetro']>) => {
        if (isPaso1Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, perimetro: { ...prev.perimetro, ...patch } }))
    }

    const setTipoEscena = (tipo: 'escena_completa' | 'solo_evidencia') => {
        if (isPaso1Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, tipoEscena: tipo }))
    }

    const addEvidencia = () => {
        if (isPaso2Completado) return
        const nuevoNumero = generarNumeroSecuencial()
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: [...prev.evidencias, {
                id: crypto.randomUUID(),
                numeroSecuencial: nuevoNumero,
                tipo: '',
                descripcion: '',
                ubicacion: '',
                responsable: '',
                embalaje: '',
                horaRecoleccion: '',
                hashIntegridad: undefined,
                timestamp: undefined,
            }],
        }))
    }

    const guardarEvidenciaEnBackend = async (localId: string) => {
        if (!state.escenaId) return
        const ev = state.evidencias.find(e => e.id === localId)
        if (!ev) return
        const { crearEvidencia } = await import('../services/escenaService')
        try {
            const dto = {
                numeroItem: ev.numeroSecuencial,
                tipo: ev.tipo,
                descripcion: ev.descripcion,
                escenaId: state.escenaId,
                investigadorId: ev.investigadorId ?? undefined,
            }
            const saved = await crearEvidencia(dto)
            // Actualizar el ID local → ID del backend, y volcar el hash recibido
            setState((prev: EscenaCrimenState) => ({
                ...prev,
                evidencias: prev.evidencias.map(e =>
                    e.id === localId
                        ? {
                            ...e,
                            id: String(saved.id),        // reemplaza el UUID temporal
                            hashIntegridad: saved.hashIntegridad,
                            timestamp: saved.timestampRegistro,
                        }
                        : e
                ),
            }))
        } catch (err) {
            console.error('Error al guardar evidencia en backend:', err)
            throw err   // El componente puede mostrar un toast de error
        }
    }

    const removeEvidencia = (id: string) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: prev.evidencias.filter(e => e.id !== id),
        }))
    }

    const updateEvidencia = (id: string, patch: Partial<Evidencia>) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: prev.evidencias.map(e => e.id === id ? { ...e, ...patch, timestamp: new Date().toISOString() } : e),
        }))
    }

    const updateLiberacion = (patch: Partial<EscenaCrimenState['liberacion']>) => {
        if (isPaso4Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, liberacion: { ...prev.liberacion, ...patch } }))
    }

    const addEscenaNegativa = () => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaNegativa: [...prev.escenaNegativa, {
                id: crypto.randomUUID(),
                elemento: '',
                lugar: '',
                resultado: '',
                observacion: ''
            }],
        }))
    }

    const removeEscenaNegativa = async (id: string) => {
        if (isPaso2Completado) return
        const isBackendId = !id.includes('-')   // UUIDs tienen guiones; IDs del backend son numéricos
        if (isBackendId) {
            const { eliminarEscenaNegativa } = await import('../services/escenaService')
            try {
                await eliminarEscenaNegativa(Number(id))
            } catch (err: any) {
                // Si el backend rechaza la eliminación (BusinessException → 422/400),
                // no se elimina localmente tampoco. OCP: la regla vive en el backend.
                console.error('El backend no permitió eliminar la escena negativa:', err.message)
                throw err   // El componente muestra el error al usuario
            }
        }
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaNegativa: prev.escenaNegativa.filter(e => e.id !== id),
        }))
    }

    const guardarEscenaNegativaEnBackend = async (localId: string) => {
        if (!state.escenaId) return
        const en = state.escenaNegativa.find(e => e.id === localId)
        if (!en) return
        const { crearEscenaNegativa } = await import('../services/escenaService')
        try {
            const dto = {
                elementoBuscado: en.elemento,        // mapping frontend → backend
                areaInspeccionada: en.lugar,         // mapping frontend → backend
                resultado: en.resultado,
                observacion: en.observacion,
                escenaId: state.escenaId,
            }
            const saved = await crearEscenaNegativa(dto)
            setState((prev: EscenaCrimenState) => ({
                ...prev,
                escenaNegativa: prev.escenaNegativa.map(e =>
                    e.id === localId ? { ...e, id: String(saved.id) } : e
                ),
            }))
        } catch (err) {
            console.error('Error al guardar escena negativa en backend:', err)
            throw err
        }
    }

    const persistirNoHayEscenaNegativa = async () => {
        if (!state.escenaId) return
        const { crearEscenaNegativa } = await import('../services/escenaService')
        try {
            await crearEscenaNegativa({
                elementoBuscado: 'SIN_ELEMENTOS_NEGATIVOS',
                areaInspeccionada: 'N/A',
                resultado: 'SIN_HALLAZGOS',
                observacion: 'El investigador confirmó que no hay elementos negativos a reportar.',
                escenaId: state.escenaId,
                sinElementosNegativos: true,
            })
        } catch (err) {
            console.error('Error al persistir flag sinElementosNegativos:', err)
            throw err
        }
    }

    const updateEscenaNegativa = (id: string, patch: Partial<EscenaNegativaItem>) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaNegativa: prev.escenaNegativa.map(e => e.id === id ? { ...e, ...patch } : e),
        }))
    }

    const setNoHayEscenaNegativa = (value: boolean) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            noHayEscenaNegativa: value,
            escenaNegativa: value ? [] : prev.escenaNegativa
        }))
    }

    const verificarIntegridad = async (_expedienteId?: string) => {
        if (!state.escenaId) {
            console.warn('No hay escenaId — la escena aún no fue persistida en el backend.')
            return
        }
        const { verificarHashEvidencia } = await import('../services/escenaService')
        const resultados = await Promise.allSettled(
            state.evidencias
                .filter(ev => ev.id && !ev.id.includes('-'))   // IDs numéricos son del backend
                .map(async ev => {
                    const integro = await verificarHashEvidencia(Number(ev.id))
                    return { evidenciaId: ev.id, integro }
                })
        )
        const nuevasAlertas = resultados
            .filter((r): r is PromiseFulfilledResult<{ evidenciaId: string; integro: boolean }> => r.status === 'fulfilled')
            .map(r => ({
                evidenciaId: r.value.evidenciaId,
                mensaje: r.value.integro
                    ? `Evidencia ${r.value.evidenciaId}: integridad verificada ✓`
                    : `⚠ Evidencia ${r.value.evidenciaId}: discrepancia de hash detectada`,
                integro: r.value.integro,
            }))
        setState((prev: EscenaCrimenState) => ({ ...prev, alertasIntegridad: nuevasAlertas }))
    }

    const limpiarAlertas = () => {
        setState((prev: EscenaCrimenState) => ({ ...prev, alertasIntegridad: [] }))
    }

    const resetEscena = () => {
        localStorage.removeItem(STORAGE_KEY)
        contadorEvidencias = 0
        setState(makeInitialState())
    }

    return {
        state,
        isPaso1Completado,
        isPaso2Completado,
        isPaso3Completado,
        isPaso4Completado,
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
        guardarEvidenciaEnBackend,
        removeEvidencia,
        updateEvidencia,
        updateLiberacion,
        addEscenaNegativa,
        removeEscenaNegativa,
        guardarEscenaNegativaEnBackend,
        persistirNoHayEscenaNegativa,
        updateEscenaNegativa,
        setNoHayEscenaNegativa,
        verificarIntegridad,
        limpiarAlertas,
        resetEscena,
        setInvestigador,
    }
}