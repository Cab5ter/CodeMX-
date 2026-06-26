import { Link, NavLink } from 'react-router-dom'

export default function Navbar() {
  const usuarioId = localStorage.getItem('usuarioId')

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
          <NavLink to="/" className={linkClass} end>Retos</NavLink>
          <NavLink to="/ranking" className={linkClass}>Ranking</NavLink>
          <NavLink to="/registro" className={linkClass}>Registro</NavLink>
        </div>

        {usuarioId && (
          <div className="flex items-center gap-2 bg-gray-800 px-3 py-1.5 rounded-full border border-gray-700">
            <span className="w-2 h-2 rounded-full bg-emerald-400" />
            <span className="text-xs text-gray-300 font-mono">Usuario #{usuarioId}</span>
          </div>
        )}
      </div>
    </nav>
  )
}
