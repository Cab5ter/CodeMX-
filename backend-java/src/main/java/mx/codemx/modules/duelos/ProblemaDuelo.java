package mx.codemx.modules.duelos;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

/**
 * Problema generado para un duelo. Lo produce un {@link GeneradorProblemas}
 * (Claude cuando hay API key, o un reto sembrado como respaldo). Los {@code casos}
 * son la fuente de verdad para decidir si una solución es correcta y NUNCA se envían al
 * cliente: el frontend sólo ve el enunciado y un ejemplo.
 */
public record ProblemaDuelo(
        String titulo,
        String enunciado,
        String ejemploEntrada,
        String ejemploSalida,
        List<CasoPrueba> casos) {
}
