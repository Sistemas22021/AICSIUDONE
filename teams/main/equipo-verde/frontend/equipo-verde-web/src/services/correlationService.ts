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
  ): Promise<{ results: CorrelationResult[]; totalElements: number }> {
    const res = await fetch(`/api/v1/correlate/${sourceId}?page=${page}&size=${size}`, { method: 'POST' });
    if (!res.ok) throw new Error(`Error en correlación del backend: ${res.status}`);
    
    const data: CorrelationPageResponse = await res.json();
    const results: CorrelationResult[] = [];
    
    // Retrieve source evidence to use correct sourceValues in details
    const sourceEvidence = database.find(e => e.id === String(sourceId));
    for (const item of data.content) {
      // Evitamos compararlo contra sí mismo si viniera en la respuesta
      if (item.idBullet === sourceId) continue;

      let target: EvidenceRecord;
      const found = database.find(e => e.id === String(item.idBullet));
      if (found) {
        target = found;
      } else {
        target = {
          id: String(item.idBullet),
          createdAt: '',
          expediente: item.caseFile,
          calibre: 'N/A',
          estrias: 'N/A',
          twist: 'NONE' as any,
          percussion: 'CENTRAL' as any,
          marca: item.manufacturer,
          previewUrl: null
        };
      }
      
      const confidence = item.matchScore >= 80 ? 'Alta' : (item.matchScore >= 50 ? 'Media' : 'Baja');
      
      // Crear los detalles para que ComparisonViewerModal funcione
      const details = [];
      
      details.push({
        field: 'Análisis de Visión Artificial (OpenCV ORB)',
        sourceValue: 'Imagen procesada',
        targetValue: 'Imagen almacenada',
        isMatch: item.breakdown.striaeMatched,
        scoreContribution: item.breakdown.striaeMatched ? 40 : 0,
        justification: item.breakdown.striaeMatched 
          ? `Motor OpenCV detectó coincidencia de micro-estrías con ${item.breakdown.validMatchesCount} puntos clave ORB emparejados.`
          : `Visión artificial no detectó suficientes puntos clave de similitud (${item.breakdown.validMatchesCount} emparejados).`
      });

      details.push({
        field: 'Dirección de Giro (Twist)',
        sourceValue: sourceEvidence?.twist ?? 'N/A',
        targetValue: target.twist,
        isMatch: item.breakdown.twistMatched,
        scoreContribution: item.breakdown.twistMatched ? 30 : 0,
        justification: item.breakdown.twistMatched ? 'Giro coincidente (evaluación backend)' : 'Giro divergente'
      });

      details.push({
        field: 'Tipo de Percusión',
        sourceValue: sourceEvidence?.percussion ?? 'N/A',
        targetValue: target.percussion,
        isMatch: item.breakdown.percussionMatched,
        scoreContribution: item.breakdown.percussionMatched ? 20 : 0,
        justification: item.breakdown.percussionMatched ? 'Mecanismos de disparo compatibles' : 'Mecanismos de disparo diferentes'
      });

      details.push({
        field: 'Marca / Fabricante',
        sourceValue: sourceEvidence?.marca ?? 'N/A',
        targetValue: target.marca,
        isMatch: item.breakdown.brandMatched,
        scoreContribution: item.breakdown.brandMatched ? 10 : 0,
        justification: item.breakdown.brandMatched ? 'Fabricante compatible' : 'Fabricante distinto'
      });
      
      results.push({
        targetEvidence: target,
        matchInfo: {
          isViable: true,
          score: Math.round(item.matchScore),
          confidence,
          details,
          comparisonImageBase64: item.breakdown.comparisonImageBase64
        } as any // Forzamos el tipo por si TypeScript se queja del tipo local
      });
    }
    
    results.sort((a, b) => b.matchInfo.score - a.matchInfo.score);
    return { results, totalElements: data.totalElements };
  }
}
