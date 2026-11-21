
const CLAVE_TOKEN = 'token'

export function getSesion() {
  const id = localStorage.getItem('usuarioId')
  if (!id) return null
  return { id: Number(id), nombre: localStorage.getItem('nombre') || `Usuario #${id}` }
}

export function getToken() {
  return localStorage.getItem(CLAVE_TOKEN)
}

export function cabeceraAuth() {
  const token = getToken()
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function guardarSesion(sesion) {
  localStorage.setItem('usuarioId', String(sesion.usuarioId ?? sesion.id))
  localStorage.setItem('nombre', sesion.nombre ?? '')
  if (sesion.token) localStorage.setItem(CLAVE_TOKEN, sesion.token)
  window.dispatchEvent(new Event('sesion'))
}

export function cerrarSesion() {
  localStorage.removeItem('usuarioId')
  localStorage.removeItem('nombre')
  localStorage.removeItem(CLAVE_TOKEN)
  window.dispatchEvent(new Event('sesion'))
}
