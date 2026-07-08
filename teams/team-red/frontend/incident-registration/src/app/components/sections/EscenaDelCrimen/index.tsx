export { EscenaDelCrimen } from './EscenaDelCrimen'
export { HistorialEscenas } from './HistorialEscenas'
// ─── Tipos de opciones ────────────────────────────────────────────────────────
export const tiposEvidencia = [
    'Huella dactilar',
    'Rastro biológico (sangre, semen, saliva)',
    'Rastro químico (droga, explosivo)',
    'Documento',
    'Arma blanca',
    'Arma de fuego',
    'Vehículo',
    'Electrónico (teléfono, ordenador)',
    'Prenda de vestir',
    'Otro'
] as const

export const tiposEmbalaje = [
    'Sobre de papel',
    'Bolsa de plástico sellable',
    'Frasco de vidrio estéril',
    'Caja de cartón',
    'Tubo de ensayo',
    'Sobresito de papel para drogas',
    'Bolsa de papel',
    'Lata metálica'
] as const

export const resultadoNegativo = [
    'No se localiza en el área inspeccionada',
    'No se corresponde con el elemento buscado',
    'Elemento presente pero sin valor criminalístico',
    'No hay registro del elemento en la lista de evidencias'
] as const