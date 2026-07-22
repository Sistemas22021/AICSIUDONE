import Modal from './Modal';

interface Props {
  abierto: boolean;
  titulo: string;
  mensaje: string;
  onConfirmar: () => void;
  onCancelar: () => void;
  textoConfirmar?: string;
  peligro?: boolean;
}

export default function ModalConfirmar({
  abierto,
  titulo,
  mensaje,
  onConfirmar,
  onCancelar,
  textoConfirmar = 'Confirmar',
  peligro = false,
}: Props) {
  return (
    <Modal abierto={abierto} onClose={onCancelar} titulo={titulo} icono="help_outline" ancho={440}>
      <p style={{ color: 'var(--slate-300)', fontSize: 14, lineHeight: 1.6, margin: 0 }}>
        {mensaje}
      </p>

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
        <button className="btn-ghost" onClick={onCancelar}>
          Cancelar
        </button>
        <button
          className="btn-primary"
          onClick={onConfirmar}
          style={peligro ? { background: 'var(--red-600)' } : undefined}
        >
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
            {peligro ? 'delete_forever' : 'check'}
          </span>
          {textoConfirmar}
        </button>
      </div>
    </Modal>
  );
}