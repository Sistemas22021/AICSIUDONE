import axios from 'axios';

const NEXO_API_URL = 'https://nexo-criminal-api-final.onrender.com/api/v1';

export interface NexoPersona {
  id: number;
  cedula: string;
  nombre: string;
  apellido: string;
  rol: string;
  // Añade otros campos básicos si es necesario
  dni?: string;
  identificacion?: string;
  documento?: string;
}

class NexoService {
  async getSospechosos(): Promise<NexoPersona[]> {
    try {
      const response = await axios.get(`${NEXO_API_URL}/personas?rol=SOSPECHOSO`);
      return response.data;
    } catch (error) {
      console.error('Error obteniendo sospechosos', error);
      throw new Error('No se pudo obtener la lista de sospechosos');
    }
  }
}

export const nexoService = new NexoService();
