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
    'Lata metálica',
    'Kit de hisopos estériles con tubo',
    'Sobre de papel secante',
    'Contenedor de plástico hermético',
    'Microtubo Eppendorf',
    'Caja de portaobjetos',
    'Caja alargada de cartón rígido',
    'Tubo de PVC con tapa',
    'Caja con inserto de espuma',
    'Funda de acetato',
    'Carpeta de cartulina sin ácido',
    'Bolsa antiestática (ESD)',
    'Caja de cartón con acolchado',
    'Frasco de vidrio ámbar',
    'Bolsa de nylon sellada al vacío',
    'Contenedor metálico hermético',
    'Bolsa de papel kraft grande',
    'Caja genérica con relleno de espuma',
    'Frasco de boca ancha',
    'Bolsa de bioseguridad (roja)'
] as const

export const resultadoNegativo = [
    'No se localiza en el área inspeccionada',
    'No se corresponde con el elemento buscado',
    'Elemento presente pero sin valor criminalístico',
    'No hay registro del elemento en la lista de evidencias'
] as const