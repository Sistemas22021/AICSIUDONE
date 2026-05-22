import { 
  Box, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper
} from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { Terminal, ShieldCheck, User, Globe } from 'lucide-react';

const logs = [
  {
    timestamp: '2026-04-28 10:15:02',
    usuario: 'Perito A. Córdoba',
    accion: 'INGRESO NUEVA EVIDENCIA',
    detalle: 'Evidencia: EXP-2026-089',
    ip: '192.168.1.45'
  },
  {
    timestamp: '2026-04-28 10:45:12',
    usuario: 'Analista J. Pérez',
    accion: 'CONSULTA CORRELACIÓN',
    detalle: 'Parámetros: 9mm, 6 estrías',
    ip: '192.168.1.12'
  },
  {
    timestamp: '2026-04-28 14:22:00',
    usuario: 'Juez Tribunal 2',
    accion: 'CAMBIO DE ESTADO: A TRIBUNAL',
    detalle: 'Evidencia: EV-410',
    ip: '10.0.4.55'
  },
];

export const AuditoriaPage = () => {
  return (
    <Box>
      <SectionTitle>Logs de Inmutabilidad y Trazabilidad</SectionTitle>
      
      <TableContainer component={Paper} elevation={0} className="border border-slate-200 rounded-xl overflow-hidden shadow-sm">
        <Box className="bg-slate-900 p-4 flex items-center gap-3">
          <Terminal size={20} className="text-emerald-400" />
          <Typography variant="body2" className="text-emerald-400 font-mono font-bold tracking-wider">
            SISTEMA DE AUDITORÍA FORENSE - CADENA DE CUSTODIA DIGITAL v4.0.2
          </Typography>
        </Box>
        
        <Table sx={{ minWidth: 650 }}>
          <TableHead className="bg-slate-50">
            <TableRow>
              <TableCell className="font-bold text-slate-500 uppercase text-[10px] w-[180px]">Timestamp</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[10px]">Usuario</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[10px]">Acción / Operación</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[10px]">Objeto de Datos</TableCell>
              <TableCell className="font-bold text-slate-500 uppercase text-[10px] text-right">Dirección IP</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {logs.map((log, index) => (
              <TableRow key={index} className="hover:bg-slate-50 font-mono text-xs">
                <TableCell className="text-slate-500">[{log.timestamp}]</TableCell>
                <TableCell>
                  <Box className="flex items-center gap-2">
                    <User size={12} className="text-slate-400" />
                    <span className="font-bold text-slate-700">{log.usuario}</span>
                  </Box>
                </TableCell>
                <TableCell>
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                    log.accion.includes('INGRESO') ? 'bg-emerald-100 text-emerald-800' : 
                    log.accion.includes('CAMBIO') ? 'bg-amber-100 text-amber-800' : 'bg-blue-100 text-blue-800'
                  }`}>
                    {log.accion}
                  </span>
                </TableCell>
                <TableCell className="text-slate-600 italic">
                  | {log.detalle}
                </TableCell>
                <TableCell align="right" className="text-slate-400">
                  <Box className="flex items-center justify-end gap-1">
                    <Globe size={12} />
                    {log.ip}
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      
      <Box className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4">
        <Paper elevation={0} className="p-4 border border-emerald-200 bg-emerald-50 rounded-xl flex items-start gap-3">
          <ShieldCheck className="text-emerald-600 mt-1" size={20} />
          <Box>
            <Typography variant="body2" className="font-bold text-emerald-900">Nivel de Integridad: 100%</Typography>
            <Typography variant="caption" className="text-emerald-700 block">Todos los bloques de auditoría han sido verificados mediante Hashing SHA-256 encadenado.</Typography>
          </Box>
        </Paper>
        
        <Box className="col-span-2 flex items-center justify-end">
          <Typography variant="caption" className="text-slate-400 max-w-xs text-right italic">
            "Este registro constituye prueba pericial de la cadena de custodia y no puede ser modificado por ningún usuario del sistema."
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};
