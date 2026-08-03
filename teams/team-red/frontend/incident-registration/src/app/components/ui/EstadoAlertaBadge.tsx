import { Siren, ShieldCheck, ShieldOff, type LucideIcon } from 'lucide-react'
import type { EstadoAlertaPatron } from '../../types/api.types'

interface EstadoMeta {
    label: string
    className: string
    Icon: LucideIcon
}

const ESTADO_META: Record<EstadoAlertaPatron, EstadoMeta> = {
    PENDIENTE:  { label: 'Pendiente de revisión', className: 'text-amber-400 border-amber-500/40 bg-amber-500/10 animate-pulse', Icon: Siren },
    REVISADA:   { label: 'Revisada',              className: 'text-emerald-400 border-emerald-500/40 bg-emerald-500/10',        Icon: ShieldCheck },
    DESCARTADA: { label: 'Descartada',            className: 'text-cyan-600 border-cyan-600/30 bg-cyan-600/5',                  Icon: ShieldOff },
}

/** Badge reutilizable para el estado de una alerta de patrón de MO (HU6). Las pendientes parpadean para quedar destacadas (CA6). */
export const EstadoAlertaBadge = ({ estado }: { estado: EstadoAlertaPatron }) => {
    const { label, className, Icon } = ESTADO_META[estado]
    return (
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border ${className}`}>
            <Icon size={11} />
            {label}
        </span>
    )
}