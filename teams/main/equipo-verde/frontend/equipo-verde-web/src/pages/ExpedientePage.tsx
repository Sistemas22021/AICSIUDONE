import { useState, useEffect } from 'react';
import {
  Box, TextField, MenuItem, Button, Typography, Snackbar,
  CircularProgress, LinearProgress, IconButton, Paper, Chip
} from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { DataCard } from '../components/DataCard';
import {
  FolderOpen, FolderPlus, Edit2, ArrowLeft, CheckCircle2
} from 'lucide-react';
import {
  ExpedienteDTO
} from '../types/expediente';
import {
  getExpedientes, createExpediente, updateExpediente
} from '../services/expedienteService';

const premiumInputStyles = {
  '& .MuiOutlinedInput-root': {
    borderRadius: '12px',
    backgroundColor: '#f8fafc',
    transition: 'all 0.2s ease-in-out',
    '& fieldset': {
      borderColor: '#e2e8f0',
      transition: 'all 0.2s',
    },
    '&:hover fieldset': {
      borderColor: '#cbd5e1'
    },
    '&.Mui-focused': {
      backgroundColor: '#ffffff',
      boxShadow: '0 0 0 4px rgba(15, 23, 42, 0.05)',
      '& fieldset': {
        borderWidth: '1px',
        borderColor: '#0f172a',
      }
    }
  },
  '& .MuiInputLabel-root.Mui-focused': {
    color: '#0f172a',
    fontWeight: 600
  }
};

export const ExpedientePage = () => {
  const [viewMode, setViewMode] = useState<'list' | 'form'>('list');
  const [expedientes, setExpedientes] = useState<ExpedienteDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<ExpedienteDTO>({
    caseNumber: '',
    description: '',
    status: 'ABIERTO'
  });
  const [formErrors, setFormErrors] = useState<Partial<Record<keyof ExpedienteDTO, string>>>({});
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean, message: string, severity: 'success' | 'error' }>({ open: false, message: '', severity: 'success' });

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await getExpedientes(search, 0, 50);
      setExpedientes(data.content);
    } catch (error) {
      console.error("Error al cargar expedientes", error);
      showSnackbar("Error al cargar los expedientes", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (viewMode === 'list') {
      loadData();
    }
  }, [viewMode, search]);

  const showSnackbar = (message: string, severity: 'success' | 'error') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCreateNew = () => {
    setFormData({ caseNumber: '', description: '', status: 'ABIERTO' });
    setEditingId(null);
    setFormErrors({});
    setSnackbar(prev => ({ ...prev, open: false }));
    setViewMode('form');
  };

  const handleEdit = (exp: ExpedienteDTO) => {
    setFormData({
      caseNumber: exp.caseNumber,
      description: exp.description,
      status: exp.status
    });
    setEditingId(exp.idExpedient || null);
    setFormErrors({});
    setSnackbar(prev => ({ ...prev, open: false }));
    setViewMode('form');
  };

  const validateForm = () => {
    const errors: Partial<Record<keyof ExpedienteDTO, string>> = {};
    if (!formData.caseNumber.trim()) errors.caseNumber = 'Requerido';
    if (!formData.description.trim()) errors.description = 'Requerido';
    if (!formData.status) errors.status = 'Requerido';
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;
    
    setIsSubmitting(true);
    try {
      if (editingId) {
        await updateExpediente(editingId, formData);
        showSnackbar("Expediente actualizado correctamente", "success");
      } else {
        await createExpediente(formData);
        showSnackbar("Expediente creado correctamente", "success");
      }
      setViewMode('list');
    } catch (error) {
      console.error(error);
      showSnackbar("Error al guardar el expediente", "error");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (viewMode === 'list') {
    return (
      <Box sx={{ maxWidth: '1200px', mx: 'auto', p: 4 }}>
        <SectionTitle>
          <Box className="flex items-center gap-2">
            <FolderOpen size={28} className="text-slate-600" />
            Gestión de Expedientes
          </Box>
        </SectionTitle>
        <Typography variant="body1" className="text-slate-500 mb-6">
          Administra los expedientes y casos forenses del sistema.
        </Typography>

        <Box sx={{ display: 'flex', gap: 2, mb: 4, alignItems: 'center' }}>
          <TextField
            placeholder="Buscar por código o descripción..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            sx={{ ...premiumInputStyles, flex: 1 }}
          />
          <Button
            variant="contained"
            onClick={handleCreateNew}
            startIcon={<FolderPlus size={20} />}
            sx={{
              backgroundColor: '#0f172a',
              borderRadius: '12px',
              padding: '12px 24px',
              textTransform: 'none',
              fontWeight: 600,
              boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
              '&:hover': {
                backgroundColor: '#1e293b',
              }
            }}
          >
            Nuevo Expediente
          </Button>
        </Box>

        {loading ? (
          <LinearProgress sx={{ borderRadius: 2, my: 4 }} />
        ) : (
          <Box sx={{ display: 'grid', gap: 3, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr', lg: 'repeat(3, 1fr)' } }}>
            {expedientes.length === 0 && (
              <Box sx={{ gridColumn: '1 / -1' }}>
                <Paper sx={{ p: 6, textAlign: 'center', borderRadius: '16px', border: '1px dashed #cbd5e1', bgcolor: 'transparent', boxShadow: 'none' }}>
                  <Typography variant="h6" color="text.secondary">No se encontraron expedientes registrados.</Typography>
                </Paper>
              </Box>
            )}
            {expedientes.map(exp => (
              <Box key={exp.idExpedient}>
                <DataCard>
                  <Box sx={{ p: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                      <Typography variant="h6" sx={{ fontWeight: 700, color: '#0f172a' }}>
                        {exp.caseNumber}
                      </Typography>
                      <Chip 
                        label={exp.status} 
                        size="small"
                        sx={{ 
                          fontWeight: 600,
                          bgcolor: exp.status === 'ABIERTO' ? '#dcfce7' : '#f1f5f9',
                          color: exp.status === 'ABIERTO' ? '#166534' : '#475569',
                          borderRadius: '8px'
                        }} 
                      />
                    </Box>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 3, minHeight: '40px', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                      {exp.description}
                    </Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                      <IconButton size="small" onClick={() => handleEdit(exp)} sx={{ color: '#3b82f6', bgcolor: '#eff6ff', '&:hover': { bgcolor: '#dbeafe' } }}>
                        <Edit2 size={16} />
                      </IconButton>
                    </Box>
                  </Box>
                </DataCard>
              </Box>
            ))}
          </Box>
        )}
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: '800px', mx: 'auto', p: 4 }}>
      <Button
        startIcon={<ArrowLeft size={20} />}
        onClick={() => setViewMode('list')}
        sx={{ mb: 3, color: '#64748b', textTransform: 'none', fontWeight: 600 }}
      >
        Volver a la lista
      </Button>

      <SectionTitle>
        <Box className="flex items-center gap-2">
          <FolderPlus size={28} className="text-slate-600" />
          {editingId ? "Editar Expediente" : "Registrar Nuevo Expediente"}
        </Box>
      </SectionTitle>
      <Typography variant="body1" className="text-slate-500 mb-6">
        Ingresa la información detallada del caso forense.
      </Typography>

      <DataCard>
        <Box sx={{ p: 4, display: 'flex', flexDirection: 'column', gap: 3 }}>
          <TextField
            label="Código del Expediente"
            placeholder="Ej. EXP-2026-001"
            value={formData.caseNumber}
            onChange={(e) => setFormData({...formData, caseNumber: e.target.value})}
            error={!!formErrors.caseNumber}
            helperText={formErrors.caseNumber}
            fullWidth
            sx={premiumInputStyles}
          />
          
          <TextField
            label="Estado del Caso"
            select
            value={formData.status}
            onChange={(e) => setFormData({...formData, status: e.target.value})}
            error={!!formErrors.status}
            helperText={formErrors.status}
            fullWidth
            sx={premiumInputStyles}
          >
            <MenuItem value="ABIERTO">Abierto / En Investigación</MenuItem>
            <MenuItem value="CERRADO">Cerrado / Finalizado</MenuItem>
          </TextField>

          <TextField
            label="Descripción o Delito"
            placeholder="Descripción de los hechos o el delito investigado..."
            value={formData.description}
            onChange={(e) => setFormData({...formData, description: e.target.value})}
            error={!!formErrors.description}
            helperText={formErrors.description}
            fullWidth
            multiline
            rows={4}
            sx={premiumInputStyles}
          />

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 2 }}>
            <Button
              variant="outlined"
              onClick={() => setViewMode('list')}
              sx={{
                borderRadius: '12px',
                borderColor: '#e2e8f0',
                color: '#64748b',
                padding: '10px 24px',
                fontWeight: 600,
                '&:hover': {
                  borderColor: '#cbd5e1',
                  backgroundColor: '#f8fafc'
                }
              }}
            >
              Cancelar
            </Button>
            <Button
              variant="contained"
              onClick={handleSubmit}
              disabled={isSubmitting}
              startIcon={isSubmitting ? <CircularProgress size={20} color="inherit" /> : <CheckCircle2 size={20} />}
              sx={{
                backgroundColor: '#0f172a',
                borderRadius: '12px',
                padding: '10px 32px',
                fontWeight: 600,
                '&:hover': {
                  backgroundColor: '#1e293b'
                }
              }}
            >
              {editingId ? 'Guardar Cambios' : 'Crear Expediente'}
            </Button>
          </Box>
        </Box>
      </DataCard>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        message={snackbar.message}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      />
    </Box>
  );
};
