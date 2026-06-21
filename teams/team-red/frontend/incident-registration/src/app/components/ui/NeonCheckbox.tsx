import { type InputHTMLAttributes } from 'react'
import { Check } from 'lucide-react'

export interface NeonCheckboxProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

export const NeonCheckbox = ({ label, checked, className = '', ...props }: NeonCheckboxProps) => (
  <label className="flex items-center gap-3 cursor-pointer group select-none">
    <div className="relative flex-shrink-0">
      <input type="checkbox" className="sr-only" checked={checked} {...props} />
      <div
        className={`
          w-4 h-4 border-2 rounded-sm
          flex items-center justify-center
          transition-all duration-300
          ${checked ? 'border-cyan-400 bg-cyan-400/15' : 'border-cyan-400/40 group-hover:border-cyan-400/70'}
        `}
        style={
          checked
            ? { boxShadow: '0 0 8px rgba(51,153,255,0.5), inset 0 0 4px rgba(51,153,255,0.2)' }
            : undefined
        }
      >
        {checked && (
          <Check
            size={10}
            className="text-cyan-400"
            style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.9))' }}
          />
        )}
      </div>
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
