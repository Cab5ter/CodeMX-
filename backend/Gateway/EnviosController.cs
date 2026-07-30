using CodeMX.Api.Modules.Envios;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>Gateway / API REST → módulo Envíos. Punto donde el frontend manda las soluciones.</summary>
[ApiController]
[Route("api/envios")]
[Tags("Envios")]
public class EnviosController : ControllerBase
{
    private readonly IEnviosApi _envios;

    public EnviosController(IEnviosApi envios) => _envios = envios;

    [HttpPost]
    public async Task<ActionResult<Envio>> Enviar([FromBody] Envio envio) => Ok(await _envios.EnviarAsync(envio));

    [HttpGet("{id:long}")]
    public async Task<ActionResult<Envio>> Obtener(long id)
    {
        var envio = await _envios.BuscarPorIdAsync(id);
        return envio is null ? NotFound() : Ok(envio);
    }

    [HttpGet("usuario/{usuarioId:long}")]
    public async Task<ActionResult<List<Envio>>> PorUsuario(long usuarioId) =>
        Ok(await _envios.ListarPorUsuarioAsync(usuarioId));

    [HttpGet("reto/{retoId:long}")]
    public async Task<ActionResult<List<Envio>>> PorReto(long retoId) =>
        Ok(await _envios.ListarPorRetoAsync(retoId));
}
