/**
 * Convierte un array de objetos en un CSV descargable.
 */
export function exportarCSV<T extends Record<string, any>>(
  datos: T[],
  nombreArchivo: string,
  columnas?: { campo: keyof T; etiqueta: string }[]
) {
  if (datos.length === 0) {
    alert('No hay datos para exportar');
    return;
  }

  const cols =
    columnas || (Object.keys(datos[0]).map((k) => ({ campo: k as keyof T, etiqueta: k })));

  const cabecera = cols.map((c) => `"${c.etiqueta}"`).join(',');

  const filas = datos.map((row) =>
    cols
      .map((c) => {
        const v = row[c.campo];
        if (v == null) return '""';
        if (typeof v === 'object') return `"${JSON.stringify(v).replace(/"/g, '""')}"`;
        return `"${String(v).replace(/"/g, '""')}"`;
      })
      .join(',')
  );

  const csv = '\ufeff' + [cabecera, ...filas].join('\n'); // BOM para Excel

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `${nombreArchivo}_${new Date().toISOString().slice(0, 10)}.csv`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}