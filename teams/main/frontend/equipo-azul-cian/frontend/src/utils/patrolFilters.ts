import type { Patrol } from '../types/patrol';

/**
 * filterPatrols
 *
 * Filtra un array de patrullas según un término de búsqueda (por código u
 * oficial) y un filtro de estado. Función pura: no tiene efectos secundarios
 * y siempre devuelve el mismo resultado para los mismos argumentos.
 *
 * @param patrols     - Lista completa de patrullas
 * @param searchTerm  - Texto a buscar en código o nombre del oficial
 * @param statusFilter - Estado a filtrar ('ALL' para no filtrar por estado)
 * @returns           - Subconjunto de patrullas que cumplen ambos criterios
 */
export function filterPatrols(
  patrols: Patrol[],
  searchTerm: string,
  statusFilter: string
): Patrol[] {
  return patrols.filter((patrol) => {
    const term = searchTerm.toLowerCase();
    const matchesSearch =
      patrol.code.toLowerCase().includes(term) ||
      patrol.officerName.toLowerCase().includes(term);

    const matchesStatus =
      statusFilter === 'ALL' || patrol.status === statusFilter;

    return matchesSearch && matchesStatus;
  });
}
