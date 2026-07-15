import { useEffect, useRef, useState } from 'react';
import { iaService } from '../services/api';
import type { MensajeChat } from '../types';
import TextoIA from '../components/TextoIA';

const SUGERENCIAS = [
  '¿Cuáles son las alertas críticas activas?',
  'Dame un resumen del estado del sistema',
  '¿Qué patrones detectaste en las desapariciones recientes?',
  '¿Qué ubicaciones sospechosas hay en el sistema?',
  'Recomendame próximos pasos para investigar',
];

export default function AsistenteIA() {
  const [mensajes, setMensajes] = useState<MensajeChat[]>([]);
  const [pregunta, setPregunta] = useState('');
  const [pensando, setPensando] = useState(false);
  const [configurada, setConfigurada] = useState<boolean | null>(null);
  const [error, setError] = useState('');

  const chatRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    iaService.estado()
      .then(e => setConfigurada(e.configurada))
      .catch(() => setConfigurada(false));
  }, []);

  useEffect(() => {
    chatRef.current?.scrollTo({ top: chatRef.current.scrollHeight, behavior: 'smooth' });
  }, [mensajes, pensando]);

  const enviar = async (texto?: string) => {
    const q = (texto || pregunta).trim();
    if (!q || pensando) return;

    setError('');
    setPregunta('');
    setMensajes(prev => [...prev, { role: 'user', content: q }]);
    setPensando(true);

    try {
      const respuesta = await iaService.chat(mensajes, q, true);
      setMensajes(prev => [...prev, { role: 'assistant', content: respuesta.contenido }]);
    } catch (e: any) {
      setError(e?.response?.data?.error || 'Error al consultar la IA');
      setMensajes(prev => prev.slice(0, -1)); // Sacar el último mensaje del usuario
    } finally {
      setPensando(false);
    }
  };

  const reiniciar = () => {
    setMensajes([]);
    setError('');
  };

  if (configurada === false) {
    return (
      <div className="page-header">
        <div>
          <h1 className="page-title">Asistente IA</h1>
          <p className="page-subtitle">Consultas inteligentes sobre el sistema.</p>
        </div>
        <div className="card" style={{ marginTop: 20, padding: 30, textAlign: 'center' }}>
          <span className="material-symbols-outlined" style={{ fontSize: 48, color: 'var(--red-500)' }}>
            warning
          </span>
          <h3 style={{ color: 'white', marginTop: 12 }}>API de IA no configurada</h3>
          <p style={{ color: 'var(--slate-400)', fontSize: 13 }}>
            La API Key de Anthropic no está configurada en el backend.
            <br />
            Definí la variable de entorno <code>ANTHROPIC_API_KEY</code> y reiniciá el servidor.
          </p>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">🤖 Asistente IA</h1>
          <p className="page-subtitle">
            Consultas inteligentes sobre inteligencia criminal. Powered by Claude.
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill live">claude-sonnet-4-5</span>
          {mensajes.length > 0 && (
            <button className="btn-ghost" onClick={reiniciar} style={{ fontSize: 11 }}>
              <span className="material-symbols-outlined" style={{ fontSize: 14 }}>refresh</span>
              Nueva conversación
            </button>
          )}
        </div>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: '1fr',
        gap: 16,
        height: 'calc(100vh - 220px)',
      }}>
        <div style={{
          background: 'var(--slate-900)',
          border: '1px solid var(--slate-800)',
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
        }}>
          {/* Chat area */}
          <div ref={chatRef} style={{
            flex: 1,
            overflowY: 'auto',
            padding: 24,
            display: 'flex',
            flexDirection: 'column',
            gap: 16,
          }}>
            {mensajes.length === 0 && (
              <div style={{
                textAlign: 'center',
                padding: '40px 20px',
                color: 'var(--slate-500)',
              }}>
                <div style={{
                  width: 80,
                  height: 80,
                  margin: '0 auto 20px',
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.2) 0%, rgba(220, 38, 38, 0.1) 100%)',
                  border: '2px solid rgba(139, 92, 246, 0.4)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}>
                  <span className="material-symbols-outlined" style={{ fontSize: 40, color: '#A78BFA' }}>
                    psychology
                  </span>
                </div>
                <h3 style={{ color: 'white', margin: '0 0 8px', fontSize: 18 }}>
                  ¿En qué puedo ayudarte?
                </h3>
                <p style={{ fontSize: 13, marginBottom: 24, color: 'var(--slate-400)' }}>
                  Soy tu asistente de inteligencia criminal. Puedo analizar datos del sistema, detectar patrones,
                  <br />sugerir acciones y generar reportes.
                </p>

                {/* Sugerencias */}
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                  gap: 10,
                  maxWidth: 700,
                  margin: '0 auto',
                }}>
                  {SUGERENCIAS.map((s, i) => (
                    <button
                      key={i}
                      onClick={() => enviar(s)}
                      style={{
                        padding: 12,
                        background: 'var(--slate-950)',
                        border: '1px solid var(--slate-800)',
                        color: 'var(--slate-300)',
                        fontSize: 12,
                        textAlign: 'left',
                        cursor: 'pointer',
                        transition: 'all 0.15s',
                      }}
                      onMouseEnter={e => {
                        e.currentTarget.style.borderColor = 'var(--red-500)';
                        e.currentTarget.style.color = 'white';
                      }}
                      onMouseLeave={e => {
                        e.currentTarget.style.borderColor = 'var(--slate-800)';
                        e.currentTarget.style.color = 'var(--slate-300)';
                      }}
                    >
                      <span className="material-symbols-outlined" style={{ fontSize: 14, color: 'var(--red-500)', marginRight: 6, verticalAlign: 'middle' }}>
                        auto_awesome
                      </span>
                      {s}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {mensajes.map((m, i) => (
              <div key={i} style={{
                display: 'flex',
                gap: 12,
                alignItems: 'flex-start',
                flexDirection: m.role === 'user' ? 'row-reverse' : 'row',
              }}>
                <div style={{
                  width: 36,
                  height: 36,
                  borderRadius: '50%',
                  background: m.role === 'user'
                    ? 'var(--red-600)'
                    : 'linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'white',
                  flexShrink: 0,
                }}>
                  <span className="material-symbols-outlined" style={{ fontSize: 18 }}>
                    {m.role === 'user' ? 'person' : 'psychology'}
                  </span>
                </div>
                <div style={{
                  maxWidth: '75%',
                  padding: '12px 16px',
                  background: m.role === 'user'
                    ? 'var(--red-600)'
                    : 'var(--slate-950)',
                  border: m.role === 'assistant' ? '1px solid var(--slate-800)' : 'none',
                  color: 'white',
                  fontSize: 13,
                  lineHeight: 1.6,
                  wordBreak: 'break-word',
                }}>
                  {m.role === 'assistant'
                    ? <TextoIA texto={m.content} />
                    : <span style={{ whiteSpace: 'pre-wrap' }}>{m.content}</span>}
                </div>
              </div>
            ))}

            {pensando && (
              <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                <div style={{
                  width: 36,
                  height: 36,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}>
                  <span className="material-symbols-outlined" style={{ fontSize: 18, color: 'white' }}>
                    psychology
                  </span>
                </div>
                <div style={{
                  padding: '12px 16px',
                  background: 'var(--slate-950)',
                  border: '1px solid var(--slate-800)',
                  color: 'var(--slate-400)',
                  fontSize: 13,
                  fontStyle: 'italic',
                }}>
                  Analizando<span className="dots-loading">...</span>
                </div>
              </div>
            )}

            {error && (
              <div className="error" style={{ margin: '8px 0' }}>
                {error}
              </div>
            )}
          </div>

          {/* Input */}
          <div style={{
            borderTop: '1px solid var(--slate-800)',
            padding: 16,
            background: 'var(--slate-950)',
          }}>
            <div style={{ display: 'flex', gap: 8, alignItems: 'flex-end' }}>
              <textarea
                value={pregunta}
                onChange={(e) => setPregunta(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    enviar();
                  }
                }}
                placeholder="Preguntá lo que quieras... (Enter para enviar, Shift+Enter para nueva línea)"
                rows={2}
                style={{
                  flex: 1,
                  resize: 'none',
                  fontFamily: 'inherit',
                  fontSize: 13,
                }}
                disabled={pensando}
              />
              <button
                className="btn-primary"
                onClick={() => enviar()}
                disabled={pensando || !pregunta.trim()}
                style={{ height: 'fit-content' }}
              >
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                  send
                </span>
                Enviar
              </button>
            </div>
            <div style={{ fontSize: 10, color: 'var(--slate-600)', marginTop: 6, textAlign: 'center' }}>
              La IA puede cometer errores. Verificá información crítica con fuentes oficiales.
            </div>
          </div>
        </div>
      </div>
    </>
  );
}