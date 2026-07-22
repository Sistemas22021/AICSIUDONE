import { useState, useEffect, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { MapPin, AlertTriangle, Info, DoorOpen, Gauge, X, Clock, UserPlus, Search, ArrowRight, Activity } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'
import { cedulaKeyFilter, formatCedulaIntelligent } from '../../shared/validations'

interface CellData {
  id: string
  identifier: string
  currentOccupancy: number
  conductLevel: string
  lengthMeters: number | null
  widthMeters: number | null
  maxCapacity: number
}

interface InmateData {
  id: string
  status: string
  cellId: string | null
  admissionDate?: string
  dischargeDate?: string
}

interface TransferRequestDto {
  id: string
  inmateId: string
  inmateName: string
  inmateCedula: string
  sourceCellId: string | null
  sourceCellIdentifier: string | null
  targetCellId: string
  targetCellIdentifier: string
  reason: string
  status: string
  requestedBy: string
  createdAt: string
}

export default function DashboardPage() {
  const navigate = useNavigate()
  const [celdas, setCeldas] = useState<CellData[]>([])
  const [reclusos, setReclusos] = useState<InmateData[]>([])
  const [transfers, setTransfers] = useState<TransferRequestDto[]>([])
  const [isInitialLoad, setIsInitialLoad] = useState(true)

  const [rejectionModalTransfer, setRejectionModalTransfer] = useState<TransferRequestDto | null>(null)
  const [rejectionReason, setRejectionReason] = useState('')
  const [rejectionError, setRejectionError] = useState('')
  const [actionLoading, setActionLoading] = useState(false)
  const [searchModalOpen, setSearchModalOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')

  const currentUser = sessionStorage.getItem('username') || 'Oficial'
  const auth = useAuth()
  const canResolveTransfers = auth.hasRole('Supervisor')

  const loadDashboard = async (isPolling = false) => {
    try {
      const [cellsRes, inmatesRes, transfersRes] = await Promise.all([
        api.get<CellData[]>('/cells'),
        api.get<InmateData[]>('/inmates').catch(() => ({ data: [] as InmateData[] })),
        api.get<TransferRequestDto[]>('/transfers/pending').catch(() => ({ data: [] as TransferRequestDto[] }))
      ])
      setCeldas(cellsRes.data || [])
      setReclusos(inmatesRes.data || [])
      setTransfers(transfersRes.data || [])
    } catch (err) {
      console.error('Error loading dashboard data', err)
    } finally {
      if (!isPolling) setIsInitialLoad(false)
    }
  }

  const initialLoadDone = useRef(false)

  useEffect(() => {
    if (!initialLoadDone.current) {
      initialLoadDone.current = true
      loadDashboard(false)
    }
    const interval = setInterval(() => {
      loadDashboard(true)
    }, 5000)
    return () => clearInterval(interval)
  }, [])

  const handleApprove = async (transfer: TransferRequestDto) => {
    if (!confirm(`¿Está seguro de aprobar el traslado de ${transfer.inmateName} a la Celda ${transfer.targetCellIdentifier}?`)) {
      return
    }

    setActionLoading(true)
    try {
      await api.put(`/transfers/${transfer.id}/resolve`, {
        status: 'APROBADO'
      }, {
        headers: { 'X-User-Name': currentUser }
      })
      alert('Traslado aprobado con éxito.')
      loadDashboard()
    } catch (err) {
      console.log(err)
    } finally {
      setActionLoading(false)
    }
  }

  const handleOpenRejection = (transfer: TransferRequestDto) => {
    setRejectionModalTransfer(transfer)
    setRejectionReason('')
    setRejectionError('')
  }

  const handleConfirmRejection = async () => {
    if (!rejectionReason.trim()) {
      setRejectionError('El motivo de rechazo es obligatorio.')
      return
    }
    if (!rejectionModalTransfer) return

    setActionLoading(true)
    setRejectionError('')
    try {
      await api.put(`/transfers/${rejectionModalTransfer.id}/resolve`, {
        status: 'RECHAZADO',
        rejectionReason: rejectionReason.trim()
      }, {
        headers: { 'X-User-Name': currentUser }
      })
      alert('Solicitud de traslado rechazada con éxito.')
      setRejectionModalTransfer(null)
      loadDashboard()
    } catch (err) {
      console.log(err)
    } finally {
      setActionLoading(false)
    }
  }

  const handleSearchExpediente = () => {
    setSearchQuery('')
    setSearchModalOpen(true)
  }

  const isToday = (dateString?: string) => {
    if (!dateString) return false;
    const today = new Date();
    const parts = dateString.split('-');
    if (parts.length < 3) return false;
    return parseInt(parts[0]) === today.getFullYear() &&
           parseInt(parts[1]) === today.getMonth() + 1 &&
           parseInt(parts[2]) === today.getDate();
  };

  const totalCeldas = celdas.length
  const totalReclusos = celdas.reduce((s, c) => s + (c.currentOccupancy ?? 0), 0)
  const capacidadTotal = celdas.reduce((s, c) => s + c.maxCapacity, 0)
  const pctOcupacionNum = capacidadTotal > 0 ? (totalReclusos / capacidadTotal) * 100 : 0
  const pctOcupacion = pctOcupacionNum.toFixed(1)
  
  const ingresosHoy = reclusos.filter(r => isToday(r.admissionDate)).length
  const egresosHoy = reclusos.filter(r => r.status === 'EGRESADO' && isToday(r.dischargeDate)).length
  const celdasDisponibles = celdas.filter(c => (c.currentOccupancy ?? 0) < c.maxCapacity).length
  
  const isCritical = pctOcupacionNum >= 90
  
  const cards = [
    { 
      label: 'Ocupación General', 
      valor: pctOcupacion + '%', 
      sub: isCritical ? 'Nivel Crítico (>=90%)' : 'Nivel Normal', 
      icon: isCritical ? AlertTriangle : Gauge, 
      color: isCritical ? 'text-red-600' : 'text-blue-600',
      bg: isCritical ? 'bg-red-50 border-red-200' : 'bg-white border-gray-200',
      iconBg: isCritical ? 'bg-red-100 text-red-600 animate-pulse' : 'bg-blue-50 text-blue-500'
    },
    { 
      label: 'Celdas Disponibles', 
      valor: celdasDisponibles, 
      sub: celdasDisponibles === 0 ? 'Sin espacio disponible' : 'Listas para ingreso', 
      icon: DoorOpen, 
      color: celdasDisponibles === 0 ? 'text-red-600' : 'text-emerald-600',
      bg: celdasDisponibles === 0 ? 'bg-red-50 border-red-200' : 'bg-white border-gray-200',
      iconBg: celdasDisponibles === 0 ? 'bg-red-100 text-red-600' : 'bg-emerald-50 text-emerald-500'
    },
    { 
      label: 'Ingresos Hoy', 
      valor: ingresosHoy, 
      sub: 'Nuevos registros', 
      icon: UserPlus, 
      color: 'text-indigo-600',
      bg: 'bg-white border-gray-200',
      iconBg: 'bg-indigo-50 text-indigo-500'
    },
    { 
      label: 'Egresos Hoy', 
      valor: egresosHoy, 
      sub: 'Liberaciones / Traslados', 
      icon: ArrowRight, 
      color: 'text-amber-600',
      bg: 'bg-white border-gray-200',
      iconBg: 'bg-amber-50 text-amber-500'
    },
  ]

  if (isInitialLoad) {
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

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-2">
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-2">
              <Activity className="w-8 h-8 text-blue-600" />
              Dashboard Operativo
              {!isInitialLoad && <span className="flex h-3 w-3 ml-2 relative">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-3 w-3 bg-emerald-500"></span>
              </span>}
            </h1>
            <p className="text-sm text-gray-500 mt-1">
              Monitoreo en tiempo real del estado del penal y métricas clave.
            </p>
          </div>
        </div>

        {/* Accesos Rápidos */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          {auth.hasRole('Oficial Penitenciario', 'Administrador del Sistema') && (
            <Link
              to="/internos/registrar"
              className="flex items-center justify-center gap-2 px-4 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all font-semibold shadow-md hover:shadow-lg hover:-translate-y-0.5"
            >
              <UserPlus className="w-5 h-5" />
              Registrar Ingreso
            </Link>
          )}
          {auth.hasRole('Oficial Penitenciario', 'Supervisor', 'Administrador del Sistema') && (
            <Link
              to="/mapa"
              className="flex items-center justify-center gap-2 px-4 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all font-semibold shadow-md hover:shadow-lg hover:-translate-y-0.5"
            >
              <MapPin className="w-5 h-5" />
              Ver Mapa de Celdas
            </Link>
          )}
          {auth.hasRole('Oficial Penitenciario', 'Supervisor', 'Administrador del Sistema') && (
            <button
              onClick={handleSearchExpediente}
              className="flex items-center justify-center gap-2 px-4 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all font-semibold shadow-md hover:shadow-lg hover:-translate-y-0.5 cursor-pointer"
            >
              <Search className="w-5 h-5" />
              Consultar Expediente
            </button>
          )}
        </div>

        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {cards.map((card, i) => {
            const CardIcon = card.icon
            return (
              <div key={i} className={`rounded-xl border p-5 shadow-sm flex items-center justify-between transition-all duration-300 ${card.bg}`}>
                <div className="space-y-1">
                  <p className="text-xs font-bold text-gray-500 uppercase tracking-wider">{card.label}</p>
                  <p className={'text-3xl font-black ' + card.color}>{card.valor}</p>
                  <p className={`text-[11px] font-bold ${card.color} opacity-80`}>{card.sub}</p>
                </div>
                <div className={`p-3 rounded-xl ${card.iconBg}`}>
                  <CardIcon className="w-6 h-6" />
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

        {/* Solicitudes de Traslado Pendientes */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div className="px-5 py-3.5 border-b border-gray-100 bg-gray-50 flex items-center justify-between">
            <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2">
              <Clock className="w-4 h-4 text-gray-400" />
              Solicitudes de Traslado Pendientes
            </h3>
            <span className="bg-blue-100 text-blue-800 text-[10px] font-bold px-2 py-0.5 rounded-full border border-blue-200 uppercase tracking-wide animate-pulse">
              {transfers.length} Pendientes
            </span>
          </div>
          <div className="p-0">
            {transfers.length === 0 ? (
              <p className="text-xs text-gray-400 italic p-6 text-center">No hay solicitudes de traslado pendientes de resolución.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs border-collapse">
                  <thead>
                    <tr className="border-b border-gray-150 bg-gray-50/50 text-gray-400 font-bold uppercase tracking-wider">
                      <th className="px-6 py-3">Recluso</th>
                      <th className="px-6 py-3">Origen</th>
                      <th className="px-6 py-3">Destino</th>
                      <th className="px-6 py-3">Justificación</th>
                      <th className="px-6 py-3">Solicitante</th>
                      <th className="px-6 py-3">Fecha</th>
                      <th className="px-6 py-3 text-right">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {transfers.map(t => {
                      const isOwnRequest = t.requestedBy.toLowerCase() === currentUser.toLowerCase()
                      return (
                        <tr key={t.id} className="hover:bg-gray-50/30 transition-colors">
                          <td className="px-6 py-4.5">
                            <Link
                              to={`/internos/expediente/${t.inmateId}`}
                              state={{ from: '/dashboard' }}
                              className="font-bold text-gray-800 hover:text-blue-600 hover:underline block"
                            >
                              {t.inmateName}
                            </Link>
                            <span className="text-[10px] text-gray-400">C.I. {t.inmateCedula}</span>
                          </td>
                          <td className="px-6 py-4.5 font-semibold text-gray-700">{t.sourceCellIdentifier}</td>
                          <td className="px-6 py-4.5 font-semibold text-gray-750">{t.targetCellIdentifier}</td>
                          <td className="px-6 py-4.5 text-gray-600 italic max-w-xs truncate" title={t.reason}>
                            {t.reason}
                          </td>
                          <td className="px-6 py-4.5 font-medium text-gray-600">{t.requestedBy}</td>
                          <td className="px-6 py-4.5 text-gray-400 font-mono">
                            {new Date(t.createdAt).toLocaleString()}
                          </td>
                          <td className="px-6 py-4.5 text-right">
                            {!canResolveTransfers ? (
                              <span className="inline-block px-2.5 py-1.5 bg-gray-50 border border-gray-200 text-gray-500 rounded-lg text-[10px] font-bold">
                                Solo Supervisor
                              </span>
                            ) : isOwnRequest ? (
                              <span className="inline-block px-2.5 py-1.5 bg-amber-50 border border-amber-200 text-amber-800 rounded-lg text-[10px] font-bold max-w-[190px] text-center leading-tight">
                                No puedes resolver tu propia solicitud (Segregación)
                              </span>
                            ) : (
                              <div className="flex justify-end gap-2">
                                <button
                                  onClick={() => handleOpenRejection(t)}
                                  disabled={actionLoading}
                                  className="px-3 py-1.5 border border-red-200 text-red-650 hover:bg-red-50 rounded-lg text-[11px] font-bold uppercase transition-colors disabled:opacity-50 cursor-pointer bg-white"
                                >
                                  Rechazar
                                </button>
                                <button
                                  onClick={() => handleApprove(t)}
                                  disabled={actionLoading}
                                  className="px-3 py-1.5 bg-emerald-600 text-white hover:bg-emerald-700 rounded-lg text-[11px] font-bold uppercase transition-all disabled:opacity-50 shadow-sm cursor-pointer"
                                >
                                  Aprobar
                                </button>
                              </div>
                            )}
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modal para ingresar el motivo de rechazo */}
      {rejectionModalTransfer && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[70] p-4 transition-all duration-300 animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden border border-gray-250">
            <div className="flex items-center justify-between px-5 py-3.5 bg-red-600 text-white">
              <h3 className="text-sm font-bold uppercase tracking-wider">Rechazar Solicitud de Traslado</h3>
              <button onClick={() => setRejectionModalTransfer(null)} className="p-1 hover:bg-red-700 rounded-lg text-white">
                <X className="w-5 h-5" />
              </button>
            </div>
            
            <div className="p-5 space-y-4 text-xs">
              <p className="text-gray-500 leading-relaxed font-semibold">
                Debe ingresar una justificación detallada de por qué se rechaza la solicitud de traslado a la Celda {rejectionModalTransfer.targetCellIdentifier}.
              </p>

              {rejectionError && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-800 font-semibold animate-pulse">
                  {rejectionError}
                </div>
              )}

              <div className="space-y-1">
                <label className="block text-gray-700 font-bold uppercase tracking-wide">Motivo de Rechazo</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Detalle los motivos de seguridad o administrativos para el rechazo..."
                  value={rejectionReason}
                  onChange={e => setRejectionReason(e.target.value)}
                  className="w-full p-3 border border-gray-300 rounded-lg text-xs focus:ring-1 focus:ring-red-500 focus:border-red-500 focus:outline-none"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  onClick={() => setRejectionModalTransfer(null)}
                  disabled={actionLoading}
                  className="flex-1 py-2.5 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-100 font-bold uppercase tracking-wider transition-colors disabled:opacity-50 cursor-pointer bg-white"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleConfirmRejection}
                  disabled={actionLoading || !rejectionReason.trim()}
                  className="flex-1 py-2.5 bg-red-600 text-white rounded-xl hover:bg-red-700 font-bold uppercase tracking-wider transition-all disabled:bg-gray-300 disabled:cursor-not-allowed shadow-sm flex items-center justify-center gap-1.5 cursor-pointer"
                >
                  {actionLoading ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                  ) : 'Confirmar Rechazo'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal para Buscar Expediente */}
      {searchModalOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[70] p-4 transition-all duration-300 animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden border border-gray-250">
            <div className="flex items-center justify-between px-5 py-3.5 bg-indigo-600 text-white">
              <h3 className="text-sm font-bold uppercase tracking-wider flex items-center gap-2">
                <Search className="w-4 h-4" />
                Consultar Expediente
              </h3>
              <button onClick={() => setSearchModalOpen(false)} className="p-1 hover:bg-indigo-700 rounded-lg text-white">
                <X className="w-5 h-5" />
              </button>
            </div>
            
            <form onSubmit={e => {
              e.preventDefault();
              if (searchQuery.trim()) {
                setSearchModalOpen(false);
                navigate(`/internos/expediente/${searchQuery.trim()}`);
              }
            }} className="p-5 space-y-4">
              <p className="text-gray-500 leading-relaxed font-semibold text-xs">
                Ingrese la Cédula del recluso para consultar su expediente y estatus general dentro del sistema.
              </p>

              <div className="space-y-1">
                <label className="block text-gray-700 font-bold uppercase tracking-wide text-xs">Cédula de Identidad</label>
                <div className="relative">
                  <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    autoFocus
                    type="text"
                    placeholder="Ej: V-12345678"
                    value={searchQuery}
                    onKeyDown={cedulaKeyFilter}
                    onChange={e => setSearchQuery(formatCedulaIntelligent(e.target.value))}
                    className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition-all"
                  />
                </div>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setSearchModalOpen(false)}
                  className="flex-1 py-2.5 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-100 font-bold uppercase tracking-wider text-xs transition-colors bg-white cursor-pointer"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={!searchQuery.trim()}
                  className="flex-1 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 font-bold uppercase tracking-wider text-xs transition-all disabled:bg-gray-300 disabled:cursor-not-allowed shadow-sm flex items-center justify-center cursor-pointer"
                >
                  Buscar Expediente
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </SidebarLayout>
  )
}
