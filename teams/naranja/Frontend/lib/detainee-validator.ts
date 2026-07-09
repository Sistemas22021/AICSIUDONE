export interface DetaineeData {
  expediente: string;
  cedula: string;
}

export async function validateDetaineeData(data: DetaineeData): Promise<{ success: boolean; message?: string }> {
  // NOTA DE ARQUITECTURA: Aquí se implementará la llamada al endpoint externo por el otro equipo.
  // Por ahora, aceptamos la información de manera libre tal como se requiere.
  
  if (!data.expediente.trim() || !data.cedula.trim()) {
    return { success: false, message: "El número de expediente y la cédula son obligatorios." };
  }

  return { success: true };
}