import { useState, useEffect } from 'react'
import TablaPosiciones from '../components/TablaPosiciones'
import { getRanking } from '../api/codemx'

export default function Ranking() {
  const [entradas, setEntradas] = useState([])
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    getRanking()
      .then(setEntradas)
      .finally(() => setCargando(false))
  }, [])

  return (
    <div>
      <div className="mb-8 pb-6 border-b border-gray-800">
        <div className="flex items-center gap-2 mb-2">
          <span className="h-px w-8 bg-emerald-500" />
          <span className="text-emerald-400 text-sm font-medium tracking-wide uppercase">Global</span>
        </div>
        <h1 className="text-3xl font-bold text-white">Tabla de Posiciones</h1>
        <p className="text-gray-500 mt-1">Los mejores resolutores de CodeMX, ordenados por puntos.</p>
      </div>

      {cargando ? (
        <div className="grid grid-cols-3 gap-3 mb-6">
          {[1, 2, 3].map(i => <div key={i} className="h-32 bg-gray-900 rounded-xl animate-pulse border border-gray-800" />)}
        </div>
      ) : (
        <TablaPosiciones entradas={entradas} />
      )}
    </div>
  )
}
