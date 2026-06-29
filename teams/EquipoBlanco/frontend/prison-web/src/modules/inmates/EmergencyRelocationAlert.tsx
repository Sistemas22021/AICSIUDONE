import { useState, useEffect } from 'react'
import { AlertCircle, ArrowRight, ShieldAlert, X } from 'lucide-react'
import api from '../../shared/api'

interface InmateData {
    id: string
    cedula: string
    firstName: string
    firstLastname: string
    cellIdentifier?: string
    status: string
}

interface CellData {
    id: string
    identifier: string
    conductLevel: string
    currentOccupancy: number
    maxCapacity: number
    blockedForInvestigation: boolean
    occupancyStatus?: string
}

interface EmergencyRelocationAlertProps {
    onRelocateSuccess?: () => void
}

export default function EmergencyRelocationAlert({ onRelocateSuccess }: EmergencyRelocationAlertProps) {
    const [pendingInmates, setPendingInmates] = useState<InmateData[]>([])
    const [cells, setCells] = useState<CellData[]>([])
    const [selectedInmate, setSelectedInmate] = useState<InmateData | null>(null)
    const [targetCellId, setTargetCellId] = useState('')
    const [modalOpen, setModalOpen] = useState(false)
    const [loading, setLoading] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState('')

    const fetchPendingInmates = async () => {
        try {
            const res = await api.get<InmateData[]>('/inmates/by-status/PENDIENTE_REUBICACION_EMERGENCIA')
            setPendingInmates(res.data)
        } catch (err) {
            console.error('Error fetching pending emergency relocation inmates', err)
        }
    }

    const fetchCells = async () => {
        try {
            const res = await api.get<CellData[]>('/cells')
            setCells(res.data)
        } catch (err) {
            console.error('Error fetching cells', err)
        }
    }

    useEffect(() => {
        fetchPendingInmates()
        fetchCells()
        
        // Polling every 10 seconds to keep the alert fresh, or just on mount
        const interval = setInterval(fetchPendingInmates, 10000)
        return () => clearInterval(interval)
    }, [])

    const handleOpenModal = (inmate: InmateData) => {
        setSelectedInmate(inmate)
        setTargetCellId('')
        setError('')
        setModalOpen(true)
    }

    const handleCloseModal = () => {
        setSelectedInmate(null)
        setTargetCellId('')
        setModalOpen(false)
    }

    const handleRelocate = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!selectedInmate || !targetCellId) return

        setSubmitting(true)
        setError('')
        try {
            await api.post(`/inmates/${selectedInmate.id}/relocate-emergency`, {
                targetCellId
            })
            alert(`Reubicación de emergencia para ${selectedInmate.firstName} completada con éxito.`)
            handleCloseModal()
            await fetchPendingInmates()
            await fetchCells()
            if (onRelocateSuccess) {
                onRelocateSuccess()
            }
        } catch (err: any) {
            console.error(err)
            setError(err.response?.data?.message || 'Error al reubicar al recluso de emergencia.')
        } finally {
            setSubmitting(false)
        }
    }

    if (pendingInmates.length === 0) return null

    // Filter available cells: not blocked, has capacity
    const availableCells = cells.filter(c => 
        !c.blockedForInvestigation && 
        c.occupancyStatus !== 'BLOQUEADA' && 
        c.currentOccupancy < c.maxCapacity
    )

    return (
        <div className="space-y-4">
            {/* Warning Banner */}
            <div className="bg-red-50 border-l-4 border-red-600 rounded-xl p-4 shadow-sm border border-red-100 flex items-start gap-4">
                <ShieldAlert className="w-6 h-6 text-red-600 shrink-0 mt-0.5" />
                <div className="flex-1">
                    <h3 className="font-bold text-red-950 text-sm md:text-base">
                        ⚠️ ALERTA: Reubicación de Emergencia Pendiente ({pendingInmates.length})
                    </h3>
                    <p className="text-xs md:text-sm text-red-700 mt-1">
                        Se detectaron co-habitantes de celdas clausuradas por investigación judicial que requieren reubicación inmediata para resguardar su integridad y orden.
                    </p>
                    
                    <div className="mt-3 space-y-2">
                        {pendingInmates.map(inmate => (
                            <div key={inmate.id} className="flex flex-col sm:flex-row sm:items-center justify-between bg-white/80 p-3 rounded-lg border border-red-100/50 gap-2">
                                <div>
                                    <p className="text-xs font-bold text-gray-900">{inmate.firstName} {inmate.firstLastname}</p>
                                    <p className="text-[10px] text-gray-500">C.I. {inmate.cedula} | Celda de Origen: {inmate.cellIdentifier || 'Clausurada'}</p>
                                </div>
                                <button
                                    onClick={() => handleOpenModal(inmate)}
                                    className="px-3.5 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-lg text-xs font-bold transition-colors flex items-center gap-1.5 self-start sm:self-center"
                                >
                                    Reubicar Celda <ArrowRight className="w-3.5 h-3.5" />
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Relocation Modal */}
            {modalOpen && selectedInmate && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50 animate-fadeIn">
                    <div className="bg-white rounded-xl shadow-xl border border-gray-200 w-full max-w-md overflow-hidden">
                        <div className="bg-red-50 border-b border-red-100 p-4 flex items-center justify-between">
                            <div className="flex items-center gap-2">
                                <ShieldAlert className="w-5 h-5 text-red-600" />
                                <h3 className="font-bold text-red-950 text-sm">Asignar Celda de Emergencia</h3>
                            </div>
                            <button onClick={handleCloseModal} className="text-gray-400 hover:text-gray-600">
                                <X className="w-5 h-5" />
                            </button>
                        </div>

                        <form onSubmit={handleRelocate} className="p-5 space-y-4">
                            {error && (
                                <div className="p-3 bg-red-50 text-red-700 rounded-lg text-xs border border-red-100">
                                    {error}
                                </div>
                            )}

                            <div className="bg-gray-50 p-3 rounded-lg border border-gray-100 space-y-1">
                                <p className="text-xs text-gray-500 font-medium">Recluso a Reubicar:</p>
                                <p className="text-sm font-bold text-gray-900">{selectedInmate.firstName} {selectedInmate.firstLastname}</p>
                                <p className="text-xs text-gray-500">C.I. {selectedInmate.cedula} | Celda Origen: {selectedInmate.cellIdentifier}</p>
                            </div>

                            <div>
                                <label className="block text-xs font-bold text-gray-700 mb-1">
                                    Seleccionar Celda Destino Disponibles *
                                </label>
                                <select
                                    value={targetCellId}
                                    onChange={e => setTargetCellId(e.target.value)}
                                    className="w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500 bg-white text-sm"
                                    required
                                >
                                    <option value="">-- Seleccionar Celda --</option>
                                    {availableCells.map(cell => (
                                        <option key={cell.id} value={cell.id}>
                                            Celda {cell.identifier} (Capacidad: {cell.currentOccupancy}/{cell.maxCapacity} | Nivel: {cell.conductLevel})
                                        </option>
                                    ))}
                                </select>
                                {availableCells.length === 0 && (
                                    <p className="text-[11px] text-red-600 mt-1">
                                        No hay celdas disponibles con capacidad libre y que no estén bloqueadas.
                                    </p>
                                )}
                            </div>

                            <div className="pt-3 border-t border-gray-100 flex gap-2 justify-end">
                                <button
                                    type="button"
                                    onClick={handleCloseModal}
                                    className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium text-xs transition-colors"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={submitting || !targetCellId}
                                    className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-bold text-xs transition-colors disabled:opacity-50"
                                >
                                    {submitting ? 'Procesando...' : 'Confirmar Reubicación'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    )
}
