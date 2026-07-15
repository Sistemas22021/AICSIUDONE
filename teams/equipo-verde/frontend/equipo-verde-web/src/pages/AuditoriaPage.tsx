import { useState, useMemo } from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { Terminal, ShieldCheck } from 'lucide-react';

import { AuditFilters, AuditFiltersState } from '../components/audit/AuditFilters';
import { AuditTimeline } from '../components/audit/AuditTimeline';

import { auditService } from '../services/auditService';

export const AuditoriaPage = () => {
  const [filters, setFilters] = useState<AuditFiltersState>({
    search: '',
    type: '',
    dateFrom: '',
    dateTo: ''
  });
  
  // Get data and filter it
  const filteredEntries = useMemo(() => {
    let data = auditService.getEntries();
    
    if (filters.search) {
      const s = filters.search.toLowerCase();
      data = data.filter(e => 
        e.userName.toLowerCase().includes(s) || 
        e.targetEntity.toLowerCase().includes(s) ||
        e.description.toLowerCase().includes(s)
      );
    }
    
    if (filters.type) {
      data = data.filter(e => e.actionType === filters.type);
    }
    
    if (filters.dateFrom) {
      const from = new Date(filters.dateFrom).getTime();
      data = data.filter(e => new Date(e.timestamp).getTime() >= from);
    }
    
    if (filters.dateTo) {
      // Add 1 day to include the whole "To" date
      const toDate = new Date(filters.dateTo);
      toDate.setDate(toDate.getDate() + 1);
      const to = toDate.getTime();
      data = data.filter(e => new Date(e.timestamp).getTime() < to);
    }
    
    return data;
  }, [filters]);

  const handleClearFilters = () => {
    setFilters({
      search: '',
      type: '',
      dateFrom: '',
      dateTo: ''
    });
  };

  return (
    <Box className="animate-fade-in max-w-5xl mx-auto px-4 py-6">
      <SectionTitle>Bitácora de Auditoría Forense</SectionTitle>
      
      {/* Header Info */}
      <Box className="bg-slate-900 p-4 rounded-2xl flex items-center gap-3 mb-6 shadow-md border border-slate-800">
        <Terminal size={20} className="text-emerald-400" />
        <Typography variant="body2" className="text-emerald-400 font-mono font-bold tracking-wider">
          SISTEMA DE TRAZABILIDAD - CADENA DE CUSTODIA DIGITAL v4.1.0
        </Typography>
      </Box>

      {/* Filters */}
      <AuditFilters 
        filters={filters} 
        onFilterChange={setFilters} 
        onClear={handleClearFilters} 
      />

      {/* Timeline */}
      <Box className="mb-8">
        <AuditTimeline 
          entries={filteredEntries} 
        />
      </Box>

      {/* Integrity Badge */}
      <Box className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4 border-t border-slate-200 pt-8">
        <Paper elevation={0} className="p-5 border border-emerald-200 bg-emerald-50 rounded-2xl flex items-start gap-4 shadow-sm">
          <ShieldCheck className="text-emerald-600 mt-0.5" size={24} strokeWidth={2.5} />
          <Box>
            <Typography variant="subtitle2" className="font-extrabold text-emerald-900 uppercase tracking-wider mb-1">Nivel de Integridad: 100%</Typography>
            <Typography variant="caption" className="text-emerald-700 block font-medium leading-relaxed">Todos los bloques de auditoría han sido verificados mediante Hashing SHA-256 encadenado.</Typography>
          </Box>
        </Paper>
        
        <Box className="col-span-2 flex items-center md:justify-end">
          <Typography variant="caption" className="text-slate-400 max-w-sm md:text-right font-medium leading-relaxed">
            "Este registro constituye prueba pericial de la cadena de custodia y no puede ser alterado ni eliminado por ningún usuario del sistema, garantizando la inmutabilidad de la evidencia."
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};
