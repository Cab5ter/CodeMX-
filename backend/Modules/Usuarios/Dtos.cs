namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Datos para registrar una cuenta nueva. La contraseña llega en claro y se hashea en el servidor.</summary>
public record RegistroRequest(string Nombre, string Email, string Password);

/// <summary>Credenciales para iniciar sesión.</summary>
public record LoginRequest(string Email, string Password);

/// <summary>Vista pública de un usuario. No incluye la contraseña ni su hash.</summary>
public record UsuarioDto(long Id, string Nombre, string Email, DateTime CreadoEn)
{
    public static UsuarioDto De(Usuario u) => new(u.Id, u.Nombre, u.Email, u.CreadoEn);
}
