interface Props {
  total: number;
  pagina: number;
  porPagina: number;
  onCambiar: (n: number) => void;
  label?: string;
}

export default function Paginacion({ total, pagina, porPagina, onCambiar, label = 'registros' }: Props) {
  const totalPaginas = Math.max(1, Math.ceil(total / porPagina));
  const desde = total === 0 ? 0 : (pagina - 1) * porPagina + 1;
  const hasta = Math.min(pagina * porPagina, total);

  // Construir lista de páginas visibles (máximo 5 con elipsis)
  const paginas: (number | '...')[] = [];
  if (totalPaginas <= 7) {
    for (let i = 1; i <= totalPaginas; i++) paginas.push(i);
  } else {
    paginas.push(1);
    if (pagina > 3) paginas.push('...');
    const inicio = Math.max(2, pagina - 1);
    const fin = Math.min(totalPaginas - 1, pagina + 1);
    for (let i = inicio; i <= fin; i++) paginas.push(i);
    if (pagina < totalPaginas - 2) paginas.push('...');
    paginas.push(totalPaginas);
  }

  return (
    <div className="pagination">
      <span>
        Mostrando {desde}–{hasta} de {total} {label}
      </span>
      <div style={{ display: 'flex', gap: 4 }}>
        <button
          className="btn-ghost"
          style={{ padding: '4px 8px', minWidth: 32 }}
          disabled={pagina === 1}
          onClick={() => onCambiar(pagina - 1)}
          title="Anterior"
        >
          <span className="material-symbols-outlined" style={{ fontSize: 14 }}>
            chevron_left
          </span>
        </button>
        {paginas.map((p, i) =>
          p === '...' ? (
            <span key={`e-${i}`} style={{ padding: '4px 6px', color: 'var(--slate-600)' }}>
              …
            </span>
          ) : (
            <button
              key={p}
              className="btn-ghost"
              style={{
                padding: '4px 10px',
                minWidth: 32,
                ...(p === pagina
                  ? { background: 'var(--red-600)', color: 'white', borderColor: 'var(--red-700)' }
                  : {}),
              }}
              onClick={() => onCambiar(p as number)}
            >
              {p}
            </button>
          )
        )}
        <button
          className="btn-ghost"
          style={{ padding: '4px 8px', minWidth: 32 }}
          disabled={pagina === totalPaginas}
          onClick={() => onCambiar(pagina + 1)}
          title="Siguiente"
        >
          <span className="material-symbols-outlined" style={{ fontSize: 14 }}>
            chevron_right
          </span>
        </button>
      </div>
    </div>
  );
}