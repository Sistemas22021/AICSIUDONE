import { useState, useEffect } from 'react'
import { ShieldAlert, CalendarClock, AlertTriangle, UserCheck, CheckCircle, XCircle, Bell } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'

export default function ControlDashboardPage() {
    const { username, hasRole } = useAuth()
    
    const [stats, setStats] = useState({
        totalActivos: 0,
        pendientesHoy: 0,
        incumplimientos: 0,
        alertasN3: 0
    })
    
    const [loading, setLoading] = useState(true)
    const [pendientes, setPendientes] = useState<any[]>([])
    const [loadingPendientes, setLoadingPendientes] = useState(true)
    const [actionModal, setActionModal] = useState<{ type: 'cumplida' | 'incumplida', p: any } | null>(null)
    const [observaciones, setObservaciones] = useState('')

    // ── Alertas de Nivel 1 ───────────────────────────────────────────────────
    type AlertaN1 = {
        id: string
        nivel: number
        fechaEmision: string
        destinatario: string
        estado: string
        accionRequerida: string
        expedienteId: string | null
        nombreEgresado: string | null
        cedulaEgresado: string | null
        observacionAtencion: string | null
    }
    const [alertasN1, setAlertasN1] = useState<AlertaN1[]>([])
    const [atenderN1Modal, setAtenderN1Modal] = useState<AlertaN1 | null>(null)
    const [obsN1, setObsN1] = useState('')
    // ─────────────────────────────────────────────────────────────────────────

    const fetchAlertasN1 = async () => {
        try {
            const res = await api.get<AlertaN1[]>('/alertas/nivel1')
            setAlertasN1(res.data || [])
        } catch {
            // silencioso
        }
    }

    const fetchDashboard = async () => {
        try {
            const [expRes, pendRes] = await Promise.all([
                api.get('/post-penal/expedientes'),
                api.get('/calendario/pendientes/hoy', { params: { oficialCedula: hasRole('Oficial de Seguimiento') ? username : '' } })
            ])
            
            const expedientes = expRes.data || []
            const pendientesData = pendRes.data || []
            
            let activos = expedientes.length
            if (hasRole('Oficial de Seguimiento')) {
                activos = expedientes.filter((e: any) => e.oficialAsignadoNombre === username).length
            }
            
            const incumplimientos = expedientes.reduce((acc: number, e: any) => acc + (e.contadorIncumplimientos || 0), 0)
            const alertas = expedientes.filter((e: any) => e.estado === 'alerta_critica').length
            
            setStats({
                totalActivos: activos,
                pendientesHoy: pendientesData.length,
                incumplimientos: incumplimientos,
                alertasN3: alertas
            })
            setPendientes(pendientesData)
        } catch (err) {
            console.error("Error fetching control dashboard", err)
        } finally {
            setLoading(false)
            setLoadingPendientes(false)
        }
    }

    const handleAtenderN1 = async () => {
        if (!atenderN1Modal) return
        try {
            await api.put(`/alertas/${atenderN1Modal.id}/atender`, { observacion: obsN1 })
            setAtenderN1Modal(null)
            setObsN1('')
            fetchAlertasN1()
        } catch {
            alert('Error al atender la alerta')
        }
    }

    useEffect(() => {
        fetchDashboard()
        fetchAlertasN1()
        const interval = setInterval(() => { fetchDashboard(); fetchAlertasN1() }, 10000)
        return () => clearInterval(interval)
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
            fetchDashboard()
        } catch {
            alert('Error al registrar')
        }
    }

    return (
        <SidebarLayout>
            <div className="max-w-6xl mx-auto space-y-6">
                <div>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <ShieldAlert className="w-8 h-8 text-indigo-600" />
                        Control y Disciplina
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Monitor de presentaciones, incidencias y alertas escalonadas.
                    </p>
                </div>

                {loading ? (
                    <div className="text-center py-10">Cargando...</div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Egresados Activos</p>
                                    <h3 className="text-3xl font-black text-gray-900">{stats.totalActivos}</h3>
                                </div>
                                <div className="p-2 bg-indigo-50 rounded-xl">
                                    <UserCheck className="w-5 h-5 text-indigo-600" />
                                </div>
                            </div>
                        </div>
                        
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Presentaciones Hoy</p>
                                    <h3 className="text-3xl font-black text-blue-600">{stats.pendientesHoy}</h3>
                                </div>
                                <div className="p-2 bg-blue-50 rounded-xl">
                                    <CalendarClock className="w-5 h-5 text-blue-600" />
                                </div>
                            </div>
                        </div>

                        <div className="bg-white p-5 rounded-2xl border border-orange-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-orange-600 uppercase tracking-wider mb-1">Incumplimientos</p>
                                    <h3 className="text-3xl font-black text-orange-600">{stats.incumplimientos}</h3>
                                </div>
                                <div className="p-2 bg-orange-50 rounded-xl">
                                    <AlertTriangle className="w-5 h-5 text-orange-600" />
                                </div>
                            </div>
                        </div>

                        <div className="bg-white p-5 rounded-2xl border border-red-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-red-600 uppercase tracking-wider mb-1">Alertas N3 (Críticas)</p>
                                    <h3 className="text-3xl font-black text-red-600">{stats.alertasN3}</h3>
                                </div>
                                <div className="p-2 bg-red-50 rounded-xl">
                                    <ShieldAlert className="w-5 h-5 text-red-600" />
                                </div>
                            </div>
                        </div>

                        <div className="bg-white p-5 rounded-2xl border border-yellow-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-yellow-700 uppercase tracking-wider mb-1">Alertas N1 Activas</p>
                                    <h3 className="text-3xl font-black text-yellow-700">{alertasN1.filter(a => a.estado === 'activa').length}</h3>
                                </div>
                                <div className="p-2 bg-yellow-50 rounded-xl">
                                    <Bell className="w-5 h-5 text-yellow-600" />
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tabla de presentaciones pendientes */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <CalendarClock className="w-5 h-5 text-blue-600" />
                            Presentaciones Pendientes de Hoy
                        </h2>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200 text-sm text-left">
                            <thead className="bg-gray-50 text-gray-500 font-medium">
                                <tr>
                                    <th className="px-6 py-4">ID Expediente</th>
                                    <th className="px-6 py-4">Recluso</th>
                                    <th className="px-6 py-4">Cédula</th>
                                    <th className="px-6 py-4">Fecha Programada</th>
                                    <th className="px-6 py-4">Estado</th>
                                    <th className="px-6 py-4">Acciones</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                                {loadingPendientes ? (
                                    <tr><td colSpan={6} className="px-6 py-4 text-center">Cargando...</td></tr>
                                ) : pendientes.length === 0 ? (
                                    <tr><td colSpan={6} className="px-6 py-4 text-center text-gray-500">No hay presentaciones pendientes para hoy.</td></tr>
                                ) : (
                                    pendientes.map((p: any) => (
                                        <tr key={p.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 font-mono text-xs text-gray-500">{p.expedienteId}</td>
                                            <td className="px-6 py-4 font-medium text-gray-900">{p.reclusoNombre || '—'}</td>
                                            <td className="px-6 py-4 text-gray-600 font-mono text-xs">{p.reclusoCedula || '—'}</td>
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

            {/* ── Tabla de Alertas de Nivel 1 ── */}
            <div className="bg-white rounded-2xl shadow-sm border border-yellow-200 overflow-hidden">
                <div className="px-6 py-4 border-b border-yellow-100 flex items-center justify-between">
                    <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <Bell className="w-5 h-5 text-yellow-600" />
                        Alertas de Nivel 1
                    </h2>
                    <span className="text-xs text-gray-400">Primer incumplimiento detectado</span>
                </div>
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200 text-sm text-left">
                        <thead className="bg-yellow-50 text-gray-500 font-medium">
                            <tr>
                                <th className="px-6 py-4">Egresado</th>
                                <th className="px-6 py-4">Cédula</th>
                                <th className="px-6 py-4">Fecha Emisión</th>
                                <th className="px-6 py-4">Destinatario</th>
                                <th className="px-6 py-4">Estado</th>
                                <th className="px-6 py-4">Acciones</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {alertasN1.length === 0 ? (
                                <tr><td colSpan={6} className="px-6 py-4 text-center text-gray-400">No hay alertas de Nivel 1 registradas.</td></tr>
                            ) : (
                                alertasN1.map(a => (
                                    <tr key={a.id} className="hover:bg-yellow-50/50">
                                        <td className="px-6 py-4 font-medium text-gray-900">{a.nombreEgresado ?? '—'}</td>
                                        <td className="px-6 py-4 text-gray-600 font-mono text-xs">{a.cedulaEgresado ?? '—'}</td>
                                        <td className="px-6 py-4 text-gray-600">{new Date(a.fechaEmision).toLocaleString('es-VE')}</td>
                                        <td className="px-6 py-4 text-gray-700">{a.destinatario ?? '—'}</td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold ${
                                                a.estado === 'activa' ? 'bg-yellow-100 text-yellow-800' : 'bg-emerald-50 text-emerald-700'
                                            }`}>
                                                {a.estado === 'activa' ? 'ACTIVA' : 'ATENDIDA'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            {a.estado === 'activa' ? (
                                                <button
                                                    id={`btn-atender-n1-${a.id}`}
                                                    onClick={() => setAtenderN1Modal(a)}
                                                    className="text-emerald-600 hover:bg-emerald-50 px-3 py-1.5 rounded-lg transition-colors flex items-center gap-1.5 text-xs font-semibold border border-emerald-200"
                                                >
                                                    <CheckCircle className="w-4 h-4" /> Atender
                                                </button>
                                            ) : (
                                                <span className="text-xs text-gray-400 italic">{a.observacionAtencion ?? 'Sin observación'}</span>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
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
                                    <strong>¡Atención!</strong> Registrar un incumplimiento sumará al contador de faltas del egresado y podría generar alertas automáticas al Supervisor.
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

            {/* ── Modal: Atender Alerta N1 desde dashboard ── */}
            {atenderN1Modal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/50 backdrop-blur-sm">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
                        <div className="p-5 bg-emerald-50 text-emerald-900 flex justify-between items-center">
                            <h3 className="font-bold text-lg flex items-center gap-2">
                                <CheckCircle className="w-6 h-6 text-emerald-600" />
                                Atender Alerta — {atenderN1Modal.nombreEgresado}
                            </h3>
                            <button onClick={() => { setAtenderN1Modal(null); setObsN1('') }} className="text-gray-500 hover:text-gray-900">✕</button>
                        </div>
                        <div className="p-6 space-y-4">
                            <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-800 space-y-1">
                                <p><strong>Egresado:</strong> {atenderN1Modal.nombreEgresado ?? '—'}</p>
                                <p><strong>Cédula:</strong> {atenderN1Modal.cedulaEgresado ?? '—'}</p>
                                <p><strong>Fecha emisión:</strong> {new Date(atenderN1Modal.fechaEmision).toLocaleString('es-VE')}</p>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-900 mb-1">
                                    Observación <span className="font-normal text-gray-400">(opcional)</span>
                                </label>
                                <textarea
                                    id="textarea-obs-n1"
                                    value={obsN1}
                                    onChange={e => setObsN1(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                                    rows={3}
                                    placeholder="Acción tomada por el oficial..."
                                />
                            </div>
                            <div className="flex justify-end gap-3 pt-2">
                                <button
                                    onClick={() => { setAtenderN1Modal(null); setObsN1('') }}
                                    className="px-4 py-2 text-sm font-semibold text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50"
                                >
                                    Cancelar
                                </button>
                                <button
                                    id="btn-confirmar-n1"
                                    onClick={handleAtenderN1}
                                    className="px-4 py-2 text-sm font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-sm"
                                >
                                    Confirmar atención
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </SidebarLayout>
    )
}
