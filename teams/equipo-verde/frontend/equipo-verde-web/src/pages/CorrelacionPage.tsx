import { useState, useMemo } from 'react';
import { 
  Box, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper,
  IconButton,
  Tooltip
} from '@mui/material';
import { Check, X } from 'lucide-react';

import { SectionTitle } from '../components/SectionTitle';
import { ConfidenceBadge } from '../components/ranking/ConfidenceBadge';
import { TrendIndicator } from '../components/ranking/TrendIndicator';
import { RankingFilters, RankingFilterState } from '../components/ranking/RankingFilters';
import type { MatchRankingResult } from '../types/balistica';

const MOCK_RESULTS: MatchRankingResult[] = [
  {
    id: 'R-001',
    evidencia: 'EV-992',
    fecha: '2026-03-12',
    caso: 'Homicidio-Centro',
    match: 'Marcas de estrías idénticas',
    estado: 'En Bóveda',
    score: 98.5,
    confidence: 'Alta',
    recurrenceCount: 4,
    weaponType: 'Pistola 9mm',
    location: 'Centro',
    verificationStatus: 'Pendiente'
  },
  {
    id: 'R-002',
    evidencia: 'EV-410',
    fecha: '2025-11-05',
    caso: 'Robo-Norte',
    match: 'Huella de percusión similar',
    estado: 'En Tribunal',
    score: 75.2,
    confidence: 'Media',
    recurrenceCount: 2,
    weaponType: 'Revolver .38',
    location: 'Norte',
    verificationStatus: 'Confirmada'
  },
  {
    id: 'R-003',
    evidencia: 'EV-102',
    fecha: '2024-08-22',
    caso: 'Hallazgo-Sur',
    match: 'Mismo calibre',
    estado: 'Destruida',
    score: 45.0,
    confidence: 'Baja',
    recurrenceCount: 1,
    weaponType: 'Escopeta 12GA',
    location: 'Sur',
    verificationStatus: 'Descartada'
  }
];

export const CorrelacionPage = () => {
  const [results, setResults] = useState<MatchRankingResult[]>(MOCK_RESULTS);
  const [filters, setFilters] = useState<RankingFilterState>({
    search: '',
    sortBy: 'score',
    sortOrder: 'desc',
    confidenceFilter: 'all'
  });

  const handleVerify = (id: string, isConfirmed: boolean) => {
    setResults(prev => prev.map(r => 
      r.id === id ? { ...r, verificationStatus: isConfirmed ? 'Confirmada' : 'Descartada' } : r
    ));
  };

  const filteredAndSortedResults = useMemo(() => {
    let filtered = [...results];

    // Search filter
    if (filters.search) {
      const q = filters.search.toLowerCase();
      filtered = filtered.filter(r => 
        r.caso.toLowerCase().includes(q) || 
        r.weaponType.toLowerCase().includes(q) || 
        r.location.toLowerCase().includes(q) ||
        r.evidencia.toLowerCase().includes(q)
      );
    }

    // Confidence filter
    if (filters.confidenceFilter !== 'all') {
      filtered = filtered.filter(r => r.confidence === filters.confidenceFilter);
    }

    // Sorting
    filtered.sort((a, b) => {
      let valA: any = a[filters.sortBy as keyof MatchRankingResult];
      let valB: any = b[filters.sortBy as keyof MatchRankingResult];
      
      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();

      if (valA < valB) return filters.sortOrder === 'asc' ? -1 : 1;
      if (valA > valB) return filters.sortOrder === 'asc' ? 1 : -1;
      return 0;
    });

    return filtered;
  }, [results, filters]);

  return (
    <Box className="animate-fade-in max-w-6xl mx-auto px-4 py-6">
      <SectionTitle>Motor Correlación</SectionTitle>
      <Typography variant="body2" className="text-slate-500 mb-6 max-w-3xl">
        Motor analítico jerárquico. Las evidencias con mayor probabilidad matemática de estar vinculadas aparecen en las primeras posiciones. Verifique y valide los hallazgos directamente.
      </Typography>
      
      <RankingFilters filters={filters} onFilterChange={setFilters} />
      
      <TableContainer component={Paper} elevation={0} className="border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
        <Table sx={{ minWidth: 800 }} className="border-collapse">
          <TableHead className="bg-slate-50 border-b border-slate-200">
            <TableRow>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider w-16 text-center py-4">N°</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Similitud</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Evidencia (Origen)</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider text-center py-4">Tendencia</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Detalles Técnicos</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Estado</TableCell>
              <TableCell align="right" className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Acción</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredAndSortedResults.map((row, index) => {
              const isConfirmed = row.verificationStatus === 'Confirmada';
              const isDiscarded = row.verificationStatus === 'Descartada';

              return (
                <TableRow 
                  key={row.id} 
                  hover 
                  className={`transition-colors ${isDiscarded ? 'opacity-50' : ''}`}
                >
                  <TableCell className="text-center py-3">
                    <Typography variant="body2" className="font-bold text-slate-700">
                      {index + 1}
                    </Typography>
                  </TableCell>
                  <TableCell className="py-3">
                    <ConfidenceBadge confidence={row.confidence} score={row.score} />
                  </TableCell>
                  <TableCell className="py-3">
                    <Typography className="font-bold text-slate-700 text-sm">{row.evidencia}</Typography>
                  </TableCell>
                  <TableCell align="center" className="py-3">
                    <TrendIndicator count={row.recurrenceCount} />
                  </TableCell>
                  <TableCell className="py-3">
                    <Typography variant="body2" className="text-slate-700 font-medium">{row.weaponType}</Typography>
                  </TableCell>
                  <TableCell className="py-3">
                    {isConfirmed ? (
                      <span className="text-emerald-700 font-medium text-[13px]">Verificado</span>
                    ) : isDiscarded ? (
                      <span className="text-slate-400 font-medium text-[13px]">Descartado</span>
                    ) : (
                      <span className="text-amber-600 font-medium text-[13px]">Pendiente</span>
                    )}
                  </TableCell>
                  <TableCell align="right" className="py-3">
                    <Box className="flex items-center justify-end gap-2">
                      {row.verificationStatus === 'Pendiente' && (
                        <>
                          <Tooltip title="Confirmar similitud">
                            <IconButton 
                              size="small" 
                              onClick={() => handleVerify(row.id, true)} 
                              className="bg-white border border-slate-200 shadow-sm hover:shadow text-emerald-600 hover:bg-emerald-50 w-9 h-9"
                            >
                              <Check size={16} strokeWidth={2.5} />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Descartar">
                            <IconButton 
                              size="small" 
                              onClick={() => handleVerify(row.id, false)} 
                              className="bg-white border border-slate-200 shadow-sm hover:shadow text-red-600 hover:bg-red-50 w-9 h-9"
                            >
                              <X size={16} strokeWidth={2.5} />
                            </IconButton>
                          </Tooltip>
                        </>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              );
            })}
            
            {filteredAndSortedResults.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} className="text-center py-12">
                  <Typography className="text-slate-500">No se encontraron coincidencias para estos filtros.</Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};
