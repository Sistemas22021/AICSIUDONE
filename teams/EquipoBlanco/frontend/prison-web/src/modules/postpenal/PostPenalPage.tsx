import { useState, useEffect, useRef } from 'react'
import { FileText, Users, UserCheck, Clock, X, AlertTriangle, CheckCircle, RefreshCw, History } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface ExpedienteData {
    id: string
    idRecluso: string
    reclusoNombre: string
    reclusoCedula: string
    fechaEgreso: string
    oficialAsignadoNombre: string | null
    oficialAsignadoCedula: string | null
    estado: string
    historialAsignaciones: string[]
}

interface OficialCarga {
    nombre: string
    cedula: string
    casosActivos: number
}

export default function PostPenalPage() {
    const [expedientes, setExpedientes] = useState<ExpedienteData[]>([])
    const [oficiales, setOficiales] = useState<OficialCarga[]>([])
    const [selectedExpediente, setSelectedExpediente] = useState<ExpedienteData | null>(null)
    const [motivoCambio, setMotivoCambio] = useState('')
    const [loading, setLoading] = useState(true)
    const [assigning, setAssigning] = useState(false)
    const [error, setError] = useState('')
    const [showHistorial, setShowHistorial] = useState<ExpedienteData | null>(null)
    const [filter, setFilter] = useState<'all' | 'pendiente' | 'asignado'>('all')

    const loadData = async () => {
        try {
            const [expRes, ofRes] = await Promise.all([
                api.get<ExpedienteData[]>('/post-penal/expedientes'),
                api.get<OficialCarga[]>('/post-penal/oficiales/carga')
            ])
            setExpedientes(expRes.data || [])
            setOficiales(ofRes.data || [])
        } catch (err) {
            console.error('Error cargando datos post-penal', err)
        } finally {
            setLoading(false)
        }
    }

    const initialLoadDone = useRef(false)

    useEffect(() => {
        if (!initialLoadDone.current) {
            initialLoadDone.current = true
            loadData()
        }
    }, [])

    const filteredExpedientes = expedientes.filter(e => {
        if (filter === 'pendiente') return e.estado === 'pendiente'
        if (filter === 'asignado') return e.estado === 'asignado'
        return true
    })

    const pendingCount = expedientes.filter(e => e.estado === 'pendiente').length
    const assignedCount = expedientes.filter(e => e.estado === 'asignado').length

    const handleAssign = async (oficial: OficialCarga) => {
        if (!selectedExpediente) return

        const isReassignment = selectedExpediente.oficialAsignadoCedula !== null
        if (isReassignment && !motivoCambio.trim()) {
            setError('El motivo de reasignación es obligatorio.')
            return
        }

        const confirmMsg = isReassignment
            ? `¿Reasignar el expediente de ${selectedExpediente.reclusoNombre} de ${selectedExpediente.oficialAsignadoNombre} a ${oficial.nombre}?`
            : `¿Asignar el expediente de ${selectedExpediente.reclusoNombre} a ${oficial.nombre}?`

        if (!confirm(confirmMsg)) return

        setAssigning(true)
        setError('')
        try {
            await api.post(`/post-penal/expedientes/${selectedExpediente.id}/assign`, {
                oficialNombre: oficial.nombre,
                oficialCedula: oficial.cedula,
                motivoCambio: isReassignment ? motivoCambio.trim() : null
            })
            setSelectedExpediente(null)
            setMotivoCambio('')
            await loadData()
        } catch (err) {
            console.error('Error asignando oficial', err)
        } finally {
            setAssigning(false)
        }
    }

    const daysSince = (dateStr: string) => {
        if (!dateStr) return 0
        const diff = new Date().getTime() - new Date(dateStr).getTime()
        return Math.floor(diff / (1000 * 60 * 60 * 24))
    }

    const getCargaColor = (carga: number) => {
        if (carga === 0) return 'bg-emerald-100 text-emerald-700 border-emerald-200'
        if (carga <= 3) return 'bg-blue-100 text-blue-700 border-blue-200'
        if (carga <= 6) return 'bg-amber-100 text-amber-700 border-amber-200'
        return 'bg-red-100 text-red-700 border-red-200'
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center min-h-[60vh]">
                    <div className="flex flex-col items-center gap-3">
                        <div className="w-10 h-10 border-4 border-blue-100 border-t-blue-600 rounded-full animate-spin"></div>
                        <p className="text-sm text-gray-500 font-medium">Cargando módulo Post-Penitenciario...</p>
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            <div className="max-w-7xl mx-auto space-y-6">

                {/* Header */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                            <FileText className="w-8 h-8 text-indigo-600" />
                            Módulo Post-Penitenciario
                        </h1>
                        <p className="text-sm text-gray-500 mt-1">
                            Gestión de asignación de oficiales de seguimiento a expedientes de egresados.
                        </p>
                    </div>
                    <button
                        onClick={() => { setLoading(true); loadData() }}
                        className="flex items-center gap-2 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 text-sm font-medium transition-colors"
                    >
                        <RefreshCw className="w-4 h-4" />
                        Actualizar
                    </button>
                </div>

                {/* Stats */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-sm flex items-center justify-between">
                        <div>
                            <p className="text-xs font-bold text-gray-500 uppercase tracking-wider">Total Expedientes</p>
                            <p className="text-3xl font-black text-gray-900">{expedientes.length}</p>
                        </div>
                        <div className="p-3 rounded-xl bg-indigo-50 text-indigo-500">
                            <FileText className="w-6 h-6" />
                        </div>
                    </div>
                    <div className={`rounded-xl border p-5 shadow-sm flex items-center justify-between ${pendingCount > 0 ? 'bg-amber-50 border-amber-200' : 'bg-white border-gray-200'}`}>
                        <div>
                            <p className="text-xs font-bold text-gray-500 uppercase tracking-wider">Sin Oficial</p>
                            <p className={`text-3xl font-black ${pendingCount > 0 ? 'text-amber-600' : 'text-gray-900'}`}>{pendingCount}</p>
                            <p className="text-[11px] font-bold text-amber-600 opacity-80">{pendingCount > 0 ? 'Requieren asignación' : 'Todo asignado'}</p>
                        </div>
                        <div className={`p-3 rounded-xl ${pendingCount > 0 ? 'bg-amber-100 text-amber-600 animate-pulse' : 'bg-gray-50 text-gray-400'}`}>
                            <AlertTriangle className="w-6 h-6" />
                        </div>
                    </div>
                    <div className="bg-white rounded-xl border border-gray-200 p-5 shadow-sm flex items-center justify-between">
                        <div>
                            <p className="text-xs font-bold text-gray-500 uppercase tracking-wider">Con Oficial</p>
                            <p className="text-3xl font-black text-emerald-600">{assignedCount}</p>
                            <p className="text-[11px] font-bold text-emerald-600 opacity-80">En seguimiento activo</p>
                        </div>
                        <div className="p-3 rounded-xl bg-emerald-50 text-emerald-500">
                            <UserCheck className="w-6 h-6" />
                        </div>
                    </div>
                </div>

                {/* Filter tabs */}
                <div className="flex gap-2">
                    {[
                        { key: 'all' as const, label: 'Todos', count: expedientes.length },
                        { key: 'pendiente' as const, label: 'Sin Oficial', count: pendingCount },
                        { key: 'asignado' as const, label: 'Asignados', count: assignedCount }
                    ].map(tab => (
                        <button
                            key={tab.key}
                            onClick={() => setFilter(tab.key)}
                            className={`px-4 py-2 rounded-lg text-xs font-bold uppercase tracking-wider transition-colors ${
                                filter === tab.key
                                    ? 'bg-indigo-600 text-white shadow-sm'
                                    : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                            }`}
                        >
                            {tab.label} ({tab.count})
                        </button>
                    ))}
                </div>

                {/* Expedientes table */}
                <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                    <div className="px-5 py-3.5 border-b border-gray-100 bg-gray-50">
                        <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2">
                            <Clock className="w-4 h-4 text-gray-400" />
                            Expedientes de Seguimiento
                        </h3>
                    </div>
                    {filteredExpedientes.length === 0 ? (
                        <p className="text-xs text-gray-400 italic p-6 text-center">No hay expedientes en esta categoría.</p>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-xs border-collapse">
                                <thead>
                                    <tr className="border-b border-gray-150 bg-gray-50/50 text-gray-400 font-bold uppercase tracking-wider">
                                        <th className="px-6 py-3">Egresado</th>
                                        <th className="px-6 py-3">Fecha Egreso</th>
                                        <th className="px-6 py-3">Días</th>
                                        <th className="px-6 py-3">Estado</th>
                                        <th className="px-6 py-3">Oficial Asignado</th>
                                        <th className="px-6 py-3 text-right">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {filteredExpedientes.map(exp => (
                                        <tr key={exp.id} className="hover:bg-gray-50/30 transition-colors">
                                            <td className="px-6 py-4">
                                                <p className="font-bold text-gray-800">{exp.reclusoNombre}</p>
                                                <span className="text-[10px] text-gray-400">C.I. {exp.reclusoCedula}</span>
                                            </td>
                                            <td className="px-6 py-4 font-mono text-gray-600">{exp.fechaEgreso || '—'}</td>
                                            <td className="px-6 py-4">
                                                <span className={`inline-block px-2 py-0.5 rounded-full text-[10px] font-bold ${
                                                    daysSince(exp.fechaEgreso) > 7 ? 'bg-red-100 text-red-700' :
                                                    daysSince(exp.fechaEgreso) > 3 ? 'bg-amber-100 text-amber-700' :
                                                    'bg-gray-100 text-gray-600'
                                                }`}>
                                                    {daysSince(exp.fechaEgreso)}d
                                                </span>
                                            </td>
                                            <td className="px-6 py-4">
                                                {exp.estado === 'pendiente' ? (
                                                    <span className="inline-flex items-center gap-1 px-2 py-1 rounded-full bg-amber-100 text-amber-700 text-[10px] font-bold border border-amber-200">
                                                        <AlertTriangle className="w-3 h-3" /> Sin oficial
                                                    </span>
                                                ) : (
                                                    <span className="inline-flex items-center gap-1 px-2 py-1 rounded-full bg-emerald-100 text-emerald-700 text-[10px] font-bold border border-emerald-200">
                                                        <CheckCircle className="w-3 h-3" /> Asignado
                                                    </span>
                                                )}
                                            </td>
                                            <td className="px-6 py-4 font-medium text-gray-600">
                                                {exp.oficialAsignadoNombre || <span className="text-gray-300 italic">—</span>}
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <div className="flex justify-end gap-2">
                                                    {exp.historialAsignaciones && exp.historialAsignaciones.length > 0 && (
                                                        <button
                                                            onClick={() => setShowHistorial(exp)}
                                                            className="px-2.5 py-1.5 border border-gray-200 text-gray-500 hover:bg-gray-50 rounded-lg text-[11px] font-bold transition-colors"
                                                            title="Ver historial"
                                                        >
                                                            <History className="w-3.5 h-3.5" />
                                                        </button>
                                                    )}
                                                    <button
                                                        onClick={() => { setSelectedExpediente(exp); setMotivoCambio(''); setError('') }}
                                                        className="px-3 py-1.5 bg-indigo-600 text-white hover:bg-indigo-700 rounded-lg text-[11px] font-bold uppercase transition-all shadow-sm cursor-pointer"
                                                    >
                                                        {exp.estado === 'pendiente' ? 'Asignar' : 'Reasignar'}
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* Modal: Asignar Oficial */}
            {selectedExpediente && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[70] p-4">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden border border-gray-200">
                        <div className="flex items-center justify-between px-5 py-3.5 bg-indigo-600 text-white">
                            <h3 className="text-sm font-bold uppercase tracking-wider">
                                {selectedExpediente.oficialAsignadoCedula ? 'Reasignar' : 'Asignar'} Oficial de Seguimiento
                            </h3>
                            <button onClick={() => setSelectedExpediente(null)} className="p-1 hover:bg-indigo-700 rounded-lg text-white">
                                <X className="w-5 h-5" />
                            </button>
                        </div>

                        <div className="p-5 space-y-4">
                            {/* Expediente info */}
                            <div className="bg-indigo-50 border border-indigo-100 rounded-lg p-3">
                                <p className="text-xs text-indigo-500 font-bold uppercase tracking-wider mb-1">Expediente de:</p>
                                <p className="font-bold text-gray-900">{selectedExpediente.reclusoNombre}</p>
                                <p className="text-xs text-gray-500">C.I. {selectedExpediente.reclusoCedula} | Egreso: {selectedExpediente.fechaEgreso}</p>
                                {selectedExpediente.oficialAsignadoNombre && (
                                    <p className="text-xs text-amber-600 mt-1 font-semibold">
                                        Oficial actual: {selectedExpediente.oficialAsignadoNombre} ({selectedExpediente.oficialAsignadoCedula})
                                    </p>
                                )}
                            </div>

                            {error && (
                                <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-800 text-xs font-semibold">
                                    {error}
                                </div>
                            )}

                            {/* Motivo de cambio (solo reasignaciones) */}
                            {selectedExpediente.oficialAsignadoCedula && (
                                <div>
                                    <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                        Motivo de Reasignación *
                                    </label>
                                    <textarea
                                        rows={2}
                                        required
                                        placeholder="Describa el motivo del cambio de oficial..."
                                        value={motivoCambio}
                                        onChange={e => setMotivoCambio(e.target.value)}
                                        className="w-full p-3 border border-gray-300 rounded-lg text-xs focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 focus:outline-none"
                                    />
                                </div>
                            )}

                            {/* Lista de oficiales */}
                            <div>
                                <p className="text-xs font-bold text-gray-700 uppercase tracking-wide mb-2">
                                    <Users className="w-3.5 h-3.5 inline mr-1" />
                                    Oficiales Disponibles
                                </p>
                                <div className="space-y-2 max-h-60 overflow-y-auto">
                                    {oficiales.map(oficial => {
                                        const isCurrentOfficer = oficial.cedula === selectedExpediente.oficialAsignadoCedula
                                        return (
                                            <div
                                                key={oficial.cedula}
                                                className={`flex items-center justify-between p-3 border rounded-lg transition-colors ${
                                                    isCurrentOfficer
                                                        ? 'border-gray-200 bg-gray-50 opacity-60'
                                                        : 'border-gray-100 hover:border-indigo-300 hover:bg-indigo-50/50 cursor-pointer'
                                                }`}
                                            >
                                                <div>
                                                    <p className="font-bold text-gray-900 text-sm">{oficial.nombre}</p>
                                                    <p className="text-[10px] text-gray-400">{oficial.cedula}</p>
                                                </div>
                                                <div className="flex items-center gap-2">
                                                    <span className={`inline-block px-2.5 py-1 rounded-full text-[10px] font-bold border ${getCargaColor(oficial.casosActivos)}`}>
                                                        {oficial.casosActivos} {oficial.casosActivos === 1 ? 'caso' : 'casos'}
                                                    </span>
                                                    {isCurrentOfficer ? (
                                                        <span className="text-[10px] text-gray-400 font-bold">Actual</span>
                                                    ) : (
                                                        <button
                                                            onClick={() => handleAssign(oficial)}
                                                            disabled={assigning}
                                                            className="px-3 py-1.5 bg-indigo-600 text-white hover:bg-indigo-700 rounded-lg text-[10px] font-bold uppercase transition-all disabled:opacity-50 shadow-sm cursor-pointer"
                                                        >
                                                            {assigning ? '...' : 'Asignar'}
                                                        </button>
                                                    )}
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal: Historial de asignaciones */}
            {showHistorial && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[70] p-4">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden border border-gray-200">
                        <div className="flex items-center justify-between px-5 py-3.5 bg-gray-800 text-white">
                            <h3 className="text-sm font-bold uppercase tracking-wider">Historial de Asignaciones</h3>
                            <button onClick={() => setShowHistorial(null)} className="p-1 hover:bg-gray-700 rounded-lg text-white">
                                <X className="w-5 h-5" />
                            </button>
                        </div>
                        <div className="p-5 space-y-3">
                            <p className="text-xs text-gray-500 font-semibold">
                                Expediente de: <span className="text-gray-900">{showHistorial.reclusoNombre}</span>
                            </p>
                            <div className="space-y-2 max-h-60 overflow-y-auto">
                                {showHistorial.historialAsignaciones.map((reg, i) => (
                                    <div key={i} className="p-3 bg-gray-50 border border-gray-100 rounded-lg text-xs text-gray-700">
                                        {reg}
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </SidebarLayout>
    )
}
