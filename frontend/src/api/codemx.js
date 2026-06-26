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
  // Vista de Procesos (ADR-02): el envío entra por el módulo Retos.
  const res = await fetch(`${BASE}/retos/${retoId}/submit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ usuarioId, codigoFuente })
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
