using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Abstracción para obtener el problema de un duelo, de la dificultad elegida. Tiene dos
/// implementaciones intercambiables (mismo espíritu que el patrón Strategy del módulo
/// Evaluación): <see cref="ClaudeGeneradorProblemas"/> (IA) y <see cref="RetoSembradoGenerador"/> (respaldo).
/// </summary>
public interface IGeneradorProblemas
{
    Task<ProblemaDuelo> GenerarAsync(Dificultad dificultad, CancellationToken ct = default);
}
