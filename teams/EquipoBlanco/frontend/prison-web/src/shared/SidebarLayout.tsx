import { useState, useEffect, useRef } from 'react'
import { NavLink, useLocation, useNavigate } from 'react-router-dom'
import { getMockUser } from './mockUser'
import { useAuth, type UserRole } from './authContext'
import api from './api'

type MenuItem = {
  label: string
  to?: string
  children?: MenuItem[]
  roles?: UserRole[]
}

const ALL_MENU_ITEMS: (MenuItem & { roles?: UserRole[] })[] = [
  // Dashboard
  { 
    label: 'Dashboard', 
    to: '/dashboard',
    roles: [
      'Oficial Penitenciario',
      'Supervisor'
    ]
  },
  // Gestión de Internos
  { 
    label: 'Registro de recluso', 
    to: '/internos/registrar', 
    roles: [
      'Oficial Penitenciario'
    ] 
  },
  { 
    label: 'Egreso temporal', 
    to: '/internos/egreso-temporal', 
    roles: [
      'Oficial Penitenciario',
      'Supervisor'
    ] 
  },
  { 
    label: 'Retorno', 
    to: '/internos/retorno-temporal', 
    roles: [
      'Oficial Penitenciario',
      'Supervisor'
    ] 
  },
  { 
    label: 'Registro de egreso', 
    to: '/internos/egreso', 
    roles: [
      'Oficial Penitenciario',
      'Supervisor'
    ] 
  },
  // Mapa de Celdas
  {
    label: 'Mapa de Celdas',
    roles: [
      'Oficial Penitenciario', 
      'Supervisor',
      'Administrador del Sistema'
    ],
    children: [
      { label: 'Ver Mapa', to: '/mapa' },
      { label: 'Configuración de Celdas', to: '/celdas/configurar', roles: ['Administrador del Sistema'] },
    ],
  },
  // seguimiento post-penitenciario
  {
    label: 'Post-Penitenciario',
    roles: [
      'Oficial de Seguimiento', 
      'Supervisor'
    ],
    children: [
      { label: 'Post-Penitenciario', to: '/post' },
    ],
  },
  { 
    label: 'Control y Disciplina', 
    to: '/control', 
    roles: [
      'Oficial de Seguimiento', 
      'Supervisor',
      'Oficial Penitenciario'
    ] 
  },
  { 
    label: 'Bitácora de Incidentes', 
    to: '/incidentes', 
    roles: [
      'Supervisor'
    ] 
  },
]

export default function SidebarLayout({ children }: { children: React.ReactNode }) {
  const [collapsed, setCollapsed] = useState(false)
  const mockUser = getMockUser()
  const auth = useAuth()
  const { pathname } = useLocation()
  const navigate = useNavigate()

  // ── Campana de notificaciones ─────────────────────────────────────────────
  type AlertaNotif = {
    id: string
    nivel: number
    nombreEgresado: string | null
    cedulaEgresado: string | null
    fechaEmision: string
    accionRequerida: string
    expedienteId: string | null
    estado: string
    observacionAtencion: string | null
  }

  const [alertas, setAlertas] = useState<AlertaNotif[]>([])
  const [campanAbierta, setCampanaAbierta] = useState(false)
  const [atenderModal, setAtenderModal] = useState<AlertaNotif | null>(null)
  const [obsAtencion, setObsAtencion] = useState('')
  const campanRef = useRef<HTMLDivElement>(null)

  const fetchAlertas = async () => {
    try {
      const res = await api.get<AlertaNotif[]>('/alertas', {
        params: { destinatario: auth.username }
      })
      setAlertas(res.data || [])
    } catch {
      // silencioso — el backend puede no estar disponible en dev
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchAlertas()
    const interval = setInterval(fetchAlertas, 30000)
    return () => clearInterval(interval)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth.username])

  // Cerrar dropdown al hacer clic fuera
  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (campanRef.current && !campanRef.current.contains(e.target as Node)) {
        setCampanaAbierta(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleAtender = async () => {
    if (!atenderModal) return
    try {
      await api.put(`/alertas/${atenderModal.id}/atender`, { observacion: obsAtencion })
      setAtenderModal(null)
      setObsAtencion('')
      fetchAlertas()
    } catch {
      alert('Error al atender la alerta')
    }
  }
  // ─────────────────────────────────────────────────────────────────────────

  const MENU_ITEMS = ALL_MENU_ITEMS.filter(item => {
    if (!item.roles) return true
    return auth.hasRole(...item.roles)
  }).map(item => {
    if (item.children) {
      return {
        ...item,
        children: item.children.filter(child => {
          if (!child.roles) return true
          return auth.hasRole(...child.roles)
        })
      }
    }
    return item
  })

  const [expandedMenus, setExpandedMenus] = useState<Record<string, boolean>>(() => {
    const initial: Record<string, boolean> = {}
    for (const item of MENU_ITEMS) {
      if (item.children) {
        initial[item.label] = item.children.some(c => c.to === pathname)
      }
    }
    return initial
  })

  function toggleMenu(label: string) {
    setExpandedMenus(prev => ({ ...prev, [label]: !prev[label] }))
  }

  function handleLogout() {
    localStorage.removeItem('mock_user')
    sessionStorage.clear()
    window.location.reload()
  }

  const linkBase = (isActive: boolean) =>
    `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${
      isActive ? 'bg-gray-100 text-gray-900 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
    }`

  return (
    <>
      <aside className={`fixed left-0 top-0 h-screen bg-white border-r border-gray-200 flex flex-col transition-all duration-300 z-50 ${collapsed ? 'w-[70px]' : 'w-[260px]'}`}>
        <div className="flex items-center h-16 px-4 border-b border-gray-100">
          {!collapsed && <h1 className="text-xl font-bold text-gray-900 tracking-wide">SIGP</h1>}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className={`${collapsed ? 'mx-auto' : 'ml-auto'} p-1.5 text-gray-400 hover:text-gray-600 rounded hover:bg-gray-100`}
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {collapsed
                ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 5l7 7-7 7M5 5l7 7-7 7" />
                : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
              }
            </svg>
          </button>
        </div>

        <nav className="flex-1 px-2 py-4 space-y-1 overflow-y-auto">
          {MENU_ITEMS.map(item => {
            if (item.children) {
              const hasActiveChild = item.children.some(c => c.to === pathname)
              const isExpanded = expandedMenus[item.label]

              if (collapsed) {
                return (
                  <div key={item.label} className="space-y-1">
                    <div className="flex items-center justify-center px-3 py-2.5 rounded-lg text-xs text-gray-400 uppercase tracking-wider">
                      {item.label.slice(0, 1)}
                    </div>
                  </div>
                )
              }

              return (
                <div key={item.label} className="space-y-0.5">
                  <button
                    onClick={() => toggleMenu(item.label)}
                    className={`w-full flex items-center justify-between gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${
                      hasActiveChild ? 'bg-gray-100 text-gray-900 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                    }`}
                  >
                    <span className="truncate">{item.label}</span>
                    <svg
                      className={`w-4 h-4 shrink-0 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
                      fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                  {isExpanded && (
                    <div className="ml-3 pl-3 border-l border-gray-200 space-y-0.5">
                      {item.children.map(child => (
                        <NavLink
                          key={child.to}
                          to={child.to!}
                          className={({ isActive }) =>
                            `${linkBase(isActive)} ${collapsed ? 'justify-center' : ''}`
                          }
                          title={collapsed ? child.label : undefined}
                        >
                          {!collapsed && <span className="truncate text-xs">{child.label}</span>}
                        </NavLink>
                      ))}
                    </div>
                  )}
                </div>
              )
            }

            return (
              <NavLink
                key={item.to}
                to={item.to!}
                className={({ isActive }) =>
                  `${linkBase(isActive)} ${collapsed ? 'justify-center' : ''}`
                }
                title={collapsed ? item.label : undefined}
              >
                {!collapsed && <span className="truncate">{item.label}</span>}
              </NavLink>
            )
          })}
        </nav>

        <div className="border-t border-gray-100">
          {!collapsed && (
            <div className="px-4 py-3">
              <p className="text-sm font-medium text-gray-900">{mockUser.username}</p>
              <p className="text-xs text-gray-400">{mockUser.role}</p>
            </div>
          )}
          <button
            onClick={handleLogout}
            className={`flex items-center gap-3 w-full px-4 py-3 text-sm text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors ${collapsed ? 'justify-center' : ''}`}
            title={collapsed ? 'Cerrar Sesión' : undefined}
          >
            <svg className="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            {!collapsed && <span>Cerrar Sesión</span>}
          </button>
        </div>
      </aside>

      <div className="min-h-screen w-full bg-[#f9fafb]" style={{ marginLeft: collapsed ? 70 : 260, transition: 'margin-left 300ms' }}>
        <header className="sticky top-0 z-40 bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
          <div className="text-sm text-gray-500">
            <span className="font-semibold text-gray-700">SIGP</span>
            <span className="mx-1.5">|</span>
            <span>Gestión Penitenciaria</span>
          </div>
          <div className="flex items-center gap-3">
            {/* ── Campana de notificaciones ── */}
            <div className="relative" ref={campanRef}>
              <button
                id="btn-campana-alertas"
                onClick={() => setCampanaAbierta(prev => !prev)}
                className="relative p-2 text-gray-400 hover:text-indigo-600 rounded-lg hover:bg-indigo-50 transition-colors"
                title="Notificaciones de alertas"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
                {alertas.length > 0 && (
                  <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                    {alertas.length > 9 ? '9+' : alertas.length}
                  </span>
                )}
              </button>

              {/* Dropdown de alertas */}
              {campanAbierta && (
                <div className="absolute right-0 top-full mt-2 w-96 bg-white rounded-2xl shadow-xl border border-gray-200 z-50 overflow-hidden">
                  <div className="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
                    <span className="font-bold text-gray-900 text-sm">Alertas activas</span>
                    <span className="text-xs text-gray-400">{auth.username}</span>
                  </div>
                  <div className="max-h-80 overflow-y-auto divide-y divide-gray-100">
                    {alertas.length === 0 ? (
                      <div className="px-4 py-6 text-center text-sm text-gray-400">
                        Sin alertas activas ✓
                      </div>
                    ) : (
                      alertas.map(a => (
                        <div key={a.id} className="px-4 py-3 hover:bg-gray-50 space-y-1">
                          <div className="flex items-start justify-between gap-2">
                            <div className="flex-1 min-w-0">
                              <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-xs font-bold mb-1 ${
                                a.nivel === 1 ? 'bg-yellow-100 text-yellow-800' :
                                a.nivel === 2 ? 'bg-orange-100 text-orange-800' :
                                'bg-red-100 text-red-800'
                              }`}>
                                Nivel {a.nivel}
                              </span>
                              <p className="text-sm font-semibold text-gray-900 truncate">
                                {a.nombreEgresado ?? 'Egresado desconocido'}
                              </p>
                              <p className="text-xs text-gray-500">
                                Cédula: {a.cedulaEgresado ?? '—'} · {new Date(a.fechaEmision).toLocaleDateString('es-VE')}
                              </p>
                            </div>
                          </div>
                          <div className="flex items-center gap-2 pt-1">
                            {a.expedienteId && (
                              <button
                                id={`btn-ver-expediente-${a.id}`}
                                onClick={() => {
                                  setCampanaAbierta(false)
                                  navigate(`/post/expediente/${a.expedienteId}/calendario`)
                                }}
                                className="text-xs text-indigo-600 hover:text-indigo-800 font-medium hover:underline"
                              >
                                Ver expediente →
                              </button>
                            )}
                            <button
                              id={`btn-atender-alerta-${a.id}`}
                              onClick={() => { setAtenderModal(a); setCampanaAbierta(false) }}
                              className="text-xs text-emerald-600 hover:text-emerald-800 font-medium hover:underline ml-auto"
                            >
                              Marcar atendida
                            </button>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>
            {/* ── Fin campana ── */}

            <button className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100">
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </button>
          </div>
        </header>

        <main className="p-6 min-h-[calc(100vh-57px)] w-full">
          {children}
        </main>
      </div>

      {/* ── Modal: Marcar alerta atendida ── */}
      {atenderModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-gray-900/50 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
            <div className="p-5 bg-emerald-50 text-emerald-900 flex justify-between items-center">
              <h3 className="font-bold text-lg flex items-center gap-2">
                <svg className="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                Marcar alerta como atendida
              </h3>
              <button onClick={() => { setAtenderModal(null); setObsAtencion('') }} className="text-gray-500 hover:text-gray-900">✕</button>
            </div>
            <div className="p-6 space-y-4">
              <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-800 space-y-1">
                <p><strong>Egresado:</strong> {atenderModal.nombreEgresado ?? '—'}</p>
                <p><strong>Cédula:</strong> {atenderModal.cedulaEgresado ?? '—'}</p>
                <p><strong>Fecha:</strong> {new Date(atenderModal.fechaEmision).toLocaleString('es-VE')}</p>
                <p><strong>Nivel:</strong> {atenderModal.nivel}</p>
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-900 mb-1">
                  Observación <span className="font-normal text-gray-400">(opcional)</span>
                </label>
                <textarea
                  id="textarea-obs-atencion"
                  value={obsAtencion}
                  onChange={e => setObsAtencion(e.target.value)}
                  maxLength={500}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                  rows={3}
                  placeholder="Ej: Se realizó llamada de seguimiento, el egresado presentó justificación..."
                />
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button
                  onClick={() => { setAtenderModal(null); setObsAtencion('') }}
                  className="px-4 py-2 text-sm font-semibold text-gray-700 bg-white border border-gray-300 rounded-xl hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  id="btn-confirmar-atencion"
                  onClick={handleAtender}
                  className="px-4 py-2 text-sm font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-sm"
                >
                  Confirmar atención
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
