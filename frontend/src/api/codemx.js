const BASE = '/api'

export async function getRetos(dificultad = null) {
  const url = dificultad ? `${BASE}/retos?dificultad=${dificultad}` : `${BASE}/retos`
  const res = await fetch(url)
  if (!res.ok) throw new Error('Error al cargar retos')
  return res.json()
}

export async function getRetoById(id) {
  const res = await fetch(`${BASE}/retos/${id}`)
  if (!res.ok) throw new Error('Reto no encontrado')
  return res.json()
}

export async function getRanking() {
  const res = await fetch(`${BASE}/ranking`)
  if (!res.ok) throw new Error('Error al cargar ranking')
  return res.json()
}

export async function registrarUsuario(datos) {
  const res = await fetch(`${BASE}/usuarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos)
  })
  if (!res.ok) throw new Error('Error al registrar usuario')
  return res.json()
}

export async function enviarSolucion({ usuarioId, retoId, codigoFuente }) {
  const res = await fetch(`${BASE}/envios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ usuarioId, retoId, codigoFuente })
  })
  if (!res.ok) throw new Error('Error al enviar solución')
  return res.json()
}
