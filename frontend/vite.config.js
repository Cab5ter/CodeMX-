import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,        // escucha en 0.0.0.0 → accesible desde otras máquinas de la red
    proxy: {
      // El proxy corre en la misma máquina que el backend, por eso localhost sí alcanza al .NET.
      '/api': 'http://localhost:8080'
    }
  }
})
