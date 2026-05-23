import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import CellConfigPage from './modules/cells/CellConfigPage'
import CellMapPage from './modules/cells/CellMapPage'
import InmateRegisterPage from './modules/inmates/InmateRegisterPage'

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/celdas/configurar" />} />
                <Route path="/celdas/configurar" element={<CellConfigPage />} />
                <Route path="/mapa" element={<CellMapPage />} />
                <Route path="/internos/registrar" element={<InmateRegisterPage />} />
                <Route path="/dashboard" element={<CellConfigPage />} />
                <Route path="/post" element={<CellConfigPage />} />
                <Route path="/control" element={<CellConfigPage />} />
            </Routes>
        </BrowserRouter>
    )
}
