'use client'

import { Download, Search, Filter } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { useState } from 'react'

interface AuditRecord {
  id: string
  timestamp: string
  action: string
  officer: string
  signature: string
  hash: string
  status: 'verified' | 'pending' | 'failed'
}

const auditRecords: AuditRecord[] = [
  {
    id: 'REC-001',
    timestamp: '2026-04-19 14:32:45',
    action: 'Inicio de Interrogatorio',
    officer: 'Oficial A. Ramírez',
    signature: 'AR-2026-04-19-143245',
    hash: 'a7f3e9c2b1d4f6e8c0a2b4d6f8e0a2b4d6f8e0a2b4d6f8e0a2b4d6f8e0a2b4',
    status: 'verified'
  },
  {
    id: 'REC-002',
    timestamp: '2026-04-19 14:33:10',
    action: 'Captura de Huellas',
    officer: 'Oficial J. García',
    signature: 'JG-2026-04-19-143310',
    hash: 'b8e4f0d3c2e5g7f9d1b3c5e7g9f1b3c5e7g9f1b3c5e7g9f1b3c5e7g9f1b3c5',
    status: 'verified'
  },
  {
    id: 'REC-003',
    timestamp: '2026-04-19 14:33:45',
    action: 'Fotografía de Detención',
    officer: 'Oficial A. Ramírez',
    signature: 'AR-2026-04-19-143345',
    hash: 'c9f5g1e4d3f6h8i0e2c4d6f8h0i2c4d6f8h0i2c4d6f8h0i2c4d6f8h0i2c4d6',
    status: 'verified'
  },
  {
    id: 'REC-004',
    timestamp: '2026-04-19 14:34:02',
    action: 'Lectura de Derechos (Miranda)',
    officer: 'Oficial M. López',
    signature: 'ML-2026-04-19-143402',
    hash: 'd0g6h2f5e4g7i9j1f3d5e7g9i1j3d5e7g9i1j3d5e7g9i1j3d5e7g9i1j3d5e7',
    status: 'verified'
  },
  {
    id: 'REC-005',
    timestamp: '2026-04-19 14:34:30',
    action: 'Presentación de Abogado',
    officer: 'Oficial A. Ramírez',
    signature: 'AR-2026-04-19-143430',
    hash: 'e1h7i3g6f5h8j0k2g4e6f8h0j2k4e6f8h0j2k4e6f8h0j2k4e6f8h0j2k4e6f8',
    status: 'verified'
  },
  {
    id: 'REC-006',
    timestamp: '2026-04-19 14:35:15',
    action: 'Registro de Voz (Consentimiento)',
    officer: 'Oficial J. García',
    signature: 'JG-2026-04-19-143515',
    hash: 'f2i8j4h7g6i9k1l3h5f7g9i1k3l5f7g9i1k3l5f7g9i1k3l5f7g9i1k3l5f7g9',
    status: 'verified'
  },
  {
    id: 'REC-007',
    timestamp: '2026-04-19 14:35:45',
    action: 'Asistencia Médica Verificada',
    officer: 'Dr. C. Flores',
    signature: 'CF-2026-04-19-143545',
    hash: 'g3j9k5i8h7j0l2m4i6g8h0j2l4m6g8h0j2l4m6g8h0j2l4m6g8h0j2l4m6g8h0',
    status: 'verified'
  },
  {
    id: 'REC-008',
    timestamp: '2026-04-19 14:36:20',
    action: 'Transcripción Preliminar Sellada',
    officer: 'Sistema CUSTODIA',
    signature: 'SYS-2026-04-19-143620',
    hash: 'h4k0l6j9i8k1m3n5j7h9i1k3m5n7h9i1k3m5n7h9i1k3m5n7h9i1k3m5n7h9i1',
    status: 'verified'
  }
]

export function AuditTrailPanel() {
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<'all' | 'verified' | 'pending' | 'failed'>('all')

  const filteredRecords = auditRecords.filter(record => {
    const matchesSearch = 
      record.action.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.officer.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.signature.toLowerCase().includes(searchTerm.toLowerCase())
    
    const matchesStatus = statusFilter === 'all' || record.status === statusFilter
    
    return matchesSearch && matchesStatus
  })

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'verified':
        return 'bg-chart-3/10 text-chart-3 border-chart-3/30'
      case 'pending':
        return 'bg-chart-4/10 text-chart-4 border-chart-4/30'
      case 'failed':
        return 'bg-destructive/10 text-destructive border-destructive/30'
      default:
        return 'bg-muted/10 text-muted-foreground border-muted/30'
    }
  }

  return (
    <div className="flex flex-col h-full gap-4 p-6">
      {/* Header Section */}
      <div>
        <h2 className="text-2xl font-bold text-foreground mb-1">Auditoría de Cadena de Custodia</h2>
        <p className="text-sm text-muted-foreground">Expediente #EXP-2026-8902 | Registros inmutables con SHA-256</p>
      </div>

      {/* Controls Section */}
      <Card className="bg-card border-border p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Search Input */}
          <div className="md:col-span-2 relative">
            <Search size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground" />
            <input
              type="text"
              placeholder="Buscar por acción, oficial o firma..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-secondary border border-border rounded-lg text-foreground text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          {/* Status Filter */}
          <div className="flex gap-2">
            <button
              onClick={() => setStatusFilter('all')}
              className={`flex-1 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                statusFilter === 'all'
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-secondary text-foreground hover:bg-muted border border-border'
              }`}
            >
              Todos
            </button>
            <button
              onClick={() => setStatusFilter('verified')}
              className={`flex-1 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                statusFilter === 'verified'
                  ? 'bg-chart-3 text-background'
                  : 'bg-secondary text-foreground hover:bg-muted border border-border'
              }`}
            >
              ✓ Verificados
            </button>
          </div>
        </div>

        {/* Export Button */}
        <div className="mt-4 pt-4 border-t border-border flex justify-end">
          <Button className="bg-primary hover:bg-primary/90 text-primary-foreground gap-2">
            <Download size={18} />
            Exportar Auditoría
          </Button>
        </div>
      </Card>

      {/* Table Section */}
      <Card className="bg-card border-border flex-1 overflow-auto">
        <div className="inline-block min-w-full">
          <table className="w-full text-sm">
            <thead className="sticky top-0">
              <tr className="border-b border-border bg-secondary/50">
                <th className="px-4 py-3 text-left font-bold text-foreground">ID Registro</th>
                <th className="px-4 py-3 text-left font-bold text-foreground">Timestamp</th>
                <th className="px-4 py-3 text-left font-bold text-foreground">Acción</th>
                <th className="px-4 py-3 text-left font-bold text-foreground">Oficial / Sistema</th>
                <th className="px-4 py-3 text-left font-bold text-foreground">Firma Digital</th>
                <th className="px-4 py-3 text-left font-bold text-foreground">SHA-256 Hash</th>
                <th className="px-4 py-3 text-center font-bold text-foreground">Estado</th>
              </tr>
            </thead>
            <tbody>
              {filteredRecords.map((record, idx) => (
                <tr
                  key={record.id}
                  className={`border-b border-border hover:bg-secondary/30 transition-colors ${
                    idx % 2 === 0 ? 'bg-background/50' : 'bg-secondary/10'
                  }`}
                >
                  <td className="px-4 py-3">
                    <span className="font-mono text-xs text-primary bg-primary/10 px-2 py-1 rounded">
                      {record.id}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span className="font-mono text-xs text-foreground">{record.timestamp}</span>
                  </td>
                  <td className="px-4 py-3">
                    <span className="text-foreground">{record.action}</span>
                  </td>
                  <td className="px-4 py-3">
                    <span className="text-foreground text-sm">{record.officer}</span>
                  </td>
                  <td className="px-4 py-3">
                    <span className="font-mono text-xs text-primary truncate max-w-xs inline-block">
                      {record.signature}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="group relative">
                      <span className="font-mono text-xs text-muted-foreground truncate max-w-xs inline-block cursor-help">
                        {record.hash.substring(0, 24)}...
                      </span>
                      <div className="hidden group-hover:block absolute bottom-full left-0 mb-2 bg-secondary border border-border rounded p-2 text-xs font-mono text-foreground z-10 w-max">
                        {record.hash}
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-center">
                    <span
                      className={`inline-block px-2 py-1 rounded text-xs font-bold border ${getStatusColor(
                        record.status
                      )}`}
                    >
                      {record.status === 'verified' ? '✓ Verificado' : record.status === 'pending' ? '⏳ Pendiente' : '✗ Fallido'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {filteredRecords.length === 0 && (
            <div className="text-center py-8 text-muted-foreground">
              No se encontraron registros que coincidan con los filtros.
            </div>
          )}
        </div>
      </Card>

      {/* Summary Stats */}
      <div className="grid grid-cols-4 gap-4">
        <Card className="bg-secondary border-border p-4">
          <p className="text-xs text-muted-foreground mb-1">Total de Registros</p>
          <p className="text-2xl font-bold text-foreground">{filteredRecords.length}</p>
        </Card>
        <Card className="bg-secondary border-border p-4">
          <p className="text-xs text-muted-foreground mb-1">Verificados</p>
          <p className="text-2xl font-bold text-chart-3">{filteredRecords.filter(r => r.status === 'verified').length}</p>
        </Card>
        <Card className="bg-secondary border-border p-4">
          <p className="text-xs text-muted-foreground mb-1">Pendientes</p>
          <p className="text-2xl font-bold text-chart-4">{filteredRecords.filter(r => r.status === 'pending').length}</p>
        </Card>
        <Card className="bg-secondary border-border p-4">
          <p className="text-xs text-muted-foreground mb-1">Integridad</p>
          <p className="text-2xl font-bold text-chart-3">100%</p>
        </Card>
      </div>
    </div>
  )
}
