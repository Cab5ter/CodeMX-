using CodeMX.Api.Modules.Retos;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>Gateway / API REST → módulo Retos.</summary>
[ApiController]
[Route("api/retos")]
[Tags("Retos")]
public class RetosController : ControllerBase
{
    private readonly IRetosApi _retos;

    public RetosController(IRetosApi retos) => _retos = retos;

    [HttpGet]
    public async Task<ActionResult<List<Reto>>> Listar([FromQuery] Dificultad? dificultad)
    {
        if (dificultad is not null) return Ok(await _retos.ListarPorDificultadAsync(dificultad.Value));
        return Ok(await _retos.ListarTodosAsync());
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<Reto>> Obtener(long id)
    {
        var reto = await _retos.BuscarPorIdAsync(id);
        return reto is null ? NotFound() : Ok(reto);
    }

    [HttpGet("{id:long}/ejemplo")]
    public async Task<ActionResult<CasoPrueba>> Ejemplo(long id)
    {
        var caso = await _retos.ObtenerEjemploAsync(id);
        return caso is null ? NotFound() : Ok(caso);
    }

    [HttpPost]
    public async Task<ActionResult<Reto>> Crear([FromBody] Reto reto) => Ok(await _retos.GuardarAsync(reto));

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Eliminar(long id)
    {
        await _retos.EliminarAsync(id);
        return NoContent();
    }
}
