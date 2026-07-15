import { apiClient } from './api'
import type { Usuario } from '../types/api.types'

export async function fetchUsuarios(): Promise<Usuario[]> {
    const res = await apiClient.get<{ data: Usuario[] }>('/usuarios')
    return res.data
}