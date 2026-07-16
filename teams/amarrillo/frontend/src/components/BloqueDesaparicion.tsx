import { useEffect, useState } from 'react';
import { ubicacionService } from '../services/api';
import type { Ubicacion } from '../types';
import FormularioDesaparecida from './FormularioDesaparecida';

interface Props {
  onGuardado: () => void;
  onCancelar: () => void;
}

/**
 * Monta el FormularioDesaparecida ya existente. Una desaparicion NO es un suceso
 * en el backend: este bloque guarda contra /desaparecidas (lo hace el propio
 * FormularioDesaparecida internamente).
 */
export default function BloqueDesaparicion({ onGuardado, onCancelar }: Props) {
  const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);

  useEffect(() => {
    ubicacionService.listar().then(setUbicaciones).catch(() => setUbicaciones([]));
  }, []);

  return (
    <FormularioDesaparecida
      inicial={null}
      ubicaciones={ubicaciones}
      onGuardado={onGuardado}
      onCancelar={onCancelar}
    />
  );
}
