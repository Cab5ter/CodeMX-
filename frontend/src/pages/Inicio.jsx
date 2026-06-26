import { useState, useEffect } from 'react'
import RetoCard from '../components/RetoCard'
import { getRetos } from '../api/codemx'

const FILTROS = [null, 'BASICO', 'INTERMEDIO', 'AVANZADO']
const FILTRO_LABEL = { null: 'Todos', BASICO: 'Básico', INTERMEDIO: 'Intermedio', AVANZADO: 'Avanzado' }

export default function Inicio() {
  const [retos, setRetos] = useState([])
  const [filtro, setFiltro] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setCargando(true)
    setError(null)
    getRetos(filtro)
      .then(setRetos)
      .catch(e => setError(e.message))
      .finally(() => setCargando(false))
  }, [filtro])

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-100">Retos de Programación</h1>
        <p className="text-gray-400 mt-1">Practica y mejora tus habilidades con retos en español</p>
      </div>

      <div className="flex gap-2 mb-6 flex-wrap">
        {FILTROS.map(f => (
          <button
            key={String(f)}
            onClick={() => setFiltro(f)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium transition-colors ${
              filtro === f
                ? 'bg-emerald-600 text-white'
                : 'bg-gray-800 text-gray-400 hover:text-white border border-gray-700'
            }`}
          >
            {FILTRO_LABEL[f]}
          </button>
        ))}
      </div>

      {cargando && (
        <div className="flex flex-col gap-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="bg-gray-800 rounded-lg p-5 border border-gray-700 animate-pulse h-20" />
          ))}
        </div>
      )}

      {error && <p className="text-red-400">{error}</p>}

      {!cargando && !error && (
        <div className="flex flex-col gap-3">
          {retos.map(reto => (
            <RetoCard key={reto.id} reto={reto} />
          ))}
        </div>
      )}
    </div>
  )
}
