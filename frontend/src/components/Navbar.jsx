import { useState, useEffect } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { getSesion, cerrarSesion } from '../api/sesion'

export default function Navbar() {
  const [sesion, setSesion] = useState(getSesion())
  const navigate = useNavigate()

  useEffect(() => {
    const refrescar = () => setSesion(getSesion())
    window.addEventListener('sesion', refrescar)
    window.addEventListener('storage', refrescar)
    return () => {
      window.removeEventListener('sesion', refrescar)
      window.removeEventListener('storage', refrescar)
    }
  }, [])

  function salir() {
    cerrarSesion()
    navigate('/login')
  }

  const linkClass = ({ isActive }) =>
    `px-4 py-2 text-sm font-medium transition-all border-b-2 ${
      isActive
        ? 'border-emerald-400 text-emerald-400'
        : 'border-transparent text-gray-400 hover:text-gray-100 hover:border-gray-600'
    }`

  return (
    <nav className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 flex items-center justify-between h-14">
        <Link to="/" className="flex items-center gap-2">
          <span className="font-mono font-bold text-lg bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            &lt;CodeMX/&gt;
          </span>
        </Link>

        <div className="flex items-center h-14">
          <NavLink to="/" className={linkClass} end>Cursos</NavLink>
          <NavLink to="/ejercicios" className={linkClass}>Ejercicios</NavLink>
          <NavLink to="/vs" className={linkClass}>1 vs 1</NavLink>
          <NavLink to="/ranking" className={linkClass}>Ranking</NavLink>
          {!sesion && <NavLink to="/registro" className={linkClass}>Registro</NavLink>}
        </div>

        {sesion ? (
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-2 bg-gray-800 px-3 py-1.5 rounded-full border border-gray-700">
              <span className="w-2 h-2 rounded-full bg-emerald-400" />
              <span className="text-xs text-gray-200 font-medium max-w-[120px] truncate" title={sesion.nombre}>
                {sesion.nombre}
              </span>
            </div>
            <button
              onClick={salir}
              className="text-xs text-gray-400 hover:text-red-400 px-3 py-1.5 rounded-full border border-gray-700 hover:border-red-500/40 transition-colors"
            >
              Salir
            </button>
          </div>
        ) : (
          <Link
            to="/login"
            className="text-sm text-emerald-400 hover:text-emerald-300 px-4 py-1.5 rounded-full border border-emerald-500/40 hover:border-emerald-500/70 transition-colors"
          >
            Iniciar sesión
          </Link>
        )}
      </div>
    </nav>
  )
}
