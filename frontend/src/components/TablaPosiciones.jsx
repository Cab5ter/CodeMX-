const MEDALS = ['🥇', '🥈', '🥉']
const MEDAL_STYLE = [
  'bg-gradient-to-br from-yellow-900/40 to-yellow-800/20 border-yellow-700/50 text-yellow-300',
  'bg-gradient-to-br from-gray-800/60 to-gray-700/20 border-gray-600/50 text-gray-300',
  'bg-gradient-to-br from-orange-900/40 to-orange-800/20 border-orange-700/50 text-orange-300',
]

export default function TablaPosiciones({ entradas }) {
  if (entradas.length === 0) {
    return (
      <div className="text-center py-20">
        <p className="text-5xl mb-4">🏆</p>
        <p className="text-gray-400 text-lg font-medium">El ranking está vacío</p>
        <p className="text-gray-600 text-sm mt-1">¡Sé el primero en resolver un reto!</p>
      </div>
    )
  }

  const top3 = entradas.slice(0, 3)
  const resto = entradas.slice(3)

  return (
    <div className="space-y-6">
      {/* Podio */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        {top3.map((entrada, i) => (
          <div key={entrada.id} className={`rounded-xl border p-5 text-center ${MEDAL_STYLE[i]}`}>
            <div className="text-3xl mb-2">{MEDALS[i]}</div>
            <div className="font-mono font-bold text-lg">#{entrada.usuarioId}</div>
            <div className="text-2xl font-bold mt-1">{entrada.puntajeTotal} pts</div>
            <div className="text-xs mt-1 opacity-60">{entrada.retosResueltos} retos resueltos</div>
          </div>
        ))}
      </div>

      {/* Resto */}
      {resto.length > 0 && (
        <div className="rounded-xl border border-gray-800 overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-900 text-gray-500 text-xs uppercase tracking-wider text-left">
                <th className="px-5 py-3 w-12">#</th>
                <th className="px-5 py-3">Usuario</th>
                <th className="px-5 py-3 text-right">Retos</th>
                <th className="px-5 py-3 text-right">Puntos</th>
              </tr>
            </thead>
            <tbody>
              {resto.map((entrada, i) => (
                <tr key={entrada.id} className="border-t border-gray-800 bg-gray-900/50 hover:bg-gray-800/40 transition-colors">
                  <td className="px-5 py-3 text-gray-500 font-mono text-xs">{i + 4}</td>
                  <td className="px-5 py-3 text-gray-300 font-mono">Usuario #{entrada.usuarioId}</td>
                  <td className="px-5 py-3 text-right text-gray-500">{entrada.retosResueltos}</td>
                  <td className="px-5 py-3 text-right font-bold text-emerald-400">{entrada.puntajeTotal} pts</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
