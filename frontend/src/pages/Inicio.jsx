import { useState, useEffect } from 'react'
import RetoCard from '../components/RetoCard'
import { getRetos } from '../api/codemx'

const FILTROS = [
  { value: null,         label: 'Todos',       color: 'text-gray-300'    },
  { value: 'BASICO',     label: 'Básico',       color: 'text-emerald-400' },
  { value: 'INTERMEDIO', label: 'Intermedio',   color: 'text-amber-400'   },
  { value: 'AVANZADO',   label: 'Avanzado',     color: 'text-red-400'     },
]

export default function Inicio() {
  const [retos, setRetos] = useState([])
  const [total, setTotal] = useState(0)
  const [filtro, setFiltro] = useState(null)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    getRetos().then(r => setTotal(r.length)).catch(() => {})
  }, [])

  useEffect(() => {
    setCargando(true)
    getRetos(filtro)
      .then(setRetos)
      .finally(() => setCargando(false))
  }, [filtro])

  const STATS = [
    { valor: String(total || '—'), label: 'Retos disponibles' },
    { valor: '3',     label: 'Niveles de dificultad' },
    { valor: '100%',  label: 'En español' },
  ]

  return (
    <div>
      <div className="mb-10 py-8 border-b border-gray-800">
        <div className="flex items-center gap-2 mb-3">
          <span className="h-px w-8 bg-emerald-500" />
          <span className="text-emerald-400 text-sm font-medium tracking-wide uppercase">Plataforma educativa</span>
        </div>
        <h1 className="text-4xl font-bold text-white mb-3 leading-tight">
          Aprende a programar<br />
          <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            resolviendo retos
          </span>
        </h1>
        <p className="text-gray-400 text-lg max-w-xl">
          Retos de programación en español, ordenados por dificultad y diseñados para estudiantes universitarios mexicanos.
        </p>

        <div className="flex gap-8 mt-6">
          {STATS.map(s => (
            <div key={s.label}>
              <div className="text-2xl font-bold text-white">{s.valor}</div>
              <div className="text-xs text-gray-500 mt-0.5">{s.label}</div>
            </div>
          ))}
        </div>
      </div>

      <div className="flex items-center gap-2 mb-5">
        <span className="text-gray-500 text-sm mr-1">Filtrar:</span>
        {FILTROS.map(f => (
          <button
            key={String(f.value)}
            onClick={() => setFiltro(f.value)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium transition-all border ${
              filtro === f.value
                ? 'bg-gray-800 border-gray-600 ' + f.color
                : 'border-transparent text-gray-500 hover:text-gray-300 hover:bg-gray-900'
            }`}
          >
            {f.label}
          </button>
        ))}
      </div>

      {cargando ? (
        <div className="space-y-3">
          {[1, 2, 3, 4].map(i => (
            <div key={i} className="h-[72px] bg-gray-900 rounded-lg border border-gray-800 animate-pulse" />
          ))}
        </div>
      ) : (
        <div className="space-y-2">
          {retos.map(reto => <RetoCard key={reto.id} reto={reto} />)}
        </div>
      )}
    </div>
  )
}
