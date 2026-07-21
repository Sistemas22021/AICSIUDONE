import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, ShieldAlert, CheckCircle, Users, FileText, AlertTriangle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { maxDateTimeNow } from '../../shared/validations'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    firstLastname: string
    cellId?: string
    cellIdentifier?: string
    status: string
}

interface DeathReportData {
    id: string
    inmateId: string
    deceaseType: string
    dateTimeFound: string
    description: string
}

interface CohabitantState {
    inmateId: string
    firstName: string
    firstLastname: string
    cedula: string
    initialStatus: 'ILESO' | 'LESIONADO' | 'TRASLADADO_ENFERMERIA'
    comments: string
}

export default function IncidentRegisterPage() {
    const { inmateId } = useParams<{ inmateId: string }>()
    const navigate = useNavigate()
    
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    
    const [deceased, setDeceased] = useState<InmateData | null>(null)
    const [, setDeathReport] = useState<DeathReportData | null>(null)
    
    const [description, setDescription] = useState('')
    const [reporter, setReporter] = useState('')
    const [incidentDate, setIncidentDate] = useState(new Date().toISOString().slice(0, 16))
    const [cohabitants, setCohabitants] = useState<CohabitantState[]>([])
    
    const [successCode, setSuccessCode] = useState('')
    const [successId, setSuccessId] = useState('')

    useEffect(() => {
        const loadData = async () => {
            if (!inmateId) return
            setLoading(true)
            setError('')
            try {
                // Get mock user for reporter name
                const mockUser = JSON.parse(localStorage.getItem('mock_user') || '{}')
                setReporter(mockUser.username || 'Supervisor')

                // 1. Fetch deceased inmate details
                const inmateRes = await api.get<InmateData>(`/inmates/${inmateId}`)
                setDeceased(inmateRes.data)

                // 2. Try fetching death report draft
                let draftDescription = ''
                let draftDate = new Date().toISOString().slice(0, 16)
                try {
                    const reportRes = await api.get<DeathReportData>(`/inmates/${inmateId}/death-report`)
                    setDeathReport(reportRes.data)
                    draftDescription = reportRes.data.description || ''
                    if (reportRes.data.dateTimeFound) {
                        draftDate = reportRes.data.dateTimeFound.slice(0, 16)
                    }
                } catch (e) {
                    console.log('No draft found, using defaults', e)
                }

                setDescription(draftDescription)
                setIncidentDate(draftDate)

                // 3. Fetch cell co-habitants if cellId exists
                if (inmateRes.data.cellId) {
                    const cellMatesRes = await api.get<InmateData[]>(`/inmates/cell/${inmateRes.data.cellId}`)
                    const mates = cellMatesRes.data
                        .filter(mate => mate.id !== inmateId && mate.status !== 'ACTIVO_SALIDA_TEMPORAL')
                        .map(mate => ({
                            inmateId: mate.id,
                            firstName: mate.firstName,
                            firstLastname: mate.firstLastname,
                            cedula: mate.cedula,
                            initialStatus: 'ILESO' as const,
                            comments: ''
                        }))
                    setCohabitants(mates)
                }
            } catch (err) {
                console.error(err)
                setError('Error al cargar la información del recluso o la celda.')
            } finally {
                setLoading(false)
            }
        }
        loadData()
    }, [inmateId])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!description.trim()) {
            setError('La descripción de los hechos es obligatoria.')
            return
        }
        if (!reporter.trim()) {
            setError('Las iniciales o nombre del reportante es obligatorio.')
            return
        }

        setSaving(true)
        setError('')

        // Format dates correctly (append seconds)
        const dateISO = incidentDate.length === 16 ? incidentDate + ':00' : incidentDate

        // Construct participants
        const participants = [
            {
                inmateId: inmateId!,
                role: 'FALLECIDO',
                initialStatus: 'FALLECIDO',
                comments: 'Recluso hallado sin vida.'
            },
            ...cohabitants.map(c => ({
                inmateId: c.inmateId,
                role: 'COHABITANTE',
                initialStatus: c.initialStatus,
                comments: c.comments || 'Presente en celda.'
            }))
        ]

        try {
            const res = await api.post<{ id: string; code: string }>('/incidents', {
                description,
                reporter,
                incidentDate: dateISO,
                participants
            })
            setSuccessCode(res.data.code)
            setSuccessId(res.data.id)
            alert(`Expediente de incidente ${res.data.code} creado exitosamente.`);
        } catch (err) {
            console.error(err)
        } finally {
            setSaving(false)
        }
    }

    const handleStatusChange = (mateId: string, status: string) => {
        const validStatus = status as CohabitantState['initialStatus']
        setCohabitants(prev => prev.map(c => c.inmateId === mateId ? { ...c, initialStatus: validStatus } : c))
    }

    const handleCommentsChange = (mateId: string, value: string) => {
        setCohabitants(prev => prev.map(c => c.inmateId === mateId ? { ...c, comments: value } : c))
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center h-64">
                    <p className="text-gray-500 font-medium">Cargando datos del expediente...</p>
                </div>
            </SidebarLayout>
        )
    }

    if (successCode) {
        return (
            <SidebarLayout>
                <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-md border border-emerald-100 overflow-hidden mt-8">
                    <div className="bg-emerald-50 border-b border-emerald-100 p-6 text-center">
                        <CheckCircle className="w-16 h-16 text-emerald-600 mx-auto mb-3" />
                        <h2 className="text-2xl font-bold text-emerald-950">¡Expediente Registrado con Éxito!</h2>
                        <p className="text-emerald-800 font-semibold mt-1">Código del Siniestro: {successCode}</p>
                    </div>
                    <div className="p-6 space-y-6">
                        <div className="bg-amber-50 border border-amber-100 rounded-lg p-4 flex gap-3">
                            <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
                            <div>
                                <h4 className="font-bold text-amber-900 text-sm">Protocolo de Emergencia Activado</h4>
                                <p className="text-amber-800 text-xs mt-1">
                                    La celda {deceased?.cellIdentifier} ha sido **clausurada y bloqueada** para fines de investigación judicial. 
                                    Los sobrevivientes (co-habitantes) se encuentran en estado de **Reubicación de Emergencia Pendiente**.
                                </p>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <button
                                onClick={() => navigate('/mapa')}
                                className="flex flex-col items-center justify-center p-5 border border-blue-200 rounded-xl bg-blue-50/50 hover:bg-blue-50 transition-all text-center gap-2"
                            >
                                <Users className="w-8 h-8 text-blue-600" />
                                <span className="font-bold text-blue-900">Reubicar Sobrevivientes</span>
                                <span className="text-xs text-blue-600">Ir al mapa 2D para asignar nuevas celdas</span>
                            </button>

                            <button
                                onClick={() => navigate(`/incidentes/detalle/${successId}`)}
                                className="flex flex-col items-center justify-center p-5 border border-gray-200 rounded-xl bg-gray-50/50 hover:bg-gray-50 transition-all text-center gap-2"
                            >
                                <FileText className="w-8 h-8 text-gray-600" />
                                <span className="font-bold text-gray-900">Ver Expediente</span>
                                <span className="text-xs text-gray-500">Consultar y generar reporte imprimible</span>
                            </button>
                        </div>

                        <div className="pt-4 border-t border-gray-100 flex justify-end">
                            <button
                                onClick={() => navigate('/dashboard')}
                                className="px-6 py-2.5 bg-gray-900 hover:bg-gray-800 text-white rounded-lg font-medium transition-colors"
                            >
                                Volver al Dashboard
                            </button>
                        </div>
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            <div className="max-w-4xl mx-auto space-y-6">
                <div>
                    <button onClick={() => navigate('/egresos')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver a Egresos
                    </button>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <ShieldAlert className="w-8 h-8 text-red-600" />
                        Expediente de Incidente Interno de Seguridad
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Levantamiento de acta obligatoria por deceso no natural de recluso en celda.
                    </p>
                </div>

                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    {/* Deceased Inmate Info header */}
                    <div className="bg-red-50/60 border-b border-red-100 p-5 flex flex-wrap items-center justify-between gap-4">
                        <div>
                            <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-red-100 text-red-800 uppercase tracking-wider">
                                Deceso No Natural
                            </span>
                            <h2 className="text-lg font-bold text-gray-800 mt-2">
                                Fallecido: {deceased?.firstName} {deceased?.firstLastname}
                            </h2>
                            <p className="text-xs text-gray-500">
                                Cédula: {deceased?.cedula} | Celda de Origen: <span className="font-semibold text-gray-700">{deceased?.cellIdentifier || 'Sin asignar'}</span>
                            </p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit} className="p-6 space-y-6">
                        {error && (
                            <div className="p-3 bg-red-50 text-red-700 rounded-lg text-sm border border-red-100 font-medium">
                                {error}
                            </div>
                        )}

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Nombre o Iniciales del Reportante *</label>
                                <input
                                    type="text"
                                    value={reporter}
                                    onChange={e => setReporter(e.target.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s.]/g, ''))}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                                    placeholder="Ej. Sup. Gómez / S.G."
                                    maxLength={80}
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora del Incidente / Hallazgo *</label>
                                <input
                                    type="datetime-local"
                                    value={incidentDate}
                                    max={maxDateTimeNow()}
                                    onChange={e => setIncidentDate(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-1">Descripción / Relato General de los Hechos *</label>
                            <textarea
                                rows={4}
                                value={description}
                                onChange={e => setDescription(e.target.value)}
                                minLength={30}
                                className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 text-sm"
                                placeholder="Narrativa de cómo se descubrió la escena, primeros auxilios prestados, y llamado a fiscalía..."
                                required
                            />
                            <p className="text-xs text-gray-500 mt-1">Mínimo 30 caracteres.</p>
                        </div>

                        {/* Cohabitants section */}
                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <Users className="w-5 h-5 text-gray-600" />
                                <h3 className="font-bold text-gray-800">Co-habitantes y Testigos (Sobrevivientes)</h3>
                            </div>
                            
                            {cohabitants.length === 0 ? (
                                <p className="text-sm text-gray-500 italic bg-gray-50 p-4 rounded-lg border border-gray-100">
                                    El recluso se encontraba solo en esta celda. No hay co-habitantes para procesar.
                                </p>
                            ) : (
                                <div className="border border-gray-200 rounded-xl overflow-hidden shadow-sm">
                                    <table className="w-full text-left border-collapse">
                                        <thead>
                                            <tr className="bg-gray-50 text-gray-700 text-xs font-bold uppercase border-b border-gray-200">
                                                <th className="p-3">Nombre</th>
                                                <th className="p-3">Cédula</th>
                                                <th className="p-3">Estado Inicial</th>
                                                <th className="p-3">Comentarios / Observaciones</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-gray-100 text-sm">
                                            {cohabitants.map(mate => (
                                                <tr key={mate.inmateId} className="hover:bg-gray-50/50">
                                                    <td className="p-3 font-semibold text-gray-900">{mate.firstName} {mate.firstLastname}</td>
                                                    <td className="p-3 text-gray-500">C.I. {mate.cedula}</td>
                                                    <td className="p-3">
                                                        <select
                                                            value={mate.initialStatus}
                                                            onChange={e => handleStatusChange(mate.inmateId, e.target.value)}
                                                            className="p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-red-500 focus:border-red-500 bg-white text-xs font-bold"
                                                        >
                                                            <option value="ILESO">Ileso</option>
                                                            <option value="LESIONADO">Lesionado</option>
                                                            <option value="TRASLADADO_ENFERMERIA">Trasladado a Enfermería</option>
                                                        </select>
                                                    </td>
                                                    <td className="p-3">
                                                        <input
                                                            type="text"
                                                            value={mate.comments}
                                                            onChange={e => handleCommentsChange(mate.inmateId, e.target.value)}
                                                            maxLength={300}
                                                            placeholder="Notas del estado físico o testimonios..."
                                                            className="w-full p-2 border border-gray-300 rounded-md focus:ring-1 focus:ring-red-500 focus:border-red-500 text-xs"
                                                        />
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>

                        <div className="pt-4 border-t border-gray-100 flex justify-end gap-3">
                            <button
                                type="button"
                                onClick={() => navigate('/egresos')}
                                className="px-5 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                disabled={saving}
                                className="px-6 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-lg font-bold transition-colors disabled:opacity-50 flex items-center gap-2"
                            >
                                {saving ? 'Guardando...' : 'Cerrar Acta e Incidente'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </SidebarLayout>
    )
}
