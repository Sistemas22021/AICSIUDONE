import { useState } from 'react';
import type { TipoSuceso } from '../types';
import BloqueRobo from './BloqueRobo';
import BloqueDesaparicion from './BloqueDesaparicion';
import BloqueSucesoSimple from './BloqueSucesoSimple';

interface Props {
  /** Se dispara cuando un suceso de cualquier tipo se guarda con éxito. */
  onGuardado: () => void;
}

const TIPOS: { valor: TipoSuceso; label: string; icono: string }[] = [
  { valor: 'ROBO_VEHICULO', label: 'Robo de vehículo', icono: 'directions_car' },
  { valor: 'DESAPARICION', label: 'Desaparición', icono: 'person_search' },
  { valor: 'AVISTAMIENTO', label: 'Avistamiento', icono: 'visibility' },
  { valor: 'TRANSACCION', label: 'Transacción', icono: 'paid' },
];

export default function FormSucesoPorTipo({ onGuardado }: Props) {
  const [tipo, setTipo] = useState<TipoSuceso>('ROBO_VEHICULO');
  const [fechaHora, setFechaHora] = useState(new Date().toISOString().slice(0, 16));

  return (
    <div>
      {/* Selector de tipo */}
      <div className="form-group" style={{ marginBottom: 16 }}>
        <label className="form-label">Tipo de suceso</label>
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
          {TIPOS.map(t => (
            <button key={t.valor} type="button"
              className={`option-chip ${tipo === t.valor ? 'active' : ''}`}
              onClick={() => setTipo(t.valor)}
              style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>{t.icono}</span>
              {t.label}
            </button>
          ))}
        </div>
      </div>

      {/* Fecha y hora (común a todos los tipos excepto desaparición, que tiene la suya) */}
      {tipo !== 'DESAPARICION' && (
        <div className="form-group" style={{ marginBottom: 16 }}>
          <label className="form-label">Fecha y hora</label>
          <input type="datetime-local" value={fechaHora}
            max={new Date().toISOString().slice(0, 16)}
            onChange={(e) => setFechaHora(e.target.value)} required />
        </div>
      )}

      {/* Bloque según el tipo */}
      {tipo === 'ROBO_VEHICULO' && (
        <BloqueRobo fechaHora={fechaHora} onGuardado={onGuardado} />
      )}
      {tipo === 'DESAPARICION' && (
        <BloqueDesaparicion onGuardado={onGuardado} onCancelar={() => setTipo('ROBO_VEHICULO')} />
      )}
      {(tipo === 'AVISTAMIENTO' || tipo === 'TRANSACCION') && (
        <BloqueSucesoSimple tipo={tipo} fechaHora={fechaHora} onGuardado={onGuardado} />
      )}
    </div>
  );
}
