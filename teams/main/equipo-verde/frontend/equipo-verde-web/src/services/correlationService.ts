import { EvidenceRecord } from '../types/evidence';
import { MatchingEngine, MatchResult } from './matchingEngine';

export interface CorrelationResult {
  targetEvidence: EvidenceRecord;
  matchInfo: MatchResult;
}

export interface BackendCorrelationResult {
  idBullet: number;
  caseFile: string;
  manufacturer: string;
  matchScore: number;
  breakdown: {
    striaeMatched: boolean;
    twistMatched: boolean;
    percussionMatched: boolean;
    brandMatched: boolean;
    validMatchesCount: number;
    comparisonImageBase64: string;
  };
}

export interface CorrelationPageResponse {
  content: BackendCorrelationResult[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

/**
 * Servicio de Correlación
 * Responsable de orquestar la búsqueda de patrones comparando una evidencia
 * origen contra toda la base de datos histórica.
 */
export class CorrelationService {
  
  /**
   * Ejecuta el análisis de correlación iterando sobre el dataset proporcionado localmente.
   * (Método legado/simulado)
   */
  public static runAnalysis(source: EvidenceRecord, database: EvidenceRecord[]): CorrelationResult[] {
    const results: CorrelationResult[] = [];

    for (const target of database) {
      if (source.id === target.id) continue;
      const matchInfo = MatchingEngine.compare(source, target);
      if (matchInfo.isViable && matchInfo.score > 0) {
        results.push({ targetEvidence: target, matchInfo });
      }
    }
    results.sort((a, b) => b.matchInfo.score - a.matchInfo.score);
    return results;
  }

  /**
   * Ejecuta la correlación llamando al API real con motor OpenCV.
   */
  public static async runBackendAnalysis(
    sourceId: number,
    database: EvidenceRecord[],
    page = 0,
    size = 50
  ): Promise<CorrelationResult[]> {
    const res = await fetch(`/api/v1/correlate/${sourceId}?page=${page}&size=${size}`, { method: 'POST' });
    if (!res.ok) throw new Error(`Error en correlación del backend: ${res.status}`);
    
    const data: CorrelationPageResponse = await res.json();
    const results: CorrelationResult[] = [];
    
    for (const item of data.content) {
      // Evitamos compararlo contra sí mismo si viniera en la respuesta
      if (item.idBullet === sourceId) continue;

      let target = database.find(e => e.id === String(item.idBullet));
      if (!target) {
        target = {
          id: String(item.idBullet),
          createdAt: '',
          expediente: item.caseFile,
          calibre: 'N/A',
          estrias: 'N/A',
          twist: 'NONE',
          percussion: 'CENTRAL',
          marca: item.manufacturer,
          previewUrl: null
        };
      }
      
      const confidence = item.matchScore >= 80 ? 'Alta' : (item.matchScore >= 50 ? 'Media' : 'Baja');
      
      const reasons = [];
      if (item.breakdown.striaeMatched) reasons.push('Estrías coinciden');
      if (item.breakdown.twistMatched) reasons.push('Giro coinciden');
      if (item.breakdown.percussionMatched) reasons.push('Percusión coincide');
      if (item.breakdown.brandMatched) reasons.push('Marca coincide');
      reasons.push(`OpenCV: ${item.breakdown.validMatchesCount} puntos clave ORB emparejados`);
      
      results.push({
        targetEvidence: target,
        matchInfo: {
          isViable: true,
          score: Math.round(item.matchScore),
          confidence,
          reasons
        }
      });
    }
    
    results.sort((a, b) => b.matchInfo.score - a.matchInfo.score);
    return results;
  }
}
