// Conexión al canal de tiempo real del modo 1 vs 1.
//
// El backend expone un WebSocket en /api/hub/duelos con un protocolo JSON simétrico:
// { target, arguments }. Esta función lo envuelve en una superficie pequeña —
// on / onclose / start / stop / invoke — para que los componentes no hablen del
// protocolo ni del transporte.
//
// La URL es relativa, así que pasa por el proxy de Vite (que tiene ws: true) hacia el
// backend en :8080 — funciona igual desde localhost o desde otra máquina de la red.
export function crearConexionDuelos() {
  const handlers = new Map()   // target -> [callback]
  const alCerrar = []
  let socket = null
  let cerradaPorNosotros = false

  function urlDelHub() {
    const protocolo = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    return `${protocolo}//${window.location.host}/api/hub/duelos`
  }

  return {
    /** Registra un callback para un evento que emite el servidor. */
    on(target, callback) {
      const lista = handlers.get(target) ?? []
      lista.push(callback)
      handlers.set(target, lista)
    },

    /** Se dispara si el servidor cierra la conexión (no al llamar a stop()). */
    onclose(callback) {
      alCerrar.push(callback)
    },

    start() {
      return new Promise((resolve, reject) => {
        cerradaPorNosotros = false
        socket = new WebSocket(urlDelHub())

        socket.onopen = () => resolve()

        socket.onerror = () => reject(new Error('No se pudo abrir el canal de duelos'))

        socket.onmessage = evento => {
          let sobre
          try {
            sobre = JSON.parse(evento.data)
          } catch {
            return
          }
          const lista = handlers.get(sobre.target) ?? []
          // El servidor manda 0 o 1 argumento por evento (igual que el hub anterior).
          for (const callback of lista) callback(...(sobre.arguments ?? []))
        }

        socket.onclose = () => {
          if (!cerradaPorNosotros) {
            for (const callback of alCerrar) callback()
          }
        }
      })
    },

    /**
     * Invoca un método del servidor. No espera confirmación: el servidor responde
     * siempre por un evento (ResultadoEnvio, DueloIniciado, …), así que la promesa
     * sólo refleja que el mensaje salió.
     */
    invoke(target, ...args) {
      return new Promise((resolve, reject) => {
        if (!socket || socket.readyState !== WebSocket.OPEN) {
          reject(new Error('El canal de duelos no está conectado'))
          return
        }
        socket.send(JSON.stringify({ target, arguments: args }))
        resolve()
      })
    },

    stop() {
      return new Promise(resolve => {
        if (!socket || socket.readyState === WebSocket.CLOSED) {
          resolve()
          return
        }
        cerradaPorNosotros = true
        socket.addEventListener('close', () => resolve(), { once: true })
        socket.close()
      })
    },
  }
}
