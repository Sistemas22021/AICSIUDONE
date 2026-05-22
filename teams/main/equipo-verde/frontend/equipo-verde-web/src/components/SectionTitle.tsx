import { Typography, type TypographyProps } from '@mui/material';

interface SectionTitleProps extends TypographyProps {
  children: React.ReactNode;
}

export const SectionTitle = ({ children, className = '', ...props }: SectionTitleProps) => (
  <Typography
    variant="h4"
    className={`font-extrabold text-slate-900 mb-6 ${className}`}
    sx={{ fontWeight: 800 }}
    {...props}
  >
    {children}
  </Typography>
);
