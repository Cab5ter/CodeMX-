export default function BarraProgreso({ progreso }) {
  return (
    <div className="w-full bg-gray-800 rounded-full h-2 overflow-hidden">
      <div
        className="h-full bg-gradient-to-r from-emerald-500 to-cyan-400 rounded-full transition-all duration-500"
        style={{ width: `${progreso}%` }}
      />
    </div>
  )
}
