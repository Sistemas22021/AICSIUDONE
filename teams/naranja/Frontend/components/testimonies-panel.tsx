'use client'

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Sparkles, FileText } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Eye, Edit3, Volume2, Check, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface Testimony {
  sessionId: string;
  cedula: string;
  caseNumber: string;
  hasAudio: boolean;
  hasOriginalText: boolean; // Mapea a Transcripción
  hasModifiedText: boolean; // Mapea a Resumen
}

interface TestimonyDetails {
  sessionId: string;
  originalText: string;
  modifiedText: string;
  audioUrl: string;
}

export function TestimoniesPanel() {
  const [testimonies, setTestimonies] = useState<Testimony[]>([])
  const [isLoading, setIsLoading] = useState<boolean>(true)

  const [selectedSessionId, setSelectedSessionId] = useState<string | null>(null)
  const [detailData, setDetailData] = useState<TestimonyDetails | null>(null)
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false)
  const [isLoadingDetail, setIsLoadingDetail] = useState<boolean>(false)

  const handleVerDetalles = async (sessionId: string) => {
    setSelectedSessionId(sessionId)
    setIsModalOpen(true)
    setIsLoadingDetail(true)
    setDetailData(null)

    try {
      const baseUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080'
      
      // 1. Un solo viaje para traer texto y metadatos del audio
      const response = await fetch(`${baseUrl}/api/v1/testimonies/${sessionId}/details`)
      
      if (response.ok) {
        const data: TestimonyDetails = await response.json()
        setDetailData(data)
      } else {
        console.error('Error al recuperar los detalles:', response.status)
      }
    } catch (error) {
      console.error('Error fetching testimony details:', error)
    } finally {
      setIsLoadingDetail(false)
    }
  }

  useEffect(() => {
    const fetchTestimonies = async () => {
      try {
        const baseUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080'
        if (!baseUrl) {
          console.error('Error: NEXT_PUBLIC_BACKEND_URL no está definida.')
          setIsLoading(false)
          return
        }

        const response = await fetch(`${baseUrl}/api/v1/testimonies/history`)
        
        if (response.ok) {
          const data: Testimony[] = await response.json()
          setTestimonies(data)
        } else {
          console.error('Error en la respuesta del servidor:', response.status)
        }
      } catch (error) {
        console.error('Error fetching testimonies:', error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchTestimonies()
  }, [])

  if (isLoading) {
    return (
      <div className="flex items-center gap-2 text-muted-foreground font-medium py-4">
        <Loader2 className="h-4 w-4 animate-spin text-primary" />
        <span>Cargando historial de registros...</span>
      </div>
    )
  }

  return (
    // Componente Full-width sin márgenes ni paddings propios para respetar el layout padre
    <div className="w-full">
      
      {/* Encabezado */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Historial de Testimonios</h1>
        <p className="text-sm text-muted-foreground mt-1">Listado de testimonios y sus estados de procesamiento.</p>
      </div>

      {/* Contenedor de la Tabla con Scroll Dedicado */}
      <div className="w-full bg-card rounded-xl shadow-sm border border-border overflow-hidden">
        <div className="w-full overflow-x-auto max-h-[65vh] overflow-y-auto"> 
          <table className="w-full border-collapse text-left text-sm table-auto">
            {/* Cabecera adaptada con los estilos de grises de tu app */}
            <thead className="bg-slate-900 text-white font-semibold uppercase text-xs tracking-wider">
              <tr>
                <th className="px-6 py-4 text-center">Caso</th>
                <th className="px-6 py-4 text-center">Cédula</th>
                <th className="px-6 py-4 text-center">Audio</th>
                <th className="px-6 py-4 text-center">Transcripción</th>
                <th className="px-6 py-4 text-center">Resumen</th>
                <th className="px-6 py-4 text-center">Acciones</th>
              </tr>
            </thead>
            
            <tbody className="divide-y divide-border text-foreground">
              {testimonies.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-muted-foreground font-medium">
                    No hay testimonios grabados aún.
                  </td>
                </tr>
              ) : (
                testimonies.map((testimony, index) => (
                  <tr 
                    key={testimony.sessionId} 
                    className={`${index % 2 === 0 ? 'bg-card' : 'bg-muted/30'} hover:bg-muted/60 transition-colors`}
                  >
                    {/* Caso */}
                    <td className="px-6 py-4 font-semibold text-foreground text-center">
                      {testimony.caseNumber}
                    </td>
                    
                    {/* Cédula */}
                    <td className="px-6 py-4 text-muted-foreground font-medium text-center">
                      {testimony.cedula}
                    </td>
                    
                    {/* Audio */}
                    <td className="px-6 py-4 text-center">
                      {testimony.hasAudio ? (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 border border-emerald-500/20">
                          <Check className="w-3.5 h-3.5" /> Sí
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-sky-500/10 text-sky-600 border border-sky-500/20">
                          <Volume2 className="w-3.5 h-3.5" /> Sin Audio
                        </span>
                      )}
                    </td>
                    
                    {/* Transcripción */}
                    <td className="px-6 py-4 text-center">
                      {testimony.hasOriginalText ? (
                        <span className=" inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 border border-emerald-500/20">
                          <Check className="w-3.5 h-3.5" /> Sí
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-muted text-muted-foreground border border-border">
                          Sin transcripción
                        </span>
                      )}
                    </td>
                    
                    {/* Resumen */}
                    <td className="px-6 py-4 text-center">
                      {testimony.hasModifiedText ? (
                        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 border border-emerald-500/20">
                          Generado
                        </span>
                      ) : (
                        <span className="text-xs text-muted-foreground italic">No solicitado</span>
                      )}
                    </td>
                    
                    {/* Acciones */}
                    <td className="px-6 py-4 text-right whitespace-nowrap space-x-2">
                      <Button 
                        size="sm"
                        onClick={() => handleVerDetalles(testimony.sessionId)}
                        className="bg-primary hover:bg-primary/90 text-primary-foreground shadow-sm gap-2 rounded-full px-6 transition-all duration-300 hover:scale-105 hover:shadow-md hover:shadow-primary/20 active:scale-95"
                      >
                        <Eye className="w-3.5 h-3.5" /> Ver Detalles
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    
      {/* Modal Emergente de Detalles */}
      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent className="sm:max-w-[850px] max-h-[90vh] flex flex-col p-0 overflow-hidden border-border shadow-2xl animate-in zoom-in-95 duration-300">
          
          {/* Encabezado */}
          <DialogHeader className="p-6 pb-4 border-b border-border/60 bg-gradient-to-r from-slate-900/5 via-transparent to-transparent">
            <DialogTitle className="text-xl font-bold text-white">
              Detalle del Testimonio Custodiado
            </DialogTitle>
            <DialogDescription className="text-xs mt-0.5 font-mono">
              Sesión ID: {selectedSessionId}
            </DialogDescription>
          </DialogHeader>

          {/* Cuerpo Principal */}
          <ScrollArea className="flex-1 p-6 overflow-y-auto bg-card">
            {isLoadingDetail ? (
              <div className="flex flex-col items-center justify-center py-16 gap-2 text-muted-foreground">
                <Loader2 className="h-6 w-6 animate-spin text-primary" />
                <span className="text-sm font-medium">Recuperando expediente digital...</span>
              </div>
            ) : detailData ? (
              <div className="space-y-6 pr-2">
                
                {/* SECCIÓN DE AUDIO: Reproductor Nativo inyectado */}
                <div className="p-4 bg-muted/40 border border-border rounded-xl flex flex-col gap-2">
                  <span className="text-xs font-bold text-slate-500 uppercase tracking-wide">
                    Reproductor de Audio Evidencial
                  </span>
                  {/* Inyección directa del src anteponiendo la URL base */}
                  <audio 
                    src={`${process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080'}${detailData.audioUrl}`} 
                    preload="metadata"
                    controls 
                    className="w-full mt-1"
                  />
                </div>

                {/* CUERPO BINARIO: Transcripción y Resumen */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  
                  {/* Columna Izquierda: Transcripción Original */}
                  <div className="space-y-2">
                    <h4 className="font-bold text-sm text-slate-700 flex items-center gap-1.5 uppercase tracking-wide">
                      Transcripción
                    </h4>
                    <div className="p-4 bg-amber-500/5 border border-border rounded-lg text-sm leading-relaxed text-foreground whitespace-pre-wrap h-[40vh] overflow-y-auto shadow-inner">
                      {detailData.originalText || 'No hay transcripción disponible.'}
                    </div>
                  </div>

                  {/* Columna Derecha: Resumen de la IA */}
                  <div className="space-y-2">
                    <h4 className="font-bold text-sm text-slate-700 flex items-center gap-1.5 uppercase tracking-wide">
                      Resumen
                    </h4>
                    <div className="p-4 bg-amber-500/5 border border-amber-500/10 rounded-lg text-sm leading-relaxed text-foreground whitespace-pre-wrap h-[40vh] overflow-y-auto shadow-inner">
                      {detailData.modifiedText || 'El resumen no ha sido generado para este caso.'}
                    </div>
                  </div>

                </div>
              </div>
            ) : (
              <p className="text-sm text-muted-foreground italic text-center py-8">
                No se pudieron mapear los detalles del testimonio.
              </p>
            )}
          </ScrollArea>

          {/* Pie de página */}
          <div className="p-4 border-t border-border/60 bg-muted/30 flex justify-end">
            <Button 
              variant="default" 
              onClick={() => setIsModalOpen(false)}
              className="bg-slate-300 hover:bg-slate-800 rounded-lg px-5 text-sm"
            >
              Cerrar
            </Button>
          </div>

        </DialogContent>
      </Dialog>
  </div>
  )
}