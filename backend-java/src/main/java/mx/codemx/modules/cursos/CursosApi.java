package mx.codemx.modules.cursos;

import java.util.List;
import java.util.Map;
import mx.codemx.modules.cursos.CursosDtos.LeccionDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloResumen;
import mx.codemx.modules.cursos.CursosDtos.PreguntaVista;
import mx.codemx.modules.cursos.CursosDtos.ResultadoExamen;

/**
 * API pública del módulo Cursos (plataforma de aprendizaje). La consume el gateway.
 * Internamente consulta al módulo Envíos (EnviosApi) para saber si un ejercicio fue resuelto.
 *
 * <p>{@code usuarioId} puede ser {@code null} en las consultas de lectura: significa
 * "visitante sin sesión", y entonces ninguna lección aparece como completada.
 */
public interface CursosApi {

    List<ModuloResumen> listarModulos(Long usuarioId);

    ModuloDetalle obtenerModulo(long moduloId, Long usuarioId);

    LeccionDetalle obtenerLeccion(long leccionId, Long usuarioId);

    void completarLeccion(long leccionId, long usuarioId);

    List<PreguntaVista> obtenerExamen(long moduloId, long usuarioId);

    ResultadoExamen calificarExamen(long moduloId, long usuarioId, Map<Long, Integer> respuestas);
}
