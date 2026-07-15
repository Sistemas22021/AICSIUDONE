import { Lock } from 'lucide-react'

interface SellarExpedienteButtonProps {
    onOpenPanel: () => void  // ← función que abre el panel lateral
}

export const SellarExpedienteButton = ({ onOpenPanel }: SellarExpedienteButtonProps) => {
    return (
        <button
            onClick={onOpenPanel}
            className="w-full px-3 py-2 text-[10px] uppercase tracking-[0.12em] text-cyan-300 hover:bg-cyan-400/15 transition-all text-left flex items-center gap-2"
        >
            <Lock size={12} />
            Sellar Expediente
        </button>
    )
}