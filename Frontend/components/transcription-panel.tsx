'use client'

import { useState, useRef, useEffect } from 'react'
import { Pause, Play, Square, Mic, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { useToast } from '@/hooks/use-toast'
import { MicrophoneCapture } from '@/lib/microphone'
import { GladiaWebSocket, TranscriptData } from '@/lib/gladia-websocket'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Loader2, CloudUpload, BrainCircuit, CheckCircle2, Sparkles, Copy } from "lucide-react"

interface AiReport {
  content: string;
  sessionId: string;
}

export function TranscriptionPanel() {
  const [mounted, setMounted] = useState(false)
  const [report, setReport] = useState<AiReport | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  const [status, setStatus] = useState<'idle' | 'recording' | 'connecting'>('idle')
  const [errorMsg, setErrorMsg] = useState<string | null>(null)

  const [finalTranscripts, setFinalTranscripts] = useState<string[]>([])
  const [partialTranscript, setPartialTranscript] = useState<string>('')

  const { toast } = useToast()

  const microphoneRef = useRef<MicrophoneCapture | null>(null)
  const gladiaWsRef = useRef<GladiaWebSocket | null>(null)
  const textSnapshotRef = useRef<string>('');

  const mediaRecorderRef = useRef<MediaRecorder | null>(null)
  const audioChunksRef = useRef<Blob[]>([])

  const canvasRef = useRef<HTMLCanvasElement>(null)
  const animationRef = useRef<number | null>(null)
  const scrollRef = useRef<HTMLDivElement>(null)

  // Auto-scroll dynamically as new transcriptions arrive
  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [finalTranscripts, partialTranscript])

  const sendTranscriptionToBackend = async () => {
    if (audioChunksRef.current.length === 0) return

    try {
      setIsSaving(true);
      const audioBlob = new Blob(audioChunksRef.current, { type: audioChunksRef.current[0].type })

      const formData = new FormData()
      
      const transcription = textSnapshotRef.current
      formData.append('audio', audioBlob, `audio_testimonio_${Date.now()}.webm`)
      formData.append('transcription', transcription)

      const backendUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/v1/testimonies`

      const response = await fetch(backendUrl, {
        method: 'POST',
        body: formData, 
      })
      console.log(`URL: ${backendUrl}`)

      if (!response.ok) {
        throw new Error(`Error en el servidor: ${response.status}`)
      }

      const data: AiReport = await response.json()
      setReport(data);
      setIsModalOpen(true)

      toast({
        title: "Testimonio procesado",
        description: "El informe legal ha sido generado por la IA con éxito.",
      });

      setStatus('idle');

    } catch (error: any) {
      console.error("Error al subir el audio:", error)
      toast({
        title: "Error de envio",
        description: "No se pudo guardar el testimonio ni generar el informe.",
        variant: "destructive"
      })
    } finally {
      setIsSaving(false);
    }
  }

  const startRecording = async () => {
    setErrorMsg(null)
    setStatus('connecting')

    audioChunksRef.current = []

    try {
      // 1. Iniciar Micrófono PRIMERO (Obligatorio en móviles iOS/Safari para no perder el contexto de gesto)
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error("Tu navegador no soporta captura de audio, o la conexión no es segura (HTTPS).")
      }
      const mic = new MicrophoneCapture()
      microphoneRef.current = mic
      await mic.start() // Pide permisos y levanta el AudioContext

      if (mic.stream) {
        const mediaRecorder = new MediaRecorder(mic.stream)
        mediaRecorderRef.current = mediaRecorder

        // Cada vez que haya un fragmento de audio disponible, lo guardamos
        mediaRecorder.ondataavailable = (event) => {
          if (event.data && event.data.size > 0) {
            audioChunksRef.current.push(event.data)
          }
        }

        mediaRecorder.onstop = async () => {
          await sendTranscriptionToBackend()
        }

        // Le pedimos al grabador que corte fragmentos cada 1 segundo (1000ms)
        mediaRecorder.start(1000)
          }

      // Iniciar Visualizador para feedback inmediato
      startVisualizer(mic.getAnalyser())

      // 2. Obtener sesión de Gladia
      const res = await fetch('/api/gladia/session', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ language: 'es' })
      })

      if (!res.ok) {
        const errText = await res.text()
        let errMsg = 'Error al obtener sesión de Gladia'
        try {
          const errJson = JSON.parse(errText)
          errMsg = errJson.error || errMsg
        } catch {
          errMsg = `Error del servidor: ${res.status}`
        }
        throw new Error(errMsg)
      }

      const { ws_url } = await res.json()

      // 3. Conectar WebSocket
      const ws = new GladiaWebSocket()
      gladiaWsRef.current = ws

      ws.onTranscript = (data: TranscriptData) => {
        if (data.isFinal) {
          setFinalTranscripts(prev => {
            const updated = [...prev, data.text]
            textSnapshotRef.current = updated.join(' ');
            return updated
          })
          setPartialTranscript('')
        } else {
          setPartialTranscript(data.text)
        }
      }

      ws.onError = (err) => {
        console.error('WS Error:', err)
        setErrorMsg('Error en la conexión con Gladia.')
        stopRecording()
      }

      await ws.connect(ws_url)

      // 4. Enlazar audio hacia Gladia
      mic.onAudioData = (buffer) => {
        ws.sendAudio(buffer)
        const audioBlob = new Blob([buffer], { type: 'audio/wav' })
      audioChunksRef.current.push(audioBlob)
      }

      setStatus('recording')
      const { dismiss } = toast({
        title: "Micrófono activado",
        description: "Comienza a hablar, la transcripción está en vivo.",
        duration: 3000,
      })
      setTimeout(dismiss, 3000)

    } catch (error: any) {
      console.error("Error starting recording:", error)
      setErrorMsg(error.message)

      setStatus('idle')
      cleanup()
    }
  }

  const stopRecording = () => {
    if (gladiaWsRef.current) {
      gladiaWsRef.current.stopRecording()
      setTimeout(() => {
        gladiaWsRef.current?.disconnect()
      }, 1500)
    }

    cleanup()
    setStatus('idle')
    setPartialTranscript('')
  }

  const cleanup = () => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop()
      mediaRecorderRef.current = null
    }

    if (microphoneRef.current) {
      microphoneRef.current.stop()
      microphoneRef.current = null
    }
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current)
      animationRef.current = null

      if (canvasRef.current) {
        const ctx = canvasRef.current.getContext('2d')
        ctx?.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height)
      }
    }
  }

  const startVisualizer = (analyser: AnalyserNode | null) => {
    if (!analyser || !canvasRef.current) return
    const canvas = canvasRef.current
    const canvasCtx = canvas.getContext('2d')
    if (!canvasCtx) return

    const bufferLength = analyser.frequencyBinCount
    const dataArray = new Uint8Array(bufferLength)

    const draw = () => {
      animationRef.current = requestAnimationFrame(draw)
      analyser.getByteTimeDomainData(dataArray)

      canvasCtx.fillStyle = 'rgba(0, 0, 0, 0)' // Transparente
      canvasCtx.clearRect(0, 0, canvas.width, canvas.height)

      canvasCtx.lineWidth = 2
      canvasCtx.strokeStyle = 'rgb(34, 197, 94)' // Verde
      canvasCtx.beginPath()

      const sliceWidth = canvas.width * 1.0 / bufferLength
      let x = 0

      for (let i = 0; i < bufferLength; i++) {
        const v = dataArray[i] / 128.0
        const y = v * (canvas.height / 2)

        if (i === 0) {
          canvasCtx.moveTo(x, y)
        } else {
          canvasCtx.lineTo(x, y)
        }

        x += sliceWidth
      }

      canvasCtx.lineTo(canvas.width, canvas.height / 2)
      canvasCtx.stroke()
    }

    draw()
  }

  // Limpieza al desmontar
  useEffect(() => {
    return () => {
      cleanup()
      if (gladiaWsRef.current) {
        gladiaWsRef.current.disconnect()
      }
    }
  }, [])

  if (!mounted) return <div className="h-full w-full flex items-center justify-center text-muted-foreground animate-pulse">Cargando panel de audio...</div>

  return (
    <div className="flex flex-col h-full space-y-4">
      {/* Área de Transcripción */}
      <Card className="flex-1 min-h-0 bg-secondary/20 border-border p-4 sm:p-6 overflow-y-auto flex flex-col gap-2 select-none">
        {finalTranscripts.length === 0 && !partialTranscript && (
          <div className="flex flex-col items-center justify-center h-full text-muted-foreground">
            <Mic size={48} className="mb-4 opacity-20" />
            <p>La transcripción en vivo aparecerá aquí...</p>
          </div>
        )}

        {finalTranscripts.map((text, i) => (
          <p key={i} className="text-foreground leading-relaxed">{text}</p>
        ))}

        {partialTranscript && (
          <p className="text-muted-foreground italic leading-relaxed animate-pulse">
            {partialTranscript}
          </p>
        )}
        <div ref={scrollRef} />
      </Card>

      {/* Controles y Visualizador */}
      <Card className="bg-secondary/50 border-border p-4 shadow-sm flex flex-col gap-4">
        {errorMsg && (
          <Alert variant="destructive" className="bg-destructive/10 text-destructive border-destructive/20">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{errorMsg}</AlertDescription>
          </Alert>
        )}

        {/* Visualizador de Audio */}
        <div className="w-full h-16 bg-background/50 rounded-md border border-border/50 relative overflow-hidden flex items-center justify-center">
          {status === 'idle' && (
            <span className="text-xs text-muted-foreground absolute">Visualizador de audio inactivo</span>
          )}
          {status === 'connecting' && (
            <span className="text-xs text-muted-foreground absolute animate-pulse">Conectando con Gladia...</span>
          )}
          <canvas
            ref={canvasRef}
            width={800}
            height={64}
            className="w-full h-full absolute top-0 left-0"
          />
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className={`w-3 h-3 rounded-full transition-colors duration-300 ${status === 'recording' ? 'bg-red-500 animate-pulse' :
                status === 'connecting' ? 'bg-yellow-500 animate-pulse' : 'bg-muted-foreground'
              }`} />
            <span className={`text-sm font-medium ${status === 'recording' ? 'text-red-500' :
                status === 'connecting' ? 'text-yellow-600' : 'text-muted-foreground'
              }`}>
              {status === 'recording' ? 'Grabando Audio...' :
                status === 'connecting' ? 'Conectando...' : 'Listo para grabar'}
            </span>
          </div>

          <div className="flex gap-2 items-center">
            {status === 'idle' ? (
              <Button onClick={startRecording} className="bg-primary hover:bg-primary/90 text-primary-foreground shadow-sm gap-2 rounded-full px-6 transition-all duration-300 hover:scale-105 hover:shadow-md hover:shadow-primary/20 active:scale-95">
                <Mic size={18} className="animate-pulse" /> Iniciar grabación
              </Button>
            ) : (
              <div className="flex gap-2 animate-in fade-in slide-in-from-right-4 duration-300">
                <Button onClick={stopRecording} variant="destructive" className="gap-2 rounded-full shadow-sm hover:bg-destructive/90 transition-all duration-300 hover:scale-105 hover:shadow-md hover:shadow-destructive/20 active:scale-95" disabled={status === 'connecting'}>
                  <Square size={16} fill="currentColor" /> Finalizar
                </Button>
              </div>
            )}
          </div>
        </div>
      </Card>

    {/* INDICADOR DE ENVÍO Y ESPERA DE INFORME */}
    {isSaving && (
      <div className="fixed inset-0 bg-background/80 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-in fade-in duration-300">
        <Card className="w-full max-w-md border-primary/20 shadow-2xl bg-card p-6 text-center space-y-6">
          
          {/* Iconografía animada según el paso */}
          <div className="flex justify-center content-center relative">
            <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center text-primary relative">
              {/* Si tenemos el reporte null pero está guardando, está en proceso */}
              {!report ? (
                <BrainCircuit className="h-10 w-10 animate-pulse text-primary" />
              ) : (
                <CloudUpload className="h-10 w-10 animate-bounce" />
              )}
              
              {/* Spinner giratorio externo constante */}
              <div className="absolute inset-0 border-4 border-primary/30 border-t-primary rounded-full animate-spin" />
            </div>
          </div>

          {/* Textos Dinámicos de Estado */}
          <div className="space-y-2">
            <h3 className="text-xl font-bold tracking-tight text-foreground">
              {!report ? "Procesando Información" : "Enviando Evidencia"}
            </h3>
            
            <p className="text-sm text-muted-foreground max-w-xs mx-auto">
              {/* Mensaje de progreso según lo que hace el backend */}
              Audio recibido con éxito. Por favor espere mientras la IA analiza el testimonio y redacta el informe judicial.
            </p>
          </div>

          {/* Línea de tiempo / Pasos visuales */}
          <div className="border-t border-border/60 pt-4 text-left space-y-3">
            <div className="flex items-center gap-3 text-sm">
              <CheckCircle2 className="h-4 w-4 text-green-500 fill-green-500/10" />
              <span className="font-medium text-foreground">Captura de audio finalizada</span>
            </div>
            
            <div className="flex items-center gap-3 text-sm">
              {/* Paso 2: Subida de archivo (Siempre rápido en local) */}
              <CheckCircle2 className="h-4 w-4 text-green-500 fill-green-500/10 animate-in zoom-in" />
              <span className="text-foreground">Evidencia de audio transmitida al servidor</span>
            </div>

            <div className="flex items-center gap-3 text-sm">
              {/* Paso 3: Espera de la IA (Aquí es donde se queda el spinner) */}
              <Loader2 className="h-4 w-4 text-primary animate-spin" />
              <span className="font-medium text-primary animate-pulse">Redactando informe analítico con IA...</span>
            </div>
          </div>

          <p className="text-[11px] text-muted-foreground italic pt-2">
            Este proceso puede tomar unos segundos dependiendo de la duración del testimonio.
          </p>
        </Card>
      </div>
    )}

      {/* Pantalla emergente del informe IA */}
    <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
      <DialogContent className="sm:max-w-[700px] max-h-[85vh] flex flex-col p-0 overflow-hidden border-primary/20 shadow-2xl animate-in zoom-in-95 duration-300">
        
        {/* Encabezado del Modal */}
        <DialogHeader className="p-6 pb-4 border-b border-border/60 bg-gradient-to-r from-primary/5 via-transparent to-transparent">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="p-2 bg-primary/10 rounded-lg text-primary">
                <Sparkles className="h-5 w-5 animate-pulse" />
              </div>
              <div>
                <DialogTitle className="text-xl font-bold text-primary">
                  Informe de Testimonio (IA)
                </DialogTitle>
                <DialogDescription className="text-xs mt-0.5">
                  Sesión Judicial ID: {report?.sessionId}
                </DialogDescription>
              </div>
            </div>

            {/* Botón rápido para copiar */}
            {report && (
              <Button 
                variant="outline" 
                size="sm" 
                className="gap-2 h-8 text-xs font-medium mr-4"
                onClick={() => {
                  navigator.clipboard.writeText(report.content)
                  toast({ description: "Contenido copiado al portapapeles" })
                }}
              >
                <Copy size={13} /> Copiar Informe
              </Button>
            )}
          </div>
        </DialogHeader>

        {/* Cuerpo del Modal con scroll dedicado */}
        <ScrollArea className="flex-1 p-6 overflow-y-auto bg-card">
          {report ? (
            <div className="text-foreground leading-relaxed whitespace-pre-wrap text-sm md:text-base pr-4">
              {report.content}
            </div>
          ) : (
            <p className="text-sm text-muted-foreground italic text-center py-8">
              No hay datos disponibles para este informe.
            </p>
          )}
        </ScrollArea>

        {/* Pie de página del Modal */}
        <div className="p-4 border-t border-border/60 bg-muted/30 flex justify-end gap-2">
          <Button 
            variant="default" 
            onClick={() => setIsModalOpen(false)}
            className="rounded-lg px-5 text-sm"
          >
            Entendido, Cerrar
          </Button>
        </div>

      </DialogContent>
    </Dialog>

    </div>
  )
}