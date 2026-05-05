/* Punto de entrada principal */
import React from 'react';
import { createRoot } from 'react-dom/client'; // Importación nombrada
import App from './App';
import './index.css';

const container = document.getElementById('root');
if (!container) throw new Error("No se encontró el elemento root");

const root = createRoot(container);
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);