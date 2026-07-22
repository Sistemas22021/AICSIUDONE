import { useState, useEffect } from 'react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

const CONDUCT_LEVELS = ['BAJO', 'MEDIO', 'ALTO'] as const

const CONDUCT_BADGE: Record<string, string> = {
  BAJO: 'bg-green-100 text-green-700',
  MEDIO: 'bg-yellow-100 text-yellow-700',
  ALTO: 'bg-red-100 text-red-700',
}

interface CellData {
  id: string
  identifier: string
  conductLevel: string
  lengthMeters: number | null
  widthMeters: number | null
  currentOccupancy: number
  maxCapacity: number
}

interface CellForm {
  identifier: string
  conductLevel: string
  lengthMeters: string
  widthMeters: string
}

const EMPTY_FORM: CellForm = {
  identifier: '', conductLevel: 'BAJO', lengthMeters: '', widthMeters: '',
}

export default function CellConfigPage() {
  const [cells, setCells] = useState<CellData[]>([])
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState<CellForm>({ ...EMPTY_FORM })
  const [editingId, setEditingId] = useState<string | null>(null)
  const [error, setError] = useState('')

  useEffect(() => { loadCells() }, [])

  async function loadCells() {
    try {
      const res = await api.get<CellData[]>('/cells')
      setCells(res.data)
    } catch {
      setCells([])
    }
  }

  function openNew() {
    setForm({ ...EMPTY_FORM })
    setEditingId(null)
    setError('')
    setModalOpen(true)
  }

  function openEdit(cell: CellData) {
    setForm({
      identifier: cell.identifier,
      conductLevel: cell.conductLevel,
      lengthMeters: String(cell.lengthMeters ?? ''),
      widthMeters: String(cell.widthMeters ?? ''),
    })
    setEditingId(cell.id)
    setError('')
    setModalOpen(true)
  }

  function closeModal() {
    setModalOpen(false)
    setError('')
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    try {
      const payload = {
        identifier: form.identifier,
        conductLevel: form.conductLevel,
        lengthMeters: Number(form.lengthMeters) || null,
        widthMeters: Number(form.widthMeters) || null,
      }
      if (editingId) {
        await api.put(`/cells/${editingId}`, payload)
      } else {
        await api.post('/cells', payload)
      }
      setForm({ ...EMPTY_FORM })
      setEditingId(null)
      closeModal()
      loadCells()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { error?: string; message?: string } } }
      setError(axiosErr.response?.data?.error || axiosErr.response?.data?.message || 'Error al guardar la celda')
    }
  }

  async function handleDelete(cell: CellData) {
    if (cell.currentOccupancy > 0) {
      alert('No se puede eliminar una celda con reclusos activos')
      return
    }
    if (!confirm('¿Eliminar esta celda?')) return
    try {
      await api.delete(`/cells/${cell.id}`)
      loadCells()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } }
      alert(axiosErr.response?.data?.message || 'No se puede eliminar')
    }
  }

  return (
    <SidebarLayout>
      <div className="w-full">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-2">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Administrar Celdas</h1>
            <p className="text-sm text-gray-500 mt-1">Gestione la infraestructura de celdas del establecimiento</p>
          </div>
          <button
            onClick={openNew}
            className="bg-gray-900 text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-800 transition-colors self-start"
          >
            + Nueva Celda
          </button>
        </div>

        <div className="mt-6 bg-white rounded-lg border border-gray-200 overflow-x-auto w-full">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100">
                <th className="text-left text-xs font-semibold text-gray-400 uppercase tracking-wider px-4 py-3.5">Identificador</th>
                <th className="text-left text-xs font-semibold text-gray-400 uppercase tracking-wider px-4 py-3.5">Nivel Conducta</th>
                <th className="hidden sm:table-cell text-left text-xs font-semibold text-gray-400 uppercase tracking-wider px-4 py-3.5">Dimensiones (m)</th>
                <th className="text-left text-xs font-semibold text-gray-400 uppercase tracking-wider px-4 py-3.5">Ocupación</th>
                <th className="text-right text-xs font-semibold text-gray-400 uppercase tracking-wider px-4 py-3.5">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {cells.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-12 text-center text-gray-400">
                    No hay celdas registradas. Haga clic en "+ Nueva Celda" para agregar una.
                  </td>
                </tr>
              ) : (
                cells.map(cell => (
                  <tr key={cell.id} className="border-b border-gray-100 last:border-0">
                    <td className="px-4 py-4 font-medium text-gray-900">{cell.identifier}</td>
                    <td className="px-4 py-4">
                      <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-medium ${CONDUCT_BADGE[cell.conductLevel]}`}>
                        {cell.conductLevel}
                      </span>
                    </td>
                    <td className="hidden sm:table-cell px-4 py-4 text-gray-600">
                      {cell.lengthMeters != null && cell.widthMeters != null
                        ? `${cell.lengthMeters}x${cell.widthMeters}`
                        : '\u2014'}
                    </td>
                    <td className="px-4 py-4 text-gray-600">{cell.currentOccupancy}-{cell.maxCapacity}</td>
                    <td className="px-4 py-4 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => openEdit(cell)}
                          className="p-1.5 text-blue-500 hover:bg-blue-50 rounded transition-colors"
                          title="Editar"
                        >
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                          </svg>
                        </button>
                        <button
                          onClick={() => handleDelete(cell)}
                          className="p-1.5 text-red-500 hover:bg-red-50 rounded transition-colors"
                          title="Eliminar"
                        >
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="fixed inset-0 bg-black/40" onClick={closeModal} />
          <div className="relative bg-white rounded-lg shadow-lg w-full max-w-lg mx-4 p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-5">
              {editingId ? 'Editar Celda' : 'Nueva Celda'}
            </h2>

            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">{error}</div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Identificador <span className="text-red-500">*</span>
                  </label>
                  <input
                    name="identifier"
                    value={form.identifier}
                    onChange={handleChange}
                    placeholder="Ej. A-01"
                    pattern="^[A-Z]-\d{2,3}$"
                    title="Formato requerido: Letra mayúscula, guion y 2 o 3 números (Ej: A-01, B-103)"
                    required
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nivel de conducta <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="conductLevel"
                    value={form.conductLevel}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                  >
                    {CONDUCT_LEVELS.map(l => <option key={l}>{l}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Largo (m)</label>
                  <input
                    name="lengthMeters"
                    type="number"
                    step="0.01"
                    min="1"
                    max="50"
                    value={form.lengthMeters}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Ancho (m)</label>
                  <input
                    name="widthMeters"
                    type="number"
                    step="0.01"
                    min="1"
                    max="50"
                    value={form.widthMeters}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 mt-6 pt-4 border-t border-gray-100">
                <button
                  type="button"
                  onClick={closeModal}
                  className="px-5 py-2 text-sm font-medium text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-sm font-medium text-white bg-gray-900 rounded-lg hover:bg-gray-800 transition-colors"
                >
                  {editingId ? 'Actualizar' : 'Crear Celda'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </SidebarLayout>
  )
}
