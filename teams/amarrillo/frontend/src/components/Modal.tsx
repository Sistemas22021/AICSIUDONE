import { ReactNode, useEffect } from 'react';

interface Props {
  abierto: boolean;
  onClose: () => void;
  titulo: string;
  icono?: string;
  children: ReactNode;
  footer?: ReactNode;
  ancho?: number;
}

export default function Modal({ abierto, onClose, titulo, icono, children, footer, ancho }: Props) {
  useEffect(() => {
    if (!abierto) return;
    const h = (e: KeyboardEvent) => e.key === 'Escape' && onClose();
    document.addEventListener('keydown', h);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', h);
      document.body.style.overflow = '';
    };
  }, [abierto, onClose]);

  if (!abierto) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" style={ancho ? { maxWidth: ancho } : undefined} onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-title">
            {icono && <span className="material-symbols-outlined">{icono}</span>}
            <h2>{titulo}</h2>
          </div>
          <button className="modal-close" onClick={onClose} title="Cerrar (Esc)">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>
  );
}