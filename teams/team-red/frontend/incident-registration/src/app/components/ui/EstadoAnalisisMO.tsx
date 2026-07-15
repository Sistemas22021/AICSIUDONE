import { Loader2, Sparkles, ShieldCheck, ShieldAlert, ShieldX, HelpCircle, type LucideIcon } from 'lucide-react'
import type { EstadoPropuestaMO } from '../../types/api.types'
import type { EstadoCargaMO } from '../../hooks/usePropuestaModusOperandi'

interface EstadoAnalisisMOProps {
    estadoCarga: EstadoCargaMO
    estado?: EstadoPropuestaMO
}

interface EstadoMeta {
    label: string
    className: string
    Icon: LucideIcon
}

const ESTADO_META: Record<EstadoPropuestaMO, EstadoMeta> = {
    PENDIENTE:         { label: 'MO propuesto — sin revisar', className: 'text-amber-400 border-amber-500/40 bg-amber-500/10', Icon: Sparkles },
    SIN_COINCIDENCIAS: { label: 'Sin coincidencias previas',  className: 'text-cyan-500 border-cyan-500/30 bg-cyan-500/5',     Icon: HelpCircle },
    APROBADA:          { label: 'MO aprobado',                className: 'text-emerald-400 border-emerald-500/40 bg-emerald-500/10', Icon: ShieldCheck },
    CORREGIDA:         { label: 'MO corregido',               className: 'text-sky-400 border-sky-500/40 bg-sky-500/10',      Icon: ShieldAlert },
    RECHAZADA:         { label: 'MO rechazado (manual)',      className: 'text-red-400 border-red-500/40 bg-red-500/10',      Icon: ShieldX },
}

/** Badge reutilizable que refleja tanto el estado de carga (HU2, async) como el estado final de la propuesta (HU3). */
export const EstadoAnalisisMO = ({ estadoCarga, estado }: EstadoAnalisisMOProps) => {
    if (estadoCarga === 'analizando') {
        return (
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border border-cyan-400/30 text-cyan-400 bg-cyan-400/5">
        <Loader2 size={11} className="animate-spin" />
        Analizando Modus Operandi…
      </span>
        )
    }

    if (estadoCarga === 'sin_analisis') {
        return (
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border border-gray-500/30 text-gray-400 bg-gray-500/5">
        <HelpCircle size={11} />
        Sin análisis de MO
      </span>
        )
    }

    if (estadoCarga === 'error') {
        return (
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border border-red-500/40 text-red-400 bg-red-500/10">
        <ShieldX size={11} />
        Error consultando MO
      </span>
        )
    }

    if (!estado) return null
    const { label, className, Icon } = ESTADO_META[estado]
    return (
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-sm text-[10px] border ${className}`}>
      <Icon size={11} />
            {label}
    </span>
    )
}