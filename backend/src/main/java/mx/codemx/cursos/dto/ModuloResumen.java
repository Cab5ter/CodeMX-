package mx.codemx.cursos.dto;

/**
 * Tarjeta de un módulo en el menú principal: incluye el avance del usuario.
 */
public record ModuloResumen(
        Long id,
        String titulo,
        String descripcion,
        String icono,
        int totalLecciones,
        int leccionesCompletadas,
        int progreso,            // porcentaje 0-100
        boolean examenDesbloqueado
) {}
