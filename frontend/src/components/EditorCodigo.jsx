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

  return (
    <div className="rounded-xl overflow-hidden border border-gray-800 bg-gray-950">
      <div className="bg-gray-900 border-b border-gray-800 px-4 py-2.5 flex items-center justify-between">
        <div className="flex items-center gap-1.5">
          <span className="w-3 h-3 rounded-full bg-red-500/70" />
          <span className="w-3 h-3 rounded-full bg-amber-400/70" />
          <span className="w-3 h-3 rounded-full bg-emerald-500/70" />
        </div>
        <span className="text-gray-500 text-xs font-mono">solucion.py</span>
        <span className="text-gray-600 text-xs">Python 3</span>
      </div>
      <textarea
        value={value}
        onChange={e => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        className="w-full bg-gray-950 text-gray-200 font-mono text-sm p-5 outline-none resize-none leading-relaxed caret-emerald-400"
        rows={18}
        spellCheck={false}
        placeholder="# Escribe tu solución aquí..."
        autoComplete="off"
        autoCorrect="off"
        autoCapitalize="off"
      />
    </div>
  )
}
