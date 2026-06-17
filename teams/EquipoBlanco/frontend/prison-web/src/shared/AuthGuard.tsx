import { useEffect, useState } from 'react'

// ─── Roles del sistema penitenciario ───
const MOCK_USERS = [
  { username: 'Carlos Méndez',   role: 'Oficial Penitenciario' },
  { username: 'Ana Rodríguez',   role: 'Administrador del Sistema' },
  { username: 'Luis Hernández',  role: 'Supervisor Penitenciario' },
  { username: 'María González',  role: 'Oficial de Seguimiento' },
  { username: 'Pedro Castillo',  role: 'Supervisor Policial' },
]

/**
 * AuthGuard "Zero-UI":
 * - Si no existe `mock_user` en localStorage, inyecta automáticamente
 *   el primer perfil (Oficial Penitenciario) como sesión por defecto.
 * - El usuario puede cambiar de perfil desde DevTools > Application > Local Storage
 *   editando el JSON de `mock_user` o usando la consola:
 *
 *   localStorage.setItem('mock_user', JSON.stringify({
 *     username: 'Ana Rodríguez',
 *     role: 'Administrador del Sistema'
 *   }))
 *
 * Roles disponibles:
 *   - Oficial Penitenciario
 *   - Administrador del Sistema
 *   - Supervisor Penitenciario
 *   - Oficial de Seguimiento
 *   - Supervisor Policial
 */
export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const [checked, setChecked] = useState(false)

  useEffect(() => {
    // 1. Verificar si ya existe una sesión mock en localStorage
    const existing = localStorage.getItem('mock_user')

    if (!existing) {
      // Inyectar perfil por defecto (Oficial Penitenciario)
      const defaultUser = MOCK_USERS[0]
      localStorage.setItem('mock_user', JSON.stringify(defaultUser))
      console.info(
        '%c🔐 Mock Auth activado',
        'color: #10b981; font-weight: bold;',
        `\nUsuario: ${defaultUser.username}\nRol: ${defaultUser.role}\n\n` +
        `Para cambiar de usuario, abre DevTools (F12) > Application > Local Storage\n` +
        `y edita la clave "mock_user". Roles disponibles:\n` +
        MOCK_USERS.map(u => `  • ${u.role} (${u.username})`).join('\n')
      )
    }

    // 2. Sincronizar token en sessionStorage para compatibilidad con api.ts
    const mockUser = JSON.parse(localStorage.getItem('mock_user')!)
    sessionStorage.setItem('token', 'mock.jwt.dev.' + btoa(mockUser.username))
    sessionStorage.setItem('username', mockUser.username)

    requestIdleCallback(() => setChecked(true))
  }, [])

  if (!checked) return <div className="p-4 text-center text-gray-500">Verificando sesión...</div>
  return children
}
