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
      {/* Podio: en móvil una fila por lugar; desde sm, tres columnas */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        {top3.map((entrada, i) => (
          <div
            key={entrada.id}
            className={`rounded-xl border p-4 sm:p-5 flex items-center gap-3 sm:block sm:text-center ${MEDAL_STYLE[i]}`}
          >
            <div className="text-2xl sm:text-3xl sm:mb-2 shrink-0">{MEDALS[i]}</div>
            <div className="min-w-0 flex-1 sm:flex-none">
              <div className="font-bold text-base sm:text-lg truncate" title={entrada.nombre}>{entrada.nombre}</div>
              <div className="text-xs mt-0.5 sm:mt-1 opacity-60">{entrada.retosResueltos} retos resueltos</div>
            </div>
            <div className="text-xl sm:text-2xl font-bold sm:mt-1 shrink-0">{entrada.puntajeTotal} pts</div>
          </div>
        ))}
      </div>

      {/* Resto: la tabla se desplaza dentro de su contenedor y nunca ensancha la página */}
      {resto.length > 0 && (
        <div className="rounded-xl border border-gray-800 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm min-w-[22rem]">
              <thead>
                <tr className="bg-gray-900 text-gray-500 text-xs uppercase tracking-wider text-left">
                  <th className="px-3 sm:px-5 py-3 w-10">#</th>
                  <th className="px-3 sm:px-5 py-3">Usuario</th>
                  <th className="px-3 sm:px-5 py-3 text-right">Retos</th>
                  <th className="px-3 sm:px-5 py-3 text-right">Puntos</th>
                </tr>
              </thead>
              <tbody>
                {resto.map((entrada, i) => (
                  <tr key={entrada.id} className="border-t border-gray-800 bg-gray-900/50 hover:bg-gray-800/40 transition-colors">
                    <td className="px-3 sm:px-5 py-3 text-gray-500 font-mono text-xs">{i + 4}</td>
                    <td className="px-3 sm:px-5 py-3 text-gray-200 max-w-[10rem] truncate" title={entrada.nombre}>{entrada.nombre}</td>
                    <td className="px-3 sm:px-5 py-3 text-right text-gray-500">{entrada.retosResueltos}</td>
                    <td className="px-3 sm:px-5 py-3 text-right font-bold text-emerald-400 whitespace-nowrap">{entrada.puntajeTotal} pts</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
