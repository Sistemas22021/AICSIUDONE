import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, ShieldAlert, Printer, AlertTriangle, User, Calendar, Home, FileText } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface IncidentParticipantDto {
    id: string
    inmateId: string
    inmateName: string
    inmateCedula: string
    role: string // FALLECIDO, COHABITANTE
    initialStatus: string // FALLECIDO, ILESO, LESIONADO, TRASLADADO_ENFERMERIA
    comments: string
}

interface IncidentData {
    id: string
    code: string
    cellIdentifier: string
    cellId: string
    description: string
    incidentDate: string
    reporter: string
    status: string
    participants: IncidentParticipantDto[]
}

export default function IncidentDetailPage() {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const [incident, setIncident] = useState<IncidentData | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        const loadIncident = async () => {
            if (!id) return
            setLoading(true)
            setError('')
            try {
                const res = await api.get<IncidentData>(`/incidents/${id}`)
                setIncident(res.data)
            } catch (err: any) {
                console.error(err)
                setError('Error al cargar el detalle del incidente.')
            } finally {
                setLoading(false)
            }
        }
        loadIncident()
    }, [id])

    const handlePrint = () => {
        window.print()
    }

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

    const getStatusStyle = (status: string) => {
        switch (status) {
            case 'FALLECIDO':
                return 'bg-red-100 text-red-800 border-red-200'
            case 'LESIONADO':
                return 'bg-amber-100 text-amber-800 border-amber-200'
            case 'TRASLADADO_ENFERMERIA':
                return 'bg-blue-100 text-blue-800 border-blue-200'
            default:
                return 'bg-emerald-100 text-emerald-800 border-emerald-200'
        }
    }

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center h-64">
                    <p className="text-gray-500 font-medium">Cargando expediente del incidente...</p>
                </div>
            </SidebarLayout>
        )
    }

    if (error || !incident) {
        return (
            <SidebarLayout>
                <div className="max-w-4xl mx-auto space-y-6">
                    <button onClick={() => navigate('/incidentes')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4 transition-colors">
                        <ArrowLeft className="w-4 h-4" /> Volver a la Bitácora
                    </button>
                    <div className="p-4 bg-red-50 text-red-700 rounded-xl border border-red-100 text-sm font-medium">
                        {error || 'El incidente no pudo ser cargado.'}
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    return (
        <SidebarLayout>
            {/* Inject custom print styles to hide everything except the print-area */}
            <style dangerouslySetInnerHTML={{__html: `
                @media print {
                    body {
                        background-color: white !important;
                        color: black !important;
                    }
                    /* Hide sidebar and actions */
                    aside, nav, button, .no-print {
                        display: none !important;
                    }
                    /* Ensure print layout spans whole page */
                    main, .print-area {
                        margin: 0 !important;
                        padding: 0 !important;
                        border: none !important;
                        box-shadow: none !important;
                        width: 100% !important;
                    }
                }
            `}} />

            <div className="max-w-4xl mx-auto space-y-6 print-area">
                <div className="flex items-center justify-between gap-4 no-print">
                    <button onClick={() => navigate('/incidentes')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver a la Bitácora
                    </button>
                    <button
                        onClick={handlePrint}
                        className="px-4 py-2 bg-gray-900 hover:bg-gray-800 text-white rounded-lg font-bold text-sm transition-colors flex items-center gap-2"
                    >
                        <Printer className="w-4 h-4" />
                        Imprimir Expediente
                    </button>
                </div>

                {/* Report Header for Print / View */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className="bg-red-950 text-white p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div className="space-y-1">
                            <div className="flex items-center gap-2">
                                <ShieldAlert className="w-6 h-6 text-red-400" />
                                <span className="text-xs uppercase font-extrabold tracking-wider text-red-300">
                                    ACTA DE INCIDENTE INTERNO DE SEGURIDAD
                                </span>
                            </div>
                            <h1 className="text-2xl font-black">{incident.code}</h1>
                        </div>
                        <div className="text-right">
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-500 text-gray-950 uppercase tracking-wider">
                                {incident.status === 'EN_INVESTIGACION' ? 'En Investigación' : incident.status}
                            </span>
                        </div>
                    </div>

                    <div className="p-6 space-y-6">
                        {/* Meta information grid */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 border-b border-gray-100 pb-6">
                            <div className="flex items-center gap-3">
                                <Home className="w-5 h-5 text-gray-400 shrink-0" />
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Celda Clausurada</p>
                                    <p className="text-sm font-bold text-gray-800">Celda {incident.cellIdentifier}</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-3">
                                <Calendar className="w-5 h-5 text-gray-400 shrink-0" />
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Fecha del Hallazgo</p>
                                    <p className="text-sm font-bold text-gray-800">{formatDateTime(incident.incidentDate)}</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-3">
                                <User className="w-5 h-5 text-gray-400 shrink-0" />
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Oficial Reportante</p>
                                    <p className="text-sm font-bold text-gray-800">{incident.reporter}</p>
                                </div>
                            </div>
                        </div>

                        {/* Event details narration */}
                        <div className="space-y-2">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider flex items-center gap-2">
                                <FileText className="w-4 h-4 text-gray-400" /> Relato de los Hechos
                            </h3>
                            <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 text-sm text-gray-700 leading-relaxed whitespace-pre-wrap font-serif">
                                {incident.description}
                            </div>
                        </div>

                        {/* Involved participants */}
                        <div className="space-y-3">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider flex items-center gap-2">
                                <User className="w-4 h-4 text-gray-400" /> Sujetos Involucrados / Testigos
                            </h3>
                            <div className="border border-gray-200 rounded-xl overflow-hidden shadow-sm">
                                <table className="w-full text-left border-collapse">
                                    <thead>
                                        <tr className="bg-gray-50 text-gray-700 text-[10px] font-black uppercase tracking-wider border-b border-gray-200">
                                            <th className="p-3">Nombre</th>
                                            <th className="p-3">Cédula</th>
                                            <th className="p-3">Rol en Evento</th>
                                            <th className="p-3">Estado Reportado</th>
                                            <th className="p-3">Observaciones de Campo</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-100 text-xs">
                                        {incident.participants.map(p => (
                                            <tr key={p.id} className="hover:bg-gray-50/50">
                                                <td className="p-3 font-bold text-gray-900">{p.inmateName}</td>
                                                <td className="p-3 text-gray-500 font-mono">{p.inmateCedula}</td>
                                                <td className="p-3 font-semibold">
                                                    <span className={`px-2 py-0.5 rounded text-[10px] uppercase tracking-wider font-extrabold ${p.role === 'FALLECIDO' ? 'bg-red-950 text-red-200' : 'bg-gray-100 text-gray-700'}`}>
                                                        {p.role}
                                                    </span>
                                                </td>
                                                <td className="p-3">
                                                    <span className={`px-2 py-0.5 rounded border text-[10px] uppercase font-bold ${getStatusStyle(p.initialStatus)}`}>
                                                        {p.initialStatus === 'TRASLADADO_ENFERMERIA' ? 'Enfermería' : p.initialStatus}
                                                    </span>
                                                </td>
                                                <td className="p-3 text-gray-600 italic">{p.comments || '-'}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        {/* Signatures block for printing */}
                        <div className="hidden print:grid grid-cols-2 gap-12 mt-16 pt-12 border-t border-dashed border-gray-300">
                            <div className="text-center space-y-8">
                                <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                <p className="text-xs font-bold text-gray-800">{incident.reporter}</p>
                                <p className="text-[10px] text-gray-400 uppercase font-bold">Oficial Reportante</p>
                            </div>
                            <div className="text-center space-y-8">
                                <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                <p className="text-xs font-bold text-gray-800">Firma Autorizada</p>
                                <p className="text-[10px] text-gray-400 uppercase font-bold">Forense / Fiscalía</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </SidebarLayout>
    )
}
