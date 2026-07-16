import Modal from './Modal';
import type { ReactNode } from 'react';

interface CampoDetalle {
  etiqueta: string;
  valor: string | ReactNode;
  destacado?: boolean;
  mono?: boolean;
}

interface Props {
  abierto: boolean;
  onClose: () => void;
  titulo: string;
  subtitulo?: string;
  icono?: string;
  avatar?: ReactNode;
  campos: CampoDetalle[];
  acciones?: ReactNode;
  extra?: ReactNode;
}

export default function ModalDetalle({
  abierto,
  onClose,
  titulo,
  subtitulo,
  icono = 'info',
  avatar,
  campos,
  acciones,
  extra,
}: Props) {
  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Ficha de entidad" icono={icono} ancho={620}>
      <div className="dossier" style={{ marginBottom: 16 }}>
        <div className="dossier-header">
          {avatar || (
            <div className="dossier-avatar" style={{ background: 'var(--red-600)' }}>
              <span className="material-symbols-outlined">{icono}</span>
            </div>
          )}
          <div>
            <div className="dossier-name">{titulo}</div>
            {subtitulo && <div className="dossier-type">{subtitulo}</div>}
          </div>
        </div>
        <div className="dossier-fields">
          {campos.map((c, i) => (
            <div className="dossier-field" key={i}>
              <span>{c.etiqueta}</span>
              <strong
                style={{
                  fontFamily: c.mono ? 'var(--font-mono)' : 'var(--font-body)',
                  color: c.destacado ? 'var(--red-500)' : 'white',
                }}
              >
                {c.valor || '—'}
              </strong>
            </div>
          ))}
        </div>
      </div>

      {extra}

      {acciones && (
        <div
          style={{
            display: 'flex',
            gap: 8,
            justifyContent: 'flex-end',
            marginTop: 20,
            paddingTop: 16,
            borderTop: '1px solid var(--slate-800)',
          }}
        >
          {acciones}
        </div>
      )}
    </Modal>
  );
}