import { Typography } from '@mui/material';

interface Props {
  count: number;
}

export const TrendIndicator = ({ count }: Props) => {
  if (count <= 1) {
    return (
      <Typography variant="body2" className="text-slate-400 font-medium">
        Única
      </Typography>
    );
  }

  return (
    <Typography variant="body2" className="text-slate-600 font-medium">
      {count} {count === 1 ? 'vinculación' : 'vinculaciones'}
    </Typography>
  );
};
