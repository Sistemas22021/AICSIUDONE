import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@cell-component': 'C:/Users/Jeisi Rosales/Documents/AICSIUDONE/teams/main/frontend/lib-react-component/src/components',
    },
  },
})
