// Barra superior con el logo ZAC, datos del expediente activo y menú de opciones
import { useState, useEffect, useRef } from 'react'
import { ChevronDown } from 'lucide-react'
import { NeonPanel } from './ui/NeonPanel'
import { useFormContext } from '../context/FormContext'

interface HeaderProps {
  onSearchClick: () => void
}

export const Header = ({ onSearchClick }: HeaderProps) => {
  const { formData, resetForm } = useFormContext()
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!isDropdownOpen) return
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setIsDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [isDropdownOpen])

  return (
    <div className="p-4 sm:p-6 flex flex-col lg:flex-row gap-4 items-stretch w-[95%] max-w-[1600px] mx-auto">
      {/* Panel Izquierdo - Logo y Opciones */}
      <NeonPanel className="flex-shrink-0 w-full lg:w-[280px] flex flex-col justify-between h-full">
        <div>
          <div className="flex items-center gap-2 mb-1.5">
            <span
              className="text-2xl tracking-[0.2em] text-cyan-300"
              style={{
                fontFamily: 'Orbitron, monospace',
                textShadow:
                  '0 0 15px rgba(51,153,255,0.9), 0 0 30px rgba(51,153,255,0.5), 0 0 45px rgba(51,153,255,0.3)',
                fontWeight: 700,
              }}
            >
              [ ZAC ]
            </span>
          </div>
          <div className="text-[9px] text-cyan-400/80 uppercase tracking-[0.15em] mb-4">
            Sistema de Análisis Criminal
          </div>
        </div>

        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setIsDropdownOpen((v) => !v)}
            className="w-full px-3 py-2 border-2 border-cyan-400/50 rounded text-[10px] uppercase tracking-[0.12em] text-cyan-300 hover:bg-cyan-400/10 hover:border-cyan-400 transition-all flex items-center justify-between holo-panel"
          >
            Opciones
            <ChevronDown
              size={14}
              className={`transition-transform duration-300 ${isDropdownOpen ? 'rotate-180' : ''}`}
              style={{ filter: 'drop-shadow(0 0 3px rgba(51,153,255,0.6))' }}
            />
          </button>

          {isDropdownOpen && (
            <div
              className="absolute top-full left-0 right-0 mt-2 border-2 border-cyan-400/60 bg-[#070C12]/98 backdrop-blur-sm rounded overflow-hidden z-50"
              style={{
                boxShadow:
                  '0 4px 20px rgba(51,153,255,0.4), 0 8px 40px rgba(51,153,255,0.2), inset 0 1px 3px rgba(51,153,255,0.1)',
              }}
            >
              <button
                onClick={() => { resetForm(); setIsDropdownOpen(false) }}
                className="w-full px-3 py-2 text-[10px] uppercase tracking-[0.12em] text-cyan-300 hover:bg-cyan-400/15 transition-all text-left border-b border-cyan-400/30"
              >
                Nuevo Expediente
              </button>
              <button
                onClick={() => { onSearchClick(); setIsDropdownOpen(false) }}
                className="w-full px-3 py-2 text-[10px] uppercase tracking-[0.12em] text-cyan-300 hover:bg-cyan-400/15 transition-all text-left"
              >
                Búsqueda y Casos
              </button>
            </div>
          )}
        </div>
      </NeonPanel>

      {/* Panel Derecho - Información del Expediente */}
      <NeonPanel className="flex-1 flex flex-col justify-between h-full">
        <div
          className="text-xs uppercase tracking-[0.15em] text-cyan-300 mb-3"
          style={{ textShadow: '0 0 12px rgba(51,153,255,0.7), 0 0 25px rgba(51,153,255,0.4)' }}
        >
          EXP — {new Date().getFullYear()} — EN PROGRESO
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {[
            { label: 'Agente Registrador', value: formData.agenteRegistrador },
            { label: 'Investigador',        value: formData.investigador },
            { label: 'Tipo de Delito',      value: formData.tipoDelito || '—' },
            { label: 'Sub Tipo',            value: formData.subTipo     || '—' },
          ].map(({ label, value }) => (
            <div key={label}>
              <div className="text-[9px] uppercase tracking-[0.12em] text-cyan-400/70 mb-1.5">{label}:</div>
              <div
                className="text-xs text-cyan-300 px-3 py-2 bg-[#080D13]/80 border-2 border-cyan-400/30 rounded font-medium min-h-[36px] flex items-center"
                style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.12), inset 0 1px 3px rgba(51,153,255,0.08)' }}
              >
                {value}
              </div>
            </div>
          ))}
        </div>
      </NeonPanel>
    </div>
  )
}
