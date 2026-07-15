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

export interface ExpedienteDetalleResponse {
  id: number
  folio: string
  estadoExpediente: string
  fechaCreacion: string
  descripcionHecho: string
}

// ─── Expedientes activos ──────────────────────────────────────────────────────

export type EstatusExpediente =
    | 'BORRADOR'
    | 'EN_VALIDACION'
    | 'PROCESADO_Y_SELLADO'
    | 'EN_REVISION'
    | 'ASIGNADO_A_EQUIPO'
    | 'INVESTIGACION_ACTIVA'
    | 'CERRADO'
    | 'SOLICITUD_DE_REAPERTURA'
    | 'ARCHIVADO'

export interface ExpedienteActivo {
  id:                   string
  folioCOPP:            string
  tipoDelito:           string | null
  subtipoDelito:        string | null
  fechaHecho:           string | null
  fechaCreacion:        string
  investigadorAsignado: string
  estatus:              EstatusExpediente
  tieneAlertaPatron:    boolean
  municipio:            string | null
  sector:               string | null
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
  tipoRegistro:     string
  ubicacion:        UbicacionPayload
  delitos:          DelitoPayload[]
  descripcion:      string
  involucrados:     InvolucradoPayload[]
  esDenunciaFormal: boolean
  denunciante:      DenunciantePayload | null
  reporte:          ReportePayload
  fotos:            FotoPayload[]
}

export interface Usuario {
  id:             number
  nombre:         string
  identificacion: string
  correo:         string
}

export interface ExpedienteResumenDTO {
  id: number
  folio: string
  tipoDelito: string | null
  subtipoDelito: string | null
  estatus: EstatusExpediente
  fechaHecho: string | null
  investigadorAsignado: string
  municipio: string | null
}

export interface CasoRequestDTO {
  creadoPorIdentificacion: number
  expedienteIds: number[]
  motivo: string
  alertaOrigenId?: number | null
}

export interface CasoResponseDTO {
  id: number
  codigoCaso: string
  motivo: string
  creadoPor: Usuario
  fechaCreacion: string
  alertaOrigenId: number | null
  expedientes: ExpedienteResumenDTO[]
}

export type EstadoPropuestaMO =
    | 'PENDIENTE'
    | 'SIN_COINCIDENCIAS'
    | 'APROBADA'
    | 'CORREGIDA'
    | 'RECHAZADA'

export interface ExpedienteSimilarMO {
  expedienteId: number
  folio: string
  similitudPorcentaje: number
}

export interface PropuestaModusOperandi {
  id: number
  expedienteId: number
  folioExpediente: string
  version: number
  vigente: boolean
  estado: EstadoPropuestaMO
  caracteristicasComunes: string | null
  posibleFirma: string | null
  consistenciaHorarioZona: string | null
  resumenGenerado: string | null
  nivelConfianza: number | null
  modeloEmbedding: string | null
  modeloChat: string | null
  fechaGeneracion: string
  expedientesSimilares: ExpedienteSimilarMO[]
  revisadoPorExperto: boolean
  analistaRevisorId: number | null
  analistaRevisorNombre: string | null
  justificacionRevision: string | null
  clasificacionManual: string | null
  fechaRevision: string | null
}

export interface AprobarPropuestaMoPayload {
  analistaId: number
}

export interface CorregirPropuestaMoPayload {
  analistaId: number
  caracteristicasComunes?: string
  posibleFirma?: string
  consistenciaHorarioZona?: string
  justificacion: string
}

export interface RechazarPropuestaMoPayload {
  analistaId: number
  clasificacionManual: string
  justificacion: string
}