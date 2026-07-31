import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import EditorCodigo from '../components/EditorCodigo'
import { getRetoById, getEjemploReto, enviarSolucion } from '../api/codemx'

const DIFFICULTY = {
  BASICO:     { label: 'Básico',     puntos: 10, style: 'bg-emerald-500/10 text-emerald-400 ring-1 ring-emerald-500/30' },
  INTERMEDIO: { label: 'Intermedio', puntos: 25, style: 'bg-amber-500/10  text-amber-400  ring-1 ring-amber-400/30'  },
  AVANZADO:   { label: 'Avanzado',   puntos: 50, style: 'bg-red-500/10    text-red-400    ring-1 ring-red-500/30'    },
}

const VEREDICTO = {
  ACEPTADO:               { bg: 'bg-emerald-500/10 border-emerald-500/40', text: 'text-emerald-400', icono: '✓', label: 'Aceptado' },
  INCORRECTO:             { bg: 'bg-red-500/10     border-red-500/40',     text: 'text-red-400',     icono: '✗', label: 'Incorrecto' },
  TIEMPO_LIMITE_EXCEDIDO: { bg: 'bg-amber-500/10  border-amber-500/40',   text: 'text-amber-400',   icono: '⏱', label: 'Tiempo límite excedido' },
  ERROR_EN_EJECUCION:     { bg: 'bg-orange-500/10 border-orange-500/40',  text: 'text-orange-400',  icono: '⚠', label: 'Error en ejecución' },
  PENDIENTE:              { bg: 'bg-gray-800       border-gray-700',       text: 'text-gray-400',    icono: '⏳', label: 'Pendiente' },
}

function BloqueCodigo({ titulo, contenido }) {
  return (
    <div>
      <p className="text-xs text-gray-500 uppercase tracking-wider mb-1.5 font-medium">{titulo}</p>
      <pre className="bg-gray-950 border border-gray-800 rounded-lg px-4 py-3 text-sm text-gray-300 font-mono overflow-x-auto whitespace-pre-wrap">
        {contenido}
      </pre>
    </div>
  )
}

export default function DetalleReto() {
  const { id } = useParams()
  const [reto, setReto] = useState(null)
  const [ejemplo, setEjemplo] = useState(null)
  const [codigo, setCodigo] = useState('')
  const [usuarioId, setUsuarioId] = useState(localStorage.getItem('usuarioId') ?? '')
  const [resultado, setResultado] = useState(null)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    Promise.all([getRetoById(id), getEjemploReto(id)])
      .then(([r, e]) => { setReto(r); setEjemplo(e) })
      .catch(e => setError(e.message))
  }, [id])

  async function handleEnviar() {
    if (!usuarioId) { setError('Ingresa tu ID de usuario'); return }
    if (!codigo.trim()) { setError('El código no puede estar vacío'); return }
    setError(null)
    setResultado(null)
    setEnviando(true)
    try {
      const res = await enviarSolucion({ usuarioId: Number(usuarioId), retoId: Number(id), codigoFuente: codigo })
      setResultado(res)
      localStorage.setItem('usuarioId', usuarioId)
    } catch (e) {
      setError(e.message)
    } finally {
      setEnviando(false)
    }
  }

  if (!reto) return (
    <div className="space-y-4">
      <div className="h-6 bg-gray-900 rounded animate-pulse w-32" />
      <div className="h-10 bg-gray-900 rounded animate-pulse w-2/3" />
      <div className="grid lg:grid-cols-2 gap-4 sm:gap-6 mt-6">
        <div className="h-80 bg-gray-900 rounded-xl animate-pulse" />
        <div className="h-80 bg-gray-900 rounded-xl animate-pulse" />
      </div>
    </div>
  )

  const d = DIFFICULTY[reto.dificultad]
  const v = resultado ? VEREDICTO[resultado.veredicto] : null

  return (
    <div>
      <Link to="/" className="inline-flex items-center gap-1.5 text-gray-500 hover:text-gray-300 text-sm mb-6 transition-colors">
        ← Volver a retos
      </Link>

      <div className="grid lg:grid-cols-2 gap-4 sm:gap-6 items-start">

        {/* Panel izquierdo: enunciado */}
        <div className="space-y-5">
          <div>
            <div className="flex items-center gap-3 mb-3">
              <span className="text-gray-600 font-mono text-sm">#{String(reto.id).padStart(2, '0')}</span>
              <span className={`text-xs px-2.5 py-1 rounded-full font-medium ${d.style}`}>
                {d.label}
              </span>
              <span className="text-emerald-400 text-xs font-semibold">+{d.puntos} pts al resolver</span>
            </div>
            <h1 className="text-2xl font-bold text-white">{reto.titulo}</h1>
          </div>

          <div className="bg-gray-900 border border-gray-800 rounded-xl p-5 space-y-3">
            <h2 className="text-xs text-gray-500 uppercase tracking-wider font-medium">Descripción</h2>
            <p className="text-gray-300 text-sm leading-relaxed whitespace-pre-line">{reto.descripcion}</p>
          </div>

          {ejemplo && (
            <div className="bg-gray-900 border border-gray-800 rounded-xl p-5 space-y-4">
              <h2 className="text-xs text-gray-500 uppercase tracking-wider font-medium">Ejemplo</h2>
              <div className="grid grid-cols-1 xs:grid-cols-2 gap-3">
                <BloqueCodigo titulo="Entrada" contenido={ejemplo.inputData} />
                <BloqueCodigo titulo="Salida esperada" contenido={ejemplo.outputEsperado} />
              </div>
            </div>
          )}
        </div>

        {/* Panel derecho: editor */}
        <div className="space-y-3">
          <div className="flex items-center gap-3 bg-gray-900 border border-gray-800 rounded-xl px-4 py-3">
            <span className="text-gray-500 text-sm">ID de usuario:</span>
            <input
              type="number"
              value={usuarioId}
              onChange={e => setUsuarioId(e.target.value)}
              className="bg-transparent border-b border-gray-700 focus:border-emerald-500 text-gray-200 text-sm w-20 outline-none pb-0.5 transition-colors font-mono"
              placeholder="1"
            />
            {!usuarioId && (
              <Link to="/registro" className="ml-auto text-emerald-400 text-xs hover:underline">
                Crear cuenta →
              </Link>
            )}
          </div>

          <EditorCodigo value={codigo} onChange={setCodigo} />

          {error && (
            <p className="text-red-400 text-sm px-1">{error}</p>
          )}

          {v && (
            <div className={`rounded-xl border p-4 ${v.bg}`}>
              <div className="flex items-center gap-2">
                <span className={`text-xl ${v.text}`}>{v.icono}</span>
                <span className={`font-semibold ${v.text}`}>{v.label}</span>
              </div>
              {resultado.veredicto === 'ACEPTADO' && (
                <p className="text-emerald-500/70 text-sm mt-1.5">
                  ¡Solución correcta! Los puntos fueron sumados a tu posición en el ranking.
                </p>
              )}
              {resultado.veredicto === 'INCORRECTO' && (
                <p className="text-red-400/70 text-sm mt-1.5">
                  Tu solución no produjo el resultado esperado. Revisa la lógica e intenta de nuevo.
                </p>
              )}
              {resultado.veredicto === 'ERROR_EN_EJECUCION' && resultado.mensajeError && (
                <p className="text-orange-400/70 text-xs mt-1.5 font-mono">{resultado.mensajeError}</p>
              )}
            </div>
          )}

          <button
            onClick={handleEnviar}
            disabled={enviando}
            className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-800 disabled:text-gray-600 text-white font-semibold py-3 rounded-xl transition-colors"
          >
            {enviando ? (
              <span className="flex items-center justify-center gap-2">
                <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Evaluando...
              </span>
            ) : 'Enviar solución'}
          </button>
        </div>
      </div>
    </div>
  )
}
