package mx.codemx.cursos.dto;

import mx.codemx.cursos.model.TipoLeccion;

/**
 * Lección dentro del detalle de un módulo, con el estado de avance del usuario.
 */
public record LeccionVista(
        Long id,
        String titulo,
        TipoLeccion tipo,
        Long retoId,        // sólo para lecciones de EJERCICIO
        boolean completada,
        int orden
) {}
