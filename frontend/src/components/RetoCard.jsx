import { Link } from 'react-router-dom'

const DIFFICULTY = {
  BASICO:     { label: 'Básico',      border: 'border-l-emerald-500', badge: 'bg-emerald-500/10 text-emerald-400 ring-1 ring-emerald-500/30' },
  INTERMEDIO: { label: 'Intermedio',  border: 'border-l-amber-400',   badge: 'bg-amber-500/10  text-amber-400  ring-1 ring-amber-400/30'  },
  AVANZADO:   { label: 'Avanzado',    border: 'border-l-red-500',     badge: 'bg-red-500/10    text-red-400    ring-1 ring-red-500/30'    },
}

export default function RetoCard({ reto }) {
  const d = DIFFICULTY[reto.dificultad]

  return (
    <Link to={`/retos/${reto.id}`} className="group block">
      <div className={`bg-gray-900 border border-gray-800 border-l-4 ${d.border} rounded-lg px-5 py-4 hover:border-gray-700 hover:bg-gray-800/60 transition-all duration-200`}>
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2 mb-1">
              <span className="text-gray-600 text-xs font-mono">#{String(reto.id).padStart(2, '0')}</span>
            </div>
            <h3 className="text-gray-100 font-semibold group-hover:text-white transition-colors">
              {reto.titulo}
            </h3>
            <p className="text-gray-500 text-sm mt-1.5 line-clamp-1">{reto.descripcion.split('\n')[0]}</p>
          </div>
          <div className="flex flex-col items-end gap-3 flex-shrink-0">
            <span className={`text-xs px-2.5 py-1 rounded-full font-medium ${d.badge}`}>
              {d.label}
            </span>
            <span className="text-gray-600 text-xs group-hover:text-emerald-400 transition-colors font-medium">
              Resolver →
            </span>
          </div>
        </div>
      </div>
    </Link>
  )
}
