import { useMemo } from 'react'
import { Siren, Inbox } from 'lucide-react'
import { NeonPanel } from '../../ui/NeonPanel'
import { useNeonToast } from '../../ui/NeonToast'
import { useAuth } from '../../../context/AuthContext'
import { useAlertasPatron } from '../../../hooks/useAlertasPatron'
import { AlertaPatronCard } from './AlertaPatronCard'
import type { AlertaPatron } from '../../../types/api.types'

/**
 * Alerta interna de patrón de MO.
 *
 * Muestra dos vistas en la misma pantalla: la bandeja personal del
 * Investigador (alertas de expedientes que registró, visible solo para
 * ANALISTA) y el panel general del Guardia con todas las alertas del sistema
 * (visible para OFICIAL y ANALISTA — ver RoleGuard en App.tsx).
 */
export const AlertasPatronPanel = () => {
    const { userId, isAnalista } = useAuth()
    const { todas, misAlertas, loading, error, revisar, descartar } = useAlertasPatron(isAnalista ? userId : null)
    const { showToast, ToastContainer } = useNeonToast()

    const pendientesCount = useMemo(() => todas.filter(a => a.estado === 'PENDIENTE').length, [todas])

    const handleRevisar = async (alertaId: number) => {
        if (!userId) return
        try {
            await revisar(alertaId, { usuarioId: userId })
            showToast('Alerta marcada como revisada.', 'success')
        } catch (err) {
            console.error('[AlertasPatronPanel] Error al revisar la alerta', err)
            showToast('No se pudo marcar la alerta como revisada.', 'error')
        }
    }

    const handleDescartar = async (alertaId: number, motivo: string) => {
        if (!userId) return
        try {
            await descartar(alertaId, { usuarioId: userId, motivo: motivo.trim() || undefined })
            showToast('Alerta descartada.', 'success')
        } catch (err) {
            console.error('[AlertasPatronPanel] Error al descartar la alerta', err)
            showToast('No se pudo descartar la alerta.', 'error')
        }
    }

    const renderLista = (alertas: AlertaPatron[], vacio: string) => {
        if (loading && alertas.length === 0) {
            return <p className="text-sm text-cyan-500/70">Cargando alertas…</p>
        }
        if (alertas.length === 0) {
            return <p className="text-sm text-cyan-500/70">{vacio}</p>
        }
        return (
            <div className="flex flex-col gap-4">
                {alertas.map(alerta => (
                    <AlertaPatronCard
                        key={alerta.id}
                        alerta={alerta}
                        onRevisar={handleRevisar}
                        onDescartar={handleDescartar}
                    />
                ))}
            </div>
        )
    }

    return (
        <div className="flex flex-col gap-6 pb-6">
            <ToastContainer />
            {error && <p className="text-sm text-red-400">{error}</p>}

            {isAnalista && (
                <NeonPanel
                    title="Mis notificaciones"
                    subtitle="Bandeja del Investigador — alertas de patrones de MO en expedientes que registraste"
                >
                    <div className="flex items-center gap-2 mb-4 text-cyan-500">
                        <Inbox size={14} />
                        <span className="text-[11px] uppercase tracking-wider">
                            {misAlertas.length} alerta(s) en tu bandeja
                        </span>
                    </div>
                    {renderLista(misAlertas, 'No tienes alertas asignadas por ahora.')}
                </NeonPanel>
            )}

            <NeonPanel
                title="Panel general de alertas"
                subtitle="Panel del Guardia — todas las alertas de patrón de MO generadas por el sistema"
            >
                <div className="flex items-center gap-2 mb-4 text-amber-400">
                    <Siren size={14} />
                    <span className="text-[11px] uppercase tracking-wider">
                        {pendientesCount} alerta(s) pendiente(s) de revisar
                    </span>
                </div>
                {renderLista(todas, 'No hay alertas de patrón registradas todavía.')}
            </NeonPanel>
        </div>
    )
}