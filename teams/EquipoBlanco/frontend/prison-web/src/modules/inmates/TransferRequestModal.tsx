import React, { useState, useEffect, useRef } from 'react'
import { X, Search, FileText, AlertCircle } from 'lucide-react'
import api from '../../shared/api'

interface CellData {
  id: string
  identifier: string
  maxCapacity: number
  currentOccupancy: number
  conductLevel: string
}

interface TransferRequestModalProps {
  isOpen: boolean
  onClose: () => void
  inmateId: string
  inmateName: string
  inmateCedula: string
  sourceCellId: string | null
  sourceCellIdentifier: string | null
  onSuccess?: () => void
}

export default function TransferRequestModal({
  isOpen,
  onClose,
  inmateId,
  inmateName,
  inmateCedula,
  sourceCellId,
  sourceCellIdentifier,
  onSuccess
}: TransferRequestModalProps) {
  const [cells, setCells] = useState<CellData[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCellId, setSelectedCellId] = useState('')
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')

  const hasOpened = useRef(false)

  useEffect(() => {
    if (isOpen && !hasOpened.current) {
      hasOpened.current = true
      setSelectedCellId('')
      setReason('')
      setErrorMsg('')
      const fetchCells = async () => {
        try {
          const res = await api.get<CellData[]>('/cells')
          setCells(res.data || [])
        } catch {
          setErrorMsg('Error al cargar la lista de celdas')
        }
      }
      fetchCells()
    } else if (!isOpen) {
      hasOpened.current = false
    }
  }, [isOpen])

  if (!isOpen) return null

  const filteredCells = cells.filter(cell => {
    // Exclude source cell
    if (sourceCellId && cell.id === sourceCellId) return false
    return cell.identifier.toLowerCase().includes(searchQuery.toLowerCase())
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedCellId) {
      setErrorMsg('Debe seleccionar una celda de destino.')
      return
    }
    if (!reason.trim()) {
      setErrorMsg('Debe ingresar la justificación del traslado.')
      return
    }

    setLoading(true)
    setErrorMsg('')
    try {
      const username = sessionStorage.getItem('username') || 'Oficial'
      await api.post('/transfers', {
        inmateId,
        targetCellId: selectedCellId,
        reason: reason.trim()
      }, {
        headers: { 'X-User-Name': username }
      })
      alert('Solicitud de traslado creada con éxito en estado PENDIENTE.')
      if (onSuccess) onSuccess()
      onClose()
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error al enviar la solicitud de traslado.'
      setErrorMsg(message)
    } finally {
      setLoading(false)
    }
  }

  const selectedCell = cells.find(c => c.id === selectedCellId)

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[70] p-4 transition-all duration-300 animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden border border-gray-200 flex flex-col max-h-[90vh]">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4.5 bg-gray-900 text-white shrink-0">
          <div>
            <h3 className="text-lg font-bold">Solicitar Traslado de Recluso</h3>
            <p className="text-xs text-gray-300 mt-0.5">Creación de solicitud formal para evaluación</p>
          </div>
          <button onClick={onClose} className="p-1.5 hover:bg-gray-800 rounded-lg transition-colors text-white">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4 overflow-y-auto flex-1 text-sm">
          
          {/* Informacion de Recluso */}
          <div className="p-4 bg-gray-50 border border-gray-200 rounded-xl space-y-2">
            <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1">Recluso</h4>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <div>
                <span className="text-gray-500 font-medium">Nombre:</span>
                <p className="font-bold text-gray-800">{inmateName}</p>
              </div>
              <div>
                <span className="text-gray-500 font-medium">Cédula:</span>
                <p className="font-bold text-gray-800">{inmateCedula}</p>
              </div>
              <div className="col-span-2 pt-1 border-t border-gray-150">
                <span className="text-gray-500 font-medium">Celda Actual:</span>
                <p className="font-bold text-gray-800">{sourceCellIdentifier || 'Sin Celda Asignada'}</p>
              </div>
            </div>
          </div>

          {errorMsg && (
            <div className="p-3.5 bg-red-50 border border-red-200 rounded-xl flex items-start gap-2.5 text-red-800">
              <AlertCircle className="w-4.5 h-4.5 text-red-600 shrink-0 mt-0.5" />
              <div className="text-xs font-semibold leading-relaxed">{errorMsg}</div>
            </div>
          )}

          {/* Seleccionar Celda Destino */}
          <div className="space-y-2">
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide">Celda Destino</label>
            <div className="relative">
              <input
                type="text"
                placeholder="Buscar celda por identificador..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                className="w-full pl-9 pr-4 py-2 border border-gray-300 rounded-lg text-xs focus:ring-1 focus:ring-blue-500 focus:border-blue-500 focus:outline-none"
              />
              <Search className="w-4 h-4 text-gray-400 absolute left-3 top-2.5" />
            </div>
            
            <div className="border border-gray-200 rounded-xl max-h-40 overflow-y-auto bg-gray-50/50 p-1.5 space-y-1">
              {filteredCells.length === 0 ? (
                <p className="text-xs text-gray-400 italic text-center py-4">No se encontraron celdas de destino disponibles.</p>
              ) : (
                filteredCells.map(cell => {
                  const isFull = cell.currentOccupancy >= cell.maxCapacity
                  const isSelected = selectedCellId === cell.id
                  return (
                    <label
                      key={cell.id}
                      className={`flex items-center justify-between p-2.5 rounded-lg cursor-pointer border transition-all ${
                        isSelected
                          ? 'bg-blue-50 border-blue-400 shadow-sm'
                          : 'bg-white border-transparent hover:border-gray-250'
                      }`}
                    >
                      <div className="flex items-center gap-2.5">
                        <input
                          type="radio"
                          name="target-cell-select"
                          value={cell.id}
                          checked={isSelected}
                          onChange={e => setSelectedCellId(e.target.value)}
                          className="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                        />
                        <div>
                          <p className="text-xs font-bold text-gray-800">Celda {cell.identifier}</p>
                          <p className="text-[10px] text-gray-400 font-medium">Capacidad: {cell.currentOccupancy} / {cell.maxCapacity}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-1.5">
                        <span className={`text-[9px] px-2 py-0.5 rounded font-bold uppercase tracking-wider ${
                          cell.conductLevel === 'ALTO' ? 'bg-red-50 border border-red-200 text-red-700' :
                          cell.conductLevel === 'MEDIO' ? 'bg-yellow-50 border border-yellow-250 text-yellow-800' :
                          'bg-green-50 border border-green-200 text-green-700'
                        }`}>
                          {cell.conductLevel}
                        </span>
                        {isFull && (
                          <span className="text-[8px] bg-red-100 border border-red-200 text-red-800 px-1.5 py-0.5 rounded font-bold uppercase">
                            Llena
                          </span>
                        )}
                      </div>
                    </label>
                  )
                })
              )}
            </div>
          </div>

          {/* Justificacion */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold text-gray-700 uppercase tracking-wide flex items-center gap-1">
              <FileText className="w-3.5 h-3.5 text-gray-400" />
              Justificación de Traslado
            </label>
            <textarea
              required
              rows={3}
              placeholder="Describa el motivo o justificación de seguridad, conducta o segregación para solicitar el traslado..."
              value={reason}
              onChange={e => setReason(e.target.value)}
              className="w-full p-3 border border-gray-300 rounded-lg text-xs focus:ring-1 focus:ring-blue-500 focus:border-blue-500 focus:outline-none"
            />
          </div>

          {selectedCell && selectedCell.currentOccupancy >= selectedCell.maxCapacity && (
            <div className="p-3 bg-amber-50 border border-amber-200 rounded-xl text-xs text-amber-800 flex items-start gap-2">
              <AlertCircle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5 animate-pulse" />
              <div>
                <span className="font-bold block">Aviso de Capacidad Máxima:</span>
                La celda destino seleccionada se encuentra al límite. La aprobación del traslado está permitida y excederá la capacidad si se aprueba.
              </div>
            </div>
          )}

          {/* Footer Actions */}
          <div className="flex gap-3 pt-3 border-t border-gray-150">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="flex-1 py-2.5 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-100 transition-colors text-xs font-bold uppercase tracking-wider disabled:opacity-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || !selectedCellId || !reason.trim()}
              className="flex-1 py-2.5 bg-gray-900 text-white rounded-xl hover:bg-gray-800 transition-all disabled:bg-gray-300 disabled:cursor-not-allowed text-xs font-bold uppercase tracking-wider shadow-sm flex items-center justify-center gap-1.5"
            >
              {loading ? (
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
              ) : 'Enviar Solicitud'}
            </button>
          </div>

        </form>
      </div>
    </div>
  )
}
