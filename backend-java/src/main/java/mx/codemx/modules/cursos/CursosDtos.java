package mx.codemx.modules.cursos;

import java.util.List;

public final class CursosDtos {

    private CursosDtos() {
    }

    public record ModuloResumen(
            long id, String titulo, String descripcion, String icono,
            int totalLecciones, int leccionesCompletadas, int progreso, boolean examenDesbloqueado) {
    }

    public record LeccionVista(
            long id, String titulo, TipoLeccion tipo, Long retoId, boolean completada, int orden) {
    }

    public record ModuloDetalle(
            long id, String titulo, String descripcion, String icono,
            List<LeccionVista> lecciones, int totalLecciones, int leccionesCompletadas,
            int progreso, int umbralExamen, boolean examenDesbloqueado) {
    }

    public record LeccionDetalle(
            long id, long moduloId, String tituloModulo, String titulo, TipoLeccion tipo,
            String contenido, String ejemploCodigo, Long retoId, boolean completada) {
    }

    public record PreguntaVista(long id, String enunciado, List<String> opciones) {
    }

    public record ResultadoExamen(int aciertos, int total, int porcentaje, boolean aprobado) {
    }
}
