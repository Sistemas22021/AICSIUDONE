import { EvidenceRecord } from '../types/evidence';
import { MatchingEngine, MatchResult } from './matchingEngine';

export interface CorrelationResult {
  targetEvidence: EvidenceRecord;
  matchInfo: MatchResult;
}

/**
 * Servicio de Correlación
 * Responsable de orquestar la búsqueda de patrones comparando una evidencia
 * origen contra toda la base de datos histórica.
 */
export class CorrelationService {
  
  /**
   * Ejecuta el análisis de correlación iterando sobre el dataset proporcionado.
   * Excluye automáticamente la misma evidencia (si ya está en la BD) y descarta
   * aquellas que el motor indique como no viables (ej. calibre distinto).
   * 
   * @param source Evidencia a analizar
   * @param database Base de datos completa de evidencias históricas
   * @returns Lista ordenada de mayores coincidencias
   */
  public static runAnalysis(source: EvidenceRecord, database: EvidenceRecord[]): CorrelationResult[] {
    const results: CorrelationResult[] = [];

    for (const target of database) {
      // No comparamos la evidencia contra sí misma
      if (source.id === target.id) continue;

      const matchInfo = MatchingEngine.compare(source, target);

      // Solo incluimos resultados viables (que pasaron los filtros estrictos)
      // y que tengan al menos alguna similitud básica (> 0 score) para no ensuciar la vista
      if (matchInfo.isViable && matchInfo.score > 0) {
        results.push({
          targetEvidence: target,
          matchInfo
        });
      }
    }

    // Ordenar jerárquicamente de mayor a menor probabilidad matemática
    results.sort((a, b) => b.matchInfo.score - a.matchInfo.score);

    return results;
  }
}
