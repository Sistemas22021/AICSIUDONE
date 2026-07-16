import { useState } from 'react';
import Modal from './Modal';
import { iaService } from '../services/api';
import type { RespuestaIA } from '../types';
import TextoIA from './TextoIA';
import { useToast } from '../services/ToastContext';

interface Props {
  tipo: 'desaparecida' | 'suceso' | 'alerta';
  id: number;
  accion?: 'reporte' | 'zonas';
  label?: string;
  icono?: string;
}

export default function BotonIA({ tipo, id, accion = 'reporte', label, icono = 'auto_awesome' }: Props) {
  const [abierto, setAbierto] = useState(false);
  const [cargando, setCargando] = useState(false);
  const [respuesta, setRespuesta] = useState<RespuestaIA | null>(null);
  const [error, setError] = useState('');
  const toast = useToast();

  const ejecutar = async () => {
    setAbierto(true);
    setCargando(true);
    setError('');
    setRespuesta(null);

    try {
      let r: RespuestaIA;
      if (accion === 'zonas' && tipo === 'desaparecida') {
        r = await iaService.zonasBusqueda(id);
      } else {
        r = await iaService.reporte(tipo, id);
      }
      setRespuesta(r);
    } catch (e: any) {
      setError(e?.response?.data?.error || 'Error al consultar IA');
    } finally {
      setCargando(false);
    }
  };

  const copiar = () => {
    if (respuesta) {
      navigator.clipboard.writeText(respuesta.contenido);
      toast.exito('Copiado al portapapeles');
    }
  };

  const titulo = accion === 'zonas' ? 'Zonas de búsqueda sugeridas' : 'Reporte ejecutivo IA';
  const labelBoton = label || (accion === 'zonas' ? 'Predecir zonas con IA' : 'Generar reporte IA');

  return (
    <>
      <button
        className="btn-secondary"
        onClick={ejecutar}
        style={{
          background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(220, 38, 38, 0.1) 100%)',
          border: '1px solid rgba(139, 92, 246, 0.4)',
          color: '#C4B5FD',
        }}
      >
        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>{icono}</span>
        {labelBoton}
      </button>

      <Modal abierto={abierto} onClose={() => setAbierto(false)} titulo={titulo} icono="psychology" ancho={720}>
        {cargando && (
          <div style={{ padding: 40, textAlign: 'center', color: 'var(--slate-400)' }}>
            <div style={{
              width: 60, height: 60, margin: '0 auto 16px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.3) 0%, rgba(220, 38, 38, 0.15) 100%)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              animation: 'pulse 1.5s infinite',
            }}>
              <span className="material-symbols-outlined" style={{ fontSize: 32, color: '#A78BFA' }}>
                psychology
              </span>
            </div>
            <div style={{ color: 'white', fontWeight: 700, marginBottom: 4 }}>
              Analizando con IA<span className="dots-loading">...</span>
            </div>
            <div style={{ fontSize: 11 }}>
              Esto puede demorar 5-15 segundos
            </div>
          </div>
        )}

        {error && (
          <div className="error" style={{ marginBottom: 12 }}>{error}</div>
        )}

        {respuesta && (
          <>
            <div style={{
              padding: 16,
              background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.08) 0%, rgba(220, 38, 38, 0.04) 100%)',
              border: '1px solid rgba(139, 92, 246, 0.3)',
              fontSize: 13,
              lineHeight: 1.7,
              color: 'var(--slate-200)',
              maxHeight: 500,
              overflowY: 'auto',
            }}>
              <TextoIA texto={respuesta.contenido} />
            </div>

            <div style={{
              marginTop: 12,
              fontSize: 10,
              color: 'var(--slate-600)',
              fontFamily: 'var(--font-mono)',
              display: 'flex',
              justifyContent: 'space-between',
            }}>
              <span>Modelo: {respuesta.modelo}</span>
              {respuesta.tokensSalida && (
                <span>{respuesta.tokensEntrada} → {respuesta.tokensSalida} tokens · {respuesta.durationMs}ms</span>
              )}
            </div>

            <div style={{
              display: 'flex', gap: 8, justifyContent: 'flex-end',
              marginTop: 16, paddingTop: 16,
              borderTop: '1px solid var(--slate-800)',
            }}>
              <button className="btn-ghost" onClick={copiar}>
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>content_copy</span>
                Copiar
              </button>
              <button className="btn-primary" onClick={() => setAbierto(false)}>
                Cerrar
              </button>
            </div>
          </>
        )}
      </Modal>
    </>
  );
}