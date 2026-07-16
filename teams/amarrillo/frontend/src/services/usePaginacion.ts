import { useMemo, useState, useEffect } from 'react';

export function usePaginacion<T>(items: T[], porPagina = 10) {
  const [pagina, setPagina] = useState(1);

  const totalPaginas = Math.max(1, Math.ceil(items.length / porPagina));

  // Si cambia el filtro y la página queda fuera, volver a la 1
  useEffect(() => {
    if (pagina > totalPaginas) setPagina(1);
  }, [items.length, totalPaginas, pagina]);

  const visibles = useMemo(
    () => items.slice((pagina - 1) * porPagina, pagina * porPagina),
    [items, pagina, porPagina]
  );

  return {
    visibles,
    pagina,
    setPagina,
    totalPaginas,
    total: items.length,
    porPagina,
  };
}