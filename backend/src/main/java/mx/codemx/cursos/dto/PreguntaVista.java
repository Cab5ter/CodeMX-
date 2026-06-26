package mx.codemx.cursos.dto;

import java.util.List;

/**
 * Pregunta de examen tal como la ve el alumno: sin revelar la respuesta correcta.
 */
public record PreguntaVista(
        Long id,
        String enunciado,
        List<String> opciones
) {}
