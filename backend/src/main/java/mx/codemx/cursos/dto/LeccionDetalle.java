package mx.codemx.cursos.dto;

import mx.codemx.cursos.model.TipoLeccion;

/**
 * Contenido completo de una lección de teoría para mostrarla al alumno.
 */
public record LeccionDetalle(
        Long id,
        Long moduloId,
        String tituloModulo,
        String titulo,
        TipoLeccion tipo,
        String contenido,
        String ejemploCodigo,
        Long retoId,
        boolean completada
) {}
