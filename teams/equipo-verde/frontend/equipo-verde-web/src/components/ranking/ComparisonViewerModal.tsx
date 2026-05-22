import { Dialog, Box, Typography, IconButton, Paper, Chip } from '@mui/material';
import { X, CheckCircle2, AlertCircle, Fingerprint, ShieldAlert, Crosshair, HelpCircle } from 'lucide-react';
import { CorrelationResult } from '../../services/correlationService';
import { EvidenceRecord } from '../../types/evidence';

interface Props {
  open: boolean;
  onClose: () => void;
  result: CorrelationResult | null;
  sourceEvidence: EvidenceRecord | null;
}

export const ComparisonViewerModal = ({ open, onClose, result, sourceEvidence }: Props) => {
  if (!result || !sourceEvidence) return null;

  const { targetEvidence, matchInfo } = result;

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="md"
      fullWidth
      classes={{ paper: 'rounded-[1.5rem] shadow-2xl overflow-hidden' }}
    >
      {/* Header */}
      <Box className="bg-slate-900 px-6 py-4 flex items-center justify-between border-b border-slate-800">
        <Box className="flex items-center gap-3">
          <Box className="p-2 bg-indigo-500/20 rounded-lg border border-indigo-500/30">
            <Fingerprint className="text-indigo-400" size={24} />
          </Box>
          <Box>
            <Typography variant="h6" className="text-white font-bold leading-tight">
              Análisis Comparativo Detallado
            </Typography>
            <Typography variant="caption" className="text-slate-400 font-medium">
              Motor Matemático de Correlación Balística (V1.0)
            </Typography>
          </Box>
        </Box>
        
        <Box className="flex items-center gap-4">
          <Box className="flex flex-col items-end">
            <Typography variant="caption" className="text-slate-400 font-bold uppercase tracking-wider">
              Similitud General
            </Typography>
            <Typography variant="h6" className="text-emerald-400 font-black leading-none">
              {matchInfo.score}%
            </Typography>
          </Box>
          <IconButton onClick={onClose} size="small" className="text-slate-400 hover:text-white hover:bg-slate-800 transition-colors">
            <X size={20} />
          </IconButton>
        </Box>
      </Box>

      {/* Content */}
      <Box className="bg-slate-50 p-6 max-h-[75vh] overflow-y-auto">
        
        {/* Visor de Cotejo Directo (Imágenes) */}
        <Box className="mb-6 bg-slate-900 rounded-xl border border-slate-800 shadow-inner overflow-hidden">
          <Box className="grid grid-cols-2 divide-x divide-slate-800 relative">
            
            {/* Lente Central (Decoración) */}
            <Box className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-12 h-12 bg-slate-900 rounded-full border-4 border-slate-800 z-10 flex items-center justify-center shadow-2xl">
              <Crosshair size={20} className="text-emerald-500" />
            </Box>

            {/* Imagen Origen */}
            <Box className="relative aspect-square bg-slate-800 group">
              <Box className="absolute top-3 left-3 z-10">
                <Chip 
                  label="Origen" 
                  size="small" 
                  className="bg-indigo-500 text-white font-bold tracking-wider shadow-lg" 
                />
              </Box>
              <img 
                src="/bullet-1.png" 
                alt="Evidencia Origen" 
                className="w-full h-full object-cover opacity-90 group-hover:opacity-100 transition-opacity"
              />
              <Box className="absolute bottom-3 left-3 right-3">
                <Typography variant="caption" className="text-white bg-black/60 px-2 py-1 rounded backdrop-blur-sm font-mono text-[10px]">
                  FMT: PNG LOSSLESS | RES: 1080x1080
                </Typography>
              </Box>
            </Box>

            {/* Imagen Hallazgo */}
            <Box className="relative aspect-square bg-slate-800 group">
              <Box className="absolute top-3 right-3 z-10">
                <Chip 
                  label="Hallazgo" 
                  size="small" 
                  className="bg-slate-700 text-slate-100 font-bold tracking-wider shadow-lg" 
                />
              </Box>
              <img 
                src="/bullet-2.png" 
                alt="Evidencia Hallazgo" 
                className="w-full h-full object-cover opacity-90 group-hover:opacity-100 transition-opacity"
              />
              <Box className="absolute bottom-3 left-3 right-3 text-right">
                <Typography variant="caption" className="text-white bg-black/60 px-2 py-1 rounded backdrop-blur-sm font-mono text-[10px]">
                  FMT: PNG LOSSLESS | RES: 1080x1080
                </Typography>
              </Box>
            </Box>
          </Box>
          <Box className="bg-slate-900 px-4 py-2 border-t border-slate-800 text-center">
            <Typography variant="caption" className="text-slate-400 font-bold tracking-widest uppercase text-[10px]">
              Visualización microscópica normalizada (1:1 Aspect Ratio)
            </Typography>
          </Box>
        </Box>

        {/* Abstract Context */}
        <Box className="mb-6 bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-start gap-4">
          <Box className="mt-1">
            <Crosshair className="text-slate-400" size={24} />
          </Box>
          <Box>
            <Typography variant="subtitle2" className="text-slate-800 font-bold mb-1">
              Contexto del Análisis
            </Typography>
            <Typography variant="body2" className="text-slate-600 leading-relaxed text-justify">
              Este reporte presenta el desglose del cotejo jerárquico entre la evidencia origen (<strong>{sourceEvidence.expediente}</strong>) 
              y el hallazgo en base de datos (<strong>{targetEvidence.expediente}</strong>). 
              El algoritmo evalúa características de clase (calibre, estrías, rotación) y de manufactura para determinar 
              la probabilidad de que ambas muestras hayan sido percutidas por la misma arma de fuego.
            </Typography>
          </Box>
        </Box>

        {/* Comparison Grid Header */}
        <Box className="grid grid-cols-12 gap-4 mb-4 px-2">
          <Box className="col-span-3">
            <Typography variant="caption" className="text-slate-500 font-bold uppercase tracking-wider">Característica</Typography>
          </Box>
          <Box className="col-span-4 text-center">
            <Chip 
              label={`Origen: ${sourceEvidence.expediente}`} 
              size="small" 
              className="bg-indigo-50 text-indigo-700 font-bold border border-indigo-200" 
            />
          </Box>
          <Box className="col-span-4 text-center">
            <Chip 
              label={`Hallazgo: ${targetEvidence.expediente}`} 
              size="small" 
              className="bg-slate-100 text-slate-700 font-bold border border-slate-200" 
            />
          </Box>
          <Box className="col-span-1 text-center">
            <Typography variant="caption" className="text-slate-500 font-bold uppercase tracking-wider">Pts</Typography>
          </Box>
        </Box>

        {/* Detailed Traits */}
        <Box className="flex flex-col gap-3">
          {matchInfo.details.map((detail, idx) => (
            <Paper 
              key={idx} 
              elevation={0}
              className={`border overflow-hidden transition-all ${
                detail.isMatch 
                  ? 'border-emerald-200 bg-emerald-50/30' 
                  : 'border-slate-200 bg-white'
              }`}
            >
              <Box className="grid grid-cols-12 gap-4 p-4 items-center">
                {/* Field Name */}
                <Box className="col-span-3 flex items-center gap-2">
                  {detail.isMatch ? (
                    <CheckCircle2 size={16} className="text-emerald-500 flex-shrink-0" />
                  ) : (
                    <AlertCircle size={16} className="text-slate-400 flex-shrink-0" />
                  )}
                  <Typography variant="subtitle2" className="text-slate-800 font-bold">
                    {detail.field}
                  </Typography>
                </Box>

                {/* Values Comparison */}
                <Box className="col-span-4 text-center">
                  <Typography variant="body2" className="text-slate-700 font-medium">
                    {detail.sourceValue}
                  </Typography>
                </Box>
                <Box className="col-span-4 text-center">
                  <Typography variant="body2" className="text-slate-700 font-medium">
                    {detail.targetValue}
                  </Typography>
                </Box>

                {/* Score */}
                <Box className="col-span-1 text-center">
                  <Typography variant="subtitle2" className={`font-black ${detail.isMatch ? 'text-emerald-600' : 'text-slate-400'}`}>
                    +{detail.scoreContribution}
                  </Typography>
                </Box>
              </Box>
              
              {/* Forensic Justification */}
              <Box className={`px-4 py-3 border-t text-sm flex gap-3 ${detail.isMatch ? 'bg-emerald-50/50 border-emerald-100' : 'bg-slate-50 border-slate-100'}`}>
                <Box className="mt-0.5">
                  <HelpCircle size={16} className={detail.isMatch ? 'text-emerald-500' : 'text-slate-400'} />
                </Box>
                <Box>
                  <Typography variant="caption" className={`font-bold block mb-0.5 ${detail.isMatch ? 'text-emerald-800' : 'text-slate-700'}`}>
                    Análisis Forense:
                  </Typography>
                  <Typography variant="body2" className={detail.isMatch ? 'text-emerald-700' : 'text-slate-600'}>
                    {detail.justification}
                  </Typography>
                </Box>
              </Box>
            </Paper>
          ))}
        </Box>

        {/* Footer info */}
        <Box className="mt-6 flex items-center gap-3 p-4 bg-blue-50 border border-blue-100 rounded-xl">
          <ShieldAlert className="text-blue-500 flex-shrink-0" size={24} />
          <Typography variant="caption" className="text-blue-800 leading-relaxed text-justify">
            <strong>Nota Legal:</strong> Este puntaje es generado por un modelo estadístico de comparación de características de clase y no sustituye el peritaje microscópico final. Un puntaje alto indica "alta probabilidad de vinculación jerárquica", debiendo ser el perito humano quien emita el dictamen conclusivo tras la observación en el microscopio de comparación.
          </Typography>
        </Box>

      </Box>
    </Dialog>
  );
};
