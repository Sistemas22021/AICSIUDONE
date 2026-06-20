import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { FileText, Save, ArrowLeft, User, MapPin, Phone, AlertTriangle, CalendarDays, CheckCircle2, XCircle } from 'lucide-react'
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
    domicilio?: string
    municipio?: string
    contactoEmergenciaNombre?: string
    contactoEmergenciaTelefono?: string
    nivelRiesgo?: string
}

export default function PostPenalProfilePage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [expediente, setExpediente] = useState<ExpedienteData | null>(null)
    const [presentaciones, setPresentaciones] = useState<any[]>([])
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    
    // Form state
    const [formData, setFormData] = useState({
        domicilio: '',
        municipio: '',
        contactoEmergenciaNombre: '',
        contactoEmergenciaTelefono: '',
        nivelRiesgo: 'BAJO'
    })

    useEffect(() => {
        const loadExpediente = async () => {
            try {
                // Since there is no GET /expedientes/{id} endpoint based on the plan,
                // we will fetch all and filter, or we should create the endpoint.
                // Let's assume we fetch all and find it for now to save time, 
                // but the best practice would be an endpoint.
                const res = await api.get<ExpedienteData[]>('/post-penal/expedientes')
                const exp = res.data.find(e => e.id === id)
                if (exp) {
                    setExpediente(exp)
                    setFormData({
                        domicilio: exp.domicilio || '',
                        municipio: exp.municipio || '',
                        contactoEmergenciaNombre: exp.contactoEmergenciaNombre || '',
                        contactoEmergenciaTelefono: exp.contactoEmergenciaTelefono || '',
                        nivelRiesgo: exp.nivelRiesgo || 'BAJO'
                    })
                    
                    try {
                        const calRes = await api.get(`/calendario/${exp.id}`)
                        setPresentaciones(calRes.data || [])
                    } catch (e) {
                        console.error('Error cargando calendario', e)
                    }
                } else {
                    setError('Expediente no encontrado')
                }
            } catch (err) {
                console.error('Error cargando expediente', err)
                setError('Error al cargar el expediente')
            } finally {
                setLoading(false)
            }
        }
        if (id) loadExpediente()
    }, [id])

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault()
        setSaving(true)
        setError('')
        try {
            await api.put(`/post-penal/expedientes/${id}/profile`, formData)
            navigate('/post')
        } catch (err) {
            console.error('Error guardando perfil', err)
            setError('Error al guardar los cambios')
        } finally {
            setSaving(false)
        }
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center min-h-[60vh]">
                    <div className="flex flex-col items-center gap-3">
                        <div className="w-10 h-10 border-4 border-blue-100 border-t-blue-600 rounded-full animate-spin"></div>
                        <p className="text-sm text-gray-500 font-medium">Cargando perfil...</p>
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    if (!expediente) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center min-h-[60vh]">
                    <div className="text-center text-red-500 font-bold">{error}</div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            <div className="max-w-4xl mx-auto space-y-6">
                
                <div className="flex items-center gap-4">
                    <button 
                        onClick={() => navigate('/post')}
                        className="p-2 bg-white border border-gray-200 rounded-xl text-gray-500 hover:bg-gray-50 hover:text-gray-700 transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5" />
                    </button>
                    <div>
                        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                            <FileText className="w-8 h-8 text-indigo-600" />
                            Perfil Post-Penitenciario
                        </h1>
                        <p className="text-sm text-gray-500 mt-1">
                            Complete los datos de seguimiento del egresado.
                        </p>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Read-only Data */}
                    <div className="md:col-span-1 space-y-4">
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm">
                            <h3 className="text-sm font-bold text-gray-900 flex items-center gap-2 border-b border-gray-100 pb-3 mb-4">
                                <User className="w-4 h-4 text-indigo-500" />
                                Datos del Egresado
                            </h3>
                            <div className="space-y-4">
                                <div>
                                    <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Nombre Completo</p>
                                    <p className="font-bold text-gray-800 text-sm mt-0.5">{expediente.reclusoNombre}</p>
                                </div>
                                <div>
                                    <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Cédula</p>
                                    <p className="font-bold text-gray-800 text-sm mt-0.5">{expediente.reclusoCedula}</p>
                                </div>
                                <div>
                                    <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Fecha de Egreso</p>
                                    <p className="font-bold text-gray-800 text-sm mt-0.5">{expediente.fechaEgreso}</p>
                                </div>
                                <div>
                                    <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Oficial Asignado</p>
                                    <p className="font-bold text-indigo-600 text-sm mt-0.5">
                                        {expediente.oficialAsignadoNombre} ({expediente.oficialAsignadoCedula})
                                    </p>
                                </div>
                                <div>
                                    <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Estado</p>
                                    <p className="font-bold text-emerald-600 text-sm mt-0.5 uppercase">{expediente.estado}</p>
                                </div>
                            </div>
                        </div>

                        {/* Historial de Presentaciones */}
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm mt-6">
                            <h3 className="text-sm font-bold text-gray-900 flex items-center gap-2 border-b border-gray-100 pb-3 mb-4">
                                <CalendarDays className="w-4 h-4 text-indigo-500" />
                                Historial de Presentaciones
                            </h3>
                            {presentaciones.length === 0 ? (
                                <p className="text-xs text-gray-500 italic text-center">No hay presentaciones registradas.</p>
                            ) : (
                                <div className="space-y-3">
                                    {presentaciones.map(p => (
                                        <div key={p.id} className="p-3 border border-gray-100 rounded-xl bg-gray-50 flex items-start gap-3">
                                            {p.estado === 'CUMPLIDA' ? (
                                                <CheckCircle2 className="w-5 h-5 text-emerald-500 mt-0.5 shrink-0" />
                                            ) : p.estado === 'INCUMPLIDA' ? (
                                                <XCircle className="w-5 h-5 text-red-500 mt-0.5 shrink-0" />
                                            ) : (
                                                <div className="w-5 h-5 rounded-full border-2 border-yellow-400 mt-0.5 shrink-0"></div>
                                            )}
                                            <div>
                                                <p className="text-xs font-bold text-gray-900">{p.fechaProgramada}</p>
                                                <p className={`text-[10px] font-bold mt-1 ${p.estado === 'CUMPLIDA' ? 'text-emerald-600' : p.estado === 'INCUMPLIDA' ? 'text-red-600' : 'text-yellow-600'}`}>
                                                    {p.estado}
                                                </p>
                                            </div>
                                        </div>
                                    ))}
                                    
                                    <div className="pt-3 border-t border-gray-100">
                                        <p className="text-[10px] font-semibold text-gray-500 text-center">
                                            Total: {presentaciones.length} | Cumplidas: {presentaciones.filter(p => p.estado === 'CUMPLIDA').length} | Incumplidas: {presentaciones.filter(p => p.estado === 'INCUMPLIDA').length}
                                        </p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Editable Form */}
                    <div className="md:col-span-2">
                        <form onSubmit={handleSave} className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm space-y-6">
                            {error && (
                                <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-800 text-xs font-semibold">
                                    {error}
                                </div>
                            )}

                            <div>
                                <h3 className="text-sm font-bold text-gray-900 flex items-center gap-2 border-b border-gray-100 pb-3 mb-4">
                                    <MapPin className="w-4 h-4 text-indigo-500" />
                                    Ubicación y Residencia
                                </h3>
                                <div className="space-y-4">
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                            Municipio *
                                        </label>
                                        <input
                                            required
                                            type="text"
                                            value={formData.municipio}
                                            onChange={e => setFormData({ ...formData, municipio: e.target.value })}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                                            placeholder="Ej. Municipio Sucre"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                            Domicilio Exacto *
                                        </label>
                                        <textarea
                                            required
                                            rows={2}
                                            value={formData.domicilio}
                                            onChange={e => setFormData({ ...formData, domicilio: e.target.value })}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                                            placeholder="Dirección completa del lugar de residencia..."
                                        />
                                    </div>
                                </div>
                            </div>

                            <div>
                                <h3 className="text-sm font-bold text-gray-900 flex items-center gap-2 border-b border-gray-100 pb-3 mb-4">
                                    <Phone className="w-4 h-4 text-indigo-500" />
                                    Contacto de Emergencia
                                </h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                            Nombre Completo *
                                        </label>
                                        <input
                                            required
                                            type="text"
                                            value={formData.contactoEmergenciaNombre}
                                            onChange={e => setFormData({ ...formData, contactoEmergenciaNombre: e.target.value })}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                            Teléfono *
                                        </label>
                                        <input
                                            required
                                            type="text"
                                            value={formData.contactoEmergenciaTelefono}
                                            onChange={e => setFormData({ ...formData, contactoEmergenciaTelefono: e.target.value })}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                                            placeholder="0414-0000000"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div>
                                <h3 className="text-sm font-bold text-gray-900 flex items-center gap-2 border-b border-gray-100 pb-3 mb-4">
                                    <AlertTriangle className="w-4 h-4 text-indigo-500" />
                                    Evaluación de Riesgo
                                </h3>
                                <div>
                                    <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide mb-1">
                                        Nivel de Riesgo *
                                    </label>
                                    <select
                                        value={formData.nivelRiesgo}
                                        onChange={e => setFormData({ ...formData, nivelRiesgo: e.target.value })}
                                        className="w-full p-2.5 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all bg-white"
                                    >
                                        <option value="BAJO">BAJO (Presentaciones regulares)</option>
                                        <option value="MEDIO">MEDIO (Requiere atención moderada)</option>
                                        <option value="ALTO">ALTO (Seguimiento estricto requerido)</option>
                                    </select>
                                </div>
                            </div>

                            <div className="flex justify-end pt-4 border-t border-gray-100">
                                <button
                                    type="submit"
                                    disabled={saving}
                                    className="flex items-center gap-2 px-6 py-2.5 bg-indigo-600 text-white hover:bg-indigo-700 rounded-xl font-bold uppercase tracking-wider text-sm transition-all disabled:opacity-50"
                                >
                                    <Save className="w-4 h-4" />
                                    {saving ? 'Guardando...' : 'Guardar Perfil'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </SidebarLayout>
    )
}
