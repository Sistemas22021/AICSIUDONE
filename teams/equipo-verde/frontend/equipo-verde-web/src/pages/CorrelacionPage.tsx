import { useState, useMemo, useEffect } from 'react';
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
  Tooltip,
  Button
} from '@mui/material';
import { Check, X } from 'lucide-react';

import { SectionTitle } from '../components/SectionTitle';
import { ConfidenceBadge } from '../components/ranking/ConfidenceBadge';
import { RankingFilters, RankingFilterState } from '../components/ranking/RankingFilters';
import { ComparisonViewerModal } from '../components/ranking/ComparisonViewerModal';

// Nuevos Servicios
import { CorrelationService, CorrelationResult } from '../services/correlationService';
import { MOCK_DATABASE } from '../data/mockDatabase';
import { EvidenceRecord } from '../types/evidence';
import { auditService } from '../services/auditService';
import { AuditActionType } from '../types/audit';

// Extender el resultado con campos para la UI de esta tabla
interface UIRankingResult extends CorrelationResult {
  id: string; // Para React key
  verificationStatus: 'Pendiente' | 'Confirmada' | 'Descartada';
}

export const CorrelacionPage = () => {
  const [results, setResults] = useState<UIRankingResult[]>([]);
  const [sourceEvidence, setSourceEvidence] = useState<EvidenceRecord | null>(null);
  
  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedResult, setSelectedResult] = useState<CorrelationResult | null>(null);

  const [filters, setFilters] = useState<RankingFilterState>({
    search: '',
    sortBy: 'score',
    sortOrder: 'desc',
    confidenceFilter: 'all'
  });

  // Simular la llegada de una nueva evidencia y ejecutar el motor
  useEffect(() => {
    // Tomamos la primera de la BD como "Origen" para la demo
    const nuevaEvidencia = MOCK_DATABASE[0];
    setSourceEvidence(nuevaEvidencia);

    // Corremos el análisis contra el resto de la BD
    const rawResults = CorrelationService.runAnalysis(nuevaEvidencia, MOCK_DATABASE);
    
    // Mapeamos al estado de UI
    const uiResults: UIRankingResult[] = rawResults.map((res, idx) => ({
      ...res,
      id: `MATCH-${idx}`,
      verificationStatus: 'Pendiente'
    }));

    setResults(uiResults);
  }, []);

  const handleVerify = (result: UIRankingResult, isConfirmed: boolean) => {
    setResults(prev => prev.map(r => 
      r.id === result.id ? { ...r, verificationStatus: isConfirmed ? 'Confirmada' : 'Descartada' } : r
    ));

    if (isConfirmed && sourceEvidence) {
      // Registrar en el Log de Auditoría
      auditService.logAction({
        actionType: AuditActionType.COMPARISON_RUN,
        userName: 'Perito Principal',
        userRole: 'Perito Balístico',
        userId: 'u101',
        targetEntity: sourceEvidence.expediente,
        description: `Cotejo confirmado jerárquicamente entre ${sourceEvidence.expediente} y ${result.targetEvidence.expediente}. Similitud matemática: ${result.matchInfo.score}%.`,
        ipAddress: '192.168.1.55',
        changes: []
      });
    }
  };

  const handleOpenDetail = (result: UIRankingResult) => {
    setSelectedResult(result);
    setIsModalOpen(true);
  };

  const filteredAndSortedResults = useMemo(() => {
    let filtered = [...results];

    // Search filter
    if (filters.search) {
      const q = filters.search.toLowerCase();
      filtered = filtered.filter(r => 
        r.targetEvidence.expediente.toLowerCase().includes(q) || 
        r.targetEvidence.calibre.toLowerCase().includes(q) ||
        r.targetEvidence.marca.toLowerCase().includes(q)
      );
    }

    // Confidence filter
    if (filters.confidenceFilter !== 'all') {
      filtered = filtered.filter(r => r.matchInfo.confidence === filters.confidenceFilter);
    }

    // Sorting (Simplificado para el nuevo modelo)
    filtered.sort((a, b) => {
      let valA: any = filters.sortBy === 'score' ? a.matchInfo.score : a.targetEvidence.expediente;
      let valB: any = filters.sortBy === 'score' ? b.matchInfo.score : b.targetEvidence.expediente;
      
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
      <SectionTitle>Motor de cotejo</SectionTitle>
      
      {sourceEvidence && (
        <Box className="mb-6 p-4 bg-indigo-50 border border-indigo-100 rounded-xl flex items-center gap-4">
          <Box className="w-12 h-12 bg-indigo-500 text-white rounded-full flex items-center justify-center font-black shadow-inner">
            E-1
          </Box>
          <Box>
            <Typography variant="subtitle2" className="text-indigo-900 font-bold">
              Evidencia Activa: {sourceEvidence.expediente}
            </Typography>
            <Typography variant="body2" className="text-indigo-700">
              Corriendo análisis jerárquico contra toda la base de datos histórica...
            </Typography>
          </Box>
        </Box>
      )}
      
      <RankingFilters filters={filters} onFilterChange={setFilters} />
      
      <TableContainer component={Paper} elevation={0} className="border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
        <Table sx={{ minWidth: 800 }} className="border-collapse">
          <TableHead className="bg-slate-50 border-b border-slate-200">
            <TableRow>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider w-16 text-center py-4">Ranking</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Similitud</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Expediente (Hallazgo)</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4">Calibre/Marca</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[11px] tracking-wider py-4 text-center">Detalle Analítico</TableCell>
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
                    <Box className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center mx-auto border border-slate-200 shadow-sm">
                      <Typography variant="body2" className="font-black text-slate-700">
                        {index + 1}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell className="py-3">
                    <ConfidenceBadge confidence={row.matchInfo.confidence} score={row.matchInfo.score} />
                  </TableCell>
                  <TableCell className="py-3">
                    <Typography className="font-bold text-slate-800 text-sm">{row.targetEvidence.expediente}</Typography>
                    <Typography className="text-xs text-slate-500">{row.targetEvidence.createdAt}</Typography>
                  </TableCell>
                  <TableCell className="py-3">
                    <Typography variant="body2" className="text-slate-700 font-bold">{row.targetEvidence.calibre}</Typography>
                    <Typography variant="caption" className="text-slate-500">{row.targetEvidence.marca}</Typography>
                  </TableCell>
                  <TableCell className="py-3 text-center">
                    <Button 
                      variant="text" 
                      size="small"
                      onClick={() => handleOpenDetail(row)}
                      className="font-bold text-indigo-600 hover:bg-indigo-50"
                      sx={{ textTransform: 'none', borderRadius: '999px' }}
                    >
                      Ver Análisis
                    </Button>
                  </TableCell>
                  <TableCell className="py-3">
                    {isConfirmed ? (
                      <span className="text-emerald-700 font-bold text-[13px] bg-emerald-50 px-2 py-1 rounded-md">Verificado</span>
                    ) : isDiscarded ? (
                      <span className="text-slate-500 font-bold text-[13px] bg-slate-100 px-2 py-1 rounded-md">Descartado</span>
                    ) : (
                      <span className="text-amber-700 font-bold text-[13px] bg-amber-50 px-2 py-1 rounded-md">Pendiente</span>
                    )}
                  </TableCell>
                  <TableCell align="right" className="py-3">
                    <Box className="flex items-center justify-end gap-2">
                      {row.verificationStatus === 'Pendiente' && (
                        <>
                          <Tooltip title="Confirmar similitud">
                            <IconButton 
                              size="small" 
                              onClick={() => handleVerify(row, true)} 
                              className="bg-white border border-slate-200 shadow-sm hover:shadow text-emerald-600 hover:bg-emerald-50 w-9 h-9"
                            >
                              <Check size={16} strokeWidth={2.5} />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Descartar">
                            <IconButton 
                              size="small" 
                              onClick={() => handleVerify(row, false)} 
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
                  <Typography className="text-slate-500 font-medium">No se encontraron coincidencias viables para esta evidencia origen.</Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Modal Visor Comparativo */}
      <ComparisonViewerModal 
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        result={selectedResult}
        sourceEvidence={sourceEvidence}
      />
    </Box>
  );
};
