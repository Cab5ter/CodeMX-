import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getExamen, enviarExamen } from '../api/codemx'

const LETRAS = ['A', 'B', 'C', 'D']

export default function Examen() {
  const { id } = useParams()
  const usuarioId = localStorage.getItem('usuarioId')

  const [preguntas, setPreguntas] = useState(null)
  const [respuestas, setRespuestas] = useState({})
  const [resultado, setResultado] = useState(null)
  const [bloqueado, setBloqueado] = useState(false)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    getExamen(id, usuarioId)
      .then(setPreguntas)
      .catch(e => {
        if (e.message === 'BLOQUEADO') setBloqueado(true)
        else setError(e.message)
      })
  }, [id, usuarioId])

  function elegir(preguntaId, indice) {
    setRespuestas(r => ({ ...r, [preguntaId]: indice }))
  }

  async function handleEnviar() {
    setEnviando(true)
    setError(null)
    try {
      const res = await enviarExamen(id, usuarioId, respuestas)
      setResultado(res)
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } catch (e) {
      setError(e.message)
    } finally {
      setEnviando(false)
    }
  }

  if (bloqueado) return (
    <div className="max-w-lg mx-auto text-center pt-12">
      <div className="text-6xl mb-4">🔒</div>
      <h1 className="text-2xl font-bold text-white mb-2">Examen bloqueado</h1>
      <p className="text-gray-400 mb-6">
        Necesitas completar al menos el 70% de las lecciones del módulo antes de presentar el examen.
      </p>
      <Link to={`/modulos/${id}`} className="text-emerald-400 hover:underline">← Volver al módulo</Link>
    </div>
  )

  if (!preguntas) return (
    <div className="max-w-2xl mx-auto space-y-4">
      <div className="h-8 bg-gray-900 rounded animate-pulse w-1/2" />
      {[1, 2, 3].map(i => <div key={i} className="h-40 bg-gray-900 rounded-xl animate-pulse" />)}
    </div>
  )

  if (resultado) {
    return (
      <div className="max-w-lg mx-auto text-center pt-10">
        <div className="text-6xl mb-4">{resultado.aprobado ? '🎉' : '😕'}</div>
        <h1 className="text-2xl font-bold text-white mb-2">
          {resultado.aprobado ? '¡Aprobaste!' : 'No alcanzó esta vez'}
        </h1>
        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 my-6">
          <p className={`text-5xl font-bold ${resultado.aprobado ? 'text-emerald-400' : 'text-amber-400'}`}>
            {resultado.porcentaje}%
          </p>
          <p className="text-gray-400 mt-2">{resultado.aciertos} de {resultado.total} respuestas correctas</p>
          <p className="text-gray-600 text-xs mt-1">Mínimo para aprobar: 70%</p>
        </div>
        <div className="flex gap-3 justify-center">
          {!resultado.aprobado && (
            <button
              onClick={() => { setResultado(null); setRespuestas({}) }}
              className="bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-6 py-2.5 rounded-xl transition-colors"
            >
              Reintentar
            </button>
          )}
          <Link to={`/modulos/${id}`} className="bg-gray-800 hover:bg-gray-700 text-gray-200 font-semibold px-6 py-2.5 rounded-xl transition-colors">
            Volver al módulo
          </Link>
        </div>
      </div>
    )
  }

  const todasContestadas = preguntas.length > 0 && preguntas.every(p => respuestas[p.id] !== undefined)

  return (
    <div className="max-w-2xl mx-auto">
      <Link to={`/modulos/${id}`} className="inline-flex items-center gap-1.5 text-gray-500 hover:text-gray-300 text-sm mb-6 transition-colors">
        ← Volver al módulo
      </Link>

      <h1 className="text-2xl font-bold text-white mb-1">Examen del módulo</h1>
      <p className="text-gray-500 text-sm mb-8">Responde todas las preguntas. Necesitas 70% para aprobar.</p>

      <div className="space-y-5">
        {preguntas.map((p, i) => (
          <div key={p.id} className="bg-gray-900 border border-gray-800 rounded-xl p-5">
            <p className="text-gray-100 font-medium mb-4">
              <span className="text-gray-600 mr-2">{i + 1}.</span>{p.enunciado}
            </p>
            <div className="space-y-2">
              {p.opciones.map((op, idx) => {
                const elegida = respuestas[p.id] === idx
                return (
                  <button
                    key={idx}
                    onClick={() => elegir(p.id, idx)}
                    className={`w-full text-left flex items-center gap-3 px-4 py-2.5 rounded-lg border transition-all ${
                      elegida
                        ? 'bg-emerald-500/10 border-emerald-500/50 text-emerald-300'
                        : 'bg-gray-950 border-gray-800 text-gray-300 hover:border-gray-700'
                    }`}
                  >
                    <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 ${
                      elegida ? 'bg-emerald-500 text-gray-950' : 'bg-gray-800 text-gray-500'
                    }`}>
                      {LETRAS[idx]}
                    </span>
                    <span className="text-sm font-mono">{op}</span>
                  </button>
                )
              })}
            </div>
          </div>
        ))}
      </div>

      {error && <p className="text-red-400 text-sm mt-4">{error}</p>}

      <button
        onClick={handleEnviar}
        disabled={!todasContestadas || enviando}
        className="w-full mt-6 bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-800 disabled:text-gray-600 text-white font-semibold py-3 rounded-xl transition-colors"
      >
        {enviando ? 'Calificando...' : todasContestadas ? 'Entregar examen' : 'Responde todas las preguntas'}
      </button>
    </div>
  )
}
