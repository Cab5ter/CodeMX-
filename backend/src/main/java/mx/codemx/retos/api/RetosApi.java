package mx.codemx.retos.api;

import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Retos.
 *
 * Expone el catálogo de retos y sus casos de prueba. Según el ADR-03, el módulo
 * Ranking consume esta interfaz (para conocer los puntos de un reto) y el módulo
 * Evaluación la consume para obtener los casos de prueba que debe correr.
 * Ningún módulo accede al repositorio de Retos directamente.
 */
public interface RetosApi {

    List<Reto> listarTodos();

    List<Reto> listarPorDificultad(Dificultad dificultad);

    Optional<Reto> buscarPorId(Long id);

    Optional<CasoPrueba> obtenerEjemplo(Long retoId);

    /** Casos de prueba de un reto. Lo usa el módulo Evaluación. */
    List<CasoPrueba> obtenerCasos(Long retoId);

    /** Puntos que otorga un reto según su dificultad. Lo usa el módulo Ranking. */
    int puntosPorReto(Long retoId);

    Reto guardar(Reto reto);

    void eliminar(Long id);
}
