using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// Implementación del módulo Ranking. Consulta los puntos del reto al módulo Retos
/// mediante su interfaz pública (IRetosApi), nunca su repositorio.
/// </summary>
public class RankingService : IRankingApi
{
    private readonly RankingRepository _repo;
    private readonly IRetosApi _retos;

    public RankingService(RankingRepository repo, IRetosApi retos)
    {
        _repo = repo;
        _retos = retos;
    }

    public Task<List<EntradaRanking>> ObtenerRankingAsync() => _repo.ObtenerOrdenado();

    public Task<EntradaRanking?> ObtenerPorUsuarioAsync(long usuarioId) => _repo.BuscarPorUsuario(usuarioId);

    public async Task RegistrarAciertoAsync(long usuarioId, long retoId)
    {
        int puntos = await _retos.PuntosPorRetoAsync(retoId);

        var entrada = await _repo.BuscarPorUsuario(usuarioId) ?? new EntradaRanking { UsuarioId = usuarioId };
        entrada.PuntajeTotal += puntos;
        entrada.RetosResueltos += 1;
        entrada.ActualizadoEn = DateTime.UtcNow;

        await _repo.Guardar(entrada);
    }
}
