import { Paper, Box } from '@mui/material';

interface DataCardProps {
  children: React.ReactNode;
  className?: string;
  noPadding?: boolean;
}

export const DataCard = ({ children, className = '', noPadding = false }: DataCardProps) => (
  <Paper
    elevation={0}
    className={`bg-white border border-slate-100 rounded-[1.5rem] overflow-hidden shadow-[0_8px_30px_rgb(0,0,0,0.04)] transition-all duration-300 hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)] ${className}`}
  >
    <Box className={noPadding ? '' : 'p-8'}>
      {children}
    </Box>
  </Paper>
);
