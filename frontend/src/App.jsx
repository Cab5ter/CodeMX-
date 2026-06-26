import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Inicio from './pages/Inicio'
import DetalleReto from './pages/DetalleReto'
import Ranking from './pages/Ranking'
import Registro from './pages/Registro'

export default function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-900 text-gray-100">
        <Navbar />
        <main className="max-w-5xl mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<Inicio />} />
            <Route path="/retos/:id" element={<DetalleReto />} />
            <Route path="/ranking" element={<Ranking />} />
            <Route path="/registro" element={<Registro />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}
