// @ts-ignore
import { useState, useEffect } from 'react'

// --- TIPOS ---

export interface EscenaCrimenState {
    paso_actual: 1 | 2 | 3 | 4
    paso1_completado: boolean
    paso2_completado: boolean
    paso3_completado: boolean
    paso4_completado: boolean
    perimetro: {
        sellado: boolean
        agentes: number
        horaCierre: string
    }
    evidencias: Array<{
        id: string
        tipo: string
        descripcion: string
    }>
    recoleccion: {
        hora: string
        embalaje: string
    }
    liberacion: {
        hora: string
        pin: string
    }
    escenaNegativa: Array<{
        id: string
        elemento: string
        lugar: string
        resultado: string
    }>
}

const STORAGE_KEY = 'escena_crimen_local'

function makeInitialState(): EscenaCrimenState {
    return {
        paso_actual: 1,
        paso1_completado: false,
        paso2_completado: false,
        paso3_completado: false,
        paso4_completado: false,
        perimetro: {
            sellado: false,
            agentes: 0,
            horaCierre: '',
        },
        evidencias: [], // Empieza vacío
        recoleccion: {
            hora: '',
            embalaje: '',
        },
        liberacion: {
            hora: '',
            pin: '',
        },
        escenaNegativa: [], // Empieza vacío
    }
}

// --- HOOK ---

export function useEscenaCrimen() {
    const [state, setState] = useState<EscenaCrimenState>(()=> {
        const saved= localStorage.getItem(STORAGE_KEY)
        if (saved) {
            try {
                const parsed = JSON.parse(saved)
                if (!parsed.evidencias) parsed.evidencias = []
                if (!parsed.escenaNegativa) parsed.escenaNegativa = []
                return parsed
            } catch {
                return makeInitialState()
            }
        }
        return makeInitialState()
    })

    useEffect(() => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
    }, [state])

    const isPaso1Completado = state.paso1_completado
    const isPaso2Completado = state.paso2_completado
    const isPaso3Completado = state.paso3_completado
    const isPaso4Completado = state.paso4_completado

    const canCompletarPaso1 = state.perimetro.sellado && state.perimetro.agentes > 0 && !!state.perimetro.horaCierre

    // VALIDACIÓN: TODAS las evidencias deben estar completas
    const todasEvidenciasCompletas = state.evidencias.length > 0 && state.evidencias.every(e => e.tipo.trim() !== '' && e.descripcion.trim() !== '')

    // VALIDACIÓN: TODAS las escenas negativas deben estar completas
    const todasEscenasNegativasCompletas = state.escenaNegativa.length > 0 && state.escenaNegativa.every(en => en.elemento.trim() !== '' && en.lugar.trim() !== '' && en.resultado.trim() !== '')

    const canCompletarPaso2 = todasEvidenciasCompletas && todasEscenasNegativasCompletas
    const canCompletarPaso3 = !!state.recoleccion.hora && !!state.recoleccion.embalaje
    const canCompletarPaso4 = !!state.liberacion.hora && !!state.liberacion.pin

    const completarPaso1 = ()=> {
        if (!canCompletarPaso1) return
        if (isPaso1Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            paso_actual: 2,
            paso1_completado: true,
        }))
    }

    const completarPaso2 = ()=> {
        if (!canCompletarPaso2) return
        if (isPaso2Completado) return
        setState(( prev: EscenaCrimenState)  => ({
            ...prev,
            paso_actual: 3,
            paso2_completado: true,
        }))
    }

    const completarPaso3 = ()=> {
        if (!canCompletarPaso3) return
        if (isPaso3Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            paso_actual: 4,
            paso3_completado: true,
        }))
    }

    const completarPaso4 = ()=> {
        if (!canCompletarPaso4) return
        if (isPaso4Completado) return
        setState((prev: EscenaCrimenState)=> ({ ...prev, paso4_completado: true, }))
    }

    const siguientePaso = (paso: 1 | 2 | 3 | 4) => {
        if (paso === 1) {
            setState((prev: EscenaCrimenState) => ({ ...prev, paso_actual: 1 }))
        }
        if (paso === 2 && isPaso1Completado) {
            setState((prev: EscenaCrimenState) => ({ ...prev, paso_actual: 2 }))
        }
        if (paso === 3 && isPaso2Completado) {
            setState((prev: EscenaCrimenState) => ({ ...prev, paso_actual: 3 }))
        }
        if (paso === 4 && isPaso3Completado) {
            setState((prev: EscenaCrimenState) => ({ ...prev, paso_actual: 4 }))
        }
    }

    const updatePerimetro = (patch: Partial<EscenaCrimenState['perimetro']>) => {
        if (isPaso1Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, perimetro: { ...prev.perimetro, ...patch } }))
    }

    // Puedes agregar CUANTAS evidencias quieras (sin límite)
    const addEvidencia = () => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: [...prev.evidencias, { id: crypto.randomUUID(), tipo: '', descripcion: '' }],
        }))
    }

    const removeEvidencia = (id: string) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: prev.evidencias.filter(e => e.id !== id),
        }))
    }

    const updateEvidencia = (id: string, patch: { tipo?: string; descripcion?: string }) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            evidencias: prev.evidencias.map(e => e.id === id ? { ...e, ...patch } : e),
        }))
    }

    const updateRecoleccion = (patch: Partial<EscenaCrimenState['recoleccion']>) => {
        if (isPaso3Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, recoleccion: { ...prev.recoleccion, ...patch } }))
    }

    const updateLiberacion = (patch: Partial<EscenaCrimenState['liberacion']>) => {
        if (isPaso4Completado) return
        setState((prev: EscenaCrimenState) => ({ ...prev, liberacion: { ...prev.liberacion, ...patch } }))
    }

    // Puedes agregar CUANTAS escenas negativas quieras (sin límite)
    const addEscenaNegativa = () => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaNegativa: [...prev.escenaNegativa, { id: crypto.randomUUID(), elemento: '', lugar: '', resultado: '' }],
        }))
    }

    const removeEscenaNegativa = (id: string) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState)=> ({
            ...prev,
            escenaNegativa: prev.escenaNegativa.filter(e => e.id !== id),
        }))
    }

    const updateEscenaNegativa = (id: string, patch: { elemento?: string; lugar?: string; resultado?: string }) => {
        if (isPaso2Completado) return
        setState((prev: EscenaCrimenState) => ({
            ...prev,
            escenaNegativa: prev.escenaNegativa.map(e => e.id === id ? { ...e, ...patch } : e),
        }))
    }

    const resetEscena = () => {
        localStorage.removeItem(STORAGE_KEY)
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
        siguientePaso,
        updatePerimetro,
        addEvidencia,
        removeEvidencia,
        updateEvidencia,
        updateRecoleccion,
        updateLiberacion,
        addEscenaNegativa,
        removeEscenaNegativa,
        updateEscenaNegativa,
        resetEscena,
    }
}