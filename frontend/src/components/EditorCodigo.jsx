export default function EditorCodigo({ value, onChange }) {
  return (
    <div className="rounded-lg overflow-hidden border border-gray-700">
      <div className="bg-gray-700 px-4 py-2 flex items-center gap-2">
        <div className="flex gap-1.5">
          <span className="w-3 h-3 rounded-full bg-red-500 opacity-70" />
          <span className="w-3 h-3 rounded-full bg-yellow-500 opacity-70" />
          <span className="w-3 h-3 rounded-full bg-green-500 opacity-70" />
        </div>
        <span className="text-gray-400 text-xs font-mono ml-2">solucion.py</span>
      </div>
      <textarea
        value={value}
        onChange={e => onChange(e.target.value)}
        className="w-full bg-gray-900 text-gray-100 font-mono text-sm p-4 outline-none resize-none leading-relaxed"
        rows={16}
        spellCheck={false}
        placeholder="# Escribe tu solución aquí..."
      />
    </div>
  )
}
