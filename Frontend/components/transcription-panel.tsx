'use client'

import { useState, useRef, useEffect } from 'react'
import { Pause, Play, Square, Mic, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { useToast } from '@/hooks/use-toast'
import { MicrophoneCapture } from '@/lib/microphone'
import { GladiaWebSocket, TranscriptData } from '@/lib/gladia-websocket'

export function TranscriptionPanel() {
  const [mounted, setMounted] = useState(false)

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

  const canvasRef = useRef<HTMLCanvasElement>(null)
  const animationRef = useRef<number | null>(null)
  const scrollRef = useRef<HTMLDivElement>(null)

  // Auto-scroll dynamically as new transcriptions arrive
  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [finalTranscripts, partialTranscript])

  const startRecording = async () => {
    setErrorMsg(null)
    setStatus('connecting')

    try {
      // 1. Iniciar Micrófono PRIMERO (Obligatorio en móviles iOS/Safari para no perder el contexto de gesto)
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error("Tu navegador no soporta captura de audio, o la conexión no es segura (HTTPS).")
      }
      const mic = new MicrophoneCapture()
      microphoneRef.current = mic
      await mic.start() // Pide permisos y levanta el AudioContext

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
          setFinalTranscripts(prev => [...prev, data.text])
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
    </div>
  )
}