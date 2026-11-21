import { cabeceraAuth, cerrarSesion, guardarSesion } from './sesion'

const BASE = '/api'

async function pedir(url, opciones = {}, mensajeError = 'Error al llamar la API') {
  const res = await fetch(url, {
    ...opciones,
    headers: { ...(opciones.headers ?? {}), ...cabeceraAuth() },
  })

  if (res.status === 401) {
    cerrarSesion()
    throw new Error('Tu sesión expiró. Vuelve a iniciar sesión.')
  }
  if (!res.ok) throw new Error(mensajeError)

  return res.status === 204 ? null : res.json()
}

function conJson(cuerpo, metodo = 'POST') {
  return {
    method: metodo,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cuerpo),
  }
}

export async function getRetos(dificultad = null) {
  const url = dificultad ? `${BASE}/retos?dificultad=${dificultad}` : `${BASE}/retos`
  return pedir(url, {}, 'Error al cargar retos')
}

export async function getRetoById(id) {
  return pedir(`${BASE}/retos/${id}`, {}, 'Reto no encontrado')
}

export async function getEjemploReto(id) {
  try {
    return await pedir(`${BASE}/retos/${id}/ejemplo`)
  } catch {
    return null
  }
}

export async function getRanking() {
  return pedir(`${BASE}/ranking`, {}, 'Error al cargar ranking')
}

export async function registrarUsuario({ nombre, email, passwordHash }) {
  const res = await fetch(`${BASE}/usuarios`, conJson({ nombre, email, passwordHash }))
  if (res.status === 409) throw new Error('Ese correo ya está registrado')
  if (!res.ok) throw new Error('Error al registrar usuario')

  return iniciarSesion({ email, passwordHash })
}

export async function iniciarSesion({ email, passwordHash }) {
  const res = await fetch('/auth/login', conJson({ email, contrasena: passwordHash }))
  if (res.status === 401) throw new Error('Correo o contraseña incorrectos')
  if (!res.ok) throw new Error('No se pudo iniciar sesión')

  const sesion = await res.json()
  guardarSesion(sesion)
  return sesion
}

export async function enviarSolucion({ retoId, codigoFuente }) {
  return pedir(
    `${BASE}/envios`,
    conJson({ retoId, codigoFuente }),
    'Error al enviar solución',
  )
}


function usuarioParam(usuarioId) {
  return usuarioId ? `?usuarioId=${usuarioId}` : ''
}

export async function getModulos(usuarioId) {
  return pedir(`${BASE}/cursos${usuarioParam(usuarioId)}`, {}, 'Error al cargar los cursos')
}

export async function getModulo(moduloId, usuarioId) {
  return pedir(`${BASE}/cursos/modulos/${moduloId}${usuarioParam(usuarioId)}`, {}, 'Módulo no encontrado')
}

export async function getLeccion(leccionId, usuarioId) {
  return pedir(`${BASE}/cursos/lecciones/${leccionId}${usuarioParam(usuarioId)}`, {}, 'Lección no encontrada')
}

export async function completarLeccion(leccionId, usuarioId) {
  await pedir(
    `${BASE}/cursos/lecciones/${leccionId}/completar?usuarioId=${usuarioId}`,
    { method: 'POST' },
    'No se pudo marcar la lección',
  )
}

export async function getExamen(moduloId, usuarioId) {
  const res = await fetch(`${BASE}/cursos/modulos/${moduloId}/examen?usuarioId=${usuarioId}`, {
    headers: cabeceraAuth(),
  })
  if (res.status === 401) {
    cerrarSesion()
    throw new Error('Tu sesión expiró. Vuelve a iniciar sesión.')
  }
  if (res.status === 403) throw new Error('BLOQUEADO')
  if (!res.ok) throw new Error('No se pudo cargar el examen')
  return res.json()
}

export async function enviarExamen(moduloId, usuarioId, respuestas) {
  return pedir(
    `${BASE}/cursos/modulos/${moduloId}/examen?usuarioId=${usuarioId}`,
    conJson(respuestas),
    'No se pudo calificar el examen',
  )
}
