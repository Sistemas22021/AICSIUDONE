import { type InputHTMLAttributes } from 'react'

export interface NeonRadioProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

export const NeonRadio = ({ label, checked, className = '', ...props }: NeonRadioProps) => (
  <label className="flex items-center gap-3 cursor-pointer group select-none">
    <div className="relative flex-shrink-0">
      <input type="radio" className="sr-only" checked={checked} {...props} />
      {/* Ring exterior */}
      <div
        className={`
          w-4 h-4 rounded-full border-2 transition-all duration-300
          ${checked ? 'border-cyan-400 bg-cyan-400/15' : 'border-cyan-400/40 group-hover:border-cyan-400/70'}
        `}
        style={
          checked
            ? { boxShadow: '0 0 8px rgba(51,153,255,0.5), inset 0 0 4px rgba(51,153,255,0.2)' }
            : undefined
        }
      />
      {/* Dot interior */}
      {checked && (
        <div
          className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1.5 h-1.5 rounded-full bg-cyan-400"
          style={{ boxShadow: '0 0 6px rgba(51,153,255,0.9)' }}
        />
      )}
    </div>
    <span
      className={`
        text-xs uppercase tracking-[0.1em] transition-colors duration-300
        ${checked ? 'text-cyan-300' : 'text-cyan-500 group-hover:text-cyan-400'}
      `}
    >
      {label}
    </span>
  </label>
)
