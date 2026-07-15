import { useState } from 'react'
import { CheckCircle2, Lock, ArrowLeft } from 'lucide-react'
import { NeonSelect } from './ui/NeonSelect'
import { NeonButton } from './ui/NeonButton'
import { useUsuarios } from '../hooks/useUsuarios'
import { useSellarExpediente } from '../hooks/useSellarExpediente'
import type { ExpedienteActivo } from '../types/api.types'

interface SellarExpedienteFormProps {
    expediente: ExpedienteActivo
    onSellado:  () => void   // se llama cuando el sellado terminó exitosamente
    onCancelar: () => void   // vuelve al listado sin hacer nada
}

export const SellarExpedienteForm = ({ expediente, onSellado, onCancelar }: SellarExpedienteFormProps) => {
    const { usuarios, loading: cargandoUsuarios } = useUsuarios()
    const { sellando, sellarExpediente } = useSellarExpediente()

    const [agenteSelladorId, setAgenteSelladorId] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [exito, setExito] = useState(false)

    const handleConfirmar = async () => {
        if (!agenteSelladorId) {
            setError('Debes seleccionar el agente que sella el expediente.')
            return
        }
        setError(null)

        const resultado = await sellarExpediente(Number(expediente.id), Number(agenteSelladorId))
        if (resultado.exito) {
            setExito(true)
        } else {
            setError(resultado.mensaje)
        }
    }

    if (exito) {
        return (
            <div className="flex flex-col items-center justify-center py-16 gap-4 text-center">
                <CheckCircle2 size={40} className="text-emerald-400" style={{ filter: 'drop-shadow(0 0 10px rgba(0,255,136,0.6))' }} />
                <div>
                    <div className="text-sm uppercase tracking-wider text-emerald-300 font-semibold mb-1">
                        Expediente sellado exitosamente
                    </div>
                    <div className="text-xs text-cyan-500">
                        {expediente.folioCOPP} quedó marcado como sellado y ya no aparecerá en pendientes.
                    </div>
                </div>
                <NeonButton variant="success" onClick={onSellado}>
                    Volver al listado
                </NeonButton>
            </div>
        )
    }

    return (
        <div className="max-w-md mx-auto py-6">
            <button
                onClick={onCancelar}
                className="mb-5 flex items-center gap-1.5 text-[11px] uppercase tracking-wider text-cyan-500 hover:text-cyan-300 transition-colors"
            >
                <ArrowLeft size={12} />
                Volver al listado
            </button>

            <div className="flex items-center gap-2 mb-1">
                <Lock size={15} className="text-amber-400" />
                <span className="text-sm uppercase tracking-wider text-cyan-300 font-semibold">Sellar Expediente</span>
            </div>
            <p className="text-[11px] text-cyan-600 mb-5">
                Esta acción es definitiva y no podrá modificarse después.
            </p>

            {/* Datos del expediente — solo lectura, para confirmar que es el correcto */}
            <div className="border border-cyan-400/15 rounded px-4 py-3 mb-5 bg-[#04101E]/40 text-xs">
                <div className="flex justify-between py-1">
                    <span className="text-cyan-600">Folio</span>
                    <span className="text-cyan-300 font-medium">{expediente.folioCOPP}</span>
                </div>
                <div className="flex justify-between py-1">
                    <span className="text-cyan-600">Delito</span>
                    <span className="text-cyan-300">{expediente.subtipoDelito ?? 'Sin especificar'}</span>
                </div>
                <div className="flex justify-between py-1">
                    <span className="text-cyan-600">Municipio</span>
                    <span className="text-cyan-300">{expediente.municipio ?? '—'}</span>
                </div>
            </div>

            {/* Dato necesario para completar el sellado */}
            <NeonSelect
                label="Agente Sellador"
                required
                error={!!error && !agenteSelladorId}
                disabled={cargandoUsuarios}
                value={agenteSelladorId}
                onChange={e => setAgenteSelladorId(e.target.value)}
                options={[
                    { value: '', label: cargandoUsuarios ? 'Cargando usuarios…' : 'Selecciona un agente' },
                    ...usuarios.map(u => ({ value: String(u.id), label: `${u.nombre} (${u.identificacion})` })),
                ]}
            />

            {error && <p className="mt-2 text-[11px] text-red-400">{error}</p>}

            <div className="flex gap-3 justify-end mt-6">
                <NeonButton variant="outline" onClick={onCancelar} disabled={sellando}>
                    Cancelar
                </NeonButton>
                <NeonButton variant="danger" onClick={handleConfirmar} disabled={sellando || cargandoUsuarios}>
                    {sellando ? 'Sellando…' : 'Confirmar Sellado'}
                </NeonButton>
            </div>
        </div>
    )
}