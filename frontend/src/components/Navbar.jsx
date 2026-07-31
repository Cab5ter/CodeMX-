import { useState, useEffect } from 'react'
import { Link, NavLink, useNavigate, useLocation } from 'react-router-dom'
import { getSesion, cerrarSesion } from '../api/sesion'

const ENLACES = [
  { to: '/',           texto: 'Cursos',     end: true },
  { to: '/ejercicios', texto: 'Ejercicios' },
  { to: '/vs',         texto: '1 vs 1' },
  { to: '/ranking',    texto: 'Ranking' },
]

export default function Navbar() {
  const [sesion, setSesion] = useState(getSesion())
  const [menuAbierto, setMenuAbierto] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  // Se mantiene al día cuando entras o sales (evento 'sesion') o cambia otra pestaña ('storage').
  useEffect(() => {
    const refrescar = () => setSesion(getSesion())
    window.addEventListener('sesion', refrescar)
    window.addEventListener('storage', refrescar)
    return () => {
      window.removeEventListener('sesion', refrescar)
      window.removeEventListener('storage', refrescar)
    }
  }, [])

  // En móvil el menú se cierra al navegar; si no, queda tapando la página nueva.
  useEffect(() => { setMenuAbierto(false) }, [location.pathname])

  function salir() {
    cerrarSesion()
    setMenuAbierto(false)
    navigate('/login')
  }

  const linkEscritorio = ({ isActive }) =>
    `px-3 lg:px-4 py-2 text-sm font-medium transition-all border-b-2 whitespace-nowrap ${
      isActive
        ? 'border-emerald-400 text-emerald-400'
        : 'border-transparent text-gray-400 hover:text-gray-100 hover:border-gray-600'
    }`

  const linkMovil = ({ isActive }) =>
    `block px-4 py-3 rounded-lg text-base font-medium transition-colors ${
      isActive ? 'bg-emerald-500/10 text-emerald-400' : 'text-gray-300 hover:bg-gray-800'
    }`

  return (
    <nav className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 flex items-center justify-between h-14 gap-2">
        <Link to="/" className="flex items-center gap-2 shrink-0">
          <span className="font-mono font-bold text-base sm:text-lg bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            &lt;CodeMX/&gt;
          </span>
        </Link>

        {/* Navegación de escritorio */}
        <div className="hidden md:flex items-center h-14">
          {ENLACES.map(e => (
            <NavLink key={e.to} to={e.to} className={linkEscritorio} end={e.end}>{e.texto}</NavLink>
          ))}
          {!sesion && <NavLink to="/registro" className={linkEscritorio}>Registro</NavLink>}
        </div>

        {/* Sesión en escritorio */}
        <div className="hidden md:block">
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

        {/* Botón hamburguesa: sólo en móvil. 44px de lado para cumplir el mínimo táctil. */}
        <button
          onClick={() => setMenuAbierto(a => !a)}
          className="md:hidden flex items-center justify-center w-11 h-11 -mr-2 rounded-lg text-gray-300 hover:bg-gray-800 transition-colors"
          aria-label={menuAbierto ? 'Cerrar menú' : 'Abrir menú'}
          aria-expanded={menuAbierto}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            {menuAbierto
              ? <><line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" /></>
              : <><line x1="3" y1="6" x2="21" y2="6" /><line x1="3" y1="12" x2="21" y2="12" /><line x1="3" y1="18" x2="21" y2="18" /></>}
          </svg>
        </button>
      </div>

      {/* Panel desplegable en móvil */}
      {menuAbierto && (
        <div className="md:hidden border-t border-gray-800 bg-gray-900 px-4 py-3 space-y-1">
          {ENLACES.map(e => (
            <NavLink key={e.to} to={e.to} className={linkMovil} end={e.end}>{e.texto}</NavLink>
          ))}

          <div className="pt-3 mt-2 border-t border-gray-800">
            {sesion ? (
              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-2 min-w-0">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 shrink-0" />
                  <span className="text-sm text-gray-200 font-medium truncate">{sesion.nombre}</span>
                </div>
                <button
                  onClick={salir}
                  className="text-sm text-gray-400 hover:text-red-400 px-4 py-2 rounded-lg border border-gray-700 shrink-0"
                >
                  Salir
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-2 gap-2">
                <Link to="/login" className="text-center text-sm text-emerald-400 px-4 py-3 rounded-lg border border-emerald-500/40">
                  Iniciar sesión
                </Link>
                <Link to="/registro" className="text-center text-sm font-medium text-gray-950 bg-emerald-500 px-4 py-3 rounded-lg">
                  Registro
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}
