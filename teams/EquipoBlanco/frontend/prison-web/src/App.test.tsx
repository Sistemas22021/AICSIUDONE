import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import App from './App'

/**
 * @fileoverview Pruebas de integración para el componente principal `App`.
 * * Este conjunto de pruebas verifica el renderizado condicional de la interfaz 
 * y el Control de Acceso Basado en Roles (RBAC) de la aplicación. Utiliza 
 * `localStorage` para inyectar usuarios simulados (`mock_user`) y evaluar 
 * cómo reacciona la navegación y el contenido según los permisos del rol.
 * * Escenarios evaluados:
 * - Aislamiento de pruebas: Limpieza correcta de `localStorage` y `sessionStorage` 
 * antes y después de cada test.
 * - Renderizado base: Presencia de la marca del sistema (SIGP).
 * - Permisos por Rol:
 * - `Oficial Penitenciario`: Visualiza el 'Dashboard', pero se le ocultan 
 * vistas restringidas como la 'Bitácora de Incidentes'.
 * - `Administrador del Sistema`: Tiene acceso a vistas de gestión como 
 * 'Configuración de Celdas'.
 * - `Supervisor`: Tiene acceso a módulos específicos de su cargo como la 
 * 'Bitácora de Incidentes'.
 */

function setMockUser(username: string, role: string) {
  localStorage.setItem('mock_user', JSON.stringify({ username, role }))
}

describe('App', () => {
  beforeEach(() => {
    localStorage.clear()
    sessionStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
    sessionStorage.clear()
  })

  it('renders SIGP branding for Oficial Penitenciario', () => {
    setMockUser('Carlos Méndez', 'Oficial Penitenciario')
    render(<App />)
    expect(screen.getAllByText('SIGP').length).toBeGreaterThan(0)
  })

  it('shows Dashboard menu for Oficial Penitenciario on root', () => {
    setMockUser('Carlos Méndez', 'Oficial Penitenciario')
    render(<App />)
    expect(screen.getByText('Dashboard')).toBeInTheDocument()
  })

  it('shows Configuración de Celdas for Administrador del Sistema on root', () => {
    setMockUser('Ana Rodríguez', 'Administrador del Sistema')
    render(<App />)
    expect(screen.getByText('Configuración de Celdas')).toBeInTheDocument()
  })

  it('hides Bitácora de Incidentes for Oficial Penitenciario (Supervisor only)', () => {
    setMockUser('Carlos Méndez', 'Oficial Penitenciario')
    render(<App />)
    const securityMenu = screen.queryByText('Seguridad y Control')
    if (securityMenu) {
      fireEvent.click(securityMenu)
    }
    expect(screen.queryByText('Bitácora de Incidentes')).not.toBeInTheDocument()
  })

  it('shows Bitácora de Incidentes for Supervisor', () => {
    setMockUser('Pedro Castillo', 'Supervisor')
    render(<App />)
    fireEvent.click(screen.getByText('Seguridad y Control'))
    expect(screen.getByText('Bitácora de Incidentes')).toBeInTheDocument()
  })
})
