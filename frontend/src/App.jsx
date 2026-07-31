import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Cursos from './pages/Cursos'
import ModuloDetalle from './pages/ModuloDetalle'
import Leccion from './pages/Leccion'
import Examen from './pages/Examen'
import Inicio from './pages/Inicio'
import DetalleReto from './pages/DetalleReto'
import Ranking from './pages/Ranking'
import Registro from './pages/Registro'
import Login from './pages/Login'
import Versus from './pages/Versus'

export default function App() {
  return (
    <BrowserRouter>
      {/* overflow-x-hidden: ningún elemento ancho puede provocar scroll lateral en el teléfono */}
      <div className="min-h-screen bg-gray-950 text-gray-100 overflow-x-hidden">
        <Navbar />
        <main className="max-w-6xl mx-auto px-4 py-5 sm:py-8">
          <Routes>
            <Route path="/" element={<Cursos />} />
            <Route path="/modulos/:id" element={<ModuloDetalle />} />
            <Route path="/modulos/:id/examen" element={<Examen />} />
            <Route path="/lecciones/:id" element={<Leccion />} />
            <Route path="/ejercicios" element={<Inicio />} />
            <Route path="/retos/:id" element={<DetalleReto />} />
            <Route path="/ranking" element={<Ranking />} />
            <Route path="/vs" element={<Versus />} />
            <Route path="/registro" element={<Registro />} />
            <Route path="/login" element={<Login />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}
