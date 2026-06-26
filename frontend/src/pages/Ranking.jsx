import { useState, useEffect } from 'react'
import TablaPosiciones from '../components/TablaPosiciones'
import { getRanking } from '../api/codemx'

export default function Ranking() {
  const [entradas, setEntradas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    getRanking()
      .then(setEntradas)
      .catch(e => setError(e.message))
      .finally(() => setCargando(false))
  }, [])

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-100">Tabla de Posiciones</h1>
        <p className="text-gray-400 mt-1">Los mejores resolutores de CodeMX</p>
      </div>

      {cargando && (
        <div className="space-y-2">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-12 bg-gray-800 rounded animate-pulse" />
          ))}
        </div>
      )}

      {error && <p className="text-red-400">{error}</p>}

      {!cargando && !error && <TablaPosiciones entradas={entradas} />}
    </div>
  )
}
