import { 
  Box, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Chip, 
  Button, 
  Paper,
  type ChipProps 
} from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { DataCard } from '../components/DataCard';
import type { EvidenceStatus } from '../types/balistica';
import { Search, Filter, ChevronRight } from 'lucide-react';

const results = [
  { 
    evidencia: 'EV-992', 
    fecha: '12/03/2026', 
    caso: 'Homicidio-Centro', 
    match: 'Metadatos Exactos', 
    estado: 'En Bóveda' as EvidenceStatus,
    color: 'success'
  },
  { 
    evidencia: 'EV-410', 
    fecha: '05/11/2025', 
    caso: 'Robo-Norte', 
    match: 'Metadatos Exactos', 
    estado: 'En Tribunal' as EvidenceStatus,
    color: 'warning'
  },
  { 
    evidencia: 'EV-102', 
    fecha: '22/08/2024', 
    caso: 'Hallazgo-Sur', 
    match: 'Metadatos Exactos', 
    estado: 'Destruida' as EvidenceStatus,
    color: 'error'
  },
];

const FilterChip = ({ label, value }: { label: string, value: string }) => (
  <Box className="bg-slate-100 px-3 py-1.5 rounded-full border border-slate-200 flex items-center gap-2">
    <Typography variant="caption" className="text-slate-500 font-bold uppercase">{label}:</Typography>
    <Typography variant="body2" className="text-slate-900 font-bold">{value}</Typography>
  </Box>
);

export const CorrelacionPage = () => {
  return (
    <Box>
      <SectionTitle>Búsqueda Paramétrica de Coincidencias</SectionTitle>
      
      <DataCard className="mb-6">
        <Box className="flex flex-row items-center gap-4 flex-wrap">
          <Box className="bg-slate-900 text-white p-2 rounded-lg">
            <Filter size={18} />
          </Box>
          <Typography variant="body2" className="text-slate-500 font-bold">FILTROS ACTIVOS:</Typography>
          <FilterChip label="Calibre" value="9mm" />
          <FilterChip label="Estrías" value="6" />
          <FilterChip label="Giro" value="Derecha" />
          
          <Box className="flex-grow" />
          
          <Button 
            variant="outlined" 
            startIcon={<Search size={16} />}
            className="border-slate-200 text-slate-600 capitalize font-bold hover:bg-slate-50"
          >
            Modificar Búsqueda
          </Button>
        </Box>
      </DataCard>
      
      <TableContainer component={Paper} elevation={0} className="border border-slate-200 rounded-xl overflow-hidden shadow-sm">
        <Table sx={{ minWidth: 650 }}>
          <TableHead className="bg-slate-50">
            <TableRow>
              <TableCell className="font-bold text-slate-500 uppercase text-xs">Evidencia</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-xs">Fecha</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-xs">Caso</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-xs">Match Relacional</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-xs">Estado</TableCell>
              <TableCell align="right" className="font-bold text-slate-500 uppercase text-xs">Acción</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {results.map((row) => (
              <TableRow key={row.evidencia} hover className="transition-colors">
                <TableCell className="font-bold text-slate-900">{row.evidencia}</TableCell>
                <TableCell className="text-slate-600">{row.fecha}</TableCell>
                <TableCell className="font-medium text-slate-700">{row.caso}</TableCell>
                <TableCell>
                  <Box className="flex items-center gap-1 text-emerald-600 font-bold text-xs uppercase">
                    <CheckCircle2 size={12} className="inline" /> {row.match}
                  </Box>
                </TableCell>
                <TableCell>
                  <Chip 
                    label={row.estado} 
                    size="small" 
                    color={row.color as ChipProps['color']} 
                    variant="outlined"
                    className="font-bold text-[10px] uppercase"
                  />
                </TableCell>
                <TableCell align="right">
                  <Button 
                    variant="text" 
                    size="small" 
                    endIcon={<ChevronRight size={14} />}
                    className="text-slate-600 font-bold capitalize hover:text-slate-900"
                  >
                    Ver Perfil
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      
      <Box className="mt-6 flex justify-between items-center px-2">
        <Typography variant="caption" className="text-slate-400">
          Mostrando 3 coincidencias exactas basadas en metadatos técnicos.
        </Typography>
        <Button variant="text" size="small" className="text-slate-400 capitalize">
          Exportar Informe PDF
        </Button>
      </Box>
    </Box>
  );
};

// Internal component for the icon to avoid missing import
const CheckCircle2 = ({ size, className }: { size: number, className: string }) => (
  <svg 
    xmlns="http://www.w3.org/2000/svg" 
    width={size} 
    height={size} 
    viewBox="0 0 24 24" 
    fill="none" 
    stroke="currentColor" 
    strokeWidth="2" 
    strokeLinecap="round" 
    strokeLinejoin="round" 
    className={className}
  >
    <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"/><path d="m9 12 2 2 4-4"/>
  </svg>
);
