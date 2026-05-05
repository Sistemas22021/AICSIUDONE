/**
 * Maneja la captura y visualización de ubicación.
 * - Capturar coordenadas GPS.
 * - Registrar datos de ubicación (municipio, sector, dirección).
 * - Mostrar coordenadas y mapa.
 */

import React from "react";

function FormLabel({ text, required }: { text: string; required?: boolean }) {
    return (
        <label style={{
            fontSize: 9, fontWeight: 600,
            textTransform: "uppercase", letterSpacing: 1,
            color: "var(--text-muted)"
        }}>
            {text} {required && <span style={{ color: "var(--accent-red)" }}>*</span>}
        </label>
    );
}

function Input({ ...props }: React.InputHTMLAttributes<HTMLInputElement>) {
    return <input className="form-control" {...props} />;
}

interface LocationPanelProps {
    municipality: string;
    setMunicipality: (v: string) => void;
    sector: string;
    setSector: (v: string) => void;
    address: string;
    setAddress: (v: string) => void;
    reference: string;
    setReference: (v: string) => void;
    gpsLat: string;
    gpsLng: string;
    captureGPS: () => void;
}

export function LocationPanel({
                                  municipality, setMunicipality,
                                  sector, setSector,
                                  address, setAddress,
                                  reference, setReference,
                                  gpsLat, gpsLng, captureGPS,
                              }: LocationPanelProps) {
    return (
        <div className="map-panel">
            <div className="form-section-title">
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <circle cx="7" cy="6" r="2.5" />
                    <path d="M7 1C4.2 1 2 3.2 2 6c0 4 5 8 5 8s5-4 5-8c0-2.8-2.2-5-5-5z" />
                </svg>
                Geolocalización
            </div>

            {/* GPS */}
            <div className="gps-row">
                <button className="btn-gps" onClick={captureGPS}>
                    <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <circle cx="6.5" cy="6.5" r="3" />
                        <path d="M6.5 1v2M6.5 10v2M1 6.5h2M10 6.5h2" />
                    </svg>
                    Capturar GPS Actual
                </button>
                <div className="coords-display">
                    {gpsLat && gpsLng ? `${gpsLat}° N\n${gpsLng}° W` : "Sin coordenadas"}
                </div>
            </div>

            {/* Municipio y sector */}
            <div className="form-row">
                <div className="form-group">
                    <FormLabel text="Municipio" required />
                    <Input placeholder="Municipio" value={municipality} onChange={(e) => setMunicipality(e.target.value)} />
                </div>
                <div className="form-group">
                    <FormLabel text="Sector / Colonia" required />
                    <Input placeholder="Sector" value={sector} onChange={(e) => setSector(e.target.value)} />
                </div>
            </div>

            {/* Dirección */}
            <div className="form-group">
                <FormLabel text="Dirección" required />
                <Input placeholder="Calle, avenida…" value={address} onChange={(e) => setAddress(e.target.value)} />
            </div>

            {/* Referencia */}
            <div className="form-group">
                <FormLabel text="Referencia" />
                <Input placeholder="Ej: frente al parque central" value={reference} onChange={(e) => setReference(e.target.value)} />
            </div>

            {/* Mapa placeholder */}
            <div className="map-placeholder">
                <div className="map-grid" />
                {gpsLat && gpsLng && <div className="map-dot" />}
                <div className="map-label">
                    {gpsLat && gpsLng ? `${gpsLat}, ${gpsLng}` : "Vista de mapa — integración pendiente"}
                </div>
            </div>
        </div>
    );
}