// src/app/components/ui/MapaPicker.tsx
import { useEffect, useRef, useState } from 'react'
import L from 'leaflet'
import { Loader2, Navigation } from 'lucide-react'

// ── Fix del ícono roto de Leaflet en Vite ─────────────────────────────────────
// Leaflet busca sus imágenes por una ruta que Vite rompe al compilar.
// Esta corrección lo resuelve de una vez.
import iconUrl       from 'leaflet/dist/images/marker-icon.png'
import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png'
import shadowUrl     from 'leaflet/dist/images/marker-shadow.png'

delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({ iconUrl, iconRetinaUrl, shadowUrl })

// ─────────────────────────────────────────────────────────────────────────────

export interface MapaCoordenadas {
    lat:       number
    lng:       number
    precision?: number
}

interface MapaPickerProps {
    onChange:      (coords: MapaCoordenadas) => void
    initialCoords?: MapaCoordenadas | null
}

// Centro por defecto: UDONE
const DEFAULT_CENTER: [number, number] = [10.995508626789533, -63.86910977871679]
const DEFAULT_ZOOM = 14

export function MapaPicker({ onChange, initialCoords }: MapaPickerProps) {
    const mapDivRef = useRef<HTMLDivElement>(null)
    const mapRef    = useRef<L.Map | null>(null)
    const markerRef = useRef<L.Marker | null>(null)

    const [loadingGps,  setLoadingGps]  = useState(false)
    const [coordenadas, setCoordenadas] = useState<MapaCoordenadas | null>(
        initialCoords ?? null
    )

    // ── Inicializar el mapa (solo una vez al montar) ───────────────────────────
    useEffect(() => {
        if (!mapDivRef.current || mapRef.current) return

        const center: [number, number] = initialCoords
            ? [initialCoords.lat, initialCoords.lng]
            : DEFAULT_CENTER

        // Crear el mapa
        const map = L.map(mapDivRef.current, {
            center,
            zoom:              DEFAULT_ZOOM,
            zoomControl:       true,
            attributionControl: true,
        })

        // Capa de tiles de OpenStreetMap — completamente gratuita
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
            maxZoom: 19,
        }).addTo(map)

        // Marcador arrastrable
        const marker = L.marker(center, { draggable: true }).addTo(map)

        // Actualizar al arrastrar
        marker.on('dragend', () => {
            const pos     = marker.getLatLng()
            const nuevas  = { lat: pos.lat, lng: pos.lng }
            setCoordenadas(nuevas)
            onChange(nuevas)
        })

        // Mover el marcador al hacer clic en el mapa
        map.on('click', (e: L.LeafletMouseEvent) => {
            marker.setLatLng(e.latlng)
            const nuevas = { lat: e.latlng.lat, lng: e.latlng.lng }
            setCoordenadas(nuevas)
            onChange(nuevas)
        })

        mapRef.current    = map
        markerRef.current = marker

        if (initialCoords) onChange(initialCoords)

        // Limpieza al desmontar el componente
        return () => {
            map.remove()
            mapRef.current    = null
            markerRef.current = null
        }
    }, [])

    // ── Botón "Mi ubicación" ───────────────────────────────────────────────────
    const usarMiUbicacion = () => {
        if (!navigator.geolocation) return
        setLoadingGps(true)

        navigator.geolocation.getCurrentPosition(
            (pos) => {
                const nuevas: MapaCoordenadas = {
                    lat:       pos.coords.latitude,
                    lng:       pos.coords.longitude,
                    precision: Math.round(pos.coords.accuracy),
                }
                const latlng: [number, number] = [nuevas.lat, nuevas.lng]
                mapRef.current?.setView(latlng, 17)
                markerRef.current?.setLatLng(latlng)
                setCoordenadas(nuevas)
                onChange(nuevas)
                setLoadingGps(false)
            },
            () => {
                // GPS no disponible en este entorno — el usuario usa el clic en el mapa
                setLoadingGps(false)
            },
            { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
        )
    }

    // ── Render ─────────────────────────────────────────────────────────────────
    return (
        <div className="space-y-2">

            {/* Barra superior del mapa */}
            <div className="flex items-center justify-between">
        <span className="text-[10px] uppercase tracking-wider text-cyan-500/70">
          {coordenadas
              ? 'Arrastra el pin o haz clic para ajustar la posición'
              : 'Haz clic en el mapa para marcar la ubicación'}
        </span>
                <button
                    type="button"
                    onClick={usarMiUbicacion}
                    disabled={loadingGps}
                    className="flex items-center gap-1.5 text-[10px] uppercase tracking-wider
                     text-cyan-400 hover:text-cyan-300 border border-cyan-400/30
                     hover:border-cyan-400/60 rounded px-2.5 py-1 transition-all
                     disabled:opacity-40 disabled:cursor-not-allowed"
                >
                    {loadingGps
                        ? <Loader2 size={11} className="animate-spin" />
                        : <Navigation size={11} />
                    }
                    {loadingGps ? 'Localizando…' : 'Mi ubicación'}
                </button>
            </div>

            {/* El mapa */}
            <div
                className="relative border-2 border-cyan-400/50 rounded overflow-hidden"
                style={{
                    height:    '280px',
                    boxShadow: '0 2px 12px rgba(51,153,255,0.25)',
                }}
            >
                <div ref={mapDivRef} style={{ height: '100%', width: '100%' }} />
            </div>

            {/* Coordenadas capturadas */}
            {coordenadas && (
                <div className="space-y-1.5">
                    <div className="grid grid-cols-2 gap-2">
                        <div className="px-3 py-2 bg-[#020810]/60 border border-cyan-400/20 rounded">
                            <div className="text-[9px] uppercase tracking-wider text-cyan-600 mb-0.5">
                                Latitud
                            </div>
                            <div className="text-[12px] font-mono text-cyan-200">
                                {coordenadas.lat.toFixed(6)}
                            </div>
                        </div>
                        <div className="px-3 py-2 bg-[#020810]/60 border border-cyan-400/20 rounded">
                            <div className="text-[9px] uppercase tracking-wider text-cyan-600 mb-0.5">
                                Longitud
                            </div>
                            <div className="text-[12px] font-mono text-cyan-200">
                                {coordenadas.lng.toFixed(6)}
                            </div>
                        </div>
                    </div>
                    {coordenadas.precision && (
                        <p className="text-[10px] text-cyan-600">
                            Precisión GPS: ±{coordenadas.precision} metros
                        </p>
                    )}
                </div>
            )}

        </div>
    )
}