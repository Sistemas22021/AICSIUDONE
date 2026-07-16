import { useState, useRef, useEffect } from 'react'
import { MapPin, Plus, X, AlertTriangle, Clock, AlignLeft, ChevronDown, FileText, Loader2 } from 'lucide-react'
import { NeonPanel }    from '../../ui/NeonPanel'
import { NeonInput }    from '../../ui/NeonInput'
import { NeonSelect }   from '../../ui/NeonSelect'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonButton }   from '../../ui/NeonButton'
import { NeonRadio }    from '../../ui/NeonRadio'
import { NeonCheckbox } from '../../ui/NeonCheckbox'
import { useNeonToast } from '../../ui/NeonToast'
import { useExpedienteActivo } from '../../../context/ExpedienteActivoContext'
import {
    useFormContext,
    TIPOS_INVOLUCRADO,
    TIPOS_REGISTRO,
    TIPOS_REQUIEREN_DENUNCIANTE,
    type TipoInvolucrado,
    type TipoRegistro,
    type Involucrado,
} from '../../../context/FormContext'
import { useDelitoCategories }   from '../../../hooks/useDelitoCategories'
import { MapaPicker } from '../../ui/MapaPicker'
import { useDelitoList }         from './useDelitoList'
import { crearPayloadIncidente } from './crearPayloadIncidente'
import { apiClient }             from '../../../services/api'
import type { ExpedienteDetalleResponse } from '../../../types/api.types'

// ─── Tipos de errores por involucrado ────────────────────────────────────────

interface InvolucradoFieldErrors {
    nombre?:         string
    identificacion?: string
    nacionalidad?:   string
    telefono?:       string
    direccion?:      string
    foto?:           string
}

// Mapa: id del involucrado → sus errores de campo
type InvolucradoErrorMap = Record<number, InvolucradoFieldErrors>

// ─── Colores por tipo de registro ─────────────────────────────────────────────

const TIPO_REGISTRO_COLORS: Record<TipoRegistro, string> = {
    denuncia_formal:    'border-cyan-400    bg-cyan-400/15    text-cyan-300',
    denuncia_anonima:   'border-cyan-400/60 bg-cyan-400/8     text-cyan-400',
    de_oficio:          'border-blue-400    bg-blue-400/15    text-blue-300',
    llamada_emergencia: 'border-red-400     bg-red-400/15     text-red-300',
    reporte_ciudadano:  'border-amber-400   bg-amber-400/15   text-amber-300',
    flagrancia:         'border-orange-400  bg-orange-400/15  text-orange-300',
    '':                 'border-cyan-400/30 bg-transparent    text-cyan-500',
}

// ─── Validación de identificación ─────────────────────────────────────────────
function validarIdentificacion(valor: string): string | null {
    const trimmed = valor.trim()
    if (!trimmed) return 'La identificación es obligatoria'

    const sinPrefijo = trimmed.replace(/^[VEve]-?/, '')

    if (!/^\d+$/.test(sinPrefijo)) {
        return 'La identificación solo puede contener números (ej. 12345678 o V-12345678)'
    }
    if (sinPrefijo.length < 5) {
        return 'La identificación debe tener al menos 5 dígitos'
    }
    return null
}

// ─── Validación completa de un involucrado ────────────────────────────────────
function validarInvolucrado(inv: Involucrado): InvolucradoFieldErrors {
    const errors: InvolucradoFieldErrors = {}

    if (!inv.nombre.trim())
        errors.nombre = 'El nombre es obligatorio'

    const idError = validarIdentificacion(inv.identificacion)
    if (idError) errors.identificacion = idError

    if (!inv.nacionalidad.trim())
        errors.nacionalidad = 'La nacionalidad es obligatoria'

    if (!inv.telefono.trim())
        errors.telefono = 'El número de teléfono es obligatorio'

    if (!inv.direccion.trim())
        errors.direccion = 'La dirección es obligatoria'

    if (inv.tipoInvolucrado === 'victima' && !inv.foto)
        errors.foto = 'La fotografía es obligatoria para la víctima'

    return errors
}

// ─── Componente principal ─────────────────────────────────────────────────────

export const RegistroDelHecho = () => {
    const { showToast, ToastContainer } = useNeonToast()
    const {
        formData,
        updateFormData,
        addInvolucrado,
        removeInvolucrado,
        updateInvolucrado,
        resetForm,
    } = useFormContext()

    const { tipos, loading: loadingTipos, warning: tiposWarning } = useDelitoCategories()
    const { delitos, updateDelito, addDelito, removeDelito, validateTiempos, resetDelitos } = useDelitoList()

    const { setExpedienteActivo } = useExpedienteActivo()

    const [isSubmitting, setIsSubmitting]           = useState(false)
    const [involucradoErrors, setInvolucradoErrors] = useState<InvolucradoErrorMap>({})

    const [dropdownOpen, setDropdownOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (!dropdownOpen) return
        const handler = (e: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node))
                setDropdownOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [dropdownOpen])

    const requiresDenunciante = TIPOS_REQUIEREN_DENUNCIANTE.includes(formData.tipoRegistro)

    // ── GPS ───────────────────────────────────────────────────────────────────
    const [gpsMode, setGpsMode] = useState<'actual' | 'none'>('actual')

    // ── Modo Cronología ───────────────────────────────────────────────────────
    interface MomentoEntry { id: string; hora: string; texto: string }
    const [modoCronologia, setModoCronologia] = useState(false)
    const [momentos, setMomentos] = useState<MomentoEntry[]>([
        { id: crypto.randomUUID(), hora: '', texto: '' },
    ])

    const addMomento    = () =>
        setMomentos(prev => [...prev, { id: crypto.randomUUID(), hora: '', texto: '' }])
    const removeMomento = (id: string) =>
        setMomentos(prev => prev.length > 1 ? prev.filter(m => m.id !== id) : prev)
    const updateMomento = (id: string, patch: Partial<MomentoEntry>) =>
        setMomentos(prev =>
            [...prev.map(m => m.id === id ? { ...m, ...patch } : m)]
                .sort((a, b) => {
                    if (!a.hora && !b.hora) return 0
                    if (!a.hora) return 1
                    if (!b.hora) return -1
                    return a.hora.localeCompare(b.hora)
                }),
        )
    const momentosToTexto = () =>
        momentos
            .filter(m => m.hora || m.texto)
            .map(m => `[${m.hora || '--:--'}] ${m.texto}`)
            .join('\n')

    // ── Limpiar error de un campo al escribir ──────────────────────────────────
    const clearFieldError = (invId: number, field: keyof InvolucradoFieldErrors) => {
        setInvolucradoErrors(prev => {
            if (!prev[invId]) return prev
            const updated = { ...prev[invId] }
            delete updated[field]
            return { ...prev, [invId]: updated }
        })
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    const handleGuardar = async () => {
        const newErrors: InvolucradoErrorMap = {}
        let hayErrores = false

        formData.involucrados.forEach(inv => {
            const fieldErrors = validarInvolucrado(inv)
            if (Object.keys(fieldErrors).length > 0) {
                newErrors[inv.id] = fieldErrors
                hayErrores = true
            }
        })

        setInvolucradoErrors(newErrors)
        if (hayErrores) {
            showToast('Corrige los errores en los involucrados antes de guardar', 'error')
            return
        }

        const tieneGps    = formData.lat !== null && formData.lng !== null
        const tieneManual = formData.municipio.trim() !== '' ||
            formData.ubicacionDireccion.trim() !== ''

        if (!tieneGps && !tieneManual) {
            showToast('Debes registrar la ubicación del hecho (GPS o manual)', 'error')
            return
        }

        setIsSubmitting(true)
        const descripcionFinal = modoCronologia ? momentosToTexto() : formData.descripcion
        const payload = crearPayloadIncidente({
            formData: { ...formData, descripcion: descripcionFinal },
            delitos,
            gpsMode,
        })

        try {
            const expedienteCreado = await apiClient.post<{ data: ExpedienteDetalleResponse }>('/incidentes', payload)
            setExpedienteActivo(expedienteCreado.data)

            showToast('Expediente registrado exitosamente', 'success')

            resetForm()
            resetDelitos()
            setInvolucradoErrors({})
            setGpsMode('actual')
            setModoCronologia(false)
            setMomentos([{ id: crypto.randomUUID(), hora: '', texto: '' }])

        } catch (err) {
            const mensaje = err instanceof Error ? err.message : 'Error desconocido'
            showToast(`Error al guardar el incidente: ${mensaje}`, 'error')
        } finally {
            setIsSubmitting(false)
        }
    }

    // ── Opciones estáticas ────────────────────────────────────────────────────
    const tipoInvolucradoOptions = [
        { value: '', label: '— Seleccione —' },
        ...TIPOS_INVOLUCRADO.map(t => ({ value: t.value, label: t.label })),
    ]

    const selectedTipoLabel = TIPOS_REGISTRO.find(t => t.value === formData.tipoRegistro)?.label

    return (
        <div className="pb-6 space-y-4">

            {/* Toast container global */}
            <ToastContainer />

            {/* Aviso de backend no disponible */}
            {tiposWarning && (
                <div className="flex items-center gap-2 px-3 py-2 border border-amber-400/40 rounded bg-amber-400/5 text-[10px] uppercase tracking-wider text-amber-400">
                    <AlertTriangle size={12} style={{ filter: 'drop-shadow(0 0 4px rgba(255,170,0,0.6))' }} />
                    {tiposWarning}
                </div>
            )}

            <NeonPanel title="CAPTURA DEL HECHO" className="space-y-6">

                {/* ══ TIPO DE REGISTRO ══════════════════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Tipo de Registro <span className="text-red-400">*</span>
                    </div>

                    <div className="relative w-full sm:w-72" ref={dropdownRef}>
                        {/* Botón principal */}
                        <button
                            onClick={() => setDropdownOpen(v => !v)}
                            className={[
                                'w-full flex items-center justify-between gap-3',
                                'px-4 py-2.5 border-2 rounded',
                                'text-[11px] uppercase tracking-[0.12em] font-medium',
                                'transition-all duration-300',
                                formData.tipoRegistro
                                    ? TIPO_REGISTRO_COLORS[formData.tipoRegistro]
                                    : 'border-cyan-400/30 text-cyan-500 hover:border-cyan-400/60 hover:text-cyan-400',
                            ].join(' ')}
                            style={formData.tipoRegistro ? {
                                boxShadow: '0 2px 12px rgba(51,153,255,0.25), inset 0 1px 3px rgba(51,153,255,0.08)',
                            } : undefined}
                        >
                            <span className="flex items-center gap-2">
                                <FileText size={14} />
                                {selectedTipoLabel ?? 'Seleccionar tipo de registro…'}
                            </span>
                            <ChevronDown
                                size={14}
                                className={`transition-transform duration-300 flex-shrink-0 ${dropdownOpen ? 'rotate-180' : ''}`}
                                style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.5))' }}
                            />
                        </button>

                        {/* Dropdown */}
                        {dropdownOpen && (
                            <div
                                className="absolute top-full left-0 right-0 mt-1.5 z-50 border-2 border-cyan-400/60 bg-[#060B10]/98 backdrop-blur-sm rounded overflow-hidden"
                                style={{ boxShadow: '0 4px 20px rgba(51,153,255,0.4), 0 8px 40px rgba(51,153,255,0.2)' }}
                            >
                                {TIPOS_REGISTRO.map((tipo, i) => (
                                    <button
                                        key={tipo.value}
                                        onClick={() => {
                                            updateFormData({ tipoRegistro: tipo.value })
                                            setDropdownOpen(false)
                                        }}
                                        className={[
                                            'w-full text-left px-4 py-2.5',
                                            'text-[11px] uppercase tracking-[0.12em]',
                                            'transition-all duration-200',
                                            i < TIPOS_REGISTRO.length - 1 ? 'border-b border-cyan-400/20' : '',
                                            formData.tipoRegistro === tipo.value
                                                ? 'bg-cyan-400/15 text-cyan-300'
                                                : 'text-cyan-400 hover:bg-cyan-400/10 hover:text-cyan-300',
                                        ].join(' ')}
                                    >
                                        {tipo.label}
                                        {TIPOS_REQUIEREN_DENUNCIANTE.includes(tipo.value) && (
                                            <span className="ml-2 text-[9px] text-emerald-400/70 normal-case tracking-normal">
                                                (requiere denunciante)
                                            </span>
                                        )}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    {!formData.tipoRegistro && (
                        <p className="mt-2 text-[10px] text-cyan-600 tracking-wide">
                            Seleccione el tipo de registro antes de continuar.
                        </p>
                    )}
                </div>

                {/* ══ INVOLUCRADOS ═════════════════════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-1 font-medium">
                        Involucrados
                    </div>
                    {requiresDenunciante && (
                        <p className="text-[10px] text-emerald-500/70 mb-3 tracking-wide">
                            Este tipo de registro requiere que el primer involucrado sea el <span className="text-emerald-400 font-semibold">Denunciante</span>. Puede agregar más involucrados a continuación.
                        </p>
                    )}

                    <div className="space-y-4">
                        {formData.involucrados.map((inv, index) => {
                            const isDenuncianteFijo = requiresDenunciante && index === 0
                            const esVictima         = inv.tipoInvolucrado === 'victima'
                            const errs              = involucradoErrors[inv.id] ?? {}

                            return (
                                <div
                                    key={inv.id}
                                    className={[
                                        'relative p-4 border rounded',
                                        isDenuncianteFijo
                                            ? 'border-emerald-400/50 bg-emerald-400/5'
                                            : 'border-cyan-400/30 bg-[#050F1C]/40',
                                    ].join(' ')}
                                    style={{
                                        boxShadow: isDenuncianteFijo
                                            ? '0 2px 12px rgba(0,255,136,0.18), inset 0 1px 3px rgba(0,255,136,0.06)'
                                            : '0 2px 8px rgba(51,153,255,0.12), inset 0 1px 3px rgba(51,153,255,0.05)',
                                    }}
                                >
                                    {/* Botón eliminar */}
                                    {!isDenuncianteFijo && formData.involucrados.length > 1 && (
                                        <button
                                            onClick={() => removeInvolucrado(inv.id)}
                                            className="absolute top-2 right-2 text-red-400 hover:text-red-300 transition-colors p-1 rounded hover:bg-red-400/10"
                                        >
                                            <X size={16} />
                                        </button>
                                    )}

                                    {/* Encabezado de tarjeta */}
                                    {isDenuncianteFijo ? (
                                        <div className="text-[10px] uppercase tracking-wider text-emerald-400 mb-3 font-semibold flex items-center gap-1.5">
                                            <span style={{ filter: 'drop-shadow(0 0 4px rgba(0,255,136,0.6))' }}>◆</span>
                                            Denunciante <span className="text-red-400">*</span>
                                            <span className="ml-1 text-emerald-500/50 font-normal normal-case tracking-normal">(obligatorio)</span>
                                        </div>
                                    ) : (
                                        <div className="text-[10px] uppercase tracking-wider text-cyan-500/70 mb-3">
                                            Involucrado #{requiresDenunciante ? index : index + 1}
                                        </div>
                                    )}

                                    <div className="space-y-3">
                                        {/* Tipo de involucrado */}
                                        <NeonSelect
                                            label="Tipo de Involucrado"
                                            required
                                            options={tipoInvolucradoOptions}
                                            value={isDenuncianteFijo ? 'denunciante' : inv.tipoInvolucrado}
                                            onChange={e =>
                                                !isDenuncianteFijo && updateInvolucrado(inv.id, {
                                                    tipoInvolucrado: e.target.value as TipoInvolucrado,
                                                })
                                            }
                                        />

                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {/* Nombre */}
                                            <NeonInput
                                                label="Nombre y Apellido"
                                                required
                                                placeholder={isDenuncianteFijo
                                                    ? 'Nombre completo del denunciante…'
                                                    : 'Nombre completo del involucrado…'}
                                                value={inv.nombre}
                                                error={!!errs.nombre}
                                                errorMessage={errs.nombre}
                                                onChange={e => {
                                                    updateInvolucrado(inv.id, { nombre: e.target.value })
                                                    if (errs.nombre) clearFieldError(inv.id, 'nombre')
                                                }}
                                            />
                                            {/* Identificación */}
                                            <NeonInput
                                                label="Identificación"
                                                required
                                                placeholder="Ej. 12345678 o V-12345678"
                                                value={inv.identificacion}
                                                error={!!errs.identificacion}
                                                errorMessage={errs.identificacion}
                                                onChange={e => {
                                                    updateInvolucrado(inv.id, { identificacion: e.target.value })
                                                    if (errs.identificacion) clearFieldError(inv.id, 'identificacion')
                                                }}
                                            />
                                        </div>

                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {/* Nacionalidad */}
                                            <NeonInput
                                                label="Nacionalidad"
                                                required
                                                placeholder="Ej. Venezolana, Colombiana…"
                                                value={inv.nacionalidad}
                                                error={!!errs.nacionalidad}
                                                errorMessage={errs.nacionalidad}
                                                onChange={e => {
                                                    updateInvolucrado(inv.id, { nacionalidad: e.target.value })
                                                    if (errs.nacionalidad) clearFieldError(inv.id, 'nacionalidad')
                                                }}
                                            />
                                            {/* Teléfono */}
                                            <NeonInput
                                                label="Número de Teléfono"
                                                required
                                                placeholder="Ej. +58 412-1234567…"
                                                value={inv.telefono}
                                                error={!!errs.telefono}
                                                errorMessage={errs.telefono}
                                                onChange={e => {
                                                    updateInvolucrado(inv.id, { telefono: e.target.value })
                                                    if (errs.telefono) clearFieldError(inv.id, 'telefono')
                                                }}
                                            />
                                        </div>

                                        {/* Dirección */}
                                        <NeonInput
                                            label="Dirección"
                                            required
                                            placeholder="Dirección de residencia…"
                                            value={inv.direccion}
                                            error={!!errs.direccion}
                                            errorMessage={errs.direccion}
                                            onChange={e => {
                                                updateInvolucrado(inv.id, { direccion: e.target.value })
                                                if (errs.direccion) clearFieldError(inv.id, 'direccion')
                                            }}
                                        />

                                        {/* Campo Relación con el crimen — solo para denunciante */}
                                        {isDenuncianteFijo && (
                                            <NeonInput
                                                label="Relación con el Crimen"
                                                required
                                                placeholder="Ej: Testigo, Familiar, Víctima indirecta…"
                                                value={formData.denuncianteRelacion ?? ''}
                                                onChange={e => updateFormData({ denuncianteRelacion: e.target.value })}
                                            />
                                        )}

                                        {/* Fotografía — (obligatoria para víctima) */}
                                        <div className="flex flex-col gap-1.5">
                                            <label className={[
                                                'text-[11px] uppercase tracking-[0.1em] font-medium',
                                                isDenuncianteFijo ? 'text-emerald-400' : 'text-cyan-400',
                                            ].join(' ')}>
                                                {isDenuncianteFijo
                                                    ? 'Fotografía del Denunciante'
                                                    : `Fotografía del Involucrado${esVictima ? ' *' : ''}`}
                                                {esVictima && (
                                                    <span className="ml-2 normal-case text-[9px] text-cyan-500/60 tracking-normal font-normal">
                                                        (obligatoria para víctima)
                                                    </span>
                                                )}
                                            </label>
                                            <label className="cursor-pointer">
                                                <input
                                                    type="file"
                                                    accept="image/*"
                                                    className="sr-only"
                                                    onChange={e => {
                                                        const file = e.target.files?.[0] ?? null
                                                        updateInvolucrado(inv.id, { foto: file })
                                                        if (errs.foto) clearFieldError(inv.id, 'foto')
                                                    }}
                                                />
                                                {inv.foto ? (
                                                    <div
                                                        className={[
                                                            'relative w-24 h-24 border-2 rounded overflow-hidden group',
                                                            isDenuncianteFijo
                                                                ? 'border-emerald-400/60'
                                                                : errs.foto
                                                                    ? 'border-red-400/70'
                                                                    : 'border-cyan-400/60',
                                                        ].join(' ')}
                                                        style={{ boxShadow: isDenuncianteFijo ? '0 0 12px rgba(0,255,136,0.3)' : '0 0 12px rgba(51,153,255,0.3)' }}
                                                    >
                                                        <img
                                                            src={URL.createObjectURL(inv.foto)}
                                                            alt="Foto involucrado"
                                                            className="w-full h-full object-cover"
                                                        />
                                                        <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                                                            <span className={['text-[9px] uppercase tracking-wider', isDenuncianteFijo ? 'text-emerald-300' : 'text-cyan-300'].join(' ')}>Cambiar</span>
                                                        </div>
                                                    </div>
                                                ) : (
                                                    <div
                                                        className={[
                                                            'w-24 h-24 border-2 border-dashed rounded flex flex-col items-center justify-center gap-1 transition-all',
                                                            isDenuncianteFijo
                                                                ? 'border-emerald-400/30 hover:border-emerald-400/60 hover:bg-emerald-400/5'
                                                                : errs.foto
                                                                    ? 'border-red-400/60 bg-red-400/5 hover:border-red-400/80'
                                                                    : 'border-cyan-400/30 hover:border-cyan-400/60 hover:bg-cyan-400/5',
                                                        ].join(' ')}
                                                        style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.05)' }}
                                                    >
                                                        <span className={
                                                            isDenuncianteFijo
                                                                ? 'text-emerald-600'
                                                                : errs.foto
                                                                    ? 'text-red-500'
                                                                    : 'text-cyan-600'
                                                        } style={{ fontSize: 22 }}>＋</span>
                                                        <span className={[
                                                            'text-[9px] uppercase tracking-wider',
                                                            isDenuncianteFijo
                                                                ? 'text-emerald-600'
                                                                : errs.foto
                                                                    ? 'text-red-500'
                                                                    : 'text-cyan-600',
                                                        ].join(' ')}>Foto</span>
                                                    </div>
                                                )}
                                            </label>
                                            {/* Mensaje de error de foto */}
                                            {errs.foto && (
                                                <span
                                                    className="text-[10px] text-red-400 tracking-wide"
                                                    style={{ textShadow: '0 0 5px rgba(255,75,75,0.4)' }}
                                                >
                                                    ⚠ {errs.foto}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )
                        })}

                        <NeonButton variant="outline" icon={<Plus size={14} />} onClick={addInvolucrado}>
                            Agregar Involucrado
                        </NeonButton>
                    </div>
                </div>

                {/* ══ DETALLES DEL DELITO ══════════════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Detalles del Delito
                    </div>

                    <div className="space-y-4">
                        {delitos.map((delito, index) => {
                            const subtiposDelito = tipos.find(t => t.value === delito.tipoValue)?.subtipos ?? []

                            const tipoOpts = [
                                { value: '', label: loadingTipos ? 'Cargando…' : '— Seleccione —' },
                                ...tipos.map(t => ({ value: t.value, label: t.label })),
                            ]
                            const subtipoOpts = [
                                {
                                    value: '',
                                    label: delito.tipoValue ? '— Seleccione —' : '— Seleccione tipo primero —',
                                },
                                ...subtiposDelito,
                            ]

                            return (
                                <div
                                    key={delito.id}
                                    className="relative p-4 border border-cyan-400/30 rounded bg-[#050F1C]/40 space-y-3"
                                    style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.12), inset 0 1px 3px rgba(51,153,255,0.05)' }}
                                >
                                    {delitos.length > 1 && (
                                        <>
                                            <button
                                                onClick={() => removeDelito(delito.id)}
                                                className="absolute top-2 right-2 text-red-400 hover:text-red-300 transition-colors p-1 rounded hover:bg-red-400/10"
                                            >
                                                <X size={16} />
                                            </button>
                                            <div className="text-[10px] uppercase tracking-wider text-cyan-500/70">
                                                Delito #{index + 1}
                                            </div>
                                        </>
                                    )}

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        <NeonSelect
                                            label="Tipo de Delito" required
                                            options={tipoOpts}
                                            disabled={loadingTipos}
                                            value={delito.tipoValue}
                                            onChange={e => {
                                                const val   = e.target.value
                                                const label = tipos.find(t => t.value === val)?.label ?? ''
                                                updateDelito(delito.id, {
                                                    tipoValue: val, tipoLabel: label,
                                                    subtipoValue: '', subtipoLabel: '',
                                                })
                                            }}
                                        />
                                        <NeonSelect
                                            label="Subtipo" required
                                            options={subtipoOpts}
                                            disabled={!delito.tipoValue || loadingTipos}
                                            value={delito.subtipoValue}
                                            onChange={e => {
                                                const val   = e.target.value
                                                const label = subtiposDelito.find(s => s.value === val)?.label ?? ''
                                                updateDelito(delito.id, { subtipoValue: val, subtipoLabel: label })
                                            }}
                                        />
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Fecha del Hecho" required type="date"
                                            value={delito.fechaHecho}
                                            error={delito.fechaError}
                                            onChange={e => {
                                                const val      = e.target.value
                                                const isFuture = val > new Date().toISOString().split('T')[0]
                                                updateDelito(delito.id, { fechaHecho: val, fechaError: isFuture })
                                            }}
                                        />
                                    </div>
                                    {delito.fechaError && (
                                        <div className="text-[10px] text-red-400 px-2 py-1 border border-red-400/40 rounded inline-block" style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}>
                                            ⚠ La fecha del hecho no puede ser futura
                                        </div>
                                    )}

                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Hora de Inicio *" type="time"
                                            value={delito.horaInicio}
                                            error={delito.tiempoError && !delito.horaInicio}
                                            onChange={e => {
                                                updateDelito(delito.id, { horaInicio: e.target.value })
                                                validateTiempos(delito.id, e.target.value, delito.horaFin, delito.hechoEnCurso)
                                            }}
                                        />
                                        <NeonInput
                                            label="Hora de Finalización" type="time"
                                            value={delito.horaFin}
                                            disabled={delito.hechoEnCurso}
                                            error={delito.tiempoError && !delito.horaFin && !delito.hechoEnCurso}
                                            onChange={e => {
                                                updateDelito(delito.id, { horaFin: e.target.value })
                                                validateTiempos(delito.id, delito.horaInicio, e.target.value, delito.hechoEnCurso)
                                            }}
                                        />
                                    </div>

                                    <NeonCheckbox
                                        label="El hecho aún está sucediendo"
                                        checked={delito.hechoEnCurso}
                                        onChange={e => {
                                            updateDelito(delito.id, { hechoEnCurso: e.target.checked, horaFin: '' })
                                            validateTiempos(delito.id, delito.horaInicio, '', e.target.checked)
                                        }}
                                    />
                                    {delito.tiempoError && (
                                        <div className="text-[10px] text-red-400 px-2 py-1 border border-red-400/40 rounded inline-block" style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}>
                                            ⚠ {delito.tiempoErrorMsg}
                                        </div>
                                    )}
                                </div>
                            )
                        })}

                        <NeonButton variant="outline" icon={<Plus size={14} />} onClick={addDelito}>
                            Agregar Delito
                        </NeonButton>
                    </div>

                    {/* Descripción del hecho */}
                    <div className="mt-4">
                        <div className="flex items-center justify-between mb-1.5">
                            <span className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                                Descripción del Hecho <span className="text-red-400">*</span>
                            </span>
                            <button
                                type="button"
                                onClick={() => setModoCronologia(v => !v)}
                                className={[
                                    'flex items-center gap-1.5 px-2.5 py-1 rounded text-[10px] uppercase tracking-wider font-medium border transition-all',
                                    modoCronologia
                                        ? 'border-cyan-400/70 bg-cyan-400/10 text-cyan-300'
                                        : 'border-cyan-400/30 bg-transparent text-cyan-600 hover:border-cyan-400/60 hover:text-cyan-400',
                                ].join(' ')}
                            >
                                <Clock size={11} />
                                {modoCronologia ? 'Modo Texto' : 'Modo Cronología'}
                            </button>
                        </div>

                        {!modoCronologia && (
                            <NeonTextarea
                                label=""
                                required
                                placeholder="Ingrese la narrativa detallada de los hechos…"
                                rows={6}
                                showCount
                                maxCount={1000}
                                value={formData.descripcion}
                                onChange={e => updateFormData({ descripcion: e.target.value })}
                            />
                        )}

                        {modoCronologia && (
                            <div className="space-y-2">
                                {momentos.map((momento, idx) => (
                                    <div
                                        key={momento.id}
                                        className="flex items-center gap-2 p-2 border border-cyan-400/20 rounded bg-[#050F1C]/40"
                                        style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.04)' }}
                                    >
                                        <span className="text-[10px] text-cyan-600/60 w-4 text-right shrink-0">{idx + 1}</span>
                                        <input
                                            type="time"
                                            value={momento.hora}
                                            onChange={e => updateMomento(momento.id, { hora: e.target.value })}
                                            className="bg-transparent border border-cyan-400/30 rounded px-2 py-1.5 text-[12px] text-cyan-200 w-[7rem] shrink-0 focus:outline-none focus:border-cyan-400/70 [color-scheme:dark]"
                                        />
                                        <input
                                            type="text"
                                            value={momento.texto}
                                            onChange={e => updateMomento(momento.id, { texto: e.target.value })}
                                            placeholder="Descripción del momento…"
                                            className="flex-1 bg-transparent border border-cyan-400/30 rounded px-2 py-1.5 text-[12px] text-cyan-100 placeholder-cyan-700 focus:outline-none focus:border-cyan-400/70"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => removeMomento(momento.id)}
                                            disabled={momentos.length === 1}
                                            className="text-red-400/60 hover:text-red-400 disabled:opacity-20 disabled:cursor-not-allowed transition-colors p-1 rounded hover:bg-red-400/10 shrink-0"
                                        >
                                            <X size={14} />
                                        </button>
                                    </div>
                                ))}

                                <button
                                    type="button"
                                    onClick={addMomento}
                                    className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider text-cyan-500 hover:text-cyan-300 border border-dashed border-cyan-400/30 hover:border-cyan-400/60 rounded px-3 py-1.5 transition-all w-full justify-center"
                                >
                                    <Plus size={12} /> Agregar momento
                                </button>

                                {momentos.some(m => m.hora || m.texto) && (
                                    <div className="mt-2 p-3 border border-cyan-400/15 rounded bg-[#020810]/60">
                                        <div className="text-[9px] uppercase tracking-wider text-cyan-600 mb-1.5 flex items-center gap-1">
                                            <AlignLeft size={9} /> Vista previa
                                        </div>
                                        <pre className="text-[11px] text-cyan-300/70 whitespace-pre-wrap font-mono leading-relaxed">
                                            {momentosToTexto()}
                                        </pre>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>

                {/* ══ CRONOLOGÍA DEL REPORTE ═══════════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Cronología del Reporte
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div className="flex flex-col gap-1.5">
                            <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                                Fecha del Reporte
                            </label>
                            <div
                                className="px-3 py-2.5 border border-cyan-400/20 rounded bg-cyan-400/5
                                text-cyan-400 text-sm font-mono tracking-wider"
                                style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.06)' }}
                            >
                                {formData.fechaReporte}
                            </div>
                            <span className="text-[10px] text-cyan-700">
                                Generado automáticamente
                            </span>
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                                Hora del Reporte
                            </label>
                            <div
                                className="px-3 py-2.5 border border-cyan-400/20 rounded bg-cyan-400/5
                                text-cyan-400 text-sm font-mono tracking-wider"
                                style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.06)' }}
                            >
                                {formData.horaReporte}
                            </div>
                            <span className="text-[10px] text-cyan-700">
                                Generado automáticamente
                            </span>
                        </div>
                    </div>
                </div>

                {/* ══ UBICACIÓN Y MAPA ═════════════════════════════════════ */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 flex items-center gap-2 font-medium">
                        <MapPin size={14} /> Ubicación y Mapa
                    </div>

                    <div className="flex gap-6 mb-4">
                        <NeonRadio
                            label="Capturar GPS Actual"
                            name="gps"
                            checked={gpsMode === 'actual'}
                            onChange={() => setGpsMode('actual')}
                        />
                        <NeonRadio
                            label="Ingresar manualmente"
                            name="gps"
                            checked={gpsMode === 'none'}
                            onChange={() => setGpsMode('none')}
                        />
                    </div>

                    {gpsMode === 'actual' ? (
                        <MapaPicker
                            onChange={(coords) =>
                                updateFormData({ lat: coords.lat, lng: coords.lng })
                            }
                            initialCoords={
                                formData.lat && formData.lng
                                    ? { lat: formData.lat, lng: formData.lng }
                                    : null
                            }
                        />
                    ) : (
                        <div className="space-y-3">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Municipio" placeholder="Nombre del municipio…"
                                    value={formData.municipio ?? ''}
                                    onChange={e => updateFormData({ municipio: e.target.value })}
                                />
                                <NeonInput
                                    label="Sector" placeholder="Calle, avenida…"
                                    value={formData.sector ?? ''}
                                    onChange={e => updateFormData({ sector: e.target.value })}
                                />
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Dirección" placeholder="Ej: Avenida…"
                                    value={formData.ubicacionDireccion ?? ''}
                                    onChange={e => updateFormData({ ubicacionDireccion: e.target.value })}
                                />
                                <NeonInput
                                    label="Referencia" placeholder="Ej: Frente al parque Central"
                                    value={formData.referencia ?? ''}
                                    onChange={e => updateFormData({ referencia: e.target.value })}
                                />
                            </div>
                            <div
                                className="h-48 border-2 border-cyan-400/50 rounded bg-[#020818] flex items-center justify-center relative overflow-hidden"
                                style={{ boxShadow: '0 2px 12px rgba(51,153,255,0.25), inset 0 1px 5px rgba(51,153,255,0.1)' }}
                            >
                                <div className="relative">
                                    <div
                                        className="w-20 h-20 rounded-full border-2 border-cyan-400 animate-pulse"
                                        style={{ boxShadow: '0 0 25px rgba(51,153,255,0.6), 0 0 50px rgba(51,153,255,0.3)' }}
                                    />
                                    <MapPin
                                        className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-cyan-400"
                                        size={28}
                                        style={{ filter: 'drop-shadow(0 0 8px rgba(51,153,255,0.8))' }}
                                    />
                                </div>
                                <div className="absolute bottom-3 left-3 text-[10px] text-cyan-500/70 uppercase tracking-wider">
                                    Vista previa de mapa
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* ══ ACCIONES ════════════════════════════════════════════ */}
                <div className="flex flex-col sm:flex-row justify-end gap-3 pt-4">
                    <NeonButton variant="outline" disabled={isSubmitting}>
                        Cancelar
                    </NeonButton>
                    <NeonButton
                        variant="primary"
                        onClick={handleGuardar}
                        disabled={!formData.tipoRegistro || isSubmitting}
                        title={!formData.tipoRegistro ? 'Seleccione el tipo de registro primero' : undefined}
                        icon={isSubmitting ? <Loader2 size={14} className="animate-spin" /> : undefined}
                    >
                        {isSubmitting ? 'Guardando…' : 'Guardar'}
                    </NeonButton>
                </div>

            </NeonPanel>
        </div>
    )
}