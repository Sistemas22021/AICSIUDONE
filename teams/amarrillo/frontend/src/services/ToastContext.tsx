import { createContext, useContext, useState, useCallback, ReactNode } from 'react';

type TipoToast = 'exito' | 'error' | 'info';

interface Toast {
  id: number;
  tipo: TipoToast;
  mensaje: string;
}

interface ToastCtx {
  exito: (mensaje: string) => void;
  error: (mensaje: string) => void;
  info: (mensaje: string) => void;
}

const Ctx = createContext<ToastCtx | null>(null);

const ICONO: Record<TipoToast, string> = {
  exito: 'check_circle',
  error: 'error',
  info: 'info',
};

const COLOR: Record<TipoToast, string> = {
  exito: 'var(--green-600, #16A34A)',
  error: 'var(--red-600, #DC2626)',
  info: 'var(--slate-600, #475569)',
};

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const quitar = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const mostrar = useCallback((tipo: TipoToast, mensaje: string) => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, tipo, mensaje }]);
    setTimeout(() => quitar(id), 4000);
  }, [quitar]);

  const api: ToastCtx = {
    exito: (m) => mostrar('exito', m),
    error: (m) => mostrar('error', m),
    info: (m) => mostrar('info', m),
  };

  return (
    <Ctx.Provider value={api}>
      {children}
      <div style={{
        position: 'fixed', top: 20, right: 20, zIndex: 12000,
        display: 'flex', flexDirection: 'column', gap: 10, maxWidth: 360,
      }}>
        {toasts.map((t) => (
          <div key={t.id}
            onClick={() => quitar(t.id)}
            style={{
              display: 'flex', alignItems: 'center', gap: 10,
              padding: '12px 16px',
              background: 'var(--slate-900)',
              border: `1px solid ${COLOR[t.tipo]}`,
              borderLeft: `3px solid ${COLOR[t.tipo]}`,
              color: 'white', fontSize: 13, cursor: 'pointer',
              boxShadow: '0 4px 20px rgba(0,0,0,0.4)',
              animation: 'toastIn 0.2s ease-out',
            }}>
            <span className="material-symbols-outlined" style={{ fontSize: 20, color: COLOR[t.tipo] }}>
              {ICONO[t.tipo]}
            </span>
            <span style={{ flex: 1, lineHeight: 1.4 }}>{t.mensaje}</span>
          </div>
        ))}
      </div>
    </Ctx.Provider>
  );
}

export function useToast(): ToastCtx {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error('useToast debe usarse dentro de ToastProvider');
  return ctx;
}