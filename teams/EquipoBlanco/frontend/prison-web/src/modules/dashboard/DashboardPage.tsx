import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { MapPin, Users, CheckCircle, AlertTriangle, Info, DoorOpen, Gauge, BarChart3 } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface CellData {
  id: string
  identifier: string
  maxCapacity: number
  currentOccupancy: number
  conductLevel: string
  lengthMeters: number | null
  widthMeters: number | null
}

interface InmateData {
  id: string
  status: string
  cellId: string | null
}

export default function DashboardPage() {
  const [celdas, setCeldas] = useState<CellData[]>([])
  const [reclusos, setReclusos] = useState<InmateData[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const [cellsRes, inmatesRes] = await Promise.all([
          api.get<CellData[]>('/cells'),
          api.get<InmateData[]>('/inmates').catch(() => ({ data: [] as InmateData[] }))
        ])
        setCeldas(cellsRes.data || [])
        setReclusos(inmatesRes.data || [])
      } catch (err) {
        console.error('Error loading dashboard data', err)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const totalCeldas = celdas.length
  const totalReclusos = celdas.reduce((s, c) => s + (c.currentOccupancy ?? 0), 0)
  const capacidadTotal = celdas.reduce((s, c) => s + c.maxCapacity, 0)
  const capacidadDisponible = capacidadTotal - totalReclusos
  const pctOcupacion = capacidadTotal > 0 ? ((totalReclusos / capacidadTotal) * 100).toFixed(1) : '0'
  const celdasLlenas = celdas.filter(c => (c.currentOccupancy ?? 0) >= c.maxCapacity).length
  const celdasLimite = celdas.filter(c => {
    const pct = (c.currentOccupancy ?? 0) / c.maxCapacity
    return pct >= 0.8 && pct < 1
  }).length
  const reclusosSinCelda = reclusos.filter(r => r.status === 'ACTIVO_SIN_CELDA').length
  const reclusosConCelda = reclusos.filter(r => r.status === 'ACTIVO_CON_CELDA').length

  const cards = [
    { label: 'Total de Celdas', valor: totalCeldas, sub: celdasLlenas + ' llenas - ' + celdasLimite + ' al limite', icon: DoorOpen, color: 'text-gray-900' },
    { label: 'Reclusos Activos', valor: totalReclusos, sub: reclusosConCelda + ' con celda - ' + reclusosSinCelda + ' sin celda', icon: Users, color: 'text-blue-600' },
    { label: 'Capacidad Disponible', valor: capacidadDisponible, sub: 'De ' + capacidadTotal + ' plazas totales', icon: CheckCircle, color: 'text-emerald-600' },
    { label: 'Ocupacion General', valor: pctOcupacion + '%', sub: parseFloat(pctOcupacion) >= 80 ? 'Nivel critico' : 'Nivel normal', icon: Gauge, color: parseFloat(pctOcupacion) >= 80 ? 'text-amber-600' : 'text-gray-900' },
  ]

  if (loading) {
    return (
      <SidebarLayout>
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="flex flex-col items-center gap-3">
            <div className="w-10 h-10 border-4 border-blue-100 border-t-blue-600 rounded-full animate-spin"></div>
            <p className="text-sm text-gray-500 font-medium">Cargando dashboard...</p>
          </div>
        </div>
      </SidebarLayout>
    )
  }

  return (
    <SidebarLayout>
      <div className="max-w-7xl mx-auto space-y-6">

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-gray-200 pb-5">
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-2">
              <BarChart3 className="w-8 h-8 text-blue-600" />
              Dashboard
            </h1>
            <p className="text-sm text-gray-500 mt-1">
              Resumen general del estado del penal y ocupacion de celdas.
            </p>
          </div>
          <Link
            to="/mapa"
            className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-xs font-semibold shadow-sm"
          >
            <MapPin className="w-4 h-4" />
            Ver Mapa de Celdas
          </Link>
        </div>

        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {cards.map((card, i) => {
            const CardIcon = card.icon
            return (
              <div key={i} className="bg-white rounded-xl border border-gray-200 p-4 shadow-sm flex items-center justify-between">
                <div className="space-y-1">
                  <p className="text-xs font-medium text-gray-400 uppercase tracking-wider">{card.label}</p>
                  <p className={'text-2xl font-black ' + card.color}>{card.valor}</p>
                  <p className="text-[11px] text-gray-400 font-medium">{card.sub}</p>
                </div>
                <div className="bg-gray-50 p-2.5 rounded-lg border border-gray-100">
                  <CardIcon className="w-5 h-5 text-gray-400" />
                </div>
              </div>
            )
          })}
        </div>

        {totalCeldas === 0 && (
          <div className="p-6 bg-amber-50 border border-amber-200 rounded-xl flex items-center gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0" />
            <div>
              <p className="text-sm font-semibold text-amber-800">No hay datos registrados</p>
              <p className="text-xs text-amber-700 mt-0.5">
                Configure las celdas del penal para visualizar las estadisticas en el dashboard.
              </p>
            </div>
          </div>
        )}

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div className="px-5 py-3.5 border-b border-gray-100 bg-gray-50">
            <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2">
              <Info className="w-4 h-4 text-gray-400" />
              Distribucion de Ocupacion
            </h3>
          </div>
          <div className="p-5">
            {totalCeldas === 0 ? (
              <p className="text-sm text-gray-400 italic">No hay celdas configuradas.</p>
            ) : (
              <div className="space-y-3">
                {celdas.map(c => {
                  const ocupacion = c.currentOccupancy ?? 0
                  const pct = c.maxCapacity > 0 ? Math.round((ocupacion / c.maxCapacity) * 100) : 0
                  const barColor = pct >= 100 ? 'bg-red-500' : pct >= 80 ? 'bg-yellow-500' : 'bg-emerald-500'
                  return (
                    <div key={c.id} className="flex items-center gap-4">
                      <span className="text-xs font-semibold text-gray-600 w-24 truncate">{c.identifier}</span>
                      <div className="flex-1 h-3 bg-gray-100 rounded-full overflow-hidden">
                        <div className={'h-full rounded-full transition-all ' + barColor} style={{ width: pct + '%' }}></div>
                      </div>
                      <span className="text-xs font-medium text-gray-500 w-16 text-right">{ocupacion}/{c.maxCapacity}</span>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </div>
      </div>
    </SidebarLayout>
  )
}
