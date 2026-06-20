import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import CellConfigPage from './modules/cells/CellConfigPage'
import CellMapPage from './modules/cells/CellMapPage'
import InmateRegisterPage from './modules/inmates/InmateRegisterPage'
import InmateRecordPage from './modules/inmates/InmateRecordPage'
import DischargePage from './modules/inmates/DischargePage'
import DashboardPage from './modules/dashboard/DashboardPage'
import PostPenalPage from './modules/postpenal/PostPenalPage'
import PostPenalProfilePage from './modules/postpenal/PostPenalProfilePage'
import CalendarioPage from './modules/postpenal/CalendarioPage'
import ControlDashboardPage from './modules/control/ControlDashboardPage'
import PresentacionesPendientesPage from './modules/control/PresentacionesPendientesPage'
import AuthGuard from './shared/AuthGuard'
import ProtectedRoute from './shared/ProtectedRoute'
import { useAuth } from './shared/authContext'

function HomeRedirect() {
    const auth = useAuth()
    if (auth.hasRole('Oficial de Seguimiento', 'Supervisor Policial')) {
        return <Navigate to="/post" replace />
    }
    return <Navigate to="/dashboard" replace />
}

export default function App() {
    return (
        <BrowserRouter>
            <AuthGuard>
                <Routes>
                    <Route path="/" element={<HomeRedirect />} />

                    <Route path="/dashboard" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor Penitenciario', 'Administrador del Sistema']} fallback="/post">
                            <DashboardPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/celdas/configurar" element={
                        <ProtectedRoute allowedRoles={['Administrador del Sistema']}>
                            <CellConfigPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/mapa" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor Penitenciario', 'Administrador del Sistema']}>
                            <CellMapPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/registrar" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Administrador del Sistema']}>
                            <InmateRegisterPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/expediente/:id" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor Penitenciario', 'Administrador del Sistema']}>
                            <InmateRecordPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/egreso" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Administrador del Sistema']}>
                            <DischargePage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor Policial', 'Administrador del Sistema']}>
                            <PostPenalPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post/expediente/:id/perfil" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor Policial', 'Administrador del Sistema']}>
                            <PostPenalProfilePage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post/expediente/:id/calendario" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor Policial', 'Administrador del Sistema']}>
                            <CalendarioPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/control" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor Policial', 'Administrador del Sistema']}>
                            <ControlDashboardPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/control/pendientes" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor Policial', 'Administrador del Sistema']}>
                            <PresentacionesPendientesPage />
                        </ProtectedRoute>
                    } />
                </Routes>
            </AuthGuard>
        </BrowserRouter>
    )
}
