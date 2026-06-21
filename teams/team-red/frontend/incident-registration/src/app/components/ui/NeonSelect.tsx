import { forwardRef, type SelectHTMLAttributes } from 'react'
import { ChevronDown } from 'lucide-react'

export interface NeonSelectOption {
  value: string
  label: string
}

export interface NeonSelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  required?: boolean
  error?: boolean
  options: NeonSelectOption[]
}

export const NeonSelect = forwardRef<HTMLSelectElement, NeonSelectProps>(
  ({ label, required, error, options, className = '', ...props }, ref) => (
    <div className="flex flex-col gap-1.5 w-full">
      {label && (
        <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
          {label} {required && <span className="text-red-400">*</span>}
        </label>
      )}
      <div className="relative">
        <select
          ref={ref}
          className={`
            w-full px-3 py-2.5
            bg-[#080D13] text-cyan-300
            border ${error ? 'border-red-400' : 'border-cyan-400/40'}
            rounded focus:border-cyan-400 focus:outline-none
            appearance-none cursor-pointer
            transition-all duration-300
            hover:border-cyan-400/60
            disabled:opacity-40 disabled:cursor-not-allowed
            input-glow
            ${error ? 'error-glow' : ''}
            ${className}
          `}
          style={
            error
              ? undefined
              : { boxShadow: '0 2px 8px rgba(51,153,255,0.15), inset 0 1px 3px rgba(51,153,255,0.08)' }
          }
          {...props}
        >
          {options.map((opt) => (
            <option
              key={opt.value}
              value={opt.value}
              className="bg-[#060B10] text-cyan-300"
            >
              {opt.label}
            </option>
          ))}
        </select>
        <ChevronDown
          size={14}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-cyan-400 pointer-events-none"
          style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.6))' }}
        />
      </div>
    </div>
  )
)

NeonSelect.displayName = 'NeonSelect'
