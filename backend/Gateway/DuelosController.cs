using CodeMX.Api.Modules.Duelos;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>
/// Gateway / API REST → módulo Duelos. El duelo en vivo ocurre por SignalR (hub /api/hub/duelos);
/// este controlador sólo expone el historial para la interfaz.
/// </summary>
[ApiController]
[Route("api/duelos")]
[Tags("Duelos")]
public class DuelosController : ControllerBase
{
    private readonly IDuelosApi _duelos;

    public DuelosController(IDuelosApi duelos) => _duelos = duelos;

    [HttpGet("usuario/{usuarioId:long}")]
    public async Task<ActionResult<List<Duelo>>> Historial(long usuarioId) =>
        Ok(await _duelos.HistorialAsync(usuarioId));
}
