import { useEffect, useState } from 'react'

function decodeJwtUsername(token: string): string {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub || 'Oficial';
  } catch {
    return 'Oficial';
  }
}

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const [checked, setChecked] = useState(false)

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const tokenFromUrl = params.get('token')

    if (tokenFromUrl) {
      sessionStorage.setItem('token', tokenFromUrl)
      const decodedUser = decodeJwtUsername(tokenFromUrl)
      sessionStorage.setItem('username', decodedUser)
      window.history.replaceState({}, '', window.location.pathname)
    }

    const token = sessionStorage.getItem('token')

    if (!token) {
      const loginUrl = import.meta.env.VITE_LOGIN_MFE_URL as string
      const redirect = encodeURIComponent(window.location.href)
      window.location.href = `${loginUrl}?redirect=${redirect}`
      return
    }

    if (!sessionStorage.getItem('username')) {
      sessionStorage.setItem('username', decodeJwtUsername(token))
    }

    // Delay to avoid sync setState warning
    requestIdleCallback(() => setChecked(true))
  }, [])

  if (!checked) return <div className="p-4 text-center text-gray-500">Verificando sesión...</div>
  return children
}
