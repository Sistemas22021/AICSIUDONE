import { useState, useMemo, useRef, useEffect } from 'react'
import { useUsuarios } from '../../hooks/useUsuarios'
import type { Usuario } from '../../types/api.types'

interface BuscadorUsuarioProps {
    label?: string
    value: string
    onSeleccionar: (usuario: Usuario) => void
    disabled?: boolean
    placeholder?: string
}

/**
 * Campo de búsqueda de usuario por texto (nombre) o código (username).
 * Reutilizable en cualquier lugar del sistema que hoy captura un "responsable"
 * como texto libre (Evidencia, cadena de custodia, etc.).
 */
export const BuscadorUsuario = ({
                                    label = 'Responsable',
                                    value,
                                    onSeleccionar,
                                    disabled,
                                    placeholder = 'Buscar por nombre o código de usuario…',
                                }: BuscadorUsuarioProps) => {
    const { usuarios, loading } = useUsuarios()
    const [query, setQuery] = useState('')
    const [abierto, setAbierto] = useState(false)
    const contenedorRef = useRef<HTMLDivElement>(null)

    const resultados = useMemo(() => {
        const q = query.trim().toLowerCase()
        if (!q) return []
        return usuarios
            .filter(u => u.fullName.toLowerCase().includes(q) || u.username.toLowerCase().includes(q))
            .slice(0, 8)
    }, [usuarios, query])

    // Cierra el desplegable al hacer click fuera del campo
    useEffect(() => {
        const handleClickFuera = (e: MouseEvent) => {
            if (contenedorRef.current && !contenedorRef.current.contains(e.target as Node)) {
                setAbierto(false)
            }
        }
        document.addEventListener('mousedown', handleClickFuera)
        return () => document.removeEventListener('mousedown', handleClickFuera)
    }, [])

    const seleccionar = (usuario: Usuario) => {
        onSeleccionar(usuario)
        setQuery('')
        setAbierto(false)
    }

    return (
        <div className="flex flex-col gap-1.5 w-full relative" ref={contenedorRef}>
            {label && (
                <label className="text-[11px] uppercase tracking-[0.1em] text-cyan-400 font-medium">
                    {label}
                </label>
            )}
            <input
                value={abierto ? query : value}
                onChange={(e) => { setQuery(e.target.value); setAbierto(true) }}
                onFocus={() => { setQuery(''); setAbierto(true) }}
                disabled={disabled}
                placeholder={value || placeholder}
                className="
                    w-full px-3 py-2.5 bg-transparent text-cyan-300
                    border border-cyan-400/40
                    rounded focus:border-cyan-400 focus:outline-none
                    placeholder:text-cyan-900/40 placeholder:text-sm
                    transition-all duration-300
                    hover:border-cyan-400/60
                    input-glow
                    [color-scheme:dark]
                "
                style={{ boxShadow: '0 2px 8px rgba(51,153,255,0.15), inset 0 1px 3px rgba(51,153,255,0.08)' }}
            />

            {abierto && query.trim() && (
                <ul className="absolute top-full left-0 right-0 mt-1 z-20 bg-[#04101E] border border-cyan-400/40 rounded max-h-48 overflow-y-auto shadow-lg">
                    {loading && (
                        <li className="px-3 py-2 text-xs text-cyan-600">Cargando usuarios…</li>
                    )}
                    {!loading && resultados.length === 0 && (
                        <li className="px-3 py-2 text-xs text-cyan-600">Sin coincidencias</li>
                    )}
                    {!loading && resultados.map(u => (
                        <li
                            key={u.id}
                            onClick={() => seleccionar(u)}
                            className="px-3 py-2 text-sm text-cyan-300 hover:bg-cyan-400/15 cursor-pointer transition-colors"
                        >
                            {u.fullName} <span className="text-cyan-600 text-xs">@{u.username}</span>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    )
}