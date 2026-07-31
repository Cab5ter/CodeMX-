export default function EditorCodigo({ value, onChange }) {
  function handleKeyDown(e) {
    if (e.key === 'Tab') {
      e.preventDefault()
      const start = e.target.selectionStart
      const end = e.target.selectionEnd
      const next = value.substring(0, start) + '    ' + value.substring(end)
      onChange(next)
      setTimeout(() => { e.target.selectionStart = e.target.selectionEnd = start + 4 }, 0)
    }
  }

  // En móvil no hay tecla Tab: este botón inserta la indentación de 4 espacios que
  // Python exige, en la posición del cursor.
  function indentar() {
    const area = document.getElementById('editor-codigo')
    if (!area) return
    const { selectionStart: start, selectionEnd: end } = area
    onChange(value.substring(0, start) + '    ' + value.substring(end))
    setTimeout(() => {
      area.focus()
      area.selectionStart = area.selectionEnd = start + 4
    }, 0)
  }

  return (
    <div className="rounded-xl overflow-hidden border border-gray-800 bg-gray-950">
      <div className="bg-gray-900 border-b border-gray-800 px-3 sm:px-4 py-2.5 flex items-center justify-between gap-2">
        <div className="flex items-center gap-1.5 shrink-0">
          <span className="w-3 h-3 rounded-full bg-red-500/70" />
          <span className="w-3 h-3 rounded-full bg-amber-400/70" />
          <span className="w-3 h-3 rounded-full bg-emerald-500/70" />
        </div>
        <span className="text-gray-500 text-xs font-mono truncate">solucion.py</span>
        <span className="text-gray-600 text-xs shrink-0">Python 3</span>
      </div>

      <textarea
        id="editor-codigo"
        value={value}
        onChange={e => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        /* text-base en móvil: por debajo de 16px, Safari en iOS hace zoom al enfocar. */
        className="w-full bg-gray-950 text-gray-200 font-mono text-base sm:text-sm p-3 sm:p-5 outline-none resize-y leading-relaxed caret-emerald-400 min-h-[16rem] sm:min-h-[24rem]"
        rows={14}
        spellCheck={false}
        placeholder="# Escribe tu solución aquí..."
        autoComplete="off"
        autoCorrect="off"
        autoCapitalize="off"
      />

      {/* Barra de ayuda táctil: sólo en móvil, donde no existe la tecla Tab */}
      <div className="sm:hidden border-t border-gray-800 bg-gray-900 px-3 py-2 flex items-center gap-2">
        <button
          type="button"
          onClick={indentar}
          className="text-xs font-mono text-gray-300 bg-gray-800 border border-gray-700 rounded-md px-3 py-2 active:bg-gray-700"
        >
          ⇥ Indentar
        </button>
        <span className="text-gray-600 text-xs">4 espacios</span>
      </div>
    </div>
  )
}
