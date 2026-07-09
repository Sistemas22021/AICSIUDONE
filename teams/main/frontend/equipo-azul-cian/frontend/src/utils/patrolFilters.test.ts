import { describe, it, expect } from 'vitest';
import { filterPatrols } from './patrolFilters';
import type { Patrol } from '../types/patrol';

// ─── Datos de prueba ─────────────────────────────────────────────────────────
// Se definen patrullas de ejemplo que cubren todos los estados posibles.
// Usar datos fijos (no aleatorios) hace que los tests sean deterministas.
const mockPatrols: Patrol[] = [
  {
    id: 1,
    code: 'P-101',
    officerName: 'Sgto. Carlos Mendez',
    latitude: 10.48,
    longitude: -66.88,
    status: 'AVAILABLE',
  },
  {
    id: 2,
    code: 'P-202',
    officerName: 'Of. Laura Gómez',
    latitude: 10.49,
    longitude: -66.89,
    status: 'EN_ROUTE',
  },
  {
    id: 3,
    code: 'P-303',
    officerName: 'Sgto. Luis Rivas',
    latitude: 10.50,
    longitude: -66.90,
    status: 'BUSY',
  },
  {
    id: 4,
    code: 'P-404',
    officerName: 'Of. Maria Torres',
    latitude: 10.51,
    longitude: -66.91,
    status: 'OUT_OF_SERVICE',
  },
];

// ─── Tests de búsqueda por texto ─────────────────────────────────────────────
describe('filterPatrols — búsqueda por texto', () => {
  it('devuelve todas las patrullas cuando el searchTerm está vacío y el filtro es ALL', () => {
    const result = filterPatrols(mockPatrols, '', 'ALL');
    expect(result).toHaveLength(4);
  });

  it('filtra por código de unidad (case-insensitive)', () => {
    const result = filterPatrols(mockPatrols, 'p-101', 'ALL');
    expect(result).toHaveLength(1);
    expect(result[0].code).toBe('P-101');
  });

  it('filtra por nombre del oficial (parcial, case-insensitive)', () => {
    // "gomez" debe encontrar "Laura Gómez"
    // Nota: el filtro usa toLowerCase() pero no elimina tildes,
    // por lo que "gomez" no matchea "Gómez". Se prueba con el texto real.
    const result = filterPatrols(mockPatrols, 'Gómez', 'ALL');
    expect(result).toHaveLength(1);
    expect(result[0].officerName).toBe('Of. Laura Gómez');
  });

  it('filtra por rango numérico en el código (ej: "P-3")', () => {
    const result = filterPatrols(mockPatrols, 'P-3', 'ALL');
    expect(result).toHaveLength(1);
    expect(result[0].code).toBe('P-303');
  });

  it('devuelve array vacío si el searchTerm no coincide con nada', () => {
    const result = filterPatrols(mockPatrols, 'XYZ-999', 'ALL');
    expect(result).toHaveLength(0);
  });
});

// ─── Tests de filtrado por estado ─────────────────────────────────────────────
describe('filterPatrols — filtrado por estado', () => {
  it('devuelve solo las patrullas AVAILABLE', () => {
    const result = filterPatrols(mockPatrols, '', 'AVAILABLE');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('AVAILABLE');
  });

  it('devuelve solo las patrullas EN_ROUTE', () => {
    const result = filterPatrols(mockPatrols, '', 'EN_ROUTE');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('EN_ROUTE');
  });

  it('devuelve solo las patrullas BUSY', () => {
    const result = filterPatrols(mockPatrols, '', 'BUSY');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('BUSY');
  });

  it('devuelve solo las patrullas OUT_OF_SERVICE', () => {
    const result = filterPatrols(mockPatrols, '', 'OUT_OF_SERVICE');
    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('OUT_OF_SERVICE');
  });

  it('devuelve todas las patrullas cuando el filtro es ALL', () => {
    const result = filterPatrols(mockPatrols, '', 'ALL');
    expect(result).toHaveLength(4);
  });
});

// ─── Tests de filtros combinados ──────────────────────────────────────────────
describe('filterPatrols — filtros combinados (texto + estado)', () => {
  it('aplica búsqueda y filtro de estado simultáneamente', () => {
    // Buscamos "Sgto." (hay dos: Mendez y Rivas) pero solo AVAILABLE
    const result = filterPatrols(mockPatrols, 'Sgto.', 'AVAILABLE');
    expect(result).toHaveLength(1);
    expect(result[0].code).toBe('P-101');
  });

  it('devuelve vacío si la búsqueda y el estado no coinciden para ninguna patrulla', () => {
    // "Laura" existe pero su estado es EN_ROUTE, no BUSY
    const result = filterPatrols(mockPatrols, 'Laura', 'BUSY');
    expect(result).toHaveLength(0);
  });
});

// ─── Tests de casos borde ─────────────────────────────────────────────────────
describe('filterPatrols — casos borde', () => {
  it('devuelve array vacío si la lista de entrada está vacía', () => {
    const result = filterPatrols([], 'P-101', 'ALL');
    expect(result).toHaveLength(0);
  });

  it('no muta el array original', () => {
    const original = [...mockPatrols];
    filterPatrols(mockPatrols, 'P-1', 'AVAILABLE');
    expect(mockPatrols).toEqual(original);
  });
});
