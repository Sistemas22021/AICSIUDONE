import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Camera, Upload, Trash2, Fingerprint, AlertCircle, Info } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'
import {
  cedulaKeyFilter, validateCedula, validateExpediente, formatCedulaIntelligent,
  onlyLettersKeyFilter, onlyIntegersKeyFilter, onlyDecimalsKeyFilter,
  validateBirthDate, validateAdmissionDate, validateSentenceYears,
  validateSentenceMonths, validateHeight, validateWeight, maxDateToday
} from '../../shared/validations'
import {
  DELITOS_PENALES, CIRCUITOS_JUDICIALES,
  EYE_COLORS, HAIR_COLORS, BODY_BUILDS
} from '../../shared/venezuela-data'

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

type FieldErrors = Partial<Record<keyof FormData | 'cedulaVerify', string>>

const EMPTY_FORM: FormData = {
  cedula: '', firstName: '', secondName: '', firstLastname: '', secondLastname: '',
  birthDate: '', crime: '', caseNumber: '', court: '',
  admissionDate: '', sentenceYears: '', sentenceMonths: '',
  eyeColor: '', hairColor: '', bodyBuild: '', heightCm: '', weightKg: '',
  distinguishingMarks: '', photoUrl: '', photoUrl2: '', photoUrl3: '', fingerprintUrl: '', fingerprintRightUrl: ''
}

// Campos obligatorios y sus mensajes de error
const REQUIRED_FIELDS: { key: keyof FormData; label: string }[] = [
  { key: 'cedula',        label: 'Cédula de identidad' },
  { key: 'firstName',     label: 'Primer nombre' },
  { key: 'firstLastname', label: 'Primer apellido' },
  { key: 'birthDate',     label: 'Fecha de nacimiento' },
  { key: 'crime',         label: 'Delito imputado' },
  { key: 'admissionDate', label: 'Fecha de ingreso' },
  { key: 'sentenceYears', label: 'Años de condena' },
]

const CIRCUITO_CODES: Record<string, string> = {
  'DTTO_CAPITAL': 'XP01', 'MIRANDA': 'XP02', 'ZULIA': 'XP03', 'FALCON': 'XP04', 'LARA': 'XP05',
  'MERIDA': 'XP06', 'TACHIRA': 'XP07', 'TRUJILLO': 'XP08', 'BARINAS': 'XP09', 'APURE': 'XP10',
  'ARAGUA': 'XP11', 'CARABOBO': 'XP12', 'COJEDES': 'XP13', 'GUARICO': 'XP14', 'PORTUGUESA': 'XP15',
  'YARACUY': 'XP16', 'ANZOATEGUI': 'XP17', 'BOLIVAR': 'XP18', 'MONAGAS': 'XP19', 'NUEVA_ESPARTA': 'XP20',
  'SUCRE': 'XP21', 'AMAZONAS': 'XP22', 'DELTA_AMACURO': 'XP23', 'LA_GUAIRA': 'XP24'
}

function getFasePorDelito(delito: string) {
  if (!delito) return 'C'
  const graves = ['Homicidio', 'Secuestro', 'Violación', 'Terrorismo', 'Extorsión', 'Tráfico', 'Corrupción']
  return graves.some(g => delito.includes(g)) ? 'J' : 'C'
}

export default function InmateRegisterPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState<FormData>({ ...EMPTY_FORM })
  const [belongings, setBelongings] = useState<Belonging[]>([])
  const [cedulaChecked, setCedulaChecked] = useState(false)
  const [cedulaExists, setCedulaExists] = useState(false)
  const [errors, setErrors] = useState<FieldErrors>({})
  const [touched, setTouched] = useState<Partial<Record<keyof FormData, boolean>>>({})
  const [globalError, setGlobalError] = useState('')
  const [success, setSuccess] = useState('')
  const [otherCrime, setOtherCrime] = useState('')
  const videoRef = useRef<HTMLVideoElement | null>(null)
  const [cameraTarget, setCameraTarget] = useState<keyof FormData | null>(null)
  const [cameraStream, setCameraStream] = useState<MediaStream | null>(null)

  useEffect(() => {
    if (form.court && form.crime) {
      const code = CIRCUITO_CODES[form.court] || 'XP00'
      const delitoReal = form.crime === 'Otro delito' ? otherCrime : form.crime
      const fase = getFasePorDelito(delitoReal)
      const year = new Date().getFullYear()
      const prefix = `${code}-${fase}-${year}-`
      
      setForm(p => {
        let currentSeq = ''
        if (p.caseNumber) {
          const parts = p.caseNumber.split('-')
          if (parts.length >= 4) {
             currentSeq = parts.slice(3).join('-')
          } else {
             // Intenta rescatar los números finales si el operador borró un guión por error
             currentSeq = parts[parts.length - 1].replace(/\D/g, '')
          }
        }
        return { ...p, caseNumber: `${prefix}${currentSeq}` }
      })
    }
  }, [form.court, form.crime, otherCrime])

  async function openCamera(fieldName: keyof FormData) {
    setCameraTarget(fieldName)
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true })
      setCameraStream(stream)
      setTimeout(() => {
        if (videoRef.current) videoRef.current.srcObject = stream
      }, 100)
    } catch {
      alert('No se pudo acceder a la cámara. Asegúrese de dar permiso.')
    }
  }

  function capturePhoto() {
    if (!videoRef.current || !cameraTarget) return
    const canvas = document.createElement('canvas')
    canvas.width = videoRef.current.videoWidth
    canvas.height = videoRef.current.videoHeight
    canvas.getContext('2d')?.drawImage(videoRef.current, 0, 0)
    const dataUrl = canvas.toDataURL('image/jpeg', 0.85)
    setForm(prev => ({ ...prev, [cameraTarget]: dataUrl }))
    closeCamera()
  }

  function closeCamera() {
    cameraStream?.getTracks().forEach(t => t.stop())
    setCameraStream(null)
    setCameraTarget(null)
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, fieldName: keyof FormData) => {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = (event) => {
      setForm(prev => ({ ...prev, [fieldName]: event.target?.result as string }))
    }
    reader.readAsDataURL(file)
  }

  function validateField(name: keyof FormData, value: string): string {
    const required = REQUIRED_FIELDS.find(f => f.key === name)
    if (required && !value.trim()) return `${required.label} es obligatorio`
    if (name === 'cedula') return validateCedula(value)
    if (name === 'birthDate') return validateBirthDate(value)
    if (name === 'admissionDate') return validateAdmissionDate(value)
    if (name === 'sentenceYears') return validateSentenceYears(value)
    if (name === 'sentenceMonths') return validateSentenceMonths(value)
    if (name === 'caseNumber') return validateExpediente(value)
    if (name === 'heightCm') return validateHeight(value)
    if (name === 'weightKg') return validateWeight(value)
    return ''
  }

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const { name } = e.target
    let value = e.target.value
    if (name === 'cedula') value = formatCedulaIntelligent(value)
    const fieldName = name as keyof FormData
    setForm(prev => ({ ...prev, [fieldName]: value }))
    if (name === 'cedula') { setCedulaChecked(false); setCedulaExists(false) }
    if (touched[fieldName]) {
      setErrors(prev => ({ ...prev, [fieldName]: validateField(fieldName, value) }))
    }
  }

  function handleBlur(e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) {
    const { name, value } = e.target
    const fieldName = name as keyof FormData
    setTouched(prev => ({ ...prev, [fieldName]: true }))
    setErrors(prev => ({ ...prev, [fieldName]: validateField(fieldName, value) }))
  }

  function validateAll(): FieldErrors {
    const newErrors: FieldErrors = {}
    for (const { key } of REQUIRED_FIELDS) {
      const msg = validateField(key, form[key])
      if (msg) newErrors[key] = msg
    }
    // Campos opcionales con restricciones de formato
    const optionalChecks: (keyof FormData)[] = ['caseNumber', 'sentenceMonths', 'heightCm', 'weightKg']
    for (const key of optionalChecks) {
      const msg = validateField(key, form[key])
      if (msg) newErrors[key] = msg
    }
    if (!cedulaChecked) newErrors.cedulaVerify = 'Debes verificar la cédula antes de registrar'
    if (cedulaExists) newErrors.cedula = 'Esta cédula ya tiene un expediente activo'
    // Biométricos obligatorios
    if (!form.photoUrl) newErrors.photoUrl = 'La foto frontal es obligatoria'
    if (!form.photoUrl2) newErrors.photoUrl2 = 'La foto perfil izquierdo es obligatoria'
    if (!form.photoUrl3) newErrors.photoUrl3 = 'La foto perfil derecho es obligatoria'
    if (!form.fingerprintUrl) newErrors.fingerprintUrl = 'La huella mano izquierda es obligatoria'
    if (!form.fingerprintRightUrl) newErrors.fingerprintRightUrl = 'La huella mano derecha es obligatoria'
    return newErrors
  }

  async function checkCedula() {
    if (!form.cedula) {
      setErrors(prev => ({ ...prev, cedula: 'Cédula es obligatoria' }))
      return
    }
    try {
      const res = await api.get<{ hasActiveRecord: boolean }>(`/inmates/check-cedula/${form.cedula}`)
      setCedulaExists(res.data.hasActiveRecord)
      setCedulaChecked(true)
      setErrors(prev => ({
        ...prev,
        cedula: res.data.hasActiveRecord ? 'Esta cédula ya tiene un expediente activo' : '',
        cedulaVerify: ''
      }))
    } catch {
      setGlobalError('Error al verificar cédula')
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
    setGlobalError('')
    setSuccess('')

    // Marcar todos los campos como tocados y ejecutar validación completa
    const allTouched: Partial<Record<keyof FormData, boolean>> = {}
    for (const { key } of REQUIRED_FIELDS) allTouched[key] = true
    setTouched(allTouched)

    const newErrors = validateAll()
    setErrors(newErrors)
    if (Object.values(newErrors).some(v => v)) {
      setGlobalError('Por favor, corrige los errores marcados antes de continuar.')
      window.scrollTo({ top: 0, behavior: 'smooth' })
      return
    }

    try {
      const payload = { 
        ...form, 
        crime: form.crime === 'Otro delito' ? otherCrime : form.crime,
        belongings 
      }
      const res = await api.post('/inmates', payload)
      const inmate = res.data
      setForm({ ...EMPTY_FORM })
      setOtherCrime('')
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
      setGlobalError(axiosErr.response?.data?.error || axiosErr.response?.data?.message || 'Error al registrar el recluso')
    }
  }

  const releaseDate = calculateRelease()
  const age = calculateAge(form.birthDate)

  // Helper: clase CSS de input según estado de validación
  function inputClass(field: keyof FormData, extra = '') {
    const isRequired = REQUIRED_FIELDS.some(f => f.key === field)
    const hasError = touched[field] && errors[field]
    const isOk = touched[field] && isRequired && !errors[field] && form[field].trim() !== ''
    return `w-full border rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 transition-colors ${extra}
      ${hasError ? 'border-red-400 ring-1 ring-red-300 bg-red-50' : ''}
      ${isOk ? 'border-emerald-400 ring-1 ring-emerald-200' : ''}
      ${!hasError && !isOk ? 'border-gray-300 focus:ring-blue-300' : ''}`
  }

  function FieldError({ field }: { field: keyof FormData }) {
    return errors[field] && touched[field] ? (
      <p className="text-xs text-red-600 mt-1 flex items-center gap-1">
        <AlertCircle className="w-3 h-3 shrink-0" />{errors[field]}
      </p>
    ) : null
  }



  return (
    <SidebarLayout>
      <div className="w-full">
      <h1 className="text-2xl font-bold text-gray-900 mb-1">Registro de interno</h1>
      <p className="text-sm text-gray-500 mb-6">Ingrese los datos del nuevo recluso en el sistema</p>

      {success && <div className="bg-green-100 border border-green-400 text-green-800 px-4 py-3 rounded-lg mb-4">{success}</div>}
      {globalError && <div className="bg-red-100 border border-red-400 text-red-800 px-4 py-3 rounded-lg mb-4">{globalError}</div>}

      <form onSubmit={handleSubmit} className="space-y-6">

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Datos personales</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="text-sm text-gray-600 flex items-center gap-1">Cédula de identidad *
                <span title="Solo Venezolanos (V-12345678)" className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <div className="flex gap-2 mt-1">
                <input name="cedula" className={inputClass('cedula', 'flex-1 mt-0')}
                  value={form.cedula} onChange={handleChange} onBlur={handleBlur}
                  onKeyDown={cedulaKeyFilter}
                  placeholder="V-12345678" maxLength={12} />
                <button type="button" onClick={checkCedula}
                  className="bg-gray-100 border border-gray-300 rounded-lg px-4 py-2 text-sm hover:bg-gray-200 shrink-0 font-medium">
                  Verificar
                </button>
              </div>
              {errors.cedulaVerify && <p className="text-xs text-amber-600 mt-1">{errors.cedulaVerify}</p>}
              <FieldError field="cedula" />
              {cedulaChecked && !errors.cedula && (
                <p className="text-sm text-green-600 mt-1">✓ Cédula disponible</p>
              )}
            </div>
            <div>
              <label className="text-sm text-gray-600">Primer nombre *</label>
              <input name="firstName" className={inputClass('firstName')}
                value={form.firstName} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyLettersKeyFilter} placeholder="Solo letras" />
              <FieldError field="firstName" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Segundo nombre</label>
              <input name="secondName" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.secondName} onChange={handleChange}
                onKeyDown={onlyLettersKeyFilter} placeholder="Solo letras" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Primer apellido *</label>
              <input name="firstLastname" className={inputClass('firstLastname')}
                value={form.firstLastname} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyLettersKeyFilter} placeholder="Solo letras" />
              <FieldError field="firstLastname" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Segundo apellido</label>
              <input name="secondLastname" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.secondLastname} onChange={handleChange}
                onKeyDown={onlyLettersKeyFilter} placeholder="Solo letras" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Fecha de nacimiento *</label>
              <input type="date" name="birthDate" className={inputClass('birthDate')}
                value={form.birthDate} onChange={handleChange} onBlur={handleBlur}
                max={maxDateToday()} />
              {age && !errors.birthDate && <p className="text-xs text-gray-500 mt-1">Edad: {age} años</p>}
              <FieldError field="birthDate" />
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Información judicial</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="text-sm text-gray-600">Delito imputado *</label>
              <select name="crime" className={inputClass('crime')}
                value={form.crime}
                onChange={e => { setForm(p => ({ ...p, crime: e.target.value })); if (touched.crime) setErrors(p => ({ ...p, crime: validateField('crime', e.target.value) })) }}
                onBlur={handleBlur}>
                <option value="">-- Seleccione un delito --</option>
                {DELITOS_PENALES.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
              {form.crime === 'Otro delito' && (
                <input name="otherCrime" className={inputClass('crime', 'mt-2')}
                  value={otherCrime} onChange={e => setOtherCrime(e.target.value)}
                  placeholder="Especifique el delito..." required />
              )}
              <FieldError field="crime" />
            </div>
            <div>
              <label className="text-sm text-gray-600 flex items-center gap-1">N° de expediente
                <span title="El prefijo se autogenera según el Tribunal y Delito. Complete los números finales." className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <input name="caseNumber" className={inputClass('caseNumber')}
                value={form.caseNumber} onChange={handleChange} onBlur={handleBlur} 
                placeholder="Ej: XP01-J-2024-001234" maxLength={30} />
              <FieldError field="caseNumber" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Tribunal / Circuito Judicial</label>
              <select name="court" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.court}
                onChange={e => setForm(p => ({ ...p, court: e.target.value }))}>
                <option value="">-- Seleccione el tribunal --</option>
                {CIRCUITOS_JUDICIALES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
              </select>
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Condena</h2>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm text-gray-600">Fecha de ingreso *</label>
              <input type="date" name="admissionDate" className={inputClass('admissionDate')}
                value={form.admissionDate} onChange={handleChange} onBlur={handleBlur}
                max={maxDateToday()} />
              <FieldError field="admissionDate" />
            </div>
            <div>
              <label className="text-sm text-gray-600 flex items-center gap-1">Años de condena *
                <span title="Número entero entre 0 y 60" className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <input type="number" min="0" max="60" name="sentenceYears" className={inputClass('sentenceYears')}
                value={form.sentenceYears} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyIntegersKeyFilter} />
              <FieldError field="sentenceYears" />
            </div>
            <div>
              <label className="text-sm text-gray-600 flex items-center gap-1">Meses de condena
                <span title="Entre 0 y 11 meses" className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <input type="number" min="0" max="11" name="sentenceMonths" className={inputClass('sentenceMonths')}
                value={form.sentenceMonths} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyIntegersKeyFilter} />
              <FieldError field="sentenceMonths" />
            </div>
            <div>
              <label className="text-sm text-gray-600">Fecha estimada de liberación</label>
              <input type="date" className="w-full border border-gray-200 rounded-lg px-3 py-2 mt-1 bg-gray-50 text-gray-500 cursor-not-allowed"
                value={releaseDate} disabled />
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-4">Características físicas</h2>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="text-sm text-gray-600">Color de ojos</label>
              <select name="eyeColor" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.eyeColor} onChange={e => setForm(p => ({ ...p, eyeColor: e.target.value }))}>
                <option value="">-- Seleccionar --</option>
                {EYE_COLORS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm text-gray-600">Color de cabello</label>
              <select name="hairColor" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.hairColor} onChange={e => setForm(p => ({ ...p, hairColor: e.target.value }))}>
                <option value="">-- Seleccionar --</option>
                {HAIR_COLORS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm text-gray-600">Complexión</label>
              <select name="bodyBuild" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                value={form.bodyBuild} onChange={e => setForm(p => ({ ...p, bodyBuild: e.target.value }))}>
                <option value="">-- Seleccionar --</option>
                {BODY_BUILDS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm text-gray-600 flex items-center gap-1">Estatura (cm)
                <span title="Entre 100 y 250 cm" className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <input type="number" name="heightCm" min="100" max="250" className={inputClass('heightCm')}
                value={form.heightCm} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyIntegersKeyFilter} placeholder="Ej: 175" />
              <FieldError field="heightCm" />
            </div>
            <div>
              <label className="text-sm text-gray-600 flex items-center gap-1">Peso (kg)
                <span title="Entre 20 y 350 kg" className="cursor-help text-blue-400"><Info className="w-3.5 h-3.5" /></span>
              </label>
              <input type="number" step="0.1" name="weightKg" min="20" max="350" className={inputClass('weightKg')}
                value={form.weightKg} onChange={handleChange} onBlur={handleBlur}
                onKeyDown={onlyDecimalsKeyFilter} placeholder="Ej: 72.5" />
              <FieldError field="weightKg" />
            </div>
            <div className="col-span-3">
              <label className="text-sm text-gray-600">Señas particulares</label>
              <textarea name="distinguishingMarks" className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
                rows={2} value={form.distinguishingMarks} onChange={handleChange}
                maxLength={500} placeholder="Tatuajes, cicatrices, lunares, marcas de nacimiento..." />
              <p className="text-xs text-gray-400 text-right mt-0.5">{form.distinguishingMarks.length}/500</p>
            </div>
          </div>
        </section>

        <section className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-lg font-medium mb-1 flex items-center gap-2 text-gray-800">
            <Camera className="w-5 h-5 text-gray-500" />
            Registro de Bijométricos <span className="text-xs font-normal text-red-500 ml-1">(Todos obligatorios)</span>
          </h2>
          <p className="text-xs text-gray-400 mb-4">Puede subir una imagen desde el dispositivo o usar la cámara directamente.</p>

          <div className="space-y-6">
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-3">Fotografías del interno</label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {[
                  { key: 'photoUrl' as const, label: 'Foto Frontal *' },
                  { key: 'photoUrl2' as const, label: 'Perfil Izquierdo *' },
                  { key: 'photoUrl3' as const, label: 'Perfil Derecho *' }
                ].map(({ key, label }) => (
                  <div key={key} className="flex flex-col items-center">
                    <span className="text-xs text-gray-500 mb-1.5 font-medium">{label}</span>
                    <div className={`relative w-full h-40 border-2 border-dashed rounded-xl overflow-hidden flex flex-col items-center justify-center bg-gray-50 transition-colors group ${errors[key] ? 'border-red-400' : 'border-gray-300 hover:border-blue-500'}`}>
                      {form[key] ? (
                        <>
                          <img src={form[key]} alt={label} className="w-full h-full object-cover" />
                          <button type="button" onClick={() => setForm(prev => ({ ...prev, [key]: '' }))}
                            className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white p-1.5 rounded-full shadow-md">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </>
                      ) : (
                        <div className="flex flex-col items-center gap-2 p-2 w-full">
                          <label className="w-full flex flex-col items-center cursor-pointer">
                            <Upload className="w-6 h-6 text-gray-400 group-hover:text-blue-500 mb-1" />
                            <span className="text-xs font-semibold text-blue-600">Subir imagen</span>
                            <input type="file" accept="image/*" onChange={(e) => handleFileChange(e, key)} className="hidden" />
                          </label>
                          <button type="button" onClick={() => openCamera(key)}
                            className="flex items-center gap-1 text-xs text-gray-500 hover:text-indigo-600 border border-gray-200 rounded-lg px-2 py-1 hover:border-indigo-300 transition-colors">
                            <Camera className="w-3.5 h-3.5" /> Usar cámara
                          </button>
                        </div>
                      )}
                    </div>
                    {errors[key] && <p className="text-xs text-red-500 mt-1">{errors[key]}</p>}
                  </div>
                ))}
              </div>
            </div>
            <div className="border-t border-gray-100 pt-5">
              <label className="text-sm font-semibold text-gray-700 block mb-3">Huellas dactilares (Izquierda y Derecha)</label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {[
                  { key: 'fingerprintUrl' as const, label: 'Mano Izquierda *' },
                  { key: 'fingerprintRightUrl' as const, label: 'Mano Derecha *' }
                ].map(({ key, label }) => (
                  <div key={key} className="flex flex-col items-center">
                    <span className="text-xs text-gray-500 mb-1.5 font-medium">{label}</span>
                    <div className={`relative w-full h-40 border-2 border-dashed rounded-xl overflow-hidden flex flex-col items-center justify-center bg-gray-50 transition-colors group ${errors[key] ? 'border-red-400' : 'border-gray-300 hover:border-blue-500'}`}>
                      {form[key] ? (
                        <>
                          <img src={form[key]} alt={label} className="w-full h-full object-cover" />
                          <button type="button" onClick={() => setForm(prev => ({ ...prev, [key]: '' }))}
                            className="absolute top-2 right-2 bg-red-500 text-white p-1.5 rounded-full shadow-md">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </>
                      ) : (
                        <div className="flex flex-col items-center gap-2 p-2 w-full">
                          <label className="w-full flex flex-col items-center cursor-pointer">
                            <Fingerprint className="w-6 h-6 text-gray-400 group-hover:text-blue-500 mb-1" />
                            <span className="text-xs font-semibold text-blue-600">Subir huella</span>
                            <input type="file" accept="image/*" onChange={(e) => handleFileChange(e, key)} className="hidden" />
                          </label>
                          <button type="button" onClick={() => openCamera(key)}
                            className="flex items-center gap-1 text-xs text-gray-500 hover:text-indigo-600 border border-gray-200 rounded-lg px-2 py-1 hover:border-indigo-300 transition-colors">
                            <Camera className="w-3.5 h-3.5" /> Usar cámara
                          </button>
                        </div>
                      )}
                    </div>
                    {errors[key] && <p className="text-xs text-red-500 mt-1">{errors[key]}</p>}
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
                <input required placeholder="Descripción de la pertenencia *" maxLength={200}
                  className={`w-full border rounded-lg px-3 py-2 ${!b.description.trim() ? 'border-red-300' : 'border-gray-300'}`}
                  value={b.description} onChange={e => updateBelonging(i, 'description', e.target.value)} />
                {!b.description.trim() && <p className="text-xs text-red-500 mt-0.5">La descripción es obligatoria</p>}
              </div>
              <div className="w-24">
                <input type="number" min="1" max="999" placeholder="Cant." className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  value={b.quantity} onKeyDown={onlyIntegersKeyFilter}
                  onChange={e => updateBelonging(i, 'quantity', Math.max(1, parseInt(e.target.value) || 1))} />
              </div>
              <button type="button" onClick={() => removeBelonging(i)}
                className="text-red-500 text-sm mt-2 px-2 hover:text-red-700">Eliminar</button>
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

      {/* Modal cámara */}
      {cameraStream && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl overflow-hidden shadow-xl w-full max-w-lg">
            <div className="bg-gray-900 text-white px-4 py-3 flex items-center justify-between">
              <span className="font-bold text-sm flex items-center gap-2"><Camera className="w-4 h-4" /> Captura con cámara</span>
              <button onClick={closeCamera} className="text-white/70 hover:text-white text-xl leading-none">×</button>
            </div>
            <div className="p-4">
              <video ref={videoRef} autoPlay playsInline className="w-full rounded-xl border border-gray-200" />
              <div className="flex gap-3 mt-4">
                <button type="button" onClick={closeCamera}
                  className="flex-1 py-2.5 border border-gray-300 text-gray-700 rounded-xl text-sm font-semibold hover:bg-gray-50">
                  Cancelar
                </button>
                <button type="button" onClick={capturePhoto}
                  className="flex-1 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-bold hover:bg-indigo-700">
                  Capturar foto
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
    </SidebarLayout>
  )
}
