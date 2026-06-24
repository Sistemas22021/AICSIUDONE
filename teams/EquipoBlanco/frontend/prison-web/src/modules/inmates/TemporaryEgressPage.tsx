import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, Clock, ArrowLeft, ArrowRight, AlertCircle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    firstLastname: string
    cellIdentifier?: string
    status: string
}

export default function TemporaryEgressPage() {
    const navigate = useNavigate()
    const auth = useAuth()
    const [query, setQuery] = useState('')
    const [searchResults, setSearchResults] = useState<InmateData[]>([])
    const [selectedInmate, setSelectedInmate] = useState<InmateData | null>(null)
    const [motivoSalidaTemporal, setMotivoSalidaTemporal] = useState('Asistencia a Juicio')
    
    // Default departure time to now (local format YYYY-MM-DDTHH:MM)
    const [fechaSalidaTemporal, setFechaSalidaTemporal] = useState(new Date().toISOString().slice(0, 16))
    
    // Default estimated return time to tomorrow same hour
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const [fechaRetornoEstimada, setFechaRetornoEstimada] = useState(tomorrow.toISOString().slice(0, 16))
    
    const [observaciones, setObservaciones] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const motivos = [
        'Asistencia a Juicio',
        'Traslado Médico',
        'Emergencia Familiar',
        'Diligencia Judicial',
        'Otro'
    ]

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!query.trim()) return
        setLoading(true)
        setError('')
        try {
            const res = await api.get<InmateData[]>('/inmates')
            const filtered = res.data.filter(i => 
                (i.status === 'ACTIVO_CON_CELDA' || i.status === 'ACTIVO_SIN_CELDA') &&
                (i.cedula.includes(query) || 
                 i.firstName.toLowerCase().includes(query.toLowerCase()) || 
                 i.firstLastname.toLowerCase().includes(query.toLowerCase()))
            )
            setSearchResults(filtered)
            if (filtered.length === 0) {
                setError('No se encontraron reclusos activos en prisión con ese criterio.')
            }
        } catch (err) {
            setError('Error al buscar reclusos.')
            console.log(err)
        } finally {
            setLoading(false)
        }
    }

    const handleSelectInmate = (inmate: InmateData) => {
        setSelectedInmate(inmate)
        setError('')
    }

    const handleEgress = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!selectedInmate) return

        const egressDate = new Date(fechaSalidaTemporal);
        const estimatedReturnDate = new Date(fechaRetornoEstimada);

        if (estimatedReturnDate <= egressDate) {
            setError('La fecha y hora de retorno estimada debe ser posterior a la fecha de salida.')
            return
        }

        if (!confirm(`¿Está seguro de registrar la salida temporal de ${selectedInmate.firstName} ${selectedInmate.firstLastname}?`)) {
            return
        }

        setLoading(true)
        setError('')
        try {
            // Append :00 seconds if not present, since datetime-local inputs omit seconds
            const salidaISO = fechaSalidaTemporal.length === 16 ? fechaSalidaTemporal + ':00' : fechaSalidaTemporal
            const retornoISO = fechaRetornoEstimada.length === 16 ? fechaRetornoEstimada + ':00' : fechaRetornoEstimada

            await api.post(`/inmates/${selectedInmate.id}/temporary-egress`, {
                motivoSalidaTemporal,
                fechaSalidaTemporal: salidaISO,
                fechaRetornoEstimada: retornoISO,
                observaciones
            })
            alert('Salida temporal registrada exitosamente. La celda asignada se mantiene reservada.')
            navigate('/dashboard')
        } catch (err: any) {
            console.error('Error al registrar salida temporal:', err)
            const message = err.response?.data?.message || err.response?.data?.error || 'Error al registrar la salida temporal.'
            setError(message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <SidebarLayout>
            <div className="max-w-3xl mx-auto space-y-6">
                <div>
                    <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-sm text-gray-500 hover:text-gray-900 mb-4 transition-colors">
                        <ArrowLeft className="w-4 h-4" />
                        Volver al Dashboard
                    </button>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <Clock className="w-8 h-8 text-amber-600" />
                        Registrar Salida Temporal
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Registre la salida temporal de un recluso manteniendo su plaza de celda reservada.
                    </p>
                </div>

                {!selectedInmate ? (
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                        <h2 className="text-lg font-bold text-gray-800 mb-4">Selección de Recluso</h2>
                        <form onSubmit={handleSearch} className="flex gap-3 mb-6">
                            <div className="flex-1 relative">
                                <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                                <input
                                    type="text"
                                    placeholder="Buscar por nombre o cédula..."
                                    value={query}
                                    onChange={e => setQuery(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                />
                            </div>
                            <button
                                type="submit"
                                disabled={loading || !query.trim()}
                                className="px-5 py-2.5 bg-gray-900 text-white rounded-lg hover:bg-gray-800 font-medium transition-colors disabled:opacity-50"
                            >
                                Buscar
                            </button>
                        </form>

                        {error && <p className="text-sm text-red-600 mb-4">{error}</p>}

                        {searchResults.length > 0 && (
                            <div className="space-y-3">
                                {searchResults.map(inmate => (
                                    <div key={inmate.id} className="flex items-center justify-between p-4 border border-gray-100 rounded-lg hover:border-blue-300 hover:bg-blue-50/50 transition-colors cursor-pointer"
                                         onClick={() => handleSelectInmate(inmate)}>
                                        <div>
                                            <p className="font-bold text-gray-900">{inmate.firstName} {inmate.firstLastname}</p>
                                            <p className="text-xs text-gray-500">C.I. {inmate.cedula} | Celda: {inmate.cellIdentifier || 'Sin asignar'}</p>
                                        </div>
                                        <ArrowRight className="w-5 h-5 text-gray-400" />
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                        <div className="bg-amber-50 border-b border-amber-100 p-5 flex items-start justify-between">
                            <div>
                                <h2 className="text-lg font-bold text-amber-900">Completar Formulario de Salida Temporal</h2>
                                <p className="text-sm text-amber-700">Para: <span className="font-bold">{selectedInmate.firstName} {selectedInmate.firstLastname}</span> (C.I. {selectedInmate.cedula})</p>
                            </div>
                            <button onClick={() => setSelectedInmate(null)} className="text-xs text-amber-600 hover:underline font-medium">
                                Cambiar Recluso
                            </button>
                        </div>
                        
                        <form onSubmit={handleEgress} className="p-6 space-y-5">
                            {error && (
                                <div className="p-3 bg-red-50 text-red-700 rounded-lg text-sm border border-red-100 flex items-center gap-2">
                                    <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0" />
                                    {error}
                                </div>
                            )}

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Motivo de Salida Temporal *</label>
                                <select 
                                    value={motivoSalidaTemporal}
                                    onChange={e => setMotivoSalidaTemporal(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 bg-white"
                                    required
                                >
                                    {motivos.map(m => (
                                        <option key={m} value={m}>{m}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora de Salida *</label>
                                    <input 
                                        type="datetime-local" 
                                        value={fechaSalidaTemporal}
                                        onChange={e => setFechaSalidaTemporal(e.target.value)}
                                        className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                        required
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora de Retorno Estimada *</label>
                                    <input 
                                        type="datetime-local" 
                                        value={fechaRetornoEstimada}
                                        onChange={e => setFechaRetornoEstimada(e.target.value)}
                                        className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                        required
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Observaciones</label>
                                <textarea 
                                    rows={3}
                                    value={observaciones}
                                    onChange={e => setObservaciones(e.target.value)}
                                    placeholder="Detalles sobre el traslado, custodia policial, nombre de la clínica o tribunal..."
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                />
                            </div>

                            <div className="pt-4 border-t border-gray-100 flex gap-3">
                                <button
                                    type="button"
                                    onClick={() => navigate('/dashboard')}
                                    className="px-5 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium transition-colors"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="flex-1 px-5 py-2.5 bg-amber-600 text-white rounded-lg hover:bg-amber-700 font-bold transition-colors disabled:opacity-50"
                                >
                                    {loading ? 'Procesando...' : 'Confirmar Salida'}
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        </SidebarLayout>
    )
}
