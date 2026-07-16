import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Calendar as CalendarIcon, ArrowLeft, Clock, CalendarDays, CheckCircle, AlertTriangle, FileText, AlertCircle, Edit2, ShieldAlert, User, Clock3, MessageSquare } from 'lucide-react'
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
    fechaReal?: string
    horaReal?: string
    detectadoPorSistema?: boolean
    fechaIncumplimiento?: string
}

interface ExpedienteData {
    reclusoNombre: string
    reclusoCedula: string
    contadorIncumplimientos: number
}

export default function CalendarioPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const { username } = useAuth()
    
    const [presentaciones, setPresentaciones] = useState<PresentacionData[]>([])
    const [expediente, setExpediente] = useState<ExpedienteData | null>(null)
    const [loading, setLoading] = useState(true)
    const [generating, setGenerating] = useState(false)
    const [error, setError] = useState('')
    const [successMsg, setSuccessMsg] = useState('')
    
    // Form for generation
    const [frecuencia, setFrecuencia] = useState('MENSUAL')
    const [fechaInicio, setFechaInicio] = useState(new Date().toISOString().split('T')[0])
    
    // Form for updating single date (Reprogramación)
    const [editingId, setEditingId] = useState<string | null>(null)
    const [nuevaFecha, setNuevaFecha] = useState('')
    const [observacionesEdit, setObservacionesEdit] = useState('')
    const [updating, setUpdating] = useState(false)

    // Modal state for registering compliance/non-compliance
    const [selectedPres, setSelectedPres] = useState<PresentacionData | null>(null)
    const [tipoAsistencia, setTipoAsistencia] = useState<'SI' | 'NO'>('SI')
    const [horaReal, setHoraReal] = useState('')
    const [fechaIncumplimiento, setFechaIncumplimiento] = useState('')
    const [observacionesRegistro, setObservacionesRegistro] = useState('')
    const [isSubmitting, setIsSubmitting] = useState(false)

    const loadCalendario = async () => {
        try {
            const res = await api.get<PresentacionData[]>(`/calendario/${id}`)
            setPresentaciones(res.data || [])
        } catch (err) {
            console.error('Error cargando calendario', err)
        }
    }

    const loadExpediente = async () => {
        try {
            const res = await api.get<any[]>('/post-penal/expedientes')
            const exp = res.data.find((e: any) => e.id === id)
            if (exp) {
                setExpediente({
                    reclusoNombre: exp.reclusoNombre,
                    reclusoCedula: exp.reclusoCedula,
                    contadorIncumplimientos: exp.contadorIncumplimientos || 0
                })
            }
        } catch (err) {
            console.error('Error cargando expediente', err)
        }
    }

    const loadAllData = async () => {
        setLoading(true)
        await Promise.all([loadCalendario(), loadExpediente()])
        setLoading(false)
    }

    useEffect(() => {
        loadAllData()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id])

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
            await loadAllData()
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
            await loadAllData()
        } catch (err) {
            console.error('Error actualizando fecha', err)
            alert('Error al actualizar la fecha')
        } finally {
            setUpdating(false)
        }
    }

    const openRegisterModal = async (pres: PresentacionData) => {
        // Recargar datos para estar seguros del estado más actual antes de mostrar
        setLoading(true)
        try {
            const res = await api.get<PresentacionData[]>(`/calendario/${id}`)
            const latestPres = res.data.find(p => p.id === pres.id)
            await loadExpediente()
            
            if (latestPres) {
                setSelectedPres(latestPres)
                setTipoAsistencia('SI')
                
                // Formatear hora actual
                const now = new Date()
                const hh = String(now.getHours()).padStart(2, '0')
                const mm = String(now.getMinutes()).padStart(2, '0')
                setHoraReal(`${hh}:${mm}`)

                // Formatear fecha y hora local
                const yyyy = now.getFullYear()
                const month = String(now.getMonth() + 1).padStart(2, '0')
                const dd = String(now.getDate()).padStart(2, '0')
                setFechaIncumplimiento(`${yyyy}-${month}-${dd}T${hh}:${mm}`)
                
                setObservacionesRegistro('')
            }
        } catch (err) {
            console.error('Error al abrir el modal de registro', err)
        } finally {
            setLoading(false)
        }
    }

    const handleConfirmarRegistro = async () => {
        if (!selectedPres) return
        setIsSubmitting(true)
        try {
            if (tipoAsistencia === 'SI') {
                await api.put(`/calendario/${selectedPres.id}/registrar`, {
                    oficialQueRegistro: username || 'Oficial',
                    observaciones: observacionesRegistro,
                    horaReal: horaReal
                })
                alert('Presentación registrada como CUMPLIDA exitosamente.')
            } else {
                await api.post(`/calendario/${selectedPres.id}/incumplimiento`, {
                    oficialQueRegistro: username || 'Oficial',
                    observaciones: observacionesRegistro,
                    fechaDetectada: fechaIncumplimiento
                })
                alert('Presentación registrada como INCUMPLIDA. Se ha disparado el sistema de alertas.')
            }
            setSelectedPres(null)
            await loadAllData()
        } catch (err: any) {
            console.error('Error al registrar asistencia', err)
            alert(err.response?.data?.message || 'Error al procesar el registro.')
        } finally {
            setIsSubmitting(false)
        }
    }

    const getEstadoStyle = (estado: string) => {
        switch (estado) {
            case 'PENDIENTE': return 'bg-blue-50 text-blue-700 border-blue-200'
            case 'CUMPLIDA': return 'bg-emerald-50 text-emerald-700 border-emerald-200'
            case 'INCUMPLIDA': return 'bg-red-50 text-red-700 border-red-200'
            default: return 'bg-gray-50 text-gray-700 border-gray-200'
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

    const getAlertLevel = (currentCount: number) => {
        if (currentCount === 0) return { nivel: 'Nivel 1', desc: 'Notificación al Oficial de Seguimiento', style: 'bg-emerald-100 text-emerald-800 border-emerald-200' }
        if (currentCount === 1) return { nivel: 'Nivel 2', desc: 'Notificación al Oficial y Supervisor', style: 'bg-amber-100 text-amber-800 border-amber-200' }
        return { nivel: 'Nivel 3', desc: 'Medida Urgente ante Tribunal', style: 'bg-red-100 text-red-800 border-red-200' }
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center min-h-[60vh]">
                    <div className="flex flex-col items-center gap-3">
                        <div className="w-10 h-10 border-4 border-blue-100 border-t-indigo-600 rounded-full animate-spin"></div>
                        <p className="text-sm text-gray-505 font-medium">Cargando calendario de presentaciones...</p>
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            <div className="max-w-6xl mx-auto space-y-6">
                
                {/* Header */}
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div className="flex items-center gap-4">
                        <button 
                            onClick={() => navigate('/post')}
                            className="p-2.5 bg-white border border-gray-250 rounded-xl text-gray-500 hover:bg-gray-55 hover:text-gray-800 transition-colors shadow-sm"
                        >
                            <ArrowLeft className="w-5 h-5" />
                        </button>
                        <div>
                            <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                                <CalendarIcon className="w-8 h-8 text-indigo-600" />
                                Calendario de Presentaciones
                            </h1>
                            {expediente ? (
                                <p className="text-sm text-gray-505 mt-1 flex items-center gap-2">
                                    <User className="w-4 h-4 text-indigo-500" />
                                    Egresado: <strong className="text-gray-700">{expediente.reclusoNombre}</strong> (C.I. {expediente.reclusoCedula})
                                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-amber-700 border border-amber-200 ml-2">
                                        {expediente.contadorIncumplimientos} incumplimientos
                                    </span>
                                </p>
                            ) : (
                                <p className="text-sm text-gray-505 mt-1">
                                    Programe y gestione las presentaciones periódicas del egresado.
                                </p>
                            )}
                        </div>
                    </div>
                </div>

                {presentaciones.length === 0 ? (
                    <div className="bg-white p-8 rounded-2xl border border-gray-200 shadow-sm max-w-lg">
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2 mb-4">
                            <CalendarDays className="w-5 h-5 text-indigo-500" />
                            Generar Calendario
                        </h3>
                        <p className="text-sm text-gray-600 mb-6 leading-relaxed">
                            No hay presentaciones programadas para este expediente. Seleccione una frecuencia y la fecha de inicio para generar automáticamente las fechas.
                        </p>
                        
                        {error && (
                            <div className="mb-4 p-3.5 bg-red-50 border border-red-200 rounded-xl text-red-800 text-xs font-bold flex items-center gap-2">
                                <AlertCircle className="w-4 h-4" />
                                {error}
                            </div>
                        )}
                        
                        <form onSubmit={handleGenerar} className="space-y-4">
                            <div>
                                <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1.5">
                                    Frecuencia *
                                </label>
                                <select
                                    value={frecuencia}
                                    onChange={e => setFrecuencia(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-indigo-500 outline-none transition-all bg-white"
                                >
                                    <option value="SEMANAL">Semanal (Cada 7 días)</option>
                                    <option value="QUINCENAL">Quincenal (Cada 14 días)</option>
                                    <option value="MENSUAL">Mensual (Cada 1 mes)</option>
                                </select>
                            </div>
                            
                            <div>
                                <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1.5">
                                    Fecha de Inicio *
                                </label>
                                <input
                                    type="date"
                                    required
                                    value={fechaInicio}
                                    onChange={e => setFechaInicio(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                                />
                            </div>
                            
                            <div className="pt-2">
                                <button
                                    type="submit"
                                    disabled={generating}
                                    className="w-full flex justify-center items-center gap-2 px-6 py-3 bg-indigo-600 text-white hover:bg-indigo-750 rounded-xl font-bold uppercase tracking-wider text-xs transition-all disabled:opacity-50 shadow-md"
                                >
                                    <CalendarDays className="w-4.5 h-4.5" />
                                    {generating ? 'Generando...' : 'Generar Fechas'}
                                </button>
                            </div>
                        </form>
                    </div>
                ) : (
                    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
                        <div className="px-5 py-4.5 border-b border-gray-100 bg-gray-50 flex items-center justify-between">
                            <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2">
                                <CalendarDays className="w-4.5 h-4.5 text-indigo-500" />
                                Fechas Programadas
                            </h3>
                            {successMsg && (
                                <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-3 py-1.5 rounded-full border border-emerald-200">
                                    {successMsg}
                                </span>
                            )}
                        </div>
                        
                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-sm border-collapse">
                                <thead>
                                    <tr className="border-b border-gray-200 bg-gray-50/50 text-gray-400 font-bold uppercase tracking-wider text-xxs">
                                        <th className="px-6 py-3.5">#</th>
                                        <th className="px-6 py-3.5">Fecha Programada</th>
                                        <th className="px-6 py-3.5">Estado</th>
                                        <th className="px-6 py-3.5">Detalle / Fecha Real</th>
                                        <th className="px-6 py-3.5">Observaciones</th>
                                        <th className="px-6 py-3.5 text-right">Acciones</th>
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
                                                        className="p-1.5 border border-indigo-300 rounded-lg focus:ring-1 focus:ring-indigo-500 outline-none text-xs"
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
                                                {p.estado === 'INCUMPLIDA' && p.detectadoPorSistema && (
                                                    <span className="ml-1.5 inline-flex items-center px-1.5 py-0.5 rounded text-3xs font-semibold bg-gray-100 text-gray-655 border border-gray-200">
                                                        Sistema
                                                    </span>
                                                )}
                                            </td>
                                            <td className="px-6 py-4 text-xs text-gray-550 font-medium">
                                                {p.estado === 'CUMPLIDA' && p.fechaReal && (
                                                    <span>{p.fechaReal} a las {p.horaReal || '--:--'}</span>
                                                )}
                                                {p.estado === 'INCUMPLIDA' && p.fechaIncumplimiento && (
                                                    <span>Detectado: {p.fechaIncumplimiento.replace('T', ' ').substring(0, 16)}</span>
                                                )}
                                                {p.estado === 'PENDIENTE' && <span>--</span>}
                                            </td>
                                            <td className="px-6 py-4 text-xs text-gray-600 max-w-xs truncate" title={p.observaciones}>
                                                {editingId === p.id ? (
                                                    <input 
                                                        type="text"
                                                        placeholder="Motivo del cambio..."
                                                        value={observacionesEdit}
                                                        onChange={e => setObservacionesEdit(e.target.value)}
                                                        className="w-full p-1.5 border border-indigo-300 rounded-lg focus:ring-1 focus:ring-indigo-500 outline-none text-xs"
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
                                                            className="px-2.5 py-1 text-xs text-gray-500 hover:bg-gray-100 rounded-lg font-bold"
                                                        >
                                                            Cancelar
                                                        </button>
                                                        <button 
                                                            onClick={handleUpdateDate}
                                                            disabled={updating}
                                                            className="px-3 py-1 text-xs bg-indigo-650 text-white hover:bg-indigo-700 rounded-lg font-bold shadow-sm"
                                                        >
                                                            {updating ? '...' : 'Guardar'}
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <div className="flex items-center justify-end gap-2.5">
                                                        {p.estado === 'PENDIENTE' && (
                                                            <>
                                                                <button
                                                                    onClick={() => openRegisterModal(p)}
                                                                    className="px-3 py-1.5 text-xs bg-indigo-50 text-indigo-700 hover:bg-indigo-100 border border-indigo-200 rounded-lg font-bold transition-all shadow-xxs"
                                                                    title="Registrar asistencia"
                                                                >
                                                                    Registrar
                                                                </button>
                                                                <button 
                                                                    onClick={() => startEditing(p)}
                                                                    className="p-1.5 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors border border-transparent hover:border-indigo-100"
                                                                    title="Reprogramar fecha"
                                                                >
                                                                    <Edit2 className="w-4 h-4" />
                                                                </button>
                                                            </>
                                                        )}
                                                        {p.estado === 'INCUMPLIDA' && p.detectadoPorSistema && (
                                                            <button
                                                                onClick={() => openRegisterModal(p)}
                                                                className="px-2.5 py-1 text-xxs bg-gray-50 hover:bg-gray-105 text-gray-600 rounded-lg border border-gray-200 font-semibold transition-all"
                                                                title="Ver advertencia del sistema"
                                                            >
                                                                Ver Registro
                                                            </button>
                                                        )}
                                                    </div>
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

            {/* Modal for Compliance / Non-Compliance Registry */}
            {selectedPres && (
                <div className="fixed inset-0 bg-gray-900/65 backdrop-blur-sm flex items-center justify-center z-50 p-4 transition-all duration-300 animate-fade-in">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden border border-gray-200 transform transition-all">
                        
                        {/* Header */}
                        <div className="px-6 py-4.5 bg-gray-900 text-white flex items-center justify-between">
                            <div>
                                <h3 className="text-lg font-bold">Registrar Presentación</h3>
                                <p className="text-xs text-gray-300 mt-0.5 font-mono">Programada: {selectedPres.fechaProgramada}</p>
                            </div>
                            <button 
                                onClick={() => setSelectedPres(null)}
                                className="p-1.5 hover:bg-gray-800 rounded-lg text-white/80 hover:text-white transition-colors"
                            >
                                <ArrowLeft className="w-5 h-5 rotate-90" />
                            </button>
                        </div>

                        {/* Blocked by System Check */}
                        {selectedPres.estado === 'INCUMPLIDA' && selectedPres.detectadoPorSistema ? (
                            <div className="p-6 space-y-4">
                                <div className="p-4 bg-red-50 border border-red-200 text-red-800 rounded-2xl flex items-start gap-3">
                                    <ShieldAlert className="w-6 h-6 text-red-650 shrink-0 mt-0.5" />
                                    <div>
                                        <h4 className="text-sm font-bold">Registro Duplicado Bloqueado</h4>
                                        <p className="text-xs text-red-700 leading-relaxed mt-1">
                                            Esta presentación ya fue marcada automáticamente como <strong>INCUMPLIDA</strong> por el sistema al vencer la fecha programada. 
                                            No se permite el registro manual duplicado ni la modificación de estado.
                                        </p>
                                    </div>
                                </div>
                                <div className="p-4 bg-gray-50 border border-gray-200 rounded-xl space-y-2">
                                    <h5 className="text-xs font-bold text-gray-500 uppercase tracking-wider">Historial del Sistema:</h5>
                                    <p className="text-xs text-gray-700 font-mono leading-relaxed whitespace-pre-wrap">
                                        {selectedPres.observaciones || "Sin observaciones registradas."}
                                    </p>
                                </div>
                                <div className="pt-4 border-t border-gray-150 flex justify-end">
                                    <button 
                                        onClick={() => setSelectedPres(null)}
                                        className="px-5 py-2.5 bg-gray-900 hover:bg-gray-800 text-white rounded-xl text-xs font-bold uppercase tracking-wider shadow-sm transition-all"
                                    >
                                        Cerrar Detalle
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className="p-6 space-y-5">
                                
                                {/* Attendance Selector */}
                                <div className="space-y-2">
                                    <label className="block text-xs font-bold text-gray-400 uppercase tracking-wider">
                                        ¿El egresado asistió a su cita?
                                    </label>
                                    <div className="grid grid-cols-2 gap-3">
                                        <button
                                            type="button"
                                            onClick={() => setTipoAsistencia('SI')}
                                            className={`flex items-center justify-center gap-2.5 p-3 rounded-xl border font-bold transition-all shadow-xxs ${tipoAsistencia === 'SI' ? 'bg-indigo-50 border-indigo-400 text-indigo-700 ring-2 ring-indigo-500/20' : 'bg-white border-gray-250 text-gray-650 hover:bg-gray-50'}`}
                                        >
                                            <CheckCircle className={`w-5 h-5 ${tipoAsistencia === 'SI' ? 'text-indigo-600' : 'text-gray-400'}`} />
                                            Sí, asistió
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => setTipoAsistencia('NO')}
                                            className={`flex items-center justify-center gap-2.5 p-3 rounded-xl border font-bold transition-all shadow-xxs ${tipoAsistencia === 'NO' ? 'bg-red-50/50 border-red-400 text-red-700 ring-2 ring-red-500/20' : 'bg-white border-gray-255 text-gray-650 hover:bg-gray-50'}`}
                                        >
                                            <AlertTriangle className={`w-5 h-5 ${tipoAsistencia === 'NO' ? 'text-red-600' : 'text-gray-400'}`} />
                                            No se presentó
                                        </button>
                                    </div>
                                </div>

                                {/* Conditionally Render Form Controls */}
                                {tipoAsistencia === 'SI' ? (
                                    <div className="space-y-4 animate-fade-in-quick">
                                        <div className="p-3 bg-emerald-50/50 border border-emerald-100 rounded-xl flex items-start gap-2.5">
                                            <CheckCircle className="w-4.5 h-4.5 text-emerald-600 shrink-0 mt-0.5" />
                                            <p className="text-xs text-emerald-800 leading-relaxed font-semibold">
                                                Al confirmar la asistencia, el estado del calendario cambiará a <strong>CUMPLIDA</strong> de forma permanente. Este registro no podrá ser revertido.
                                            </p>
                                        </div>
                                        <div>
                                            <label className="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                                                <Clock3 className="w-4 h-4 text-gray-400" />
                                                Hora Real de Presentación
                                            </label>
                                            <input
                                                type="time"
                                                required
                                                value={horaReal}
                                                onChange={e => setHoraReal(e.target.value)}
                                                className="w-full p-2.5 border border-gray-300 rounded-xl text-sm font-semibold focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                                            />
                                        </div>
                                    </div>
                                ) : (
                                    <div className="space-y-4 animate-fade-in-quick">
                                        
                                        {/* Dynamic alert level indicator */}
                                        {expediente && (
                                            <div className="p-4 bg-gray-50 border border-gray-200 rounded-xl space-y-3 shadow-xxs">
                                                <div className="flex justify-between items-center text-xs">
                                                    <span className="text-gray-500 font-bold uppercase tracking-wider">Incumplimientos acumulados:</span>
                                                    <span className="font-extrabold text-gray-800 text-sm">{expediente.contadorIncumplimientos}</span>
                                                </div>
                                                <div className="border-t border-gray-200/60 pt-2.5 flex items-center justify-between">
                                                    <span className="text-xs text-gray-505 font-bold uppercase tracking-wider">Alerta a Generarse:</span>
                                                    <span className={`px-2.5 py-0.5 rounded-full text-xxs font-extrabold border ${getAlertLevel(expediente.contadorIncumplimientos).style}`}>
                                                        {getAlertLevel(expediente.contadorIncumplimientos).nivel}
                                                    </span>
                                                </div>
                                                <p className="text-xxs text-gray-505 italic leading-relaxed">
                                                    Acción requerida: {getAlertLevel(expediente.contadorIncumplimientos).desc}
                                                </p>
                                            </div>
                                        )}

                                        <div>
                                            <label className="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                                                <Clock3 className="w-4 h-4 text-gray-400" />
                                                Fecha y Hora del Incumplimiento
                                            </label>
                                            <input
                                                type="datetime-local"
                                                required
                                                value={fechaIncumplimiento}
                                                onChange={e => setFechaIncumplimiento(e.target.value)}
                                                className="w-full p-2.5 border border-gray-300 rounded-xl text-sm font-semibold focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
                                            />
                                        </div>
                                    </div>
                                )}

                                {/* Observations */}
                                <div>
                                    <label className="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                                        <MessageSquare className="w-4 h-4 text-gray-400" />
                                        Observaciones y Detalles del Estado
                                    </label>
                                    <textarea
                                        value={observacionesRegistro}
                                        onChange={e => setObservacionesRegistro(e.target.value.substring(0, 500))}
                                        rows={3}
                                        maxLength={500}
                                        placeholder={tipoAsistencia === 'SI' ? "Detalle el estado físico, actitud o notas generales del egresado..." : "Detalle las razones o justificaciones si se conocen, o notas del incumplimiento..."}
                                        className="w-full p-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none"
                                    />
                                    <div className="flex justify-between items-center text-3xs text-gray-400 font-bold mt-1 uppercase tracking-wider">
                                        <span>Máx 500 caracteres</span>
                                        <span>{observacionesRegistro.length} / 500</span>
                                    </div>
                                </div>

                                {/* Footer Actions */}
                                <div className="pt-4 border-t border-gray-150 flex gap-3">
                                    <button
                                        type="button"
                                        onClick={() => setSelectedPres(null)}
                                        className="flex-1 px-4 py-2.5 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors text-xs font-bold uppercase tracking-wider"
                                    >
                                        Cancelar
                                    </button>
                                    <button
                                        type="button"
                                        onClick={handleConfirmarRegistro}
                                        disabled={isSubmitting}
                                        className={`flex-1 px-4 py-2.5 text-white rounded-xl transition-all text-xs font-bold uppercase tracking-wider shadow-sm flex items-center justify-center gap-2 ${tipoAsistencia === 'SI' ? 'bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-300' : 'bg-red-600 hover:bg-red-700 disabled:bg-red-300'}`}
                                    >
                                        {isSubmitting ? 'Guardando...' : 'Confirmar Registro'}
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </SidebarLayout>
    )
}
