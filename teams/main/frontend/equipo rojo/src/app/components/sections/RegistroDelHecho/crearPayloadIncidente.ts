/**
 * Transforma el estado del formulario en el JSON
 * que espera el endpoint POST /api/v1/incidentes del backend.
 *
 */

import type { IncidentePayload } from '../../../types/api.types'
import type { DelitoEntry } from './useDelitoList.ts'

// ─── Parámetros de entrada ────────────────────────────────────────────────────
//
// formData: usa el tipo FormData de tu FormContext.
// Reemplaza `any` por el tipo real cuando lo tengas disponible:
//
//   import type { FormData } from '../context/FormContext'
//
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type FormData = any

export interface CrearPayloadOptions {
    formData: FormData
    delitos: DelitoEntry[]
    isFormalComplaint: boolean
    gpsMode: 'actual' | 'none'
}

// ─── Función principal ────────────────────────────────────────────────────────

export function crearPayloadIncidente({
                                          formData,
                                          delitos,
                                          isFormalComplaint,
                                          gpsMode,
                                      }: CrearPayloadOptions): IncidentePayload {

    // Fotos: una entrada por involucrado que tenga archivo adjunto.
    // El binario se sube en una llamada multipart separada;
    // aquí solo se registra el índice para que el backend los vincule.
    const fotos = (formData.involucrados ?? [])
        .map((inv: { foto?: File | null }, i: number) =>
            inv.foto ? { involucradoIndex: i, archivo: '<<multipart — llamada separada>>' } : null,
        )
        .filter(
            (f: { involucradoIndex: number; archivo: string } | null): f is { involucradoIndex: number; archivo: string } =>
                f !== null,
        )

    return {
        // ── Ubicación ───────────────────────────────────────────────────────────
        ubicacion: {
            municipio:   formData.municipio          ?? '',
            sector:      formData.sector             ?? '',
            direccion:   formData.ubicacionDireccion ?? '',
            referencia:  formData.referencia         ?? '',
            coordenadas:
                gpsMode === 'actual'
                    ? { lat: formData.lat ?? 0, lng: formData.lng ?? 0 }
                    : null,
        },

        // ── Delitos ──────────────────────────────────────────────────────────────
        // Cada DelitoEntry del hook useDelitoList se mapea a DelitoPayload.
        // subtipoDelito envía el value (slug) — el backend almacena el slug
        // y resuelve el label por su cuenta.
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
            nombre: string
            identificacion: string
            nacionalidad?: string
            telefono?: string
            direccion?: string
        }) => ({
            tipo:           inv.tipoInvolucrado,
            nombre:         inv.nombre,
            identificacion: inv.identificacion,
            nacionalidad:   inv.nacionalidad  || '',
            telefono:       inv.telefono      || null,
            direccion:      inv.direccion     || null,
        })),

        // ── Denuncia formal ──────────────────────────────────────────────────────
        esDenunciaFormal: isFormalComplaint,
        denunciante: isFormalComplaint
            ? {
                nombre:           formData.denuncianteNombre          ?? '',
                identificacion:   formData.denuncianteIdentificacion  ?? '',
                telefono:         formData.denuncianteTelefono        ?? '',
                nacionalidad:     formData.denuncianteNacionalidad    ?? '',
                direccion:        formData.denuncianteDireccion       ?? '',
                relacionConCrimen: formData.denuncianteRelacion       ?? '',
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
