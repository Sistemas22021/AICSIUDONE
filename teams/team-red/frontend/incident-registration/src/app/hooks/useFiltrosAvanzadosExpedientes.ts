import { useState, useEffect, useMemo, useCallback } from 'react'
import { buscarExpedientesConFiltros } from '../services/expedientesService'
import type { ExpedienteActivo, FiltrosBusquedaExpediente } from '../types/api.types'

const FILTROS_VACIOS: FiltrosBusquedaExpediente = {
    tiposDelito: [], municipio: '', colonia: '',
    latitud: null, longitud: null, radioKm: null,
    fechaDesde: null, fechaHasta: null,
}

/** Espera antes de consultar al backend tras el último cambio de filtro. */
const DEBOUNCE_MS = 400

export interface UseFiltrosAvanzadosExpedientesResult {
    filtros:            FiltrosBusquedaExpediente
    setTiposDelito:     (tipos: string[]) => void
    setMunicipio:       (v: string) => void
    setColonia:         (v: string) => void
    setPuntoRadio:      (lat: number, lng: number) => void
    setRadioKm:         (km: number | null) => void
    limpiarRadio:       () => void
    setFechaDesde:      (v: string | null) => void
    setFechaHasta:      (v: string | null) => void
    limpiarFiltros:     () => void
    hayFiltrosActivos:  boolean
    resultados:         ExpedienteActivo[]
    buscando:           boolean
    error:              string | null
}

export function useFiltrosAvanzadosExpedientes(): UseFiltrosAvanzadosExpedientesResult {
    const [filtros, setFiltros]       = useState<FiltrosBusquedaExpediente>(FILTROS_VACIOS)
    const [resultados, setResultados] = useState<ExpedienteActivo[]>([])
    const [buscando, setBuscando]     = useState(false)
    const [error, setError]           = useState<string | null>(null)

    const hayFiltrosActivos = useMemo(() => (
        filtros.tiposDelito.length > 0 ||
        filtros.municipio.trim() !== '' ||
        filtros.colonia.trim() !== '' ||
        (filtros.latitud != null && filtros.longitud != null && filtros.radioKm != null) ||
        filtros.fechaDesde !== null ||
        filtros.fechaHasta !== null
    ), [filtros])

    useEffect(() => {
        if (!hayFiltrosActivos) {
            setResultados([])
            setError(null)
            return
        }

        let cancelado = false
        setBuscando(true)

        const timeoutId = setTimeout(async () => {
            try {
                const data = await buscarExpedientesConFiltros(filtros)
                if (!cancelado) { setResultados(data); setError(null) }
            } catch (err) {
                if (!cancelado) {
                    setError(err instanceof Error ? err.message : 'No se pudo completar la búsqueda.')
                    setResultados([])
                }
            } finally {
                if (!cancelado) setBuscando(false)
            }
        }, DEBOUNCE_MS)

        return () => { cancelado = true; clearTimeout(timeoutId) }
    }, [filtros, hayFiltrosActivos])

    const setTiposDelito = useCallback((tipos: string[]) => setFiltros(f => ({ ...f, tiposDelito: tipos })), [])
    const setMunicipio   = useCallback((v: string) => setFiltros(f => ({ ...f, municipio: v })), [])
    const setColonia     = useCallback((v: string) => setFiltros(f => ({ ...f, colonia: v })), [])
    const setPuntoRadio  = useCallback((lat: number, lng: number) =>
        setFiltros(f => ({ ...f, latitud: lat, longitud: lng })), [])
    const setRadioKm     = useCallback((km: number | null) => setFiltros(f => ({ ...f, radioKm: km })), [])
    const limpiarRadio   = useCallback(() =>
        setFiltros(f => ({ ...f, latitud: null, longitud: null, radioKm: null })), [])
    const setFechaDesde  = useCallback((v: string | null) => setFiltros(f => ({ ...f, fechaDesde: v })), [])
    const setFechaHasta  = useCallback((v: string | null) => setFiltros(f => ({ ...f, fechaHasta: v })), [])
    const limpiarFiltros = useCallback(() => setFiltros(FILTROS_VACIOS), [])

    return {
        filtros, setTiposDelito, setMunicipio, setColonia,
        setPuntoRadio, setRadioKm, limpiarRadio, setFechaDesde, setFechaHasta,
        limpiarFiltros, hayFiltrosActivos, resultados, buscando, error,
    }
}