package mx.codemx.cursos.dto;

public record ResultadoExamen(
        int aciertos,
        int total,
        int porcentaje,
        boolean aprobado
) {}
