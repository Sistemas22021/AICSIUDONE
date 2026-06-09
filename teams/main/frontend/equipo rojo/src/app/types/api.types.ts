/**
 * Tipos compartidos para la capa de API.
 * Reflejan la estructura de respuesta del backend (incident-service).
 * Tipos TypeScript de todos los objetos que viajan entre el front y el back
 */

// ─── Catálogo de delitos ──────────────────────────────────────────────────────

export interface DelitoSubtipo {
  value: string
  label: string
}

export interface DelitoTipo {
  value: string
  label: string
  subtipos: DelitoSubtipo[]
}

export interface ApiError {
  status:     number
  message:    string
  timestamp?: string
}

// ─── Expedientes activos ──────────────────────────────────────────────────────

export type EstatusExpediente =
    | 'ACTIVO'
    | 'EN_PROCESO'
    | 'SUSPENDIDO'
    | 'CERRADO'

export interface ExpedienteActivo {
  id:                   string
  folioCOPP:            string
  tipoDelito:           string
  subtipoDelito:        string
  fechaHecho:           string
  fechaCreacion:        string
  investigadorAsignado: string
  estatus:              EstatusExpediente
  tieneAlertaPatron:    boolean
  municipio:            string
  sector:               string
}

// ─── Payload de creación de incidente ────────────────────────────────────────

export interface DelitoPayload {
  tipoDelito:    string
  subtipoDelito: string
  fechaHecho:    string
  horaInicio:    string
  horaFin:       string | null
  hechoEnCurso:  boolean
}

export interface CoordenadaPayload {
  lat: number
  lng: number
}

export interface UbicacionPayload {
  municipio:   string
  sector:      string
  direccion:   string
  referencia:  string
  coordenadas: CoordenadaPayload | null
}

export interface InvolucradoPayload {
  tipo:           string
  nombre:         string
  identificacion: string
  nacionalidad:   string
  telefono:       string | null
  direccion:      string | null
}

export interface DenunciantePayload {
  nombre:            string
  identificacion:    string
  telefono:          string
  nacionalidad:      string
  direccion:         string
  relacionConCrimen: string
}

export interface ReportePayload {
  fechaReporte:      string
  horaReporte:       string
  agenteRegistrador: string
  investigador:      string
}

export interface FotoPayload {
  involucradoIndex: number
  archivo:          string
}

export interface IncidentePayload {
  tipoRegistro:     string                // ← NUEVO: denuncia_formal | denuncia_anonima | ...
  ubicacion:        UbicacionPayload
  delitos:          DelitoPayload[]
  descripcion:      string
  involucrados:     InvolucradoPayload[]
  esDenunciaFormal: boolean
  denunciante:      DenunciantePayload | null
  reporte:          ReportePayload
  fotos:            FotoPayload[]
}
