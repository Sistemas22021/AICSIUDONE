import { forwardRef, type InputHTMLAttributes } from 'react'

export interface NeonInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  required?: boolean
  error?: boolean
}

export const NeonInput = forwardRef<HTMLInputElement, NeonInputProps>(
  ({ label, required, error, className = '', ...props }, ref) => (
    <div className="flex flex-col gap-1.5 w-full">
      {label && (
        <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
          {label} {required && <span className="text-red-400">*</span>}
        </label>
      )}
      <input
        ref={ref}
        className={`
          w-full px-3 py-2.5 bg-transparent text-cyan-300
          border ${error ? 'border-red-400' : 'border-cyan-400/40'}
          rounded focus:border-cyan-400 focus:outline-none
          placeholder:text-cyan-900/40 placeholder:text-sm
          transition-all duration-300
          hover:border-cyan-400/60
          input-glow
          [color-scheme:dark]
          ${error ? 'error-glow' : ''}
          ${className}
        `}
        style={
          error
            ? undefined
            : { boxShadow: '0 2px 8px rgba(51,153,255,0.15), inset 0 1px 3px rgba(51,153,255,0.08)' }
        }
        {...props}
      />
    </div>
  )
)

NeonInput.displayName = 'NeonInput'
