package mx.codemx.modules.cursos;

import java.util.List;

/** DTOs de lectura del módulo Cursos (lo que consume el frontend). */
public final class CursosDtos {

    private CursosDtos() {
    }

    /** Tarjeta de un módulo en el menú principal, con el avance del usuario. */
    public record ModuloResumen(
            long id, String titulo, String descripcion, String icono,
            int totalLecciones, int leccionesCompletadas, int progreso, boolean examenDesbloqueado) {
    }

    /** Lección dentro del detalle de un módulo, con su estado de avance. */
    public record LeccionVista(
            long id, String titulo, TipoLeccion tipo, Long retoId, boolean completada, int orden) {
    }

    /** Vista completa de un módulo: sus lecciones, avance y estado del examen. */
    public record ModuloDetalle(
            long id, String titulo, String descripcion, String icono,
            List<LeccionVista> lecciones, int totalLecciones, int leccionesCompletadas,
            int progreso, int umbralExamen, boolean examenDesbloqueado) {
    }

    /** Contenido completo de una lección de teoría. */
    public record LeccionDetalle(
            long id, long moduloId, String tituloModulo, String titulo, TipoLeccion tipo,
            String contenido, String ejemploCodigo, Long retoId, boolean completada) {
    }

    /** Pregunta del examen como la ve el alumno (sin la respuesta correcta). */
    public record PreguntaVista(long id, String enunciado, List<String> opciones) {
    }

    /** Resultado de calificar un examen. */
    public record ResultadoExamen(int aciertos, int total, int porcentaje, boolean aprobado) {
    }
}
