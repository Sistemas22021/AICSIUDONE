// Estado y lógica del array de delitos
import { useState } from 'react'

// ─── Tipos ────────────────────────────────────────────────────────────────────

export interface DelitoEntry {
    id: string
    tipoValue: string
    tipoLabel: string
    subtipoValue: string
    subtipoLabel: string
    fechaHecho: string
    horaInicio: string
    horaFin: string
    hechoEnCurso: boolean
    fechaError: boolean
    tiempoError: boolean
    tiempoErrorMsg: string
}

// ─── Factory ──────────────────────────────────────────────────────────────────

function makeDelitoEntry(): DelitoEntry {
    return {
        id: crypto.randomUUID(),
        tipoValue: '',
        tipoLabel: '',
        subtipoValue: '',
        subtipoLabel: '',
        fechaHecho: '',
        horaInicio: '',
        horaFin: '',
        hechoEnCurso: false,
        fechaError: false,
        tiempoError: false,
        tiempoErrorMsg: '',
    }
}

// ─── Hook ─────────────────────────────────────────────────────────────────────

export function useDelitoList() {
    const [delitos, setDelitos] = useState<DelitoEntry[]>([makeDelitoEntry()])

    const updateDelito = (id: string, patch: Partial<DelitoEntry>) =>
        setDelitos(prev => prev.map(d => (d.id === id ? { ...d, ...patch } : d)))

    const addDelito = () =>
        setDelitos(prev => [...prev, makeDelitoEntry()])

    const removeDelito = (id: string) =>
        setDelitos(prev => (prev.length > 1 ? prev.filter(d => d.id !== id) : prev))

    const validateTiempos = (
        id: string,
        inicio: string,
        fin: string,
        enCurso: boolean,
    ) => {
        let error = false
        let msg = ''

        if (!inicio && !fin && !enCurso) {
            error = true
            msg = 'Debe ingresar al menos la hora de inicio o de finalización'
        } else if (fin && !inicio) {
            error = true
            msg = 'Si tiene hora de finalización debe ingresar la hora de inicio'
        } else if (inicio && fin && fin < inicio) {
            error = true
            msg = 'La hora de finalización no puede ser anterior a la de inicio'
        }

        updateDelito(id, { tiempoError: error, tiempoErrorMsg: msg })
    }

    return { delitos, updateDelito, addDelito, removeDelito, validateTiempos }
}
