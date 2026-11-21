package mx.codemx.modules.retos;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Retos. La consumen Ranking (puntos por reto) y
 * Evaluación (casos de prueba). Ningún módulo accede a su repositorio directamente.
 */
public interface RetosApi {

    List<Reto> listarTodos();

    List<Reto> listarPorDificultad(Dificultad dificultad);

    Optional<Reto> buscarPorId(long id);

    Optional<CasoPrueba> obtenerEjemplo(long retoId);

    /** Casos de prueba de un reto. Lo usa el módulo Evaluación. */
    List<CasoPrueba> obtenerCasos(long retoId);

    /** Puntos que otorga un reto según su dificultad. Lo usa el módulo Ranking. */
    int puntosPorReto(long retoId);

    Reto guardar(Reto reto);

    void eliminar(long id);
}
