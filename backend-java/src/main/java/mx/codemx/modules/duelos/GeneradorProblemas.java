package mx.codemx.modules.duelos;

import mx.codemx.modules.retos.Dificultad;

/**
 * Abstracción para obtener el problema de un duelo, de la dificultad elegida. Tiene dos
 * implementaciones intercambiables (mismo espíritu que el patrón Strategy del módulo
 * Evaluación): {@link ClaudeGeneradorProblemas} (IA) y {@link RetoSembradoGenerador} (respaldo).
 */
public interface GeneradorProblemas {

    ProblemaDuelo generar(Dificultad dificultad);
}
