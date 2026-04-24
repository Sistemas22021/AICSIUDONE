'use client'

import { Pause, Lock } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

export function TranscriptionPanel() {
  return (
    <div className="flex flex-col h-full space-y-4">
      {/* Header */}
      <Card className="bg-card border-border p-4">
        <h3 className="text-lg font-bold text-foreground">Acta Preliminar</h3>
        <p className="text-xs text-muted-foreground">Transcripción en Vivo - NLP Real Time</p>
      </Card>

      {/* Transcription Content */}
      <Card className="bg-card border-border p-4 flex-1 overflow-y-auto">
        <div className="space-y-3 text-sm text-foreground leading-relaxed font-mono">
          <p>
            <span className="text-muted-foreground">[14:32:45]</span> Investigador: 
            "¿Cuál es su nombre completo y fecha de nacimiento?"
          </p>
          
          <p>
            <span className="text-muted-foreground">[14:32:52]</span> Detenido: 
            "Carlos Mendez López, 15 de marzo de 1987."
          </p>

          <p>
            <span className="text-muted-foreground">[14:33:10]</span> Investigador: 
            "¿Reconoce usted a <span className="text-primary font-bold bg-primary/10 px-1 rounded">Juan Carlos Reyes</span>?"
          </p>

          <p>
            <span className="text-muted-foreground">[14:33:28]</span> Detenido: 
            "No lo conozco. Nunca he visto a esa persona."
          </p>

          <p>
            <span className="text-muted-foreground">[14:33:45]</span> Investigador: 
            "¿Estuvo presente en <span className="text-primary font-bold bg-primary/10 px-1 rounded">Avenida Principal, Sector 7</span> el 
            <span className="text-primary font-bold bg-primary/10 px-1 rounded">15 de marzo</span>?"
          </p>

          <p>
            <span className="text-muted-foreground">[14:34:02]</span> Detenido: 
            "No, ese día estaba en casa."
          </p>

          <p>
            <span className="text-muted-foreground">[14:34:15]</span> Investigador: 
            "¿Tiene testigos que puedan verificar su ubicación?"
          </p>

          <p>
            <span className="text-muted-foreground">[14:34:30]</span> Detenido: 
            "Sí, mi hermano puede comprobarlo."
          </p>

          <div className="border-l-2 border-accent pl-3 py-2 my-4 bg-accent/5">
            <p className="text-xs text-accent">[TRANSCRIPCIÓN EN VIVO...]</p>
            <p className="text-xs text-muted-foreground mt-1">Esperando siguiente entrada...</p>
          </div>
        </div>
      </Card>

      {/* Recording Controls */}
      <Card className="bg-secondary border-border p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-destructive animate-pulse" />
            <span className="text-sm font-mono text-destructive">Grabación Activa</span>
          </div>
          
          <div className="flex gap-2">
            <Button 
              variant="outline" 
              size="sm"
              className="border-border text-foreground hover:bg-muted hover:text-primary gap-2"
            >
              <Pause size={16} />
              Pausar
            </Button>
            <Button 
              size="sm"
              className="bg-chart-3 hover:bg-chart-3/90 text-background gap-2"
            >
              <Lock size={16} />
              Sellar Acta
            </Button>
          </div>
        </div>
      </Card>
    </div>
  )
}
