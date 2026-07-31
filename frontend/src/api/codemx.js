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

export async function getEjemploReto(id) {
  const res = await fetch(`${BASE}/retos/${id}/ejemplo`)
  if (!res.ok) return null
  return res.json()
}

export async function getRanking() {
  const res = await fetch(`${BASE}/ranking`)
  if (!res.ok) throw new Error('Error al cargar ranking')
  return res.json()
}

// La contraseña viaja en claro sobre HTTPS y el servidor la hashea con BCrypt (ADR-07).
// El cliente nunca calcula ni almacena hashes.
export async function registrarUsuario({ nombre, email, password }) {
  const res = await fetch(`${BASE}/usuarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, email, password })
  })
  if (!res.ok) throw new Error(await mensajeDeError(res, 'Error al registrar usuario'))
  return res.json()
}

export async function iniciarSesion({ email, password }) {
  const res = await fetch(`${BASE}/usuarios/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  if (res.status === 401) throw new Error('Correo o contraseña incorrectos')
  if (!res.ok) throw new Error(await mensajeDeError(res, 'No se pudo iniciar sesión'))
  return res.json()
}

// El backend responde { mensaje } en 400/409; si no, se usa el texto por defecto.
async function mensajeDeError(res, porDefecto) {
  try {
    const cuerpo = await res.json()
    return cuerpo?.mensaje || porDefecto
  } catch {
    return porDefecto
  }
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

// ---------- Módulo Cursos (aprendizaje) ----------

function usuarioParam(usuarioId) {
  return usuarioId ? `?usuarioId=${usuarioId}` : ''
}

export async function getModulos(usuarioId) {
  const res = await fetch(`${BASE}/cursos${usuarioParam(usuarioId)}`)
  if (!res.ok) throw new Error('Error al cargar los cursos')
  return res.json()
}

export async function getModulo(moduloId, usuarioId) {
  const res = await fetch(`${BASE}/cursos/modulos/${moduloId}${usuarioParam(usuarioId)}`)
  if (!res.ok) throw new Error('Módulo no encontrado')
  return res.json()
}

export async function getLeccion(leccionId, usuarioId) {
  const res = await fetch(`${BASE}/cursos/lecciones/${leccionId}${usuarioParam(usuarioId)}`)
  if (!res.ok) throw new Error('Lección no encontrada')
  return res.json()
}

export async function completarLeccion(leccionId, usuarioId) {
  const res = await fetch(`${BASE}/cursos/lecciones/${leccionId}/completar?usuarioId=${usuarioId}`, {
    method: 'POST'
  })
  if (!res.ok) throw new Error('No se pudo marcar la lección')
}

export async function getExamen(moduloId, usuarioId) {
  const res = await fetch(`${BASE}/cursos/modulos/${moduloId}/examen?usuarioId=${usuarioId}`)
  if (res.status === 403) throw new Error('BLOQUEADO')
  if (!res.ok) throw new Error('No se pudo cargar el examen')
  return res.json()
}

export async function enviarExamen(moduloId, usuarioId, respuestas) {
  const res = await fetch(`${BASE}/cursos/modulos/${moduloId}/examen?usuarioId=${usuarioId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(respuestas)
  })
  if (!res.ok) throw new Error('No se pudo calificar el examen')
  return res.json()
}
