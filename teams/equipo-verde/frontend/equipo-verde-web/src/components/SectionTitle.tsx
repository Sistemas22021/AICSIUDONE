import { Typography, type TypographyProps } from '@mui/material';

interface SectionTitleProps extends TypographyProps {
  children: React.ReactNode;
}

export const SectionTitle = ({ children, className = '', ...props }: SectionTitleProps) => (
  <Typography
    variant="h5"
    className={`font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-slate-900 to-slate-600 tracking-tight ${className}`}
    sx={{ fontWeight: 800, mb: 3 }}
    {...props}
  >
    {children}
  </Typography>
);
