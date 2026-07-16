import { useEffect, useState } from 'react';
import { vehiculoService, personaService } from '../services/api';
import type { Vehiculo, EstadoVehiculo, Persona } from '../types';
import Modal from './Modal';
import FormPersona from './FormPersona';

const ESTADOS_SELECCIONABLES: EstadoVehiculo[] = [
  'ROBADO', 'RECUPERADO', 'DESAPARECIDO', 'BAJO_OBSERVACION',
];

const estadoLabel: Record<string, string> = {
  NORMAL: 'Normal', ROBADO: 'Robado', RECUPERADO: 'Recuperado',
  DESAPARECIDO: 'Desaparecido', BAJO_OBSERVACION: 'En observación',
  VEHICULO_APOYO: 'Vehículo apoyo',
};

const COLORES: Record<string, string> = {
  Blanco: '#F1F5F9', Negro: '#1E293B', Gris: '#64748B', Rojo: '#DC2626',
  Azul: '#2563EB', Plata: '#94A3B8', Verde: '#059669', Amarillo: '#F59E0B',
};

const vacio = (estado: EstadoVehiculo): Vehiculo => ({
  placa: '', marca: '', modelo: '', estado,
});

interface Props {
  /** Si viene, el form arranca en modo edición con estos datos. */
  vehiculoInicial?: Vehiculo | null;
  /** Estado con que nace el vehículo (default ROBADO). Solo aplica en creación. */
  estadoInicial?: EstadoVehiculo;
  /** Si es true, muestra el campo propietario + su botón "+". */
  mostrarPropietario?: boolean;
  /** Se dispara al guardar con éxito, con el vehículo resultante. */
  onGuardado: (vehiculo: Vehiculo) => void;
  /** Botón cancelar opcional. */
  onCancelar?: () => void;
  /** Texto del botón guardar. */
  textoGuardar?: string;
}

export default function FormVehiculo({
  vehiculoInicial,
  estadoInicial = 'ROBADO',
  mostrarPropietario = false,
  onGuardado,
  onCancelar,
  textoGuardar,
}: Props) {
  const editando = !!vehiculoInicial?.id;
  const [form, setForm] = useState<Vehiculo>(vehiculoInicial ?? vacio(estadoInicial));
  const [err, setErr] = useState('');
  const [guardando, setGuardando] = useState(false);

  // Solo si mostramos propietario: lista de personas + modal de "+"
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [modalPropietario, setModalPropietario] = useState(false);

  useEffect(() => {
    if (!mostrarPropietario) return;
    personaService.listar().then(setPersonas).catch(() => setPersonas([]));
  }, [mostrarPropietario]);

  const onPropietarioCreado = async (persona: Persona) => {
    setModalPropietario(false);
    try {
      const lista = await personaService.listar();
      setPersonas(lista);
      const nueva = lista.find((p) => p.id === persona.id) || persona;
      setForm((f) => ({ ...f, propietario: nueva }));
    } catch (e) {
      console.error('Recargar personas:', e);
    }
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErr('');
    setGuardando(true);
    try {
      let resultado: Vehiculo;
      if (editando) {
        resultado = await vehiculoService.actualizar(vehiculoInicial!.id!, form);
      } else {
        resultado = await vehiculoService.crear(form);
      }
      onGuardado(resultado);
      if (!editando) setForm(vacio(estadoInicial));
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al guardar');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <>
      <form onSubmit={submit}>
        <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)' }}>
          <div className="form-group full">
            <label className="form-label">Placa (ID Único)</label>
            <input
              value={form.placa}
              onChange={(e) => setForm({ ...form, placa: e.target.value.toUpperCase() })}
              placeholder="AB123CD"
              required
              style={{ fontFamily: 'var(--font-mono)', letterSpacing: '0.1em' }}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Marca</label>
            <input
              value={form.marca}
              onChange={(e) => setForm({ ...form, marca: e.target.value })}
              placeholder="Toyota"
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Modelo</label>
            <input
              value={form.modelo}
              onChange={(e) => setForm({ ...form, modelo: e.target.value })}
              placeholder="Hilux"
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Año</label>
            <input
              type="number"
              value={form.anio || ''}
              onChange={(e) => setForm({ ...form, anio: Number(e.target.value) || undefined })}
              placeholder="2024"
            />
          </div>
          <div className="form-group">
            <label className="form-label">Color</label>
            <select
              value={form.color || ''}
              onChange={(e) => setForm({ ...form, color: e.target.value })}
            >
              <option value="">Seleccioná</option>
              {Object.keys(COLORES).map((c) => (
                <option key={c}>{c}</option>
              ))}
            </select>
          </div>
          <div className="form-group full">
            <label className="form-label">Número de chasis</label>
            <input
              value={form.chasis || ''}
              onChange={(e) => setForm({ ...form, chasis: e.target.value.toUpperCase() })}
              placeholder="8AJBA3FS5K0000000"
              required
              maxLength={40}
              style={{ fontFamily: 'var(--font-mono)', letterSpacing: '0.05em' }}
            />
          </div>
          <div className="form-group full">
            <label className="form-label">Declaración (opcional)</label>
            <textarea
              rows={4}
              value={form.declaracion || ''}
              onChange={(e) => setForm({ ...form, declaracion: e.target.value })}
              placeholder="Relato del hecho: cómo ocurrió, circunstancias, detalles relevantes..."
            />
          </div>
          <div className="form-group">
            <label className="form-label">Estado</label>
            <select
              value={form.estado}
              onChange={(e) => setForm({ ...form, estado: e.target.value as EstadoVehiculo })}
            >
              {ESTADOS_SELECCIONABLES.map((est) => (
                <option key={est} value={est}>{estadoLabel[est]}</option>
              ))}
            </select>
          </div>

          {mostrarPropietario && (
            <div className="form-group full">
              <label className="form-label">
                Propietario
                <span className="entity-counter">{personas.length}</span>
              </label>
              <div style={{ display: 'flex', gap: 6 }}>
                <select
                  value={form.propietario?.id || ''}
                  style={{ flex: 1 }}
                  onChange={(e) => {
                    const id = Number(e.target.value);
                    setForm({ ...form, propietario: personas.find((p) => p.id === id) || null });
                  }}
                >
                  <option value="">— Sin propietario —</option>
                  {personas.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nombre} {p.apellido} ({p.documento})
                    </option>
                  ))}
                </select>
                <button type="button" className="btn-secondary"
                  title="Crear persona nueva"
                  style={{ padding: '0 12px', flexShrink: 0 }}
                  onClick={() => setModalPropietario(true)}>
                  <span className="material-symbols-outlined" style={{ fontSize: 18 }}>add</span>
                </button>
              </div>
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
          <button type="submit" className="btn-primary" disabled={guardando}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
              {editando ? 'save' : 'add_circle'}
            </span>
            {guardando ? 'Guardando...' : (textoGuardar ?? (editando ? 'Actualizar registro' : 'Crear Registro'))}
          </button>
        </div>
      </form>

      {/* Modal crear propietario inline (solo cuando se muestra propietario) */}
      {mostrarPropietario && (
        <Modal
          abierto={modalPropietario}
          onClose={() => setModalPropietario(false)}
          titulo="Registrar nueva persona"
          icono="person_add"
          ancho={620}
        >
          <p style={{ color: 'var(--slate-400)', fontSize: 12, marginBottom: 16 }}>
            La persona se registrará con rol de propietario y quedará seleccionada en el vehículo.
          </p>
          <FormPersona
            rolFijo="PROPIETARIO"
            onGuardado={onPropietarioCreado}
            onCancelar={() => setModalPropietario(false)}
            textoGuardar="Crear y seleccionar"
          />
        </Modal>
      )}
    </>
  );
}
