export function crearConexionDuelos() {
  const handlers = new Map()
  const alCerrar = []
  let socket = null
  let cerradaPorNosotros = false

  function urlDelHub() {
    const protocolo = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    return `${protocolo}//${window.location.host}/api/hub/duelos`
  }

  return {
    on(target, callback) {
      const lista = handlers.get(target) ?? []
      lista.push(callback)
      handlers.set(target, lista)
    },

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
          for (const callback of lista) callback(...(sobre.arguments ?? []))
        }

        socket.onclose = () => {
          if (!cerradaPorNosotros) {
            for (const callback of alCerrar) callback()
          }
        }
      })
    },

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
