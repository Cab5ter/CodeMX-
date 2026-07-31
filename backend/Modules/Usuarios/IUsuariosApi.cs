namespace CodeMX.Api.Modules.Usuarios;

/// <summary>
/// API pública del módulo Usuarios. Único punto de entrada para el gateway.
/// Según el ADR-03/04, Usuarios es independiente: sólo lo consume el gateway.
/// La contraseña en claro sólo cruza esta frontera en el registro y el login; el
/// hashing queda encapsulado dentro del módulo (ADR-07, cierre de DT-01).
/// </summary>
public interface IUsuariosApi
{
    Task<List<Usuario>> ListarTodosAsync();
    Task<Usuario?> BuscarPorIdAsync(long id);
    Task<Usuario?> BuscarPorEmailAsync(string email);

    /// <summary>Registra una cuenta nueva hasheando la contraseña. Lanza si el correo ya existe.</summary>
    Task<Usuario> RegistrarAsync(RegistroRequest registro);

    /// <summary>Valida credenciales (correo + contraseña en claro). Devuelve el usuario o null si no coinciden.</summary>
    Task<Usuario?> AutenticarAsync(string email, string password);

    Task EliminarAsync(long id);
}
