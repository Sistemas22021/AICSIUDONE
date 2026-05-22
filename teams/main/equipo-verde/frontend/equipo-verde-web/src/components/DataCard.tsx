import { Paper, Box } from '@mui/material';

interface DataCardProps {
  children: React.ReactNode;
  className?: string;
}

export const DataCard = ({ children, className = '' }: DataCardProps) => (
  <Paper
    elevation={0}
    className={`bg-white border border-slate-200 rounded-xl overflow-hidden shadow-sm ${className}`}
  >
    <Box className="p-6">
      {children}
    </Box>
  </Paper>
);
