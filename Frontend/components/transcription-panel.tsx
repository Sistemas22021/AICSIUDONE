'use client'

import { useState, useRef } from 'react'
import { Pause, Play, Square, Mic, Lock } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

export function TranscriptionPanel() {
  const [status, setStatus] = useState<'idle' | 'recording' | 'paused'>('idle')
  const mediaRecorderRef = useRef<MediaRecorder | null>(null)
  const audioChunksRef = useRef<Blob[]>([])

  const startRecording = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    const mediaRecorder = new MediaRecorder(stream)
    mediaRecorderRef.current = mediaRecorder
    audioChunksRef.current = []

    mediaRecorder.ondataavailable = (e) => {
      if (e.data.size > 0) audioChunksRef.current.push(e.data)
    }

    mediaRecorder.onstop = handleUpload
    mediaRecorder.start()
    setStatus('recording')
  }

  const pauseRecording = () => {
    mediaRecorderRef.current?.pause()
    setStatus('paused')
  }

  const resumeRecording = () => {
    mediaRecorderRef.current?.resume()
    setStatus('recording')
  }

  const stopRecording = () => {
    mediaRecorderRef.current?.stop()
    setStatus('idle')
  }

  const handleUpload = async () => {
    const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/wav' })
    const formData = new FormData()
    formData.append('audio', audioBlob, `declaracion_${Date.now()}.wav`)

    try {
      await fetch('http://localhost:3001/upload', { method: 'POST', body: formData })
      console.log("Grabación enviada exitosamente al servidor.")
    } catch (error) {
      console.error("Error al guardar el audio:", error)
    }
  }

  return (
      <div className="flex flex-col h-full space-y-4">
        {/* ... (Contenido de transcripción igual) ... */}

        <Card className="bg-secondary border-border p-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {status !== 'idle' && (
                  <>
                    <div className={`w-3 h-3 rounded-full bg-destructive ${status === 'recording' ? 'animate-pulse' : ''}`} />
                    <span className="text-sm font-mono text-destructive">
                  {status === 'recording' ? 'Grabando...' : 'Pausado'}
                </span>
                  </>
              )}
            </div>

            <div className="flex gap-2">
              {status === 'idle' ? (
                  <Button onClick={startRecording} className="bg-primary gap-2">
                    <Mic size={16} /> Iniciar
                  </Button>
              ) : (
                  <>
                    {status === 'recording' ? (
                        <Button onClick={pauseRecording} variant="outline" className="gap-2">
                          <Pause size={16} /> Pausar
                        </Button>
                    ) : (
                        <Button onClick={resumeRecording} variant="outline" className="gap-2 border-primary text-primary">
                          <Play size={16} /> Reanudar
                        </Button>
                    )}
                    <Button onClick={stopRecording} variant="destructive" className="gap-2">
                      <Square size={16} /> Finalizar y Guardar
                    </Button>
                  </>
              )}
            </div>
          </div>
        </Card>
      </div>
  )
}