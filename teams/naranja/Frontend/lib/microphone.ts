export class MicrophoneCapture {
  targetSampleRate: number;
  bufferSize: number;
  audioContext: AudioContext | null = null;
  stream: MediaStream | null = null;
  sourceNode: MediaStreamAudioSourceNode | null = null;
  processorNode: ScriptProcessorNode | null = null;
  analyserNode: AnalyserNode | null = null;
  isCapturing = false;
  onAudioData: ((buffer: ArrayBuffer) => void) | null = null;

  constructor({ targetSampleRate = 16000, bufferSize = 4096 } = {}) {
    this.targetSampleRate = targetSampleRate;
    this.bufferSize = bufferSize;
  }

  async start() {
    // IMPORTANTE: Crear el AudioContext de forma SÍNCRONA antes de cualquier 'await'.
    // Si se hace después de pedir permisos, iOS Safari lo bloquea.
    const AudioCtx = window.AudioContext || (window as any).webkitAudioContext;
    if (!this.audioContext) {
      try {
        this.audioContext = new AudioCtx({ sampleRate: this.targetSampleRate });
      } catch {
        this.audioContext = new AudioCtx();
      }
    }

    // ¡RESUMIR INMEDIATAMENTE ANTES DEL AWAIT!
    // Si esperamos (await), Safari bloqueará el resume porque el "gesto del usuario" expira.
    if (this.audioContext.state === 'suspended') {
      this.audioContext.resume().catch(() => {}); 
    }

    try {
      this.stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          channelCount: 1,
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
        },
      });
    } catch (err: any) {
      if (err.name === 'NotAllowedError') {
        throw new Error('Permiso de micrófono denegado. Permite el acceso e intenta de nuevo.');
      }
      if (err.name === 'NotFoundError') {
        throw new Error('No se encontró ningún micrófono en el dispositivo.');
      }
      throw new Error(`Error al acceder al micrófono: ${err.message}`);
    }

    const nativeSampleRate = this.audioContext.sampleRate;

    this.sourceNode = this.audioContext.createMediaStreamSource(this.stream);
    
    // Configurar AnalyserNode para el visualizador
    this.analyserNode = this.audioContext.createAnalyser();
    this.analyserNode.fftSize = 256;
    
    this.processorNode = this.audioContext.createScriptProcessor(this.bufferSize, 1, 1);

    this.processorNode.onaudioprocess = (event) => {
      if (!this.isCapturing || !this.onAudioData) return;

      const inputData = event.inputBuffer.getChannelData(0);

      let pcmFloat;
      if (Math.abs(nativeSampleRate - this.targetSampleRate) > 1) {
        pcmFloat = this._downsample(inputData, nativeSampleRate, this.targetSampleRate);
      } else {
        pcmFloat = inputData;
      }

      const int16Data = this._floatTo16BitPCM(pcmFloat);
      this.onAudioData(int16Data.buffer as ArrayBuffer);
    };

    // Conectar la cadena
    this.sourceNode.connect(this.analyserNode);
    this.analyserNode.connect(this.processorNode);
    this.processorNode.connect(this.audioContext.destination);

    this.isCapturing = true;
  }

  stop() {
    this.isCapturing = false;

    if (this.processorNode) {
      this.processorNode.disconnect();
      this.processorNode.onaudioprocess = null;
      this.processorNode = null;
    }
    
    if (this.analyserNode) {
      this.analyserNode.disconnect();
      this.analyserNode = null;
    }

    if (this.sourceNode) {
      this.sourceNode.disconnect();
      this.sourceNode = null;
    }

    if (this.stream) {
      this.stream.getTracks().forEach((track) => track.stop());
      this.stream = null;
    }

    if (this.audioContext) {
      this.audioContext.close().catch(() => {});
      this.audioContext = null;
    }
  }

  getAnalyser() {
    return this.analyserNode;
  }

  private _downsample(buffer: Float32Array, fromRate: number, toRate: number): Float32Array {
    const ratio = fromRate / toRate;
    const newLength = Math.round(buffer.length / ratio);
    const result = new Float32Array(newLength);

    for (let i = 0; i < newLength; i++) {
      const index = i * ratio;
      const low = Math.floor(index);
      const high = Math.min(low + 1, buffer.length - 1);
      const frac = index - low;
      result[i] = buffer[low] * (1 - frac) + buffer[high] * frac;
    }

    return result;
  }

  private _floatTo16BitPCM(float32Array: Float32Array): Int16Array {
    const int16 = new Int16Array(float32Array.length);
    for (let i = 0; i < float32Array.length; i++) {
      const s = Math.max(-1, Math.min(1, float32Array[i]));
      int16[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
    }
    return int16;
  }
}
