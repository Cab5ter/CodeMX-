import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import BarraProgreso from '../components/BarraProgreso'
import { getModulos } from '../api/codemx'

export default function Cursos() {
  const [modulos, setModulos] = useState([])
  const [cargando, setCargando] = useState(true)
  const usuarioId = localStorage.getItem('usuarioId')

  useEffect(() => {
    getModulos(usuarioId)
      .then(setModulos)
      .finally(() => setCargando(false))
  }, [usuarioId])

  return (
    <div>
      {/* Hero */}
      <div className="mb-10 py-8 border-b border-gray-800">
        <div className="flex items-center gap-2 mb-3">
          <span className="h-px w-8 bg-emerald-500" />
          <span className="text-emerald-400 text-sm font-medium tracking-wide uppercase">Ruta de aprendizaje</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-bold text-white mb-3 leading-tight">
          Aprende Python<br />
          <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            paso a paso
          </span>
        </h1>
        <p className="text-gray-400 text-lg max-w-xl">
          Cada módulo te enseña la teoría con ejemplos y luego la pones en práctica con ejercicios.
          Completa el 70% de un módulo para desbloquear su examen.
        </p>

        {!usuarioId && (
          <Link to="/registro" className="inline-block mt-5 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-6 py-2.5 rounded-xl transition-colors">
            Crear cuenta para guardar tu progreso →
          </Link>
        )}
      </div>

      {cargando ? (
        <div className="grid gap-4 sm:grid-cols-2">
          {[1, 2, 3].map(i => <div key={i} className="h-44 bg-gray-900 rounded-2xl border border-gray-800 animate-pulse" />)}
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {modulos.map((m, i) => (
            <Link key={m.id} to={`/modulos/${m.id}`} className="group">
              <div className="h-full bg-gray-900 border border-gray-800 rounded-2xl p-6 hover:border-emerald-600/60 transition-all">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <span className="text-3xl">{m.icono}</span>
                    <div>
                      <p className="text-gray-600 text-xs font-mono">Módulo {i + 1}</p>
                      <h3 className="text-white font-semibold group-hover:text-emerald-400 transition-colors">{m.titulo}</h3>
                    </div>
                  </div>
                  {m.examenDesbloqueado && (
                    <span className="text-xs bg-emerald-500/10 text-emerald-400 ring-1 ring-emerald-500/30 px-2 py-1 rounded-full">
                      Examen ✓
                    </span>
                  )}
                </div>

                <p className="text-gray-400 text-sm mb-5 line-clamp-2">{m.descripcion}</p>

                <div className="space-y-2">
                  <div className="flex justify-between text-xs text-gray-500">
                    <span>{m.leccionesCompletadas} / {m.totalLecciones} lecciones</span>
                    <span className="text-emerald-400 font-medium">{m.progreso}%</span>
                  </div>
                  <BarraProgreso progreso={m.progreso} />
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
