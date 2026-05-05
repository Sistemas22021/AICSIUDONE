/**
 * Maneja toda la lógica del formulario de incidentes.
 *
 * - Gestionae estado (inputs)
 * - Procesae datos
 * - Validae campos
 * - Enviar información al backend
 *
 * No contiene lógica de interfaz (UI).
 */

import { useState, useEffect } from "react";
import { CrimeType, SUBTYPES } from "@/types/incident";
import { createIncident } from "@/services/incidentService";

function pad(n: number) {
    return String(n).padStart(2, "0");
}

export function diffLabel(a: string, b: string): string | null {
    if (!a || !b) return null;
    const [ah, am] = a.split(":").map(Number);
    const [bh, bm] = b.split(":").map(Number);
    const diff = bh * 60 + bm - (ah * 60 + am);
    if (diff <= 0) return null;
    const h = Math.floor(diff / 60);
    const m = diff % 60;
    return `⏱ Δ ${h > 0 ? h + "h " : ""}${m > 0 ? m + "m" : ""}`.trim();
}

export function useIncidentForm() {
    // ── Tipo de delito ──────────────────────────────────────
    const [crimeType, setCrimeType] = useState<CrimeType>("HOMICIDIO");
    const [crimeSubtype, setCrimeSubtype] = useState("INTENCIONAL");

    useEffect(() => {
        setCrimeSubtype(SUBTYPES[crimeType][0].value);
    }, [crimeType]);

    // ── Fechas y horas ──────────────────────────────────────
    const [incidentDate, setIncidentDate] = useState("");
    const [incidentTime, setIncidentTime] = useState("");
    const [reportTime, setReportTime] = useState(() => {
        const n = new Date();
        return `${pad(n.getHours())}:${pad(n.getMinutes())}`;
    });

    // ── Ubicación ───────────────────────────────────────────
    const [municipality, setMunicipality] = useState("");
    const [sector, setSector] = useState("");
    const [address, setAddress] = useState("");
    const [reference, setReference] = useState("");
    const [gpsLat, setGpsLat] = useState("");
    const [gpsLng, setGpsLng] = useState("");

    // ── Denuncia ────────────────────────────────────────────
    const [isDenuncia, setIsDenuncia] = useState(false);
    const [complainantName, setComplainantName] = useState("");
    const [complainantPhone, setComplainantPhone] = useState("");
    const [complainantId, setComplainantId] = useState("");
    const [complainantAddress, setComplainantAddress] = useState("");
    const [complainantNationality, setComplainantNationality] = useState("");
    const [complainantRelation, setComplainantRelation] = useState("");
    const [complainantDescription, setComplainantDescription] = useState("");

    // ── Estado del envío ────────────────────────────────────
    const [saving, setSaving] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState("");

    // ── GPS ─────────────────────────────────────────────────
    function captureGPS() {
        if (!navigator.geolocation) return;
        navigator.geolocation.getCurrentPosition((pos) => {
            setGpsLat(pos.coords.latitude.toFixed(6));
            setGpsLng(pos.coords.longitude.toFixed(6));
        });
    }

    // ── Enviar ──────────────────────────────────────────────
    async function handleSubmit() {
        if (!crimeType || !crimeSubtype || !incidentDate || !incidentTime || !municipality || !sector || !address) {
            setError("Por favor completa todos los campos obligatorios (*).");
            return;
        }

        setError("");
        setSaving(true);

        try {
            await createIncident({
                crimeType,
                crimeSubtype,
                incidentDateTime: `${incidentDate}T${incidentTime}:00`,
                municipality,
                sector,
                address,
                gpsLatitude: gpsLat ? parseFloat(gpsLat) : null,
                gpsLongitude: gpsLng ? parseFloat(gpsLng) : null,
                isDenuncia,
                complainantName: isDenuncia ? complainantName : null,
                complainantPhone: isDenuncia ? complainantPhone : null,
                complainantId: isDenuncia ? complainantId : null,
                complainantAddress: isDenuncia ? complainantAddress : null,
                complainantNationality: isDenuncia ? complainantNationality : null,
                complainantRelation: isDenuncia ? complainantRelation : null,
                complainantDescription: isDenuncia ? complainantDescription : null,
            });

            setSuccess(true);
            setTimeout(() => setSuccess(false), 4000);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Error desconocido");
        } finally {
            setSaving(false);
        }
    }

    return {
        // Tipo de delito
        crimeType, setCrimeType,
        crimeSubtype, setCrimeSubtype,
        // Fechas
        incidentDate, setIncidentDate,
        incidentTime, setIncidentTime,
        reportTime, setReportTime,
        // Ubicación
        municipality, setMunicipality,
        sector, setSector,
        address, setAddress,
        reference, setReference,
        gpsLat, gpsLng, captureGPS,
        // Denuncia
        isDenuncia, setIsDenuncia,
        complainantName, setComplainantName,
        complainantPhone, setComplainantPhone,
        complainantId, setComplainantId,
        complainantAddress, setComplainantAddress,
        complainantNationality, setComplainantNationality,
        complainantRelation, setComplainantRelation,
        complainantDescription, setComplainantDescription,
        // Estado
        saving, success, error,
        handleSubmit,
    };
}