import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Clock, ArrowLeft, AlertCircle, CheckCircle, Users, MapPin, Calendar, RefreshCw } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    firstLastname: string
    secondName?: string
    secondLastname?: string
    cellIdentifier?: string
    status: string
    motivoSalidaTemporal?: string
    fechaSalidaTemporal?: string
    fechaRetornoEstimada?: string
}

export default function TemporaryReturnPage() {
    const navigate = useNavigate()
    const [inmates, setInmates] = useState<InmateData[]>([])
    const [selectedInmate, setSelectedInmate] = useState<InmateData | null>(null)
    
    // Default return time to now (local format YYYY-MM-DDTHH:MM)
    const [fechaRetorno, setFechaRetorno] = useState(new Date().toISOString().slice(0, 16))
    const [observaciones, setObservaciones] = useState('')
    const [loading, setLoading] = useState(false)
    const [loadingList, setLoadingList] = useState(true)
    const [error, setError] = useState('')

    const loadInmates = async () => {
        setLoadingList(true)
        setError('')
        try {
            const res = await api.get<InmateData[]>('/inmates/by-status/ACTIVO_SALIDA_TEMPORAL')
            setInmates(res.data)
        } catch (err) {
            console.error('Error cargando reclusos en salida temporal:', err)
            setError('Error al cargar la lista de reclusos en salida temporal.')
        } finally {
            setLoadingList(false)
        }
    }

    useEffect(() => {
        loadInmates()
    }, [])

    const formatDateTime = (isoStr?: string) => {
        if (!isoStr) return '—'
        try {
            const date = new Date(isoStr)
            return date.toLocaleString('es-VE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: true
            })
        } catch {
            return isoStr
        }
    }

    const isOverdue = (estimatedReturn?: string) => {
        if (!estimatedReturn) return false
        return new Date(estimatedReturn) < new Date()
    }

    const handleSelectInmate = (inmate: InmateData) => {
        setSelectedInmate(inmate)
        setError('')
        setFechaRetorno(new Date().toISOString().slice(0, 16))
        setObservaciones('')
    }

    const handleReturn = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!selectedInmate) return

        if (!confirm(`¿Está seguro de registrar el retorno de ${selectedInmate.firstName} ${selectedInmate.firstLastname}?`)) {
            return
        }

        setLoading(true)
        setError('')
        try {
            const retornoISO = fechaRetorno.length === 16 ? fechaRetorno + ':00' : fechaRetorno

            await api.post(`/inmates/${selectedInmate.id}/temporary-return`, {
                fechaRetorno: retornoISO,
                observaciones
            })
            alert('Retorno temporal registrado exitosamente. El recluso ha sido devuelto a su celda asignada.')
            setSelectedInmate(null)
            loadInmates() // Refresh the list
        } catch (err: any) {
            console.error('Error al registrar retorno:', err)
            const message = err.response?.data?.message || err.response?.data?.error || 'Error al registrar el retorno.'
            setError(message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <SidebarLayout>
            <div className="max-w-4xl mx-auto space-y-6">
                <div>
                    <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver al Dashboard
                    </button>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <CheckCircle className="w-8 h-8 text-emerald-600" />
                        Registrar Retorno de Recluso
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Seleccione un recluso de la lista de salidas temporales activas para registrar su retorno al penal.
                    </p>
                </div>

                {!selectedInmate ? (
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                        <div className="bg-gradient-to-r from-emerald-50 to-teal-50 border-b border-emerald-100 p-5 flex items-center justify-between">
                            <div className="flex items-center gap-3">
                                <Users className="w-5 h-5 text-emerald-700" />
                                <div>
                                    <h2 className="text-lg font-bold text-emerald-900">Reclusos en Salida Temporal</h2>
                                    <p className="text-xs text-emerald-600">{inmates.length} recluso(s) fuera del penal actualmente</p>
                                </div>
                            </div>
                            <button 
                                onClick={loadInmates} 
                                disabled={loadingList}
                                className="flex items-center gap-2 px-3 py-1.5 text-sm bg-white border border-emerald-200 text-emerald-700 rounded-lg hover:bg-emerald-50 transition-colors disabled:opacity-50"
                            >
                                <RefreshCw className={`w-4 h-4 ${loadingList ? 'animate-spin' : ''}`} />
                                Actualizar
                            </button>
                        </div>

                        <div className="p-4">
                            {error && (
                                <div className="p-3 bg-red-50 text-red-700 rounded-lg text-sm border border-red-100 flex items-center gap-2 mb-4">
                                    <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0" />
                                    {error}
                                </div>
                            )}

                            {loadingList ? (
                                <div className="flex items-center justify-center py-12">
                                    <div className="flex flex-col items-center gap-3">
                                        <RefreshCw className="w-8 h-8 text-emerald-500 animate-spin" />
                                        <p className="text-sm text-gray-500">Cargando reclusos en salida temporal...</p>
                                    </div>
                                </div>
                            ) : inmates.length === 0 ? (
                                <div className="flex flex-col items-center justify-center py-12 text-center">
                                    <CheckCircle className="w-12 h-12 text-emerald-300 mb-3" />
                                    <p className="text-lg font-semibold text-gray-700">Todos los reclusos están en el penal</p>
                                    <p className="text-sm text-gray-500 mt-1">No hay reclusos con salida temporal activa en este momento.</p>
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {inmates.map(inmate => {
                                        const overdue = isOverdue(inmate.fechaRetornoEstimada)
                                        return (
                                            <div 
                                                key={inmate.id} 
                                                className={`p-4 border rounded-xl transition-all cursor-pointer hover:shadow-md ${
                                                    overdue 
                                                        ? 'border-red-200 bg-red-50/50 hover:border-red-400' 
                                                        : 'border-gray-100 bg-white hover:border-emerald-300 hover:bg-emerald-50/30'
                                                }`}
                                                onClick={() => handleSelectInmate(inmate)}
                                            >
                                                <div className="flex items-start justify-between">
                                                    <div className="flex-1">
                                                        <div className="flex items-center gap-2 mb-1">
                                                            <p className="font-bold text-gray-900 text-base">
                                                                {inmate.firstName} {inmate.secondName || ''} {inmate.firstLastname} {inmate.secondLastname || ''}
                                                            </p>
                                                            {overdue && (
                                                                <span className="px-2 py-0.5 text-xs font-bold bg-red-100 text-red-700 rounded-full border border-red-200">
                                                                    RETORNO VENCIDO
                                                                </span>
                                                            )}
                                                        </div>
                                                        <p className="text-sm text-gray-600 mb-2">
                                                            C.I. <span className="font-medium">{inmate.cedula}</span>
                                                        </p>
                                                        
                                                        <div className="grid grid-cols-1 sm:grid-cols-3 gap-2 text-xs">
                                                            <div className="flex items-center gap-1.5 text-gray-500">
                                                                <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
                                                                <span>Celda: <span className="font-medium text-gray-700">{inmate.cellIdentifier || 'Sin asignar'}</span></span>
                                                            </div>
                                                            <div className="flex items-center gap-1.5 text-gray-500">
                                                                <Clock className="w-3.5 h-3.5 flex-shrink-0" />
                                                                <span>Salida: <span className="font-medium text-gray-700">{formatDateTime(inmate.fechaSalidaTemporal)}</span></span>
                                                            </div>
                                                            <div className={`flex items-center gap-1.5 ${overdue ? 'text-red-600' : 'text-gray-500'}`}>
                                                                <Calendar className="w-3.5 h-3.5 flex-shrink-0" />
                                                                <span>Retorno est.: <span className={`font-medium ${overdue ? 'text-red-700' : 'text-gray-700'}`}>{formatDateTime(inmate.fechaRetornoEstimada)}</span></span>
                                                            </div>
                                                        </div>

                                                        {inmate.motivoSalidaTemporal && (
                                                            <div className="mt-2">
                                                                <span className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-medium bg-amber-50 text-amber-700 rounded-full border border-amber-200">
                                                                    {inmate.motivoSalidaTemporal}
                                                                </span>
                                                            </div>
                                                        )}
                                                    </div>
                                                    
                                                    <button className="ml-4 px-4 py-2 text-sm font-bold bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors flex-shrink-0">
                                                        Registrar Retorno
                                                    </button>
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                            )}
                        </div>
                    </div>
                ) : (
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                        <div className="bg-emerald-50 border-b border-emerald-100 p-5">
                            <div className="flex items-start justify-between">
                                <div>
                                    <h2 className="text-lg font-bold text-emerald-900">Completar Formulario de Retorno</h2>
                                    <p className="text-sm text-emerald-700">
                                        Para: <span className="font-bold">{selectedInmate.firstName} {selectedInmate.firstLastname}</span> (C.I. {selectedInmate.cedula})
                                    </p>
                                </div>
                                <button onClick={() => setSelectedInmate(null)} className="text-xs text-emerald-600 hover:underline font-medium">
                                    ← Volver a la lista
                                </button>
                            </div>

                            {/* Summary of the egress details */}
                            <div className="mt-3 p-3 bg-white/70 rounded-lg border border-emerald-200 grid grid-cols-1 sm:grid-cols-3 gap-2 text-xs">
                                <div>
                                    <span className="text-emerald-600 font-medium">Motivo:</span>
                                    <span className="ml-1 text-gray-800">{selectedInmate.motivoSalidaTemporal || '—'}</span>
                                </div>
                                <div>
                                    <span className="text-emerald-600 font-medium">Salida:</span>
                                    <span className="ml-1 text-gray-800">{formatDateTime(selectedInmate.fechaSalidaTemporal)}</span>
                                </div>
                                <div>
                                    <span className="text-emerald-600 font-medium">Retorno est.:</span>
                                    <span className={`ml-1 ${isOverdue(selectedInmate.fechaRetornoEstimada) ? 'text-red-700 font-bold' : 'text-gray-800'}`}>
                                        {formatDateTime(selectedInmate.fechaRetornoEstimada)}
                                    </span>
                                </div>
                            </div>
                        </div>
                        
                        <form onSubmit={handleReturn} className="p-6 space-y-5">
                            {error && (
                                <div className="p-3 bg-red-50 text-red-700 rounded-lg text-sm border border-red-100 flex items-center gap-2">
                                    <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0" />
                                    {error}
                                </div>
                            )}

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora de Retorno Real *</label>
                                <input 
                                    type="datetime-local" 
                                    value={fechaRetorno}
                                    onChange={e => setFechaRetorno(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Observaciones</label>
                                <textarea 
                                    rows={3}
                                    value={observaciones}
                                    onChange={e => setObservaciones(e.target.value)}
                                    placeholder="Detalles sobre el estado del recluso al regresar, si recibió atención médica, etc..."
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                                />
                            </div>

                            <div className="pt-4 border-t border-gray-100 flex gap-3">
                                <button
                                    type="button"
                                    onClick={() => setSelectedInmate(null)}
                                    className="px-5 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="flex-1 px-5 py-2.5 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 font-bold transition-colors disabled:opacity-50"
                                >
                                    {loading ? 'Procesando...' : 'Confirmar Retorno'}
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        </SidebarLayout>
    )
}
