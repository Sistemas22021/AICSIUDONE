/**
 * Renderiza el menú lateral del sistema.
 * - Muestra navegación principal, información del usuario y reloj en tiempo real.
 */

import { useState, useEffect } from "react";

function pad(n: number) {
    return String(n).padStart(2, "0");
}

interface SidebarProps {
    currentUser: string;
}

export function Sidebar({ currentUser }: SidebarProps) {
    const [time, setTime] = useState("");

    useEffect(() => {
        const tick = () => {
            const n = new Date();
            setTime(`${pad(n.getHours())}:${pad(n.getMinutes())}:${pad(n.getSeconds())}`);
        };
        tick();
        const id = setInterval(tick, 1000);
        return () => clearInterval(id);
    }, []);

    const initials = currentUser.substring(0, 2).toUpperCase();

    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <div className="brand-icon">SIEGC </div>
                <div>
                    <div className="brand-name">SIEGC </div>
                    <div className="brand-sub">Sistema de Expedientes</div>
                </div>
            </div>

            <div className="sidebar-user">
                <div className="user-avatar">{initials}</div>
                <div>
                    <div className="user-name">{currentUser}</div>
                    <div className="user-role">Investigador</div>
                </div>
            </div>

            <nav className="sidebar-nav">
                <div className="nav-section-label">Principal</div>
                <div className="nav-item active">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <path d="M3 2h7l3 3v9a1 1 0 01-1 1H3a1 1 0 01-1-1V3a1 1 0 011-1z" />
                        <path d="M10 2v3h3M5 7h6M5 10h4" />
                    </svg>
                    Nuevo Expediente
                </div>

                <div className="nav-section-label">Investigación</div>
                <div className="nav-item">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <circle cx="7" cy="7" r="4.5" /><path d="M10.5 10.5L14 14" />
                    </svg>
                    Búsqueda y Casos
                </div>
                <div className="nav-item">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <circle cx="8" cy="8" r="6" /><path d="M8 4v4l3 2" />
                    </svg>
                    Cadena de Custodia
                </div>
            </nav>

            <div className="sidebar-clock">
                <div className="clock-time">{time}</div>
                <div className="clock-date">
                    {new Date().toLocaleDateString("es", {
                        weekday: "short", day: "numeric",
                        month: "short", year: "numeric"
                    })} · UTC-4
                </div>
            </div>
        </aside>
    );
}