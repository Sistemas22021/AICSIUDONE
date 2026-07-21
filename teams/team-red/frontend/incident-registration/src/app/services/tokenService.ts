const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8090'
const LOGIN_MFE_URL    = import.meta.env.VITE_LOGIN_MFE_URL    || 'http://localhost:3000'

let _accessToken: string | null = null

export function setAccessToken(token: string): void {
    _accessToken = token
}

export function getAccessToken(): string | null {
    return _accessToken
}

export function hasValidToken(): boolean {
    return _accessToken !== null
}

export function clearAccessToken(): void {
    _accessToken = null
}

export async function resolveToken(): Promise<string | null> {

    if (_accessToken) return _accessToken

    const urlParams = new URLSearchParams(window.location.search)
    const tokenFromUrl = urlParams.get('token')
    if (tokenFromUrl) {
        _accessToken = tokenFromUrl

        const cleanUrl = window.location.pathname
        window.history.replaceState({}, document.title, cleanUrl)
        return _accessToken
    }

    try {
        const response = await fetch(`${API_GATEWAY_URL}/api/v1/auth/refresh`, {
            method: 'POST',
            credentials: 'include', // Envía el Cookie automáticamente
        })

        if (response.ok) {
            const data = await response.json()
            _accessToken = data.accessToken
            return _accessToken
        }
    } catch {
        // No hay Cookie, el Gateway no respondió, o el refresh falló → login
    }

    return null
}

export function redirectToLogin(): void {
    const currentUrl = window.location.href
    window.location.href = `${LOGIN_MFE_URL}?redirect=${encodeURIComponent(currentUrl)}`
}

function decodeJwtPayload(token: string): Record<string, unknown> | null {
    try {
        const payload = token.split('.')[1]
        const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
        const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=')
        return JSON.parse(atob(padded))
    } catch {
        return null
    }
}

export function getAuthenticatedUsername(): string | null {
    if (!_accessToken) return null
    const payload = decodeJwtPayload(_accessToken)
    const sub = payload?.sub
    return typeof sub === 'string' && sub.length > 0 ? sub : null
}