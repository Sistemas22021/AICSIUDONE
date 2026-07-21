import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, UserMinus, ArrowLeft, ArrowRight, CheckCircle, AlertCircle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'
import { maxDateTimeNow, formatCedulaIntelligent, cedulaKeyFilter } from '../../shared/validations'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    firstLastname: string
    cellIdentifier?: string
    status: string
}

export default function DischargePage() {
    const navigate = useNavigate()
    const auth = useAuth()
    const [query, setQuery] = useState('')
    const [searchResults, setSearchResults] = useState<InmateData[]>([])
    const [selectedInmate, setSelectedInmate] = useState<InmateData | null>(null)
    const [estimatedReleaseDate, setEstimatedReleaseDate] = useState<string | null>(null)
    const [motivoEgreso, setMotivoEgreso] = useState('Cumplimiento de condena')
    const [fechaEgreso, setFechaEgreso] = useState(new Date().toISOString().slice(0, 16))
    const [observacionesEgreso, setObservacionesEgreso] = useState('')
    const [deceaseType, setDeceaseType] = useState('NATURAL')
    const [deathDateTimeFound, setDeathDateTimeFound] = useState(new Date().toISOString().slice(0, 16))
    const [deathDescription, setDeathDescription] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const motivos = auth.hasRole('Supervisor')
        ? ['Cumplimiento de condena', 'Libertad condicional', 'Fallecimiento']
        : ['Cumplimiento de condena', 'Libertad condicional']

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!query.trim()) return
        setLoading(true)
        setError('')
        try {
            // Simplified search: get all and filter locally for MVP
            const res = await api.get<InmateData[]>('/inmates')
            const filtered = res.data.filter(i => 
                (i.status !== 'EGRESADO') &&
                (i.cedula.includes(query) || 
                 i.firstName.toLowerCase().includes(query.toLowerCase()) || 
                 i.firstLastname.toLowerCase().includes(query.toLowerCase()))
            )
            setSearchResults(filtered)
            if (filtered.length === 0) {
                setError('No se encontraron reclusos activos con ese criterio.')
            }
        } catch (err) {
            setError('Error al buscar reclusos.')
            console.log(err)
        } finally {
            setLoading(false)
        }
    }

    const handleSelectInmate = async (inmate: InmateData) => {
        setSelectedInmate(inmate)
        try {
            const res = await api.get<{ estimatedReleaseDate?: string }>(`/inmates/${inmate.id}`)
            setEstimatedReleaseDate(res.data.estimatedReleaseDate || null)
        } catch {
            setEstimatedReleaseDate(null)
        }
    }

    const handleDischarge = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!selectedInmate) return

        if (motivoEgreso === 'Fallecimiento') {
            if (!deathDescription.trim() || deathDescription.trim().length < 20) {
                setError('La descripción de los hechos es obligatoria y debe tener al menos 20 caracteres.')
                return
            }
            const hallazgo = new Date(deathDateTimeFound)
            if (hallazgo > new Date()) {
                setError('La fecha de hallazgo no puede ser futura.')
                return
            }
            if (!confirm(`¿Está seguro de registrar el deceso de ${selectedInmate.firstName} ${selectedInmate.firstLastname}? Esta acción no se puede deshacer.`)) {
                return
            }

            setLoading(true)
            setError('')
            try {
                if (deceaseType === 'NATURAL') {
                    await api.post(`/inmates/${selectedInmate.id}/death-report/natural`, {
                        dateTimeFound: deathDateTimeFound,
                        description: deathDescription
                    })
                    alert('Egreso por fallecimiento natural registrado exitosamente.')
                    navigate('/dashboard')
                } else {
                    await api.post(`/inmates/${selectedInmate.id}/death-report/non-natural`, {
                        dateTimeFound: deathDateTimeFound,
                        description: deathDescription
                    })
                    alert('Borrador de deceso no natural registrado exitosamente. Redirigiendo al expediente de incidente interno.')
                    navigate(`/incidentes/registrar/${selectedInmate.id}`)
                }
            } catch (err) {
                console.error(err)
            } finally {
                setLoading(false)
            }
            return
        }

        if (motivoEgreso === 'Cumplimiento de condena' && estimatedReleaseDate && estimatedReleaseDate !== fechaEgreso.split('T')[0]) {
            if (!confirm(`ADVERTENCIA: La fecha de egreso (${fechaEgreso.split('T')[0]}) no coincide con la fecha estimada de liberación (${estimatedReleaseDate}). ¿Desea continuar de todas formas?`)) {
                return
            }
        }

        if (!confirm(`¿Está seguro de registrar el egreso de ${selectedInmate.firstName} ${selectedInmate.firstLastname}? Esta acción no se puede deshacer.`)) {
            return
        }

        setLoading(true)
        setError('')
        try {
            await api.post(`/inmates/${selectedInmate.id}/discharge`, {
                motivoEgreso,
                fechaEgreso: fechaEgreso.split('T')[0],
                observacionesEgreso
            })
            const mensaje = motivoEgreso === 'Cumplimiento de condena' || motivoEgreso === 'Libertad condicional'
                ? 'Egreso registrado exitosamente. Se ha creado un perfil base en el módulo Post-Penitenciario y se ha notificado al Supervisor para la asignación de un oficial de seguimiento.'
                : 'Egreso registrado exitosamente.'
            alert(mensaje)
            navigate('/dashboard')
        } catch (err) {
            console.log(err)
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
                        <UserMinus className="w-8 h-8 text-amber-600" />
                        Registrar Egreso
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Formalice la salida de un recluso del centro penitenciario.
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
                                    placeholder="Ej: V-12345678"
                                    value={query}
                                    onChange={e => {
                                        setQuery(formatCedulaIntelligent(e.target.value));
                                        setError('');
                                    }}
                                    onKeyDown={cedulaKeyFilter}
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
                                <h2 className="text-lg font-bold text-amber-900">Completar Formulario de Egreso</h2>
                                <p className="text-sm text-amber-700">Para: <span className="font-bold">{selectedInmate.firstName} {selectedInmate.firstLastname}</span> (C.I. {selectedInmate.cedula})</p>
                            </div>
                            <button onClick={() => setSelectedInmate(null)} className="text-xs text-amber-600 hover:underline font-medium">
                                Cambiar Recluso
                            </button>
                        </div>
                        
                        <form onSubmit={handleDischarge} className="p-6 space-y-5">
                            {error && (
                                <div className="p-3 bg-red-50 text-red-700 rounded-lg text-sm border border-red-100">
                                    {error}
                                </div>
                            )}

                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Motivo de Egreso *</label>
                                <select 
                                    value={motivoEgreso}
                                    onChange={e => setMotivoEgreso(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 bg-white"
                                    required
                                >
                                    {motivos.map(m => (
                                        <option key={m} value={m}>{m}</option>
                                    ))}
                                </select>
                                {(motivoEgreso === 'Cumplimiento de condena' || motivoEgreso === 'Libertad condicional') && (
                                    <p className="text-xs text-emerald-600 mt-1 flex items-center gap-1">
                                        <CheckCircle className="w-3 h-3" />
                                        Se creará un perfil en el módulo Post-Penitenciario automáticamente.
                                    </p>
                                )}
                                {motivoEgreso === 'Fallecimiento' && (
                                    <p className="text-xs text-red-600 mt-1 flex items-center gap-1">
                                        <AlertCircle className="w-3 h-3" />
                                        Se desplegará el formulario de informe de deceso (HU-S4-03) al confirmar.
                                    </p>
                                )}
                            </div>

                            {motivoEgreso === 'Fallecimiento' ? (
                                <>
                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 mb-1">Tipo de Deceso *</label>
                                        <select 
                                            value={deceaseType}
                                            onChange={e => setDeceaseType(e.target.value)}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 bg-white"
                                            required
                                        >
                                            <option value="NATURAL">Natural (Liberación de celda automática)</option>
                                            <option value="NO_NATURAL">No Natural (Requiere registrar expediente de incidente)</option>
                                        </select>
                                    </div>

                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora del Hallazgo *</label>
                                        <input 
                                            type="datetime-local" 
                                            value={deathDateTimeFound}
                                            max={maxDateTimeNow()}
                                            onChange={e => setDeathDateTimeFound(e.target.value)}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                            required
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 mb-1">Descripción / Relato de los Hechos *</label>
                                        <textarea 
                                            rows={4}
                                            value={deathDescription}
                                            onChange={e => setDeathDescription(e.target.value)}
                                            minLength={20}
                                            placeholder="Detalle exhaustivo del hallazgo y estado del cuerpo..."
                                            className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                            required
                                        />
                                        <p className="text-xs text-gray-500 mt-1">Mínimo 20 caracteres.</p>
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 mb-1">Fecha y Hora de Egreso *</label>
                                        <input 
                                            type="datetime-local" 
                                            value={fechaEgreso}
                                            max={maxDateTimeNow()}
                                            onChange={e => setFechaEgreso(e.target.value)}
                                            className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                            required
                                        />
                                    </div>

                                    <div>
                                        <label className="block text-sm font-bold text-gray-700 mb-1">Observaciones</label>
                                        <textarea 
                                            rows={3}
                                            value={observacionesEgreso}
                                            onChange={e => setObservacionesEgreso(e.target.value)}
                                            maxLength={1000}
                                            placeholder="Detalles adicionales sobre la liberación o traslado..."
                                            className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
                                        />
                                        <p className="text-xs text-gray-500 mt-1 text-right">{observacionesEgreso.length}/1000</p>
                                    </div>
                                </>
                            )}

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
                                    {loading ? 'Procesando...' : 'Confirmar Egreso'}
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        </SidebarLayout>
    )
}
