/// <reference types="vite/client" />

declare module '@cell-component/PieChart' {
  import type { FC } from 'react'
  interface PieChartProps {
    value: number
    sliceColor?: string
    borderColor?: string
    className?: string
  }
  export const PieChart: FC<PieChartProps>
}

declare module '@cell-component/CellComponent' {
  import type { FC } from 'react'
  interface CellComponentProps {
    value: number
    className?: string
  }
  export const CellComponent: FC<CellComponentProps>
}
