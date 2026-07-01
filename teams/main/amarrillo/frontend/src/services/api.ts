import axios, { AxiosInstance } from 'axios';
import type {
  Persona, Vehiculo, Ubicacion, Suceso, Alerta, Vinculo,
  Relacion, UserSession, GrafoData, EstadoAlerta, PersonaDesaparecida,
  MensajeChat, RespuestaIA, FotoDesaparecida
} from '../types';
import { esSSO } from './authMode';
import { getAccessToken } from './ssoAuth';


const API_BASE = (import.meta.env.VITE_API_URL || '') + '/api/v1';

const api: AxiosInstance = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
});

// Interceptor: adjunta el token JWT en cada peticion (segun el modo de auth)
api.interceptors.request.use((config) => {
  const token = esSSO()
    ? getAccessToken()                       // SSO: token en memoria
    : localStorage.getItem('nexo_token');    // Local: token en localStorage
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ---- Auth ----
export const authService = {
  login: async (username: string, password: string): Promise<UserSession> => {
    const { data } = await api.post('/auth/login', { username, password });
    return data;
  },
};

// ---- Personas ----
export const personaService = {
  listar: async (): Promise<Persona[]> => (await api.get('/personas')).data,
  obtener: async (id: number): Promise<Persona> => (await api.get(`/personas/${id}`)).data,
  crear:  async (p: Persona): Promise<Persona> => (await api.post('/personas', p)).data,
  actualizar: async (id: number, p: Persona): Promise<Persona> => (await api.put(`/personas/${id}`, p)).data,
  eliminar: async (id: number): Promise<void> => { await api.delete(`/personas/${id}`); },
  intermediarios: async (victimaId: number, sospechosoId: number): Promise<Persona[]> =>
    (await api.get('/personas/intermediarios', { params: { victimaId, sospechosoId } })).data,
};

// ---- Vehiculos ----
export const vehiculoService = {
  listar: async (estado?: string): Promise<Vehiculo[]> =>
    (await api.get('/vehiculos', { params: estado ? { estado } : {} })).data,
  obtener: async (id: number): Promise<Vehiculo> => (await api.get(`/vehiculos/${id}`)).data,
  crear: async (v: Vehiculo): Promise<Vehiculo> => (await api.post('/vehiculos', v)).data,
  actualizar: async (id: number, v: Vehiculo): Promise<Vehiculo> => (await api.put(`/vehiculos/${id}`, v)).data,
  cambiarEstado: async (id: number, estado: string): Promise<Vehiculo> =>
    (await api.patch(`/vehiculos/${id}/estado`, { estado })).data,
  eliminar: async (id: number): Promise<void> => { await api.delete(`/vehiculos/${id}`); },
};

// ---- Ubicaciones ----
export const ubicacionService = {
  listar: async (): Promise<Ubicacion[]> => (await api.get('/ubicaciones')).data,
  crear: async (u: Ubicacion): Promise<Ubicacion> => (await api.post('/ubicaciones', u)).data,
  eliminar: async (id: number): Promise<void> => { await api.delete(`/ubicaciones/${id}`); },
};

// ---- Sucesos ----
export const sucesoService = {
  listar: async (tipo?: string): Promise<Suceso[]> =>
    (await api.get('/sucesos', { params: tipo ? { tipo } : {} })).data,
  crear: async (s: Suceso): Promise<Suceso> => (await api.post('/sucesos', s)).data,
  eliminar: async (id: number): Promise<void> => { await api.delete(`/sucesos/${id}`); },
};

// ---- Robo completo (orquestador transaccional) ----
export const roboCompletoService = {
  registrar: async (datos: any): Promise<{ sucesoId: number; mensaje: string }> =>
    (await api.post('/robo-completo', datos)).data,
};

// ---- Modus Operandi (catálogo) ----
export const modusService = {
  listar: async (): Promise<{ id: number; codigo: string; etiqueta: string; descripcion?: string }[]> =>
    (await api.get('/modus')).data,
};

// ---- Relaciones ----
export const relacionService = {
  listar: async (): Promise<Relacion[]> => (await api.get('/relaciones')).data,
  crear: async (r: Relacion): Promise<Relacion> => (await api.post('/relaciones', r)).data,
};

// ---- Alertas ----
export const alertaService = {
  listar: async (pendientes = false): Promise<Alerta[]> =>
    (await api.get('/alertas', { params: pendientes ? { pendientes: true } : {} })).data,
  cambiarEstado: async (id: number, estado: EstadoAlerta): Promise<Alerta> =>
    (await api.patch(`/alertas/${id}/estado`, { estado })).data,
};

// ---- Motor Red Thread ----
export const engineService = {
  ejecutarTodo: async () => (await api.post('/engine/ejecutar-todo')).data,
  nodoLogistico: async () => (await api.post('/engine/nodo-logistico')).data,
  escolta: async () => (await api.post('/engine/escolta')).data,
  circulo: async () => (await api.post('/engine/circulo-confianza')).data,
  modus: async () => (await api.post('/engine/modus-operandi')).data,
};

export const configMotorService = {
  obtener: () => api.get('/config/motor').then(r => r.data),
  guardar: (config: any) => api.put('/config/motor', config).then(r => r.data),
  restaurarDefaults: () => api.post('/config/motor/default').then(r => r.data),
};

// ---- Grafo ----
export const grafoService = {
  completo: async (): Promise<GrafoData> => (await api.get('/grafo/completo')).data,
};

// ---- Vinculos ----
export const vinculoService = {
  listar: async (): Promise<Vinculo[]> => (await api.get('/vinculos')).data,
};

// ---- Personas Desaparecidas ----
export const desaparecidaService = {
  listar: async (params?: { estado?: string; prioridad?: string }): Promise<PersonaDesaparecida[]> =>
    (await api.get('/desaparecidas', { params })).data,

  obtener: async (id: number): Promise<PersonaDesaparecida> =>
    (await api.get(`/desaparecidas/${id}`)).data,

  crear: async (p: PersonaDesaparecida): Promise<PersonaDesaparecida> =>
    (await api.post('/desaparecidas', p)).data,

  actualizar: async (id: number, p: PersonaDesaparecida): Promise<PersonaDesaparecida> =>
    (await api.put(`/desaparecidas/${id}`, p)).data,

  cambiarEstado: async (id: number, estado: string): Promise<PersonaDesaparecida> =>
    (await api.patch(`/desaparecidas/${id}/estado`, { estado })).data,

  subirFoto: async (id: number, archivo: File): Promise<{ url: string }> => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    const { data } = await api.post(`/desaparecidas/${id}/foto`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  },

  eliminar: async (id: number): Promise<void> => {
    await api.delete(`/desaparecidas/${id}`);
  },

  estadisticas: async (): Promise<{
    total: number; buscadas: number; encontradasVivas: number;
    encontradasFallecidas: number; criticas: number;
  }> => (await api.get('/desaparecidas/estadisticas')).data,

  cercanas: async (lat: number, lng: number, radio = 5000): Promise<PersonaDesaparecida[]> =>
    (await api.get('/desaparecidas/cercanas', { params: { lat, lng, radioMetros: radio } })).data,

    // ---- Múltiples fotos ----
  listarFotos: async (id: number): Promise<FotoDesaparecida[]> =>
    (await api.get(`/desaparecidas/${id}/fotos`)).data,

  agregarFoto: async (id: number, archivo: File): Promise<FotoDesaparecida> => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    const { data } = await api.post(`/desaparecidas/${id}/fotos`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  },

  eliminarFoto: async (id: number, fotoId: number): Promise<void> => {
    await api.delete(`/desaparecidas/${id}/fotos/${fotoId}`);
  },

  marcarFotoPrincipal: async (id: number, fotoId: number): Promise<void> => {
    await api.patch(`/desaparecidas/${id}/fotos/${fotoId}/principal`);
  },
};

// ---- IA ----
export const iaService = {
  estado: async (): Promise<{ configurada: boolean; modelo: string }> =>
    (await api.get('/ia/estado')).data,

  chat: async (
    historial: MensajeChat[],
    pregunta: string,
    incluirContexto = true
  ): Promise<RespuestaIA> =>
    (await api.post('/ia/chat', { historial, pregunta, incluirContexto })).data,

  zonasBusqueda: async (id: number): Promise<RespuestaIA> =>
    (await api.post(`/ia/zonas-busqueda/${id}`)).data,

  similitudDesapariciones: async (): Promise<RespuestaIA> =>
    (await api.post('/ia/similitud-desapariciones')).data,

  reporte: async (tipo: 'desaparecida' | 'suceso' | 'alerta', id: number): Promise<RespuestaIA> =>
    (await api.post(`/ia/reporte/${tipo}/${id}`)).data,

  clasificarModus: async (descripcion: string): Promise<{ codigo: string; etiqueta: string }> =>
    (await api.post('/ia/clasificar-modus', { descripcion })).data,
};

export default api;
