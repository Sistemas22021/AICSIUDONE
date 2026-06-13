import axios from 'axios'

const api = axios.create({
    baseURL: (import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8081') + '/api/v1',
})

api.interceptors.request.use((config) => {
    // Leer sesión mock desde localStorage
    let username = 'Oficial'
    try {
        const mockUser = JSON.parse(localStorage.getItem('mock_user') || '{}')
        username = mockUser.username || 'Oficial'
    } catch { /* usar default */ }

    // Token de compatibilidad (el backend no lo valida, pero lo enviamos por estructura)
    const token = sessionStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    // Header de auditoría: el backend lee esto para registrar quién hace cada acción
    config.headers['X-User-Name'] = username

    return config
})

export default api
