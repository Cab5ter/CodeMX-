import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'
import EditorCodigo from '../components/EditorCodigo'
import { crearConexionDuelos } from '../api/duelosHub'

const VEREDICTO = {
  ACEPTADO:               { text: 'text-emerald-400', icono: '✓', label: 'Aceptado' },
  INCORRECTO:             { text: 'text-red-400',     icono: '✗', label: 'Incorrecto' },
  TIEMPO_LIMITE_EXCEDIDO: { text: 'text-amber-400',   icono: '⏱', label: 'Tiempo límite excedido' },
  ERROR_EN_EJECUCION:     { text: 'text-orange-400',  icono: '⚠', label: 'Error en ejecución' },
  TARDE:                  { text: 'text-gray-400',    icono: '⏳', label: '¡Tu rival se adelantó!' },
}

const DIFICULTADES = [
  { id: 'BASICO',     label: 'Fácil',      ganar: 15, perder: 5,  style: 'emerald' },
  { id: 'INTERMEDIO', label: 'Intermedio', ganar: 25, perder: 10, style: 'amber'   },
  { id: 'AVANZADO',   label: 'Difícil',    ganar: 40, perder: 20, style: 'red'     },
]

const ESTILO_DIF = {
  emerald: 'border-emerald-500/50 bg-emerald-500/10 text-emerald-400',
  amber:   'border-amber-400/50  bg-amber-400/10  text-amber-400',
  red:     'border-red-500/50    bg-red-500/10    text-red-400',
}

export default function Versus() {
  const [usuarioId] = useState(() => localStorage.getItem('usuarioId') ?? '')
  const nombre = localStorage.getItem('nombre') || (usuarioId ? `Usuario #${usuarioId}` : 'Invitado')

  const [fase, setFase] = useState('inicio')
  const [dificultad, setDificultad] = useState('INTERMEDIO')
  const [duelo, setDuelo] = useState(null)
  const [rival, setRival] = useState(null)
  const [codigo, setCodigo] = useState('')
  const [miResultado, setMiResultado] = useState(null)
  const [enviando, setEnviando] = useState(false)
  const [resultadoFinal, setResultadoFinal] = useState(null)
  const [error, setError] = useState(null)

  const [mensajes, setMensajes] = useState([])
  const [chatTexto, setChatTexto] = useState('')
  const [rivalEscribiendo, setRivalEscribiendo] = useState(false)

  const conexion = useRef(null)
  const escribiendoTimer = useRef(null)
  const chatFin = useRef(null)

  useEffect(() => () => { conexion.current?.stop() }, [])
  useEffect(() => { chatFin.current?.scrollIntoView({ behavior: 'smooth' }) }, [mensajes, rivalEscribiendo])

  const miId = Number(usuarioId)

  function notaSistema(texto) {
    setMensajes(m => [...m, { sistema: true, texto, ts: Date.now() }])
  }

  async function buscarDuelo() {
    if (!usuarioId) { setError('Necesitas un ID de usuario. Crea tu cuenta primero.'); return }
    setError(null)
    setResultadoFinal(null)
    setMiResultado(null)
    setMensajes([])
    setCodigo('')
    setFase('buscando')

    await conexion.current?.stop()
    const con = crearConexionDuelos()
    conexion.current = con

    con.on('EnEspera', () => setFase('buscando'))

    con.on('DueloIniciado', dto => {
      setDuelo(dto)
      setRival(dto.jugadores.find(j => j.id !== miId) ?? null)
      setFase('enDuelo')
      setMiResultado(null)
      notaSistema('¡Duelo iniciado! El primero en resolver gana. 🏁')
    })

    con.on('ResultadoEnvio', r => { setMiResultado(r); setEnviando(false) })

    con.on('RivalFallo', () => notaSistema('Tu rival envió una solución incorrecta.'))

    con.on('RivalEscribiendo', () => {
      setRivalEscribiendo(true)
      clearTimeout(escribiendoTimer.current)
      escribiendoTimer.current = setTimeout(() => setRivalEscribiendo(false), 1500)
    })

    con.on('MensajeRecibido', msg => {
      setRivalEscribiendo(false)
      setMensajes(m => [...m, { ...msg, mio: msg.usuarioId === miId }])
    })

    con.on('DueloTerminado', res => {
      setResultadoFinal(res)
      setFase('terminado')
      setEnviando(false)
    })

    con.on('DueloNoDisponible', () => { setError('Ese duelo ya no está disponible.'); setFase('inicio') })

    con.onclose(() => { if (fase !== 'terminado') setFase('inicio') })

    try {
      await con.start()
      await con.invoke('BuscarDuelo', miId, nombre, dificultad)
    } catch {
      setError('No se pudo conectar al servidor de duelos.')
      setFase('inicio')
    }
  }

  async function enviarSolucion() {
    if (!codigo.trim() || enviando || !duelo) return
    setEnviando(true)
    setMiResultado(null)
    try {
      await conexion.current.invoke('EnviarSolucion', duelo.dueloId, miId, codigo)
    } catch {
      setEnviando(false)
      setError('No se pudo enviar la solución.')
    }
  }

  async function enviarMensaje(e) {
    e.preventDefault()
    const texto = chatTexto.trim()
    if (!texto || !duelo) return
    setChatTexto('')
    try { await conexion.current.invoke('EnviarMensaje', duelo.dueloId, miId, texto) } catch {  }
  }

  function alEscribir(e) {
    setChatTexto(e.target.value)
    if (duelo) conexion.current?.invoke('Escribiendo', duelo.dueloId, miId).catch(() => {})
  }

  function reiniciar() {
    conexion.current?.stop()
    setFase('inicio'); setDuelo(null); setRival(null); setResultadoFinal(null); setMiResultado(null)
  }


  if (fase === 'inicio' || fase === 'buscando') {
    return (
      <div className="max-w-xl mx-auto text-center pt-6">
        <div className="text-6xl mb-4">⚔️</div>
        <h1 className="text-3xl font-bold text-white mb-2">
          Modo <span className="bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">1 vs 1</span>
        </h1>
        <p className="text-gray-400 mb-8">
          Te enfrentas a otra persona con un problema de programación generado al momento.
          El primero en resolverlo correctamente <b className="text-emerald-400">gana puntos</b> y sube
          en el ranking; el otro <b className="text-red-400">pierde puntos</b>. Tú eliges la dificultad.
        </p>

        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 mb-6 text-left">
          <div className="flex items-center justify-between">
            <span className="text-gray-500 text-sm">Jugando como</span>
            <span className="font-mono text-emerald-400">{nombre}</span>
          </div>
        </div>

        {fase === 'inicio' && usuarioId && (
          <div className="mb-6 text-left">
            <p className="text-gray-500 text-xs uppercase tracking-wider mb-2">Elige la dificultad</p>
            <div className="grid grid-cols-3 gap-3">
              {DIFICULTADES.map(d => {
                const activa = dificultad === d.id
                return (
                  <button
                    key={d.id}
                    onClick={() => setDificultad(d.id)}
                    className={`rounded-xl border p-3 transition-all ${activa ? ESTILO_DIF[d.style] : 'border-gray-800 bg-gray-900 text-gray-400 hover:border-gray-700'}`}
                  >
                    <div className="font-semibold text-sm">{d.label}</div>
                    <div className="text-xs mt-1 text-emerald-400">+{d.ganar} pts</div>
                    <div className="text-xs text-red-400">−{d.perder} pts</div>
                  </button>
                )
              })}
            </div>
            <p className="text-gray-600 text-xs mt-2">Te emparejas con alguien que eligió la misma dificultad.</p>
          </div>
        )}

        {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

        {fase === 'buscando' ? (
          <div className="flex flex-col items-center gap-4">
            <div className="flex items-center gap-3 text-amber-400">
              <span className="w-5 h-5 border-2 border-amber-400/30 border-t-amber-400 rounded-full animate-spin" />
              Buscando rival…
            </div>
            <button onClick={reiniciar} className="text-gray-500 hover:text-gray-300 text-sm">Cancelar</button>
          </div>
        ) : !usuarioId ? (
          <Link to="/registro" className="inline-block bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-8 py-3 rounded-xl transition-colors">
            Crear cuenta para jugar →
          </Link>
        ) : (
          <button onClick={buscarDuelo} className="bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-10 py-3 rounded-xl transition-colors">
            Buscar duelo
          </button>
        )}
        <p className="text-gray-600 text-xs mt-6">
          Tip: abre esta página en dos pestañas (o dos máquinas) con cuentas distintas para probar el emparejamiento.
        </p>
      </div>
    )
  }

  const gane = resultadoFinal && resultadoFinal.ganadorId === miId
  const v = miResultado ? VEREDICTO[miResultado.veredicto] : null

  return (
    <div className="grid lg:grid-cols-2 gap-6 items-start">
      <div className="space-y-5">
        <div className="flex items-center justify-between bg-gray-900 border border-gray-800 rounded-xl px-4 py-3">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-400" />
            <span className="text-sm text-gray-300 font-mono">{nombre}</span>
          </div>
          <span className="text-gray-600 text-xs">VS</span>
          <div className="flex items-center gap-2">
            <span className="text-sm text-gray-300 font-mono">{rival?.nombre ?? 'Rival'}</span>
            <span className="w-2 h-2 rounded-full bg-red-400" />
          </div>
        </div>

        <div>
          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs px-2.5 py-1 rounded-full bg-cyan-500/10 text-cyan-400 ring-1 ring-cyan-500/30">Duelo en curso</span>
            {duelo?.dificultad && (
              <span className="text-xs px-2.5 py-1 rounded-full ring-1 ring-gray-700 text-gray-300">
                {DIFICULTADES.find(d => d.id === duelo.dificultad)?.label ?? duelo.dificultad}
              </span>
            )}
            <span className="text-xs text-gray-500">
              En juego: <b className="text-emerald-400">+{duelo?.puntosGanar}</b> / <b className="text-red-400">−{duelo?.puntosPerder}</b>
            </span>
          </div>
          <h1 className="text-2xl font-bold text-white mt-3">{duelo?.titulo}</h1>
        </div>

        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-xs text-gray-500 uppercase tracking-wider font-medium mb-2">Enunciado</h2>
          <p className="text-gray-300 text-sm leading-relaxed whitespace-pre-line">{duelo?.enunciado}</p>
        </div>

        {(duelo?.ejemploEntrada || duelo?.ejemploSalida) && (
          <div className="bg-gray-900 border border-gray-800 rounded-xl p-5 grid grid-cols-2 gap-3">
            <div>
              <p className="text-xs text-gray-500 uppercase tracking-wider mb-1.5">Entrada</p>
              <pre className="bg-gray-950 border border-gray-800 rounded-lg px-3 py-2 text-sm text-gray-300 font-mono overflow-x-auto whitespace-pre-wrap">{duelo.ejemploEntrada}</pre>
            </div>
            <div>
              <p className="text-xs text-gray-500 uppercase tracking-wider mb-1.5">Salida esperada</p>
              <pre className="bg-gray-950 border border-gray-800 rounded-lg px-3 py-2 text-sm text-gray-300 font-mono overflow-x-auto whitespace-pre-wrap">{duelo.ejemploSalida}</pre>
            </div>
          </div>
        )}

        <div className="bg-gray-900 border border-gray-800 rounded-xl flex flex-col h-64">
          <div className="px-4 py-2.5 border-b border-gray-800 text-xs text-gray-500 uppercase tracking-wider">Chat</div>
          <div className="flex-1 overflow-y-auto px-4 py-3 space-y-2">
            {mensajes.map((m, i) =>
              m.sistema ? (
                <p key={i} className="text-center text-xs text-gray-600 italic">{m.texto}</p>
              ) : (
                <div key={i} className={`flex ${m.mio ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-[80%] rounded-2xl px-3 py-1.5 text-sm ${m.mio ? 'bg-emerald-600/80 text-white' : 'bg-gray-800 text-gray-200'}`}>
                    {!m.mio && <span className="block text-[10px] text-gray-400 font-mono">{m.nombre}</span>}
                    {m.texto}
                  </div>
                </div>
              )
            )}
            {rivalEscribiendo && <p className="text-xs text-gray-500 italic">{rival?.nombre} está escribiendo…</p>}
            <div ref={chatFin} />
          </div>
          <form onSubmit={enviarMensaje} className="flex gap-2 p-3 border-t border-gray-800">
            <input
              value={chatTexto}
              onChange={alEscribir}
              disabled={fase === 'terminado'}
              placeholder="Escribe un mensaje…"
              className="flex-1 bg-gray-950 border border-gray-800 focus:border-emerald-500 rounded-lg px-3 py-2 text-sm text-gray-200 outline-none transition-colors"
            />
            <button type="submit" className="bg-emerald-600 hover:bg-emerald-500 text-white text-sm px-4 rounded-lg transition-colors">Enviar</button>
          </form>
        </div>
      </div>

      <div className="space-y-3">
        <EditorCodigo value={codigo} onChange={setCodigo} />

        {v && (
          <div className="rounded-xl border border-gray-800 bg-gray-900 p-4">
            <div className="flex items-center gap-2">
              <span className={`text-xl ${v.text}`}>{v.icono}</span>
              <span className={`font-semibold ${v.text}`}>{v.label}</span>
            </div>
            {miResultado.mensajeError && (
              <p className="text-orange-400/70 text-xs mt-1.5 font-mono">{miResultado.mensajeError}</p>
            )}
          </div>
        )}

        {fase !== 'terminado' ? (
          <button
            onClick={enviarSolucion}
            disabled={enviando}
            className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:bg-gray-800 disabled:text-gray-600 text-white font-semibold py-3 rounded-xl transition-colors"
          >
            {enviando ? (
              <span className="flex items-center justify-center gap-2">
                <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Evaluando…
              </span>
            ) : 'Enviar solución'}
          </button>
        ) : (
          <div className={`rounded-2xl border p-6 text-center ${gane ? 'bg-emerald-500/10 border-emerald-500/40' : 'bg-red-500/10 border-red-500/40'}`}>
            <div className="text-5xl mb-2">{gane ? '🏆' : '💪'}</div>
            <h2 className={`text-xl font-bold ${gane ? 'text-emerald-400' : 'text-red-400'}`}>
              {gane ? '¡Ganaste el duelo!' : `Ganó ${resultadoFinal?.ganadorNombre}`}
            </h2>
            <p className="text-gray-400 text-sm mt-1">
              {resultadoFinal?.motivo === 'abandono'
                ? (gane ? 'Tu rival abandonó.' : 'Abandonaste el duelo.')
                : (gane ? `Resolviste primero. +${duelo?.puntosGanar} puntos en el ranking.` : `Tu rival resolvió primero. −${duelo?.puntosPerder} puntos.`)}
            </p>
            <div className="flex gap-3 justify-center mt-5">
              <button onClick={buscarDuelo} className="bg-emerald-600 hover:bg-emerald-500 text-white font-semibold px-6 py-2.5 rounded-xl transition-colors">Otro duelo</button>
              <Link to="/ranking" className="bg-gray-800 hover:bg-gray-700 text-gray-200 font-semibold px-6 py-2.5 rounded-xl transition-colors">Ver ranking</Link>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
