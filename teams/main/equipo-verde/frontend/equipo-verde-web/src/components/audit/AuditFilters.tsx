import { Box, TextField, MenuItem, Button, InputAdornment } from '@mui/material';
import { Search, FilterX } from 'lucide-react';
import { AuditActionType } from '../../types/audit';

export interface AuditFiltersState {
  search: string;
  type: string;
  dateFrom: string;
  dateTo: string;
}

interface Props {
  filters: AuditFiltersState;
  onFilterChange: (filters: AuditFiltersState) => void;
  onClear: () => void;
}

export const AuditFilters = ({ filters, onFilterChange, onClear }: Props) => {
  return (
    <Box className="bg-white p-4 rounded-2xl border border-slate-100 shadow-[0_4px_20px_rgb(0,0,0,0.03)] mb-8 grid grid-cols-1 md:grid-cols-12 gap-4">
      <Box className="md:col-span-4">
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Buscar usuario o expediente..."
          value={filters.search}
          onChange={(e) => onFilterChange({ ...filters, search: e.target.value })}
          size="small"
          slotProps={{
            input: {
              startAdornment: <InputAdornment position="start"><Search size={18} className="text-slate-400" /></InputAdornment>,
              className: 'rounded-xl bg-slate-50'
            }
          }}
        />
      </Box>
      
      <Box className="md:col-span-3">
        <TextField
          fullWidth
          select
          variant="outlined"
          value={filters.type}
          onChange={(e) => onFilterChange({ ...filters, type: e.target.value })}
          size="small"
          label="Tipo de Acción"
          slotProps={{ input: { className: 'rounded-xl bg-slate-50' } }}
        >
          <MenuItem value="">Todos los eventos</MenuItem>
          {Object.values(AuditActionType).map(type => (
            <MenuItem key={type} value={type}>{type}</MenuItem>
          ))}
        </TextField>
      </Box>
      
      <Box className="md:col-span-2">
        <TextField
          fullWidth
          type="date"
          variant="outlined"
          value={filters.dateFrom}
          onChange={(e) => onFilterChange({ ...filters, dateFrom: e.target.value })}
          size="small"
          label="Desde"
          slotProps={{ 
            inputLabel: { shrink: true },
            input: { className: 'rounded-xl bg-slate-50' } 
          }}
        />
      </Box>
      
      <Box className="md:col-span-2">
        <TextField
          fullWidth
          type="date"
          variant="outlined"
          value={filters.dateTo}
          onChange={(e) => onFilterChange({ ...filters, dateTo: e.target.value })}
          size="small"
          label="Hasta"
          slotProps={{ 
            inputLabel: { shrink: true },
            input: { className: 'rounded-xl bg-slate-50' } 
          }}
        />
      </Box>
      
      <Box className="md:col-span-1 flex items-center justify-center">
        <Button 
          variant="outlined" 
          onClick={onClear}
          className="min-w-0 w-full h-full border-slate-200 text-slate-500 hover:bg-slate-50 hover:text-slate-700 rounded-xl"
        >
          <FilterX size={20} />
        </Button>
      </Box>
    </Box>
  );
};
