import { createContext, useContext, useState, type ReactNode } from 'react'

interface Victim {
  id: number
  nombre: string
  identificacion: string
  nacionalidad: string
  telefono: string
  direccion: string
  foto: File|null
}

interface FormData {
  tipoDelito: string
  subTipo: string
  agenteRegistrador: string
  investigador: string
  victims: Victim[]
  fechaHecho: string
  horaInicioHecho: string
  horaFinHecho: string
  hechoEnCurso:boolean
  fechaReporte: string
  horaReporte: string
}

interface FormContextType {
  formData: FormData
  updateFormData: (data: Partial<FormData>) => void
  addVictim: () => void
  removeVictim: (id: number) => void
  updateVictim: (id: number, data: Partial<Victim>) => void
  resetForm: () => void
}

const FormContext = createContext<FormContextType | undefined>(undefined)

const initialFormData: FormData = {
  tipoDelito: '',
  subTipo: '',
  agenteRegistrador: new Date().toLocaleDateString('es-VE'),
  investigador: 'Agt. Ramírez',
  victims: [{ id: 1, nombre: '', identificacion: '', nacionalidad: '', telefono: '', direccion: '', foto:null }],
  fechaHecho: '',
  horaInicioHecho: '',
  horaFinHecho: '',
  hechoEnCurso: false,
  fechaReporte: new Date().toISOString().split('T')[0],
  horaReporte: new Date().toTimeString().slice(0, 5),
}

export const FormProvider = ({ children }: { children: ReactNode }) => {
  const [formData, setFormData] = useState<FormData>(initialFormData)

  const updateFormData = (data: Partial<FormData>) =>
    setFormData((prev) => ({ ...prev, ...data }))

  const addVictim = () => {
    const newId = Math.max(...formData.victims.map((v) => v.id), 0) + 1
    // @ts-ignore
    setFormData((prev) => ({
      ...prev,
      victims: [
        ...prev.victims,
        { id: newId, nombre: '', identificacion: '', nacionalidad: '', telefono: '', direccion: '', foto:null },
      ],
    }))
  }

  const removeVictim = (id: number) =>
    setFormData((prev) => ({ ...prev, victims: prev.victims.filter((v) => v.id !== id) }))

  const updateVictim = (id: number, data: Partial<Victim>) =>
    setFormData((prev) => ({
      ...prev,
      victims: prev.victims.map((v) => (v.id === id ? { ...v, ...data } : v)),
    }))

  const resetForm = () => setFormData(initialFormData)

  return (
    <FormContext.Provider value={{ formData, updateFormData, addVictim, removeVictim, updateVictim, resetForm }}>
      {children}
    </FormContext.Provider>
  )
}

export const useFormContext = () => {
  const ctx = useContext(FormContext)
  if (!ctx) throw new Error('useFormContext debe usarse dentro de <FormProvider>')
  return ctx
}
