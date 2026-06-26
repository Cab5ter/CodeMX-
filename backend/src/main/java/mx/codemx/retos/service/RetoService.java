package mx.codemx.retos.service;

import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.repository.RetoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RetoService {

    private final RetoRepository retoRepository;

    public RetoService(RetoRepository retoRepository) {
        this.retoRepository = retoRepository;
    }

    public List<Reto> listarTodos() {
        return retoRepository.findAll();
    }

    public List<Reto> listarPorDificultad(Dificultad dificultad) {
        return retoRepository.findByDificultad(dificultad);
    }

    public Optional<Reto> buscarPorId(Long id) {
        return retoRepository.findById(id);
    }

    public Reto guardar(Reto reto) {
        return retoRepository.save(reto);
    }

    public void eliminar(Long id) {
        retoRepository.deleteById(id);
    }
}
