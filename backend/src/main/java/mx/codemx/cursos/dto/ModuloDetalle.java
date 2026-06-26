package mx.codemx.cursos.dto;

import java.util.List;

/**
 * Vista completa de un módulo: sus lecciones, el avance y si el examen está desbloqueado.
 */
public record ModuloDetalle(
        Long id,
        String titulo,
        String descripcion,
        String icono,
        List<LeccionVista> lecciones,
        int totalLecciones,
        int leccionesCompletadas,
        int progreso,              // porcentaje 0-100
        int umbralExamen,          // porcentaje mínimo requerido para el examen
        boolean examenDesbloqueado
) {}
