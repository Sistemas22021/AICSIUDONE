/**
 * Componente principal de la aplicación.
 * Su función es ensamblar la interfaz, manejar estado global básico
 * (usuario, tiempo, pestañas) y conectar con el formulario de incidentes.
 */

import { useState, useEffect } from "react";
import { Sidebar } from "./components/Sidebar";
import { Topbar } from "./components/Topbar";
import { FolioCard } from "./components/FolioCard";
import { IncidentForm } from "./components/IncidentForm";
import { LocationPanel } from "./components/LocationPanel";
import { useIncidentForm } from "./hooks/useIncidentForm";

function pad(n: number) {
    return String(n).padStart(2, "0");
}

export default function App() {
    const [activeTab, setActiveTab] = useState(0);
    const [currentUser, setCurrentUser] = useState("Usuario");
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

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");
        if (token) {
            window.history.replaceState({}, "", window.location.pathname);
            try {
                const payload = JSON.parse(atob(token.split(".")[1]));
                setCurrentUser(payload.sub || payload.username || "Usuario");
            } catch { /* token inválido */ }
        }
    }, []);

    const form = useIncidentForm();

    return (
        <>
            <style>{`
        @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&family=IBM+Plex+Sans:wght@300;400;500;600&display=swap');
        :root { --bg-base:#0d1117; --bg-sidebar:#111620; --bg-card:#161c27; --bg-input:#1a2133; --bg-hover:#1f2840; --border:#243048; --border-light:#1e2a3f; --accent-blue:#2b7fff; --accent-blue-dim:#1a4a99; --accent-amber:#f59e0b; --accent-green:#22c55e; --accent-red:#ef4444; --text-primary:#e8edf5; --text-secondary:#7a8aaa; --text-muted:#4a5870; }
        * { margin:0; padding:0; box-sizing:border-box; }
        body { font-family:'IBM Plex Sans',sans-serif; background:var(--bg-base); color:var(--text-primary); font-size:13px; }
        .layout { display:flex; height:100vh; overflow:hidden; }
        .sidebar { width:240px; min-width:240px; background:var(--bg-sidebar); border-right:1px solid var(--border); display:flex; flex-direction:column; }
        .sidebar-brand { display:flex; align-items:center; gap:10px; padding:18px 16px; border-bottom:1px solid var(--border); }
        .brand-icon { width:36px; height:36px; background:var(--accent-blue); border-radius:8px; display:flex; align-items:center; justify-content:center; font-family:'JetBrains Mono',monospace; font-weight:600; font-size:12px; color:#fff; }
        .brand-name { font-weight:600; font-size:14px; }
        .brand-sub { font-size:10px; color:var(--text-muted); text-transform:uppercase; letter-spacing:1px; }
        .sidebar-user { display:flex; align-items:center; gap:10px; padding:14px 16px; border-bottom:1px solid var(--border); }
        .user-avatar { width:34px; height:34px; background:var(--accent-blue-dim); border-radius:50%; display:flex; align-items:center; justify-content:center; font-weight:600; font-size:12px; color:#fff; position:relative; }
        .user-avatar::after { content:''; width:8px; height:8px; background:var(--accent-green); border:2px solid var(--bg-sidebar); border-radius:50%; position:absolute; bottom:0; right:0; }
        .user-name { font-weight:500; font-size:13px; }
        .user-role { font-size:10px; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px; }
        .sidebar-nav { flex:1; padding:12px 8px; overflow-y:auto; }
        .nav-section-label { font-size:9px; font-weight:600; text-transform:uppercase; letter-spacing:1.5px; color:var(--text-muted); padding:8px 8px 4px; margin-top:8px; }
        .nav-item { display:flex; align-items:center; gap:10px; padding:8px 10px; border-radius:6px; cursor:pointer; color:var(--text-secondary); transition:all 0.15s; font-size:13px; }
        .nav-item:hover { background:var(--bg-hover); color:var(--text-primary); }
        .nav-item.active { background:rgba(43,127,255,0.12); color:var(--accent-blue); }
        .sidebar-clock { padding:12px 16px; border-top:1px solid var(--border); text-align:center; }
        .clock-time { font-family:'JetBrains Mono',monospace; font-size:18px; font-weight:500; color:var(--text-secondary); letter-spacing:2px; }
        .clock-date { font-size:10px; color:var(--text-muted); text-transform:uppercase; letter-spacing:1px; margin-top:2px; }
        .main { flex:1; display:flex; flex-direction:column; overflow:hidden; }
        .topbar { display:flex; align-items:center; gap:12px; padding:0 20px; height:56px; border-bottom:1px solid var(--border); background:var(--bg-sidebar); flex-shrink:0; }
        .breadcrumb { display:flex; align-items:center; gap:6px; color:var(--text-muted); font-size:13px; }
        .breadcrumb span { color:var(--text-primary); font-weight:500; }
        .topbar-actions { margin-left:auto; display:flex; align-items:center; gap:8px; }
        .btn-primary { display:flex; align-items:center; gap:6px; background:var(--accent-blue); color:#fff; border:none; padding:7px 14px; border-radius:6px; font-size:12px; font-weight:500; cursor:pointer; transition:opacity 0.15s; font-family:'IBM Plex Sans',sans-serif; }
        .btn-primary:hover:not(:disabled) { opacity:0.85; }
        .btn-primary:disabled { opacity:0.5; cursor:not-allowed; }
        .btn-primary.success { background:var(--accent-green); }
        .content { flex:1; overflow-y:auto; padding:20px; display:flex; flex-direction:column; gap:16px; }
        .folio-card { background:var(--bg-card); border:1px solid var(--border); border-radius:10px; display:flex; align-items:center; overflow:hidden; }
        .folio-block { background:#1b3a8a; padding:16px 20px; border-right:1px solid #2a4fa0; min-width:180px; }
        .folio-label { font-size:9px; text-transform:uppercase; letter-spacing:1.5px; color:#7a9fd4; margin-bottom:4px; }
        .folio-number { font-family:'JetBrains Mono',monospace; font-size:18px; font-weight:600; color:#fff; }
        .folio-meta { flex:1; display:flex; }
        .folio-field { padding:12px 20px; border-right:1px solid var(--border); flex:1; }
        .folio-field:last-child { border-right:none; }
        .ff-label { font-size:9px; text-transform:uppercase; letter-spacing:1px; color:var(--text-muted); margin-bottom:4px; }
        .ff-value { font-size:13px; font-weight:500; }
        .ff-value small { display:block; font-size:11px; color:var(--text-secondary); font-weight:400; }
        .tabs-container { background:var(--bg-card); border:1px solid var(--border); border-radius:10px; overflow:hidden; flex:1; display:flex; flex-direction:column; }
        .tabs-header { display:flex; border-bottom:1px solid var(--border); padding:0 4px; }
        .tab { padding:14px 20px; font-size:12px; font-weight:500; color:var(--text-muted); cursor:pointer; border-bottom:2px solid transparent; transition:all 0.2s; margin-bottom:-1px; white-space:nowrap; }
        .tab:hover { color:var(--text-secondary); }
        .tab.active { color:var(--accent-blue); border-bottom-color:var(--accent-blue); }
        .tab-body { padding:20px; display:grid; grid-template-columns:1fr 1fr; gap:20px; overflow-y:auto; flex:1; }
        .tab-placeholder { padding:40px; display:flex; align-items:center; justify-content:center; text-align:center; color:var(--text-muted); flex-direction:column; gap:8px; flex:1; }
        .form-section-title { font-size:11px; font-weight:600; text-transform:uppercase; letter-spacing:1px; color:var(--text-muted); margin-bottom:14px; display:flex; align-items:center; gap:8px; }
        .form-grid { display:grid; gap:12px; }
        .form-row { display:grid; grid-template-columns:1fr 1fr; gap:12px; }
        .form-group { display:flex; flex-direction:column; gap:5px; }
        .form-control { background:var(--bg-input); border:1px solid var(--border); border-radius:6px; color:var(--text-primary); font-family:'IBM Plex Sans',sans-serif; font-size:13px; padding:8px 10px; outline:none; transition:border-color 0.15s; width:100%; }
        .form-control:focus { border-color:var(--accent-blue); }
        .form-control::placeholder { color:var(--text-muted); }
        select.form-control { appearance:none; cursor:pointer; background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6' fill='none'%3E%3Cpath d='M1 1l4 4 4-4' stroke='%234a5870' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E"); background-repeat:no-repeat; background-position:right 10px center; padding-right:28px; }
        textarea.form-control { resize:none; min-height:80px; line-height:1.5; }
        .time-row { display:grid; grid-template-columns:1fr 1fr auto; gap:12px; align-items:end; }
        .delta-badge { display:inline-flex; align-items:center; gap:4px; background:rgba(245,158,11,0.12); border:1px solid rgba(245,158,11,0.25); color:var(--accent-amber); font-size:10px; font-weight:600; padding:8px 7px; border-radius:6px; font-family:'JetBrains Mono',monospace; white-space:nowrap; }
        .toggle-row { display:flex; align-items:center; gap:10px; background:var(--bg-input); border:1px solid var(--border); border-radius:6px; padding:10px 12px; }
        .toggle-switch { width:36px; height:20px; background:var(--border); border-radius:10px; position:relative; cursor:pointer; transition:background 0.2s; flex-shrink:0; }
        .toggle-switch.on { background:var(--accent-blue); }
        .toggle-switch::after { content:''; width:14px; height:14px; background:#fff; border-radius:50%; position:absolute; top:3px; left:3px; transition:left 0.2s; }
        .toggle-switch.on::after { left:19px; }
        .toggle-label { font-size:12px; font-weight:500; color:var(--text-secondary); }
        .toggle-label.on { color:var(--accent-blue); }
        .complainant-section { background:rgba(43,127,255,0.05); border:1px solid rgba(43,127,255,0.15); border-radius:8px; padding:14px; display:flex; flex-direction:column; gap:12px; }
        .map-panel { display:flex; flex-direction:column; gap:14px; }
        .gps-row { display:grid; grid-template-columns:1fr auto; gap:10px; align-items:end; }
        .btn-gps { display:flex; align-items:center; gap:6px; background:rgba(43,127,255,0.1); border:1px solid rgba(43,127,255,0.3); color:var(--accent-blue); padding:8px 12px; border-radius:6px; font-size:12px; font-weight:500; cursor:pointer; white-space:nowrap; font-family:'IBM Plex Sans',sans-serif; transition:background 0.15s; }
        .btn-gps:hover { background:rgba(43,127,255,0.18); }
        .coords-display { background:var(--bg-input); border:1px solid var(--border); border-radius:6px; padding:8px 10px; font-family:'JetBrains Mono',monospace; font-size:11px; color:var(--text-secondary); text-align:right; }
        .map-placeholder { background:var(--bg-input); border:1px solid var(--border); border-radius:8px; height:140px; display:flex; align-items:center; justify-content:center; flex-direction:column; gap:8px; position:relative; overflow:hidden; }
        .map-grid { position:absolute; inset:0; background-image:linear-gradient(var(--border-light) 1px,transparent 1px),linear-gradient(90deg,var(--border-light) 1px,transparent 1px); background-size:30px 30px; opacity:0.5; }
        .map-dot { width:12px; height:12px; background:var(--accent-blue); border:3px solid rgba(43,127,255,0.3); border-radius:50%; position:relative; z-index:1; animation:pulse 2s infinite; }
        @keyframes pulse { 0%,100%{box-shadow:0 0 0 0 rgba(43,127,255,0.4);} 50%{box-shadow:0 0 0 10px rgba(43,127,255,0);} }
        .map-label { font-size:10px; color:var(--text-muted); position:relative; z-index:1; background:var(--bg-input); padding:2px 8px; border-radius:3px; border:1px solid var(--border); }
        .alert { padding:10px 14px; border-radius:6px; font-size:12px; font-weight:500; }
        .alert-error { background:rgba(239,68,68,0.1); border:1px solid rgba(239,68,68,0.3); color:var(--accent-red); }
        .alert-success { background:rgba(34,197,94,0.1); border:1px solid rgba(34,197,94,0.3); color:var(--accent-green); }
        ::-webkit-scrollbar{width:5px;} ::-webkit-scrollbar-track{background:transparent;} ::-webkit-scrollbar-thumb{background:var(--border);border-radius:3px;}
      `}</style>

            <div className="layout">
                <Sidebar currentUser={currentUser} />

                <main className="main">
                    <Topbar saving={form.saving} success={form.success} onSave={form.handleSubmit} />

                    <div className="content">
                        <FolioCard crimeType={form.crimeType} crimeSubtype={form.crimeSubtype} currentUser={currentUser} time={time} />

                        {form.error && <div className="alert alert-error">{form.error}</div>}
                        {form.success && <div className="alert alert-success">✓ Expediente guardado correctamente.</div>}

                        <div className="tabs-container">
                            <div className="tabs-header">
                                <div className={`tab${activeTab === 0 ? " active" : ""}`} onClick={() => setActiveTab(0)}>A · Registro del Hecho</div>
                                <div className={`tab${activeTab === 1 ? " active" : ""}`} onClick={() => setActiveTab(1)}>B · Escena y Evidencia</div>
                                <div className={`tab${activeTab === 2 ? " active" : ""}`} onClick={() => setActiveTab(2)}>C · Inteligencia IA</div>
                            </div>

                            {activeTab === 0 && (
                                <div className="tab-body">
                                    <IncidentForm {...form} />
                                    <LocationPanel municipality={form.municipality} setMunicipality={form.setMunicipality} sector={form.sector} setSector={form.setSector} address={form.address} setAddress={form.setAddress} reference={form.reference} setReference={form.setReference} gpsLat={form.gpsLat} gpsLng={form.gpsLng} captureGPS={form.captureGPS} />
                                </div>
                            )}

                            {activeTab === 1 && (
                                <div className="tab-placeholder">
                                    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeWidth="1.5" style={{opacity:0.3}}><rect x="8" y="6" width="24" height="28" rx="2"/><path d="M14 14h12M14 20h12M14 26h8"/></svg>
                                    <div style={{fontSize:13}}>Escena y Evidencia</div>
                                    <div style={{fontSize:11}}>Completar sección A primero</div>
                                </div>
                            )}

                            {activeTab === 2 && (
                                <div className="tab-placeholder">
                                    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" stroke="currentColor" strokeWidth="1.5" style={{opacity:0.3}}><polyline points="6,30 14,18 20,23 28,10 34,16"/><circle cx="34" cy="16" r="3" fill="currentColor" stroke="none"/></svg>
                                    <div style={{fontSize:13}}>Inteligencia IA</div>
                                    <div style={{fontSize:11}}>Disponible tras sellar el expediente</div>
                                </div>
                            )}
                        </div>
                    </div>
                </main>
            </div>
        </>
    );
}