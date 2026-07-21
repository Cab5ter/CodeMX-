package mx.codemx.modules.retos;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación del módulo Retos. */
@Service
@Transactional
public class RetoService implements RetosApi {

    private final RetoRepository repo;
    private final CasoPruebaRepository casos;

    public RetoService(RetoRepository repo, CasoPruebaRepository casos) {
        this.repo = repo;
        this.casos = casos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reto> listarTodos() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reto> listarPorDificultad(Dificultad dificultad) {
        return repo.findByDificultad(dificultad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reto> buscarPorId(long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CasoPrueba> obtenerEjemplo(long retoId) {
        return obtenerCasos(retoId).stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CasoPrueba> obtenerCasos(long retoId) {
        return casos.findByRetoIdOrderByIdAsc(retoId);
    }

    @Override
    @Transactional(readOnly = true)
    public int puntosPorReto(long retoId) {
        return repo.findById(retoId)
                .map(reto -> switch (reto.getDificultad()) {
                    case BASICO -> 10;
                    case INTERMEDIO -> 25;
                    case AVANZADO -> 50;
                })
                .orElse(10);
    }

    @Override
    public Reto guardar(Reto reto) {
        return repo.save(reto);
    }

    @Override
    public void eliminar(long id) {
        repo.deleteById(id);
    }
}
