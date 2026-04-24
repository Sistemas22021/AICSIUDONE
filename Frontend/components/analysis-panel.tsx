'use client'

import { AlertTriangle, AlertCircle } from 'lucide-react'
import { Card } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

export function AnalysisPanel() {
  const alerts = [
    {
      id: 1,
      type: 'discrepancy',
      severity: 'high',
      title: 'Discrepancia Detectada',
      description: 'El sujeto afirma no conocer a "Juan Carlos Reyes", pero el Expediente #442 muestra vinculación previa.',
      time: 'Hace 2 min'
    },
    {
      id: 2,
      type: 'verification',
      severity: 'medium',
      title: 'Verificación Pendiente',
      description: 'Testimonio del hermano requiere validación cruzada con registros de llamadas.',
      time: 'Hace 5 min'
    },
    {
      id: 3,
      type: 'location',
      severity: 'high',
      title: 'Inconsistencia Geográfica',
      description: 'GPS del teléfono ubicó al sujeto en Sector 7 a las 14:45 del 15/03.',
      time: 'Hace 8 min'
    }
  ]

  const timeline = [
    { time: '14:32:45', event: 'Interrogatorio iniciado' },
    { time: '14:33:10', event: 'Consulta sobre Juan Carlos Reyes' },
    { time: '14:33:45', event: 'Pregunta sobre ubicación Sector 7' },
    { time: '14:34:15', event: 'Mención de testigo (hermano)' },
  ]

  return (
    <div className="space-y-4 h-full overflow-y-auto">
      {/* Alerts Header */}
      <Card className="bg-card border-border p-4">
        <h3 className="text-lg font-bold text-foreground">Motor de Análisis</h3>
        <p className="text-xs text-muted-foreground">Alertas Inteligentes & Congruencia</p>
      </Card>

      {/* Alert Cards */}
      <div className="space-y-3">
        {alerts.map((alert) => (
          <Card
            key={alert.id}
            className={`border-l-4 p-4 ${
              alert.severity === 'high'
                ? 'bg-destructive/5 border-l-destructive border-destructive/30'
                : 'bg-chart-4/5 border-l-chart-4 border-chart-4/30'
            }`}
          >
            <div className="flex gap-3">
              <div className="flex-shrink-0 mt-1">
                {alert.severity === 'high' ? (
                  <AlertTriangle size={16} className="text-destructive" />
                ) : (
                  <AlertCircle size={16} className="text-chart-4" />
                )}
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2">
                  <p className="font-bold text-foreground text-sm">{alert.title}</p>
                  <Badge 
                    variant="outline" 
                    className={`text-xs flex-shrink-0 ${
                      alert.severity === 'high'
                        ? 'border-destructive/50 text-destructive'
                        : 'border-chart-4/50 text-chart-4'
                    }`}
                  >
                    {alert.severity === 'high' ? 'CRÍTICA' : 'MEDIA'}
                  </Badge>
                </div>
                
                <p className="text-xs text-foreground mt-2 leading-relaxed">
                  {alert.description}
                </p>
                
                <p className="text-xs text-muted-foreground mt-2">{alert.time}</p>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Timeline */}
      <Card className="bg-card border-border p-4">
        <h4 className="text-xs font-bold text-foreground uppercase tracking-wider mb-3">Línea de Tiempo</h4>
        
        <div className="space-y-3">
          {timeline.map((item, idx) => (
            <div key={idx} className="flex gap-3">
              <div className="flex flex-col items-center">
                <div className="w-2 h-2 rounded-full bg-primary mt-2" />
                {idx < timeline.length - 1 && (
                  <div className="w-0.5 h-8 bg-gradient-to-b from-primary to-transparent mt-1" />
                )}
              </div>
              
              <div className="pb-4">
                <p className="text-xs font-mono text-primary">{item.time}</p>
                <p className="text-xs text-foreground mt-1">{item.event}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* System Status Indicator */}
      <Card className="bg-secondary border-border p-4">
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-chart-3 animate-pulse" />
          <p className="text-xs text-foreground">Sistema activo - Procesando en tiempo real</p>
        </div>
      </Card>
    </div>
  )
}
