import { useEffect, useState } from 'react';
import {
  personaService, ubicacionService, modusService, iaService, roboCompletoService,
} from '../services/api';
import type { Persona, Ubicacion } from '../types';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import { divIcon } from 'leaflet';
import Modal from './Modal';
import FormPersona from './FormPersona';
import TestigosInput, { TestigoForm } from './TestigosInput';

interface Props {
  fechaHora: string;
  onGuardado: () => void;
}

// Captura clicks en el mapa
function CapturadorClicks({ onPick }: { onPick: (lat: number, lng: number) => void }) {
  useMapEvents({ click(e) { onPick(e.latlng.lat, e.latlng.lng); } });
  return null;
}
function InvalidarTamano() {
  const map = useMap();
  useEffect(() => {
    const t = setTimeout(() => map.invalidateSize(), 200);
    return () => clearTimeout(t);
  }, [map]);
  return null;
}

const COLORES = ['Blanco', 'Negro', 'Gris', 'Rojo', 'Azul', 'Plata', 'Verde', 'Amarillo'];

export default function BloqueRobo({ fechaHora, onGuardado }: Props) {
  // --- Datos del vehiculo ---
  const [veh, setVeh] = useState({
    placa: '', marca: '', modelo: '', anio: '', color: '', chasis: '', declaracion: '',
  });

  // --- Victima / propietario ---
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [victimaId, setVictimaId] = useState('');
  const [modalVictima, setModalVictima] = useState(false);

  // --- Modus ---
  const [catalogoModus, setCatalogoModus] = useState<{ codigo: string; etiqueta: string }[]>([]);
  const [modus, setModus] = useState('');
  const [sugiriendo, setSugiriendo] = useState(false);

  // --- Descripcion ---
  const [descripcion, setDescripcion] = useState('');

  // --- Ubicacion ---
  const [ubiCoords, setUbiCoords] = useState<[number, number] | null>(null);
  const [pickerAbierto, setPickerAbierto] = useState(false);
  const [pickerCoords, setPickerCoords] = useState<[number, number] | null>(null);

  // --- Testigos ---
  const [testigos, setTestigos] = useState<TestigoForm[]>([]);

  const [err, setErr] = useState('');
  const [guardando, setGuardando] = useState(false);

  const cargarPersonas = () => personaService.listar().then(setPersonas).catch(() => {});
  useEffect(() => {
    cargarPersonas();
    modusService.listar().then(setCatalogoModus).catch(() => {});
  }, []);

  const onVictimaCreada = async (persona: Persona) => {
    setModalVictima(false);
    await cargarPersonas();
    if (persona.id) setVictimaId(String(persona.id));
  };

  const pickIcon = divIcon({
    className: 'custom-marker', iconSize: [32, 42], iconAnchor: [16, 32],
    html: `<div class="marker-pin sospechoso"><span class="material-symbols-outlined">push_pin</span></div>`,
  });

  const confirmarPicker = () => {
    if (pickerCoords) { setUbiCoords(pickerCoords); setPickerAbierto(false); setPickerCoords(null); }
  };

  const sugerirModus = async () => {
    if (!descripcion.trim()) {
      setErr('Escribí una descripción para que la IA sugiera el modus.');
      setTimeout(() => setErr(''), 3000);
      return;
    }
    setSugiriendo(true);
    try {
      const r = await iaService.clasificarModus(descripcion);
      setModus(r.codigo);
    } catch {
      setErr('No se pudo obtener la sugerencia de la IA.');
      setTimeout(() => setErr(''), 3000);
    } finally {
      setSugiriendo(false);
    }
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErr('');
    if (!victimaId) {
      setErr('Seleccioná o creá la víctima/propietario del vehículo.');
      return;
    }
    setGuardando(true);
    try {
      // Resolver ubicacion: si hay coords, crear la Ubicacion inline
      let ubicacionId: number | undefined;
      if (ubiCoords) {
        const u = await ubicacionService.crear({
          direccion: '', latitud: ubiCoords[0], longitud: ubiCoords[1], tipo: 'OTRO',
        } as Ubicacion);
        ubicacionId = u.id;
      }

      // Armar el payload para el orquestador
      const payload = {
        placa: veh.placa,
        marca: veh.marca,
        modelo: veh.modelo,
        anio: veh.anio ? Number(veh.anio) : null,
        color: veh.color || null,
        chasis: veh.chasis,
        declaracion: veh.declaracion || null,
        victimaId: Number(victimaId),
        fechaHora,
        modusOperandi: modus || null,
        descripcion: descripcion || null,
        ubicacionId: ubicacionId ?? null,
        testigos: testigos.map(t => ({
          id: t.id ?? null,
          documento: t.documento ?? null,
          nombre: t.nombre ?? null,
          apellido: t.apellido ?? null,
          telefono: t.telefono ?? null,
        })),
      };

      await roboCompletoService.registrar(payload);
      onGuardado();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al registrar el robo');
      setGuardando(false);
    }
  };

  return (
    <form onSubmit={submit}>
      <div className="form-grid" style={{ gridTemplateColumns: 'repeat(2, 1fr)' }}>
        {/* Vehiculo */}
        <div className="form-group full">
          <label className="form-label">Placa (ID Único)</label>
          <input value={veh.placa}
            onChange={(e) => setVeh({ ...veh, placa: e.target.value.toUpperCase() })}
            placeholder="AB123CD" required
            style={{ fontFamily: 'var(--font-mono)', letterSpacing: '0.1em' }} />
        </div>
        <div className="form-group">
          <label className="form-label">Marca</label>
          <input value={veh.marca} onChange={(e) => setVeh({ ...veh, marca: e.target.value })}
            placeholder="Toyota" required />
        </div>
        <div className="form-group">
          <label className="form-label">Modelo</label>
          <input value={veh.modelo} onChange={(e) => setVeh({ ...veh, modelo: e.target.value })}
            placeholder="Hilux" required />
        </div>
        <div className="form-group">
          <label className="form-label">Año</label>
          <input type="number" value={veh.anio}
            onChange={(e) => setVeh({ ...veh, anio: e.target.value })} placeholder="2024" />
        </div>
        <div className="form-group">
          <label className="form-label">Color</label>
          <select value={veh.color} onChange={(e) => setVeh({ ...veh, color: e.target.value })}>
            <option value="">Seleccioná</option>
            {COLORES.map(c => <option key={c}>{c}</option>)}
          </select>
        </div>
        <div className="form-group full">
          <label className="form-label">Número de chasis</label>
          <input value={veh.chasis}
            onChange={(e) => setVeh({ ...veh, chasis: e.target.value.toUpperCase() })}
            placeholder="8AJBA3FS5K0000000" required maxLength={40}
            style={{ fontFamily: 'var(--font-mono)', letterSpacing: '0.05em' }} />
        </div>
        <div className="form-group full">
          <label className="form-label">Declaración (opcional)</label>
          <textarea rows={3} value={veh.declaracion}
            onChange={(e) => setVeh({ ...veh, declaracion: e.target.value })}
            placeholder="Relato del hecho..." />
        </div>

        {/* Victima / propietario */}
        <div className="form-group full">
          <label className="form-label">
            Víctima / Propietario<span className="entity-counter">{personas.length}</span>
          </label>
          <div style={{ display: 'flex', gap: 6 }}>
            <select value={victimaId} onChange={(e) => setVictimaId(e.target.value)}
              style={{ flex: 1 }} required>
              <option value="">— Seleccionar —</option>
              {personas.map(p => (
                <option key={p.id} value={p.id}>{p.nombre} {p.apellido} ({p.documento})</option>
              ))}
            </select>
            <button type="button" className="btn-secondary" title="Crear persona nueva"
              style={{ padding: '0 12px', flexShrink: 0 }}
              onClick={() => setModalVictima(true)}>
              <span className="material-symbols-outlined" style={{ fontSize: 18 }}>add</span>
            </button>
          </div>
          <div style={{ fontSize: 10, color: 'var(--slate-500)', marginTop: 4 }}>
            Esta persona será el propietario del vehículo y la víctima del robo.
          </div>
        </div>

        {/* Modus */}
        <div className="form-group full">
          <label className="form-label">Modus operandi</label>
          <select value={modus} onChange={(e) => setModus(e.target.value)}>
            <option value="">— Sin especificar —</option>
            {catalogoModus.map(m => <option key={m.codigo} value={m.codigo}>{m.etiqueta}</option>)}
          </select>
          <button type="button" onClick={sugerirModus} disabled={sugiriendo}
            style={{
              marginTop: 8, display: 'inline-flex', alignItems: 'center', gap: 6,
              padding: '6px 12px', fontSize: 12, cursor: 'pointer',
              background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(220, 38, 38, 0.1) 100%)',
              border: '1px solid rgba(139, 92, 246, 0.4)', color: '#C4B5FD',
            }}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
              {sugiriendo ? 'hourglass_empty' : 'auto_awesome'}
            </span>
            {sugiriendo ? 'Analizando...' : 'Sugerir con IA'}
          </button>
        </div>

        {/* Descripcion */}
        <div className="form-group full">
          <label className="form-label">Descripción</label>
          <textarea rows={3} value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            placeholder="Detalles del incidente..." />
        </div>

        {/* Ubicacion */}
        <div className="form-group full">
          <label className="form-label">Ubicación del hecho</label>
          <button type="button" className="btn-secondary"
            onClick={() => { setPickerCoords(null); setPickerAbierto(true); }}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>add_location_alt</span>
            {ubiCoords ? 'Cambiar ubicación en mapa' : 'Marcar en mapa'}
          </button>
          {ubiCoords && (
            <div style={{ marginTop: 8, fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--green-600, #16A34A)' }}>
              ✓ {ubiCoords[0].toFixed(6)}, {ubiCoords[1].toFixed(6)}
            </div>
          )}
        </div>

        {/* Testigos */}
        <TestigosInput personas={personas} testigos={testigos} onChange={setTestigos} />
      </div>

      {err && <div className="error" style={{ marginTop: 12 }}>{err}</div>}

      <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: 16 }}
        disabled={guardando}>
        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>send</span>
        {guardando ? 'Registrando...' : 'Registrar robo completo'}
      </button>

      {/* Modal crear victima */}
      <Modal abierto={modalVictima} onClose={() => setModalVictima(false)}
        titulo="Registrar nueva persona" icono="person_add" ancho={620}>
        <p style={{ color: 'var(--slate-400)', fontSize: 12, marginBottom: 16 }}>
          Se registrará como víctima y quedará seleccionada como propietario/víctima.
        </p>
        <FormPersona rolFijo="VICTIMA" onGuardado={onVictimaCreada}
          onCancelar={() => setModalVictima(false)} textoGuardar="Crear y seleccionar" />
      </Modal>

      {/* Modal picker de mapa */}
      <Modal abierto={pickerAbierto}
        onClose={() => { setPickerAbierto(false); setPickerCoords(null); }}
        titulo="Marcar ubicación del hecho" icono="add_location_alt" ancho={760}>
        <p style={{ color: 'var(--slate-400)', fontSize: 12, marginBottom: 12 }}>
          Hacé click en el mapa para seleccionar las coordenadas.
        </p>
        {pickerAbierto && (
          <div style={{ height: 400, border: '1px solid var(--slate-800)' }}>
            <MapContainer key="picker-robo" center={ubiCoords || [10.45, -64.17]} zoom={11}
              style={{ height: '100%', width: '100%' }}>
              <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution="© OpenStreetMap" />
              <InvalidarTamano />
              <CapturadorClicks onPick={(lat, lng) => setPickerCoords([lat, lng])} />
              {pickerCoords && <Marker position={pickerCoords} icon={pickIcon} />}
            </MapContainer>
          </div>
        )}
        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 16, paddingTop: 16, borderTop: '1px solid var(--slate-800)' }}>
          <button type="button" className="btn-ghost"
            onClick={() => { setPickerAbierto(false); setPickerCoords(null); }}>Cancelar</button>
          <button type="button" className="btn-primary" onClick={confirmarPicker} disabled={!pickerCoords}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>check</span>
            Usar estas coordenadas
          </button>
        </div>
      </Modal>
    </form>
  );
}
