using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Respaldo sin IA: arma el problema del duelo a partir de un reto ya sembrado en la base
/// y sus casos de prueba. Permite que el modo 1 vs 1 funcione aunque no haya ANTHROPIC_API_KEY.
/// </summary>
public class RetoSembradoGenerador : IGeneradorProblemas
{
    private readonly IRetosApi _retos;

    public RetoSembradoGenerador(IRetosApi retos) => _retos = retos;

    public async Task<ProblemaDuelo> GenerarAsync(Dificultad dificultad, CancellationToken ct = default)
    {
        var todos = await _retos.ListarTodosAsync();
        if (todos.Count == 0)
            throw new InvalidOperationException("No hay retos sembrados para generar un duelo.");

        // Prefiere retos de la dificultad pedida; si no hay (p. ej. AVANZADO sin sembrar), usa cualquiera.
        var candidatos = todos.Where(r => r.Dificultad == dificultad).ToList();
        if (candidatos.Count == 0) candidatos = todos;

        var reto = candidatos[Random.Shared.Next(candidatos.Count)];
        var casos = await _retos.ObtenerCasosAsync(reto.Id);
        if (casos.Count == 0)
            throw new InvalidOperationException($"El reto '{reto.Titulo}' no tiene casos de prueba.");

        var ejemplo = await _retos.ObtenerEjemploAsync(reto.Id) ?? casos[0];

        return new ProblemaDuelo(
            reto.Titulo,
            reto.Descripcion,
            ejemplo.InputData ?? "",
            ejemplo.OutputEsperado,
            casos);
    }
}
