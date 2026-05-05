/**
 * Muestra el encabezado del expediente.
 * - Mostrar folio único del expediente.
 * - Indicar agente registrador.
 * - Mostrar fecha y hora de apertura.
 * - Mostrar tipo y subtipo de delito.
 */

import { CrimeType, CRIME_LABELS, SUBTYPES } from "@/types/incident";

interface FolioCardProps {
    crimeType: CrimeType;
    crimeSubtype: string;
    currentUser: string;
    time: string;
}

export function FolioCard({ crimeType, crimeSubtype, currentUser, time }: FolioCardProps) {
    const subtypeLabel = SUBTYPES[crimeType].find(s => s.value === crimeSubtype)?.label;

    return (
        <div className="folio-card">
            <div className="folio-block">
                <div className="folio-label">Folio Único</div>
                <div className="folio-number">EXP-2026-NUEVO</div>
            </div>
            <div className="folio-meta">
                <div className="folio-field">
                    <div className="ff-label">Agente Registrador</div>
                    <div className="ff-value">{currentUser}</div>
                </div>
                <div className="folio-field">
                    <div className="ff-label">Fecha de Apertura</div>
                    <div className="ff-value">
                        {new Date().toLocaleDateString("es")}
                        <small>— {time}</small>
                    </div>
                </div>
                <div className="folio-field">
                    <div className="ff-label">Tipo de Delito</div>
                    <div className="ff-value">
                        {CRIME_LABELS[crimeType]}
                        <small>{subtypeLabel}</small>
                    </div>
                </div>
            </div>
        </div>
    );
}