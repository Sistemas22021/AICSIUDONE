import { useState } from 'react';
import { testimonioService } from '../services/api';

export default function Testimonios() {
  const [documento, setDocumento] = useState('');
  const [texto, setTexto] = useState('');
  const [fecha, setFecha] = useState('');
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState<any>(null);
  const [err, setErr] = useState('');

  const procesar = async () => {
    if (!texto.trim()) {
      setErr('Pegá el texto del testimonio primero.');
      return;
    }
    setErr(''); setResultado(null); setCargando(true);
    try {
      const r = await testimonioService.procesar(documento.trim(), texto.trim(), fecha);
      setResultado(r);
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'No se pudo procesar el testimonio.');
    } finally {
      setCargando(false);
    }
  };

  const limpiar = () => {
    setDocumento(''); setTexto(''); setFecha('');
    setResultado(null); setErr('');
  };

  const campos = resultado?.camposExtraidos || {};
  const persona = resultado?.persona || {};
  const hayError = campos?.error;

  return (
    <>
      <div className="page-header">
        <div>
          <h1 className="page-title">Procesar Testimonio</h1>
          <p className="page-subtitle">
            Transcripción recibida del equipo de testimonios. La IA extrae los datos del hecho para su revisión.
          </p>
        </div>
        <div className="page-badges">
          <span className="badge-pill">INTEGRACIÓN IA</span>
        </div>
      </div>

      <div className="bento-grid">
        {/* Columna de entrada */}
        <div className="bento-col-5">
          <div className="form-card" style={{ height: '100%' }}>
            <div className="card-header" style={{ borderBottom: 'none', paddingBottom: 0, marginBottom: 20 }}>
              <span className="material-symbols-outlined">record_voice_over</span>
              <h3 className="card-title">Testimonio recibido</h3>
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Documento de la persona (opcional)</label>
              <input value={documento}
                onChange={(e) => setDocumento(e.target.value)}
                placeholder="Ej: V-12345678" />
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Fecha (opcional)</label>
              <input type="datetime-local" value={fecha}
                max={new Date().toISOString().slice(0, 16)}
                onChange={(e) => setFecha(e.target.value)} />
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Transcripción del testimonio</label>
              <textarea rows={10} value={texto}
                onChange={(e) => setTexto(e.target.value)}
                placeholder="Pegá acá el texto transcrito del testimonio..." />
            </div>

            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button className="btn-ghost" onClick={limpiar} disabled={cargando}>Limpiar</button>
              <button className="btn-primary" onClick={procesar} disabled={cargando}>
                <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
                  {cargando ? 'hourglass_empty' : 'neurology'}
                </span>
                {cargando ? 'Procesando...' : 'Procesar con IA'}
              </button>
            </div>

            {err && <div className="error" style={{ marginTop: 12 }}>{err}</div>}
          </div>
        </div>

        {/* Columna de resultado */}
        <div className="bento-col-7">
          <div className="form-card" style={{ height: '100%' }}>
            <div className="card-header" style={{ borderBottom: 'none', paddingBottom: 0, marginBottom: 20 }}>
              <span className="material-symbols-outlined">auto_awesome</span>
              <h3 className="card-title">Datos extraídos por la IA</h3>
            </div>

            {!resultado && (
              <div style={{ padding: 40, textAlign: 'center', color: 'var(--slate-500)' }}>
                <span className="material-symbols-outlined" style={{ fontSize: 40, opacity: 0.4 }}>science</span>
                <p style={{ marginTop: 12, fontSize: 13 }}>
                  Pegá un testimonio y presioná "Procesar con IA" para ver los datos extraídos.
                </p>
              </div>
            )}

            {resultado && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                {/* Persona */}
                <div style={{
                  padding: 12, background: 'var(--slate-950)',
                  border: '1px solid var(--slate-800)', borderRadius: 4,
                }}>
                  <div style={{ fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 6 }}>
                    Persona vinculada
                  </div>
                  {persona.encontrada ? (
                    <div style={{ color: 'white', fontSize: 14 }}>
                      {persona.nombre} {persona.apellido}
                      <span style={{ color: 'var(--slate-400)', fontFamily: 'var(--font-mono)', marginLeft: 8 }}>
                        {persona.documento}
                      </span>
                      {persona.rol && <span className="badge robado" style={{ marginLeft: 8 }}>{persona.rol}</span>}
                    </div>
                  ) : (
                    <div style={{ color: 'var(--amber-400, #d97706)', fontSize: 13 }}>
                      No se encontró persona{persona.documentoBuscado ? ` con documento ${persona.documentoBuscado}` : ''}.
                      Podés crearla luego y vincular el testimonio.
                    </div>
                  )}
                </div>

                {hayError ? (
                  <div className="error">{campos.error}</div>
                ) : (
                  <>
                    {[
                      { k: 'tipoSugerido', label: 'Tipo de suceso sugerido' },
                      { k: 'modusOperandi', label: 'Modus operandi' },
                      { k: 'descripcion', label: 'Descripción de los hechos' },
                      { k: 'ubicacionMencionada', label: 'Ubicación mencionada' },
                      { k: 'personasMencionadas', label: 'Personas mencionadas' },
                    ].map(({ k, label }) => (
                      <div key={k}>
                        <div style={{ fontSize: 11, color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 4 }}>
                          {label}
                        </div>
                        <div style={{
                          padding: 10, background: 'var(--slate-950)',
                          border: '1px solid var(--slate-800)', borderRadius: 4,
                          fontSize: 13, color: campos[k] ? 'var(--slate-200)' : 'var(--slate-600)',
                        }}>
                          {campos[k] || '— sin datos —'}
                        </div>
                      </div>
                    ))}
                  </>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}