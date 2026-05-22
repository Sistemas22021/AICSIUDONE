import { Box, Typography, Modal, IconButton } from '@mui/material';
import { X, ArrowRight } from 'lucide-react';
import { AuditEntry } from '../../types/audit';

interface Props {
  entry: AuditEntry | null;
  onClose: () => void;
}

export const AuditDiffViewer = ({ entry, onClose }: Props) => {
  if (!entry) return null;

  return (
    <Modal open={!!entry} onClose={onClose} className="flex items-center justify-center p-4">
      <Box className="bg-white rounded-2xl w-full max-w-3xl overflow-hidden shadow-2xl animate-fade-in outline-none">
        <Box className="flex justify-between items-center p-6 border-b border-slate-100 bg-slate-50">
          <Box>
            <Typography variant="h6" className="font-extrabold text-slate-800">
              Detalle de Modificaciones
            </Typography>
            <Typography variant="body2" className="text-slate-500 font-medium">
              Expediente: {entry.targetEntity} • Por: {entry.userName}
            </Typography>
          </Box>
          <IconButton onClick={onClose} className="text-slate-400 hover:text-slate-600 bg-white shadow-sm border border-slate-200">
            <X size={20} />
          </IconButton>
        </Box>

        <Box className="p-6">
          <Box className="grid grid-cols-[1fr_auto_1fr] gap-4 mb-4 px-4 py-2 bg-slate-100 rounded-lg">
            <Typography variant="overline" className="font-bold text-slate-500">Valor Anterior</Typography>
            <Box className="w-8"></Box>
            <Typography variant="overline" className="font-bold text-slate-500">Valor Nuevo</Typography>
          </Box>

          <Box className="space-y-3">
            {entry.changes.map((change, idx) => (
              <Box key={idx} className="border border-slate-200 rounded-xl overflow-hidden">
                <Box className="bg-slate-50 px-4 py-2 border-b border-slate-200">
                  <Typography variant="caption" className="font-extrabold text-slate-600 uppercase tracking-wider">
                    Campo: {change.field}
                  </Typography>
                </Box>
                <Box className="grid grid-cols-[1fr_auto_1fr] items-center">
                  <Box className="p-4 bg-red-50/50">
                    <Typography className="text-red-800 font-mono text-sm line-through opacity-80">
                      {change.oldValue || '(vacío)'}
                    </Typography>
                  </Box>
                  <Box className="px-2 flex items-center justify-center">
                    <ArrowRight size={16} className="text-slate-400" />
                  </Box>
                  <Box className="p-4 bg-emerald-50/50">
                    <Typography className="text-emerald-800 font-mono text-sm font-bold">
                      {change.newValue || '(vacío)'}
                    </Typography>
                  </Box>
                </Box>
              </Box>
            ))}
            
            {entry.changes.length === 0 && (
              <Typography className="text-center text-slate-500 py-8 italic">
                No hay cambios registrados en los campos para este evento.
              </Typography>
            )}
          </Box>
        </Box>
      </Box>
    </Modal>
  );
};
