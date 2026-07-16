import { apiClient } from './api'
import type { CasoRequestDTO, CasoResponseDTO } from '../types/api.types'

export async function crearCaso(dto: CasoRequestDTO): Promise<CasoResponseDTO> {
    const res = await apiClient.post<{ data: CasoResponseDTO }>('/casos', dto)
    return res.data
}

export async function obtenerCaso(id: number): Promise<CasoResponseDTO> {
    const res = await apiClient.get<{ data: CasoResponseDTO }>(`/casos/${id}`)
    return res.data
}

export async function obtenerCasos(): Promise<CasoResponseDTO[]> {
    const res = await apiClient.get<{ data: CasoResponseDTO[] }>('/casos')
    return res.data
}