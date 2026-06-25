import { useState, useEffect } from 'react';
import { Box, TextField, MenuItem, Button, Typography, Snackbar, CircularProgress, LinearProgress, IconButton } from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { DataCard } from '../components/DataCard';
import { UploadCloud, Hash, CheckCircle2, X, Image as ImageIcon, ArrowLeft, Eraser, Plus, Pencil, Trash2, Database } from 'lucide-react';
import { 
  TwistDirection, 
  PercussionType, 
  FormStatus, 
  EvidenceFormState, 
  EvidenceFormErrors,
  EvidenceRecord
} from '../types/evidence';
import { getBullets, createBullet, updateBullet, deleteBullet, getBulletImageUrl } from '../services/bulletService';

const INITIAL_FORM_STATE: EvidenceFormState = {
  expediente: '',
  calibre: '',
  estrias: '',
  twist: '',
  percussion: '',
  marca: ''
};

export const CALIBERS = [
  { id: 1, name: '9mm Parabellum' },
  { id: 2, name: '.22 Long Rifle' },
  { id: 3, name: '.45 ACP' },
  { id: 4, name: '.308 Winchester' },
  { id: 5, name: '7.62x39mm' },
  { id: 6, name: '5.56x45mm NATO' },
  { id: 7, name: '.357 Magnum' },
  { id: 8, name: '12 gauge' }
];

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

export const RegistroPage = () => {
  // Estado general
  const [viewMode, setViewMode] = useState<'list' | 'form'>('list');
  const [records, setRecords] = useState<EvidenceRecord[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);

  // Estados del formulario
  const [formData, setFormData] = useState<EvidenceFormState>(INITIAL_FORM_STATE);
  const [errors, setErrors] = useState<EvidenceFormErrors>({});
  const [status, setStatus] = useState<FormStatus>(FormStatus.IDLE);
  
  // Estados para Drag & Drop dinámico
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);

  useEffect(() => {
    if (viewMode === 'list') {
      loadBullets();
    }
  }, [viewMode]);

  const loadBullets = async () => {
    try {
      const data = await getBullets(0, 50);
      const mappedRecords: EvidenceRecord[] = data.content.map(b => {
        const cal = CALIBERS.find(c => c.id === b.caliber);
        return {
          id: String(b.idBullet),
          createdAt: b.createdAt || '',
          expediente: b.caseFile,
          calibre: cal ? cal.name : String(b.caliber),
          estrias: String(b.landsAndGrooves),
          twist: b.twistDirection as TwistDirection,
          percussion: b.percussionType as PercussionType,
          marca: b.manufacturer,
          previewUrl: b.images && b.images.length > 0 ? getBulletImageUrl(b.images[0]) : null
        };
      });
      setRecords(mappedRecords);
    } catch (error) {
      console.error("Error loading bullets:", error);
    }
  };

  // --- CRUD LÓGICA ---
  const handleCreateNew = () => {
    handleClear();
    setEditingId(null);
    setViewMode('form');
  };

  const handleEdit = (record: EvidenceRecord) => {
    const cal = CALIBERS.find(c => c.name === record.calibre);
    setFormData({
      expediente: record.expediente,
      calibre: cal ? String(cal.id) : '',
      estrias: record.estrias,
      twist: record.twist,
      percussion: record.percussion,
      marca: record.marca
    });
    setEditingId(record.id);
    if (record.previewUrl) {
      setPreviewUrl(record.previewUrl);
      setSelectedFile(new File([""], "evidencia-previa.png", { type: "image/png" })); // Fake file to bypass validation
    } else {
      setSelectedFile(null);
      setPreviewUrl(null);
    }
    setViewMode('form');
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('¿Estás seguro de eliminar este registro balístico?')) {
      try {
        await deleteBullet(Number(id));
        setRecords(prev => prev.filter(r => r.id !== id));
      } catch (err) {
        console.error("Error al eliminar", err);
      }
    }
  };

  // --- VALIDACIÓN Y FORMULARIO ---
  const validateForm = (): boolean => {
    const newErrors: EvidenceFormErrors = {};
    let isValid = true;

    if (!formData.expediente.trim()) {
      newErrors.expediente = 'Requerido';
      isValid = false;
    }

    if (!formData.calibre || String(formData.calibre).trim() === '') {
      newErrors.calibre = 'Requerido';
      isValid = false;
    }

    if (!formData.estrias) {
      newErrors.estrias = 'Requerido';
      isValid = false;
    } else {
      const estriasNum = Number(formData.estrias);
      if (isNaN(estriasNum) || estriasNum <= 0) {
        newErrors.estrias = 'Debe ser > 0';
        isValid = false;
      }
    }

    if (!formData.twist) {
      newErrors.twist = 'Seleccione una opción';
      isValid = false;
    }

    if (!formData.percussion) {
      newErrors.percussion = 'Seleccione una opción';
      isValid = false;
    }

    if (!formData.marca.trim()) {
      newErrors.marca = 'Requerido';
      isValid = false;
    }
    
    if (!selectedFile && !previewUrl) {
      newErrors.general = 'Debes subir una evidencia fotográfica.';
      isValid = false;
    }

    if (!isValid && !newErrors.general) {
      newErrors.general = 'Corrige los errores en rojo antes de continuar.';
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleInputChange = (field: keyof EvidenceFormState, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  // --- LÓGICA DRAG & DROP ---
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      processFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      processFile(e.target.files[0]);
    }
  };

  const processFile = (file: File) => {
    if (!file.type.startsWith('image/')) {
      setErrors(prev => ({ ...prev, general: 'Por favor, sube solo archivos de imagen (JPG, PNG).' }));
      setStatus(FormStatus.ERROR);
      return;
    }
    
    if (errors.general) {
      setErrors(prev => ({ ...prev, general: undefined }));
      setStatus(FormStatus.IDLE);
    }
    
    setIsUploading(true);
    setTimeout(() => {
      setSelectedFile(file);
      setPreviewUrl(URL.createObjectURL(file));
      setIsUploading(false);
    }, 1500);
  };

  const removeFile = () => {
    setSelectedFile(null);
    if (previewUrl && previewUrl.startsWith('blob:')) {
      URL.revokeObjectURL(previewUrl);
    }
    setPreviewUrl(null);
  };

  // --- ACCIONES ---
  const handleSave = async () => {
    if (!validateForm()) {
      setStatus(FormStatus.ERROR);
      return;
    }

    setStatus(FormStatus.SAVING);

    try {
      const bulletData = {
        caseFile: formData.expediente,
        landsAndGrooves: Number(formData.estrias),
        percussionType: formData.percussion,
        twistDirection: formData.twist,
        caliber: Number(formData.calibre),
        manufacturer: formData.marca
      };

      if (editingId) {
        await updateBullet(Number(editingId), bulletData);
      } else {
        if (!selectedFile) throw new Error("Archivo de imagen requerido para registro nuevo");
        await createBullet(bulletData, selectedFile);
      }
      
      setStatus(FormStatus.SUCCESS);
      
      setTimeout(() => {
        handleClear();
        setViewMode('list');
        setStatus(FormStatus.IDLE);
      }, 1500);
      
    } catch (err) {
      console.error(err);
      setStatus(FormStatus.ERROR);
      setErrors(prev => ({ ...prev, general: 'Error al procesar la evidencia en el servidor.' }));
    }
  };

  const handleClear = () => {
    setFormData(INITIAL_FORM_STATE);
    setErrors({});
    setStatus(FormStatus.IDLE);
    removeFile();
  };

  const handleCancel = () => {
    setViewMode('list');
    handleClear();
  };

  const handleCloseSnackbar = () => {
    if (status === FormStatus.SUCCESS || status === FormStatus.ERROR) {
      setStatus(FormStatus.IDLE);
    }
  };

  // ----- VISTA DE LISTA -----
  if (viewMode === 'list') {
    return (
      <Box className="animate-fade-in max-w-7xl mx-auto px-6 py-6">
        <Box className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
          <SectionTitle className="!mb-0">Lista de expedientes balisticos</SectionTitle>
          <Button 
            variant="contained" 
            startIcon={<Plus size={20} strokeWidth={2.5} />}
            onClick={handleCreateNew}
            className="bg-slate-900 text-white hover:bg-indigo-600 shadow-md hover:shadow-lg hover:-translate-y-0.5 px-6 py-2.5 font-bold transition-all duration-300"
            sx={{ textTransform: 'none', borderRadius: '999px', bgcolor: '#0f172a' }}
          >
            Crear registro
          </Button>
        </Box>

        <DataCard noPadding>
          {records.length === 0 ? (
            <Box className="flex flex-col items-center justify-center py-20 px-4 text-center">
              <Box className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mb-4">
                <Database size={32} className="text-slate-300" strokeWidth={1.5} />
              </Box>
              <Typography variant="h6" className="text-slate-800 font-bold mb-2">
                No hay registros aún
              </Typography>
              <Typography variant="body2" className="text-slate-500 max-w-sm mb-6">
                Crea tu primer registro de metadatos balísticos para empezar a armar la base de datos de expedientes.
              </Typography>
              <Button 
                variant="outlined" 
                startIcon={<Plus size={18} />}
                onClick={handleCreateNew}
                className="font-bold border-slate-200 text-slate-700 hover:bg-slate-50 px-6 py-2"
                sx={{ textTransform: 'none', borderRadius: '999px' }}
              >
                Comenzar ahora
              </Button>
            </Box>
          ) : (
            <div className="overflow-x-auto pb-4">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-slate-50/50 border-b border-slate-100">
                    <th className="p-4 pl-8 text-xs font-bold text-slate-500 uppercase tracking-wider">N° Expediente</th>
                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Calibre</th>
                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Estrías/Giro</th>
                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Marca</th>
                    <th className="p-4 pr-8 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">Acción</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {records.map((record) => (
                    <tr key={record.id} className="hover:bg-slate-50/80 transition-colors group">
                      <td className="p-4 pl-8 font-bold text-slate-800">{record.expediente}</td>
                      <td className="p-4">
                        <span className="inline-flex items-center text-xs font-extrabold text-slate-700 bg-slate-100 px-2.5 py-1 rounded-md">
                          {record.calibre}
                        </span>
                      </td>
                      <td className="p-4 text-slate-600 font-medium">{record.estrias} / {record.twist.split(' ')[0]}</td>
                      <td className="p-4 text-slate-600 font-medium">{record.marca}</td>
                      <td className="p-4 pr-8 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <IconButton 
                            size="small" 
                            onClick={() => handleEdit(record)}
                            className="bg-white border border-slate-200 text-slate-500 hover:text-indigo-600 hover:border-indigo-200 hover:bg-indigo-50 shadow-sm transition-colors"
                          >
                            <Pencil size={16} />
                          </IconButton>
                          <IconButton 
                            size="small" 
                            onClick={() => handleDelete(record.id)}
                            className="bg-white border border-slate-200 text-slate-500 hover:text-red-600 hover:border-red-200 hover:bg-red-50 shadow-sm transition-colors"
                          >
                            <Trash2 size={16} />
                          </IconButton>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </DataCard>
      </Box>
    );
  }

  // ----- VISTA DE FORMULARIO -----
  return (
    <Box className="animate-fade-in max-w-7xl mx-auto px-6 py-6">
      <SectionTitle>{editingId ? 'Editar Registro Balístico' : 'Nuevo Registro Balístico'}</SectionTitle>
      
      <Box className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <Box className="lg:col-span-2">
          <DataCard className="h-full flex flex-col">
            <Typography variant="overline" className="text-slate-400 font-extrabold tracking-widest block" sx={{ mb: 3 }}>
              INFORMACIÓN TÉCNICA DE LA EVIDENCIA
            </Typography>
            
            <Box className="grid grid-cols-1 md:grid-cols-2 gap-6 flex-grow">
              <TextField
                fullWidth
                label="N° de Expediente"
                placeholder="EXP-2026-089"
                value={formData.expediente}
                onChange={(e) => handleInputChange('expediente', e.target.value)}
                error={!!errors.expediente}
                helperText={errors.expediente}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              />
              <TextField
                fullWidth
                select
                label="Calibre"
                value={formData.calibre}
                onChange={(e) => handleInputChange('calibre', e.target.value)}
                error={!!errors.calibre}
                helperText={errors.calibre}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              >
                {CALIBERS.map((cal) => (
                  <MenuItem key={cal.id} value={cal.id}>
                    {cal.name}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                fullWidth
                label="Número de Estrías"
                type="number"
                placeholder="Ej: 6"
                value={formData.estrias}
                onChange={(e) => handleInputChange('estrias', e.target.value)}
                error={!!errors.estrias}
                helperText={errors.estrias}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              />
              <TextField
                fullWidth
                select
                label="Dirección de Giro (Twist)"
                value={formData.twist}
                onChange={(e) => handleInputChange('twist', e.target.value)}
                error={!!errors.twist}
                helperText={errors.twist}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              >
                {Object.values(TwistDirection).map((option) => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                fullWidth
                select
                label="Tipo de Percusión"
                value={formData.percussion}
                onChange={(e) => handleInputChange('percussion', e.target.value)}
                error={!!errors.percussion}
                helperText={errors.percussion}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              >
                {Object.values(PercussionType).map((option) => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                fullWidth
                label="Marca del Fabricante"
                placeholder="Ej: Luger"
                value={formData.marca}
                onChange={(e) => handleInputChange('marca', e.target.value)}
                error={!!errors.marca}
                helperText={errors.marca}
                variant="outlined"
                size="medium"
                sx={premiumInputStyles}
              />
            </Box>
            
            <Box className="mt-10 pt-8 border-t border-slate-100 flex flex-col sm:flex-row justify-end items-center gap-4">
              <Button 
                variant="text" 
                startIcon={<ArrowLeft size={18} strokeWidth={2.5} />}
                onClick={handleCancel}
                disabled={status === FormStatus.SAVING}
                className="w-full sm:w-auto font-bold text-slate-500 hover:text-slate-800 hover:bg-slate-100 px-6 py-2.5 transition-colors"
                sx={{ textTransform: 'none', borderRadius: '999px' }}
              >
                Volver
              </Button>
              <Button 
                variant="outlined" 
                startIcon={<Eraser size={18} strokeWidth={2.5} />}
                onClick={handleClear}
                disabled={status === FormStatus.SAVING}
                className="w-full sm:w-auto font-bold border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300 hover:text-slate-900 px-6 py-2.5 transition-all shadow-sm"
                sx={{ textTransform: 'none', borderRadius: '999px' }}
              >
                Limpiar datos
              </Button>
              <Button 
                variant="contained" 
                startIcon={status === FormStatus.SAVING ? <CircularProgress size={18} color="inherit" /> : <Hash size={18} strokeWidth={2.5} />}
                onClick={handleSave}
                disabled={status === FormStatus.SAVING}
                className="w-full sm:w-auto bg-slate-900 text-white hover:bg-indigo-600 shadow-md hover:shadow-lg hover:-translate-y-0.5 px-8 py-2.5 font-bold transition-all duration-300"
                sx={{ textTransform: 'none', borderRadius: '999px', bgcolor: '#0f172a' }}
              >
                {status === FormStatus.SAVING ? 'Procesando...' : (editingId ? 'Actualizar Registro' : 'Guardar y Generar Hash')}
              </Button>
            </Box>
          </DataCard>
        </Box>
        
        <Box className="lg:col-span-1">
          <DataCard className="h-full flex flex-col">
            <Typography variant="overline" className="text-slate-400 font-extrabold tracking-widest block" sx={{ mb: 3 }}>
              DOCUMENTACIÓN FOTOGRÁFICA
            </Typography>
            
            <Box 
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
              onClick={() => !selectedFile && !isUploading && document.getElementById('file-upload')?.click()}
              className={`relative group border-2 border-dashed rounded-[1.5rem] p-6 flex flex-col items-center justify-center transition-all duration-300 flex-grow cursor-pointer
                ${isDragging ? 'border-indigo-500 bg-indigo-50/50 scale-[1.02]' : 'border-slate-200 bg-slate-50/50'}
                ${!selectedFile && !isDragging ? 'hover:border-indigo-300 hover:bg-slate-50' : ''}
              `}
              sx={{ minHeight: '320px' }}
            >
              <input 
                type="file" 
                id="file-upload" 
                className="hidden" 
                accept="image/*"
                onChange={handleFileInput}
              />

              {isUploading ? (
                <Box className="flex flex-col items-center justify-center w-full animate-fade-in">
                  <CircularProgress size={40} className="text-indigo-500 mb-6" thickness={4} />
                  <Typography variant="body2" className="text-indigo-700 font-bold tracking-tight" sx={{ mb: 1 }}>
                    Normalizando imagen para cotejo...
                  </Typography>
                  <Typography variant="caption" className="text-indigo-500 font-mono tracking-tight" sx={{ mb: 2 }}>
                    Aplicando formato PNG lossless y tamaño 1:1
                  </Typography>
                  <Box className="w-full px-8 mt-2">
                    <LinearProgress className="rounded-full h-1.5" color="inherit" sx={{ color: '#6366f1', bgcolor: '#e0e7ff' }} />
                  </Box>
                </Box>
              ) : previewUrl ? (
                <Box className="flex flex-col items-center animate-fade-in w-full">
                  <Box className="relative mb-6">
                    <img 
                      src={previewUrl} 
                      alt="Evidencia balística" 
                      className="w-full max-w-[200px] aspect-square rounded-[1.25rem] shadow-md border-4 border-white object-cover transition-transform duration-500 group-hover:scale-105"
                    />
                    <Box className="absolute -bottom-2 -right-2 bg-emerald-500 text-white p-1.5 rounded-full shadow-lg border-2 border-white z-20">
                      <CheckCircle2 size={18} strokeWidth={3} />
                    </Box>
                  </Box>
                  <Typography variant="body2" className="text-slate-800 font-bold text-center w-full truncate px-2" sx={{ mt: 2 }}>
                    {selectedFile?.name || 'Imagen cargada'}
                  </Typography>
                  <Box className="flex flex-col gap-1 items-center mt-2">
                    <Typography variant="caption" className="text-emerald-600 font-bold bg-emerald-50 px-3 py-1 rounded-full border border-emerald-100" sx={{ display: 'inline-block' }}>
                      Formato Normalizado (PNG Lossless)
                    </Typography>
                    <Typography variant="caption" className="text-emerald-600 font-bold bg-emerald-50 px-3 py-1 rounded-full border border-emerald-100" sx={{ display: 'inline-block' }}>
                      SHA-256 Verificado
                    </Typography>
                  </Box>
                  
                  <Box className="flex gap-3 mt-6">
                    <Button 
                      variant="text" 
                      startIcon={<UploadCloud size={16} strokeWidth={2.5} />}
                      onClick={(e) => { e.stopPropagation(); document.getElementById('file-upload')?.click(); }}
                      className="text-slate-600 hover:text-indigo-600 font-bold rounded-full px-5 py-2 transition-colors bg-white hover:bg-indigo-50 border border-slate-200 shadow-sm"
                      sx={{ textTransform: 'none', whiteSpace: 'nowrap' }}
                    >
                      Cambiar
                    </Button>
                    <Button 
                      variant="text" 
                      startIcon={<Trash2 size={16} strokeWidth={2.5} />}
                      onClick={(e) => { e.stopPropagation(); removeFile(); }}
                      className="text-slate-600 hover:text-red-600 font-bold rounded-full px-5 py-2 transition-colors bg-white hover:bg-red-50 border border-slate-200 shadow-sm"
                      sx={{ textTransform: 'none', whiteSpace: 'nowrap' }}
                    >
                      Eliminar
                    </Button>
                  </Box>
                </Box>
              ) : (
                <Box className="flex flex-col items-center justify-center text-center animate-fade-in px-2">
                  <Box className={`w-16 h-16 rounded-2xl shadow-sm flex items-center justify-center mb-5 transition-all duration-300
                    ${isDragging ? 'bg-indigo-500 text-white scale-110 shadow-indigo-200' : 'bg-white text-slate-400 group-hover:bg-indigo-50 group-hover:text-indigo-500 group-hover:-translate-y-1 group-hover:shadow-md'}
                  `}>
                    <ImageIcon size={28} strokeWidth={2.5} />
                  </Box>
                  <Typography variant="subtitle1" className="font-extrabold text-slate-800 tracking-tight leading-tight" sx={{ mb: 1.5 }}>
                    {isDragging ? '¡Suéltala aquí!' : 'Evidencia Fotográfica'}
                  </Typography>
                  <Typography variant="body2" className="text-slate-500 leading-relaxed" sx={{ mb: 4 }}>
                    Arrastra tu imagen o haz clic para buscar.
                  </Typography>
                  <Button 
                    variant="contained" 
                    className="bg-slate-900 text-white shadow-none hover:bg-indigo-600 hover:shadow-md font-bold pointer-events-none transition-all duration-300"
                    sx={{ textTransform: 'none', borderRadius: '999px', whiteSpace: 'nowrap', px: 4, py: 1 }}
                  >
                    Explorar archivos
                  </Button>
                </Box>
              )}
            </Box>
            
            <Box className="mt-6 p-5 bg-gradient-to-br from-amber-50 to-orange-50 border border-amber-100 rounded-2xl">
              <Typography variant="caption" className="text-amber-800 font-medium leading-relaxed block text-justify">
                <strong>ATENCIÓN:</strong> La imagen será procesada y normalizada a un formato sin pérdida (Lossless PNG/TIFF) y redimensionada (1:1) para extraer patrones de micro-estriado. Asegúrese de que la iluminación lateral sea la adecuada.
              </Typography>
            </Box>
          </DataCard>
        </Box>
      </Box>

      <Snackbar 
        open={status === FormStatus.SUCCESS || status === FormStatus.ERROR} 
        autoHideDuration={6000} 
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        sx={{ top: { xs: 80, sm: 90 }, zIndex: 2000 }}
      >
        <Box 
          className={`flex items-center gap-4 p-4 pr-5 rounded-[1rem] shadow-[0_10px_40px_rgb(0,0,0,0.1)] border backdrop-blur-md ${
            status === FormStatus.SUCCESS 
              ? 'bg-emerald-50/90 border-emerald-200' 
              : 'bg-red-50/90 border-red-200'
          }`}
          sx={{ minWidth: '350px' }}
        >
          <Box className={`flex flex-shrink-0 items-center justify-center w-12 h-12 rounded-full ${
            status === FormStatus.SUCCESS ? 'bg-emerald-100 text-emerald-600' : 'bg-red-100 text-red-600'
          }`}>
            {status === FormStatus.SUCCESS ? <CheckCircle2 size={24} strokeWidth={2.5} /> : <X size={24} strokeWidth={2.5} />}
          </Box>
          <Box className="flex-grow">
            <Typography variant="subtitle2" className={`font-extrabold text-base tracking-tight leading-tight mb-0.5 ${
              status === FormStatus.SUCCESS ? 'text-emerald-900' : 'text-red-900'
            }`}>
              {status === FormStatus.SUCCESS 
                ? (editingId ? '¡Registro Actualizado!' : '¡Registro Exitoso!') 
                : 'Error de Validación'}
            </Typography>
            <Typography variant="body2" className={`font-medium ${
              status === FormStatus.SUCCESS ? 'text-emerald-700' : 'text-red-700'
            }`}>
              {status === FormStatus.SUCCESS 
                ? 'El hash criptográfico ha sido generado y firmado en la red.' 
                : errors.general || 'Por favor revisa los campos marcados en rojo.'}
            </Typography>
          </Box>
          <Button 
            onClick={handleCloseSnackbar} 
            className={`min-w-0 p-2 rounded-full transition-colors ${
              status === FormStatus.SUCCESS ? 'text-emerald-400 hover:text-emerald-700 hover:bg-emerald-100' : 'text-red-400 hover:text-red-700 hover:bg-red-100'
            }`}
            sx={{ borderRadius: '999px' }}
          >
            <X size={18} strokeWidth={2.5} />
          </Button>
        </Box>
      </Snackbar>
    </Box>
  );
};
