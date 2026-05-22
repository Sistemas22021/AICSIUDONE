import { Box, Typography, Button } from '@mui/material';
import { 
  Plus, Pencil, Trash2, Image as ImageIcon, 
  RefreshCw, GitCompare, LogIn, Download, Eye
} from 'lucide-react';
import { AuditActionType, AuditEntry } from '../../types/audit';

interface Props {
  entries: AuditEntry[];
  onViewDiff: (entry: AuditEntry) => void;
}

const getTypeConfig = (type: AuditActionType) => {
  switch(type) {
    case AuditActionType.RECORD_CREATED: return { color: 'emerald', icon: <Plus size={16} strokeWidth={2.5} /> };
    case AuditActionType.RECORD_UPDATED: return { color: 'amber', icon: <Pencil size={16} strokeWidth={2.5} /> };
    case AuditActionType.RECORD_DELETED: return { color: 'red', icon: <Trash2 size={16} strokeWidth={2.5} /> };
    case AuditActionType.IMAGE_UPLOAD: return { color: 'indigo', icon: <ImageIcon size={16} strokeWidth={2.5} /> };
    case AuditActionType.STATE_CHANGE: return { color: 'orange', icon: <RefreshCw size={16} strokeWidth={2.5} /> };
    case AuditActionType.COMPARISON_RUN: return { color: 'cyan', icon: <GitCompare size={16} strokeWidth={2.5} /> };
    case AuditActionType.LOGIN: return { color: 'slate', icon: <LogIn size={16} strokeWidth={2.5} /> };
    case AuditActionType.EXPORT: return { color: 'violet', icon: <Download size={16} strokeWidth={2.5} /> };
    default: return { color: 'slate', icon: <Plus size={16} strokeWidth={2.5} /> };
  }
};

const formatDate = (isoString: string) => {
  const date = new Date(isoString);
  return new Intl.DateTimeFormat('es-ES', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  }).format(date);
};

export const AuditTimeline = ({ entries, onViewDiff }: Props) => {
  if (entries.length === 0) {
    return (
      <Box className="py-12 text-center bg-white rounded-3xl border border-slate-100 shadow-sm">
        <Typography className="text-slate-500 font-medium">No se encontraron registros de auditoría para estos filtros.</Typography>
      </Box>
    );
  }

  return (
    <Box className="relative pl-6 sm:pl-0">
      {/* Línea vertical principal */}
      <div className="hidden sm:block absolute left-[120px] top-6 bottom-0 w-0.5 bg-slate-200/60 rounded-full"></div>
      
      <Box className="space-y-8">
        {entries.map((entry) => {
          const config = getTypeConfig(entry.actionType);
          const colorClass = `text-${config.color}-600 bg-${config.color}-100 border-${config.color}-200`;
          
          return (
            <Box key={entry.id} className="relative flex flex-col sm:flex-row items-start group animate-fade-in">
              {/* Timestamp (Desktop) */}
              <Box className="hidden sm:block w-[100px] text-right pt-3 pr-6 flex-shrink-0">
                <Typography variant="caption" className="font-mono text-slate-500 font-bold tracking-tight">
                  {new Date(entry.timestamp).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}
                </Typography>
                <Typography variant="caption" className="block font-mono text-slate-400 text-[10px] mt-0.5">
                  {new Date(entry.timestamp).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })}
                </Typography>
              </Box>

              {/* Nodo conmutador */}
              <Box className={`absolute sm:relative left-[-30px] sm:left-0 z-10 w-10 h-10 rounded-full border-[3px] border-white flex items-center justify-center flex-shrink-0 shadow-sm transition-all duration-300 group-hover:scale-110 group-hover:shadow-md ${colorClass}`}>
                {config.icon}
              </Box>

              {/* Tarjeta de contenido */}
              <Box className="sm:ml-6 flex-grow bg-white border border-slate-100 rounded-[1.25rem] p-4 sm:p-5 shadow-sm hover:shadow-[0_8px_30px_rgb(0,0,0,0.06)] transition-all duration-300 w-full relative overflow-hidden">
                <div className={`absolute top-0 left-0 w-1.5 h-full bg-${config.color}-400 opacity-50`}></div>
                
                <Box className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-2 mb-3">
                  <Box>
                    <Box className="flex items-center gap-2 mb-1.5 flex-wrap">
                      <span className={`px-2.5 py-0.5 rounded-md text-[10px] font-extrabold uppercase tracking-wider ${colorClass.replace('border', '')}`}>
                        {entry.actionType}
                      </span>
                      {entry.ipAddress && (
                        <span className="text-[10px] font-mono font-bold text-slate-400 bg-slate-50 px-2 py-0.5 rounded-md border border-slate-200">
                          IP: {entry.ipAddress}
                        </span>
                      )}
                    </Box>
                    <Typography className="text-slate-600 font-medium text-sm">
                      Realizado por <span className="font-extrabold text-slate-900">{entry.userName}</span> <span className="text-slate-400">({entry.userRole})</span>
                    </Typography>
                  </Box>
                  
                  {/* Timestamp (Mobile) */}
                  <Typography variant="caption" className="sm:hidden font-mono text-slate-400 font-bold bg-slate-50 px-2 py-1 rounded-md border border-slate-100">
                    {formatDate(entry.timestamp)}
                  </Typography>

                  <Typography variant="body2" className="font-mono font-bold text-slate-700 bg-slate-100 px-3 py-1 rounded-lg border border-slate-200">
                    {entry.targetEntity}
                  </Typography>
                </Box>
                
                <Typography variant="body2" className="text-slate-700 leading-relaxed mb-4 pl-1">
                  {entry.description}
                </Typography>
                
                {entry.changes && entry.changes.length > 0 && (
                  <Button 
                    variant="outlined" 
                    size="small"
                    startIcon={<Eye size={14} strokeWidth={2.5} />}
                    onClick={() => onViewDiff(entry)}
                    className="text-indigo-600 border-indigo-200 hover:bg-indigo-50 font-bold rounded-xl px-4"
                    sx={{ textTransform: 'none' }}
                  >
                    Ver detalle de modificaciones ({entry.changes.length})
                  </Button>
                )}
              </Box>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
};
