import { Box } from '@mui/material';
import type { MatchConfidence } from '../../types/balistica';

interface Props {
  confidence: MatchConfidence;
  score: number;
}

export const ConfidenceBadge = ({ confidence, score }: Props) => {
  const getConfig = () => {
    switch (confidence) {
      case 'Alta':
        return {
          color: 'text-red-700',
          bg: 'bg-red-50',
          label: 'Riesgo Alto'
        };
      case 'Media':
        return {
          color: 'text-amber-700',
          bg: 'bg-amber-50',
          label: 'Riesgo Medio'
        };
      case 'Baja':
        return {
          color: 'text-slate-700',
          bg: 'bg-slate-50',
          label: 'Riesgo Bajo'
        };
      default:
        return {
          color: 'text-slate-700',
          bg: 'bg-slate-50',
          label: 'Riesgo Desconocido'
        };
    }
  };

  const config = getConfig();

  return (
    <Box className={`inline-flex items-center px-3 py-1 rounded-lg font-bold text-xs whitespace-nowrap ${config.bg} ${config.color}`}>
      {confidence} ({score.toFixed(1)}%)
    </Box>
  );
};
