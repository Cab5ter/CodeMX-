package mx.codemx.modules.retos;

import java.util.List;
import java.util.Optional;

public interface RetosApi {

    List<Reto> listarTodos();

    List<Reto> listarPorDificultad(Dificultad dificultad);

    Optional<Reto> buscarPorId(long id);

    Optional<CasoPrueba> obtenerEjemplo(long retoId);

    List<CasoPrueba> obtenerCasos(long retoId);

    int puntosPorReto(long retoId);

    Reto guardar(Reto reto);

    void eliminar(long id);
}
