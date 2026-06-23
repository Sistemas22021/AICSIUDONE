import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Camera, Upload, Trash2, Fingerprint } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

interface Belonging {
  description: string
  quantity: number
  observations: string
}

interface FormData {
  cedula: string
  firstName: string
  secondName: string
  firstLastname: string
  secondLastname: string
  birthDate: string
  crime: string
  caseNumber: string
  court: string
  admissionDate: string
  sentenceYears: string
  sentenceMonths: string
  eyeColor: string
  hairColor: string
  bodyBuild: string
  heightCm: string
  weightKg: string
  distinguishingMarks: string
  photoUrl: string
  photoUrl2: string
  photoUrl3: string
  fingerprintUrl: string
  fingerprintRightUrl: string
}

const EMPTY_FORM: FormData = {
  cedula: '', firstName: '', secondName: '', firstLastname: '', secondLastname: '',
  birthDate: '', crime: '', caseNumber: '', court: '',
  admissionDate: '', sentenceYears: '', sentenceMonths: '',
  eyeColor: '', hairColor: '', bodyBuild: '', heightCm: '', weightKg: '',
  distinguishingMarks: '', photoUrl: '', photoUrl2: '', photoUrl3: '', fingerprintUrl: '', fingerprintRightUrl: ''
}

export default function InmateRegisterPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState<FormData>({ ...EMPTY_FORM })
  const [belongings, setBelongings] = useState<Belonging[]>([])
  const [cedulaChecked, setCedulaChecked] = useState(false)
  const [cedulaExists, setCedulaExists] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, fieldName: keyof FormData) => {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = (event) => {
      setForm(prev => ({ ...prev, [fieldName]: event.target?.result as string }))
    }
    reader.readAsDataURL(file)
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (name === 'cedula') setCedulaChecked(false)
  }

  async function checkCedula() {
    if (!form.cedula) return
    try {
      const res = await api.get<{ hasActiveRecord: boolean }>(`/inmates/check-cedula/${form.cedula}`)
      setCedulaExists(res.data.hasActiveRecord)
      setCedulaChecked(true)
    } catch {
      setError('Error al verificar cédula')
    }
  }

  function addBelonging() {
    setBelongings(prev => [...prev, { description: '', quantity: 1, observations: '' }])
  }

  function updateBelonging(index: number, field: keyof Belonging, value: string | number) {
    setBelongings(prev => prev.map((b, i) => i === index ? { ...b, [field]: value } : b))
  }

  function removeBelonging(index: number) {
    setBelongings(prev => prev.filter((_, i) => i !== index))
  }

  function calculateAge(birthDate: string) {
    if (!birthDate) return ''
    const birth = new Date(birthDate)
    const today = new Date()
    let age = today.getFullYear() - birth.getFullYear()
    const m = today.getMonth() - birth.getMonth()
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--
    return age
  }

  function calculateRelease() {
    if (!form.admissionDate) return ''
    const admission = new Date(form.admissionDate)
    const years = parseInt(form.sentenceYears) || 0
    const months = parseInt(form.sentenceMonths) || 0
    const release = new Date(admission)
    release.setFullYear(release.getFullYear() + years)
    release.setMonth(release.getMonth() + months)
    return release.toISOString().split('T')[0]
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setSuccess('')
    try {
      const res = await api.post('/inmates', { ...form, belongings })
      const inmate = res.data
      setForm({ ...EMPTY_FORM })
      setBelongings([])
      setCedulaChecked(false)
      navigate('/mapa', {
        state: {
          fromRegister: true,
          registeredInmate: {
            id: inmate.id,
            firstName: inmate.firstName,
            firstLastname: inmate.firstLastname
          }
        }
      })
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { error?: string; message?: string } } }
      setError(axiosErr.response?.data?.error || axiosErr.response?.data?.message || 'Error al registrar el recluso')
    }
  }

  const releaseDate = calculateRelease()
  const age = calculateAge(form.birthDate)

  return (
    <SidebarLayout>
      <div className="w-full">
      <h1 className="text-2xl font-bold text-gray-900 mb-1">Registro de interno</h1>
      <p className="text-sm text-gray-500 mb-6">Ingrese los datos del nuevo recluso en el sistema</p>

      {success && <div className="bg-green-100 border border-green-400 text-green-800 px-4 py-3 rounded-lg mb-4">{success}</div>}
      {error && <div className="bg-red-100 border border-red-400 text-red-800 px-4 py-3 rounded-lg mb-4">{error}</div>}

      <form onSubmit={handleSubmit} className="space-y-6">

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Datos personales</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="text-sm text-gray-600">Cédula de identidad *</label>
              <div className="flex gap-2 mt-1">
                <input name="cedula" className="flex-1 border rounded-lg px-3 py-2"
                  value={form.cedula} onChange={handleChange} placeholder="V-12345678" required />
                <button type="button" onClick={checkCedula}
                  className="bg-gray-100 border rounded-lg px-4 py-2 text-sm hover:bg-gray-200">
                  Verificar
                </button>
              </div>
              {cedulaChecked && (
                <p className={`text-sm mt-1 ${cedulaExists ? 'text-red-600' : 'text-green-600'}`}>
                  {cedulaExists ? 'Esta cédula ya tiene un expediente activo' : 'Cédula disponible'}
                </p>
              )}
            </div>
            <div>
              <label className="text-sm text-gray-600">Primer nombre *</label>
              <input name="firstName" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.firstName} onChange={handleChange} required />
            </div>
            <div>
              <label className="text-sm text-gray-600">Segundo nombre</label>
              <input name="secondName" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.secondName} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Primer apellido *</label>
              <input name="firstLastname" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.firstLastname} onChange={handleChange} required />
            </div>
            <div>
              <label className="text-sm text-gray-600">Segundo apellido</label>
              <input name="secondLastname" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.secondLastname} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Fecha de nacimiento</label>
              <input type="date" name="birthDate" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.birthDate} onChange={handleChange} />
              {age && <p className="text-xs text-gray-500 mt-1">Edad: {age} años</p>}
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Información judicial</h2>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm text-gray-600">Delito imputado</label>
              <input name="crime" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.crime} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">N° de expediente</label>
              <input name="caseNumber" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.caseNumber} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Tribunal</label>
              <input name="court" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.court} onChange={handleChange} />
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Condena</h2>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm text-gray-600">Fecha de ingreso</label>
              <input type="date" name="admissionDate" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.admissionDate} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Años de condena</label>
              <input type="number" min="0" name="sentenceYears" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.sentenceYears} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Meses de condena</label>
              <input type="number" min="0" max="11" name="sentenceMonths" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.sentenceMonths} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Fecha estimada de liberación</label>
              <input type="date" className="w-full border rounded-lg px-3 py-2 mt-1 bg-gray-50"
                value={releaseDate} disabled />
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Características físicas</h2>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="text-sm text-gray-600">Color de ojos</label>
              <input name="eyeColor" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.eyeColor} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Color de cabello</label>
              <input name="hairColor" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.hairColor} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Complexión</label>
              <input name="bodyBuild" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.bodyBuild} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Estatura (cm)</label>
              <input type="number" name="heightCm" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.heightCm} onChange={handleChange} />
            </div>
            <div>
              <label className="text-sm text-gray-600">Peso (kg)</label>
              <input type="number" step="0.1" name="weightKg" className="w-full border rounded-lg px-3 py-2 mt-1"
                value={form.weightKg} onChange={handleChange} />
            </div>
            <div className="col-span-3">
              <label className="text-sm text-gray-600">Señas particulares</label>
              <textarea name="distinguishingMarks" className="w-full border rounded-lg px-3 py-2 mt-1"
                rows={2} value={form.distinguishingMarks} onChange={handleChange} />
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4 flex items-center gap-2 text-gray-800">
            <Camera className="w-5 h-5 text-gray-500" />
            Registro de Biométricos
          </h2>
          
          <div className="space-y-6">
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-3">Fotografías del interno (Máx. 3)</label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {[
                  { key: 'photoUrl' as const, label: 'Foto Frontal' },
                  { key: 'photoUrl2' as const, label: 'Perfil Izquierdo' },
                  { key: 'photoUrl3' as const, label: 'Perfil Derecho' }
                ].map(({ key, label }) => (
                  <div key={key} className="flex flex-col items-center">
                    <span className="text-xs text-gray-500 mb-1.5 font-medium">{label}</span>
                    <div className="relative w-full h-40 border-2 border-dashed border-gray-300 hover:border-blue-500 rounded-xl overflow-hidden flex flex-col items-center justify-center bg-gray-50 transition-colors group">
                      {form[key] ? (
                        <>
                          <img src={form[key]} alt={label} className="w-full h-full object-cover" />
                          <button
                            type="button"
                            onClick={() => setForm(prev => ({ ...prev, [key]: '' }))}
                            className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white p-1.5 rounded-full shadow-md transition-colors"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </>
                      ) : (
                        <label className="w-full h-full flex flex-col items-center justify-center cursor-pointer p-4 text-center">
                          <Upload className="w-6 h-6 text-gray-400 group-hover:text-blue-500 mb-2 transition-colors" />
                          <span className="text-xs font-semibold text-blue-600 group-hover:text-blue-700 transition-colors">Subir imagen</span>
                          <span className="text-[10px] text-gray-400 mt-1">PNG, JPG</span>
                          <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => handleFileChange(e, key)}
                            className="hidden"
                          />
                        </label>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="border-t border-gray-100 pt-5">
              <label className="text-sm font-semibold text-gray-700 block mb-3">Huellas dactilares (Izquierda y Derecha)</label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {[
                  { key: 'fingerprintUrl' as const, label: 'Mano Izquierda' },
                  { key: 'fingerprintRightUrl' as const, label: 'Mano Derecha' }
                ].map(({ key, label }) => (
                  <div key={key} className="flex flex-col items-center">
                    <span className="text-xs text-gray-500 mb-1.5 font-medium">{label}</span>
                    <div className="relative w-full h-40 border-2 border-dashed border-gray-300 hover:border-blue-500 rounded-xl overflow-hidden flex flex-col items-center justify-center bg-gray-50 transition-colors group">
                      {form[key] ? (
                        <>
                          <img src={form[key]} alt={label} className="w-full h-full object-cover" />
                          <button
                            type="button"
                            onClick={() => setForm(prev => ({ ...prev, [key]: '' }))}
                            className="absolute top-2 right-2 bg-red-500 hover:bg-red-650 text-white p-1.5 rounded-full shadow-md transition-colors"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </>
                      ) : (
                        <label className="w-full h-full flex flex-col items-center justify-center cursor-pointer p-4 text-center">
                          <Fingerprint className="w-6 h-6 text-gray-400 group-hover:text-blue-500 mb-2 transition-colors" />
                          <span className="text-xs font-semibold text-blue-600 group-hover:text-blue-700 transition-colors">Subir Huella</span>
                          <span className="text-[10px] text-gray-400 mt-1">Imagen de huella</span>
                          <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => handleFileChange(e, key)}
                            className="hidden"
                          />
                        </label>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Pertenencias</h2>
          {belongings.map((b, i) => (
            <div key={i} className="flex gap-2 mb-2 items-start">
              <div className="flex-1">
                <input placeholder="Descripción" className="w-full border rounded-lg px-3 py-2"
                  value={b.description} onChange={e => updateBelonging(i, 'description', e.target.value)} />
              </div>
              <div className="w-20">
                <input type="number" min="1" placeholder="Cant." className="w-full border rounded-lg px-3 py-2"
                  value={b.quantity} onChange={e => updateBelonging(i, 'quantity', parseInt(e.target.value) || 1)} />
              </div>
              <button type="button" onClick={() => removeBelonging(i)}
                className="text-red-500 text-sm mt-2">Eliminar</button>
            </div>
          ))}
          <button type="button" onClick={addBelonging}
            className="text-blue-600 text-sm hover:underline">+ Agregar pertenencia</button>
        </section>

        <button type="submit" disabled={cedulaExists}
          className="w-full bg-blue-600 text-white py-3 rounded-xl font-medium hover:bg-blue-700 disabled:bg-gray-400">
          Registrar interno
        </button>
      </form>
    </div>
    </SidebarLayout>
  )
}
