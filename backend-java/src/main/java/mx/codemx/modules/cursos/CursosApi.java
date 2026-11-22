package mx.codemx.modules.cursos;

import java.util.List;
import java.util.Map;
import mx.codemx.modules.cursos.CursosDtos.LeccionDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloResumen;
import mx.codemx.modules.cursos.CursosDtos.PreguntaVista;
import mx.codemx.modules.cursos.CursosDtos.ResultadoExamen;

public interface CursosApi {

    List<ModuloResumen> listarModulos(Long usuarioId);

    ModuloDetalle obtenerModulo(long moduloId, Long usuarioId);

    LeccionDetalle obtenerLeccion(long leccionId, Long usuarioId);

    void completarLeccion(long leccionId, long usuarioId);

    List<PreguntaVista> obtenerExamen(long moduloId, long usuarioId);

    ResultadoExamen calificarExamen(long moduloId, long usuarioId, Map<Long, Integer> respuestas);
}
