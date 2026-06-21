import { type HTMLAttributes } from 'react'

export interface NeonPanelProps extends HTMLAttributes<HTMLDivElement> {
  title?: string
  subtitle?: string
  noBorder?: boolean
}

export const NeonPanel = ({
  title,
  subtitle,
  children,
  className = '',
  noBorder = false,
  ...props
}: NeonPanelProps) => (
  <div
    className={`
      ${noBorder ? '' : 'border-2 border-cyan-400/50'}
      bg-[#04101E]/60 backdrop-blur-sm rounded p-5
      ${className}
    `}
    style={
      noBorder
        ? undefined
        : {
            boxShadow:
              '0 2px 8px rgba(51,153,255,0.25), 0 4px 16px rgba(51,153,255,0.15), inset 0 1px 3px rgba(51,153,255,0.08), inset 0 -1px 3px rgba(51,153,255,0.05)',
          }
    }
    {...props}
  >
    {title && (
      <div className="mb-5">
        <h3
          className="text-sm uppercase tracking-[0.15em] text-cyan-300 font-semibold"
          style={{ textShadow: '0 0 10px rgba(51,153,255,0.7), 0 0 20px rgba(51,153,255,0.4)' }}
        >
          {title}
        </h3>
        {subtitle && <p className="text-[10px] text-cyan-500/70 mt-1.5">{subtitle}</p>}
      </div>
    )}
    {children}
  </div>
)
