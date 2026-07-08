import { forwardRef, useState, type TextareaHTMLAttributes } from 'react'

export interface NeonTextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  required?: boolean
  showCount?: boolean
  maxCount?: number
}

export const NeonTextarea = forwardRef<HTMLTextAreaElement, NeonTextareaProps>(
  ({ label, required, showCount, maxCount, className = '', value, onChange, ...props }, ref) => {
    const [count, setCount] = useState(value ? String(value).length : 0)

    return (
      <div className="flex flex-col gap-1.5 w-full">
        {label && (
          <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
            {label} {required && <span className="text-red-400">*</span>}
          </label>
        )}
        <textarea
          ref={ref}
          value={value}
          onChange={(e) => {
            setCount(e.target.value.length)
            onChange?.(e)
          }}
          className={`
            w-full px-3 py-2.5 bg-transparent text-cyan-300
            border border-cyan-400/40 rounded
            focus:border-cyan-400 focus:outline-none
            placeholder:text-cyan-900/40 placeholder:text-sm
            transition-all duration-300
            hover:border-cyan-400/60
            resize-none input-glow
            ${className}
          `}
          style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.15), inset 0 1px 3px rgba(51,153,255,0.08)' }}
          {...props}
        />
        {showCount && (
          <div className="text-[10px] text-cyan-500/70 text-right tracking-wider">
            {count}/{maxCount ?? '∞'}
          </div>
        )}
      </div>
    )
  }
)

NeonTextarea.displayName = 'NeonTextarea'
