const POSICION_STYLE = ['text-yellow-400', 'text-gray-300', 'text-amber-600']

export default function TablaPosiciones({ entradas }) {
  if (entradas.length === 0) {
    return (
      <p className="text-gray-500 text-center py-12">
        Aún no hay puntuaciones. ¡Sé el primero en resolver un reto!
      </p>
    )
  }

  return (
    <div className="overflow-hidden rounded-lg border border-gray-700">
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-800 text-gray-400 text-left text-xs uppercase tracking-wider">
            <th className="px-4 py-3 w-12">#</th>
            <th className="px-4 py-3">Usuario</th>
            <th className="px-4 py-3 text-right">Retos resueltos</th>
            <th className="px-4 py-3 text-right">Puntos</th>
          </tr>
        </thead>
        <tbody>
          {entradas.map((entrada, i) => (
            <tr key={entrada.id} className="border-t border-gray-700 bg-gray-800 hover:bg-gray-700 transition-colors">
              <td className={`px-4 py-3 font-bold ${POSICION_STYLE[i] ?? 'text-gray-400'}`}>
                {i + 1}
              </td>
              <td className="px-4 py-3 text-gray-200 font-mono">
                Usuario #{entrada.usuarioId}
              </td>
              <td className="px-4 py-3 text-right text-gray-400">
                {entrada.retosResueltos}
              </td>
              <td className="px-4 py-3 text-right font-bold text-emerald-400">
                {entrada.puntajeTotal} pts
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
