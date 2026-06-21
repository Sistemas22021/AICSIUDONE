import { useState, useCallback } from 'react'

export interface Coordenadas {
    lat:      number
    lng:      number
    precision: number
}

interface UseGeolocalizacionResult {
    coordenadas:     Coordenadas | null
    loading:         boolean
    error:           string | null
    obtenerUbicacion: () => void
    limpiar:         () => void
}

export function useGeolocalizacion(): UseGeolocalizacionResult {
    const [coordenadas, setCoordenadas] = useState<Coordenadas | null>(null)
    const [loading, setLoading]         = useState(false)
    const [error, setError]             = useState<string | null>(null)

    const obtenerUbicacion = useCallback(() => {
        if (!navigator.geolocation) {
            setError('Este dispositivo no soporta geolocalización.')
            return
        }

        setLoading(true)
        setError(null)
        setCoordenadas(null)

        navigator.geolocation.getCurrentPosition(

            (position) => {
                setCoordenadas({
                    lat:       position.coords.latitude,
                    lng:       position.coords.longitude,
                    precision: Math.round(position.coords.accuracy),
                })
                setLoading(false)
            },

            (err) => {
                // El navegador devuelve códigos numéricos 1, 2 o 3
                const MENSAJES: Record<number, string> = {
                    1: 'Permiso de ubicación denegado. Actívalo en la configuración del navegador.',
                    2: 'No se pudo determinar la ubicación. Intente de nuevo.',
                    3: 'Tiempo de espera agotado al obtener la ubicación.',
                }
                setError(MENSAJES[err.code] ?? 'Error desconocido al obtener la ubicación.')
                setLoading(false)
            },

            {
                enableHighAccuracy: true,   // usa GPS del dispositivo si está disponible
                timeout:            10000,  // espera máximo 10 segundos
                maximumAge:         0,      // nunca usar una posición cacheada
            }
        )
    }, [])

    const limpiar = useCallback(() => {
        setCoordenadas(null)
        setError(null)
    }, [])

    return { coordenadas, loading, error, obtenerUbicacion, limpiar }
}