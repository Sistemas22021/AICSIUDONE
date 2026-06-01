import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

/**
 * Configuración de Vite para el Login MFE.
 * Tailwind v4 usa un plugin nativo de Vite, sin PostCSS.
 */
export default defineConfig({
  plugins: [
    tailwindcss(),
    react(),
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: process.env.VITE_API_GATEWAY_URL || 'http://localhost:8090',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
  },
  define: {
    __APP_VERSION__: JSON.stringify('1.0.0'),
  },
});
