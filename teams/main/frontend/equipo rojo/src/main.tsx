// Arranca la app: monta <App> en el DOM y carga los estilos globales
import { createRoot } from 'react-dom/client'
import App from './app/App'
import './styles/index.css'

createRoot(document.getElementById('root')!).render(<App />)
