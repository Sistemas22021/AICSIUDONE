import { X } from 'lucide-react'
import { ModusOperandiContent } from './ModusOperandiContent'

interface ModusOperandiPanelProps {
    isOpen: boolean
    onClose: () => void
    expedienteId: string
    folioExpediente: string
    analistaId: number
}

/** Wrapper de modal para ModusOperandiContent — usado por el botón "Ver MO" de ExpedientesPanel. */
export const ModusOperandiPanel = ({ isOpen, onClose, expedienteId, folioExpediente, analistaId }: ModusOperandiPanelProps) => {
    if (!isOpen) return null

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-[#020818]/92 backdrop-blur-md" onClick={onClose} />
            <div
                className="relative w-full max-w-3xl max-h-[92vh] flex flex-col rounded border-2 border-cyan-400/60 bg-[#060B10]/98 backdrop-blur-sm overflow-hidden"
                style={{ boxShadow: '0 4px 30px rgba(51,153,255,0.5), 0 8px 60px rgba(51,153,255,0.25)' }}
            >
                <div className="flex-shrink-0 flex justify-end px-4 pt-4">
                    <button onClick={onClose} className="p-2 border border-cyan-400/40 rounded text-cyan-400 hover:bg-cyan-400/10 transition-all">
                        <X size={16} />
                    </button>
                </div>
                <div className="flex-1 overflow-auto px-6 pb-6">
                    <ModusOperandiContent expedienteId={expedienteId} folioExpediente={folioExpediente} analistaId={analistaId} />
                </div>
            </div>
        </div>
    )
}