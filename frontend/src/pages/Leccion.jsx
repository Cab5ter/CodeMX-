import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { getLeccion, completarLeccion } from '../api/codemx'

export default function Leccion() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [leccion, setLeccion] = useState(null)
  const [completada, setCompletada] = useState(false)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState(null)
  const usuarioId = localStorage.getItem('usuarioId')

  useEffect(() => {
    getLeccion(id, usuarioId).then(l => {
      setLeccion(l)
      setCompletada(l.completada)
    })
  }, [id, usuarioId])

  async function handleCompletar() {
    if (!usuarioId) { setError('Necesitas una cuenta para guardar tu progreso'); return }
    setGuardando(true)
    setError(null)
    try {
      await completarLeccion(id, usuarioId)
      setCompletada(true)
      setTimeout(() => navigate(`/modulos/${leccion.moduloId}`), 600)
    } catch (e) {
      setError(e.message)
    } finally {
      setGuardando(false)
    }
  }

  if (!leccion) return (
    <div className="max-w-2xl mx-auto space-y-4">
      <div className="h-6 bg-gray-900 rounded animate-pulse w-32" />
      <div className="h-8 bg-gray-900 rounded animate-pulse w-2/3" />
      <div className="h-64 bg-gray-900 rounded-xl animate-pulse" />
    </div>
  )

  return (
    <div className="max-w-2xl mx-auto">
      <Link to={`/modulos/${leccion.moduloId}`} className="inline-flex items-center gap-1.5 text-gray-500 hover:text-gray-300 text-sm mb-6 transition-colors">
        ← {leccion.tituloModulo}
      </Link>

      <div className="flex items-center gap-2 mb-2">
        <span className="text-cyan-400 text-xs uppercase tracking-wider font-medium">📖 Teoría</span>
      </div>
      <h1 className="text-2xl font-bold text-white mb-6">{leccion.titulo}</h1>

      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 mb-4">
        <p className="text-gray-300 leading-relaxed whitespace-pre-line">{leccion.contenido}</p>
      </div>

      {leccion.ejemploCodigo && (
        <div className="rounded-xl overflow-hidden border border-gray-800 mb-6">
          <div className="bg-gray-900 border-b border-gray-800 px-4 py-2 flex items-center gap-2">
            <span className="text-emerald-400 text-xs font-medium">Ejemplo</span>
            <span className="text-gray-600 text-xs font-mono ml-auto">Python</span>
          </div>
          <pre className="bg-gray-950 text-gray-200 font-mono text-sm p-5 overflow-x-auto leading-relaxed">
{leccion.ejemploCodigo}
          </pre>
        </div>
      )}

      {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

      {completada ? (
        <div className="flex items-center justify-center gap-2 bg-emerald-500/10 border border-emerald-500/40 text-emerald-400 font-semibold py-3 rounded-xl">
          ✓ Lección completada
        </div>
      ) : (
        <button
          onClick={handleCompletar}
          disabled={guardando}
          className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-800 disabled:text-gray-600 text-white font-semibold py-3 rounded-xl transition-colors"
        >
          {guardando ? 'Guardando...' : 'Marcar como completada'}
        </button>
      )}
    </div>
  )
}
