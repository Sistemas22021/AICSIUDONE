import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import CellConfigPage from './modules/cells/CellConfigPage'
import CellMapPage from './modules/cells/CellMapPage'
import InmateRegisterPage from './modules/inmates/InmateRegisterPage'
import InmateRecordPage from './modules/inmates/InmateRecordPage'
import DischargePage from './modules/inmates/DischargePage'
import IncidentRegisterPage from './modules/inmates/IncidentRegisterPage'
import IncidentListPage from './modules/control/IncidentListPage'
import IncidentDetailPage from './modules/control/IncidentDetailPage'
import DeathReportViewPage from './modules/inmates/DeathReportViewPage'
import TemporaryEgressPage from './modules/inmates/TemporaryEgressPage'
import TemporaryReturnPage from './modules/inmates/TemporaryReturnPage'
import DashboardPage from './modules/dashboard/DashboardPage'
import PostPenalPage from './modules/postpenal/PostPenalPage'
import PostPenalProfilePage from './modules/postpenal/PostPenalProfilePage'
import CalendarioPage from './modules/postpenal/CalendarioPage'
import ControlDashboardPage from './modules/control/ControlDashboardPage'
import AuthGuard from './shared/AuthGuard'
import ProtectedRoute from './shared/ProtectedRoute'
import { useAuth } from './shared/authContext'

function HomeRedirect() {
    const auth = useAuth()
    if (auth.hasRole('Administrador del Sistema')) {
        return <Navigate to="/celdas/configurar" replace />
    }
    if (auth.hasRole('Oficial de Seguimiento', 'Supervisor')) {
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
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor']}>
                            <DashboardPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/celdas/configurar" element={
                        <ProtectedRoute allowedRoles={['Administrador del Sistema']}>
                            <CellConfigPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/mapa" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor', 'Administrador del Sistema']}>
                            <CellMapPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/registrar" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario']}>
                            <InmateRegisterPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/expediente/:id" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor']}>
                            <InmateRecordPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/egreso" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor']}>
                            <DischargePage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/egreso-temporal" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor']}>
                            <TemporaryEgressPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/retorno-temporal" element={
                        <ProtectedRoute allowedRoles={['Oficial Penitenciario', 'Supervisor']}>
                            <TemporaryReturnPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor']}>
                            <PostPenalPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post/expediente/:id/perfil" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor']}>
                            <PostPenalProfilePage />
                        </ProtectedRoute>
                    } />

                    <Route path="/post/expediente/:id/calendario" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor']}>
                            <CalendarioPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/control" element={
                        <ProtectedRoute allowedRoles={['Oficial de Seguimiento', 'Supervisor', 'Oficial Penitenciario']}>
                            <ControlDashboardPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/incidentes/registrar/:inmateId" element={
                        <ProtectedRoute allowedRoles={['Supervisor']}>
                            <IncidentRegisterPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/incidentes" element={
                        <ProtectedRoute allowedRoles={['Supervisor']}>
                            <IncidentListPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/incidentes/detalle/:id" element={
                        <ProtectedRoute allowedRoles={['Supervisor']}>
                            <IncidentDetailPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/internos/informe-deceso/:inmateId" element={
                        <ProtectedRoute allowedRoles={['Supervisor']}>
                            <DeathReportViewPage />
                        </ProtectedRoute>
                    } />
                </Routes>
            </AuthGuard>
        </BrowserRouter>
    )
}
