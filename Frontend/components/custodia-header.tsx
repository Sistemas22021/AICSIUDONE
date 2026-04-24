'use client'

import { ChevronRight, Shield, LogOut, AlertTriangle } from 'lucide-react'

interface CustodiaHeaderProps {
  currentView: string
  currentStep?: number
}

const viewTitles: Record<string, { title: string; role: string }> = {
  'dashboard': { title: 'Interrogatorios', role: 'Investigador' },
  'guard-officer': { title: 'Oficial de Guardia', role: 'Oficial de Guardia' },
  'interrogation': { title: 'Interrogatorios', role: 'Investigador' },
  'audit': { title: 'Auditoría de Cadena de Custodia', role: 'Auditor' },
  'settings': { title: 'Configuración del Sistema', role: 'Administrador' },
}

export function CustodiaHeader({ currentView, currentStep }: CustodiaHeaderProps) {
  const viewInfo = viewTitles[currentView] || viewTitles['dashboard']
  
  return (
    <header className="bg-card border-b border-border px-6 py-4">
      <div className="flex items-center justify-between gap-4">
        {/* Breadcrumbs */}
        <div className="flex items-center gap-2 text-sm">
          <span className="text-primary font-medium">{viewInfo.title}</span>
          {currentStep !== undefined && (
            <>
              <ChevronRight size={16} className="text-muted-foreground" />
              <span className="text-foreground">Paso {currentStep} de 6</span>
            </>
          )}
          {currentView === 'audit' && (
            <>
              <ChevronRight size={16} className="text-muted-foreground" />
              <span className="text-foreground">Expediente #EXP-2026-8902</span>
            </>
          )}
        </div>

        {/* User Profile & Actions */}
        <div className="flex items-center gap-4">
          {/* User Role Badge */}
          <div className="flex items-center gap-2 px-4 py-2 bg-secondary rounded-lg border border-border">
            <Shield size={16} className="text-primary" />
            <div className="flex flex-col">
              <span className="text-xs text-muted-foreground">Oficial A. Ramírez</span>
              <span className="text-xs font-mono text-primary">{viewInfo.role}</span>
            </div>
          </div>

          {/* Emergency Lock Button */}
          <button
            className="p-2 rounded-lg bg-destructive/10 hover:bg-destructive/20 text-destructive border border-destructive/30 transition-all hover:shadow-lg"
            title="Bloqueo de emergencia"
          >
            <AlertTriangle size={18} />
          </button>

          {/* Logout Button */}
          <button
            className="p-2 rounded-lg bg-secondary hover:bg-muted text-foreground border border-border transition-all"
            title="Cerrar sesión"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </header>
  )
}
