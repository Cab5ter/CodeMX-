using CodeMX.Api.Modules.Ranking;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>Gateway / API REST → módulo Ranking.</summary>
[ApiController]
[Route("api/ranking")]
[Tags("Ranking")]
public class RankingController : ControllerBase
{
    private readonly IRankingApi _ranking;

    public RankingController(IRankingApi ranking) => _ranking = ranking;

    [HttpGet]
    public async Task<ActionResult<List<EntradaRanking>>> Obtener() => Ok(await _ranking.ObtenerRankingAsync());

    [HttpGet("usuario/{usuarioId:long}")]
    public async Task<ActionResult<EntradaRanking>> PorUsuario(long usuarioId)
    {
        var entrada = await _ranking.ObtenerPorUsuarioAsync(usuarioId);
        return entrada is null ? NotFound() : Ok(entrada);
    }
}
