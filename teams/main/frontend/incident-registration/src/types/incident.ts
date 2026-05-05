/**
 * Define los tipos, constantes y estructura de datos
 * utilizados para representar un incidente delictivo.
 */

export type CrimeType = "HOMICIDIO" | "ROBO" | "DANOS" | "DELITOS_SEXUALES";

export const CRIME_LABELS: Record<CrimeType, string> = {
    HOMICIDIO: "Homicidio",
    ROBO: "Robo",
    DANOS: "Danos",
    DELITOS_SEXUALES: "Delitos Sexuales",
};

export const SUBTYPES: Record<CrimeType, { value: string; label: string }[]> = {
    HOMICIDIO: [
        { value: "INTENCIONAL", label: "Intencional" },
        { value: "CALIFICADO", label: "Calificado (alevosía / ensañamiento)" },
        { value: "CULPOSO", label: "Culposo (accidental)" },
        { value: "FEMICIDIO", label: "Femicidio" },
    ],
    ROBO: [
        { value: "PERSONA_A_PERSONA", label: "Robo persona a persona" },
        { value: "VIVIENDA", label: "Robo a vivienda" },
        { value: "COMERCIO", label: "Robo a comercio" },
        { value: "AGRAVADO", label: "Robo agravado (arma / múltiples)" },
    ],
    DANOS: [
        { value: "PROPIEDAD_PUBLICA", label: "Propiedad pública" },
        { value: "PROPIEDAD_PRIVADA", label: "Propiedad privada" },
    ],
    DELITOS_SEXUALES: [
        { value: "VIOLACION", label: "Violación" },
        { value: "ABUSO_MENOR_13", label: "Abuso sexual (menor de 13 años)" },
        { value: "ABUSO_ADOLESCENTE", label: "Abuso sexual (13–16 años)" },
        { value: "ACTOS_LASCIVOS", label: "Actos lascivos" },
    ],
};

export interface IncidentPayload {
    crimeType: CrimeType;
    crimeSubtype: string;
    incidentDateTime: string;
    municipality: string;
    sector: string;
    address: string;
    gpsLatitude: number | null;
    gpsLongitude: number | null;
    isDenuncia: boolean;
    complainantName: string | null;
    complainantPhone: string | null;
    complainantId: string | null;
    complainantAddress: string | null;
    complainantNationality: string | null;
    complainantRelation: string | null;
    complainantDescription: string | null;
}