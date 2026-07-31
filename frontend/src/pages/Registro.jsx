import { useState } from 'react'
import { Link } from 'react-router-dom'
import { registrarUsuario } from '../api/codemx'
import { guardarSesion } from '../api/sesion'

const BENEFICIOS = [
  { icono: '🎯', texto: '8 retos de programación en español' },
  { icono: '📈', texto: 'Seguimiento de tu progreso en el ranking' },
  { icono: '⚡', texto: 'Evaluación automática de tu código' },
  { icono: '🏆', texto: 'Puntos según la dificultad del reto' },
]

export default function Registro() {
  const [form, setForm] = useState({ nombre: '', email: '', password: '' })
  const [usuario, setUsuario] = useState(null)
  const [error, setError] = useState(null)
  const [cargando, setCargando] = useState(false)

  function handleChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setCargando(true)
    try {
      const u = await registrarUsuario(form)
      setUsuario(u)
      guardarSesion(u)   // inicia sesión y avisa al navbar
    } catch (err) {
      // El backend explica el motivo: correo duplicado, contraseña corta, correo inválido.
      setError(err.message)
    } finally {
      setCargando(false)
    }
  }

  if (usuario) {
    return (
      <div className="max-w-md mx-auto text-center pt-8">
        <div className="text-6xl mb-4">🎉</div>
        <h2 className="text-2xl font-bold text-white mb-2">¡Bienvenido, {usuario.nombre}!</h2>
        <p className="text-gray-400 mb-6">Tu cuenta ha sido creada exitosamente.</p>

        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 mb-6">
          <p className="text-gray-500 text-sm mb-1">Tu ID de usuario</p>
          <p className="font-mono text-4xl font-bold text-emerald-400">#{usuario.id}</p>
          <p className="text-gray-600 text-xs mt-2">Guarda este número — lo necesitas para enviar soluciones</p>
        </div>

        <Link
          to="/"
          className="inline-block bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-8 py-3 rounded-xl transition-colors"
        >
          Ir a los retos →
        </Link>
      </div>
    )
  }

  return (
    <div className="grid lg:grid-cols-2 gap-12 items-start pt-4">
      {/* Lado izquierdo */}
      <div>
        <div className="flex items-center gap-2 mb-4">
          <span className="h-px w-8 bg-emerald-500" />
          <span className="text-emerald-400 text-sm font-medium tracking-wide uppercase">Gratis</span>
        </div>
        <h1 className="text-3xl font-bold text-white mb-3 leading-snug">
          Únete a <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">CodeMX</span>
        </h1>
        <p className="text-gray-400 mb-8">
          La plataforma de retos de programación en español para estudiantes universitarios de México.
        </p>

        <div className="space-y-4">
          {BENEFICIOS.map(b => (
            <div key={b.texto} className="flex items-center gap-3">
              <span className="text-xl w-8 text-center">{b.icono}</span>
              <span className="text-gray-300 text-sm">{b.texto}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Formulario */}
      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-7">
        <h2 className="text-lg font-semibold text-white mb-5">Crear cuenta</h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          {[
            { name: 'nombre',       label: 'Nombre completo', type: 'text',     placeholder: 'Tu nombre' },
            { name: 'email',        label: 'Correo electrónico', type: 'email', placeholder: 'tu@correo.com' },
            { name: 'password', label: 'Contraseña',      type: 'password', placeholder: '••••••••' },
          ].map(field => (
            <div key={field.name}>
              <label className="text-gray-400 text-xs font-medium block mb-1.5 uppercase tracking-wide">
                {field.label}
              </label>
              <input
                type={field.type}
                name={field.name}
                value={form[field.name]}
                onChange={handleChange}
                required
                className="w-full bg-gray-950 border border-gray-800 focus:border-emerald-500 rounded-lg px-4 py-2.5 text-gray-200 text-sm outline-none transition-colors"
                placeholder={field.placeholder}
              />
            </div>
          ))}

          {error && <p className="text-red-400 text-sm">{error}</p>}

          <button
            type="submit"
            disabled={cargando}
            className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-800 disabled:text-gray-600 text-white font-semibold py-3 rounded-xl transition-colors mt-2"
          >
            {cargando ? 'Creando cuenta...' : 'Crear cuenta'}
          </button>
        </form>

        <p className="text-gray-500 text-sm text-center mt-5">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-emerald-400 hover:underline">Iniciar sesión</Link>
        </p>
      </div>
    </div>
  )
}
