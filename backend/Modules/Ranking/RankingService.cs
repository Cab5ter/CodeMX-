using CodeMX.Api.Modules.Retos;
using CodeMX.Api.Modules.Usuarios;

namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// Implementación del módulo Ranking. Consulta los puntos del reto al módulo Retos y el
/// nombre de cada jugador al módulo Usuarios, siempre por sus interfaces públicas.
/// </summary>
public class RankingService : IRankingApi
{
    private readonly RankingRepository _repo;
    private readonly IRetosApi _retos;
    private readonly IUsuariosApi _usuarios;

    public RankingService(RankingRepository repo, IRetosApi retos, IUsuariosApi usuarios)
    {
        _repo = repo;
        _retos = retos;
        _usuarios = usuarios;
    }

    public Task<List<EntradaRanking>> ObtenerRankingAsync() => _repo.ObtenerOrdenado();

    public async Task<List<EntradaRankingDto>> ObtenerRankingConNombresAsync()
    {
        var entradas = await _repo.ObtenerOrdenado();
        var nombres = (await _usuarios.ListarTodosAsync()).ToDictionary(u => u.Id, u => u.Nombre);

        return entradas.Select(e => new EntradaRankingDto(
            e.Id,
            e.UsuarioId,
            nombres.TryGetValue(e.UsuarioId, out var n) && !string.IsNullOrWhiteSpace(n) ? n : $"Usuario #{e.UsuarioId}",
            e.PuntajeTotal,
            e.RetosResueltos
        )).ToList();
    }

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

    public async Task RegistrarResultadoDueloAsync(long ganadorId, long perdedorId, int puntosGanador, int puntosPerdedor)
    {
        var ahora = DateTime.UtcNow;

        var ganador = await _repo.BuscarPorUsuario(ganadorId) ?? new EntradaRanking { UsuarioId = ganadorId };
        ganador.PuntajeTotal += puntosGanador;
        ganador.ActualizadoEn = ahora;
        await _repo.Guardar(ganador);

        var perdedor = await _repo.BuscarPorUsuario(perdedorId) ?? new EntradaRanking { UsuarioId = perdedorId };
        perdedor.PuntajeTotal = Math.Max(0, perdedor.PuntajeTotal - puntosPerdedor);
        perdedor.ActualizadoEn = ahora;
        await _repo.Guardar(perdedor);
    }
}
