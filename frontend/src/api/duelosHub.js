import { HubConnectionBuilder, LogLevel } from '@microsoft/signalr'

// Conexión al hub de SignalR del modo 1 vs 1. La URL es relativa, así que pasa por el
// proxy de Vite (que tiene ws: true) hacia el backend en :8080 — funciona igual desde
// localhost o desde otra máquina de la red.
export function crearConexionDuelos() {
  return new HubConnectionBuilder()
    .withUrl('/api/hub/duelos')
    .withAutomaticReconnect()
    .configureLogging(LogLevel.Warning)
    .build()
}
