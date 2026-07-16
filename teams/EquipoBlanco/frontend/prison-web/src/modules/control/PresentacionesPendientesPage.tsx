import { useState, useEffect } from 'react'
import { CalendarClock, CheckCircle, XCircle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'

interface PendienteDto {
    id: string
    expedienteId: string
    fechaProgramada: string
}

export default function PresentacionesPendientesPage() {
    const { username, hasRole } = useAuth()
    const [pendientes, setPendientes] = useState<PendienteDto[]>([])
    const [loading, setLoading] = useState(true)
    const [actionModal, setActionModal] = useState<{ type: 'cumplida' | 'incumplida', p: PendienteDto } | null>(null)
    const [observaciones, setObservaciones] = useState('')

    const loadData = async () => {
        try {
            setLoading(true)
            const res = await api.get('/calendario/pendientes/hoy', {
                params: { oficialCedula: hasRole('Oficial de Seguimiento') ? username : '' }
            })
            setPendientes(res.data || [])
        } catch (err) {
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        loadData()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [username])

    const handleAction = async () => {
        if (!actionModal) return
        try {
            if (actionModal.type === 'cumplida') {
                await api.put(`/calendario/${actionModal.p.id}/registrar`, {
                    oficialQueRegistro: username,
                    observaciones
                })
            } else {
                await api.post(`/calendario/${actionModal.p.id}/incumplimiento`, {
                    oficialQueRegistro: username,
                    observaciones
                })
            }
            setActionModal(null)
            setObservaciones('')
            loadData()
        } catch (err) {
            console.error(err)
            alert('Error al registrar')
        }
    }

    return (
        <SidebarLayout>
            <div className="max-w-5xl mx-auto space-y-6">
                <div>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <CalendarClock className="w-8 h-8 text-blue-600" />
                        Presentaciones de Hoy
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Lista de egresados que deben presentarse en el recinto el día de hoy.
                    </p>
                </div>

                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200 text-sm text-left">
                            <thead className="bg-gray-50 text-gray-500 font-medium">
                                <tr>
                                    <th className="px-6 py-4">ID Expediente</th>
                                    <th className="px-6 py-4">Fecha Programada</th>
                                    <th className="px-6 py-4">Estado</th>
                                    <th className="px-6 py-4">Acciones</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                                {loading ? (
                                    <tr><td colSpan={4} className="px-6 py-4 text-center">Cargando...</td></tr>
                                ) : pendientes.length === 0 ? (
                                    <tr><td colSpan={4} className="px-6 py-4 text-center text-gray-500">No hay presentaciones pendientes para hoy.</td></tr>
                                ) : (
                                    pendientes.map((p) => (
                                        <tr key={p.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 font-medium text-gray-900 font-mono text-xs">{p.expedienteId}</td>
                                            <td className="px-6 py-4">{p.fechaProgramada}</td>
                                            <td className="px-6 py-4">
                                                <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-semibold bg-yellow-50 text-yellow-700">
                                                    PENDIENTE
                                                </span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-2">
                                                    <button 
                                                        onClick={() => setActionModal({ type: 'cumplida', p })}
                                                        className="text-emerald-600 hover:bg-emerald-50 p-2 rounded-lg transition-colors flex items-center gap-1"
                                                        title="Registrar Cumplimiento"
                                                    >
                                                        <CheckCircle className="w-5 h-5" />
                                                    </button>
                                                    <button 
                                                        onClick={() => setActionModal({ type: 'incumplida', p })}
                                                        className="text-red-600 hover:bg-red-50 p-2 rounded-lg transition-colors flex items-center gap-1"
                                                        title="Registrar Incumplimiento"
                                                    >
                                                        <XCircle className="w-5 h-5" />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            {/* Modal de Acción */}
            {actionModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/50 backdrop-blur-sm">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
                        <div className={`p-5 ${actionModal.type === 'cumplida' ? 'bg-emerald-50 text-emerald-900' : 'bg-red-50 text-red-900'} flex justify-between items-center`}>
                            <h3 className="font-bold text-lg flex items-center gap-2">
                                {actionModal.type === 'cumplida' ? <CheckCircle className="w-6 h-6 text-emerald-600"/> : <XCircle className="w-6 h-6 text-red-600"/>}
                                Registrar {actionModal.type === 'cumplida' ? 'Cumplimiento' : 'Incumplimiento'}
                            </h3>
                            <button onClick={() => { setActionModal(null); setObservaciones('') }} className="text-gray-500 hover:text-gray-900">✕</button>
                        </div>
                        <div className="p-6 space-y-4">
                            {actionModal.type === 'incumplida' && (
                                <div className="p-3 bg-orange-50 border border-orange-200 rounded-lg text-sm text-orange-800">
                                    <strong>¡Atención!</strong> Registrar un incumplimiento sumará al contador de faltas del egresado y podría generar alertas automáticas al Supervisor Policial.
                                </div>
                            )}
                            <div>
                                <label className="block text-sm font-semibold text-gray-900 mb-1">Observaciones</label>
                                <textarea
                                    value={observaciones}
                                    onChange={e => setObservaciones(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
                                    rows={4}
                                    placeholder={actionModal.type === 'cumplida' ? "Detalles de la presentación..." : "Motivo del incumplimiento (si se conoce)..."}
                                />
                            </div>
                            <div className="flex justify-end gap-3 pt-4">
                                <button onClick={() => { setActionModal(null); setObservaciones('') }} className="px-4 py-2 text-sm font-semibold text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50">
                                    Cancelar
                                </button>
                                <button 
                                    onClick={handleAction} 
                                    className={`px-4 py-2 text-sm font-bold text-white rounded-xl shadow-sm ${actionModal.type === 'cumplida' ? 'bg-emerald-600 hover:bg-emerald-700' : 'bg-red-600 hover:bg-red-700'}`}
                                >
                                    Confirmar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </SidebarLayout>
    )
}
