using CodeMX.Api.Modules.Cursos;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>Gateway / API REST → módulo Cursos (plataforma de aprendizaje).</summary>
[ApiController]
[Route("api/cursos")]
[Tags("Cursos")]
public class CursosController : ControllerBase
{
    private readonly ICursosApi _cursos;

    public CursosController(ICursosApi cursos) => _cursos = cursos;

    [HttpGet]
    public async Task<ActionResult<List<ModuloResumen>>> Listar([FromQuery] long? usuarioId) =>
        Ok(await _cursos.ListarModulosAsync(usuarioId));

    [HttpGet("modulos/{moduloId:long}")]
    public async Task<ActionResult<ModuloDetalle>> ObtenerModulo(long moduloId, [FromQuery] long? usuarioId)
    {
        try { return Ok(await _cursos.ObtenerModuloAsync(moduloId, usuarioId)); }
        catch (RecursoNoEncontradoException e) { return NotFound(e.Message); }
    }

    [HttpGet("lecciones/{leccionId:long}")]
    public async Task<ActionResult<LeccionDetalle>> ObtenerLeccion(long leccionId, [FromQuery] long? usuarioId)
    {
        try { return Ok(await _cursos.ObtenerLeccionAsync(leccionId, usuarioId)); }
        catch (RecursoNoEncontradoException e) { return NotFound(e.Message); }
    }

    [HttpPost("lecciones/{leccionId:long}/completar")]
    public async Task<IActionResult> Completar(long leccionId, [FromQuery] long usuarioId)
    {
        try
        {
            await _cursos.CompletarLeccionAsync(leccionId, usuarioId);
            return NoContent();
        }
        catch (RecursoNoEncontradoException e) { return NotFound(e.Message); }
        catch (InvalidOperationException e) { return BadRequest(e.Message); }
    }

    [HttpGet("modulos/{moduloId:long}/examen")]
    public async Task<ActionResult<List<PreguntaVista>>> Examen(long moduloId, [FromQuery] long usuarioId)
    {
        try { return Ok(await _cursos.ObtenerExamenAsync(moduloId, usuarioId)); }
        catch (ExamenBloqueadoException e) { return StatusCode(StatusCodes.Status403Forbidden, e.Message); }
    }

    [HttpPost("modulos/{moduloId:long}/examen")]
    public async Task<ActionResult<ResultadoExamen>> Calificar(long moduloId, [FromQuery] long usuarioId,
        [FromBody] Dictionary<long, int> respuestas)
    {
        try { return Ok(await _cursos.CalificarExamenAsync(moduloId, usuarioId, respuestas)); }
        catch (ExamenBloqueadoException e) { return StatusCode(StatusCodes.Status403Forbidden, e.Message); }
        catch (RecursoNoEncontradoException e) { return NotFound(e.Message); }
    }
}
