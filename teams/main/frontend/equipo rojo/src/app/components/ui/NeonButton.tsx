import { forwardRef, type ButtonHTMLAttributes, type ReactNode } from 'react'

export interface NeonButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'outline' | 'ghost' | 'success' | 'danger'
  icon?: ReactNode
}

const variantStyles: Record<NonNullable<NeonButtonProps['variant']>, string> = {
  primary: 'bg-cyan-400 border-cyan-400 text-black font-semibold hover:bg-cyan-300 hover:shadow-[0_0_20px_rgba(0,204,255,0.6),0_0_40px_rgba(0,204,255,0.3)]',
  outline: 'bg-transparent border-cyan-400/60 text-cyan-300 hover:border-cyan-400 hover:bg-cyan-400/10 hover:shadow-[0_0_15px_rgba(0,204,255,0.4)]',
  ghost:   'bg-transparent border-transparent text-cyan-400 hover:bg-cyan-400/10',
  success: 'bg-emerald-400 border-emerald-400 text-black font-semibold hover:bg-emerald-300 hover:shadow-[0_0_20px_rgba(0,255,136,0.6)]',
  danger:  'bg-[#FF4B4B] border-[#FF4B4B] text-black font-semibold hover:bg-[#FF6B6B] hover:shadow-[0_0_20px_rgba(255,75,75,0.6)]',
}

export const NeonButton = forwardRef<HTMLButtonElement, NeonButtonProps>(
  ({ children, variant = 'primary', icon, className = '', ...props }, ref) => (
    <button
      ref={ref}
      className={`
        px-4 py-2 border rounded
        uppercase text-xs tracking-[0.1em]
        transition-all duration-300
        flex items-center justify-center gap-2
        disabled:opacity-50 disabled:cursor-not-allowed
        ${variantStyles[variant]}
        ${className}
      `}
      {...props}
    >
      {icon && <span>{icon}</span>}
      {children}
    </button>
  )
)

NeonButton.displayName = 'NeonButton'
