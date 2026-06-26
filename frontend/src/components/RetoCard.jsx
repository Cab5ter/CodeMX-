import { Link } from 'react-router-dom'

const BADGE = {
  BASICO:      'bg-emerald-900 text-emerald-300 border border-emerald-700',
  INTERMEDIO:  'bg-yellow-900  text-yellow-300  border border-yellow-700',
  AVANZADO:    'bg-red-900     text-red-300     border border-red-700'
}

const LABEL = { BASICO: 'Básico', INTERMEDIO: 'Intermedio', AVANZADO: 'Avanzado' }

export default function RetoCard({ reto }) {
  return (
    <Link to={`/retos/${reto.id}`}>
      <div className="bg-gray-800 border border-gray-700 rounded-lg p-5 hover:border-emerald-600 transition-colors">
        <div className="flex items-start justify-between gap-3">
          <div className="min-w-0">
            <span className="text-gray-500 text-xs font-mono">#{reto.id}</span>
            <h3 className="text-gray-100 font-semibold mt-1">{reto.titulo}</h3>
            <p className="text-gray-400 text-sm mt-2 line-clamp-2">{reto.descripcion}</p>
          </div>
          <span className={`text-xs px-2 py-1 rounded font-medium whitespace-nowrap flex-shrink-0 ${BADGE[reto.dificultad]}`}>
            {LABEL[reto.dificultad]}
          </span>
        </div>
      </div>
    </Link>
  )
}
