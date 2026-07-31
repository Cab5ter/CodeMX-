import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { iniciarSesion } from '../api/codemx'
import { guardarSesion } from '../api/sesion'

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState(null)
  const [cargando, setCargando] = useState(false)
  const navigate = useNavigate()

  function handleChange(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setCargando(true)
    try {
      const usuario = await iniciarSesion(form)
      guardarSesion(usuario)
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="max-w-md mx-auto pt-10">
      <div className="flex items-center gap-2 mb-4">
        <span className="h-px w-8 bg-emerald-500" />
        <span className="text-emerald-400 text-sm font-medium tracking-wide uppercase">Bienvenido de vuelta</span>
      </div>
      <h1 className="text-3xl font-bold text-white mb-2">Iniciar sesión</h1>
      <p className="text-gray-400 mb-8">Entra con tu correo y contraseña para seguir compitiendo.</p>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-7">
        <form onSubmit={handleSubmit} className="space-y-4">
          {[
            { name: 'email',        label: 'Correo electrónico', type: 'email',    placeholder: 'tu@correo.com' },
            { name: 'password', label: 'Contraseña',         type: 'password', placeholder: '••••••••' },
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
            {cargando ? 'Entrando...' : 'Iniciar sesión'}
          </button>
        </form>

        <p className="text-gray-500 text-sm text-center mt-5">
          ¿No tienes cuenta?{' '}
          <Link to="/registro" className="text-emerald-400 hover:underline">Crear una</Link>
        </p>
      </div>
    </div>
  )
}
