/** Utilidad para leer el usuario mock actual desde cualquier componente */
export function getMockUser(): { username: string; role: string } {
  try {
    return JSON.parse(localStorage.getItem('mock_user') || '{}')
  } catch {
    return { username: 'Oficial', role: 'Oficial Penitenciario' }
  }
}
