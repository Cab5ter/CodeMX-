import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import BarraProgreso from '../components/BarraProgreso'
import { getModulo } from '../api/codemx'

export default function ModuloDetalle() {
  const { id } = useParams()
  const [modulo, setModulo] = useState(null)
  const usuarioId = localStorage.getItem('usuarioId')

  useEffect(() => {
    getModulo(id, usuarioId).then(setModulo)
  }, [id, usuarioId])

  if (!modulo) return (
    <div className="space-y-4">
      <div className="h-8 bg-gray-900 rounded animate-pulse w-1/2" />
      <div className="h-4 bg-gray-900 rounded animate-pulse w-full" />
      <div className="h-64 bg-gray-900 rounded-xl animate-pulse mt-6" />
    </div>
  )

  return (
    <div className="max-w-3xl mx-auto">
      <Link to="/" className="inline-flex items-center gap-1.5 text-gray-500 hover:text-gray-300 text-sm mb-6 transition-colors">
        ← Todos los cursos
      </Link>

      {/* Cabecera */}
      <div className="flex items-start gap-4 mb-6">
        <span className="text-4xl sm:text-5xl">{modulo.icono}</span>
        <div className="flex-1">
          <h1 className="text-2xl font-bold text-white">{modulo.titulo}</h1>
          <p className="text-gray-400 text-sm mt-1">{modulo.descripcion}</p>
        </div>
      </div>

      {/* Progreso */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5 mb-6">
        <div className="flex justify-between text-sm mb-2">
          <span className="text-gray-400">{modulo.leccionesCompletadas} de {modulo.totalLecciones} lecciones completadas</span>
          <span className="text-emerald-400 font-semibold">{modulo.progreso}%</span>
        </div>
        <BarraProgreso progreso={modulo.progreso} />
      </div>

      {/* Lecciones */}
      <div className="space-y-2 mb-6">
        {modulo.lecciones.map((l, i) => {
          const destino = l.tipo === 'EJERCICIO' ? `/retos/${l.retoId}` : `/lecciones/${l.id}`
          return (
            <Link key={l.id} to={destino} className="group block">
              <div className="flex items-center gap-4 bg-gray-900 border border-gray-800 rounded-xl px-5 py-4 hover:border-gray-700 transition-all">
                {/* Estado */}
                <div className={`flex items-center justify-center w-8 h-8 rounded-full flex-shrink-0 text-sm font-bold ${
                  l.completada
                    ? 'bg-emerald-500 text-gray-950'
                    : 'bg-gray-800 text-gray-500 ring-1 ring-gray-700'
                }`}>
                  {l.completada ? '✓' : i + 1}
                </div>

                <div className="flex-1 min-w-0">
                  <p className="text-gray-100 font-medium group-hover:text-white">{l.titulo}</p>
                  <p className="text-xs mt-0.5">
                    {l.tipo === 'EJERCICIO'
                      ? <span className="text-amber-400">⚡ Ejercicio práctico</span>
                      : <span className="text-cyan-400">📖 Lección de teoría</span>}
                  </p>
                </div>

                <span className="text-gray-600 group-hover:text-emerald-400 transition-colors text-sm">→</span>
              </div>
            </Link>
          )
        })}
      </div>

      {/* Examen */}
      <div className={`rounded-xl border p-5 ${
        modulo.examenDesbloqueado
          ? 'bg-emerald-500/5 border-emerald-500/40'
          : 'bg-gray-900 border-gray-800'
      }`}>
        <div className="flex items-center justify-between gap-4">
          <div>
            <h3 className="text-white font-semibold flex items-center gap-2">
              {modulo.examenDesbloqueado ? '📝' : '🔒'} Examen del módulo
            </h3>
            <p className="text-gray-400 text-sm mt-1">
              {modulo.examenDesbloqueado
                ? 'Ya puedes presentar el examen. ¡Demuestra lo que aprendiste!'
                : `Completa al menos el ${modulo.umbralExamen}% de las lecciones para desbloquearlo (vas en ${modulo.progreso}%).`}
            </p>
          </div>
          {modulo.examenDesbloqueado ? (
            <Link
              to={`/modulos/${modulo.id}/examen`}
              className="bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-5 py-2.5 rounded-xl transition-colors whitespace-nowrap"
            >
              Presentar →
            </Link>
          ) : (
            <span className="bg-gray-800 text-gray-600 font-semibold px-5 py-2.5 rounded-xl whitespace-nowrap cursor-not-allowed">
              Bloqueado
            </span>
          )}
        </div>
      </div>
    </div>
  )
}
