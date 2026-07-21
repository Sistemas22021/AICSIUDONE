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
    type: 'Robo',
    description: '',
    priority: 'MEDIUM',
    latitude: 0,
    longitude: 0
  });

  // 1. Estado para almacenar el tipo personalizado cuando se selecciona "Otro"
  const [customType, setCustomType] = useState('');

  // 3. Función de validación: solo permite letras, espacios, tildes y la letra Ñ
  const validateIncidentType = (text: string) => {
    return /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(text);
  };

  const handleMapSelect = (lat: number, lng: number) => {
    setForm((prev) => ({
      ...prev,
      latitude: lat,
      longitude: lng
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // 1. Validación de la descripción (.trim() evita puros espacios vacíos)
    if (!form.description.trim()) {
      alert('Completa la descripción del incidente');
      return;
    }

    // 4. Cambiar validación del tipo personalizado
    if (form.type === "Personalizado") {
      const cleanType = customType.trim();

      if (!cleanType) {
        alert("Especifique el tipo de incidente");
        return;
      }

      if (!validateIncidentType(cleanType)) {
        alert("El tipo de incidente solo puede contener letras y espacios");
        return;
      }
    }

    if (form.latitude === 0 && form.longitude === 0) {
      alert('Selecciona una ubicación en el mapa');
      return;
    }

    setIsSubmitting(true);

    // Conversión de datos final antes de enviar al backend
    const incidentData = {
      ...form,
      type: form.type === "Personalizado" ? customType.trim() : form.type
    };

    try {
      const res = await fetch(
        `${API_BASE_URL}/incidents`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(incidentData)
        }
      );

      if (!res.ok) {
        throw new Error('Error al crear el incidente');
      }

      const created = await res.json();

      onCreated(created);

      // Reinicio del formulario al estado inicial limpio
      setForm({
        type: 'Robo',
        description: '',
        priority: 'MEDIUM',
        latitude: 0,
        longitude: 0
      });
      setCustomType('');

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
          {/* SELECT DE TIPOS DE INCIDENTE */}
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
            <option value="Robo">Robo</option>
            <option value="Hurto">Hurto</option>
            <option value="Accidente de tránsito">Accidente de tránsito</option>
            <option value="Emergencia médica">Emergencia médica</option>
            <option value="Incendio">Incendio</option>
            <option value="Disturbio">Disturbio</option>
            <option value="Vandalismo">Vandalismo</option>
            <option value="Violencia doméstica">Violencia doméstica</option>
            <option value="Persona desaparecida">Persona desaparecida</option>
            <option value="Persona sospechosa">Persona sospechosa</option>
            <option value="Alteración del orden público">Alteración del orden público</option>
            <option value="Fuga de gas">Fuga de gas</option>
            <option value="Desastre natural">Desastre natural</option>
            <option value="Apoyo policial">Apoyo policial</option>
            <option value="Amenaza">Amenaza</option>
            <option value="Secuestro">Secuestro</option>
            <option value="Riña">Riña</option>
            <option value="Persona lesionada">Persona lesionada</option>
            <option value="Objeto sospechoso">Objeto sospechoso</option>
            <option value="Personalizado">Otro</option>
          </select>

          {/* Input condicional para escribir un tipo personalizado */}
          {form.type === "Personalizado" && (
            <input
              type="text"
              placeholder="Escriba el tipo de incidente"
              value={customType}
              onChange={(e) => setCustomType(e.target.value)}
              style={inputStyle}
            />
          )}

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