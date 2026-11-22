import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,        // escucha en 0.0.0.0 → accesible desde otras máquinas de la red
    proxy: {
      // El proxy corre en la misma máquina que el backend, por eso localhost sí alcanza al .NET.
      // ws: true permite que el hub de SignalR (/api/hub/duelos) use WebSockets a través del proxy.
      '/api': { target: 'http://localhost:8080', ws: true }
    }
  }
})
