'use client'

import { Check, Shield } from 'lucide-react'
import { Card } from '@/components/ui/card'

export function IdentityPanel() {
  return (
    <div className="space-y-4">
      {/* Detainee Photo & Info */}
      <Card className="bg-card border-border p-4">
        <div className="space-y-3">
          <div className="w-full aspect-square bg-secondary rounded-lg flex items-center justify-center border border-primary/20">
            <div className="text-center">
              <p className="text-xs text-muted-foreground">Foto del detenido</p>
              <p className="text-xs text-muted-foreground mt-1">[Placeholder]</p>
            </div>
          </div>
          
          <div className="space-y-1">
            <h3 className="font-bold text-foreground">Carlos Mendez López</h3>
            <p className="text-sm text-primary font-mono">#EXP-2026-8902</p>
            <p className="text-xs text-muted-foreground">DOB: 15/03/1987</p>
          </div>
        </div>
      </Card>

      {/* Ingesta Multimodal */}
      <Card className="bg-card border-border p-4">
        <h4 className="text-xs font-bold text-foreground mb-3 uppercase tracking-wider">Ingesta Multimodal</h4>
        <div className="space-y-2">
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Huellas dactilares</span>
          </div>
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Fotografía</span>
          </div>
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Registro de voz</span>
          </div>
        </div>
      </Card>

      {/* Derechos Compliance */}
      <Card className="bg-card border-border p-4">
        <h4 className="text-xs font-bold text-foreground mb-3 uppercase tracking-wider">Check-list Derechos</h4>
        <div className="space-y-2">
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Lectura de Miranda</span>
          </div>
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Abogado presente</span>
          </div>
          <div className="flex items-center gap-2 p-2 bg-secondary rounded">
            <Check size={16} className="text-chart-3" />
            <span className="text-sm text-foreground">Asistencia médica verificada</span>
          </div>
        </div>
      </Card>

      {/* Hash Status */}
      <Card className="bg-secondary border border-primary/30 p-4">
        <div className="flex items-start gap-3">
          <Shield size={18} className="text-primary flex-shrink-0 mt-0.5" />
          <div className="space-y-1">
            <p className="text-xs font-bold text-primary uppercase">SHA-256 Activo</p>
            <p className="text-xs text-foreground font-mono break-all">
              a7f3e9c2b1d4f6e8c0a2b4d6f8e0a2b4d6f8e0a2b4d6f8e0a2b4d6f8e0a2b4
            </p>
            <p className="text-xs text-chart-3 mt-2">✓ Inmutable</p>
          </div>
        </div>
      </Card>
    </div>
  )
}
