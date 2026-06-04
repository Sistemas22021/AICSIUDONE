'use client'

import { Shield } from 'lucide-react'

interface CustodiaHeaderProps {
  currentView: string
  currentStep?: number
}

const viewTitles: Record<string, { title: string; role: string }> = {
  'dashboard': { title: 'Toma de Testimonios', role: 'Investigador' },
  'guard-officer': { title: 'Oficial de Guardia', role: 'Oficial de Guardia' },
  'interrogation': { title: 'Toma de Testimonios', role: 'Investigador' },
  'audit': { title: 'Auditoría de Cadena de Custodia', role: 'Auditor' },
  'settings': { title: 'Configuración del Sistema', role: 'Administrador' },
}

export function CustodiaHeader({ currentView, currentStep }: CustodiaHeaderProps) {
  const viewInfo = viewTitles[currentView] || viewTitles['dashboard']
  
  return (
    <header className="bg-card border-b border-border px-4 py-3 sm:px-6 sm:py-4 relative flex items-center justify-between">
      {/* Left section (spacer to balance layout) */}
      <div className="flex-1 min-w-0" />

      {/* Centered Title */}
      <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 text-center pointer-events-none">
        <span className="text-primary font-bold text-sm sm:text-lg md:text-xl truncate tracking-wide">
          {viewInfo.title}
        </span>
      </div>

      {/* Right section: User Profile */}
      <div className="flex-1 flex justify-end shrink-0">
        <div className="flex items-center gap-1.5 sm:gap-2 px-2.5 py-1.5 sm:px-4 sm:py-2 bg-secondary rounded-lg border border-border">
          <Shield size={14} className="text-primary" />
          <div className="hidden sm:flex flex-col text-left">
            <span className="text-xs text-muted-foreground leading-tight">Oficial A. Ramírez</span>
            <span className="text-xs font-mono text-primary leading-tight">{viewInfo.role}</span>
          </div>
        </div>
      </div>
    </header>
  )
}
