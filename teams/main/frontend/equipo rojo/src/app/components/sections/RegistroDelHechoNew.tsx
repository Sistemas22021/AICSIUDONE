import { useState, useEffect } from 'react'
import { MapPin, Plus, X, AlertTriangle } from 'lucide-react'
import { NeonPanel } from '../ui/NeonPanel'
import { NeonInput } from '../ui/NeonInput'
import { NeonSelect } from '../ui/NeonSelect'
import { NeonTextarea } from '../ui/NeonTextarea'
import { NeonButton } from '../ui/NeonButton'
import { NeonRadio } from '../ui/NeonRadio'
import { NeonCheckbox } from '../ui/NeonCheckbox'
import { useFormContext } from '../../context/FormContext'
import { useDelitoCategories } from '../../hooks/useDelitoCategories'
import * as React from "react";

export const RegistroDelHecho = () => {
  const { formData, updateFormData, addVictim, removeVictim, updateVictim } = useFormContext()

  // ─── Tipos de delito desde el backend (con fallback a mock) ────────────────
  const { tipos, loading: loadingTipos, warning: tiposWarning } = useDelitoCategories()

  const [gpsMode, setGpsMode] = useState<'actual' | 'none'>('actual')
  const [isFormalComplaint, setIsFormalComplaint] = useState(false)
  const [selectedTipoValue, setSelectedTipoValue] = useState('')
  const [subtipos, setSubtipos] = useState<{ value: string; label: string }[]>([])
  const [fechaError, setFechaError] = useState(false)
  const [tiempoError,     setTiempoError]     = useState(false)
  const [tiempoErrorMsg,  setTiempoErrorMsg]  = useState('')

  // Actualizar subtipos cuando cambia el tipo de delito
  useEffect(() => {
    const found = tipos.find((t) => t.value === selectedTipoValue)
    setSubtipos(found ? found.subtipos : [])
  }, [selectedTipoValue, tipos])

  const handleTipoDelitoChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value
    setSelectedTipoValue(value)
    const label = tipos.find((t) => t.value === value)?.label ?? ''
    updateFormData({ tipoDelito: label, subTipo: '' })
  }

  const handleSubTipoChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const label = subtipos.find((s) => s.value === e.target.value)?.label ?? ''
    updateFormData({ subTipo: label })
  }

  const validateTiempos = (
      inicio: string,
      fin: string,
      enCurso: boolean
  ) => {
    // Al menos uno debe estar presente
    if (!inicio && !fin && !enCurso) {
      setTiempoError(true)
      setTiempoErrorMsg('Debe ingresar al menos la hora de inicio o de finalización')
      return
    }
    // Si tiene fin, debe tener inicio
    if (fin && !inicio) {
      setTiempoError(true)
      setTiempoErrorMsg('Si tiene hora de finalización debe ingresar la hora de inicio')
      return
    }
    // Fin no puede ser anterior al inicio
    if (inicio && fin && fin < inicio) {
      setTiempoError(true)
      setTiempoErrorMsg('La hora de finalización no puede ser anterior a la de inicio')
      return
    }
    setTiempoError(false)
    setTiempoErrorMsg('')
  }

// BORRAR handleFechaChange y handleHoraChange existentes, poner estos:

  const handleFechaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFormData({ fechaHecho: e.target.value })
    const isFuture = e.target.value > new Date().toISOString().split('T')[0]
    setFechaError(isFuture)
  }

  const handleInicioChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFormData({ horaInicioHecho: e.target.value })
    validateTiempos(e.target.value, formData.horaFinHecho, formData.hechoEnCurso)
  }

  const handleFinChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFormData({ horaFinHecho: e.target.value })
    validateTiempos(formData.horaInicioHecho, e.target.value, formData.hechoEnCurso)
  }

  const handleEnCursoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFormData({ hechoEnCurso: e.target.checked, horaFinHecho: '' })
    validateTiempos(formData.horaInicioHecho, '', e.target.checked)
  }

  // ─── Opciones para los selects ─────────────────────────────────────────────
  const tipoOptions = [
    { value: '', label: loadingTipos ? 'Cargando…' : '— Seleccione —' },
    ...tipos.map((t) => ({ value: t.value, label: t.label })),
  ]

  const subtipoOptions = [
    { value: '', label: selectedTipoValue ? '— Seleccione —' : '— Seleccione tipo primero —' },
    ...subtipos,
  ]

  return (
    <div className="pb-6 space-y-4">
      {/* Aviso de datos locales cuando el backend no está disponible */}
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

        {/* ── Datos de la Víctima ─────────────────────────────────── */}
        <div>
          <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
            Datos de la Víctima
          </div>

          <div className="space-y-4">
            {formData.victims.map((victim, index) => (
              <div
                key={victim.id}
                className="relative p-4 border border-cyan-400/30 rounded bg-[#050F1C]/40"
                style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.12), inset 0 1px 3px rgba(51,153,255,0.05)' }}
              >
                {formData.victims.length > 1 && (
                  <>
                    <button
                      onClick={() => removeVictim(victim.id)}
                      className="absolute top-2 right-2 text-red-400 hover:text-red-300 transition-colors p-1 rounded hover:bg-red-400/10"
                    >
                      <X size={16} />
                    </button>
                    <div className="text-[10px] uppercase tracking-wider text-cyan-500/70 mb-3">
                      Víctima #{index + 1}
                    </div>
                  </>
                )}

                <div className="space-y-3">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <NeonInput
                      label="Nombre y Apellido" required
                      placeholder="Nombre completo de la víctima…"
                      value={victim.nombre}
                      onChange={(e) => updateVictim(victim.id, { nombre: e.target.value })}
                    />
                    <NeonInput
                      label="Identificación" required
                      placeholder="Número de cédula o identificación…"
                      value={victim.identificacion}
                      onChange={(e) => updateVictim(victim.id, { identificacion: e.target.value })}
                    />
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <NeonInput
                      label="Nacionalidad"
                      placeholder="Ej. Venezolana, Colombiana…"
                      value={victim.nacionalidad}
                      onChange={(e) => updateVictim(victim.id, { nacionalidad: e.target.value })}
                    />
                    <NeonInput
                      label="Número de Teléfono"
                      placeholder="Ej. +58 412-1234567…"
                      value={victim.telefono}
                      onChange={(e) => updateVictim(victim.id, { telefono: e.target.value })}
                    />
                  </div>
                  <NeonInput
                    label="Dirección"
                    placeholder="Dirección de residencia…"
                    value={victim.direccion}
                    onChange={(e) => updateVictim(victim.id, { direccion: e.target.value })}
                  />
                  <div className="flex flex-col gap-1.5">
                    <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                      Fotografía de la Víctima
                    </label>

                    <label className="cursor-pointer">
                      <input
                          type="file"
                          accept="image/*"
                          className="sr-only"
                          onChange={(e) => {
                            const file = e.target.files?.[0] ?? null
                            updateVictim(victim.id, { foto: file })
                          }}
                      />

                      {victim.foto ? (
                          /* Preview de la foto */
                          <div className="relative w-24 h-24 border-2 border-cyan-400/60 rounded overflow-hidden group"
                               style={{ boxShadow: '0 0 12px rgba(51,153,255,0.3)' }}>
                            <img
                                src={URL.createObjectURL(victim.foto)}
                                alt="Foto víctima"
                                className="w-full h-full object-cover"
                            />
                            <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100
                        transition-opacity flex items-center justify-center">
                              <span className="text-[9px] uppercase tracking-wider text-cyan-300">Cambiar</span>
                            </div>
                          </div>
                      ) : (
                          /* Placeholder sin foto */
                          <div className="w-24 h-24 border-2 border-dashed border-cyan-400/30 rounded
                      flex flex-col items-center justify-center gap-1
                      hover:border-cyan-400/60 hover:bg-cyan-400/5 transition-all"
                               style={{ boxShadow: 'inset 0 1px 3px rgba(51,153,255,0.05)' }}>
                            <span className="text-cyan-600" style={{ fontSize: 22 }}>＋</span>
                            <span className="text-[9px] uppercase tracking-wider text-cyan-600">Foto</span>
                          </div>
                      )}
                    </label>
                  </div>
                </div>
              </div>
            ))}

            <NeonButton variant="outline" icon={<Plus size={14} />} onClick={addVictim}>
              Agregar Víctima
            </NeonButton>
          </div>
        </div>

        {/* ── Detalles del Delito ─────────────────────────────────── */}
        <div>
          <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
            Detalles del Delito
          </div>
          <div className="space-y-3">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <NeonSelect
                label="Tipo de Delito"
                required
                options={tipoOptions}
                disabled={loadingTipos}
                value={selectedTipoValue}
                onChange={handleTipoDelitoChange}
              />
              <NeonSelect
                label="Subtipo"
                required
                options={subtipoOptions}
                disabled={!selectedTipoValue || loadingTipos}
                onChange={handleSubTipoChange}
              />
            </div>

            <NeonTextarea
              label="Descripción del Hecho"
              required
              placeholder="Ingrese la narrativa detallada de los hechos…"
              rows={6}
              showCount
              maxCount={1000}
            />
          </div>
        </div>

        {/* ── Cronología ─────────────────────────────────────────── */}
        <div>
          <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 font-medium">
            Cronología
          </div>

          {/* Fila 1: Fecha del hecho + Fecha del reporte */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
            <NeonInput
                label="Fecha del Hecho" required type="date"
                value={formData.fechaHecho}
                onChange={handleFechaChange}
                error={fechaError}
            />
            <NeonInput
                label="Fecha del Reporte" required type="date"
                value={formData.fechaReporte}
                onChange={(e) => updateFormData({ fechaReporte: e.target.value })}
            />
          </div>

          {/* Fila 2: Hora inicio + Hora fin + "Aún sucediendo" */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-2">
            <NeonInput
                label="Hora de Inicio del Crimen *" type="time"
                value={formData.horaInicioHecho}
                onChange={handleInicioChange}
                error={tiempoError && !formData.horaInicioHecho}
            />
            <NeonInput
                label="Hora de Finalización" type="time"
                value={formData.horaFinHecho}
                onChange={handleFinChange}
                disabled={formData.hechoEnCurso}         // ← bloqueado si está "en curso"
                error={tiempoError && !formData.horaFinHecho && !formData.hechoEnCurso}
            />
          </div>

          {/* Checkbox "Aún sucediendo" */}
          <div className="mb-2">
            <NeonCheckbox
                label="El hecho aún está sucediendo"
                checked={formData.hechoEnCurso}
                onChange={handleEnCursoChange}
            />
          </div>

          {/* Fila 3: Hora del reporte */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-2">
            <NeonInput
                label="Hora del Reporte" required type="time"
                value={formData.horaReporte}
                onChange={(e) => updateFormData({ horaReporte: e.target.value })}
            />
          </div>

          {/* Errores */}
          {fechaError && (
              <div className="text-[10px] text-red-400 mt-1 px-2 py-1 border border-red-400/40 rounded inline-block"
                   style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}>
                ⚠ La fecha del hecho no puede ser futura
              </div>
          )}
          {tiempoError && (
              <div className="text-[10px] text-red-400 mt-1 px-2 py-1 border border-red-400/40 rounded inline-block"
                   style={{ textShadow: '0 0 5px rgba(255,75,75,0.5)' }}>
                ⚠ {tiempoErrorMsg}
              </div>
          )}
        </div>

        {/* ── Ubicación y Mapa ───────────────────────────────────── */}
        <div>
          <div className="text-[11px] uppercase tracking-[0.12em] text-cyan-400 mb-3 flex items-center gap-2 font-medium">
            <MapPin size={14} /> Ubicación y Mapa
          </div>

          {gpsMode === 'actual' ? (
            <div
              className="p-4 border-2 border-cyan-400/50 rounded bg-cyan-400/5 flex items-center justify-center gap-2"
              style={{ boxShadow: '0 2px 12px rgba(51,153,255,0.25), inset 0 1px 3px rgba(51,153,255,0.1)' }}
            >
              <MapPin size={16} className="text-cyan-400" style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.7))' }} />
              <span className="text-xs text-cyan-300 uppercase tracking-[0.12em]">
                Ubicación obtenida por GPS — Coordenadas automáticas
              </span>
            </div>
          ) : (
            <div className="space-y-3">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <NeonInput label="Municipio"  placeholder="Nombre del municipio…" />
                <NeonInput label="Sector"     placeholder="Calle, avenida…" />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <NeonInput label="Dirección"  placeholder="Ej: Avenida…" />
                <NeonInput label="Referencia" placeholder="Ej: Frente al parque Central" />
              </div>

              {/* Placeholder de mapa */}
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

        {/* ── ¿Denuncia formal? ──────────────────────────────────── */}
        <div className="pt-2">
          <NeonCheckbox
            label="ES UNA DENUNCIA FORMAL"
            checked={isFormalComplaint}
            onChange={(e) => setIsFormalComplaint(e.target.checked)}
          />
        </div>

        {/* ── Datos del Denunciante (condicional) ────────────────── */}
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
                <NeonInput label="Nombre y Apellido" required placeholder="Nombre completo del denunciante…" />
                <NeonInput label="Teléfono"          required placeholder="Teléfono de contacto…" />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <NeonInput label="Identificación" required placeholder="Número de cédula o identificación…" />
                <NeonInput label="Nacionalidad"   required placeholder="Ej. Venezolana, Colombiana…" />
              </div>
              <NeonInput label="Dirección del Denunciante"  required placeholder="Dirección de residencia…" />
              <NeonInput label="Relación con el Crimen"     required placeholder="Ej: Testigo, Familiar, Víctima indirecta…" />
            </div>
          </div>
        )}

        {/* ── Acciones ───────────────────────────────────────────── */}
        <div className="flex flex-col sm:flex-row justify-end gap-3 pt-4">
          <NeonButton variant="outline">Cancelar</NeonButton>
          <NeonButton variant="primary">Guardar</NeonButton>
        </div>
      </NeonPanel>
    </div>
  )
}
