import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ShieldAlert, CalendarClock, AlertTriangle, UserCheck } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import { useAuth } from '../../shared/authContext'

export default function ControlDashboardPage() {
    const { username, hasRole } = useAuth()
    const navigate = useNavigate()
    
    const [stats, setStats] = useState({
        totalActivos: 0,
        pendientesHoy: 0,
        incumplimientos: 0,
        alertasN3: 0
    })
    
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchDashboard = async () => {
            try {
                // In a real app we'd have a specific dashboard endpoint, but here we aggregate
                const [expRes, pendRes] = await Promise.all([
                    api.get('/post-penal/expedientes'),
                    api.get('/calendario/pendientes/hoy', { params: { oficialCedula: hasRole('Oficial de Seguimiento') ? username : '' } })
                ])
                
                const expedientes = expRes.data || []
                const pendientes = pendRes.data || []
                
                // Count expedientes assigned to me (or all if admin)
                let activos = expedientes.length
                if (hasRole('Oficial de Seguimiento')) {
                    activos = expedientes.filter((e: any) => e.oficialAsignadoNombre === username).length
                }
                
                // For MVP, we estimate incumplimientos and alertas based on the data we have
                const incumplimientos = expedientes.reduce((acc: number, e: any) => acc + (e.contadorIncumplimientos || 0), 0)
                const alertas = expedientes.filter((e: any) => e.estado === 'alerta_critica').length
                
                setStats({
                    totalActivos: activos,
                    pendientesHoy: pendientes.length,
                    incumplimientos: incumplimientos,
                    alertasN3: alertas
                })
            } catch (err) {
                console.error("Error fetching control dashboard", err)
            } finally {
                setLoading(false)
            }
        }
        
        fetchDashboard()
        const interval = setInterval(fetchDashboard, 10000)
        return () => clearInterval(interval)
    }, [username])

    return (
        <SidebarLayout>
            <div className="max-w-6xl mx-auto space-y-6">
                <div>
                    <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-3">
                        <ShieldAlert className="w-8 h-8 text-indigo-600" />
                        Control y Disciplina
                    </h1>
                    <p className="text-sm text-gray-500 mt-1">
                        Monitor de presentaciones, incidencias y alertas escalonadas.
                    </p>
                </div>

                {loading ? (
                    <div className="text-center py-10">Cargando...</div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Egresados Activos</p>
                                    <h3 className="text-3xl font-black text-gray-900">{stats.totalActivos}</h3>
                                </div>
                                <div className="p-2 bg-indigo-50 rounded-xl">
                                    <UserCheck className="w-5 h-5 text-indigo-600" />
                                </div>
                            </div>
                        </div>
                        
                        <div className="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm flex flex-col justify-between cursor-pointer hover:border-blue-300 transition-colors"
                             onClick={() => navigate('/control/pendientes')}>
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-1">Presentaciones Hoy</p>
                                    <h3 className="text-3xl font-black text-blue-600">{stats.pendientesHoy}</h3>
                                </div>
                                <div className="p-2 bg-blue-50 rounded-xl">
                                    <CalendarClock className="w-5 h-5 text-blue-600" />
                                </div>
                            </div>
                            <div className="mt-4 text-xs font-semibold text-blue-600 flex items-center gap-1">
                                Ver lista completa &rarr;
                            </div>
                        </div>

                        <div className="bg-white p-5 rounded-2xl border border-orange-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-orange-600 uppercase tracking-wider mb-1">Incumplimientos</p>
                                    <h3 className="text-3xl font-black text-orange-600">{stats.incumplimientos}</h3>
                                </div>
                                <div className="p-2 bg-orange-50 rounded-xl">
                                    <AlertTriangle className="w-5 h-5 text-orange-600" />
                                </div>
                            </div>
                        </div>

                        <div className="bg-white p-5 rounded-2xl border border-red-200 shadow-sm flex flex-col justify-between">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-xs font-bold text-red-600 uppercase tracking-wider mb-1">Alertas N3 (Críticas)</p>
                                    <h3 className="text-3xl font-black text-red-600">{stats.alertasN3}</h3>
                                </div>
                                <div className="p-2 bg-red-50 rounded-xl">
                                    <ShieldAlert className="w-5 h-5 text-red-600" />
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </SidebarLayout>
    )
}
