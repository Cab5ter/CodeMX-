namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Orquestador registrado como <see cref="IGeneradorProblemas"/>. Decide la fuente del
/// problema: si hay ANTHROPIC_API_KEY usa Claude y, ante cualquier fallo (sin key, error de
/// red, JSON inválido), recurre al respaldo de retos sembrados. Así el duelo nunca se queda
/// sin problema y la IA se activa sola cuando configuras la key.
/// </summary>
public class GeneradorProblemas : IGeneradorProblemas
{
    private readonly ClaudeGeneradorProblemas _claude;
    private readonly RetoSembradoGenerador _respaldo;
    private readonly ILogger<GeneradorProblemas> _log;

    public GeneradorProblemas(
        ClaudeGeneradorProblemas claude,
        RetoSembradoGenerador respaldo,
        ILogger<GeneradorProblemas> log)
    {
        _claude = claude;
        _respaldo = respaldo;
        _log = log;
    }

    private static bool HayApiKey =>
        !string.IsNullOrWhiteSpace(Environment.GetEnvironmentVariable("ANTHROPIC_API_KEY"));

    public async Task<ProblemaDuelo> GenerarAsync(Modules.Retos.Dificultad dificultad, CancellationToken ct = default)
    {
        if (HayApiKey)
        {
            try
            {
                return await _claude.GenerarAsync(dificultad, ct);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "Generación con Claude falló; usando retos sembrados de respaldo.");
            }
        }

        return await _respaldo.GenerarAsync(dificultad, ct);
    }
}
