import { createContext, useContext, useState, type ReactNode } from 'react'
import type { ExpedienteDetalleResponse, EstatusExpediente } from '../types/api.types'

interface ExpedienteActivoContextType {
    expedienteActivo: ExpedienteDetalleResponse | null
    setExpedienteActivo: (expediente: ExpedienteDetalleResponse) => void
    actualizarEstado: (nuevoEstado: EstatusExpediente) => void
    limpiarExpedienteActivo: () => void
}

const ExpedienteActivoContext = createContext<ExpedienteActivoContextType | undefined>(undefined)

export const ExpedienteActivoProvider = ({ children }: { children: ReactNode }) => {
    const [expedienteActivo, setExpedienteActivoState] = useState<ExpedienteDetalleResponse | null>(null)

    const setExpedienteActivo = (expediente: ExpedienteDetalleResponse) => {
        setExpedienteActivoState(expediente)
    }

    const actualizarEstado = (nuevoEstado: EstatusExpediente) => {
        setExpedienteActivoState(prev => (prev ? { ...prev, estadoExpediente: nuevoEstado } : prev))
    }

    const limpiarExpedienteActivo = () => setExpedienteActivoState(null)

    return (
        <ExpedienteActivoContext.Provider
            value={{ expedienteActivo, setExpedienteActivo, actualizarEstado, limpiarExpedienteActivo }}
        >
            {children}
        </ExpedienteActivoContext.Provider>
    )
}

export const useExpedienteActivo = () => {
    const ctx = useContext(ExpedienteActivoContext)
    if (!ctx) throw new Error('useExpedienteActivo debe usarse dentro de <ExpedienteActivoProvider>')
    return ctx
}