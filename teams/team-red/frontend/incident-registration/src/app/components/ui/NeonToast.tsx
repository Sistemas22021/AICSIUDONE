import { useState, useCallback } from 'react'
import { CheckCircle, XCircle, X } from 'lucide-react'

// ─── Tipos ────────────────────────────────────────────────────────────────────

type ToastVariant = 'success' | 'error'

interface ToastState {
    id: number
    message: string
    variant: ToastVariant
    visible: boolean
}

// ─── Hook ─────────────────────────────────────────────────────────────────────

export function useNeonToast(duration = 4000) {
    const [toasts, setToasts] = useState<ToastState[]>([])

    const showToast = useCallback((message: string, variant: ToastVariant = 'success') => {
        const id = Date.now()

        // Agrega el toast con visible=false para que el CSS lo anime al entrar
        setToasts(prev => [...prev, { id, message, variant, visible: false }])

        // Un tick después lo ponemos visible para disparar la transición de entrada
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                setToasts(prev =>
                    prev.map(t => (t.id === id ? { ...t, visible: true } : t)),
                )
            })
        })

        // Inicia el fade-out antes de removerlo
        setTimeout(() => {
            setToasts(prev =>
                prev.map(t => (t.id === id ? { ...t, visible: false } : t)),
            )
            setTimeout(() => {
                setToasts(prev => prev.filter(t => t.id !== id))
            }, 350)
        }, duration)
    }, [duration])

    const dismiss = useCallback((id: number) => {
        setToasts(prev => prev.map(t => (t.id === id ? { ...t, visible: false } : t)))
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id))
        }, 350)
    }, [])

    const ToastContainer = useCallback(() => (
        <div
            aria-live="polite"
            className="fixed bottom-6 right-6 z-50 flex flex-col gap-2 pointer-events-none"
        >
            {toasts.map(toast => (
                <NeonToastItem
                    key={toast.id}
                    toast={toast}
                    onDismiss={() => dismiss(toast.id)}
                />
            ))}
        </div>
    ), [toasts, dismiss])

    return { showToast, ToastContainer }
}

// ─── Item individual ──────────────────────────────────────────────────────────

interface NeonToastItemProps {
    toast: ToastState
    onDismiss: () => void
}

function NeonToastItem({ toast, onDismiss }: NeonToastItemProps) {
    const isSuccess = toast.variant === 'success'

    const colors = isSuccess
        ? {
            border:  'border-emerald-400/50',
            bg:      'bg-emerald-400/8',
            glow:    '0 4px 24px rgba(0,255,136,0.18), 0 1px 8px rgba(0,255,136,0.10)',
            icon:    'text-emerald-400',
            iconGlow:'drop-shadow(0 0 6px rgba(0,255,136,0.7))',
            text:    'text-emerald-100',
            dismiss: 'text-emerald-600 hover:text-emerald-300 hover:bg-emerald-400/10',
            bar:     'bg-emerald-400',
            barGlow: '0 0 8px rgba(0,255,136,0.6)',
        }
        : {
            border:  'border-red-400/50',
            bg:      'bg-red-400/8',
            glow:    '0 4px 24px rgba(255,75,75,0.18), 0 1px 8px rgba(255,75,75,0.10)',
            icon:    'text-red-400',
            iconGlow:'drop-shadow(0 0 6px rgba(255,75,75,0.7))',
            text:    'text-red-100',
            dismiss: 'text-red-600 hover:text-red-300 hover:bg-red-400/10',
            bar:     'bg-red-400',
            barGlow: '0 0 8px rgba(255,75,75,0.6)',
        }

    return (
        <div
            className="pointer-events-auto"
            style={{
                opacity:    toast.visible ? 1 : 0,
                transform:  toast.visible ? 'translateY(0) scale(1)' : 'translateY(12px) scale(0.97)',
                transition: 'opacity 300ms ease, transform 300ms ease',
            }}
        >
            <div
                className={[
                    'relative flex items-start gap-3 min-w-[280px] max-w-[360px]',
                    'px-4 py-3 rounded-lg border backdrop-blur-sm overflow-hidden',
                    'bg-[#040D1A]',
                    colors.border,
                    colors.bg,
                ].join(' ')}
                style={{ boxShadow: colors.glow }}
            >
                {/* Barra lateral de color */}
                <div
                    className={['absolute left-0 top-0 bottom-0 w-[3px] rounded-l-lg', colors.bar].join(' ')}
                    style={{ boxShadow: colors.barGlow }}
                />

                {/* Ícono */}
                <div className="mt-0.5 shrink-0">
                    {isSuccess
                        ? <CheckCircle size={16} className={colors.icon} style={{ filter: colors.iconGlow }} />
                        : <XCircle    size={16} className={colors.icon} style={{ filter: colors.iconGlow }} />
                    }
                </div>

                {/* Mensaje */}
                <p className={['flex-1 text-[12px] font-medium leading-snug', colors.text].join(' ')}>
                    {toast.message}
                </p>

                {/* Botón cerrar */}
                <button
                    onClick={onDismiss}
                    className={['ml-1 p-0.5 rounded transition-colors', colors.dismiss].join(' ')}
                >
                    <X size={13} />
                </button>
            </div>
        </div>
    )
}