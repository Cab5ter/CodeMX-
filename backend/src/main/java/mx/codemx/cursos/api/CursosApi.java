package mx.codemx.cursos.api;

import mx.codemx.cursos.dto.*;

import java.util.List;
import java.util.Map;

/**
 * API pública del módulo Cursos (plataforma de aprendizaje).
 *
 * Organiza el aprendizaje en módulos con lecciones de teoría y ejercicios.
 * El gateway la consume para el menú, el detalle de módulo, las lecciones y el examen.
 * Internamente consulta al módulo Envíos (vía EnviosApi) para saber si un ejercicio
 * fue resuelto; no accede a los internals de otros módulos.
 */
public interface CursosApi {

    List<ModuloResumen> listarModulos(Long usuarioId);

    ModuloDetalle obtenerModulo(Long moduloId, Long usuarioId);

    LeccionDetalle obtenerLeccion(Long leccionId, Long usuarioId);

    void completarLeccion(Long leccionId, Long usuarioId);

    List<PreguntaVista> obtenerExamen(Long moduloId, Long usuarioId);

    ResultadoExamen calificarExamen(Long moduloId, Long usuarioId, Map<Long, Integer> respuestas);
}
