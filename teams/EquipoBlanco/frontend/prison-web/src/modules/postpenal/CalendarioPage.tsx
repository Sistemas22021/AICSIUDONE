import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Calendar as CalendarIcon, ArrowLeft, Clock, CalendarDays, CheckCircle, AlertTriangle, FileText, AlertCircle, Edit2 } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'

interface PresentacionData {
    id: string
    expedienteId: string
    fechaProgramada: string
    estado: string
    frecuencia: string
    fechaInicio: string
    oficialQueRegistro: string
    observaciones?: string
}

export default function CalendarioPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const { username } = useAuth()
    
    const [presentaciones, setPresentaciones] = useState<PresentacionData[]>([])
    const [loading, setLoading] = useState(true)
    const [generating, setGenerating] = useState(false)
    const [error, setError] = useState('')
    const [successMsg, setSuccessMsg] = useState('')
    
    // Form for generation
    const [frecuencia, setFrecuencia] = useState('MENSUAL')
    const [fechaInicio, setFechaInicio] = useState(new Date().toISOString().split('T')[0])
    
    // Form for updating single date
    const [editingId, setEditingId] = useState<string | null>(null)
    const [nuevaFecha, setNuevaFecha] = useState('')
    const [observacionesEdit, setObservacionesEdit] = useState('')
    const [updating, setUpdating] = useState(false)

    useEffect(() => {
        loadCalendario()
    }, [id])

    const loadCalendario = async () => {
        setLoading(true)
        try {
            const res = await api.get<PresentacionData[]>(`/calendario/${id}`)
            setPresentaciones(res.data || [])
        } catch (err) {
            console.error('Error cargando calendario', err)
        } finally {
            setLoading(false)
        }
    }

    const handleGenerar = async (e: React.FormEvent) => {
        e.preventDefault()
        setGenerating(true)
        setError('')
        setSuccessMsg('')
        try {
            await api.post(`/calendario/${id}`, {
                frecuencia,
                fechaInicio,
                oficialQueRegistro: username || 'Sistema'
            })
            setSuccessMsg('Calendario generado exitosamente')
            await loadCalendario()
        } catch (err) {
            console.error('Error generando calendario', err)
            setError('Error al generar el calendario. Verifique los datos.')
        } finally {
            setGenerating(false)
        }
    }
    
    const startEditing = (p: PresentacionData) => {
        setEditingId(p.id)
        setNuevaFecha(p.fechaProgramada)
        setObservacionesEdit('')
    }
    
    const handleUpdateDate = async () => {
        if (!editingId) return
        setUpdating(true)
        try {
            await api.put(`/calendario/${editingId}`, {
                nuevaFecha,
                oficialQueRegistro: username || 'Sistema',
                observaciones: observacionesEdit
            })
            setEditingId(null)
            await loadCalendario()
        } catch (err) {
            console.error('Error actualizando fecha', err)
            alert('Error al actualizar la fecha')
        } finally {
            setUpdating(false)
        }
    }

    const getEstadoStyle = (estado: string) => {
        switch (estado) {
            case 'PENDIENTE': return 'bg-blue-100 text-blue-700 border-blue-200'
            case 'CUMPLIDA': return 'bg-emerald-100 text-emerald-700 border-emerald-200'
            case 'INCUMPLIDA': return 'bg-red-100 text-red-700 border-red-200'
            default: return 'bg-gray-100 text-gray-700 border-gray-200'
        }
    }
    
    const getEstadoIcon = (estado: string) => {
        switch (estado) {
            case 'PENDIENTE': return <Clock className="w-3.5 h-3.5" />
            case 'CUMPLIDA': return <CheckCircle className="w-3.5 h-3.5" />
            case 'INCUMPLIDA': return <AlertTriangle className="w-3.5 h-3.5" />
            default: return <FileText className="w-3.5 h-3.5" />
        }
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center min-h-[60vh]">
                    <div className="flex flex-col items-center gap-3">
                        <div className="w-10 h-10 border-4 border-blue-100 border-t-blue-600 rounded-full animate-spin"></div>
                        <p className="text-sm text-gray-500 font-medium">Cargando calendario...</p>
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            <div className="max-w-5xl mx-auto space-y-6">
                <div className="flex items-center gap-4">
                    <button 
                        onClick={() => navigate('/post')}
                        className="p-2 bg-white border border-gray-200 rounded-xl text-gray-500 hover:bg-gray-50 hover:text-gray-700 transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5" />
                    </button>
                    <div>
                        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                            <CalendarIcon className="w-8 h-8 text-indigo-600" />
                            Calendario de Presentaciones
                        </h1>
                        <p className="text-sm text-gray-500 mt-1">
                            Programe y gestione las presentaciones periódicas del egresado.
                        </p>
                    </div>
                </div>

                {presentaciones.length === 0 ? (
                    <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm max-w-lg">
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2 mb-4">
                            <CalendarDays className="w-5 h-5 text-indigo-500" />
                            Generar Calendario
                        </h3>
                        <p className="text-sm text-gray-600 mb-6">
                            No hay presentaciones programadas para este expediente. Seleccione una frecuencia y la fecha de inicio para generar automáticamente las fechas.
                        </p>
                        
                        {error && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-800 text-xs font-semibold flex items-center gap-2">
                                <AlertCircle className="w-4 h-4" />
                                {error}
                            </div>
                        )}
                        
                        <form onSubmit={handleGenerar} className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                    Frecuencia *
                                </label>
                                <select
                                    value={frecuencia}
                                    onChange={e => setFrecuencia(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none transition-all bg-white"
                                >
                                    <option value="SEMANAL">Semanal (Cada 7 días)</option>
                                    <option value="QUINCENAL">Quincenal (Cada 14 días)</option>
                                    <option value="MENSUAL">Mensual (Cada 1 mes)</option>
                                </select>
                            </div>
                            
                            <div>
                                <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                    Fecha de Inicio *
                                </label>
                                <input
                                    type="date"
                                    required
                                    value={fechaInicio}
                                    onChange={e => setFechaInicio(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                                />
                            </div>
                            
                            <div className="pt-2">
                                <button
                                    type="submit"
                                    disabled={generating}
                                    className="w-full flex justify-center items-center gap-2 px-6 py-2.5 bg-indigo-600 text-white hover:bg-indigo-700 rounded-xl font-bold uppercase tracking-wider text-sm transition-all disabled:opacity-50"
                                >
                                    <CalendarDays className="w-4 h-4" />
                                    {generating ? 'Generando...' : 'Generar Fechas'}
                                </button>
                            </div>
                        </form>
                    </div>
                ) : (
                    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
                        <div className="px-5 py-4 border-b border-gray-100 bg-gray-50 flex items-center justify-between">
                            <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2">
                                <CalendarDays className="w-4 h-4 text-indigo-500" />
                                Fechas Programadas
                            </h3>
                            {successMsg && (
                                <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-3 py-1 rounded-full border border-emerald-200">
                                    {successMsg}
                                </span>
                            )}
                        </div>
                        
                        <div className="p-0 overflow-x-auto">
                            <table className="w-full text-left text-sm border-collapse">
                                <thead>
                                    <tr className="border-b border-gray-150 bg-gray-50/50 text-gray-400 font-bold uppercase tracking-wider text-xs">
                                        <th className="px-6 py-3">#</th>
                                        <th className="px-6 py-3">Fecha Programada</th>
                                        <th className="px-6 py-3">Estado</th>
                                        <th className="px-6 py-3">Observaciones</th>
                                        <th className="px-6 py-3 text-right">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {presentaciones.map((p, idx) => (
                                        <tr key={p.id} className="hover:bg-gray-50/30 transition-colors">
                                            <td className="px-6 py-4 font-bold text-gray-400">{idx + 1}</td>
                                            <td className="px-6 py-4 font-mono font-medium text-gray-700">
                                                {editingId === p.id ? (
                                                    <input 
                                                        type="date"
                                                        value={nuevaFecha}
                                                        onChange={e => setNuevaFecha(e.target.value)}
                                                        className="p-1 border border-indigo-300 rounded focus:ring-1 focus:ring-indigo-500 outline-none"
                                                    />
                                                ) : (
                                                    p.fechaProgramada
                                                )}
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold border ${getEstadoStyle(p.estado)}`}>
                                                    {getEstadoIcon(p.estado)}
                                                    {p.estado}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-xs text-gray-600 max-w-xs truncate" title={p.observaciones}>
                                                {editingId === p.id ? (
                                                    <input 
                                                        type="text"
                                                        placeholder="Motivo del cambio..."
                                                        value={observacionesEdit}
                                                        onChange={e => setObservacionesEdit(e.target.value)}
                                                        className="w-full p-1 border border-indigo-300 rounded focus:ring-1 focus:ring-indigo-500 outline-none"
                                                    />
                                                ) : (
                                                    p.observaciones || <span className="text-gray-300 italic">Sin observaciones</span>
                                                )}
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                {editingId === p.id ? (
                                                    <div className="flex justify-end gap-2">
                                                        <button 
                                                            onClick={() => setEditingId(null)}
                                                            className="px-2 py-1 text-xs text-gray-500 hover:bg-gray-100 rounded font-medium"
                                                        >
                                                            Cancelar
                                                        </button>
                                                        <button 
                                                            onClick={handleUpdateDate}
                                                            disabled={updating}
                                                            className="px-2 py-1 text-xs bg-indigo-600 text-white hover:bg-indigo-700 rounded font-bold"
                                                        >
                                                            {updating ? '...' : 'Guardar'}
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <button 
                                                        onClick={() => startEditing(p)}
                                                        className="p-1.5 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded transition-colors"
                                                        title="Reprogramar fecha"
                                                    >
                                                        <Edit2 className="w-4 h-4" />
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </SidebarLayout>
    )
}
