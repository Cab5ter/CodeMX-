import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    proxy: {
      '/api': { target: 'http://localhost:8080', ws: true },
      // /auth queda fuera de /api, así que necesita su propia regla o el login daría 404.
      '/auth': { target: 'http://localhost:8080' }
    }
  }
})
