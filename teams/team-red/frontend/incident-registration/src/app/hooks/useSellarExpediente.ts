import { useState } from 'react'
import { sellarExpediente as sellarExpedienteService } from '../services/sellarExpedienteService'

export function useSellarExpediente() {
    const [sellando, setSellando] = useState(false)

    const sellarExpediente = async (
        expedienteId: number,
        agenteSelladorId: number,
    ): Promise<{ exito: true } | { exito: false; mensaje: string }> => {
        setSellando(true)
        try {
            await sellarExpedienteService(expedienteId, agenteSelladorId)
            return { exito: true }
        } catch (err) {
            const mensaje = err instanceof Error ? err.message : 'Error desconocido al sellar'
            return { exito: false, mensaje }
        } finally {
            setSellando(false)
        }
    }

    return { sellando, sellarExpediente }
}