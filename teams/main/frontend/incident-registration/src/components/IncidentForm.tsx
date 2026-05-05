/**
 * Renderiza el formulario principal del incidente.
 * - Capturar datos del hecho delictivo.
 * - Manejar tipo y subtipo de delito.
 * - Registrar fecha y hora del incidente.
 * - Gestionar información del denunciante (si aplica).
 */

import { CrimeType, CRIME_LABELS, SUBTYPES } from "@/types/incident";
import { diffLabel } from "@/hooks/useIncidentForm";
import React from "react";

// ── Componentes UI reutilizables ─────────────────────────
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

function Select({ children, ...props }: React.SelectHTMLAttributes<HTMLSelectElement>) {
    return <select className="form-control" {...props}>{children}</select>;
}

function Textarea({ ...props }: React.TextareaHTMLAttributes<HTMLTextAreaElement>) {
    return <textarea className="form-control" {...props} />;
}

// ── Props del componente ─────────────────────────────────
interface IncidentFormProps {
    crimeType: CrimeType;
    setCrimeType: (v: CrimeType) => void;
    crimeSubtype: string;
    setCrimeSubtype: (v: string) => void;
    incidentDate: string;
    setIncidentDate: (v: string) => void;
    incidentTime: string;
    setIncidentTime: (v: string) => void;
    reportTime: string;
    setReportTime: (v: string) => void;
    isDenuncia: boolean;
    setIsDenuncia: (v: boolean) => void;
    complainantName: string;
    setComplainantName: (v: string) => void;
    complainantPhone: string;
    setComplainantPhone: (v: string) => void;
    complainantId: string;
    setComplainantId: (v: string) => void;
    complainantAddress: string;
    setComplainantAddress: (v: string) => void;
    complainantNationality: string;
    setComplainantNationality: (v: string) => void;
    complainantRelation: string;
    setComplainantRelation: (v: string) => void;
    complainantDescription: string;
    setComplainantDescription: (v: string) => void;
}

export function IncidentForm({
                                 crimeType, setCrimeType,
                                 crimeSubtype, setCrimeSubtype,
                                 incidentDate, setIncidentDate,
                                 incidentTime, setIncidentTime,
                                 reportTime, setReportTime,
                                 isDenuncia, setIsDenuncia,
                                 complainantName, setComplainantName,
                                 complainantPhone, setComplainantPhone,
                                 complainantId, setComplainantId,
                                 complainantAddress, setComplainantAddress,
                                 complainantNationality, setComplainantNationality,
                                 complainantRelation, setComplainantRelation,
                                 complainantDescription, setComplainantDescription,
                             }: IncidentFormProps) {

    const delta = diffLabel(incidentTime, reportTime);

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            <div className="form-section-title">
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <path d="M2 2h7l3 3v7a1 1 0 01-1 1H2a1 1 0 01-1-1V3a1 1 0 011-1z" />
                    <path d="M9 2v3h3M4 7h6M4 10h4" />
                </svg>
                Captura del Hecho
                <span style={{ fontSize: 9, fontWeight: 400, color: "var(--text-muted)", marginLeft: 4 }}>
          Campos obligatorios marcados con *
        </span>
            </div>

            <div className="form-grid">
                {/* Tipo y subtipo */}
                <div className="form-row">
                    <div className="form-group">
                        <FormLabel text="Tipo de Delito" required />
                        <Select value={crimeType} onChange={(e) => setCrimeType(e.target.value as CrimeType)}>
                            {(Object.keys(CRIME_LABELS) as CrimeType[]).map((k) => (
                                <option key={k} value={k}>{CRIME_LABELS[k]}</option>
                            ))}
                        </Select>
                    </div>
                    <div className="form-group">
                        <FormLabel text="Subtipo" required />
                        <Select value={crimeSubtype} onChange={(e) => setCrimeSubtype(e.target.value)}>
                            {SUBTYPES[crimeType].map((s) => (
                                <option key={s.value} value={s.value}>{s.label}</option>
                            ))}
                        </Select>
                    </div>
                </div>

                {/* Fecha */}
                <div className="form-group">
                    <FormLabel text="Fecha del Hecho" required />
                    <Input type="date" value={incidentDate} onChange={(e) => setIncidentDate(e.target.value)} />
                </div>

                {/* Horas + delta */}
                <div className="time-row">
                    <div className="form-group">
                        <FormLabel text="Hora del Hecho" required />
                        <Input type="time" value={incidentTime} onChange={(e) => setIncidentTime(e.target.value)} />
                    </div>
                    <div className="form-group">
                        <FormLabel text="Hora de Reporte" />
                        <Input type="time" value={reportTime} onChange={(e) => setReportTime(e.target.value)} />
                    </div>
                    {delta && <div className="delta-badge">{delta}</div>}
                </div>

                {/* Toggle denuncia */}
                <div
                    className="toggle-row"
                    onClick={() => setIsDenuncia(!isDenuncia)}
                    style={{ cursor: "pointer" }}
                >
                    <div className={`toggle-switch${isDenuncia ? " on" : ""}`} />
                    <span className={`toggle-label${isDenuncia ? " on" : ""}`}>
            {isDenuncia ? "Es una denuncia formal" : "No es una denuncia formal"}
          </span>
                </div>

                {/* Sección denunciante */}
                {isDenuncia && (
                    <div className="complainant-section">
                        <div className="form-section-title" style={{ marginBottom: 0 }}>
                            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" strokeWidth="1.5">
                                <circle cx="7" cy="5" r="3" />
                                <path d="M1 13c0-3 2.7-5 6-5s6 2 6 5" />
                            </svg>
                            Datos del Denunciante
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <FormLabel text="Nombre y Apellido" required />
                                <Input placeholder="Nombre completo" value={complainantName} onChange={(e) => setComplainantName(e.target.value)} />
                            </div>
                            <div className="form-group">
                                <FormLabel text="Teléfono" />
                                <Input placeholder="Número de contacto" value={complainantPhone} onChange={(e) => setComplainantPhone(e.target.value)} />
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <FormLabel text="Identificación" />
                                <Input placeholder="Cédula / Pasaporte" value={complainantId} onChange={(e) => setComplainantId(e.target.value)} />
                            </div>
                            <div className="form-group">
                                <FormLabel text="Nacionalidad" />
                                <Input placeholder="Nacionalidad" value={complainantNationality} onChange={(e) => setComplainantNationality(e.target.value)} />
                            </div>
                        </div>

                        <div className="form-group">
                            <FormLabel text="Dirección del Denunciante" />
                            <Input placeholder="Dirección de residencia" value={complainantAddress} onChange={(e) => setComplainantAddress(e.target.value)} />
                        </div>

                        <div className="form-group">
                            <FormLabel text="Relación con el Crimen" />
                            <Input placeholder="Ej: testigo, familiar, víctima…" value={complainantRelation} onChange={(e) => setComplainantRelation(e.target.value)} />
                        </div>

                        <div className="form-group">
                            <FormLabel text="Descripción del Hecho" />
                            <Textarea
                                placeholder="Relato inicial del denunciante…"
                                value={complainantDescription}
                                onChange={(e) => setComplainantDescription(e.target.value)}
                            />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}