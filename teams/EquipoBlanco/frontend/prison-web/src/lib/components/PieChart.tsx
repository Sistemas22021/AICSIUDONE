import React from 'react';

export interface PieChartProps {
  /** Cantidad exacta de espacios a pintar. El componente lo limitará internamente entre 0 y 8. */
  value: number;
  /** Color del relleno de los segmentos. */
  sliceColor?: string;
  /** Color del borde exterior del círculo (por defecto "black"). */
  borderColor?: string;
  /** Clases utilitarias (Tailwind) para definir el tamaño y posicionamiento. */
  className?: string;
}

export const PieChart: React.FC<PieChartProps> = ({
  value,
  sliceColor = '#22c55e',
  borderColor = 'black',
  className = 'w-24 h-24',
}) => {
  // Clamp value strictly between 0 and 8
  const clampedValue = Math.max(0, Math.min(8, value));

  // Generate slice array
  const slices = Array.from({ length: clampedValue }, (_, i) => i);

  return (
    <svg
      viewBox="0 0 100 100"
      className={`${className} overflow-visible`}
      xmlns="http://www.w3.org/2000/svg"
    >
      {/* 1. Fondo blanco sólido */}
      <circle cx="50" cy="50" r="48.5" fill="#ffffff" />

      {/* 2. Segmentos coloreados */}
      {slices.map((index) => (
        <path
          key={index}
          d="M 50 50 L 50 0 A 50 50 0 0 1 85.3553 14.6447 Z"
          fill={sliceColor}
          transform={`rotate(${index * 45} 50 50)`}
        />
      ))}

      {/* 3. Grupo de 4 líneas cruzadas para divisiones internas */}
      <g stroke="black" strokeWidth="1" strokeOpacity="0.4">
        {/* Vertical */}
        <line x1="50" y1="0" x2="50" y2="100" />
        {/* Horizontal */}
        <line x1="0" y1="50" x2="100" y2="50" />
        {/* Diagonal 1 */}
        <line x1="14.6447" y1="14.6447" x2="85.3553" y2="85.3553" />
        {/* Diagonal 2 */}
        <line x1="14.6447" y1="85.3553" x2="85.3553" y2="14.6447" />
      </g>

      {/* 4. Borde exterior negro de 3px */}
      <circle
        cx="50"
        cy="50"
        r="48.5"
        fill="none"
        stroke={borderColor}
        strokeWidth="3"
      />
    </svg>
  );
};
