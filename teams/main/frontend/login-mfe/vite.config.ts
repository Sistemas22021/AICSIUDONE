import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

/**
 * Configuración de Vite para el Login MFE.
 *
 * Puntos clave:
 * - Las variables de entorno con prefijo VITE_ están disponibles en el cliente
 * - El proxy /api evita problemas de CORS en desarrollo local
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    // Proxy: en local, redirige las peticiones /api al Gateway
    // Esto evita CORS errors en desarrollo
    proxy: {
      '/api': {
        target: process.env.VITE_API_GATEWAY_URL || 'http://localhost:8090',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true, // Para debugging en producción (opcional)
  },
  // Las variables VITE_ se inyectan en el build
  define: {
    __APP_VERSION__: JSON.stringify('1.0.0'),
  },
});
