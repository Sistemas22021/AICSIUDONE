import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ShieldAlert, Eye, AlertTriangle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface IncidentData {
    id: string
    code: string
    cellIdentifier: string
    cellId: string
    description: string
    incidentDate: string
    reporter: string
    status: string
}

export default function IncidentListPage() {
    const navigate = useNavigate()
    const [incidents, setIncidents] = useState<IncidentData[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const loadIncidents = async () => {
        setLoading(true)
        setError('')
        try {
            const res = await api.get<IncidentData[]>('/incidents')
            setIncidents(res.data || [])
        } catch (err) {
            console.error(err)
            setError('Error al obtener la bitácora de incidentes.')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        loadIncidents()
    }, [])

    const formatDateTime = (dateStr: string) => {
        if (!dateStr) return '-'
        try {
            const date = new Date(dateStr)
            return date.toLocaleString('es-ES', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            })
        } catch {
            return dateStr
        }
    }

    return (
        <SidebarLayout>
            <div className="max-w-6xl mx-auto space-y-6">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                            <ShieldAlert className="w-8 h-8 text-red-600" />
                            Bitácora de Incidentes Internos
                        </h1>
                        <p className="text-sm text-gray-500 mt-1">
                            Listado general y registro histórico de actas de siniestro y clausuras de celda.
                        </p>
                    </div>
                </div>

                {error && (
                    <div className="p-4 bg-red-50 text-red-700 rounded-xl border border-red-100 text-sm font-medium">
                        {error}
                    </div>
                )}

                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200 text-sm text-left">
                            <thead className="bg-gray-50 text-gray-500 font-bold uppercase tracking-wider text-xs border-b border-gray-200">
                                <tr>
                                    <th className="px-6 py-4">Código</th>
                                    <th className="px-6 py-4">Celda afectada</th>
                                    <th className="px-6 py-4">Fecha y Hora</th>
                                    <th className="px-6 py-4">Reportante</th>
                                    <th className="px-6 py-4">Estado</th>
                                    <th className="px-6 py-4">Acciones</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                                {loading ? (
                                    <tr>
                                        <td colSpan={6} className="px-6 py-8 text-center text-gray-500 font-medium">
                                            Cargando bitácora de incidentes...
                                        </td>
                                    </tr>
                                ) : incidents.length === 0 ? (
                                    <tr>
                                        <td colSpan={6} className="px-6 py-8 text-center text-gray-500 italic">
                                            No se han registrado incidentes de seguridad en el sistema.
                                        </td>
                                    </tr>
                                ) : (
                                    incidents.map((inc) => (
                                        <tr key={inc.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4 font-mono font-bold text-gray-900 text-xs">
                                                {inc.code}
                                            </td>
                                            <td className="px-6 py-4 font-medium text-gray-700">
                                                Celda {inc.cellIdentifier || 'No especificada'}
                                            </td>
                                            <td className="px-6 py-4 text-gray-600">
                                                {formatDateTime(inc.incidentDate)}
                                            </td>
                                            <td className="px-6 py-4 text-gray-600 font-semibold">
                                                {inc.reporter || 'N/A'}
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200 uppercase tracking-wider">
                                                    <AlertTriangle className="w-3 h-3" />
                                                    {inc.status === 'EN_INVESTIGACION' ? 'En Investigación' : inc.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <button
                                                    onClick={() => navigate(`/incidentes/detalle/${inc.id}`)}
                                                    className="px-3.5 py-1.5 bg-gray-900 hover:bg-gray-800 text-white rounded-lg text-xs font-bold transition-colors flex items-center gap-1.5"
                                                >
                                                    <Eye className="w-3.5 h-3.5" /> Ver Detalle
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </SidebarLayout>
    )
}
