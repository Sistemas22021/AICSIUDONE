import { useState, useEffect, useRef } from 'react'
import { useParams, useLocation, useNavigate, } from 'react-router-dom'
import { ArrowLeft, User, Scale, Fingerprint, FileText, Check, X, Clock, AlertCircle, Move } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'
import TransferRequestModal from './TransferRequestModal'

interface BelongingDto {
  description: string
  quantity: number
}

interface InmateDto {
  id: string
  cedula: string
  firstName: string
  secondName?: string
  firstLastname: string
  secondLastname?: string
  birthDate?: string | null
  crime?: string | null
  caseNumber?: string | null
  court?: string | null
  admissionDate?: string | null
  sentenceYears?: number | null
  sentenceMonths?: number | null
  estimatedReleaseDate?: string | null
  eyeColor?: string | null
  hairColor?: string | null
  bodyBuild?: string | null
  heightCm?: number | null
  weightKg?: number | null
  distinguishingMarks?: string | null
  photoUrl?: string | null
  photoUrl2?: string | null
  photoUrl3?: string | null
  fingerprintUrl?: string | null
  fingerprintRightUrl?: string | null
  status: string
  belongings?: BelongingDto[]
  cellId: string | null
  cellIdentifier?: string | null
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
  status: string // PENDIENTE, APROBADO, RECHAZADO
  requestedBy: string
  resolvedBy?: string | null
  createdAt: string
  resolvedAt?: string | null
  rejectionReason?: string | null
}

export default function InmateRecordPage() {
  const { id } = useParams<{ id: string }>()
  const location = useLocation()
  const navigate = useNavigate()

  const [inmate, setInmate] = useState<InmateDto | null>(null)
  const [transfers, setTransfers] = useState<TransferRequestDto[]>([])
  const [loading, setLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [rejectionModalTransfer, setRejectionModalTransfer] = useState<TransferRequestDto | null>(null)
  const [rejectionReason, setRejectionReason] = useState('')
  const [rejectionError, setRejectionError] = useState('')
  const [actionLoading, setActionLoading] = useState(false)

  const auth = useAuth()
  const currentUser = sessionStorage.getItem('username') || 'Oficial'
  const isFromMap = (location.state as { from?: string })?.from === '/mapa'
  const canResolveTransfers = auth.hasRole('Supervisor Penitenciario', 'Administrador del Sistema')

  const loadData = async () => {
    if (!id) return
    setLoading(true)
    setErrorMsg('')
    try {
      const [inmateRes, transfersRes] = await Promise.all([
        api.get<InmateDto>(`/inmates/${id}`),
        api.get<TransferRequestDto[]>(`/transfers/inmate/${id}`).catch(() => ({ data: [] as TransferRequestDto[] }))
      ])
      setInmate(inmateRes.data)
      setTransfers(transfersRes.data || [])
    } catch {
      setErrorMsg('No se pudo cargar la información del recluso.')
    } finally {
      setLoading(false)
    }
  }

  const initialLoadDone = useRef(false)

  useEffect(() => {
    if (!initialLoadDone.current) {
      initialLoadDone.current = true
      loadData()
    }
  })

  const calculateAge = (birthDate?: string | null) => {
    if (!birthDate) return '—'
    const birth = new Date(birthDate)
    const today = new Date()
    let age = today.getFullYear() - birth.getFullYear()
    const m = today.getMonth() - birth.getMonth()
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
    return age
  }

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
      loadData()
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error al aprobar el traslado.'
      alert(message)
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
      alert('Solicitud de traslado rechazada.')
      setRejectionModalTransfer(null)
      loadData()
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error al rechazar el traslado.'
      setRejectionError(message)
    } finally {
      setActionLoading(false)
    }
  }

  if (loading) {
    return (
      <SidebarLayout>
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="flex flex-col items-center gap-3">
            <div className="w-10 h-10 border-4 border-blue-100 border-t-blue-600 rounded-full animate-spin"></div>
            <p className="text-sm text-gray-500 font-medium">Cargando expediente...</p>
          </div>
        </div>
      </SidebarLayout>
    )
  }

  if (errorMsg || !inmate) {
    return (
      <SidebarLayout>
        <div className="max-w-3xl mx-auto p-6 bg-red-50 border border-red-200 rounded-2xl text-center space-y-4">
          <AlertCircle className="w-12 h-12 text-red-500 mx-auto" />
          <h2 className="text-lg font-bold text-red-900">Error</h2>
          <p className="text-sm text-red-700">{errorMsg || 'Recluso no encontrado.'}</p>
          <button onClick={() => navigate(-1)} className="px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 text-xs font-bold uppercase">
            Volver
          </button>
        </div>
      </SidebarLayout>
    )
  }

  const pendingTransfer = transfers.find(t => t.status === 'PENDIENTE')

  return (
    <SidebarLayout>
      <div className="max-w-5xl mx-auto space-y-6">

        {/* Top bar / Back navigation */}
        <div className="flex items-center justify-between border-b border-gray-150 pb-5">
          <div className="flex items-center gap-3">
            {isFromMap ? (
              <button
                onClick={() => navigate('/mapa')}
                className="flex items-center gap-1.5 px-3 py-1.5 border border-gray-300 rounded-lg hover:bg-gray-150/50 text-gray-750 text-xs font-bold transition-colors cursor-pointer bg-white"
              >
                <ArrowLeft className="w-4 h-4" />
                Volver al mapa
              </button>
            ) : (
              <button
                onClick={() => navigate(-1)}
                className="flex items-center gap-1.5 px-3 py-1.5 border border-gray-300 rounded-lg hover:bg-gray-150/50 text-gray-750 text-xs font-bold transition-colors cursor-pointer bg-white"
              >
                <ArrowLeft className="w-4 h-4" />
                Atrás
              </button>
            )}
            <div>
              <h1 className="text-2xl font-black text-gray-900 leading-tight">Expediente Completo</h1>
              <p className="text-xs text-gray-400 font-semibold uppercase tracking-wider mt-0.5">C.I. {inmate.cedula}</p>
            </div>
          </div>

          {auth.hasRole('Oficial Penitenciario', 'Administrador del Sistema') && (
            <button
              onClick={() => setIsModalOpen(true)}
              className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-xs font-semibold shadow-sm cursor-pointer"
            >
              <Move className="w-4 h-4" />
              Solicitar Traslado
            </button>
          )}
        </div>

        {/* Expediente Overview Card */}
        <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden flex flex-col md:flex-row">
          <div className="p-6 md:p-8 bg-gray-50 border-r border-gray-100 flex flex-col items-center justify-center shrink-0 w-full md:w-64 text-center">
            {inmate.photoUrl ? (
              <img src={inmate.photoUrl} alt="Fotografía frontal" className="w-32 h-32 rounded-2xl object-cover border-2 border-white shadow-md mb-4" />
            ) : (
              <div className="w-32 h-32 bg-gray-200 rounded-2xl flex items-center justify-center border-2 border-white shadow-inner mb-4">
                <User className="w-16 h-16 text-gray-400" />
              </div>
            )}
            <h2 className="text-base font-bold text-gray-900">
              {inmate.firstName} {inmate.secondName || ''}
            </h2>
            <h2 className="text-base font-bold text-gray-900 -mt-1">
              {inmate.firstLastname} {inmate.secondLastname || ''}
            </h2>
            <div className="mt-3.5 flex flex-col gap-2 w-full">
              <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider block ${
                inmate.status === 'ACTIVO_CON_CELDA' ? 'bg-emerald-100 text-emerald-800 border border-emerald-200' : 'bg-amber-100 text-amber-800 border border-amber-250'
              }`}>
                {inmate.status === 'ACTIVO_CON_CELDA' ? 'Asignado' : 'Pendiente Asignación'}
              </span>
              {inmate.cellIdentifier && (
                <span className="text-xs font-bold text-gray-500 bg-gray-100 py-1 px-3.5 rounded-full block border border-gray-200">
                  Celda {inmate.cellIdentifier}
                </span>
              )}
            </div>
          </div>

          <div className="p-6 md:p-8 flex-1 space-y-6">
            
            {/* Informacion Principal Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              
              {/* Datos Personales */}
              <div className="space-y-3">
                <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-1.5">
                  <User className="w-4 h-4 text-gray-400" /> Datos Personales
                </h4>
                <div className="space-y-2 text-xs">
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Fecha de nacimiento:</span><span className="font-bold text-gray-800">{inmate.birthDate || '—'}</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Edad:</span><span className="font-bold text-gray-800">{calculateAge(inmate.birthDate)} años</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Complejión:</span><span className="font-bold text-gray-800">{inmate.bodyBuild || '—'}</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Fisionomía:</span><span className="font-bold text-gray-800">Ojos: {inmate.eyeColor || '—'} / Pelo: {inmate.hairColor || '—'}</span></div>
                  <div className="flex justify-between"><span className="text-gray-500 font-medium">Estatura / Peso:</span><span className="font-bold text-gray-800">{inmate.heightCm ? `${inmate.heightCm} cm` : '—'} / {inmate.weightKg ? `${inmate.weightKg} kg` : '—'}</span></div>
                </div>
              </div>

              {/* Información Judicial */}
              <div className="space-y-3">
                <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-1.5">
                  <Scale className="w-4 h-4 text-gray-400" /> Información Judicial
                </h4>
                <div className="space-y-2 text-xs">
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Delito imputado:</span><span className="font-bold text-gray-800 truncate max-w-[170px]" title={inmate.crime || ''}>{inmate.crime || '—'}</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">N° de expediente:</span><span className="font-bold text-gray-800">{inmate.caseNumber || '—'}</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Tribunal asignado:</span><span className="font-bold text-gray-800 truncate max-w-[170px]">{inmate.court || '—'}</span></div>
                  <div className="flex justify-between border-b border-gray-50 pb-1.5"><span className="text-gray-500 font-medium">Fecha de ingreso:</span><span className="font-bold text-gray-800">{inmate.admissionDate || '—'}</span></div>
                  <div className="flex justify-between"><span className="text-gray-500 font-medium">Condena total:</span><span className="font-bold text-gray-800">{inmate.sentenceYears || 0} años, {inmate.sentenceMonths || 0} meses</span></div>
                </div>
              </div>

            </div>

            {/* Señas particulares */}
            {inmate.distinguishingMarks && (
              <div className="bg-gray-50 rounded-xl p-4.5 border border-gray-150">
                <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                  <FileText className="w-4 h-4 text-gray-400" /> Señas Particulares
                </h4>
                <p className="text-xs text-gray-700 font-semibold leading-relaxed">{inmate.distinguishingMarks}</p>
              </div>
            )}

            {/* Pertenencias */}
            {inmate.belongings && inmate.belongings.length > 0 && (
              <div className="bg-gray-50 rounded-xl p-4.5 border border-gray-150">
                <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 border-b border-gray-200/50 pb-1.5 flex items-center gap-1.5">
                  <FileText className="w-4 h-4 text-gray-400" /> Inventario de Pertenencias
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                  {inmate.belongings.map((b, i) => (
                    <div key={i} className="flex justify-between text-xs border-b border-gray-100 pb-1.5 last:border-0">
                      <span className="text-gray-700 font-semibold">{b.description}</span>
                      <span className="text-gray-500">Cantidad: <strong className="text-gray-800">{b.quantity}</strong></span>
                    </div>
                  ))}
                </div>
              </div>
            )}

          </div>
        </div>

        {/* Biometría */}
        <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-6 space-y-4">
          <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-1.5">
            <Fingerprint className="w-4 h-4 text-gray-400" /> Biometría Completa
          </h4>
          <div className="space-y-4">
            <div>
              <h5 className="text-[10px] font-bold text-gray-400 mb-2 uppercase tracking-wide">Fotografías del Recluso</h5>
              <div className="grid grid-cols-3 gap-3">
                {[
                  { src: inmate.photoUrl, label: 'Foto Frontal' },
                  { src: inmate.photoUrl2, label: 'Perfil Izquierdo' },
                  { src: inmate.photoUrl3, label: 'Perfil Derecho' }
                ].map((photo, index) => (
                  <div key={index} className="flex flex-col items-center bg-gray-50 p-2.5 rounded-xl border border-gray-150 shadow-sm">
                    {photo.src ? (
                      <img src={photo.src} alt={photo.label} className="w-full h-28 object-cover rounded-lg cursor-zoom-in hover:scale-105 transition-transform"
                        onClick={() => { const win = window.open(); if (win) win.document.write(`<img src="${photo.src}" style="max-width:100%; max-height:100vh; display:block; margin:auto;" />`) }} />
                    ) : (
                      <div className="w-full h-28 bg-gray-150 rounded-lg flex flex-col items-center justify-center text-gray-450 border border-dashed border-gray-300">
                        <User className="w-6 h-6 mb-1 text-gray-400" /><span className="text-[9px] font-semibold text-gray-400">Sin imagen</span>
                      </div>
                    )}
                    <span className="text-[10px] text-gray-550 mt-2 font-semibold">{photo.label}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="border-t border-gray-100 pt-4">
              <h5 className="text-[10px] font-bold text-gray-400 mb-2 uppercase tracking-wide">Huellas Dactilares</h5>
              <div className="grid grid-cols-2 gap-4">
                {[
                  { src: inmate.fingerprintUrl, label: 'Mano Izquierda' },
                  { src: inmate.fingerprintRightUrl, label: 'Mano Derecha' }
                ].map((fp, index) => (
                  <div key={index} className="flex flex-col items-center bg-gray-50 p-3.5 rounded-xl border border-gray-150 shadow-sm">
                    {fp.src ? (
                      <img src={fp.src} alt={fp.label} className="w-full h-28 object-contain rounded-lg cursor-zoom-in hover:scale-105 transition-transform"
                        onClick={() => { const win = window.open(); if (win) win.document.write(`<img src="${fp.src}" style="max-width:100%; max-height:100vh; display:block; margin:auto;" />`) }} />
                    ) : (
                      <div className="w-full h-28 bg-gray-150 rounded-lg flex flex-col items-center justify-center text-gray-450 border border-dashed border-gray-300">
                        <Fingerprint className="w-6 h-6 mb-1 text-gray-400" /><span className="text-[9px] font-semibold text-gray-400">Sin huella</span>
                      </div>
                    )}
                    <span className="text-[10px] text-gray-550 mt-2 font-semibold text-gray-550">{fp.label}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Sección de Aprobación Rápida Directa en el Expediente */}
        {pendingTransfer && (
          <div className="p-5 bg-blue-50 border border-blue-200 rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4 animate-fade-in shadow-sm">
            <div className="space-y-1">
              <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-100 text-blue-800 border border-blue-200 uppercase tracking-wide">
                <Clock className="w-3 h-3" /> Traslado Pendiente
              </span>
              <p className="text-sm font-extrabold text-gray-800">
                Se solicitó el traslado a la <strong className="text-blue-700">Celda {pendingTransfer.targetCellIdentifier}</strong>
              </p>
              <p className="text-xs text-gray-600 leading-relaxed max-w-2xl">
                <strong className="text-gray-700">Motivo:</strong> {pendingTransfer.reason} (Solicitado por: <span className="font-semibold">{pendingTransfer.requestedBy}</span>)
              </p>
            </div>
            
            <div className="flex items-center gap-2 w-full md:w-auto self-end md:self-auto shrink-0">
              {!canResolveTransfers ? (
                <span className="inline-block px-3 py-2 bg-gray-50 border border-gray-200 text-gray-500 rounded-lg text-xs font-bold">
                  Solo Supervisor puede resolver
                </span>
              ) : pendingTransfer.requestedBy.toLowerCase() === currentUser.toLowerCase() ? (
                <div className="text-xs font-semibold text-amber-800 bg-amber-50 border border-amber-250 p-2.5 rounded-xl max-w-xs leading-tight">
                  No puedes resolver tu propia solicitud (Segregación de funciones).
                </div>
              ) : (
                <>
                  <button
                    onClick={() => handleOpenRejection(pendingTransfer)}
                    disabled={actionLoading}
                    className="flex-1 md:flex-none px-4 py-2 border border-red-300 text-red-700 hover:bg-red-50 rounded-lg text-xs font-bold uppercase tracking-wider transition-colors disabled:opacity-50 flex items-center justify-center gap-1.5 cursor-pointer bg-white"
                  >
                    <X className="w-4 h-4" /> Rechazar
                  </button>
                  <button
                    onClick={() => handleApprove(pendingTransfer)}
                    disabled={actionLoading}
                    className="flex-1 md:flex-none px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold uppercase tracking-wider transition-all disabled:opacity-50 flex items-center justify-center gap-1.5 shadow-sm cursor-pointer"
                  >
                    <Check className="w-4 h-4" /> Aprobar
                  </button>
                </>
              )}
            </div>
          </div>
        )}

        {/* Historial de Traslados */}
        <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-6 space-y-4">
          <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-100 pb-2 flex items-center gap-1.5">
            <Move className="w-4 h-4 text-gray-400" /> Historial de Traslados
          </h4>
          
          {transfers.length === 0 ? (
            <p className="text-xs text-gray-400 italic text-center py-6">No hay registros de solicitudes de traslado para este recluso.</p>
          ) : (
            <div className="relative pl-6 border-l border-gray-200 space-y-6">
              {transfers.map((t) => {
                const isApproved = t.status === 'APROBADO'
                const isRejected = t.status === 'RECHAZADO'
                
                return (
                  <div key={t.id} className="relative group">
                    {/* Timeline dot */}
                    <div className={`absolute -left-[31px] top-1 w-3.5 h-3.5 rounded-full border-2 bg-white transition-colors duration-300 ${
                      isApproved ? 'border-emerald-500 group-hover:bg-emerald-500' :
                      isRejected ? 'border-red-500 group-hover:bg-red-500' :
                      'border-blue-500 group-hover:bg-blue-500'
                    }`} />
                    
                    <div className="space-y-1.5">
                      <div className="flex flex-wrap items-center justify-between gap-2">
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-bold text-gray-800">
                            Celda {t.sourceCellIdentifier} &rarr; Celda {t.targetCellIdentifier}
                          </span>
                          <span className={`text-[9px] px-2 py-0.5 rounded font-bold uppercase tracking-wider ${
                            isApproved ? 'bg-emerald-50 border border-emerald-200 text-emerald-700' :
                            isRejected ? 'bg-red-50 border border-red-200 text-red-700' :
                            'bg-blue-55/10 border border-blue-200/50 text-blue-700'
                          }`}>
                            {t.status}
                          </span>
                        </div>
                        <span className="text-[10px] text-gray-400 font-medium">
                          {new Date(t.createdAt).toLocaleString()}
                        </span>
                      </div>

                      <div className="bg-gray-50 border border-gray-150 rounded-xl p-3.5 text-xs text-gray-700 space-y-1.5 leading-relaxed">
                        <p><strong className="text-gray-800">Justificación:</strong> {t.reason}</p>
                        <div className="flex flex-wrap gap-4 text-[10px] text-gray-400 font-medium pt-1.5 border-t border-gray-200/50">
                          <span>Solicitante: <strong className="text-gray-500">{t.requestedBy}</strong></span>
                          {t.resolvedBy && (
                            <>
                              <span>Resolutor: <strong className="text-gray-500">{t.resolvedBy}</strong></span>
                              <span>Fecha resolución: <strong className="text-gray-500">{new Date(t.resolvedAt!).toLocaleString()}</strong></span>
                            </>
                          )}
                        </div>
                        {isRejected && t.rejectionReason && (
                          <div className="mt-2 p-2.5 bg-red-50 border border-red-100 rounded-lg text-red-800">
                            <strong className="font-bold">Motivo de Rechazo:</strong> {t.rejectionReason}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>

      </div>

      {/* Modal de Solicitud de Traslado */}
      <TransferRequestModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        inmateId={inmate.id}
        inmateName={`${inmate.firstName} ${inmate.firstLastname}`}
        inmateCedula={inmate.cedula}
        sourceCellId={inmate.cellId}
        sourceCellIdentifier={inmate.cellIdentifier || 'Sin Celda'}
        onSuccess={loadData}
      />

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
                <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-800 font-semibold">
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

    </SidebarLayout>
  )
}
