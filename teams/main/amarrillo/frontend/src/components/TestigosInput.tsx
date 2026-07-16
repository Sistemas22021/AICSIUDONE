import { useState } from 'react';
import type { Persona } from '../types';

/**
 * Un testigo en el formulario puede ser:
 * - existente: solo lleva id (de una persona ya registrada)
 * - nuevo: lleva documento/nombre/apellido/telefono (se creara al guardar)
 * El orquestador del backend acepta ambos formatos.
 */
export interface TestigoForm {
  id?: number;
  documento?: string;
  nombre?: string;
  apellido?: string;
  telefono?: string;
  // Para mostrar en la lista (no se envia): etiqueta legible
  _label: string;
}

interface Props {
  /** Personas ya registradas, para el dropdown de "existente". */
  personas: Persona[];
  /** Lista actual de testigos. */
  testigos: TestigoForm[];
  /** Avisa al padre cuando cambia la lista. */
  onChange: (testigos: TestigoForm[]) => void;
}

export default function TestigosInput({ personas, testigos, onChange }: Props) {
  const [modo, setModo] = useState<'existente' | 'nuevo'>('existente');
  const [personaId, setPersonaId] = useState('');
  const [nuevo, setNuevo] = useState({ documento: '', nombre: '', apellido: '', telefono: '' });

  // Personas que NO son ya testigos en la lista (para no repetir)
  const idsYaAgregados = testigos.filter(t => t.id).map(t => t.id);
  const disponibles = personas.filter(p => !idsYaAgregados.includes(p.id));

  const agregarExistente = () => {
    if (!personaId) return;
    const p = personas.find(x => x.id === Number(personaId));
    if (!p) return;
    onChange([...testigos, {
      id: p.id,
      _label: `${p.nombre} ${p.apellido} (${p.documento})`,
    }]);
    setPersonaId('');
  };

  const agregarNuevo = () => {
    if (!nuevo.documento.trim() || !nuevo.nombre.trim() || !nuevo.apellido.trim()) return;
    onChange([...testigos, {
      documento: nuevo.documento,
      nombre: nuevo.nombre,
      apellido: nuevo.apellido,
      telefono: nuevo.telefono,
      _label: `${nuevo.nombre} ${nuevo.apellido} (${nuevo.documento}) · nuevo`,
    }]);
    setNuevo({ documento: '', nombre: '', apellido: '', telefono: '' });
  };

  const quitar = (idx: number) => {
    onChange(testigos.filter((_, i) => i !== idx));
  };

  return (
    <div className="form-group full">
      <label className="form-label">Testigos ({testigos.length})</label>

      {/* Lista de testigos ya agregados */}
      {testigos.length > 0 && (
        <div style={{ marginBottom: 12 }}>
          {testigos.map((t, idx) => (
            <div key={idx} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '8px 12px', background: 'var(--slate-950)',
              border: '1px solid var(--slate-800)', marginBottom: 6, fontSize: 12,
            }}>
              <span style={{ color: 'white' }}>{t._label}</span>
              <button type="button" onClick={() => quitar(idx)}
                style={{
                  background: 'none', border: 'none', cursor: 'pointer',
                  color: 'var(--red-500)', display: 'inline-flex',
                }}>
                <span className="material-symbols-outlined" style={{ fontSize: 18 }}>close</span>
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Selector de modo */}
      <div style={{ display: 'flex', gap: 4, marginBottom: 10 }}>
        <button type="button" className={`option-chip ${modo === 'existente' ? 'active' : ''}`}
          onClick={() => setModo('existente')}>Persona existente</button>
        <button type="button" className={`option-chip ${modo === 'nuevo' ? 'active' : ''}`}
          onClick={() => setModo('nuevo')}>Testigo nuevo</button>
      </div>

      {/* Modo: elegir existente */}
      {modo === 'existente' && (
        <div style={{ display: 'flex', gap: 6 }}>
          <select value={personaId} onChange={(e) => setPersonaId(e.target.value)} style={{ flex: 1 }}>
            <option value="">— Seleccionar persona —</option>
            {disponibles.map(p => (
              <option key={p.id} value={p.id}>{p.nombre} {p.apellido} ({p.documento})</option>
            ))}
          </select>
          <button type="button" className="btn-secondary"
            onClick={agregarExistente} disabled={!personaId}
            style={{ padding: '0 12px', flexShrink: 0 }}>
            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>add</span>
          </button>
        </div>
      )}

      {/* Modo: cargar nuevo */}
      {modo === 'nuevo' && (
        <div style={{
          padding: 12, background: 'var(--slate-950)',
          border: '1px solid var(--slate-800)',
        }}>
          <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)', gap: 10 }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Documento</label>
              <input value={nuevo.documento}
                onChange={(e) => setNuevo({ ...nuevo, documento: e.target.value })}
                placeholder="V-12345678" />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Teléfono</label>
              <input value={nuevo.telefono}
                onChange={(e) => setNuevo({ ...nuevo, telefono: e.target.value })}
                placeholder="0414-1234567" />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Nombre</label>
              <input value={nuevo.nombre}
                onChange={(e) => setNuevo({ ...nuevo, nombre: e.target.value })}
                placeholder="Nombre" />
            </div>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Apellido</label>
              <input value={nuevo.apellido}
                onChange={(e) => setNuevo({ ...nuevo, apellido: e.target.value })}
                placeholder="Apellido" />
            </div>
          </div>
          <button type="button" className="btn-secondary"
            onClick={agregarNuevo}
            disabled={!nuevo.documento.trim() || !nuevo.nombre.trim() || !nuevo.apellido.trim()}
            style={{ marginTop: 10 }}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>person_add</span>
            Agregar testigo
          </button>
        </div>
      )}
    </div>
  );
}
