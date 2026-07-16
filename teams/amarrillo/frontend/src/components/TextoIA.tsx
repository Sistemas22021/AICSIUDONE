import React from 'react';

interface Props {
  texto: string;
}

// Renderiza **negritas** dentro de una línea de texto
function renderInline(linea: string): React.ReactNode[] {
  const partes = linea.split(/(\*\*[^*]+\*\*)/g);
  return partes.map((parte, i) => {
    if (parte.startsWith('**') && parte.endsWith('**')) {
      return <strong key={i} style={{ color: 'white', fontWeight: 700 }}>{parte.slice(2, -2)}</strong>;
    }
    return <React.Fragment key={i}>{parte}</React.Fragment>;
  });
}

export default function TextoIA({ texto }: Props) {
  if (!texto) return null;

  const lineas = texto.split('\n');
  const bloques: React.ReactNode[] = [];
  let listaBullets: string[] = [];
  let listaNumeros: string[] = [];

  const cerrarBullets = (key: number) => {
    if (listaBullets.length === 0) return;
    bloques.push(
      <ul key={`ul-${key}`} style={{ margin: '8px 0', paddingLeft: 20, color: 'var(--slate-300)' }}>
        {listaBullets.map((item, i) => (
          <li key={i} style={{ marginBottom: 4, lineHeight: 1.6 }}>{renderInline(item)}</li>
        ))}
      </ul>
    );
    listaBullets = [];
  };

  const cerrarNumeros = (key: number) => {
    if (listaNumeros.length === 0) return;
    bloques.push(
      <ol key={`ol-${key}`} style={{ margin: '8px 0', paddingLeft: 22, color: 'var(--slate-300)' }}>
        {listaNumeros.map((item, i) => (
          <li key={i} style={{ marginBottom: 4, lineHeight: 1.6 }}>{renderInline(item)}</li>
        ))}
      </ol>
    );
    listaNumeros = [];
  };

  const cerrarListas = (key: number) => {
    cerrarBullets(key);
    cerrarNumeros(key);
  };

  lineas.forEach((linea, idx) => {
    const t = linea.trim();

    // Lista numerada (1. item, 2. item...)
    if (/^\d+\.\s+/.test(t)) {
      cerrarBullets(idx); // si venía una lista de bullets, cerrala
      listaNumeros.push(t.replace(/^\d+\.\s+/, ''));
      return;
    }

    // Lista con bullets (- item  o  * item)
    if (/^[-*]\s+/.test(t)) {
      cerrarNumeros(idx); // si venía una lista numerada, cerrala
      listaBullets.push(t.replace(/^[-*]\s+/, ''));
      return;
    }

    cerrarListas(idx);

    if (t === '') return; // línea vacía → separador

    // Títulos
    if (t.startsWith('### ')) {
      bloques.push(
        <h4 key={idx} style={{
          color: 'var(--red-400, #f87171)', fontSize: 13, fontWeight: 700,
          margin: '12px 0 6px', textTransform: 'uppercase', letterSpacing: '0.05em',
        }}>{renderInline(t.slice(4))}</h4>
      );
      return;
    }
    if (t.startsWith('## ')) {
      bloques.push(
        <h3 key={idx} style={{
          color: 'white', fontSize: 15, fontWeight: 700, margin: '14px 0 8px',
          borderBottom: '1px solid var(--slate-800)', paddingBottom: 4,
        }}>{renderInline(t.slice(3))}</h3>
      );
      return;
    }
    if (t.startsWith('# ')) {
      bloques.push(
        <h2 key={idx} style={{ color: 'white', fontSize: 17, fontWeight: 800, margin: '16px 0 8px' }}>
          {renderInline(t.slice(2))}
        </h2>
      );
      return;
    }

    // Párrafo normal
    bloques.push(
      <p key={idx} style={{ margin: '6px 0', lineHeight: 1.6, color: 'var(--slate-300)' }}>
        {renderInline(t)}
      </p>
    );
  });

  cerrarListas(9999); // cerrar listas pendientes al final

  return <div>{bloques}</div>;
}