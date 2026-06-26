import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import EditorCodigo from '../components/EditorCodigo'
import { getRetoById, enviarSolucion } from '../api/codemx'

const DIFICULTAD_BADGE = {
  BASICO:     'bg-emerald-900 text-emerald-300',
  INTERMEDIO: 'bg-yellow-900 text-yellow-300',
  AVANZADO:   'bg-red-900 text-red-300'
}
const DIFICULTAD_LABEL = { BASICO: 'Básico', INTERMEDIO: 'Intermedio', AVANZADO: 'Avanzado' }

const VEREDICTO = {
  ACEPTADO:               { color: 'bg-emerald-900 border-emerald-600 text-emerald-300', icono: '✓', texto: 'Aceptado' },
  INCORRECTO:             { color: 'bg-red-900 border-red-600 text-red-300',             icono: '✗', texto: 'Incorrecto' },
  TIEMPO_LIMITE_EXCEDIDO: { color: 'bg-yellow-900 border-yellow-600 text-yellow-300',   icono: '⏱', texto: 'Tiempo límite excedido' },
  ERROR_EN_EJECUCION:     { color: 'bg-orange-900 border-orange-600 text-orange-300',   icono: '⚠', texto: 'Error en ejecución' },
  PENDIENTE:              { color: 'bg-gray-800 border-gray-600 text-gray-300',          icono: '⏳', texto: 'Pendiente' }
}

export default function DetalleReto() {
  const { id } = useParams()
  const [reto, setReto] = useState(null)
  const [codigo, setCodigo] = useState('')
  const [usuarioId, setUsuarioId] = useState(localStorage.getItem('usuarioId') ?? '')
  const [resultado, setResultado] = useState(null)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    getRetoById(id)
      .then(setReto)
      .catch(e => setError(e.message))
  }, [id])

  async function handleEnviar() {
    if (!usuarioId) { setError('Ingresa tu ID de usuario'); return }
    if (!codigo.trim()) { setError('El código no puede estar vacío'); return }
    setError(null)
    setResultado(null)
    setEnviando(true)
    try {
      const res = await enviarSolucion({
        usuarioId: Number(usuarioId),
        retoId: Number(id),
        codigoFuente: codigo
      })
      setResultado(res)
      localStorage.setItem('usuarioId', usuarioId)
    } catch (e) {
      setError(e.message)
    } finally {
      setEnviando(false)
    }
  }

  if (error && !reto) return (
    <div className="text-center py-16">
      <p className="text-red-400 mb-4">{error}</p>
      <Link to="/" className="text-emerald-400 hover:underline">← Volver a retos</Link>
    </div>
  )

  if (!reto) return (
    <div className="grid lg:grid-cols-2 gap-6">
      <div className="space-y-4">
        <div className="h-8 bg-gray-800 rounded animate-pulse w-1/3" />
        <div className="h-6 bg-gray-800 rounded animate-pulse w-2/3" />
        <div className="h-40 bg-gray-800 rounded animate-pulse" />
      </div>
      <div className="h-96 bg-gray-800 rounded animate-pulse" />
    </div>
  )

  const v = resultado ? VEREDICTO[resultado.veredicto] : null

  return (
    <div>
      <Link to="/" className="text-gray-500 hover:text-gray-300 text-sm mb-6 inline-block">
        ← Volver a retos
      </Link>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Descripción */}
        <div>
          <div className="flex items-center gap-3 mb-3">
            <span className="text-gray-500 font-mono text-sm">#{reto.id}</span>
            <span className={`text-xs px-2 py-1 rounded font-medium ${DIFICULTAD_BADGE[reto.dificultad]}`}>
              {DIFICULTAD_LABEL[reto.dificultad]}
            </span>
          </div>
          <h1 className="text-2xl font-bold text-gray-100 mb-4">{reto.titulo}</h1>
          <div className="bg-gray-800 rounded-lg p-5 border border-gray-700">
            <p className="text-gray-300 whitespace-pre-line leading-relaxed text-sm">{reto.descripcion}</p>
          </div>
        </div>

        {/* Editor + envío */}
        <div className="flex flex-col gap-4">
          <div className="flex items-center gap-3">
            <label className="text-gray-400 text-sm whitespace-nowrap">Tu ID de usuario:</label>
            <input
              type="number"
              value={usuarioId}
              onChange={e => setUsuarioId(e.target.value)}
              className="bg-gray-800 border border-gray-700 rounded px-3 py-1.5 text-gray-200 text-sm w-24 outline-none focus:border-emerald-500"
              placeholder="1"
            />
            {!usuarioId && (
              <Link to="/registro" className="text-emerald-400 text-xs hover:underline">
                Crear cuenta →
              </Link>
            )}
          </div>

          <EditorCodigo value={codigo} onChange={setCodigo} />

          {error && <p className="text-red-400 text-sm">{error}</p>}

          {v && (
            <div className={`rounded-lg border p-4 ${v.color}`}>
              <p className="font-semibold">{v.icono} {v.texto}</p>
              {resultado.veredicto === 'ACEPTADO' && (
                <p className="text-sm mt-1 opacity-75">
                  ¡Solución correcta! Los puntos se sumaron a tu posición en el ranking.
                </p>
              )}
              {resultado.veredicto === 'INCORRECTO' && (
                <p className="text-sm mt-1 opacity-75">
                  Tu solución no pasó todos los casos de prueba. Intenta de nuevo.
                </p>
              )}
            </div>
          )}

          <button
            onClick={handleEnviar}
            disabled={enviando}
            className="bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-700 disabled:text-gray-500 text-white font-semibold py-2.5 rounded-lg transition-colors"
          >
            {enviando ? 'Evaluando...' : 'Enviar solución'}
          </button>
        </div>
      </div>
    </div>
  )
}
