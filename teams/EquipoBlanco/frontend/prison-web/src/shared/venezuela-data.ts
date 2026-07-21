// ─────────────────────────────────────────────────────────────────────────────
// venezuela-data.ts — Catálogos de datos venezolanos para el SIGP
// ─────────────────────────────────────────────────────────────────────────────

// ─── Delitos del Código Penal y COPP venezolano ──────────────────────────────
export const DELITOS_PENALES = [
  // Delitos contra las personas
  'Homicidio intencional',
  'Homicidio calificado',
  'Homicidio culposo',
  'Homicidio en riña',
  'Lesiones personales graves',
  'Lesiones personales leves',
  'Lesiones personales gravísimas',
  // Delitos contra la libertad
  'Secuestro',
  'Secuestro express',
  'Privación ilegítima de libertad',
  'Extorsión',
  'Tráfico de personas',
  // Delitos contra la propiedad
  'Robo a mano armada',
  'Robo agravado',
  'Hurto simple',
  'Hurto calificado',
  'Estafa',
  'Apropiación indebida',
  'Daños a la propiedad',
  // Delitos contra la integridad sexual
  'Violación',
  'Acto carnal con víctima especialmente vulnerable',
  'Abuso sexual',
  'Actos lascivos',
  // Delitos de drogas (LOPSJ)
  'Tráfico ilícito de drogas',
  'Posesión ilícita de drogas',
  'Distribución de drogas',
  'Legitimación de capitales (drogas)',
  // Delitos contra el orden público
  'Asociación para delinquir',
  'Terrorismo',
  'Financiamiento al terrorismo',
  // Delitos con armas
  'Porte ilícito de arma de fuego',
  'Tráfico de armas',
  // Delitos informáticos
  'Fraude electrónico',
  'Acceso indebido a sistemas',
  // Delitos contra la fe pública
  'Falsificación de documentos',
  'Uso de documento falso',
  // Delitos funcionariales
  'Corrupción',
  'Concusión',
  'Peculado doloso',
  'Malversación',
  // Otros
  'Otro delito',
] as const

export type DelitoPenal = (typeof DELITOS_PENALES)[number]

// ─── Circuitos Judiciales Penales de Venezuela ───────────────────────────────
export const CIRCUITOS_JUDICIALES = [
  // Caracas / Distrito Capital
  { value: 'DTTO_CAPITAL', label: 'Circuito Judicial Penal - Distrito Capital (Caracas)' },
  { value: 'MIRANDA', label: 'Circuito Judicial Penal del Estado Miranda' },
  // Región Occidental
  { value: 'ZULIA', label: 'Circuito Judicial Penal del Estado Zulia (Maracaibo)' },
  { value: 'FALCON', label: 'Circuito Judicial Penal del Estado Falcón (Coro)' },
  { value: 'LARA', label: 'Circuito Judicial Penal del Estado Lara (Barquisimeto)' },
  { value: 'MERIDA', label: 'Circuito Judicial Penal del Estado Mérida' },
  { value: 'TACHIRA', label: 'Circuito Judicial Penal del Estado Táchira (San Cristóbal)' },
  { value: 'TRUJILLO', label: 'Circuito Judicial Penal del Estado Trujillo' },
  { value: 'BARINAS', label: 'Circuito Judicial Penal del Estado Barinas' },
  { value: 'APURE', label: 'Circuito Judicial Penal del Estado Apure (San Fernando)' },
  // Región Central
  { value: 'ARAGUA', label: 'Circuito Judicial Penal del Estado Aragua (Maracay)' },
  { value: 'CARABOBO', label: 'Circuito Judicial Penal del Estado Carabobo (Valencia)' },
  { value: 'COJEDES', label: 'Circuito Judicial Penal del Estado Cojedes (San Carlos)' },
  { value: 'GUARICO', label: 'Circuito Judicial Penal del Estado Guárico (San Juan de los Morros)' },
  { value: 'PORTUGUESA', label: 'Circuito Judicial Penal del Estado Portuguesa (Guanare)' },
  { value: 'YARACUY', label: 'Circuito Judicial Penal del Estado Yaracuy (San Felipe)' },
  // Región Oriental
  { value: 'ANZOATEGUI', label: 'Circuito Judicial Penal del Estado Anzoátegui (Barcelona)' },
  { value: 'BOLIVAR', label: 'Circuito Judicial Penal del Estado Bolívar (Ciudad Bolívar)' },
  { value: 'MONAGAS', label: 'Circuito Judicial Penal del Estado Monagas (Maturín)' },
  { value: 'NUEVA_ESPARTA', label: 'Circuito Judicial Penal del Estado Nueva Esparta (La Asunción)' },
  { value: 'SUCRE', label: 'Circuito Judicial Penal del Estado Sucre (Cumaná)' },
  // Región Sur / Amazonia
  { value: 'AMAZONAS', label: 'Circuito Judicial Penal del Estado Amazonas (Puerto Ayacucho)' },
  { value: 'DELTA_AMACURO', label: 'Circuito Judicial Penal del Estado Delta Amacuro (Tucupita)' },
  // Vargas
  { value: 'LA_GUAIRA', label: 'Circuito Judicial Penal del Estado La Guaira (Vargas)' },
] as const

// ─── Estados y municipios de Venezuela ──────────────────────────────────────
export const ESTADOS_MUNICIPIOS: Record<string, string[]> = {
  'Distrito Capital': ['Libertador'],
  'Miranda': ['Acevedo', 'Andrés Bello', 'Baruta', 'Brión', 'Buroz', 'Carrizal', 'Chacao', 'Cristóbal Rojas', 'El Hatillo', 'Guaicaipuro', 'Independencia', 'Lander', 'Los Salias', 'Páez', 'Paz Castillo', 'Pedro Gual', 'Plaza', 'Simón Bolívar', 'Sucre', 'Urdaneta'],
  'Zulia': ['Baralt', 'Cabimas', 'Catatumbo', 'Colón', 'Francisco Javier Pulgar', 'Jesús Enrique Lossada', 'Jesús María Semprún', 'La Cañada de Urdaneta', 'Lagunillas', 'Machiques de Perijá', 'Mara', 'Maracaibo', 'Miranda', 'Rosario de Perijá', 'San Francisco', 'Santa Rita', 'Simón Bolívar', 'Sucre', 'Valmore Rodríguez'],
  'Caracas / Dtto. Capital': ['Libertador'],
  'Aragua': ['Bolívar', 'Camatagua', 'Francisco Linares Alcántara', 'Girardot', 'José Ángel Lamas', 'José Félix Ribas', 'José Rafael Revenga', 'Libertador', 'Mario Briceño Iragorry', 'Ocumare de la Costa de Oro', 'San Casimiro', 'San Sebastián', 'Santiago Mariño', 'Santos Michelena', 'Sucre', 'Tovar', 'Urdaneta'],
  'Carabobo': ['Bejuma', 'Carlos Arvelo', 'Diego Ibarra', 'Guacara', 'Juan José Mora', 'Libertador', 'Los Guayos', 'Miranda', 'Montalbán', 'Naguanagua', 'Puerto Cabello', 'San Diego', 'San Joaquín', 'Valencia'],
  'Lara': ['Andrés Eloy Blanco', 'Crespo', 'Iribarren', 'Jiménez', 'Morán', 'Palavecino', 'Simón Planas', 'Torres', 'Urdaneta'],
  'Bolívar': ['Angostura del Orinoco', 'Caroní', 'Cedeño', 'El Callao', 'Gran Sabana', 'Heres', 'Independencia', 'Piar', 'Raúl Leoni', 'Roscio', 'Sifontes', 'Sucre'],
  'Táchira': ['Ayacucho', 'Andrés Bello', 'Antonio Rómulo Costa', 'Bolívar', 'Cárdenas', 'Córdoba', 'Fernández Feo', 'Francisco de Miranda', 'García de Hevia', 'Guásimos', 'Independencia', 'Jáuregui', 'José María Vargas', 'Junín', 'Libertad', 'Libertador', 'Lobatera', 'Michelena', 'Panamericano', 'Pedro María Ureña', 'Rafael Urdaneta', 'Samuel Darío Maldonado', 'San Cristóbal', 'Seboruco', 'Simón Rodríguez', 'Sucre', 'Torbes', 'Uribante'],
  'Mérida': ['Alberto Adriani', 'Andrés Bello', 'Aricagua', 'Arzobispo Chacón', 'Campo Elías', 'Cardenal Quintero', 'Guaraque', 'Julio César Salas', 'Justo Briceño', 'Libertador', 'Miranda', 'Obispo Ramos de Lora', 'Padre Noguera', 'Pueblo Llano', 'Rangel', 'Rivas Dávila', 'Santos Marquina', 'Sucre', 'Tovar', 'Tulio Febres Cordero', 'Zea'],
  'Otro': ['Municipio no listado'],
}

// Lista plana de todos los municipios (para selects simples)
export const MUNICIPIOS_VENEZUELA: string[] = [
  ...new Set(Object.values(ESTADOS_MUNICIPIOS).flat().sort()),
]

// ─── Características físicas ─────────────────────────────────────────────────
export const EYE_COLORS = [
  'Negros',
  'Marrones oscuros',
  'Marrones claros',
  'Verdes',
  'Azules',
  'Grises',
  'Avellana',
  'Miel',
] as const

export const HAIR_COLORS = [
  'Negro',
  'Castaño oscuro',
  'Castaño claro',
  'Rubio',
  'Rojo',
  'Gris',
  'Blanco / Canoso',
  'Calvo / Sin cabello',
] as const

export const BODY_BUILDS = [
  'Delgado',
  'Atlético',
  'Normal / Promedio',
  'Robusto',
  'Obeso',
  'Ectomorfo',
  'Mesomorfo',
  'Endomorfo',
] as const
