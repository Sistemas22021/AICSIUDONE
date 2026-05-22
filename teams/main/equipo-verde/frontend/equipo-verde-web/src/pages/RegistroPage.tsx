import { Box, Grid, TextField, MenuItem, Button, Typography } from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { DataCard } from '../components/DataCard';
import { UploadCloud, Hash, CheckCircle2 } from 'lucide-react';
import ballisticsThumb from '../assets/ballistics_thumb.png';

export const RegistroPage = () => {
  return (
    <Box>
      <SectionTitle>Registro de Metadatos Balísticos</SectionTitle>
      
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, lg: 8 }}>
          <DataCard>
            <Typography variant="overline" className="text-slate-500 font-bold mb-4 block">
              Información Técnica de la Evidencia
            </Typography>
            
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="N° de Expediente"
                  defaultValue="EXP-2026-089"
                  variant="outlined"
                  size="small"
                  slotProps={{ input: { readOnly: true } }}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="Calibre"
                  defaultValue="9mm Parabellum"
                  variant="outlined"
                  size="small"
                  slotProps={{ input: { readOnly: true } }}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="Número de Estrías"
                  defaultValue="6"
                  variant="outlined"
                  size="small"
                  slotProps={{ input: { readOnly: true } }}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  select
                  label="Dirección de Giro (Twist)"
                  defaultValue="Dextrógiro (Derecha)"
                  variant="outlined"
                  size="small"
                >
                  <MenuItem value="Dextrógiro (Derecha)">Dextrógiro (Derecha)</MenuItem>
                  <MenuItem value="Levógiro (Izquierda)">Levógiro (Izquierda)</MenuItem>
                </TextField>
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  select
                  label="Tipo de Percusión"
                  defaultValue="Fuego Central"
                  variant="outlined"
                  size="small"
                >
                  <MenuItem value="Fuego Central">Fuego Central</MenuItem>
                  <MenuItem value="Fuego Anular">Fuego Anular</MenuItem>
                </TextField>
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="Marca del Fabricante"
                  defaultValue="Luger"
                  variant="outlined"
                  size="small"
                  slotProps={{ input: { readOnly: true } }}
                />
              </Grid>
            </Grid>
            
            <Box className="mt-8 flex justify-end">
              <Button 
                variant="contained" 
                startIcon={<Hash size={18} />}
                className="bg-slate-900 hover:bg-slate-800 py-2.5 px-6 rounded-lg font-bold"
                sx={{ bgcolor: '#0f172a', '&:hover': { bgcolor: '#1e293b' } }}
              >
                Guardar y Generar Hash
              </Button>
            </Box>
          </DataCard>
        </Grid>
        
        <Grid size={{ xs: 12, lg: 4 }}>
          <DataCard className="h-full">
            <Typography variant="overline" className="text-slate-500 font-bold mb-4 block">
              Documentación Fotográfica
            </Typography>
            
            <Box 
              className="border-2 border-dashed border-slate-200 rounded-xl p-8 flex flex-col items-center justify-center bg-slate-50"
              sx={{ minHeight: '300px' }}
            >
              <Box className="relative mb-4">
                <img 
                  src={ballisticsThumb} 
                  alt="Microscopic preview" 
                  className="w-full max-w-[200px] rounded-lg shadow-lg grayscale border border-slate-300"
                />
                <Box className="absolute -bottom-2 -right-2 bg-emerald-500 text-white p-1 rounded-full border-2 border-white">
                  <CheckCircle2 size={16} />
                </Box>
              </Box>
              <Typography variant="body2" className="text-slate-600 font-medium text-center">
                IMG-BAL-089.png cargada con éxito
              </Typography>
              <Typography variant="caption" className="text-slate-400 mt-1">
                Firma Digital: SHA-256 (Verificado)
              </Typography>
              
              <Button 
                variant="text" 
                startIcon={<UploadCloud size={18} />}
                className="mt-6 text-slate-500 capitalize font-bold"
              >
                Cambiar Imagen
              </Button>
            </Box>
            
            <Box className="mt-6 p-4 bg-amber-50 border border-amber-100 rounded-lg">
              <Typography variant="caption" className="text-amber-800 font-medium leading-tight block">
                ATENCIÓN: La imagen será procesada mediante algoritmos de visión artificial para extraer patrones de micro-estriado.
              </Typography>
            </Box>
          </DataCard>
        </Grid>
      </Grid>
    </Box>
  );
};
