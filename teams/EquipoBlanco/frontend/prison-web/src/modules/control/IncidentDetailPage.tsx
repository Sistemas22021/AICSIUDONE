import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
    ArrowLeft, ShieldAlert, Printer, User, Calendar, Home, FileText,
    CheckCircle, XCircle, AlertTriangle, Gavel, Lock, Unlock
} from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface IncidentParticipantDto {
    id: string
    inmateId: string
    inmateName: string
    inmateCedula: string
    role: string
    initialStatus: string
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
    // Campos de conclusión
    conclusionType?: string
    causaMedicaDefinitiva?: string
    autopsiaProtocolo?: string
    fiscaliaExpediente?: string
    responsableInmateId?: string
    responsableInmateName?: string
    responsablePersonal?: string
    responsableNoAplica?: boolean
    concludedAt?: string
    concludedBy?: string
    additionalSentenceYears?: number
    additionalSentenceMonths?: number
}

const CONCLUSION_TYPES = ['HOMICIDIO', 'SUICIDIO', 'ACCIDENTAL', 'NATURAL']
const CONCLUSION_LABELS: Record<string, string> = {
    HOMICIDIO: 'Homicidio',
    SUICIDIO: 'Suicidio',
    ACCIDENTAL: 'Accidental',
    NATURAL: 'Muerte Natural',
}

export default function IncidentDetailPage() {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const [incident, setIncident] = useState<IncidentData | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    // Modal state
    const [showModal, setShowModal] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [formError, setFormError] = useState('')
    const [form, setForm] = useState({
        conclusionType: '',
        causaMedicaDefinitiva: '',
        autopsiaProtocolo: '',
        fiscaliaExpediente: '',
        responsableInmateId: '',
        responsableNoAplica: false,
        additionalSentenceYears: 0,
        additionalSentenceMonths: 0,
    })

    let role = ''
    let username = 'Supervisor'
    try {
        const mockUser = JSON.parse(localStorage.getItem('mock_user') || '{}')
        role = mockUser.role || ''
        username = mockUser.username || 'Supervisor'
    } catch (e) {
        console.error(e)
    }

    useEffect(() => {
        const loadIncident = async () => {
            if (!id) return
            setLoading(true)
            setError('')
            try {
                const res = await api.get<IncidentData>(`/incidents/${id}`)
                setIncident(res.data)
            } catch (err) {
                console.error(err)
                setError('Error al cargar el detalle del incidente.')
            } finally {
                setLoading(false)
            }
        }
        loadIncident()
    }, [id])

    const formatDateTime = (dateStr?: string) => {
        if (!dateStr) return '-'
        try {
            const date = new Date(dateStr)
            return date.toLocaleString('es-ES', {
                day: '2-digit', month: '2-digit', year: 'numeric',
                hour: '2-digit', minute: '2-digit'
            })
        } catch {
            return dateStr
        }
    }

    const getStatusStyle = (status: string) => {
        switch (status) {
            case 'FALLECIDO': return 'bg-red-100 text-red-800 border-red-200'
            case 'LESIONADO': return 'bg-amber-100 text-amber-800 border-amber-200'
            case 'TRASLADADO_ENFERMERIA': return 'bg-blue-100 text-blue-800 border-blue-200'
            default: return 'bg-emerald-100 text-emerald-800 border-emerald-200'
        }
    }

    const isClosed = incident?.status === 'CERRADO'
    const canConclude = !isClosed && incident?.status === 'EN_INVESTIGACION'
    const formIsValid = form.autopsiaProtocolo.trim() !== '' && form.fiscaliaExpediente.trim() !== '' && form.conclusionType !== ''

    const handleFormChange = (field: string, value: string | boolean | number) => {
        setForm(prev => ({ ...prev, [field]: value }))
    }

    const handleConclude = async () => {
        if (!formIsValid || !id) return
        setSubmitting(true)
        setFormError('')
        try {
            const hasResponsable = !form.responsableNoAplica && !!form.responsableInmateId
            const payload = {
                conclusionType: form.conclusionType,
                causaMedicaDefinitiva: form.causaMedicaDefinitiva || null,
                autopsiaProtocolo: form.autopsiaProtocolo,
                fiscaliaExpediente: form.fiscaliaExpediente,
                responsableInmateId: hasResponsable ? form.responsableInmateId : null,
                responsableNoAplica: form.responsableNoAplica,
                additionalSentenceYears: hasResponsable ? (parseInt(String(form.additionalSentenceYears)) || 0) : null,
                additionalSentenceMonths: hasResponsable ? (parseInt(String(form.additionalSentenceMonths)) || 0) : null,
            }
            const res = await api.put<IncidentData>(`/incidents/${id}/conclude`, payload)
            setIncident(res.data)
            setShowModal(false)
        } catch (err: any) {
            const msg = err?.response?.data?.message || err?.response?.data || 'Error al concluir la investigación.'
            setFormError(typeof msg === 'string' ? msg : 'Error al concluir la investigación.')
        } finally {
            setSubmitting(false)
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
            <style dangerouslySetInnerHTML={{__html: `
                @media print {
                    body { background-color: white !important; color: black !important; }
                    aside, nav, button, .no-print { display: none !important; }
                    main, .print-area { margin: 0 !important; padding: 0 !important; border: none !important; box-shadow: none !important; width: 100% !important; }
                }
            `}} />

            <div className="max-w-4xl mx-auto space-y-6 print-area">
                {/* Top bar */}
                <div className="flex items-center justify-between gap-4 no-print">
                    <button onClick={() => navigate('/incidentes')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver a la Bitácora
                    </button>
                    <div className="flex items-center gap-3">
                        {canConclude && (
                            <button
                                id="btn-conclude-incident"
                                onClick={() => setShowModal(true)}
                                className="px-4 py-2 bg-emerald-700 hover:bg-emerald-800 text-white rounded-lg font-bold text-sm transition-colors flex items-center gap-2"
                            >
                                <Gavel className="w-4 h-4" />
                                Concluir Investigación
                            </button>
                        )}
                        <button
                            onClick={() => window.print()}
                            className="px-4 py-2 bg-gray-900 hover:bg-gray-800 text-white rounded-lg font-bold text-sm transition-colors flex items-center gap-2"
                        >
                            <Printer className="w-4 h-4" />
                            Imprimir Expediente
                        </button>
                    </div>
                </div>

                {/* Report Header */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className={`text-white p-6 flex flex-col md:flex-row md:items-center justify-between gap-4 ${isClosed ? 'bg-gray-800' : 'bg-red-950'}`}>
                        <div className="space-y-1">
                            <div className="flex items-center gap-2">
                                <ShieldAlert className={`w-6 h-6 ${isClosed ? 'text-gray-400' : 'text-red-400'}`} />
                                <span className={`text-xs uppercase font-extrabold tracking-wider ${isClosed ? 'text-gray-300' : 'text-red-300'}`}>
                                    ACTA DE INCIDENTE INTERNO DE SEGURIDAD
                                </span>
                            </div>
                            <h1 className="text-2xl font-black">{incident.code}</h1>
                        </div>
                        <div className="text-right flex flex-col items-end gap-2">
                            {isClosed ? (
                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-400 text-gray-950 uppercase tracking-wider">
                                    <CheckCircle className="w-3.5 h-3.5" /> Cerrado
                                </span>
                            ) : (
                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-500 text-gray-950 uppercase tracking-wider">
                                    <AlertTriangle className="w-3.5 h-3.5" /> En Investigación
                                </span>
                            )}
                        </div>
                    </div>

                    <div className="p-6 space-y-6">
                        {/* Meta information grid */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 border-b border-gray-100 pb-6">
                            <div className="flex items-center gap-3">
                                <Home className="w-5 h-5 text-gray-400 shrink-0" />
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Celda del Incidente</p>
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

                        {/* Event description */}
                        <div className="space-y-2">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider flex items-center gap-2">
                                <FileText className="w-4 h-4 text-gray-400" /> Relato de los Hechos
                            </h3>
                            <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 text-sm text-gray-700 leading-relaxed whitespace-pre-wrap font-serif">
                                {incident.description}
                            </div>
                        </div>

                        {/* Participants */}
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

                        {/* ── CONCLUSIÓN DE LA INVESTIGACIÓN (solo si está cerrado) ── */}
                        {isClosed && (
                            <div className="space-y-4 border-t border-gray-100 pt-6">
                                <h3 className="text-xs font-black text-emerald-700 uppercase tracking-wider flex items-center gap-2">
                                    <CheckCircle className="w-4 h-4 text-emerald-600" /> Conclusión Oficial de la Investigación
                                </h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-200 space-y-1">
                                        <p className="text-[10px] text-emerald-600 font-black uppercase">Tipo de Conclusión</p>
                                        <p className="text-sm font-bold text-emerald-900">{CONCLUSION_LABELS[incident.conclusionType ?? ''] ?? incident.conclusionType ?? '-'}</p>
                                    </div>
                                    <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-200 space-y-1">
                                        <p className="text-[10px] text-emerald-600 font-black uppercase">Causa Médica Definitiva</p>
                                        <p className="text-sm font-bold text-emerald-900">{incident.causaMedicaDefinitiva || '-'}</p>
                                    </div>
                                    <div className="p-4 bg-blue-50 rounded-xl border border-blue-200 space-y-1">
                                        <p className="text-[10px] text-blue-600 font-black uppercase">N° Protocolo de Autopsia</p>
                                        <p className="text-sm font-bold text-blue-900 font-mono">{incident.autopsiaProtocolo || '-'}</p>
                                    </div>
                                    <div className="p-4 bg-blue-50 rounded-xl border border-blue-200 space-y-1">
                                        <p className="text-[10px] text-blue-600 font-black uppercase">N° Expediente Fiscalía</p>
                                        <p className="text-sm font-bold text-blue-900 font-mono">{incident.fiscaliaExpediente || '-'}</p>
                                    </div>
                                    <div className="p-4 bg-gray-50 rounded-xl border border-gray-200 space-y-1">
                                        <p className="text-[10px] text-gray-500 font-black uppercase">Responsabilidad Penal</p>
                                        <p className="text-sm font-bold text-gray-800">
                                            {incident.responsableNoAplica
                                                ? 'No aplica (sin responsable directo)'
                                                : (incident.responsableInmateName || incident.responsablePersonal || (incident.responsableInmateId ? `ID: ${incident.responsableInmateId}` : '-'))}
                                        </p>
                                    </div>
                                    <div className="p-4 bg-gray-50 rounded-xl border border-gray-200 space-y-1">
                                        <p className="text-[10px] text-gray-500 font-black uppercase">Investigación cerrada por</p>
                                        <p className="text-sm font-bold text-gray-800">{incident.concludedBy || '-'}</p>
                                        <p className="text-[10px] text-gray-400">{formatDateTime(incident.concludedAt)}</p>
                                    </div>
                                </div>

                                {/* Condena adicional imputada */}
                                {!incident.responsableNoAplica && incident.responsableInmateId && (incident.additionalSentenceYears || incident.additionalSentenceMonths) && (
                                    <div className="flex items-start gap-3 bg-red-50 border border-red-200 rounded-xl px-4 py-3">
                                        <Gavel className="w-5 h-5 text-red-600 mt-0.5 shrink-0" />
                                        <div>
                                            <p className="text-xs font-black text-red-700 uppercase tracking-wider mb-0.5">Condena Adicional Imputada</p>
                                            <p className="text-sm font-bold text-red-900">
                                                <span className="font-black">{incident.responsableInmateName}</span> — se le añadieron{' '}
                                                {incident.additionalSentenceYears ? `${incident.additionalSentenceYears} año(s)` : ''}{' '}
                                                {incident.additionalSentenceMonths ? `${incident.additionalSentenceMonths} mes(es)` : ''} a su condena.
                                            </p>
                                        </div>
                                    </div>
                                )}

                                {/* Celda desbloqueada notice */}
                                <div className="flex items-center gap-2 text-xs text-emerald-700 bg-emerald-50 border border-emerald-200 rounded-lg px-3 py-2">
                                    <Unlock className="w-4 h-4 shrink-0" />
                                    <span>Celda <strong>{incident.cellIdentifier}</strong> fue desbloqueada automáticamente al cerrar esta investigación.</span>
                                </div>
            
                            </div>
                        )}

                        {/* Signatures block for printing */}
                        <div className="hidden print:grid grid-cols-2 gap-12 mt-16 pt-12 border-t border-dashed border-gray-300">
                            <div className="text-center space-y-8">
                                <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                <p className="text-xs font-bold text-gray-800">{incident.reporter}</p>
                                <p className="text-[10px] text-gray-400 uppercase font-bold">Oficial Reportante</p>
                            </div>
                            <div className="text-center space-y-8">
                                <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                <p className="text-xs font-bold text-gray-800">{incident.concludedBy || 'Firma Autorizada'}</p>
                                <p className="text-[10px] text-gray-400 uppercase font-bold">Forense / Fiscalía</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* ── MODAL DE CIERRE DE INVESTIGACIÓN ── */}
            {showModal && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 no-print">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden">
                        {/* Modal Header */}
                        <div className="bg-gray-900 text-white px-6 py-4 flex items-center justify-between">
                            <div className="flex items-center gap-2">
                                <Gavel className="w-5 h-5 text-emerald-400" />
                                <h2 className="text-sm font-black uppercase tracking-wide">Concluir Investigación — {incident.code}</h2>
                            </div>
                            <button onClick={() => setShowModal(false)} className="text-gray-400 hover:text-white transition-colors">
                                <XCircle className="w-5 h-5" />
                            </button>
                        </div>

                        {/* Modal Body */}
                        <div className="p-6 space-y-5 max-h-[75vh] overflow-y-auto">
                            {/* Section 1: Causa oficial */}
                            <div>
                                <p className="text-[10px] font-black text-gray-400 uppercase tracking-wider mb-3">1. Causa de Muerte Oficial</p>
                                <div className="space-y-3">
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 mb-1">Tipo de Conclusión <span className="text-red-500">*</span></label>
                                        <select
                                            id="select-conclusion-type"
                                            value={form.conclusionType}
                                            onChange={e => handleFormChange('conclusionType', e.target.value)}
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                        >
                                            <option value="">Seleccionar...</option>
                                            {CONCLUSION_TYPES.map(t => (
                                                <option key={t} value={t}>{CONCLUSION_LABELS[t]}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 mb-1">Causa Médica Definitiva</label>
                                        <textarea
                                            id="input-causa-medica"
                                            value={form.causaMedicaDefinitiva}
                                            onChange={e => handleFormChange('causaMedicaDefinitiva', e.target.value)}
                                            rows={3}
                                            placeholder="Ej. Asfixia mecánica por ahorcamiento..."
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Section 2: Sustento legal */}
                            <div className="border-t border-gray-100 pt-4">
                                <p className="text-[10px] font-black text-blue-500 uppercase tracking-wider mb-3">2. Sustento Legal Externo <span className="text-red-500 text-xs ml-1 normal-case font-bold">(ambos obligatorios)</span></p>
                                <div className="space-y-3">
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 mb-1">N° Protocolo de Autopsia <span className="text-red-500">*</span></label>
                                        <input
                                            id="input-autopsia-protocolo"
                                            type="text"
                                            value={form.autopsiaProtocolo}
                                            onChange={e => handleFormChange('autopsiaProtocolo', e.target.value)}
                                            placeholder="Ej. AUT-2026-00123"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-bold text-gray-700 mb-1">N° Expediente Fiscalía <span className="text-red-500">*</span></label>
                                        <input
                                            id="input-fiscalia-expediente"
                                            type="text"
                                            value={form.fiscaliaExpediente}
                                            onChange={e => handleFormChange('fiscaliaExpediente', e.target.value)}
                                            placeholder="Ej. FIS-2026-00456"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Section 3: Responsabilidad */}
                            <div className="border-t border-gray-100 pt-4">
                                <p className="text-[10px] font-black text-gray-400 uppercase tracking-wider mb-3">3. Responsabilidad Penal</p>
                                <div className="space-y-3">
                                    <label className="flex items-center gap-2 cursor-pointer">
                                        <input
                                            id="check-no-aplica"
                                            type="checkbox"
                                            checked={form.responsableNoAplica}
                                            onChange={e => handleFormChange('responsableNoAplica', e.target.checked)}
                                            className="rounded border-gray-300 text-emerald-600 focus:ring-emerald-500"
                                        />
                                        <span className="text-xs font-bold text-gray-700">No aplica — sin responsable directo (suicidio / accidente)</span>
                                    </label>
                                    {!form.responsableNoAplica && (
                                        <div className="space-y-3">
                                            {/* Selector de cohabitantes del incidente */}
                                            <div>
                                                <label className="block text-xs font-bold text-gray-700 mb-1">Interno Responsable</label>
                                                <select
                                                    id="select-responsable-inmate"
                                                    value={form.responsableInmateId}
                                                    onChange={e => handleFormChange('responsableInmateId', e.target.value)}
                                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
                                                >
                                                    <option value="">Seleccionar de la lista de involucrados...</option>
                                                    {incident?.participants
                                                        .filter(p => p.role === 'COHABITANTE')
                                                        .map(p => (
                                                            <option key={p.inmateId} value={p.inmateId}>
                                                                {p.inmateName} — Céd. {p.inmateCedula} ({p.initialStatus})
                                                            </option>
                                                        ))}
                                                </select>
                                                <p className="text-[10px] text-gray-400 mt-1">Solo se muestran los internos registrados como cohabitantes de celda en este incidente.</p>
                                            </div>

                                            {/* Condena adicional, solo si se seleccionó un responsable */}
                                            {form.responsableInmateId && (
                                                <div className="bg-red-50 border border-red-200 rounded-xl p-4 space-y-3">
                                                    <p className="text-[10px] font-black text-red-600 uppercase tracking-wider">⚖️ Condena Adicional a Imputar</p>
                                                    <div className="grid grid-cols-2 gap-3">
                                                        <div>
                                                            <label className="block text-xs font-bold text-gray-700 mb-1">Años adicionales</label>
                                                            <input
                                                                id="input-sentence-years"
                                                                type="number"
                                                                min="0"
                                                                max="50"
                                                                value={form.additionalSentenceYears}
                                                                onChange={e => handleFormChange('additionalSentenceYears', e.target.value)}
                                                                className="w-full border border-red-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400 font-mono text-center"
                                                            />
                                                        </div>
                                                        <div>
                                                            <label className="block text-xs font-bold text-gray-700 mb-1">Meses adicionales</label>
                                                            <input
                                                                id="input-sentence-months"
                                                                type="number"
                                                                min="0"
                                                                max="11"
                                                                value={form.additionalSentenceMonths}
                                                                onChange={e => handleFormChange('additionalSentenceMonths', e.target.value)}
                                                                className="w-full border border-red-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400 font-mono text-center"
                                                            />
                                                        </div>
                                                    </div>
                                                    <p className="text-[10px] text-red-600 leading-relaxed">Este tiempo se sumará automáticamente a la condena vigente del interno y se recalculará su fecha de egreso estimada.</p>
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Legal warning */}
                            <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 flex items-start gap-2">
                                <Lock className="w-4 h-4 text-amber-600 mt-0.5 shrink-0" />
                                <p className="text-xs text-amber-800 leading-relaxed">
                                    <strong>Aviso Legal:</strong> Sin el Protocolo de Autopsia y el Expediente de la Fiscalía, el sistema no puede autorizar el cierre de esta investigación. Al confirmar, la celda será desbloqueada automáticamente.
                                </p>
                            </div>

                            {formError && (
                                <div className="bg-red-50 border border-red-200 rounded-lg p-3 text-xs text-red-700 font-medium">
                                    {formError}
                                </div>
                            )}
                        </div>

                        {/* Modal Footer */}
                        <div className="px-6 py-4 border-t border-gray-100 flex items-center justify-end gap-3 bg-gray-50">
                            <button
                                onClick={() => setShowModal(false)}
                                className="px-4 py-2 text-sm font-bold text-gray-600 hover:text-gray-900 transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                id="btn-confirm-conclude"
                                onClick={handleConclude}
                                disabled={!formIsValid || submitting}
                                className="px-5 py-2 bg-emerald-700 hover:bg-emerald-800 disabled:bg-gray-300 disabled:cursor-not-allowed text-white rounded-lg font-bold text-sm transition-colors flex items-center gap-2"
                            >
                                {submitting ? (
                                    <span className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent" />
                                ) : (
                                    <CheckCircle className="w-4 h-4" />
                                )}
                                Cerrar Investigación
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </SidebarLayout>
    )
}
