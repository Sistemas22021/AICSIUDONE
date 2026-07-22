import React, { useState } from 'react';
import MapView from '../map/MapView';
import { API_BASE_URL } from '../../config';

interface Props {
  onClose: () => void;
  onCreated: (incident: any) => void;
}

const IncidentsModal: React.FC<Props> = ({ onClose, onCreated }) => {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [form, setForm] = useState({
    type: 'ROBO',
    description: '',
    priority: 'MEDIUM',
    latitude: 0,
    longitude: 0
  });

  const handleMapSelect = (lat: number, lng: number) => {
    setForm((prev) => ({
      ...prev,
      latitude: lat,
      longitude: lng
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.description) {
      alert('Completa la descripción');
      return;
    }

    if (form.latitude === 0 && form.longitude === 0) {
      alert('Selecciona una ubicación en el mapa');
      return;
    }

    setIsSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE_URL}/incidents`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Bypass-Tunnel-Reminder': 'true'
          },
          body: JSON.stringify(form)
        }
      );

      if (!res.ok) {
        throw new Error('Error al crear el incidente');
      }

      const created = await res.json();

      onCreated(created);

      setForm({
        type: 'ROBO',
        description: '',
        priority: 'MEDIUM',
        latitude: 0,
        longitude: 0
      });

      onClose();
    } catch (err: any) {
      alert(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0,0,0,0.75)',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 9999
      }}
    >
      <div
        style={{
          background: '#111827',
          padding: 24,
          width: 650,
          borderRadius: 12,
          border: '1px solid #2a2a2a',
          color: '#fff'
        }}
      >
        <h2 style={{ marginBottom: 16 }}>
          Nuevo Incidente
        </h2>

        <form onSubmit={handleSubmit}>
          {/* TIPO */}
          <select
            value={form.type}
            onChange={(e) =>
              setForm({
                ...form,
                type: e.target.value
              })
            }
            style={inputStyle}
          >
            <option value="ROBO">Robo</option>

            <option value="ACCIDENTE">
              Accidente de tránsito
            </option>

            <option value="DISTURBIO">
              Disturbio
            </option>

            <option value="EMERGENCIA_MEDICA">
              Emergencia médica
            </option>

            <option value="VANDALISMO">
              Vandalismo
            </option>
          </select>

          {/* PRIORIDAD */}
          <select
            value={form.priority}
            onChange={(e) =>
              setForm({
                ...form,
                priority: e.target.value
              })
            }
            style={inputStyle}
          >
            <option value="LOW">Baja</option>
            <option value="MEDIUM">Media</option>
            <option value="HIGH">Alta</option>
          </select>

          {/* DESCRIPCIÓN */}
          <textarea
            placeholder="Descripción del incidente"
            value={form.description}
            onChange={(e) =>
              setForm({
                ...form,
                description: e.target.value
              })
            }
            style={{
              ...inputStyle,
              height: 80
            }}
          />

          {/* MAPA */}
          <div
            style={{
              height: 300,
              marginTop: 12,
              borderRadius: 8,
              overflow: 'hidden'
            }}
          >
            <MapView
              incidents={[]}
              onSelectPosition={handleMapSelect}
              selectedPosition={
                form.latitude !== 0 &&
                form.longitude !== 0
                  ? [
                      form.latitude,
                      form.longitude
                    ]
                  : null
              }
              showCounters={false}
            />
          </div>

          {/* COORDENADAS */}
          <p
            style={{
              marginTop: 10,
              fontSize: 12,
              color: '#aaa'
            }}
          >
            Lat: {form.latitude.toFixed(6)}
            {' | '}
            Lng: {form.longitude.toFixed(6)}
          </p>

          {/* BOTONES */}
          <div
            style={{
              display: 'flex',
              gap: 10,
              marginTop: 16
            }}
          >
            <button
              type="submit"
              disabled={isSubmitting}
              style={{
                flex: 1,
                padding: 10,
                background: '#dc2626',
                color: '#fff',
                border: 'none',
                borderRadius: 6,
                cursor: 'pointer',
                opacity: isSubmitting ? 0.6 : 1
              }}
            >
              {isSubmitting
                ? 'Guardando...'
                : 'Guardar'}
            </button>

            <button
              type="button"
              onClick={onClose}
              style={{
                flex: 1,
                padding: 10,
                background: '#333',
                color: '#fff',
                border: 'none',
                borderRadius: 6,
                cursor: 'pointer'
              }}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: 10,
  marginBottom: 10,
  borderRadius: 6,
  border: '1px solid #333',
  background: '#1f2937',
  color: '#fff',
  outline: 'none'
};

export default IncidentsModal;