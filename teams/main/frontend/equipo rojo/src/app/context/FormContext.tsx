import { createContext, useContext, useState, type ReactNode } from 'react'

// ─── Tipos de involucrado ─────────────────────────────────────────────────────

export type TipoInvolucrado =
    | 'victima'
    | 'imputado_sospechoso'
    | 'acusado'
    | 'testigo'
    | 'denunciante'
    | 'querellante'
    | 'experto_perito'
    | 'detenido_arrestado'

export const TIPOS_INVOLUCRADO: { value: TipoInvolucrado; label: string }[] = [
  { value: 'victima',             label: 'Víctima' },
  { value: 'imputado_sospechoso', label: 'Imputado / Sospechoso' },
  { value: 'acusado',             label: 'Acusado' },
  { value: 'testigo',             label: 'Testigo' },
  { value: 'denunciante',         label: 'Denunciante' },
  { value: 'querellante',         label: 'Querellante' },
  { value: 'experto_perito',      label: 'Experto / Perito' },
  { value: 'detenido_arrestado',  label: 'Detenido / Arrestado' },
]

// ─── Involucrado ──────────────────────────────────────────────────────────────

export interface Involucrado {
  id: number
  tipoInvolucrado: TipoInvolucrado
  nombre: string
  identificacion: string
  nacionalidad: string
  telefono: string
  direccion: string
  foto: File | null
}

// ─── FormData ─────────────────────────────────────────────────────────────────
//
// Los campos de delito (tipoDelito, subTipo, horaInicioHecho, horaFinHecho,
// hechoEnCurso) estan en useDelitoList como un
// array de DelitoEntry, para poder registrar múltiples delitos por
// incidente.

export interface FormData {
  // ── Involucrados ────────────────────────────────────────────────────────────
  involucrados: Involucrado[]

  // ── Descripción del hecho ───────────────────────────────────────────────────
  descripcion: string

  // ── Ubicación manual (cuando no se usa GPS) ─────────────────────────────────
  municipio:          string
  sector:             string
  ubicacionDireccion: string
  referencia:         string
  lat:                number | null
  lng:                number | null

  // ── Cronología del reporte ──────────────────────────────────────────────────
  fechaReporte: string
  horaReporte:  string

  // ── Agente e investigador ───────────────────────────────────────────────────
  agenteRegistrador: string
  investigador:      string

  // ── Denuncia formal ─────────────────────────────────────────────────────────
  denuncianteNombre:         string
  denuncianteTelefono:       string
  denuncianteIdentificacion: string
  denuncianteNacionalidad:   string
  denuncianteDireccion:      string
  denuncianteRelacion:       string
}

// ─── Contexto ─────────────────────────────────────────────────────────────────

interface FormContextType {
  formData: FormData
  updateFormData:    (data: Partial<FormData>) => void
  addInvolucrado:    () => void
  removeInvolucrado: (id: number) => void
  updateInvolucrado: (id: number, data: Partial<Involucrado>) => void
  resetForm:         () => void
}

const FormContext = createContext<FormContextType | undefined>(undefined)

// ─── Estado inicial ───────────────────────────────────────────────────────────

const initialFormData: FormData = {
  involucrados: [
    {
      id: 1,
      tipoInvolucrado: 'victima',
      nombre: '',
      identificacion: '',
      nacionalidad: '',
      telefono: '',
      direccion: '',
      foto: null,
    },
  ],

  descripcion: '',

  municipio:          '',
  sector:             '',
  ubicacionDireccion: '',
  referencia:         '',
  lat:                null,
  lng:                null,

  fechaReporte: new Date().toISOString().split('T')[0],
  horaReporte:  new Date().toTimeString().slice(0, 5),

  agenteRegistrador: new Date().toLocaleDateString('es-VE'),
  investigador:      'Agt. Ramírez',

  denuncianteNombre:         '',
  denuncianteTelefono:       '',
  denuncianteIdentificacion: '',
  denuncianteNacionalidad:   '',
  denuncianteDireccion:      '',
  denuncianteRelacion:       '',
}

// ─── Provider ─────────────────────────────────────────────────────────────────

export const FormProvider = ({ children }: { children: ReactNode }) => {
  const [formData, setFormData] = useState<FormData>(initialFormData)

  const updateFormData = (data: Partial<FormData>) =>
      setFormData(prev => ({ ...prev, ...data }))

  const addInvolucrado = () => {
    const newId = Math.max(...formData.involucrados.map(v => v.id), 0) + 1
    setFormData(prev => ({
      ...prev,
      involucrados: [
        ...prev.involucrados,
        {
          id: newId,
          tipoInvolucrado: 'victima',
          nombre: '',
          identificacion: '',
          nacionalidad: '',
          telefono: '',
          direccion: '',
          foto: null,
        },
      ],
    }))
  }

  const removeInvolucrado = (id: number) =>
      setFormData(prev => ({
        ...prev,
        involucrados: prev.involucrados.filter(v => v.id !== id),
      }))

  const updateInvolucrado = (id: number, data: Partial<Involucrado>) =>
      setFormData(prev => ({
        ...prev,
        involucrados: prev.involucrados.map(v => (v.id === id ? { ...v, ...data } : v)),
      }))

  const resetForm = () => setFormData(initialFormData)

  return (
      <FormContext.Provider
          value={{
            formData,
            updateFormData,
            addInvolucrado,
            removeInvolucrado,
            updateInvolucrado,
            resetForm,
          }}
      >
        {children}
      </FormContext.Provider>
  )
}

// ─── Hook ─────────────────────────────────────────────────────────────────────

export const useFormContext = () => {
  const ctx = useContext(FormContext)
  if (!ctx) throw new Error('useFormContext debe usarse dentro de <FormProvider>')
  return ctx
}