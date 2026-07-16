import { useEffect, useState } from 'react';
import {
  sucesoService, ubicacionService, vehiculoService, personaService,
} from '../services/api';
import type { Vehiculo, Persona, Ubicacion, TipoSuceso, Suceso } from '../types';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import { divIcon } from 'leaflet';
import Modal from './Modal';

interface Props {
  tipo: Extract<TipoSuceso, 'AVISTAMIENTO' | 'TRANSACCION'>;
  fechaHora: string;
  onGuardado: () => void;
}

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

export default function BloqueSucesoSimple({ tipo, fechaHora, onGuardado }: Props) {
  // En avistamiento se relaciona un vehiculo; en transaccion, una persona.
  const relacionaVehiculo = tipo === 'AVISTAMIENTO';

  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([]);
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [entidadId, setEntidadId] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const [ubiCoords, setUbiCoords] = useState<[number, number] | null>(null);
  const [pickerAbierto, setPickerAbierto] = useState(false);
  const [pickerCoords, setPickerCoords] = useState<[number, number] | null>(null);

  const [err, setErr] = useState('');
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    if (relacionaVehiculo) {
      vehiculoService.listar().then(setVehiculos).catch(() => {});
    } else {
      personaService.listar().then(setPersonas).catch(() => {});
    }
  }, [relacionaVehiculo]);

  const pickIcon = divIcon({
    className: 'custom-marker', iconSize: [32, 42], iconAnchor: [16, 32],
    html: `<div class="marker-pin sospechoso"><span class="material-symbols-outlined">push_pin</span></div>`,
  });
  const confirmarPicker = () => {
    if (pickerCoords) { setUbiCoords(pickerCoords); setPickerAbierto(false); setPickerCoords(null); }
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErr('');
    setGuardando(true);
    try {
      let ubicacionId: number | undefined;
      if (ubiCoords) {
        const u = await ubicacionService.crear({
          direccion: '', latitud: ubiCoords[0], longitud: ubiCoords[1], tipo: 'OTRO',
        } as Ubicacion);
        ubicacionId = u.id;
      }

      const payload: any = {
        tipo,
        fechaHora,
        descripcion: descripcion || null,
        ubicacion: ubicacionId ? { id: ubicacionId } : null,
      };
      if (entidadId) {
        if (relacionaVehiculo) payload.vehiculo = { id: Number(entidadId) };
        else payload.victima = { id: Number(entidadId) };
      }

      await sucesoService.crear(payload as Suceso);
      onGuardado();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al registrar');
      setGuardando(false);
    }
  };

  return (
    <form onSubmit={submit}>
      <div className="form-grid" style={{ gridTemplateColumns: '1fr' }}>
        {/* Entidad relacionada */}
        <div className="form-group">
          <label className="form-label">
            {relacionaVehiculo ? 'Vehículo avistado (opcional)' : 'Persona involucrada (opcional)'}
          </label>
          <select value={entidadId} onChange={(e) => setEntidadId(e.target.value)}>
            <option value="">— Ninguno —</option>
            {relacionaVehiculo
              ? vehiculos.map(v => (
                  <option key={v.id} value={v.id}>{v.placa} — {v.marca} {v.modelo}</option>
                ))
              : personas.map(p => (
                  <option key={p.id} value={p.id}>{p.nombre} {p.apellido} ({p.documento})</option>
                ))}
          </select>
        </div>

        {/* Ubicacion */}
        <div className="form-group">
          <label className="form-label">Ubicación</label>
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

        {/* Descripcion */}
        <div className="form-group">
          <label className="form-label">Descripción</label>
          <textarea rows={4} value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            placeholder="Detalles del evento..." />
        </div>
      </div>

      {err && <div className="error" style={{ marginTop: 12 }}>{err}</div>}

      <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: 16 }}
        disabled={guardando}>
        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>send</span>
        {guardando ? 'Registrando...' : 'Registrar suceso'}
      </button>

      {/* Modal picker de mapa */}
      <Modal abierto={pickerAbierto}
        onClose={() => { setPickerAbierto(false); setPickerCoords(null); }}
        titulo="Marcar ubicación" icono="add_location_alt" ancho={760}>
        <p style={{ color: 'var(--slate-400)', fontSize: 12, marginBottom: 12 }}>
          Hacé click en el mapa para seleccionar las coordenadas.
        </p>
        {pickerAbierto && (
          <div style={{ height: 400, border: '1px solid var(--slate-800)' }}>
            <MapContainer key="picker-simple" center={ubiCoords || [10.45, -64.17]} zoom={11}
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
