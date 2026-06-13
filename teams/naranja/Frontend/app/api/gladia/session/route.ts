import { NextResponse } from 'next/server';

export async function POST(request: Request) {
  try {
    const body = await request.json().catch(() => ({}));
    const language = body.language || 'es';

    const apiKey = process.env.GLADIA_API_KEY;
    if (!apiKey) {
      return NextResponse.json(
        { error: 'GLADIA_API_KEY no está configurada.' },
        { status: 500 }
      );
    }

    const sessionConfig = {
      model: 'solaria-1',
      encoding: 'wav/pcm',
      sample_rate: 16000,
      bit_depth: 16,
      channels: 1,
      language_config: {
        languages: [language],
        code_switching: false,
      },
      messages_config: {
        receive_partial_transcripts: true,
        receive_final_transcripts: true,
      },
    };

    const response = await fetch('https://api.gladia.io/v2/live', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'x-gladia-key': apiKey,
      },
      body: JSON.stringify(sessionConfig),
    });

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: `Error de la API de Gladia: ${errorText}` },
        { status: response.status }
      );
    }

    const data = await response.json();
    return NextResponse.json({
      id: data.id,
      ws_url: data.url,
    });
  } catch (error: any) {
    console.error('[Session Error]', error.message);
    return NextResponse.json(
      { error: 'No se pudo crear la sesión de transcripción', details: error.message },
      { status: 500 }
    );
  }
}
