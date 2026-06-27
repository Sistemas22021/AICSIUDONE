import { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import ModalConfirmar from '../components/ModalConfirmar';

interface OpcionesConfirm {
  titulo: string;
  mensaje: string;
  textoConfirmar?: string;
  peligro?: boolean;
}

type ConfirmFn = (opciones: OpcionesConfirm) => Promise<boolean>;

const Ctx = createContext<ConfirmFn | null>(null);

export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [opciones, setOpciones] = useState<OpcionesConfirm | null>(null);
  const [resolver, setResolver] = useState<((v: boolean) => void) | null>(null);

  const confirmar = useCallback<ConfirmFn>((opts) => {
    setOpciones(opts);
    return new Promise<boolean>((resolve) => {
      setResolver(() => resolve);
    });
  }, []);

  const cerrar = (valor: boolean) => {
    if (resolver) resolver(valor);
    setResolver(null);
    setOpciones(null);
  };

  return (
    <Ctx.Provider value={confirmar}>
      {children}
      <ModalConfirmar
        abierto={opciones !== null}
        titulo={opciones?.titulo || ''}
        mensaje={opciones?.mensaje || ''}
        textoConfirmar={opciones?.textoConfirmar}
        peligro={opciones?.peligro}
        onConfirmar={() => cerrar(true)}
        onCancelar={() => cerrar(false)}
      />
    </Ctx.Provider>
  );
}

export function useConfirm(): ConfirmFn {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error('useConfirm debe usarse dentro de ConfirmProvider');
  return ctx;
}