namespace CodeMX.Api.Modules.Usuarios;

/// <summary>
/// API pública del módulo Usuarios. Único punto de entrada para el gateway.
/// Según el ADR-03/04, Usuarios es independiente: sólo lo consume el gateway.
/// </summary>
public interface IUsuariosApi
{
    Task<List<Usuario>> ListarTodosAsync();
    Task<Usuario?> BuscarPorIdAsync(long id);
    Task<Usuario?> BuscarPorEmailAsync(string email);

    /// <summary>Valida credenciales (correo + contraseña). Devuelve el usuario o null si no coinciden.</summary>
    Task<Usuario?> AutenticarAsync(string email, string passwordHash);

    Task<Usuario> GuardarAsync(Usuario usuario);
    Task EliminarAsync(long id);
}
