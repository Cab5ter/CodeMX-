// Manejo simple de sesión en localStorage. Dispara un evento 'sesion' para que el navbar
// (u otros componentes) se actualicen al entrar o salir sin recargar la página.

export function getSesion() {
  const id = localStorage.getItem('usuarioId')
  if (!id) return null
  return { id: Number(id), nombre: localStorage.getItem('nombre') || `Usuario #${id}` }
}

export function guardarSesion(usuario) {
  localStorage.setItem('usuarioId', String(usuario.id))
  localStorage.setItem('nombre', usuario.nombre ?? '')
  window.dispatchEvent(new Event('sesion'))
}

export function cerrarSesion() {
  localStorage.removeItem('usuarioId')
  localStorage.removeItem('nombre')
  window.dispatchEvent(new Event('sesion'))
}
