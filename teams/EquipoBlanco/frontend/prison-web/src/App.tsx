import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import CellConfigPage from './modules/cells/CellConfigPage'
import CellMapPage from './modules/cells/CellMapPage'
import InmateRegisterPage from './modules/inmates/InmateRegisterPage'
import DashboardPage from './modules/dashboard/DashboardPage'
import PlaceholderPage from './shared/PlaceholderPage'
import { FileText, ShieldAlert } from 'lucide-react'

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/dashboard" />} />
                <Route path="/celdas/configurar" element={<CellConfigPage />} />
                <Route path="/mapa" element={<CellMapPage />} />
                <Route path="/internos/registrar" element={<InmateRegisterPage />} />
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/post" element={
                    <PlaceholderPage
                        title="Post-Penitenciario"
                        description="Gestion de seguimiento post-penitenciario, libertad condicional y reinsercion social."
                        icon={FileText}
                    />
                } />
                <Route path="/control" element={
                    <PlaceholderPage
                        title="Control y Disciplina"
                        description="Administracion de faltas disciplinarias, sanciones y registros de conducta."
                        icon={ShieldAlert}
                    />
                } />
            </Routes>
        </BrowserRouter>
    )
}
