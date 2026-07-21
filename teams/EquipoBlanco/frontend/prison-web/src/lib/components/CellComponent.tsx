import React from 'react';
import { PieChart } from './PieChart';

export interface CellComponentProps {
  /** Cantidad real de internos en la celda (sin límite máximo). */
  value: number;
  /** Clases utilitarias (Tailwind) para inyectar en el contenedor. */
  className?: string;
}

export const CellComponent: React.FC<CellComponentProps> = ({
  value,
  className = 'w-24 h-24',
}) => {
  // Determine color based on occupancy rules
  let sliceColor = '#ffffff'; // Vacío (<= 0)

  if (value > 0 && value <= 3) {
    sliceColor = '#22c55e'; // Baja Ocupación (Verde)
  } else if (value >= 4 && value <= 6) {
    sliceColor = '#eab308'; // Ocupación Media (Amarillo)
  } else if (value >= 7 && value <= 8) {
    sliceColor = '#ef4444'; // Alta Ocupación / Lleno (Rojo)
  } else if (value > 8) {
    sliceColor = '#a855f7'; // Sobrepoblación (Morado)
  }

  return (
    <div className="flex flex-col items-center justify-center p-4 bg-white rounded-xl shadow-md border border-slate-100 hover:shadow-lg transition-shadow duration-300">
      <PieChart
        value={value}
        sliceColor={sliceColor}
        borderColor="black"
        className={className}
      />
      <div className="mt-3 text-center">
        <span className="block text-sm font-semibold text-slate-800">
          Ocupación: {value}
        </span>
        <span className={`inline-block mt-1 px-2 py-0.5 text-xs font-medium rounded-full ${
          value <= 0
            ? 'bg-slate-100 text-slate-600'
            : value <= 3
            ? 'bg-green-50 text-green-700 border border-green-200'
            : value <= 6
            ? 'bg-yellow-50 text-yellow-700 border border-yellow-200'
            : value <= 8
            ? 'bg-red-50 text-red-700 border border-red-200'
            : 'bg-purple-50 text-purple-700 border border-purple-200'
        }`}>
          {value <= 0
            ? 'Vacío'
            : value <= 3
            ? 'Baja Ocupación'
            : value <= 6
            ? 'Ocupación Media'
            : value <= 8
            ? 'Alta Ocupación'
            : 'Sobrepoblación'}
        </span>
      </div>
    </div>
  );
};
