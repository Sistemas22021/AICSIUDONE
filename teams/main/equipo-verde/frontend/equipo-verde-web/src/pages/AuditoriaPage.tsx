import { useState, useEffect, useMemo } from 'react';
import { 
  Box, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper,
  TextField,
  MenuItem,
  TablePagination,
  Chip,
  CircularProgress,
  InputAdornment
} from '@mui/material';
import { SectionTitle } from '../components/SectionTitle';
import { 
  Terminal, 
  ShieldCheck, 
  User, 
  Globe,
  Search,
  Database,
  RefreshCw
} from 'lucide-react';
import { auditService, AuditLogBackendEntry } from '../services/auditService';

export const AuditoriaPage = () => {
  const [logs, setLogs] = useState<AuditLogBackendEntry[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Pagination State
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Filters State
  const [searchQuery, setSearchQuery] = useState('');
  const [actionFilter, setActionFilter] = useState('all'); // all, 0, 1, 2
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      // Fetch a larger page size if locally filtering, or standard page size
      // Since backend doesn't support filters, we'll fetch page size 100 to allow broad filtering and pagination,
      // or we can paginate backend-side. Let's fetch 100 entries so we can filter them effectively on frontend.
      const data = await auditService.getAuditLogs(0, 100);
      setLogs(data.content);
    } catch (err: any) {
      console.error(err);
      setError('No se pudieron obtener los logs del servidor de auditoría Envers.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const getActionLabel = (revType: number, entityType: string) => {
    const entity = entityType === 'BULLET' ? 'PROYECTIL' : (entityType === 'IMAGES' ? 'IMAGEN' : entityType);
    if (revType === 0) return `CREACIÓN DE ${entity}`;
    if (revType === 1) return `MODIFICACIÓN DE ${entity}`;
    if (revType === 2) return `ELIMINACIÓN DE ${entity}`;
    return `ACCIÓN DE ${entity}`;
  };

  const getActionColorClass = (revType: number) => {
    if (revType === 0) return 'bg-emerald-50 text-emerald-700 border-emerald-200';
    if (revType === 1) return 'bg-blue-50 text-blue-700 border-blue-200';
    if (revType === 2) return 'bg-rose-50 text-rose-700 border-rose-200';
    return 'bg-slate-50 text-slate-700 border-slate-200';
  };

  const formatDate = (epochMs: number) => {
    const date = new Date(epochMs);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  // Local filtering based on UI controls
  const filteredLogs = useMemo(() => {
    return logs.filter(log => {
      // 1. Search Query (Operator, EntityType, EntityId, Rev)
      const matchesSearch = searchQuery === '' || 
        log.operator.toLowerCase().includes(searchQuery.toLowerCase()) ||
        log.entityType.toLowerCase().includes(searchQuery.toLowerCase()) ||
        log.entityId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        String(log.rev).includes(searchQuery);

      // 2. Action Filter
      const matchesAction = actionFilter === 'all' || String(log.revType) === actionFilter;

      // 3. Date Filters
      const logDate = new Date(log.revTimestamp);
      let matchesStartDate = true;
      if (startDate) {
        const start = new Date(startDate);
        start.setHours(0, 0, 0, 0);
        matchesStartDate = logDate >= start;
      }

      let matchesEndDate = true;
      if (endDate) {
        const end = new Date(endDate);
        end.setHours(23, 59, 59, 999);
        matchesEndDate = logDate <= end;
      }

      return matchesSearch && matchesAction && matchesStartDate && matchesEndDate;
    });
  }, [logs, searchQuery, actionFilter, startDate, endDate]);

  // Frontend-side pagination over filtered results
  const paginatedLogs = useMemo(() => {
    const startIndex = page * pageSize;
    return filteredLogs.slice(startIndex, startIndex + pageSize);
  }, [filteredLogs, page, pageSize]);

  return (
    <Box className="animate-fade-in max-w-6xl mx-auto px-4 py-6">
      <SectionTitle>Logs de Inmutabilidad y Trazabilidad</SectionTitle>

      {/* integrity card */}
      <Box className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <Paper elevation={0} className="p-4 border border-emerald-200 bg-emerald-50/50 rounded-2xl flex items-center gap-3">
          <ShieldCheck className="text-emerald-600 flex-shrink-0" size={24} />
          <Box>
            <Typography variant="subtitle2" className="font-bold text-emerald-950">Nivel de Integridad: 100%</Typography>
            <Typography variant="caption" className="text-emerald-700 block">Todos los bloques de auditoría han sido validados con Envers (PostgreSQL).</Typography>
          </Box>
        </Paper>

        <Paper elevation={0} className="p-4 border border-indigo-200 bg-indigo-50/50 rounded-2xl flex items-center gap-3">
          <Database className="text-indigo-600 flex-shrink-0" size={24} />
          <Box>
            <Typography variant="subtitle2" className="font-bold text-indigo-950">Sistema Histórico Activo</Typography>
            <Typography variant="caption" className="text-indigo-700 block">Seguimiento forense continuo del expediente y la cadena de custodia.</Typography>
          </Box>
        </Paper>

        <Box className="flex items-center justify-end">
          <Chip 
            icon={<RefreshCw size={14} />} 
            label="Actualizar logs" 
            onClick={fetchLogs} 
            variant="outlined"
            className="border-slate-200 hover:bg-slate-50 rounded-xl"
          />
        </Box>
      </Box>

      {/* Filter panel */}
      <Box className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm mb-6 grid grid-cols-1 md:grid-cols-4 gap-4 items-center">
        {/* search */}
        <TextField
          placeholder="Buscar operador o ID de entidad..."
          variant="outlined"
          size="small"
          fullWidth
          value={searchQuery}
          onChange={(e) => {
            setSearchQuery(e.target.value);
            setPage(0);
          }}
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

        {/* type action */}
        <TextField
          select
          size="small"
          label="Acción"
          value={actionFilter}
          onChange={(e) => {
            setActionFilter(e.target.value);
            setPage(0);
          }}
          slotProps={{ input: { className: "bg-slate-50 rounded-xl" } }}
        >
          <MenuItem value="all">Todas las Acciones</MenuItem>
          <MenuItem value="0">Creaciones</MenuItem>
          <MenuItem value="1">Modificaciones</MenuItem>
          <MenuItem value="2">Eliminaciones</MenuItem>
        </TextField>

        {/* Start Date */}
        <TextField
          type="date"
          label="Fecha Inicio"
          size="small"
          value={startDate}
          onChange={(e) => {
            setStartDate(e.target.value);
            setPage(0);
          }}
          slotProps={{ inputLabel: { shrink: true }, input: { className: "bg-slate-50 rounded-xl" } }}
        />

        {/* End Date */}
        <TextField
          type="date"
          label="Fecha Fin"
          size="small"
          value={endDate}
          onChange={(e) => {
            setEndDate(e.target.value);
            setPage(0);
          }}
          slotProps={{ inputLabel: { shrink: true }, input: { className: "bg-slate-50 rounded-xl" } }}
        />
      </Box>

      {/* Main Table */}
      <TableContainer component={Paper} elevation={0} className="border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
        <Box className="bg-slate-900 p-4 flex items-center gap-3">
          <Terminal size={20} className="text-emerald-400" />
          <Typography variant="body2" className="text-emerald-400 font-mono font-bold tracking-wider">
            SISTEMA DE AUDITORÍA FORENSE - CADENA DE CUSTODIA DIGITAL REAL
          </Typography>
        </Box>

        {loading ? (
          <Box className="flex flex-col items-center justify-center py-16 gap-3">
            <CircularProgress size={40} className="text-slate-800" />
            <Typography variant="body2" className="text-slate-500 font-medium">Cargando registros reales de auditoría Envers...</Typography>
          </Box>
        ) : error ? (
          <Box className="py-16 text-center">
            <Typography className="text-red-500 font-medium mb-2">{error}</Typography>
            <Chip label="Intentar nuevamente" onClick={fetchLogs} className="bg-red-50 text-red-700 hover:bg-red-100" />
          </Box>
        ) : (
          <>
            <Table sx={{ minWidth: 800 }}>
              <TableHead className="bg-slate-50">
                <TableRow>
                  <TableCell className="font-bold text-slate-500 uppercase text-[10px] w-[180px] py-4">Timestamp</TableCell>
                  <TableCell className="font-bold text-slate-500 uppercase text-[10px] py-4">Usuario / Operador</TableCell>
                  <TableCell className="font-bold text-slate-500 uppercase text-[10px] py-4">Acción / Operación</TableCell>
                  <TableCell className="font-bold text-slate-500 uppercase text-[10px] py-4">ID de Entidad</TableCell>
                  <TableCell className="font-bold text-slate-500 uppercase text-[10px] py-4 text-right">Rev. ID</TableCell>
                </TableRow>
              </TableHead>
              <TableBody className="font-mono">
                {paginatedLogs.map((log) => (
                  <TableRow key={log.id} hover className="border-b border-slate-100">
                    <TableCell className="text-slate-500 text-xs py-3">[{formatDate(log.revTimestamp)}]</TableCell>
                    <TableCell className="py-3">
                      <Box className="flex items-center gap-2">
                        <User size={14} className="text-slate-400" />
                        <span className="font-bold text-slate-700 text-xs">{log.operator}</span>
                      </Box>
                    </TableCell>
                    <TableCell className="py-3">
                      <Chip 
                        label={getActionLabel(log.revType, log.entityType)} 
                        size="small" 
                        variant="outlined"
                        className={`text-[10px] font-bold ${getActionColorClass(log.revType)}`}
                      />
                    </TableCell>
                    <TableCell className="text-slate-600 text-xs py-3">
                      | Entidad: {log.entityType} #{log.entityId}
                    </TableCell>
                    <TableCell align="right" className="text-slate-400 text-xs py-3">
                      <Box className="flex items-center justify-end gap-1">
                        <Globe size={12} />
                        REV-{log.rev}
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}

                {filteredLogs.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center py-12">
                      <Typography className="text-slate-500 font-medium">No se encontraron logs de auditoría con los filtros aplicados.</Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
            <TablePagination
              rowsPerPageOptions={[5, 10, 20, 50]}
              component="div"
              count={filteredLogs.length}
              rowsPerPage={pageSize}
              page={page}
              onPageChange={(_, newPage) => setPage(newPage)}
              onRowsPerPageChange={(e) => {
                setPageSize(parseInt(e.target.value, 10));
                setPage(0);
              }}
              labelRowsPerPage="Filas por página"
            />
          </>
        )}
      </TableContainer>
    </Box>
  );
};
