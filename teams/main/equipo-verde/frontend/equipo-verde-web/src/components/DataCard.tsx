import { Paper, Box } from '@mui/material';

interface DataCardProps {
  children: React.ReactNode;
  className?: string;
  noPadding?: boolean;
}

export const DataCard = ({ children, className = '', noPadding = false }: DataCardProps) => (
  <Paper
    elevation={0}
    className={`bg-white border border-slate-200 rounded-xl overflow-hidden shadow-sm ${className}`}
  >
    <Box className={noPadding ? "" : "p-6"}>
      {children}
    </Box>
  </Paper>
);
