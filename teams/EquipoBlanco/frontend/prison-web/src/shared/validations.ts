// ─────────────────────────────────────────────────────────────────────────────
// validations.ts — Utilidades de validación para el SIGP
// Contexto: Centros Penitenciarios de Venezuela
// ─────────────────────────────────────────────────────────────────────────────

/** Retorna la fecha de hoy en formato YYYY-MM-DD (para atributo max de inputs date) */
export function maxDateToday(): string {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${dd}`
}

/** Retorna datetime actual en formato YYYY-MM-DDTHH:MM (para atributo max de inputs datetime-local) */
export function maxDateTimeNow(): string {
  const d = new Date()
  const y = d.getFullYear()
  const mo = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${mo}-${dd}T${hh}:${mm}`
}

// ─── Cédula venezolana ───────────────────────────────────────────────────────
const CEDULA_REGEX = /^[Vv]-?\d{6,9}$/
const PASSPORT_REGEX = /^[A-Z]{1,3}\d{6,9}$/i

/**
 * Valida cédula venezolana:
 * Solo V seguido de guion opcional y 6-9 dígitos.
 * Si allowPassport=true también acepta formato pasaporte internacional.
 */
export function validateCedula(value: string, allowPassport = false): string {
  if (!value.trim()) return 'La cédula es obligatoria'
  const v = value.trim().toUpperCase()
  if (CEDULA_REGEX.test(v)) return ''
  if (allowPassport && PASSPORT_REGEX.test(v)) return ''
  return 'Formato inválido. Debe comenzar por V- (Ej: V-12345678)'
}

/**
 * Formateo inteligente de cédula:
 * Si el usuario tipea números, agrega automáticamente "V-".
 * Filtra cualquier letra no permitida.
 */
export function formatCedulaIntelligent(val: string): string {
  let formatted = val.toUpperCase().trim()
  if (/^\d/.test(formatted)) {
    formatted = 'V-' + formatted
  } else if (/^V\d/.test(formatted)) {
    formatted = 'V-' + formatted.substring(1)
  }
  if (/^V-?/.test(formatted)) {
    const parts = formatted.split('-')
    if (parts.length > 1) {
      formatted = 'V-' + parts.slice(1).join('').replace(/\D/g, '')
    }
  }
  return formatted
}

/**
 * Filtra teclas en el input de cédula:
 * - Primera letra: solo V, E, J, P, G
 * - Tras la letra: solo guion y dígitos
 */
export function cedulaKeyFilter(e: React.KeyboardEvent<HTMLInputElement>) {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab']
  if (allowed.includes(e.key)) return
  if (e.ctrlKey || e.metaKey) return

  const input = e.target as HTMLInputElement
  let current = input.value
  const selStart = input.selectionStart ?? 0

  if (selStart === 0 && current.length === 0) {
    if (!/^[Vv\d]$/.test(e.key)) {
      e.preventDefault()
    }
    return
  }
  
  if (selStart === 1 && current.toUpperCase().startsWith('V') && e.key === '-') return

  if (!/^\d$/.test(e.key)) {
    e.preventDefault()
  }
}

// ─── Número de expediente judicial venezolano ────────────────────────────────
// Formatos: XP01-P-2024-001234 | 45º C-1699-23 | GP01-P-2023-012345
const EXPEDIENTE_REGEX_1 = /^[A-Z]{2}\d{2}-[PCEJH]-\d{4}-\d{4,6}$/i
const EXPEDIENTE_REGEX_2 = /^\d{1,3}[°º]?\s?[PCEJH]-\d{3,6}-\d{2,4}$/i

export function validateExpediente(value: string): string {
  if (!value.trim()) return '' // Opcional
  const v = value.trim()
  if (EXPEDIENTE_REGEX_1.test(v) || EXPEDIENTE_REGEX_2.test(v)) return ''
  return 'Formato inválido. Ej: XP01-P-2024-001234 o 45º C-1699-23'
}

// ─── Solo letras (nombres, apellidos, lugares) ───────────────────────────────
export function onlyLettersKeyFilter(e: React.KeyboardEvent<HTMLInputElement | HTMLTextAreaElement>) {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Tab', ' ', 'Home', 'End']
  if (allowed.includes(e.key)) return
  // Permitir letras, tildes, ñ, ü y espacios
  if (/^[a-zA-ZáéíóúÁÉÍÓÚàèìòùÀÈÌÒÙñÑüÜ\-'.]$/.test(e.key)) return
  e.preventDefault()
}

// ─── Solo números enteros positivos ─────────────────────────────────────────
export function onlyIntegersKeyFilter(e: React.KeyboardEvent<HTMLInputElement>) {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab']
  if (allowed.includes(e.key)) return
  if (!/^\d$/.test(e.key)) e.preventDefault()
}

// ─── Solo números con decimales ──────────────────────────────────────────────
export function onlyDecimalsKeyFilter(e: React.KeyboardEvent<HTMLInputElement>) {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab']
  if (allowed.includes(e.key)) return
  const current = (e.target as HTMLInputElement).value
  if (e.key === '.' && !current.includes('.')) return
  if (!/^\d$/.test(e.key)) e.preventDefault()
}

// ─── Teléfono venezolano ─────────────────────────────────────────────────────
const VE_PHONE_REGEX = /^(0412|0416|0426|0414|0424|0212|0212|0241|0251|0261|0261|0281|0291)\d{7}$/

export function validateVenezuelanPhone(value: string): string {
  if (!value.trim()) return 'El teléfono es obligatorio'
  const v = value.trim().replace(/[\s\-()]/g, '')
  if (VE_PHONE_REGEX.test(v)) return ''
  return 'Formato inválido. Ej: 0414-1234567 (operadoras VE: 0412, 0416, 0426, 0414, 0424)'
}

export function phoneKeyFilter(e: React.KeyboardEvent<HTMLInputElement>) {
  const allowed = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab']
  if (allowed.includes(e.key)) return
  if (e.key === '-') return
  if (!/^\d$/.test(e.key)) e.preventDefault()
}

// ─── Estatura y Peso ─────────────────────────────────────────────────────────
export function validateHeight(value: string): string {
  if (!value) return ''
  const n = Number(value)
  if (isNaN(n) || n < 100 || n > 250) return 'Estatura debe estar entre 100 y 250 cm'
  return ''
}

export function validateWeight(value: string): string {
  if (!value) return ''
  const n = Number(value)
  if (isNaN(n) || n < 20 || n > 350) return 'Peso debe estar entre 20 y 350 kg'
  return ''
}

// ─── Años/Meses de condena ───────────────────────────────────────────────────
export function validateSentenceYears(value: string): string {
  if (!value.trim()) return 'Los años de condena son obligatorios'
  const n = Number(value)
  if (isNaN(n) || !Number.isInteger(n) || n < 0 || n > 60) return 'Debe ser un número entero entre 0 y 60'
  return ''
}

export function validateSentenceMonths(value: string): string {
  if (!value.trim()) return ''
  const n = Number(value)
  if (isNaN(n) || !Number.isInteger(n) || n < 0 || n > 11) return 'Debe ser un número entre 0 y 11'
  return ''
}

// ─── Fechas ──────────────────────────────────────────────────────────────────
export function validateBirthDate(value: string): string {
  if (!value) return 'La fecha de nacimiento es obligatoria'
  const birth = new Date(value)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  if (birth > today) return 'La fecha no puede ser futura'
  const minAge = new Date(today)
  minAge.setFullYear(minAge.getFullYear() - 14)
  if (birth > minAge) return 'El recluso debe tener al menos 14 años'
  const maxAge = new Date(today)
  maxAge.setFullYear(maxAge.getFullYear() - 120)
  if (birth < maxAge) return 'Fecha de nacimiento inválida'
  return ''
}

export function validateAdmissionDate(value: string): string {
  if (!value) return 'La fecha de ingreso es obligatoria'
  const d = new Date(value)
  const today = new Date()
  today.setHours(23, 59, 59, 999)
  if (d > today) return 'La fecha de ingreso no puede ser futura'
  return ''
}

export function validatePastDatetime(value: string, label: string): string {
  if (!value) return `${label} es obligatorio`
  const d = new Date(value)
  if (d > new Date()) return `${label} no puede ser una fecha futura`
  return ''
}
