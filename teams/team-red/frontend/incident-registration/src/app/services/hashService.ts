
export async function sha256DeTexto(texto: string): Promise<string> {
    const encoder = new TextEncoder()
    const data = encoder.encode(texto)
    const hashBuffer = await crypto.subtle.digest('SHA-256', data)
    return bufferToHex(hashBuffer)
}

export async function sha256DeArchivo(archivo: File): Promise<string> {
    const arrayBuffer = await archivo.arrayBuffer()
    const hashBuffer = await crypto.subtle.digest('SHA-256', arrayBuffer)
    return bufferToHex(hashBuffer)
}

function bufferToHex(buffer: ArrayBuffer): string {
    return Array.from(new Uint8Array(buffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('')
}