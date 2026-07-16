import { useEffect, useRef, useState } from 'react';
import { desaparecidaService } from '../services/api';
import { fileUrl } from '../services/files';
import type { FotoDesaparecida } from '../types';

interface Props {
  /** Id de la persona desaparecida. Si es null/undefined, la galería está deshabilitada. */
  desaparecidaId?: number | null;
  /** Se dispara cuando cambian las fotos (subir/borrar/principal), por si el padre quiere recargar. */
  onCambio?: () => void;
}

export default function GaleriaFotos({ desaparecidaId, onCambio }: Props) {
  const [fotos, setFotos] = useState<FotoDesaparecida[]>([]);
  const [cargando, setCargando] = useState(false);
  const [subiendo, setSubiendo] = useState(false);
  const [err, setErr] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  const cargarFotos = async () => {
    if (!desaparecidaId) return;
    setCargando(true);
    try {
      const lista = await desaparecidaService.listarFotos(desaparecidaId);
      // Ordenar: principal primero, luego por orden
      lista.sort((a, b) => {
        if (a.principal && !b.principal) return -1;
        if (!a.principal && b.principal) return 1;
        return (a.orden ?? 0) - (b.orden ?? 0);
      });
      setFotos(lista);
    } catch (e) {
      console.error('Cargar fotos:', e);
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarFotos();
  }, [desaparecidaId]);

  const handleArchivos = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const archivos = Array.from(e.target.files || []);
    if (archivos.length === 0 || !desaparecidaId) return;
    setErr('');
    setSubiendo(true);
    try {
      // Subir una por una (el endpoint acepta de a una)
      for (const archivo of archivos) {
        if (!archivo.type.startsWith('image/')) {
          setErr('Solo se permiten imágenes');
          continue;
        }
        if (archivo.size > 5 * 1024 * 1024) {
          setErr('Cada imagen debe ser menor a 5MB');
          continue;
        }
        await desaparecidaService.agregarFoto(desaparecidaId, archivo);
      }
      await cargarFotos();
      onCambio?.();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Error al subir las fotos');
    } finally {
      setSubiendo(false);
      if (inputRef.current) inputRef.current.value = '';
    }
  };

  const marcarPrincipal = async (fotoId: number) => {
    if (!desaparecidaId) return;
    try {
      await desaparecidaService.marcarFotoPrincipal(desaparecidaId, fotoId);
      await cargarFotos();
      onCambio?.();
    } catch (e) {
      console.error('Marcar principal:', e);
    }
  };

  const borrar = async (fotoId: number) => {
    if (!desaparecidaId) return;
    try {
      await desaparecidaService.eliminarFoto(desaparecidaId, fotoId);
      await cargarFotos();
      onCambio?.();
    } catch (e) {
      console.error('Borrar foto:', e);
    }
  };

  // Sin id: la persona aún no existe (modo creación), no se pueden subir fotos
  if (!desaparecidaId) {
    return (
      <div style={{
        padding: 16, textAlign: 'center', background: 'var(--slate-950)',
        border: '1px dashed var(--slate-700)', color: 'var(--slate-500)', fontSize: 12,
      }}>
        <span className="material-symbols-outlined" style={{ fontSize: 28, display: 'block', marginBottom: 6 }}>
          photo_library
        </span>
        Guardá el caso primero para poder agregar varias fotos.
      </div>
    );
  }

  return (
    <div>
      {/* Grilla de fotos */}
      {fotos.length > 0 && (
        <div style={{
          display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(110px, 1fr))',
          gap: 10, marginBottom: 12,
        }}>
          {fotos.map((f) => (
            <div key={f.id} style={{
              position: 'relative', border: f.principal ? '2px solid var(--red-500)' : '1px solid var(--slate-800)',
              background: 'var(--slate-950)', overflow: 'hidden',
            }}>
              <img src={fileUrl(f.url) || ''} alt={f.descripcion || 'Foto'}
                style={{ width: '100%', height: 110, objectFit: 'cover', display: 'block' }} />

              {/* Badge principal */}
              {f.principal && (
                <div style={{
                  position: 'absolute', top: 4, left: 4, background: 'var(--red-600)',
                  color: 'white', fontSize: 9, fontWeight: 700, padding: '2px 6px',
                  textTransform: 'uppercase', letterSpacing: '0.05em',
                }}>
                  Principal
                </div>
              )}

              {/* Acciones sobre la foto */}
              <div style={{
                position: 'absolute', bottom: 0, left: 0, right: 0,
                display: 'flex', justifyContent: 'space-between',
                background: 'rgba(2, 6, 23, 0.85)', padding: '4px 6px',
              }}>
                {!f.principal ? (
                  <button type="button" title="Marcar como principal"
                    onClick={() => f.id && marcarPrincipal(f.id)}
                    style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--slate-300)', display: 'inline-flex' }}>
                    <span className="material-symbols-outlined" style={{ fontSize: 16 }}>star</span>
                  </button>
                ) : <span style={{ width: 16 }} />}
                <button type="button" title="Eliminar foto"
                  onClick={() => f.id && borrar(f.id)}
                  style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--red-500)', display: 'inline-flex' }}>
                  <span className="material-symbols-outlined" style={{ fontSize: 16 }}>delete</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {cargando && (
        <div style={{ fontSize: 12, color: 'var(--slate-500)', marginBottom: 8 }}>Cargando fotos...</div>
      )}

      {fotos.length === 0 && !cargando && (
        <div style={{ fontSize: 12, color: 'var(--slate-600)', marginBottom: 8 }}>
          Sin fotos adicionales todavía.
        </div>
      )}

      {/* Subir fotos */}
      <label className="btn-secondary" style={{ display: 'inline-flex', cursor: subiendo ? 'wait' : 'pointer' }}>
        <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
          {subiendo ? 'hourglass_empty' : 'add_photo_alternate'}
        </span>
        {subiendo ? 'Subiendo...' : 'Agregar fotos'}
        <input ref={inputRef} type="file" accept="image/*" multiple
          onChange={handleArchivos} disabled={subiendo} style={{ display: 'none' }} />
      </label>
      <div style={{ fontSize: 10, color: 'var(--slate-600)', marginTop: 6 }}>
        Podés seleccionar varias a la vez · Máx 5MB c/u · JPG, PNG, WebP
      </div>

      {err && <div className="error" style={{ marginTop: 8 }}>{err}</div>}
    </div>
  );
}
