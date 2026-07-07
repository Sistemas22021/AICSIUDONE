import { useState } from 'react';
import { personaService } from '../services/api';
import type { Persona, RolPersona } from '../types';

const ROLES: RolPersona[] = ['VICTIMA', 'SOSPECHOSO', 'TESTIGO', 'PROPIETARIO', 'INTERMEDIARIO'];

const rolLabel: Record<string, string> = {
  VICTIMA: 'Víctima',
  SOSPECHOSO: 'Sospechoso',
  TESTIGO: 'Testigo',
  PROPIETARIO: 'Propietario',
  INTERMEDIARIO: 'Intermediario',
};

const vacia = (rol: RolPersona): Persona => ({
  documento: '', nombre: '', apellido: '', rol,
});

interface Props {
  /** Si viene, el form arranca en modo edición con estos datos. */
  personaInicial?: Persona | null;
  /** Si viene, fija el rol y oculta el selector (caso inline: VICTIMA, PROPIETARIO). */
  rolFijo?: RolPersona;
  /** Se dispara al guardar con éxito, con la persona resultante (incluye id). */
  onGuardado: (persona: Persona) => void;
  /** Botón cancelar opcional. */
  onCancelar?: () => void;
  /** Texto del botón guardar (opcional, según contexto). */
  textoGuardar?: string;
}

export default function FormPersona({
  personaInicial,
  rolFijo,
  onGuardado,
  onCancelar,
  textoGuardar,
}: Props) {
  const editando = !!personaInicial?.id;
  const [form, setForm] = useState<Persona>(
    personaInicial ?? vacia(rolFijo ?? 'VICTIMA')
  );
  const [err, setErr] = useState('');
  const [guardando, setGuardando] = useState(false);

  const submit = async () => {
    setErr('');
    setGuardando(true);
    try {
      // Si hay rolFijo, lo forzamos sí o sí
      const payload = rolFijo ? { ...form, rol: rolFijo } : form;
      let resultado: Persona;
      if (editando) {
        resultado = await personaService.actualizar(personaInicial!.id!, payload);
      } else {
        resultado = await personaService.crear(payload);
      }
      onGuardado(resultado);
      // En modo creación, limpiamos para permitir cargar otra
      if (!editando) setForm(vacia(rolFijo ?? 'VICTIMA'));
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al guardar');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div>
      <div className="form-grid">
        <div className="form-group">
          <label className="form-label">Documento / ID</label>
          <input
            value={form.documento}
            onChange={(e) => setForm({ ...form, documento: e.target.value })}
            placeholder="V-12345678"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Nombre</label>
          <input
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            placeholder="Ingresá el nombre"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Apellido</label>
          <input
            value={form.apellido}
            onChange={(e) => setForm({ ...form, apellido: e.target.value })}
            placeholder="Ingresá el apellido"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Alias / Apodo</label>
          <input
            value={form.alias || ''}
            onChange={(e) => setForm({ ...form, alias: e.target.value })}
            placeholder="Apodo conocido"
          />
        </div>
        <div className="form-group">
          <label className="form-label">Teléfono</label>
          <input
            value={form.telefono || ''}
            onChange={(e) => setForm({ ...form, telefono: e.target.value })}
            placeholder="0414-1234567"
          />
        </div>
        {!rolFijo && (
          <div className="form-group">
            <label className="form-label">Rol</label>
            <select
              value={form.rol}
              onChange={(e) => setForm({ ...form, rol: e.target.value as RolPersona })}
            >
              {ROLES.map((r) => (
                <option key={r} value={r}>{rolLabel[r]}</option>
              ))}
            </select>
          </div>
        )}
      </div>

      {err && <div className="error">{err}</div>}

      <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 16 }}>
        {onCancelar && (
          <button type="button" className="btn-ghost" onClick={onCancelar}>
            Cancelar
          </button>
        )}
        <button type="button" className="btn-primary" disabled={guardando} onClick={submit}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>save</span>
          {guardando ? 'Guardando...' : (textoGuardar ?? (editando ? 'Actualizar registro' : 'Guardar en base de datos'))}
        </button>
      </div>
    </div>
  );
}