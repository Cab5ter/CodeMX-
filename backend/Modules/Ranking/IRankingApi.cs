namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// API pública del módulo Ranking. La consume Envíos para registrar un acierto.
/// Internamente consulta los puntos del reto al módulo Retos (vía IRetosApi).
/// </summary>
public interface IRankingApi
{
    Task<List<EntradaRanking>> ObtenerRankingAsync();
    Task<EntradaRanking?> ObtenerPorUsuarioAsync(long usuarioId);

    /// <summary>Registra que un usuario resolvió un reto por primera vez.</summary>
    Task RegistrarAciertoAsync(long usuarioId, long retoId);
}
