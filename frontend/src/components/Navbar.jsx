import { Link, NavLink } from 'react-router-dom'

export default function Navbar() {
  const linkClass = ({ isActive }) =>
    `px-3 py-2 rounded text-sm font-medium transition-colors ${
      isActive
        ? 'bg-emerald-600 text-white'
        : 'text-gray-300 hover:text-white hover:bg-gray-700'
    }`

  return (
    <nav className="bg-gray-800 border-b border-gray-700">
      <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="text-emerald-400 font-bold text-xl font-mono">
          {'<CodeMX/>'}
        </Link>
        <div className="flex gap-1">
          <NavLink to="/" className={linkClass} end>Retos</NavLink>
          <NavLink to="/ranking" className={linkClass}>Ranking</NavLink>
          <NavLink to="/registro" className={linkClass}>Registro</NavLink>
        </div>
      </div>
    </nav>
  )
}
