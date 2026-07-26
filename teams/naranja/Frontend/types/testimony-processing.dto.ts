// Enums recomendados para mantener el tipado fuerte en TypeScript
export type PersonRole = 'VICTIMA' | 'IMPUTADO' | 'TESTIGO' | 'DENUNCIANTE'; // Ajusta según los roles de tu sistema

export interface PersonData {
  encontrada: boolean;
  id?: number;
  nombre?: string;
  apellido?: string;
  documento?: string;
  rol?: PersonRole | string;
}

export interface ExtractedFields {
  tipoSugerido?: string;
  modusOperandi?: string;
  descripcion?: string;
  ubicacionMencionada?: string;
  personasMencionadas?: string;
}

// DTO/Interfaz principal de respuesta de la IA
export interface TestimonyProcessingResponse {
  persona: PersonData;
  camposExtraidos: ExtractedFields;
  fecha: string;
  textoOriginal: string;
}