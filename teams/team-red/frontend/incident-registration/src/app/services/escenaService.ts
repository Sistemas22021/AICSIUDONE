/**
 *  Responsable de la comunicación HTTP con /api/v1/escenas,
 *      /api/v1/evidencias y /api/v1/escenas-negativas.
 */

import { apiClient } from './api'
import type { Usuario } from '../types/api.types'

// ─── Tipos espejo de los DTOs del backend ────────────────────────────────────

export interface EscenaResponseDTO {
    id: number
    estadoChecklist: 'INICIADO' | 'COMPLETADO' | 'CERRADO'
    estado: 'ACTIVA' | 'LIBERADA'
    pasoActual: string
    inicioProceso: string | null
    cierreProceso: string | null
    expedienteId: number
    levantadaPor: { id: string; username: string; fullName: string; profilePhotoUrl: string | null; rol: string } | null
    evidencias: EvidenciaResponseDTO[]
    escenasNegativas: EscenaNegativaResponseDTO[]
    liberadaPor: { id: string; username: string; fullName: string; profilePhotoUrl: string | null; rol: string } | null
    horaLiberacion: string | null
    observacionesLiberacion: string | null
    hashLiberacion: string | null
    perimetroAgentes: number | null
    horaAseguramientoPerimetro: string | null
}

export interface EvidenciaResponseDTO {
    id: number
    numeroItem: string
    tipo: string
    descripcion: string
    escenaId: number
    hashIntegridad: string
    timestampRegistro: string
    investigadorNombre: string
}

export interface EvidenciaRequestDTO {
    numeroItem: string
    tipo: string
    descripcion: string
    escenaId: number
    investigadorId?: string
}

export interface EscenaNegativaResponseDTO {
    id: number
    elementoBuscado: string
    areaInspeccionada: string
    resultado: string
    observacion: string
    escenaId: number
    sinElementosNegativos: boolean
}

export interface EscenaNegativaRequestDTO {
    elementoBuscado: string
    areaInspeccionada: string
    resultado: string
    observacion: string
    escenaId: number
    sinElementosNegativos?: boolean
}

export interface EscenaCrearRequestDTO {
    expedienteId: number
    levantadaPorId: string
    perimetroAgentes?: number
    horaAseguramientoPerimetro?: string
}

export interface LiberarEscenaRequestDTO {
    investigadorResponsableId: string
    observaciones?: string
}

export interface LiberarEscenaResponseDTO {
    id: number
    estadoChecklist: string
    estado: string
    pasoActual: string | null
    inicioProceso: string
    cierreProceso: string
    expedienteId: number
    liberadaPor: { id: string; username: string; fullName: string; profilePhotoUrl: string | null; rol: string } | null
    horaLiberacion: string | null
    observacionesLiberacion: string | null
    hashLiberacion: string | null
}

export interface UsuarioResponseDTO {
    id: number
    nombre: string
    identificacion: string
    correo: string
}

// ─── Métodos ──────────────────────────────────────────────────────────────────

/** Crea una nueva escena vinculada a un expediente */
export async function crearEscena(dto: EscenaCrearRequestDTO): Promise<EscenaResponseDTO> {
    const res = await apiClient.post<{ data: EscenaResponseDTO }>('/escenas', dto)
    return res.data
}

/** Obtiene el estado actual de una escena por ID */
export async function obtenerEscena(escenaId: number): Promise<EscenaResponseDTO> {
    const res = await apiClient.get<{ data: EscenaResponseDTO }>(`/escenas/${escenaId}`)
    return res.data
}

/** Avanza al siguiente paso del checklist en el backend */
export async function avanzarPasoEscena(escenaId: number): Promise<EscenaResponseDTO> {
    const res = await apiClient.patch<{ data: EscenaResponseDTO }>(`/escenas/${escenaId}/avanzar`)
    return res.data
}

/** Registra una evidencia en el backend */
export async function crearEvidencia(dto: EvidenciaRequestDTO): Promise<EvidenciaResponseDTO> {
    const res = await apiClient.post<{ data: EvidenciaResponseDTO }>('/evidencias', dto)
    return res.data
}

/** Verifica el hash de integridad de una evidencia ya guardada */
export async function verificarHashEvidencia(evidenciaId: number): Promise<boolean> {
    const res = await apiClient.post<{ data: boolean }>(`/evidencias/${evidenciaId}/verificar-hash`, {})
    return res.data
}

/** Registra una escena negativa en el backend */
export async function crearEscenaNegativa(dto: EscenaNegativaRequestDTO): Promise<EscenaNegativaResponseDTO> {
    const res = await apiClient.post<{ data: EscenaNegativaResponseDTO }>('/escenas-negativas', dto)
    return res.data
}

/** Intenta eliminar una escena negativa — el backend lanzará BusinessException si está protegida */
export async function eliminarEscenaNegativa(id: number): Promise<void> {
    await apiClient.delete<{ data: null }>(`/escenas-negativas/${id}`)
}

/** Lista las escenas negativas de una escena */
export async function obtenerEscenasNegativas(escenaId: number): Promise<EscenaNegativaResponseDTO[]> {
    const res = await apiClient.get<{ data: EscenaNegativaResponseDTO[] }>(`/escenas-negativas/por-escena/${escenaId}`)
    return res.data
}

/** Registra la liberación formal de la escena del crimen */
export async function liberarEscena(
    escenaId: number,
    dto: LiberarEscenaRequestDTO
): Promise<LiberarEscenaResponseDTO> {
    const res = await apiClient.post<{ data: LiberarEscenaResponseDTO }>(
        `/escenas/${escenaId}/liberar`,
        dto
    )
    return res.data
}

export async function iniciarChecklistEscena(escenaId: number): Promise<EscenaResponseDTO> {
    const res = await apiClient.patch<{ data: EscenaResponseDTO }>(`/escenas/${escenaId}/iniciar-checklist`)
    return res.data
}

export async function buscarInvestigadorPorNombre(username: string): Promise<Usuario> {
    const res = await apiClient.get<{ data: Usuario }>(`/usuarios/username/${encodeURIComponent(username)}`)
    return res.data
}
