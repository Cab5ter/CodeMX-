using CodeMX.Api.Modules.Usuarios;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>
/// Gateway / API REST → módulo Usuarios.
/// Todas las respuestas usan <see cref="UsuarioDto"/>: la entidad con el hash nunca se serializa.
/// </summary>
[ApiController]
[Route("api/usuarios")]
[Tags("Usuarios")]
public class UsuariosController : ControllerBase
{
    private readonly IUsuariosApi _usuarios;

    public UsuariosController(IUsuariosApi usuarios) => _usuarios = usuarios;

    [HttpGet]
    public async Task<ActionResult<List<UsuarioDto>>> Listar()
    {
        var usuarios = await _usuarios.ListarTodosAsync();
        return Ok(usuarios.Select(UsuarioDto.De).ToList());
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<UsuarioDto>> Obtener(long id)
    {
        var usuario = await _usuarios.BuscarPorIdAsync(id);
        return usuario is null ? NotFound() : Ok(UsuarioDto.De(usuario));
    }

    /// <summary>Registra una cuenta nueva. La contraseña se hashea con BCrypt en el servidor.</summary>
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<UsuarioDto>> Registrar([FromBody] RegistroRequest registro)
    {
        try
        {
            var usuario = await _usuarios.RegistrarAsync(registro);
            return Ok(UsuarioDto.De(usuario));
        }
        catch (EmailYaRegistradoException e) { return Conflict(new { mensaje = e.Message }); }
        catch (RegistroInvalidoException e) { return BadRequest(new { mensaje = e.Message }); }
    }

    /// <summary>Inicia sesión validando correo + contraseña.</summary>
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public async Task<ActionResult<UsuarioDto>> Login([FromBody] LoginRequest credenciales)
    {
        var usuario = await _usuarios.AutenticarAsync(credenciales.Email, credenciales.Password);
        return usuario is null
            ? Unauthorized(new { mensaje = "Correo o contraseña incorrectos." })
            : Ok(UsuarioDto.De(usuario));
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Eliminar(long id)
    {
        await _usuarios.EliminarAsync(id);
        return NoContent();
    }
}
