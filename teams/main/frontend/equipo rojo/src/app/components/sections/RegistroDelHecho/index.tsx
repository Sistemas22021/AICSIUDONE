import { useState } from 'react'
import { MapPin, Plus, X, AlertTriangle, Clock, AlignLeft } from 'lucide-react'
import { NeonPanel }    from '../../ui/NeonPanel'
import { NeonInput }    from '../../ui/NeonInput'
import { NeonSelect }   from '../../ui/NeonSelect'
import { NeonTextarea } from '../../ui/NeonTextarea'
import { NeonButton }   from '../../ui/NeonButton'
import { NeonRadio }    from '../../ui/NeonRadio'
import { NeonCheckbox } from '../../ui/NeonCheckbox'
import { useNeonToast } from '../../ui/NeonToast'
import { useFormContext }                        from '../../../context/FormContext'
import { TIPOS_INVOLUCRADO, type TipoInvolucrado } from '../../../context/FormContext'
import { useDelitoCategories }                  from '../../../hooks/useDelitoCategories'
import { useDelitoList }                        from './useDelitoList'
import { crearPayloadIncidente }                from './crearPayloadIncidente'
import { apiClient }                            from '../../../services/api'


export const RegistroDelHecho = () => {
    const { showToast } = useNeonToast()
    const {
        formData,
        updateFormData,
        addInvolucrado,
        removeInvolucrado,
        updateInvolucrado,
    } = useFormContext()

    const { tipos, loading: loadingTipos, warning: tiposWarning } = useDelitoCategories()
    const { delitos, updateDelito, addDelito, removeDelito, validateTiempos } = useDelitoList()

    const [gpsMode, setGpsMode]               = useState<'actual' | 'none'>('actual')
    const [isFormalComplaint, setIsFormal]    = useState(false)

    // ─── Modo Cronología ───────────────────────────────────────────────────────

    interface MomentoEntry {
        id: string
        hora: string
        texto: string
    }

    const [modoCronologia, setModoCronologia] = useState(false)
    const [momentos, setMomentos] = useState<MomentoEntry[]>([
        { id: crypto.randomUUID(), hora: '', texto: '' },
    ])

    const addMomento = () =>
        setMomentos(prev => [...prev, { id: crypto.randomUUID(), hora: '', texto: '' }])

    const removeMomento = (id: string) =>
        setMomentos(prev => prev.length > 1 ? prev.filter(m => m.id !== id) : prev)

    const updateMomento = (id: string, patch: Partial<MomentoEntry>) =>
        setMomentos(prev => {
            const updated = prev.map(m => (m.id === id ? { ...m, ...patch } : m))
            return [...updated].sort((a, b) => {
                if (!a.hora && !b.hora) return 0
                if (!a.hora) return 1
                if (!b.hora) return -1
                return a.hora.localeCompare(b.hora)
            })
        })

    const momentosToTexto = (): string =>
        momentos
            .filter(m => m.hora || m.texto)
            .map(m => `[${m.hora || '--:--'}] ${m.texto}`)
            .join('\n')

    // ─── Submit ────────────────────────────────────────────────────────────────

    const handleGuardar = async () => {
        // Si está en modo cronología, serializa los momentos como descripción
        const descripcionFinal = modoCronologia
            ? momentosToTexto()
            : formData.descripcion

        const payload = crearPayloadIncidente({
            formData: { ...formData, descripcion: descripcionFinal },
            delitos,
            isFormalComplaint,
            gpsMode,
        })
        try {
            await apiClient.post('/incidentes', payload)
            showToast('Incidente guardado correctamente')
        } catch (err) {
            showToast('Ocurrió un error al guardar el incidente', 'error')
        }
    }

    // ─── Opciones estáticas ────────────────────────────────────────────────────

    const tipoInvolucradoOptions = [
        { value: '', label: '— Seleccione —' },
        ...TIPOS_INVOLUCRADO.map(t => ({ value: t.value, label: t.label })),
    ]

    // ─── Render ────────────────────────────────────────────────────────────────

    return (
        <div className="pb-6 space-y-4">

            {/* Aviso cuando el backend no está disponible */}
            {tiposWarning && (
                <div className="flex items-center gap-2 px-3 py-2 border border-amber-400/40 rounded bg-amber-400/5 text-[10px] uppercase tracking-wider text-amber-400">
                    <AlertTriangle size={12} style={{ filter: 'drop-shadow(0 0 4px rgba(255,170,0,0.6))' }} />
                    {tiposWarning}
                </div>
            )}

            <NeonPanel title="CAPTURA DEL HECHO" className="space-y-6">

                {/* ── Geolocalización ─────────────────────────────────────── */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Geolocalización
                    </div>
                    <div className="space-y-2">
                        <NeonRadio
                            label="Capturar GPS Actual"
                            name="gps"
                            checked={gpsMode === 'actual'}
                            onChange={() => setGpsMode('actual')}
                        />
                        <NeonRadio
                            label="Sin coordenadas"
                            name="gps"
                            checked={gpsMode === 'none'}
                            onChange={() => setGpsMode('none')}
                        />
                    </div>
                </div>

                {/* ── Involucrados ─────────────────────────────────────────── */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Involucrados
                    </div>

                    <div className="space-y-4">
                        {formData.involucrados.map((inv, index) => (
                            <div
                                key={inv.id}
                                className="relative p-4 border border-cyan-400/30 rounded bg-[#050F1C]/40"
                                style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.12), inset 0 1px 3px rgba(51,153,255,0.05)' }}
                            >
                                {formData.involucrados.length > 1 && (
                                    <>
                                        <button
                                            onClick={() => removeInvolucrado(inv.id)}
                                            className="absolute top-2 right-2 text-red-400 hover:text-red-300 transition-colors p-1 rounded hover:bg-red-400/10"
                                        >
                                            <X size={16} />
                                        </button>
                                        <div className="text-[10px] uppercase tracking-wider text-cyan-500/70 mb-3">
                                            Involucrado #{index + 1}
                                        </div>
                                    </>
                                )}

                                <div className="space-y-3">
                                    <NeonSelect
                                        label="Tipo de Involucrado"
                                        required
                                        options={tipoInvolucradoOptions}
                                        value={inv.tipoInvolucrado}
                                        onChange={e =>
                                            updateInvolucrado(inv.id, {
                                                tipoInvolucrado: e.target.value as TipoInvolucrado,
                                            })
                                        }
                                    />

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Nombre y Apellido" required
                                            placeholder="Nombre completo del involucrado…"
                                            value={inv.nombre}
                                            onChange={e => updateInvolucrado(inv.id, { nombre: e.target.value })}
                                        />
                                        <NeonInput
                                            label="Identificación" required
                                            placeholder="Número de cédula o identificación…"
                                            value={inv.identificacion}
                                            onChange={e => updateInvolucrado(inv.id, { identificacion: e.target.value })}
                                        />
                                    </div>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Nacionalidad"
                                            placeholder="Ej. Venezolana, Colombiana…"
                                            value={inv.nacionalidad}
                                            onChange={e => updateInvolucrado(inv.id, { nacionalidad: e.target.value })}
                                        />
                                        <NeonInput
                                            label="Número de Teléfono"
                                            placeholder="Ej. +58 412-1234567…"
                                            value={inv.telefono}
                                            onChange={e => updateInvolucrado(inv.id, { telefono: e.target.value })}
                                        />
                                    </div>

                                    <NeonInput
                                        label="Dirección"
                                        placeholder="Dirección de residencia…"
                                        value={inv.direccion}
                                        onChange={e => updateInvolucrado(inv.id, { direccion: e.target.value })}
                                    />

                                    {/* Fotografía */}
                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                                            Fotografía del Involucrado
                                        </label>
                                        <label className="cursor-pointer">
                                            <input
                                                type="file"
                                                accept="image/*"
                                                className="sr-only"
                                                onChange={e => {
                                                    const file = e.target.files?.[0] ?? null
                                                    updateInvolucrado(inv.id, { foto: file })
                                                }}
                                            />
                                            {inv.foto ? (
                                                <div
                                                    className="relative w-24 h-24 border-2 border-cyan-400/60 rounded overflow-hidden group"
                                                    style={{ boxShadow: '0 0 12px rgba(51,153,255,0.3)' }}
                                                >
                                                    <img
                                                        src={URL.createObjectURL(inv.foto)}
                                                        alt="Foto involucrado"
                                                        className="w-full h-full object-cover"
                                                    />
                                                    <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                                                        <span className="text-[9px] uppercase tracking-wider text-cyan-300">Cambiar</span>
                                                    </div>
                                                </div>
                                            ) : (
                                                <div
                                                    className="w-24 h-24 border-2 border-dashed border-cyan-400/30 rounded flex flex-col items-center justify-center gap-1 hover:border-cyan-400/60 hover:bg-cyan-400/5 transition-all"
                                                    style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.05)' }}
                                                >
                                                    <span className="text-cyan-600" style={{ fontSize: 22 }}>＋</span>
                                                    <span className="text-[9px] uppercase tracking-wider text-cyan-600">Foto</span>
                                                </div>
                                            )}
                                        </label>
                                    </div>
                                </div>
                            </div>
                        ))}

                        <NeonButton variant="outline" icon={<Plus size={14} />} onClick={addInvolucrado}>
                            Agregar Involucrado
                        </NeonButton>
                    </div>
                </div>

                {/* ── Detalles del Delito ──────────────────────────────────── */}
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

                                    {/* Tipo y subtipo */}
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        <NeonSelect
                                            label="Tipo de Delito"
                                            required
                                            options={tipoOpts}
                                            disabled={loadingTipos}
                                            value={delito.tipoValue}
                                            onChange={e => {
                                                const val   = e.target.value
                                                const label = tipos.find(t => t.value === val)?.label ?? ''
                                                updateDelito(delito.id, {
                                                    tipoValue:    val,
                                                    tipoLabel:    label,
                                                    subtipoValue: '',
                                                    subtipoLabel: '',
                                                })
                                            }}
                                        />
                                        <NeonSelect
                                            label="Subtipo"
                                            required
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

                                    {/* Fecha del hecho */}
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Fecha del Hecho"
                                            required
                                            type="date"
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
                                        <div
                                            className="text-[10px] text-red-400 px-2 py-1 border border-red-400/40 rounded inline-block"
                                            style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}
                                        >
                                            ⚠ La fecha del hecho no puede ser futura
                                        </div>
                                    )}

                                    {/* Horas */}
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                                        <NeonInput
                                            label="Hora de Inicio *"
                                            type="time"
                                            value={delito.horaInicio}
                                            error={delito.tiempoError && !delito.horaInicio}
                                            onChange={e => {
                                                updateDelito(delito.id, { horaInicio: e.target.value })
                                                validateTiempos(delito.id, e.target.value, delito.horaFin, delito.hechoEnCurso)
                                            }}
                                        />
                                        <NeonInput
                                            label="Hora de Finalización"
                                            type="time"
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
                                        <div
                                            className="text-[10px] text-red-400 px-2 py-1 border border-red-400/40 rounded inline-block"
                                            style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}
                                        >
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

                    {/* Descripción general — una sola por incidente */}
                    <div className="mt-4">

                        {/* Cabecera: label + toggle Modo Cronología */}
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

                        {/* ── Modo texto normal ── */}
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

                        {/* ── Modo cronología ── */}
                        {modoCronologia && (
                            <div className="space-y-2">
                                {momentos.map((momento, idx) => (
                                    <div
                                        key={momento.id}
                                        className="flex items-center gap-2 p-2 border border-cyan-400/20 rounded bg-[#050F1C]/40"
                                        style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.04)' }}
                                    >
                                        {/* Número de orden */}
                                        <span className="text-[10px] text-cyan-600/60 w-4 text-right shrink-0">
                      {idx + 1}
                    </span>

                                        {/* Campo hora */}
                                        <input
                                            type="time"
                                            value={momento.hora}
                                            onChange={e => updateMomento(momento.id, { hora: e.target.value })}
                                            className={[
                                                'bg-transparent border border-cyan-400/30 rounded px-2 py-1.5',
                                                'text-[12px] text-cyan-200 w-[7rem] shrink-0',
                                                'focus:outline-none focus:border-cyan-400/70',
                                                '[color-scheme:dark]',
                                            ].join(' ')}
                                        />

                                        {/* Campo descripción del momento */}
                                        <input
                                            type="text"
                                            value={momento.texto}
                                            onChange={e => updateMomento(momento.id, { texto: e.target.value })}
                                            placeholder="Descripción del momento…"
                                            className={[
                                                'flex-1 bg-transparent border border-cyan-400/30 rounded px-2 py-1.5',
                                                'text-[12px] text-cyan-100 placeholder-cyan-700',
                                                'focus:outline-none focus:border-cyan-400/70',
                                            ].join(' ')}
                                        />

                                        {/* Botón eliminar */}
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

                                {/* Vista previa del texto que se guardará */}
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

                {/* ── Cronología del Reporte ───────────────────────────────── */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
                        Cronología del Reporte
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <NeonInput
                            label="Fecha del Reporte"
                            required
                            type="date"
                            value={formData.fechaReporte}
                            onChange={e => updateFormData({ fechaReporte: e.target.value })}
                        />
                        <NeonInput
                            label="Hora del Reporte"
                            required
                            type="time"
                            value={formData.horaReporte}
                            onChange={e => updateFormData({ horaReporte: e.target.value })}
                        />
                    </div>
                </div>

                {/* ── Ubicación y Mapa ─────────────────────────────────────── */}
                <div>
                    <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 flex items-center gap-2 font-medium">
                        <MapPin size={14} /> Ubicación y Mapa
                    </div>

                    {gpsMode === 'actual' ? (
                        <div
                            className="p-4 border-2 border-cyan-400/50 rounded bg-cyan-400/5 flex items-center justify-center gap-2"
                            style={{ boxShadow: '0 2px 12px rgba(51,153,255,0.25), inset 0 1px 3px rgba(51,153,255,0.1)' }}
                        >
                            <MapPin
                                size={16}
                                className="text-cyan-400"
                                style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.7))' }}
                            />
                            <span className="text-xs text-cyan-300 uppercase tracking-[0.12em]">
                Ubicación obtenida por GPS — Coordenadas automáticas
              </span>
                        </div>
                    ) : (
                        <div className="space-y-3">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Municipio"
                                    placeholder="Nombre del municipio…"
                                    value={formData.municipio ?? ''}
                                    onChange={e => updateFormData({ municipio: e.target.value })}
                                />
                                <NeonInput
                                    label="Sector"
                                    placeholder="Calle, avenida…"
                                    value={formData.sector ?? ''}
                                    onChange={e => updateFormData({ sector: e.target.value })}
                                />
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Dirección"
                                    placeholder="Ej: Avenida…"
                                    value={formData.ubicacionDireccion ?? ''}
                                    onChange={e => updateFormData({ ubicacionDireccion: e.target.value })}
                                />
                                <NeonInput
                                    label="Referencia"
                                    placeholder="Ej: Frente al parque Central"
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

                {/* ── ¿Denuncia formal? ────────────────────────────────────── */}
                <div className="pt-2">
                    <NeonCheckbox
                        label="ES UNA DENUNCIA FORMAL"
                        checked={isFormalComplaint}
                        onChange={e => setIsFormal(e.target.checked)}
                    />
                </div>

                {/* ── Datos del Denunciante (condicional) ──────────────────── */}
                {isFormalComplaint && (
                    <div
                        className="border-2 border-emerald-400/40 rounded p-4 bg-emerald-400/5 animate-fade-in"
                        style={{ boxShadow: '0 2px 12px rgba(0,255,136,0.25), inset 0 1px 3px rgba(0,255,136,0.08)' }}
                    >
                        <div className="text-[11px] uppercase tracking-[0.12em] text-emerald-400 mb-3 font-medium">
                            Datos del Denunciante
                        </div>
                        <div className="space-y-3">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Nombre y Apellido" required
                                    placeholder="Nombre completo del denunciante…"
                                    value={formData.denuncianteNombre ?? ''}
                                    onChange={e => updateFormData({ denuncianteNombre: e.target.value })}
                                />
                                <NeonInput
                                    label="Teléfono" required
                                    placeholder="Teléfono de contacto…"
                                    value={formData.denuncianteTelefono ?? ''}
                                    onChange={e => updateFormData({ denuncianteTelefono: e.target.value })}
                                />
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                <NeonInput
                                    label="Identificación" required
                                    placeholder="Número de cédula o identificación…"
                                    value={formData.denuncianteIdentificacion ?? ''}
                                    onChange={e => updateFormData({ denuncianteIdentificacion: e.target.value })}
                                />
                                <NeonInput
                                    label="Nacionalidad" required
                                    placeholder="Ej. Venezolana, Colombiana…"
                                    value={formData.denuncianteNacionalidad ?? ''}
                                    onChange={e => updateFormData({ denuncianteNacionalidad: e.target.value })}
                                />
                            </div>
                            <NeonInput
                                label="Dirección del Denunciante" required
                                placeholder="Dirección de residencia…"
                                value={formData.denuncianteDireccion ?? ''}
                                onChange={e => updateFormData({ denuncianteDireccion: e.target.value })}
                            />
                            <NeonInput
                                label="Relación con el Crimen" required
                                placeholder="Ej: Testigo, Familiar, Víctima indirecta…"
                                value={formData.denuncianteRelacion ?? ''}
                                onChange={e => updateFormData({ denuncianteRelacion: e.target.value })}
                            />
                        </div>
                    </div>
                )}

                {/* ── Acciones ─────────────────────────────────────────────── */}
                <div className="flex flex-col sm:flex-row justify-end gap-3 pt-4">
                    <NeonButton variant="outline">Cancelar</NeonButton>
                    <NeonButton variant="primary" onClick={handleGuardar}>Guardar</NeonButton>
                </div>

            </NeonPanel>
        </div>
    )
}
