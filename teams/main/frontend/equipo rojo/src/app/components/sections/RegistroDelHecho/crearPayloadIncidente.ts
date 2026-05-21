/**
 * Transforma el estado del formulario en el JSON
 * que espera el endpoint POST /api/v1/incidentes del backend.
 */

import type { IncidentePayload } from '../../../types/api.types'
import type { DelitoEntry }      from './useDelitoList.ts'
import { TIPOS_REQUIEREN_DENUNCIANTE } from '../../../context/FormContext'

type FormData = any // eslint-disable-line @typescript-eslint/no-explicit-any

export interface CrearPayloadOptions {
    formData: FormData
    delitos:  DelitoEntry[]
    gpsMode:  'actual' | 'none'
}

export function crearPayloadIncidente({
                                          formData,
                                          delitos,
                                          gpsMode,
                                      }: CrearPayloadOptions): IncidentePayload {

    const tipoRegistro: string = formData.tipoRegistro ?? ''

    // ¿Este tipo de registro requiere denunciante?
    const esDenunciaFormal = tipoRegistro === 'denuncia_formal'
    const requiresDenunciante = TIPOS_REQUIEREN_DENUNCIANTE.includes(tipoRegistro as any)

    // Fotos: índice de involucrados que tienen archivo adjunto.
    // El binario se sube en una llamada multipart separada.
    const fotos = (formData.involucrados ?? [])
        .map((inv: { foto?: File | null }, i: number) =>
            inv.foto ? { involucradoIndex: i, archivo: '<<multipart — llamada separada>>' } : null,
        )
        .filter((f: unknown): f is { involucradoIndex: number; archivo: string } => f !== null)

    return {
        // ── Tipo de registro ────────────────────────────────────────────────────
        tipoRegistro,

        // ── Ubicación ───────────────────────────────────────────────────────────
        ubicacion: {
            municipio:  formData.municipio          ?? '',
            sector:     formData.sector             ?? '',
            direccion:  formData.ubicacionDireccion ?? '',
            referencia: formData.referencia         ?? '',
            coordenadas:
                gpsMode === 'actual'
                    ? { lat: formData.lat ?? 0, lng: formData.lng ?? 0 }
                    : null,
        },

        // ── Delitos ──────────────────────────────────────────────────────────────
        delitos: delitos.map(d => ({
            tipoDelito:    d.tipoLabel,
            subtipoDelito: d.subtipoValue,
            fechaHecho:    d.fechaHecho,
            horaInicio:    d.horaInicio,
            horaFin:       d.hechoEnCurso ? null : (d.horaFin || null),
            hechoEnCurso:  d.hechoEnCurso,
        })),

        // ── Descripción ──────────────────────────────────────────────────────────
        descripcion: formData.descripcion ?? '',

        // ── Involucrados ─────────────────────────────────────────────────────────
        involucrados: (formData.involucrados ?? []).map((inv: {
            tipoInvolucrado: string
            nombre:          string
            identificacion:  string
            nacionalidad?:   string
            telefono?:       string
            direccion?:      string
        }) => ({
            tipo:           inv.tipoInvolucrado,
            nombre:         inv.nombre,
            identificacion: inv.identificacion,
            nacionalidad:   inv.nacionalidad  || '',
            telefono:       inv.telefono      || null,
            direccion:      inv.direccion     || null,
        })),

        // ── Denuncia ─────────────────────────────────────────────────────────────
        esDenunciaFormal,
        denunciante: requiresDenunciante
            ? {
                nombre:            formData.denuncianteNombre          ?? '',
                identificacion:    formData.denuncianteIdentificacion  ?? '',
                telefono:          formData.denuncianteTelefono        ?? '',
                nacionalidad:      formData.denuncianteNacionalidad    ?? '',
                direccion:         formData.denuncianteDireccion       ?? '',
                relacionConCrimen: formData.denuncianteRelacion        ?? '',
            }
            : null,

        // ── Reporte ──────────────────────────────────────────────────────────────
        reporte: {
            fechaReporte:      formData.fechaReporte      ?? '',
            horaReporte:       formData.horaReporte       ?? '',
            agenteRegistrador: formData.agenteRegistrador ?? '',
            investigador:      formData.investigador      ?? '',
        },

        // ── Fotos ────────────────────────────────────────────────────────────────
        fotos,
    }
}
