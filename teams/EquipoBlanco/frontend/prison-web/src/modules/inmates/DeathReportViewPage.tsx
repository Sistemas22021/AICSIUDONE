import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Printer, FileText, Activity, User } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    secondName?: string
    firstLastname: string
    secondLastname?: string
    birthDate: string
    eyeColor?: string
    hairColor?: string
    bodyBuild?: string
    heightCm?: number
    weightKg?: number
    distinguishingMarks?: string
    admissionDate: string
    dischargeDate?: string
    motivoEgreso?: string
    observacionesEgreso?: string
    cellIdentifier?: string
}

interface DeathReportData {
    id: string
    inmateId: string
    deceaseType: string // NATURAL, NO_NATURAL
    dateTimeFound: string
    description: string
}

export default function DeathReportViewPage() {
    const { inmateId } = useParams<{ inmateId: string }>()
    const navigate = useNavigate()
    const [inmate, setInmate] = useState<InmateData | null>(null)
    const [deathReport, setDeathReport] = useState<DeathReportData | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        const loadReport = async () => {
            if (!inmateId) return
            setLoading(true)
            setError('')
            try {
                const inmateRes = await api.get<InmateData>(`/inmates/${inmateId}`)
                setInmate(inmateRes.data)

                const reportRes = await api.get<DeathReportData>(`/inmates/${inmateId}/death-report`)
                setDeathReport(reportRes.data)
            } catch (err) {
                console.error(err)
                setError('Error al obtener el informe de deceso. Asegúrese de que haya sido registrado.')
            } finally {
                setLoading(false)
            }
        }
        loadReport()
    }, [inmateId])

    const handlePrint = () => {
        window.print()
    }

    const formatDate = (dateStr?: string) => {
        if (!dateStr) return '-'
        try {
            const date = new Date(dateStr)
            return date.toLocaleDateString('es-ES', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            })
        } catch {
            return dateStr
        }
    }

    const formatDateTime = (dateStr?: string) => {
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

    if (loading) {
        return (
            <SidebarLayout>
                <div className="flex items-center justify-center h-64">
                    <p className="text-gray-500 font-medium">Cargando acta e informe médico-legal...</p>
                </div>
            </SidebarLayout>
        )
    }

    if (error || !inmate || !deathReport) {
        return (
            <SidebarLayout>
                <div className="max-w-4xl mx-auto space-y-6">
                    <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4 transition-colors">
                        <ArrowLeft className="w-4 h-4" /> Volver
                    </button>
                    <div className="p-4 bg-red-50 text-red-700 rounded-xl border border-red-100 text-sm font-medium">
                        {error || 'El informe de deceso no pudo ser cargado.'}
                    </div>
                </div>
            </SidebarLayout>
        )
    }

    const fullName = `${inmate.firstName} ${inmate.secondName || ''} ${inmate.firstLastname} ${inmate.secondLastname || ''}`.replace(/\s+/g, ' ').trim()

    return (
        <SidebarLayout>
            <style dangerouslySetInnerHTML={{__html: `
                @media print {
                    body {
                        background-color: white !important;
                        color: black !important;
                    }
                    aside, nav, button, .no-print {
                        display: none !important;
                    }
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
                    <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver al Expediente
                    </button>
                    <button
                        onClick={handlePrint}
                        className="px-4 py-2 bg-gray-900 hover:bg-gray-800 text-white rounded-lg font-bold text-sm transition-colors flex items-center gap-2"
                    >
                        <Printer className="w-4 h-4" />
                        Imprimir Acta de Defunción
                    </button>
                </div>

                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    {/* Header */}
                    <div className="bg-gray-950 text-white p-6 text-center space-y-2 border-b border-gray-800">
                        <h2 className="text-xs font-black tracking-widest text-gray-400 uppercase">
                            DIRECCIÓN GENERAL DE ESTABLECIMIENTOS PENITENCIARIOS
                        </h2>
                        <h1 className="text-2xl font-black tracking-tight">
                            INFORME TÉCNICO DE DECESO DE RECLUSO
                        </h1>
                        <p className="text-[10px] text-gray-500">
                            DOCUMENTO MÉDICO-LEGAL INTERNO PARA USO ADMINISTRATIVO
                        </p>
                    </div>

                    <div className="p-8 space-y-8">
                        {/* Status bar */}
                        <div className="flex justify-between items-center bg-gray-50 p-4 rounded-xl border border-gray-100">
                            <div>
                                <p className="text-[10px] text-gray-400 font-bold uppercase">Tipo de Deceso</p>
                                <span className={`inline-flex items-center gap-1.5 mt-1 px-2.5 py-0.5 rounded text-xs font-bold ${deathReport.deceaseType === 'NATURAL' ? 'bg-emerald-50 text-emerald-800 border border-emerald-200' : 'bg-red-50 text-red-800 border border-red-200'} uppercase`}>
                                    {deathReport.deceaseType === 'NATURAL' ? 'Natural' : 'No Natural'}
                                </span>
                            </div>
                            <div className="text-right">
                                <p className="text-[10px] text-gray-400 font-bold uppercase">Fecha y Hora de Hallazgo</p>
                                <p className="text-sm font-bold text-gray-800 mt-1">{formatDateTime(deathReport.dateTimeFound)}</p>
                            </div>
                        </div>

                        {/* Section 1: Inmate Identity */}
                        <div className="space-y-4">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-2">
                                <User className="w-4 h-4 text-gray-400" /> 1. IDENTIDAD DEL FALLECIDO
                            </h3>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-sm">
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Apellidos y Nombres</p>
                                    <p className="font-bold text-gray-900 mt-0.5">{fullName}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Cédula de Identidad</p>
                                    <p className="font-semibold text-gray-800 mt-0.5 font-mono">{inmate.cedula}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Fecha de Nacimiento</p>
                                    <p className="text-gray-800 mt-0.5">{formatDate(inmate.birthDate)}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Fecha de Ingreso al Penal</p>
                                    <p className="text-gray-800 mt-0.5">{formatDate(inmate.admissionDate)}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Celda de Origen</p>
                                    <p className="text-gray-800 mt-0.5">Celda {inmate.cellIdentifier || 'Sin asignar'}</p>
                                </div>
                            </div>
                        </div>

                        {/* Section 2: Biometrics */}
                        <div className="space-y-4">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-2">
                                <Activity className="w-4 h-4 text-gray-400" /> 2. CARACTERÍSTICAS BIOMÉTRICAS
                            </h3>
                            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 text-sm">
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Estatura</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.heightCm ? `${inmate.heightCm} cm` : 'No registrado'}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Peso Aproximado</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.weightKg ? `${inmate.weightKg} kg` : 'No registrado'}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Color de Ojos</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.eyeColor || 'No registrado'}</p>
                                </div>
                                <div>
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Tipo de Cabello</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.hairColor || 'No registrado'}</p>
                                </div>
                                <div className="md:col-span-2">
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Complexión Física</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.bodyBuild || 'No registrado'}</p>
                                </div>
                                <div className="md:col-span-2">
                                    <p className="text-[10px] text-gray-400 font-bold uppercase">Señas Particulares / Cicatrices</p>
                                    <p className="text-gray-800 mt-0.5">{inmate.distinguishingMarks || 'Ninguna registrada'}</p>
                                </div>
                            </div>
                        </div>

                        {/* Section 3: relato de deceso */}
                        <div className="space-y-4">
                            <h3 className="text-xs font-black text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-2">
                                <FileText className="w-4 h-4 text-gray-400" /> 3. INFORME DE HALLAZGO Y RELATO DE LOS HECHOS
                            </h3>
                            <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 text-sm text-gray-700 leading-relaxed font-serif whitespace-pre-wrap">
                                {deathReport.description}
                            </div>
                        </div>

                        {/* Legal Note / Signatures */}
                        <div className="space-y-4 pt-4 border-t border-gray-100">
                            <p className="text-[10px] text-gray-400 leading-relaxed italic text-center">
                                Certifico que los datos descritos en esta acta de deceso corresponden a los registros biométricos del sistema AIC-SIU-DONE y que el deceso ha sido registrado de acuerdo a los protocolos penitenciarios y de derechos humanos vigentes.
                            </p>
                            
                            <div className="grid grid-cols-2 gap-12 mt-12 pt-8">
                                <div className="text-center space-y-6">
                                    <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                    <p className="text-xs font-bold text-gray-800">Firma Supervisor de Turno</p>
                                    <p className="text-[10px] text-gray-400 uppercase">Establecimiento Penitenciario</p>
                                </div>
                                <div className="text-center space-y-6">
                                    <div className="border-b border-gray-400 w-48 mx-auto h-12"></div>
                                    <p className="text-xs font-bold text-gray-800">Firma Médico Forense / Testigo</p>
                                    <p className="text-[10px] text-gray-400 uppercase">Matrícula / Cédula</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </SidebarLayout>
    )
}
