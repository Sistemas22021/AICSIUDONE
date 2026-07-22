import { ExpedienteDTO, ExpedientePageResponse } from '../types/expediente';

const BASE_URL = '/api/v1/expedients';

export class BackendApiError extends Error {
  status: number;
  backendMessage: string;

  constructor(status: number, backendMessage: string) {
    super(backendMessage);
    this.status = status;
    this.backendMessage = backendMessage;
    this.name = 'BackendApiError';
  }
}

async function throwBackendError(res: Response): Promise<never> {
  let backendMessage = `Error del servidor (${res.status})`;
  try {
    const body = await res.json();
    if (body?.message) backendMessage = body.message;
    else if (body?.error) backendMessage = body.error;
  } catch {
    // No pudo leer JSON
  }
  throw new BackendApiError(res.status, backendMessage);
}

export const getExpedientes = async (keyword: string = '', page: number = 0, size: number = 10): Promise<ExpedientePageResponse> => {
  const params = new URLSearchParams({ keyword, page: String(page), size: String(size) });
  const response = await fetch(`${BASE_URL}?${params.toString()}`);
  if (!response.ok) await throwBackendError(response);
  return response.json();
};

export const getExpedienteById = async (id: number): Promise<ExpedienteDTO> => {
  const response = await fetch(`${BASE_URL}/${id}`);
  if (!response.ok) await throwBackendError(response);
  return response.json();
};

export const createExpediente = async (expediente: ExpedienteDTO): Promise<ExpedienteDTO> => {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(expediente)
  });
  if (!response.ok) await throwBackendError(response);
  return response.json();
};

export const updateExpediente = async (id: number, expediente: ExpedienteDTO): Promise<ExpedienteDTO> => {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(expediente)
  });
  if (!response.ok) await throwBackendError(response);
  return response.json();
};

export const deleteExpediente = async (id: number): Promise<void> => {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: 'DELETE'
  });
  if (!response.ok) await throwBackendError(response);
};
