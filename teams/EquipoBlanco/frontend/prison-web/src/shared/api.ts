import axios from 'axios'

const api = axios.create({
    baseURL: (import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8081') + '/api/v1',
})

api.interceptors.request.use((config) => {
    let username = 'Oficial'
    let role = 'Oficial Penitenciario'
    try {
        const mockUser = JSON.parse(localStorage.getItem('mock_user') || '{}')
        username = mockUser.username || 'Oficial'
        role = mockUser.role || 'Oficial Penitenciario'
    } catch { /* usar default */ }

    const token = sessionStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    config.headers['X-User-Name'] = username
    config.headers['X-User-Role'] = role

    return config
})

export default api
