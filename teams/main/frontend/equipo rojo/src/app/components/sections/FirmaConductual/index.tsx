export { FirmaConductual } from './FirmaConductual'
export { HistorialFirmaConductual } from './HistorialFirmaConductual'

// ─── Campos del formulario ────────────────────────────────────────────────────
// Clave = nombre exacto del campo en los DTOs del backend
// (Registrar/ActualizarFirmaConductualRequest y FirmaConductualResponse).

export type CampoFirmaConductualKey =
    | 'comportamientoPreDelictivo'
    | 'metodoAproximacion'
    | 'metodoAtaque'
    | 'comportamientoPostDelictivo'
    | 'elementosDistintivos'

export interface CampoFirmaConductualDef {
    key: CampoFirmaConductualKey
    label: string
    placeholder: string
    helper: string
}

export const CAMPOS_FIRMA_CONDUCTUAL: CampoFirmaConductualDef[] = [
    {
        key: 'comportamientoPreDelictivo',
        label: 'Comportamiento Pre-delictivo',
        placeholder: 'Actitudes, rutinas o señales de vigilancia observadas antes del hecho…',
        helper: 'Conducta del sospechoso previa a la comisión del delito.',
    },
    {
        key: 'metodoAproximacion',
        label: 'Método de Aproximación',
        placeholder: 'Cómo se acercó el sospechoso a la víctima o al lugar del hecho…',
        helper: 'Estrategia utilizada para acercarse a la víctima u objetivo.',
    },
    {
        key: 'metodoAtaque',
        label: 'Método de Ataque',
        placeholder: 'Arma, técnica o secuencia de acciones utilizada durante el hecho…',
        helper: 'Estrategia empleada para cometer el delito.',
    },
    {
        key: 'comportamientoPostDelictivo',
        label: 'Comportamiento Post-delictivo',
        placeholder: 'Acciones del sospechoso inmediatamente después del hecho…',
        helper: 'Conducta observada luego de cometido el delito.',
    },
    {
        key: 'elementosDistintivos',
        label: 'Elementos Distintivos',
        placeholder: 'Marcas, frases, rituales, objetos dejados u otras firmas particulares…',
        helper: 'Características o marcas distintivas del perpetrador.',
    },
]