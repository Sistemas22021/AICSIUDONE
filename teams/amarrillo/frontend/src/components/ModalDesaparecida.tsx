import Modal from './Modal';
import type { PersonaDesaparecida, EstadoDesaparicion } from '../types';
import BotonIA from './BotonIA';
import TextoIA from './TextoIA';
import { fileUrl } from '../services/files';

interface Props {
  persona: PersonaDesaparecida;
  abierto: boolean;
  onClose: () => void;
  onEditar: () => void;
  onCambiarEstado: (estado: EstadoDesaparicion) => void;
}

const estadoLabel: Record<EstadoDesaparicion, string> = {
  BUSCADA: 'Buscada',
  ENCONTRADA_VIVA: 'Encontrada con vida',
  ENCONTRADA_FALLECIDA: 'Encontrada fallecida',
  ARCHIVADA: 'Archivada',
};

const prioridadColor: Record<string, string> = {
  CRITICA: '#DC2626',
  ALTA: '#F59E0B',
  MEDIA: '#3B82F6',
  BAJA: '#64748B',
};

const dias = (fecha: string) =>
  Math.floor((Date.now() - new Date(fecha).getTime()) / (1000 * 60 * 60 * 24));

const iniciales = (n: string, a: string) =>
  `${n.charAt(0)}${a.charAt(0)}`.toUpperCase();

export default function ModalDesaparecida({ persona, abierto, onClose, onEditar, onCambiarEstado }: Props) {
  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Ficha de persona desaparecida" icono="person_search" ancho={760}>
      {/* Header con foto */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: '180px 1fr',
        gap: 20,
        marginBottom: 20,
        paddingBottom: 20,
        borderBottom: '1px solid var(--slate-800)',
      }}>
        <div>
          {persona.fotoUrl ? (
            <img src={fileUrl(persona.fotoUrl)} alt={`${persona.nombre} ${persona.apellido}`}
              style={{
                width: '100%',
                height: 220,
                objectFit: 'cover',
                border: `2px solid ${prioridadColor[persona.prioridad]}`,
              }} />
          ) : (
            <div style={{
              width: '100%',
              height: 220,
              background: 'var(--slate-950)',
              border: `2px solid ${prioridadColor[persona.prioridad]}`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontWeight: 700,
              fontSize: 56,
            }}>
              {iniciales(persona.nombre, persona.apellido)}
            </div>
          )}
        </div>

        <div>
          <h2 style={{ color: 'white', margin: 0, fontSize: 22, fontWeight: 800 }}>
            {persona.nombre} {persona.apellido}
          </h2>
          {persona.alias && (
            <div style={{ color: 'var(--slate-500)', fontSize: 13, fontStyle: 'italic', marginTop: 4 }}>
              "{persona.alias}"
            </div>
          )}

          <div style={{ display: 'flex', gap: 8, marginTop: 14, flexWrap: 'wrap' }}>
            <span style={{
              background: prioridadColor[persona.prioridad],
              color: 'white',
              fontSize: 10,
              fontWeight: 700,
              padding: '5px 12px',
              textTransform: 'uppercase',
              letterSpacing: '0.05em',
            }}>
              Prioridad: {persona.prioridad}
            </span>
            <span className="badge muted" style={{ fontSize: 11, padding: '5px 12px' }}>
              {estadoLabel[persona.estado]}
            </span>
            {persona.estado === 'BUSCADA' && (
              <span className="badge robado" style={{ fontSize: 11, padding: '5px 12px' }}>
                {dias(persona.fechaDesaparicion)} días desaparecida
              </span>
            )}
          </div>

          <div style={{
            marginTop: 16,
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: 8,
            fontSize: 12,
          }}>
            <div>
              <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                Documento
              </div>
              <div style={{ color: 'white', fontFamily: 'var(--font-mono)' }}>{persona.documento}</div>
            </div>
            <div>
              <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                Fecha desaparición
              </div>
              <div style={{ color: 'white' }}>
                {new Date(persona.fechaDesaparicion).toLocaleString('es-ES')}
              </div>
            </div>
            {persona.fechaNacimiento && (
              <div>
                <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                  Nacimiento
                </div>
                <div style={{ color: 'white' }}>
                  {new Date(persona.fechaNacimiento).toLocaleDateString('es-ES')}
                </div>
              </div>
            )}
            {persona.genero && (
              <div>
                <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                  Género
                </div>
                <div style={{ color: 'white' }}>{persona.genero}</div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Datos físicos */}
      {(persona.estaturaCm || persona.pesoKg || persona.colorCabello || persona.colorOjos || persona.señasParticulares) && (
        <Section titulo="Descripción física">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
            {persona.estaturaCm && <Field label="Estatura" value={`${persona.estaturaCm} cm`} />}
            {persona.pesoKg && <Field label="Peso" value={`${persona.pesoKg} kg`} />}
            {persona.contextura && <Field label="Contextura" value={persona.contextura} />}
            {persona.colorCabello && <Field label="Cabello" value={persona.colorCabello} />}
            {persona.colorOjos && <Field label="Ojos" value={persona.colorOjos} />}
          </div>
          {persona.señasParticulares && (
            <div style={{ marginTop: 12 }}>
              <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 4 }}>
                Señas particulares
              </div>
              <div style={{ color: 'var(--slate-300)', fontSize: 13, lineHeight: 1.5, padding: 10, background: 'var(--slate-950)', border: '1px solid var(--slate-800)' }}>
                {persona.señasParticulares}
              </div>
            </div>
          )}
          {persona.ropaAlDesaparecer && (
            <div style={{ marginTop: 12 }}>
              <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 4 }}>
                Ropa al desaparecer
              </div>
              <div style={{ color: 'var(--slate-300)', fontSize: 13, lineHeight: 1.5, padding: 10, background: 'var(--slate-950)', border: '1px solid var(--slate-800)' }}>
                {persona.ropaAlDesaparecer}
              </div>
            </div>
          )}
        </Section>
      )}

      {/* Circunstancias */}
      {(persona.ultimaUbicacion || persona.circunstancias) && (
        <Section titulo="Circunstancias">
          {persona.ultimaUbicacion && (
            <Field
              label="Última ubicación conocida"
              value={`${persona.ultimaUbicacion.direccion || `Ubi #${persona.ultimaUbicacion.id}`} (${persona.ultimaUbicacion.latitud.toFixed(4)}, ${persona.ultimaUbicacion.longitud.toFixed(4)})`}
            />
          )}
          {persona.circunstancias && (
            <div style={{
              marginTop: 10,
              padding: 12,
              background: 'var(--slate-950)',
              border: '1px solid var(--slate-800)',
              borderLeft: '3px solid var(--red-500)',
              fontSize: 13,
              color: 'var(--slate-300)',
              lineHeight: 1.6,
            }}>
              {persona.circunstancias}
            </div>
          )}
        </Section>
      )}

      {/* Reportante */}
      {(persona.reportanteNombre || persona.reportanteTelefono) && (
        <Section titulo="Datos del reportante">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
            {persona.reportanteNombre && <Field label="Nombre" value={persona.reportanteNombre} />}
            {persona.reportanteTelefono && <Field label="Teléfono" value={persona.reportanteTelefono} mono />}
            {persona.reportanteRelacion && <Field label="Relación" value={persona.reportanteRelacion} />}
          </div>
        </Section>
      )}

      {/* Análisis IA */}
      {persona.analisisIA && (
        <Section titulo="🤖 Análisis IA">
          <div style={{
            padding: 12,
            background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(220, 38, 38, 0.05) 100%)',
            border: '1px solid rgba(139, 92, 246, 0.3)',
            fontSize: 13,
            color: 'var(--slate-300)',
            lineHeight: 1.6,
          }}>
            <TextoIA texto={persona.analisisIA} />
          </div>
        </Section>
      )}

      {/* Acciones */}
      <div style={{
        display: 'flex',
        gap: 8,
        justifyContent: 'flex-end',
        marginTop: 20,
        paddingTop: 16,
        borderTop: '1px solid var(--slate-800)',
        flexWrap: 'wrap',
      }}>
        {persona.estado === 'BUSCADA' && (
          <>
            <button className="btn-primary"
              onClick={() => onCambiarEstado('ENCONTRADA_VIVA')}
              style={{ background: 'var(--green-600, #16A34A)' }}>
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>check_circle</span>
              Marcar como encontrada
            </button>
            <button className="btn-ghost" onClick={() => onCambiarEstado('ARCHIVADA')}>
              Archivar caso
            </button>
          </>
        )}
        <BotonIA tipo="desaparecida" id={persona.id!} accion="zonas" label="Zonas de búsqueda IA" icono="travel_explore" />
        <BotonIA tipo="desaparecida" id={persona.id!} accion="reporte" label="Reporte IA" icono="description" />
        <button className="btn-secondary" onClick={onEditar}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>edit</span>
          Editar
        </button>
      </div>
    </Modal>
  );
}

function Section({ titulo, children }: { titulo: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 16 }}>
      <h4 style={{
        fontSize: 11,
        color: 'var(--red-500)',
        textTransform: 'uppercase',
        letterSpacing: '0.1em',
        margin: '0 0 10px',
        fontWeight: 700,
      }}>
        {titulo}
      </h4>
      {children}
    </div>
  );
}

function Field({ label, value, mono }: { label: string; value: string; mono?: boolean }) {
  return (
    <div style={{
      padding: 8,
      background: 'var(--slate-950)',
      border: '1px solid var(--slate-800)',
    }}>
      <div style={{ color: 'var(--slate-500)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 2 }}>
        {label}
      </div>
      <div style={{
        color: 'white',
        fontSize: 12,
        fontFamily: mono ? 'var(--font-mono)' : 'inherit',
      }}>
        {value}
      </div>
    </div>
  );
}