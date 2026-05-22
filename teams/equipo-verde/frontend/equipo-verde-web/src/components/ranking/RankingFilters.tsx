import { Box, Button, TextField, MenuItem, InputAdornment } from '@mui/material';
import { Search, ArrowUpDown } from 'lucide-react';

export type SortField = 'score' | 'fecha' | 'weaponType' | 'location';
export type SortOrder = 'asc' | 'desc';

export interface RankingFilterState {
  search: string;
  sortBy: SortField;
  sortOrder: SortOrder;
  confidenceFilter: string;
}

interface Props {
  filters: RankingFilterState;
  onFilterChange: (filters: RankingFilterState) => void;
}

export const RankingFilters = ({ filters, onFilterChange }: Props) => {
  const handleChange = (field: keyof RankingFilterState, value: string) => {
    onFilterChange({ ...filters, [field]: value });
  };

  const toggleSortOrder = () => {
    onFilterChange({ 
      ...filters, 
      sortOrder: filters.sortOrder === 'asc' ? 'desc' : 'asc' 
    });
  };

  return (
    <Box className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm mb-6 flex flex-col md:flex-row gap-4 items-center">
      <TextField
        placeholder="Buscar por caso, arma o ubicación..."
        variant="outlined"
        size="small"
        fullWidth
        value={filters.search}
        onChange={(e) => handleChange('search', e.target.value)}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <Search size={18} className="text-slate-400" />
              </InputAdornment>
            ),
            className: "bg-slate-50 rounded-xl"
          }
        }}
      />
      
      <Box className="flex flex-row gap-3 w-full md:w-auto">
        <TextField
          select
          size="small"
          value={filters.confidenceFilter}
          onChange={(e) => handleChange('confidenceFilter', e.target.value)}
          slotProps={{ input: { className: "bg-slate-50 rounded-xl min-w-[140px]" } }}
        >
          <MenuItem value="all">Todas las Probabilidades</MenuItem>
          <MenuItem value="Alta">Alta Probabilidad</MenuItem>
          <MenuItem value="Media">Media Probabilidad</MenuItem>
          <MenuItem value="Baja">Baja Probabilidad</MenuItem>
        </TextField>

        <TextField
          select
          size="small"
          value={filters.sortBy}
          onChange={(e) => handleChange('sortBy', e.target.value)}
          slotProps={{ input: { className: "bg-slate-50 rounded-xl min-w-[140px]" } }}
        >
          <MenuItem value="score">Mayor Similitud</MenuItem>
          <MenuItem value="fecha">Fecha del Hecho</MenuItem>
          <MenuItem value="weaponType">Tipo de Arma</MenuItem>
          <MenuItem value="location">Lugar del Hallazgo</MenuItem>
        </TextField>

        <Button 
          variant="outlined" 
          onClick={toggleSortOrder}
          className="border-slate-200 text-slate-600 bg-slate-50 min-w-[40px] px-0 rounded-xl hover:bg-slate-100"
        >
          <ArrowUpDown size={18} />
        </Button>
      </Box>
    </Box>
  );
};
