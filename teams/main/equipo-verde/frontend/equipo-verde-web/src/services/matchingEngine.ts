import { EvidenceRecord } from '../types/evidence';
import { MatchConfidence } from '../types/balistica';

export interface MatchingDetail {
  field: string;
  sourceValue: string | number;
  targetValue: string | number;
  isMatch: boolean;
  scoreContribution: number;
  justification: string;
}

export interface MatchResult {
  score: number;
  confidence: MatchConfidence;
  details: MatchingDetail[];
  isViable: boolean; // Si es false, el filtro estricto lo descartó (ej. distinto calibre)
}

/**
 * Motor de Cotejo Forense (Matching Engine)
 * Responsable de calcular la similitud matemática entre dos evidencias balísticas
 * basándose en principios de identificación de armas de fuego.
 */
export class MatchingEngine {
  // Pesos asignados a cada característica basada en su relevancia identificativa (Total: 100)
  private static WEIGHTS = {
    ESTRIAS: 70,    // Alto valor identificativo de clase
    TWIST: 10,      // Alto valor identificativo de clase
    PERCUSSION: 10, // Valor identificativo medio
    MARCA: 10       // Valor identificativo bajo (puede variar o ser genérica)
  };

  /**
   * Compara dos evidencias y devuelve un resultado matemático detallado.
   * @param source Evidencia de origen (ej. la encontrada en la escena)
   * @param target Evidencia objetivo (ej. una en la base de datos)
   * @returns Resultado detallado del cotejo
   */
  public static compare(source: EvidenceRecord, target: EvidenceRecord): MatchResult {
    const details: MatchingDetail[] = [];
    let totalScore = 0;

    // 1. FILTRO ESTRICTO (Hard Filter): Calibre
    // En balística forense, si el calibre es diferente, es físicamente imposible
    // que la misma arma haya percutido ambos proyectiles/vainas.
    const calibreMatch = source.calibre.toLowerCase().trim() === target.calibre.toLowerCase().trim();
    details.push({
      field: 'Calibre',
      sourceValue: source.calibre,
      targetValue: target.calibre,
      isMatch: calibreMatch,
      scoreContribution: 0,
      justification: calibreMatch 
        ? 'El diámetro y tipo de munición son compatibles, permitiendo el cotejo del resto de características.' 
        : 'Incompatibilidad física: Las evidencias provienen de armas con recámaras de diferentes dimensiones.'
    });

    if (!calibreMatch) {
      return { score: 0, confidence: 'Baja', details, isViable: false };
    }

    // 2. COMPARACIÓN DE CLASE: Número de Estrías (40%)
    const estriasMatch = source.estrias === target.estrias;
    if (estriasMatch) totalScore += this.WEIGHTS.ESTRIAS;
    details.push({
      field: 'Número de Estrías',
      sourceValue: source.estrias,
      targetValue: target.estrias,
      isMatch: estriasMatch,
      scoreContribution: estriasMatch ? this.WEIGHTS.ESTRIAS : 0,
      justification: estriasMatch
        ? 'Coincidencia en el patrón de estriado del cañón (marcas de clase idénticas).'
        : 'Divergencia estructural: El número de campos y estrías dejados por el cañón difieren.'
    });

    // 3. COMPARACIÓN DE CLASE: Dirección de Giro / Twist (30%)
    const twistMatch = source.twist === target.twist;
    if (twistMatch) totalScore += this.WEIGHTS.TWIST;
    details.push({
      field: 'Dirección de Giro (Twist)',
      sourceValue: source.twist,
      targetValue: target.twist,
      isMatch: twistMatch,
      scoreContribution: twistMatch ? this.WEIGHTS.TWIST : 0,
      justification: twistMatch
        ? 'Coincidencia balística interior: El paso de hélice (dextrógiro/levógiro) concuerda.'
        : 'Divergencia balística: La cinemática de rotación impresa en el proyectil es opuesta.'
    });

    // 4. COMPARACIÓN DE CLASE: Tipo de Percusión (20%)
    const percussionMatch = source.percussion === target.percussion;
    if (percussionMatch) totalScore += this.WEIGHTS.PERCUSSION;
    details.push({
      field: 'Tipo de Percusión',
      sourceValue: source.percussion,
      targetValue: target.percussion,
      isMatch: percussionMatch,
      scoreContribution: percussionMatch ? this.WEIGHTS.PERCUSSION : 0,
      justification: percussionMatch
        ? 'El mecanismo de disparo (central/anular) golpea el fulminante en la misma zona geométrica.'
        : 'Diferencia en el sistema de aguja percutora, indicando familias de armas distintas.'
    });

    // 5. COMPARACIÓN SECUNDARIA: Marca del fabricante (10%)
    // La marca se compara parcialmente, ya que puede estar registrada de formas ligeramente diferentes
    const marcaMatch = source.marca.toLowerCase().includes(target.marca.toLowerCase()) || 
                       target.marca.toLowerCase().includes(source.marca.toLowerCase());
    if (marcaMatch) totalScore += this.WEIGHTS.MARCA;
    details.push({
      field: 'Marca / Fabricante',
      sourceValue: source.marca,
      targetValue: target.marca,
      isMatch: marcaMatch,
      scoreContribution: marcaMatch ? this.WEIGHTS.MARCA : 0,
      justification: marcaMatch
        ? 'Compatibilidad de manufactura: Las huellas de mecanizado (brochado/martillado) provienen del mismo fabricante.'
        : 'Fabricantes diferentes detectados. (Nota: esto no descarta modificaciones de piezas (aftermarket)).'
    });

    // 6. CÁLCULO DE CONFIANZA
    let confidence: MatchConfidence = 'Baja';
    if (totalScore >= 85) {
      confidence = 'Alta';
    } else if (totalScore >= 50) {
      confidence = 'Media';
    }

    return {
      score: totalScore,
      confidence,
      details,
      isViable: true
    };
  }
}
