import axios from 'axios'

const VALID_ROLES = ['Oficial Penitenciario', 'Administrador del Sistema', 'Oficial de Seguimiento', 'Supervisor']

const api = axios.create({
    baseURL: (import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8081') + '/api/v1',
})

api.interceptors.request.use((config) => {
    let username
    let role
    try {
        const mockUser = JSON.parse(localStorage.getItem('mock_user') || '{}')
        if (!VALID_ROLES.includes(mockUser.role)) {
            const defaultUser = { username: 'Carlos Méndez', role: 'Oficial Penitenciario' }
            localStorage.setItem('mock_user', JSON.stringify(defaultUser))
            username = defaultUser.username
            role = defaultUser.role
        } else {
            username = mockUser.username || 'Oficial'
            role = mockUser.role || 'Oficial Penitenciario'
        }
    } catch {
        const defaultUser = { username: 'Carlos Méndez', role: 'Oficial Penitenciario' }
        localStorage.setItem('mock_user', JSON.stringify(defaultUser))
        username = defaultUser.username
        role = defaultUser.role
    }

    const token = sessionStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    config.headers['X-User-Name'] = username
    config.headers['X-User-Role'] = role

    return config
})

export default api
