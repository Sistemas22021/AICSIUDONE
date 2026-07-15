export type TranscriptData = {
  text: string;
  isFinal: boolean;
  language: string;
  confidence: number;
};

export class GladiaWebSocket {
  socket: WebSocket | null = null;

  onTranscript: ((data: TranscriptData) => void) | null = null;
  onLifecycle: ((data: any) => void) | null = null;
  onError: ((error: Event | Error) => void) | null = null;
  onClose: ((event: CloseEvent) => void) | null = null;
  onOpen: (() => void) | null = null;

  connect(wsUrl: string): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.socket = new WebSocket(wsUrl);
        this.socket.binaryType = 'arraybuffer';
      } catch (err: any) {
        reject(new Error(`No se pudo crear la conexión WebSocket: ${err.message}`));
        return;
      }

      const timeout = setTimeout(() => {
        reject(new Error('Timeout al conectar con Gladia (10s)'));
        this.disconnect();
      }, 10000);

      this.socket.onopen = () => {
        clearTimeout(timeout);
        if (this.onOpen) this.onOpen();
        resolve();
      };

      this.socket.onmessage = (event) => {
        this._handleMessage(event);
      };

      this.socket.onerror = (event) => {
        clearTimeout(timeout);
        const error = new Error('Error en la conexión WebSocket con Gladia');
        if (this.onError) this.onError(error);
        reject(error);
      };

      this.socket.onclose = (event) => {
        clearTimeout(timeout);
        if (this.onClose) this.onClose(event);
      };
    });
  }

  sendAudio(audioBuffer: ArrayBuffer) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(audioBuffer);
    }
  }

  stopRecording() {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ type: 'stop_recording' }));
    }
  }

  disconnect() {
    if (this.socket) {
      try {
        this.socket.close(1000, 'Client disconnect');
      } catch {
        // Ignorar errores al cerrar
      }
      this.socket = null;
    }
  }

  get isConnected() {
    return this.socket !== null && this.socket.readyState === WebSocket.OPEN;
  }

  private _handleMessage(event: MessageEvent) {
    if (typeof event.data !== 'string') return;

    let message;
    try {
      message = JSON.parse(event.data);
    } catch {
      console.warn('[GladiaWS] Mensaje no-JSON recibido:', event.data);
      return;
    }

    switch (message.type) {
      case 'transcript':
        if (this.onTranscript) {
          this.onTranscript({
            text: message.data?.utterance?.text || '',
            isFinal: message.data?.is_final || false,
            language: message.data?.utterance?.language || '',
            confidence: message.data?.utterance?.confidence || 0,
          });
        }
        break;

      case 'lifecycle':
        if (this.onLifecycle) {
          this.onLifecycle(message.data || message);
        }
        break;
    }
  }
}
