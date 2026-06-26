import { useState } from 'react'
import { Link } from 'react-router-dom'
import { registrarUsuario } from '../api/codemx'

export default function Registro() {
  const [form, setForm] = useState({ nombre: '', email: '', passwordHash: '' })
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
      localStorage.setItem('usuarioId', String(u.id))
    } catch {
      setError('No se pudo crear la cuenta. ¿El correo ya está registrado?')
    } finally {
      setCargando(false)
    }
  }

  if (usuario) {
    return (
      <div className="max-w-md mx-auto text-center">
        <div className="bg-emerald-900 border border-emerald-600 rounded-lg p-8">
          <p className="text-emerald-300 text-xl font-bold mb-2">¡Cuenta creada!</p>
          <p className="text-gray-300">Bienvenido, <span className="font-semibold">{usuario.nombre}</span></p>
          <div className="mt-4 bg-gray-900 rounded-lg p-3 inline-block">
            <p className="text-gray-500 text-xs">Tu ID de usuario</p>
            <p className="text-emerald-400 font-mono font-bold text-2xl">#{usuario.id}</p>
          </div>
          <p className="text-gray-500 text-xs mt-4">
            Guarda este número — lo usarás para enviar soluciones.
          </p>
          <Link
            to="/"
            className="mt-6 inline-block bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-6 py-2.5 rounded-lg transition-colors"
          >
            Ver retos →
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-md mx-auto">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-100">Crear cuenta</h1>
        <p className="text-gray-400 mt-1">Únete a CodeMX y empieza a resolver retos</p>
      </div>

      <form onSubmit={handleSubmit} className="bg-gray-800 rounded-lg border border-gray-700 p-6 flex flex-col gap-4">
        <div>
          <label className="text-gray-400 text-sm block mb-1">Nombre completo</label>
          <input
            type="text"
            name="nombre"
            value={form.nombre}
            onChange={handleChange}
            required
            className="w-full bg-gray-900 border border-gray-700 rounded px-3 py-2 text-gray-200 text-sm outline-none focus:border-emerald-500 transition-colors"
            placeholder="Tu nombre"
          />
        </div>

        <div>
          <label className="text-gray-400 text-sm block mb-1">Correo electrónico</label>
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full bg-gray-900 border border-gray-700 rounded px-3 py-2 text-gray-200 text-sm outline-none focus:border-emerald-500 transition-colors"
            placeholder="tu@correo.com"
          />
        </div>

        <div>
          <label className="text-gray-400 text-sm block mb-1">Contraseña</label>
          <input
            type="password"
            name="passwordHash"
            value={form.passwordHash}
            onChange={handleChange}
            required
            className="w-full bg-gray-900 border border-gray-700 rounded px-3 py-2 text-gray-200 text-sm outline-none focus:border-emerald-500 transition-colors"
            placeholder="••••••••"
          />
        </div>

        {error && <p className="text-red-400 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={cargando}
          className="bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-700 disabled:text-gray-500 text-white font-semibold py-2.5 rounded-lg transition-colors mt-2"
        >
          {cargando ? 'Creando cuenta...' : 'Crear cuenta'}
        </button>
      </form>
    </div>
  )
}
