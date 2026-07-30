namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// API pública del módulo Ranking. La consume Envíos para registrar un acierto.
/// Internamente consulta los puntos del reto al módulo Retos (vía IRetosApi).
/// </summary>
public interface IRankingApi
{
    Task<List<EntradaRanking>> ObtenerRankingAsync();

    /// <summary>Ranking con el nombre de cada jugador (para mostrar en la interfaz).</summary>
    Task<List<EntradaRankingDto>> ObtenerRankingConNombresAsync();

    Task<EntradaRanking?> ObtenerPorUsuarioAsync(long usuarioId);

    /// <summary>Registra que un usuario resolvió un reto por primera vez.</summary>
    Task RegistrarAciertoAsync(long usuarioId, long retoId);

    /// <summary>
    /// Aplica el resultado de un duelo 1 vs 1: suma puntos al ganador y resta al perdedor
    /// (el puntaje del perdedor nunca baja de 0).
    /// </summary>
    Task RegistrarResultadoDueloAsync(long ganadorId, long perdedorId, int puntosGanador, int puntosPerdedor);
}
