import { render, screen } from '@testing-library/react'
import { describe, it, expect, beforeEach, afterEach } from 'vitest'
import App from './App'

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
    expect(screen.queryByText('Bitácora de Incidentes')).not.toBeInTheDocument()
  })

  it('shows Bitácora de Incidentes for Supervisor', () => {
    setMockUser('Pedro Castillo', 'Supervisor')
    render(<App />)
    expect(screen.getByText('Bitácora de Incidentes')).toBeInTheDocument()
  })
})
