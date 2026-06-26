package mx.codemx.retos.service;

import mx.codemx.retos.api.RetosApi;
import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.repository.CasoPruebaRepository;
import mx.codemx.retos.repository.RetoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RetoService implements RetosApi {

    private final RetoRepository retoRepository;
    private final CasoPruebaRepository casoPruebaRepository;

    public RetoService(RetoRepository retoRepository, CasoPruebaRepository casoPruebaRepository) {
        this.retoRepository = retoRepository;
        this.casoPruebaRepository = casoPruebaRepository;
    }

    @Override
    public List<Reto> listarTodos() {
        return retoRepository.findAll();
    }

    @Override
    public List<Reto> listarPorDificultad(Dificultad dificultad) {
        return retoRepository.findByDificultad(dificultad);
    }

    @Override
    public Optional<Reto> buscarPorId(Long id) {
        return retoRepository.findById(id);
    }

    @Override
    public Optional<CasoPrueba> obtenerEjemplo(Long retoId) {
        return casoPruebaRepository.findByRetoId(retoId).stream().findFirst();
    }

    @Override
    public List<CasoPrueba> obtenerCasos(Long retoId) {
        return casoPruebaRepository.findByRetoId(retoId);
    }

    @Override
    public int puntosPorReto(Long retoId) {
        return retoRepository.findById(retoId)
                .map(reto -> switch (reto.getDificultad()) {
                    case BASICO -> 10;
                    case INTERMEDIO -> 25;
                    case AVANZADO -> 50;
                })
                .orElse(10);
    }

    @Override
    public Reto guardar(Reto reto) {
        return retoRepository.save(reto);
    }

    @Override
    public void eliminar(Long id) {
        retoRepository.deleteById(id);
    }
}
