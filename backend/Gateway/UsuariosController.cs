using CodeMX.Api.Modules.Usuarios;
using Microsoft.AspNetCore.Mvc;

namespace CodeMX.Api.Gateway;

/// <summary>Gateway / API REST → módulo Usuarios.</summary>
[ApiController]
[Route("api/usuarios")]
[Tags("Usuarios")]
public class UsuariosController : ControllerBase
{
    private readonly IUsuariosApi _usuarios;

    public UsuariosController(IUsuariosApi usuarios) => _usuarios = usuarios;

    [HttpGet]
    public async Task<ActionResult<List<Usuario>>> Listar() => Ok(await _usuarios.ListarTodosAsync());

    [HttpGet("{id:long}")]
    public async Task<ActionResult<Usuario>> Obtener(long id)
    {
        var usuario = await _usuarios.BuscarPorIdAsync(id);
        return usuario is null ? NotFound() : Ok(usuario);
    }

    [HttpPost]
    public async Task<ActionResult<Usuario>> Crear([FromBody] Usuario usuario) =>
        Ok(await _usuarios.GuardarAsync(usuario));

    /// <summary>Inicia sesión validando correo + contraseña.</summary>
    [HttpPost("login")]
    public async Task<ActionResult<Usuario>> Login([FromBody] CredencialesLogin credenciales)
    {
        var usuario = await _usuarios.AutenticarAsync(credenciales.Email, credenciales.PasswordHash);
        return usuario is null ? Unauthorized() : Ok(usuario);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Eliminar(long id)
    {
        await _usuarios.EliminarAsync(id);
        return NoContent();
    }
}

/// <summary>Credenciales para iniciar sesión.</summary>
public record CredencialesLogin(string Email, string PasswordHash);
