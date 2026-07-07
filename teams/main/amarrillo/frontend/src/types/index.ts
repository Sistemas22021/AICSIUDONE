export type RolPersona = 'VICTIMA' | 'SOSPECHOSO' | 'TESTIGO' | 'PROPIETARIO' | 'INTERMEDIARIO';
export type EstadoVehiculo = 'NORMAL' | 'ROBADO' | 'RECUPERADO' | 'DESAPARECIDO' | 'BAJO_OBSERVACION' | 'VEHICULO_APOYO';
export type TipoUbicacion = 'TALLER' | 'GALPON' | 'TERRENO_BALDIO' | 'DOMICILIO' | 'CAJERO' | 'TRANSPORTE_PUBLICO' | 'COMERCIO' | 'OTRO';
export type TipoSuceso = 'ROBO_VEHICULO' | 'DESAPARICION' | 'AVISTAMIENTO' | 'TRANSACCION';
export type TipoAlerta = 'NODO_LOGISTICO' | 'VEHICULO_APOYO' | 'INTERMEDIARIO' | 'POI_DESAPARICION' | 'MODUS_OPERANDI';
export type NivelRiesgo = 'BAJO' | 'MEDIO' | 'ALTO' | 'CRITICO';
export type EstadoAlerta = 'PENDIENTE' | 'EN_REVISION' | 'CONFIRMADA' | 'DESCARTADA';
export type TipoRelacion = 'FAMILIAR' | 'AMIGO' | 'LABORAL' | 'CONTACTO_TELEFONICO' | 'REDES_SOCIALES' | 'OTRO';
export type EstadoDesaparicion = 'BUSCADA' | 'ENCONTRADA_VIVA' | 'ENCONTRADA_FALLECIDA' | 'ARCHIVADA';
export type PrioridadDesaparicion = 'CRITICA' | 'ALTA' | 'MEDIA' | 'BAJA';

export interface Persona {
  id?: number;
  documento: string;
  nombre: string;
  apellido: string;
  alias?: string;
  fechaNacimiento?: string;
  rol: RolPersona;
  telefono?: string;
  estado?: string;
}

export interface Vehiculo {
  id?: number;
  placa: string;
  marca: string;
  modelo: string;
  anio?: number;
  color?: string;
  estado?: EstadoVehiculo;
  propietario?: Persona | null;
  chasis?: string;
  declaracion?: string;
}

export interface Ubicacion {
  id?: number;
  direccion?: string;
  latitud: number;
  longitud: number;
  tipo?: TipoUbicacion;
  nodoSospechoso?: boolean;
}

export interface Suceso {
  id?: number;
  tipo: TipoSuceso;
  fechaHora: string;
  ubicacion?: Ubicacion | null;
  ubicacionUltima?: Ubicacion | null;
  vehiculo?: Vehiculo | null;
  victima?: Persona | null;
  modusOperandi?: string;
  descripcion?: string;
}

export interface Alerta {
  id: number;
  tipo: TipoAlerta;
  titulo: string;
  descripcion?: string;
  nivelRiesgo: NivelRiesgo;
  vinculoId?: number;
  estado: EstadoAlerta;
  creadaEn: string;
}

export interface Vinculo {
  id: number;
  tipoOrigen: string;
  idOrigen: number;
  tipoDestino: string;
  idDestino: number;
  reglaDetectada: string;
  score: number;
  detectadoEn: string;
}

export interface Relacion {
  id?: number;
  personaA: Persona;
  personaB: Persona;
  tipoRelacion: TipoRelacion;
  peso?: number;
}

export interface UserSession {
  token: string;
  username: string;
  nombreCompleto: string;
  rol: string;
}

export interface GrafoData {
  nodes: Array<{ data: { id: string; label: string; tipo: string; subtipo?: string; sospechoso?: boolean } }>;
  edges: Array<{ data: { id: string; source: string; target: string; regla: string; score?: number; tipo: string } }>;
}

export interface PersonaDesaparecida {
  id?: number;
  documento: string;
  nombre: string;
  apellido: string;
  alias?: string;
  fechaNacimiento?: string;
  genero?: string;
  estaturaCm?: number;
  pesoKg?: number;
  contextura?: string;
  colorCabello?: string;
  colorOjos?: string;
  señasParticulares?: string;
  ropaAlDesaparecer?: string;
  fotoUrl?: string;        // foto principal (compatibilidad)
  fotos?: FotoDesaparecida[];  // NUEVO: todas las fotos
  fechaDesaparicion: string;
  ultimaUbicacion?: Ubicacion | null;
  circunstancias?: string;
  reportanteNombre?: string;
  reportanteTelefono?: string;
  reportanteRelacion?: string;
  estado: EstadoDesaparicion;
  prioridad: PrioridadDesaparicion;
  analisisIA?: string;
  zonasBusquedaIA?: string;
  fechaResolucion?: string;
  creadoEn?: string;
  actualizadoEn?: string;
}

// ========== IA ==========
export interface MensajeChat {
  role: 'user' | 'assistant';
  content: string;
}

export interface RespuestaIA {
  contenido: string;
  modelo: string;
  tokensEntrada?: number;
  tokensSalida?: number;
  durationMs?: number;
}

export interface FotoDesaparecida {
  id?: number;
  url: string;
  orden: number;
  principal: boolean;
  descripcion?: string;
  creadoEn?: string;
}